package com.scu.guanyan.ui.home;

import android.content.Intent;
import android.view.View;
import android.widget.Button;

import com.scu.guanyan.R;
import com.scu.guanyan.base.BaseFragment;
import com.scu.guanyan.base.ViewHolder;

/**
 * @program: Guanyan
 * @author: 陈博文
 * @create: 2022-11-07 17:03
 * @description:
 **/
public class HomeFragment extends BaseFragment {
    Button translate;
    Button real_time_tran;
    Button floating_window;
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_home;
    }

    @Override
    protected void initView(ViewHolder viewHolder, View root) {
        setButton();
    }

    /**
     * 设置按钮功能
     */
    private void setButton(){
        translate=getActivity().findViewById(R.id.translate);
        real_time_tran=getActivity().findViewById(R.id.real_time_tran);
        floating_window=getActivity().findViewById(R.id.floating_window);

        translate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent intent = new Intent(getActivity(),translate.class);//想调到哪个界面就把login改成界面对应的activity名
                //startActivity(intent);
            }
        });

        real_time_tran.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent intent = new Intent(getActivity(),translate.class);//想调到哪个界面就把login改成界面对应的activity名
                //startActivity(intent);
            }
        });

        floating_window.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent intent = new Intent(getActivity(),translate.class);//想调到哪个界面就把login改成界面对应的activity名
                //startActivity(intent);
            }
        });
    }
}
