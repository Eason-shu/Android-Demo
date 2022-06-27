package com.shu;


import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    DropDownMenu menu_warehouse; // 下拉菜单控件


    private void initView() {
        // 数据
        List<String>  list = new ArrayList<>();
        for (int i=0;i<10;i++) {

            list.add(i+"");
        }

        // 下拉菜单listview 的adapter
        DropDownMenuListAdapter<String> cangkuAdapter = new DropDownMenuListAdapter<>(getApplicationContext(), list, R.layout.item_drop_down_menu_warehouse, R.id.content);
        menu_warehouse.setAdapter(cangkuAdapter);
    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        menu_warehouse=findViewById(R.id.menu_warehouse);
        initView();




    }


    @Override
    public void onClick(View v) {

    }
}