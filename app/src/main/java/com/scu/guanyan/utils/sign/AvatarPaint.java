/**
 * Copyright 2021 . Huawei Technologies Co., Ltd. All rights reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.scu.guanyan.utils.sign;

import android.graphics.*;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;

import java.util.Date;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.huawei.hms.signpal.GeneratorConstants.FPS_30;

import com.scu.guanyan.widget.SignView;


public class AvatarPaint {
    private final static String TAG = "AvatarPaint";
    private final int DEFAULT_CANVAS_WIDTH = 650;
    private final int DEFAULT_CANVAS_HEIGHT = 650;
    private final int WIDTH_OFFSET = 300;
    private final int HEIGHT_OFFSET = 4250;
    private final float SCALE = 10;

    private Queue<Pair<Bitmap, Integer>> frameQueue = new ConcurrentLinkedQueue<>();
    private Queue<FrameData> frameDataQueue = new ConcurrentLinkedQueue<>();
    private Avatar avatar = Avatar.getInstance();

    private SignView mView;
    private int mFps;
    private int mBackGround = Color.WHITE;
    private long mTimeStamp = new Date().getTime();

    private Handler mAnimator = new Handler();
    private Handler mFrameCreator = new Handler();
    private Runnable mAnimatorThread, mFrameCreatorThread;

    public AvatarPaint(SignView view, int mode){
        this(view, mode, 30, Color.WHITE, false);
    }

    public AvatarPaint(SignView view,int mode, int fps, int backGroundColor, boolean startup) {
        init();
        this.mView = view;
        this.mFps = fps;
        this.mBackGround = backGroundColor;
        if (startup) startAndPlay();
    }

    public void addFrameDataList(List<FrameData> frameDataList){
            this.frameDataQueue.addAll(frameDataList);
            mTimeStamp = new Date().getTime();
    }

    public void clearFrameData(){
        this.frameDataQueue.clear();
        this.frameQueue.clear();
    }

    private void init(){
        mAnimatorThread = new Runnable() {
            @Override
            public void run() {
                if (!frameQueue.isEmpty()) {
                    Pair<Bitmap, Integer> frameDataPair = frameQueue.poll();
                    mView.setSign(frameDataPair.first);
                    mView.setFace(frameDataPair.second);
                }
                mAnimator.post(this);
//                try {
//                    Thread.sleep(1000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
            }
        };
        mFrameCreatorThread = new Runnable() {
            @Override
            public void run() {
                if (!frameDataQueue.isEmpty()) {
                    drawFrame(frameDataQueue.poll());
                }
                mFrameCreator.postDelayed(this, 1000/mFps);
            }
        };
    }


    public Bitmap getBitMapWithBackground(int color) {
        Paint paint = new Paint();
        paint.setColor(color);
        Bitmap bitmap = Bitmap.createBitmap(DEFAULT_CANVAS_WIDTH, DEFAULT_CANVAS_HEIGHT, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawRect(0, 0, bitmap.getWidth(), bitmap.getHeight(), paint);
        canvas.drawBitmap(bitmap, 0, 0, paint);
        return bitmap;
    }

    public void drawLine(Canvas canvas, Point start, Point end, Paint paint) {
        canvas.drawLine(start.x, start.y, end.x, end.y, paint);
    }


    public void drawFrame(FrameData data) {
        Bitmap bitmap = getBitMapWithBackground(mBackGround);
        // init canvas
        Canvas canvas = new Canvas(bitmap);
        for (String name : Avatar.boneNames) {
            Bone endBone = Avatar.boneMap.get(name);
            if (TextUtils.isEmpty(endBone.parentName)) {
                continue;
            }
            Bone startBone = Avatar.boneMap.get(endBone.parentName);

            // get paint setting
            Paint paint = avatar.getBoneColor(endBone.color);

            // draw bone
            endBone.setRotate(data.getDataByBoneName(startBone.name), startBone);
            Avatar.boneMap.put(name, endBone); // update endBone pose
            Point start = transTobitMapPoint(startBone.worldPosition.x, startBone.worldPosition.y);
            Point end = transTobitMapPoint(endBone.worldPosition.x, endBone.worldPosition.y);
//            Log.i(TAG, String.format("start:%s(%s,%s) end:%s(%s,%s)", startBone.name, start.x, start.y,
//                    endBone.name, end.x, end.y));
            drawLine(canvas, start, end, paint);
        }
        frameQueue.offer(new Pair<>(scaleBitmap(bitmap), data.getFaceType()));
    }

    private Bitmap scaleBitmap(Bitmap src) {
        if (src == null) {
            return null;
        }
        Bitmap dst = Bitmap.createScaledBitmap(src, mView.getWidth(), mView.getHeight(), false);
        if (dst.equals(src)) {
            return dst;
        }
        src.recycle();
        return dst;
    }


    private Point transTobitMapPoint(float x1, float y1) {
        return new Point((int) (SCALE * x1 + WIDTH_OFFSET), DEFAULT_CANVAS_HEIGHT - (int) (SCALE * y1 + HEIGHT_OFFSET));
    }

    public void startAndPlay() {
        mFrameCreator.post(mFrameCreatorThread);
        mAnimator.post(mAnimatorThread);
    }


    public void destroy() {
        mFrameCreator.removeCallbacks(mFrameCreatorThread);
        mAnimator.removeCallbacks(mAnimatorThread);
    }
}
