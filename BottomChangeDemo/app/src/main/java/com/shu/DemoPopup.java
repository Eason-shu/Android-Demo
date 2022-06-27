package com.shu;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import razerdp.basepopup.BasePopupWindow;

/**
 * @Author shu
 * @Version 1.0
 * @Date: 2022/06/27/ 9:18
 * @Description
 **/
public class DemoPopup extends BasePopupWindow {



    TextView textView01;
    TextView textView02;
    TextView textView03;

    public DemoPopup(Context context) {
        super(context);
        setContentView(R.layout.popup_normal);
        setViewClickListener(this::click,textView01,textView02,textView03);
        setPopupGravity(Gravity.CENTER);
    }


    @Override
    public void onViewCreated(View contentView) {
        textView01=findViewById(R.id.tv_item_1);
        textView02=findViewById(R.id.tv_item_2);
        textView03=findViewById(R.id.tv_item_3);
    }


    void click(View v) {
        System.out.println(((TextView) v).getText().toString());
    }

}