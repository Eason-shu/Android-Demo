package com.shu;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initLoadOpenCv();
        Button button = (Button)findViewById(R.id.button);
        Button button2 = (Button)findViewById(R.id.button2);
        button.setOnClickListener(this);
        button2.setOnClickListener(this);

    }


    /**
     * 加载opencv
     */
    public void initLoadOpenCv(){
        boolean initDebug = OpenCVLoader.initDebug();
        if(initDebug){
            Toast.makeText(this,"opencv 加载中....",Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(this,"不能加载 opencv",Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * 点击事件,灰度加载
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button:
                Bitmap resource = BitmapFactory.decodeResource(this.getResources(), R.drawable.lena);
                Mat src = new Mat();
                Mat dst = new Mat();
                Utils.bitmapToMat(resource,src);
                Imgproc.cvtColor (src,dst,Imgproc.COLOR_BGRA2GRAY);Utils.matToBitmap (dst, resource);
                ImageView iv = (ImageView)this.findViewById (R.id.imageView);
                iv.setImageBitmap(resource) ;
                src.release() ;
                dst.release();
                break;
            //取消按钮点击事件
            case R.id.button2:
                Intent intent = new Intent(this, IdCradActivity.class);
                startActivity(intent);
                break;
        }

    }



}