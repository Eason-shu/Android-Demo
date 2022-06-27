package com.shu;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import wang.relish.widget.vehicleedittext.VehicleKeyboardHelper;

public class MainActivity extends AppCompatActivity {


    EditText text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        text = findViewById(R.id.test);
        VehicleKeyboardHelper.bind(text);
    }
}