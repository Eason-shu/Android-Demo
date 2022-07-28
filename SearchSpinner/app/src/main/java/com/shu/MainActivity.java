package com.shu;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.shu.model.Select;
import com.shu.spinner.SearchSpinner;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    SearchSpinner editSpinner;
    SearchSpinner editSpinner2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final List<Select> tvs = new ArrayList<>();
        tvs.add(new Select("检查", "1"));
        tvs.add(new Select("车辆检查", "2"));
        tvs.add(new Select("车辆数检查", "3"));
        tvs.add(new Select("联网检查", "4"));
        tvs.add(new Select("底盘检测", "5"));

         List<Select> tvs2 = new ArrayList<>();
        tvs2.add(new Select("检", "1"));
        tvs2.add(new Select("检查", "2"));
        tvs2.add(new Select("数检查", "3"));
        tvs2.add(new Select("检查", "4"));
        tvs2.add(new Select("检测", "5"));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editSpinner = (SearchSpinner) findViewById(R.id.editSpinner1);
        editSpinner.setRightImageResource(R.drawable.ic_expand_more_black);
        editSpinner.setHint("请选择检测类型");
        editSpinner.setItemData(tvs);

        editSpinner2 = (SearchSpinner) findViewById(R.id.editSpinner2);
        editSpinner2.setRightImageResource(R.drawable.ic_expand_more_black);
        editSpinner2.setHint("请选择检测类型2");
        editSpinner2.setItemData(tvs2);
    }


    /**
     * 获取值
     * @param view
     */
    public void clickButton(View view){
        Select spinnerText = editSpinner.getSelect();
        Toast.makeText(getApplicationContext(), "你选择的是值："+spinnerText.getValue()+"  你选择的是名称："+spinnerText.getName(), Toast.LENGTH_SHORT).show();
    }


    /**
     * 设置默认值
     * @param view
     */
    public void clickButton2(View view){
        editSpinner.setText("2");
    }
}