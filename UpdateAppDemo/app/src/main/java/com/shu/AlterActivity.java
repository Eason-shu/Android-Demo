package com.shu;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import com.shu.Utils.MyToastUtils;

public class AlterActivity extends AppCompatActivity {
    private Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alter);
        context=getApplicationContext();
    }

    public void successAlter(View view){
        MyToastUtils.showSuccessToast(context,"这是成功消息");
    }

    public void errorAlter(View view){
        MyToastUtils.showErrorToast(context,"这是错误消息");
    }

    public void warningAlter(View view){
        MyToastUtils.showWarningToast(context,"这是警告消息");
    }

    public void infoAlter(View view){
        MyToastUtils.showInfoToast(context,"这是消息");
    }
}