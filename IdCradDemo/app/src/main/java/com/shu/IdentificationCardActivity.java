package com.shu;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.googlecode.tesseract.android.TessBaseAPI;
import com.shu.utils.BitmapUtil;
import com.shu.utils.IdentityUtils;
import com.shu.utils.SDCardUtil;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author shu
 * @Version 1.0
 * @Date: 2022/05/28/ 15:28
 * @Description 身份省识别案例
 **/
public class IdentificationCardActivity extends AppCompatActivity implements View.OnClickListener{

    /**
     * 拍摄类型-身份证正面
     */
    public final static int TYPE_IDCARD_FRONT = 1;
    /**
     * 拍摄类型-身份证反面
     */
    public final static int TYPE_IDCARD_BACK = 2;
    Button choose_images;
    Button crop_id;
    Button recognition_text;
    Button ocr_button;
    TextView result;
    ImageView image;
    private static final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 1;
    private static final int MY_PERMISSIONS_REQUEST_CALL_CAMERA = 2;
    private String TAG = "IDCARD";
    private Uri fileUri;
    private TessBaseAPI baseApi;// opencv
    private static final String DEFAULT_LANGUAGE = "nums";

    String[] permissions = new String[]{
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
    };
    // 声明一个集合，在后面的代码中用来存储用户拒绝授权的权
    List<String> mPermissionList = new ArrayList<>();





    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.identificationcard);
        choose_images=findViewById(R.id.select_image_btn);
        choose_images.setOnClickListener(this);
        crop_id=findViewById(R.id.ocr_recognize_btn);
        crop_id.setOnClickListener(this);
        image=findViewById(R.id.chapter8_imageView);
        ocr_button=findViewById(R.id.ocr_btn);
        ocr_button.setOnClickListener(this);
        requestStoragePermission();
        result=findViewById(R.id.text_result_id);
        try {
            initTessBaseAPI();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 点击事件
     * @param v
     */
    @Override
    public void onClick(View v) {
        int id = v.getId();
        // 前面
        if (id == R.id.select_image_btn) {
            Intent intent = new Intent(this, CameraActivity.class);
            intent.putExtra("type",TYPE_IDCARD_FRONT);
            startActivityForResult(intent,1);
            // 后面
        } else if (id == R.id.ocr_recognize_btn) {
            Intent intent = new Intent(this, CameraActivity.class);
            intent.putExtra("type",TYPE_IDCARD_BACK);
            startActivityForResult(intent,1);
        }
        else if(id==R.id.ocr_btn){
            OcrTest();
        }
    }



    /**
     * 申请权限
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void requestStoragePermission() {
        for (int i = 0; i < permissions.length; i++) {
            if (ContextCompat.checkSelfPermission(this, permissions[i]) != PackageManager.PERMISSION_GRANTED) {
                mPermissionList.add(permissions[i]);
            }
        }
        if (mPermissionList.isEmpty()) {//未授予的权限为空，表示都授予了
            Toast.makeText(this,"已经授权",Toast.LENGTH_LONG).show();
        } else {//请求权限方法
            String[] permissions = mPermissionList.toArray(new String[mPermissionList.size()]);//将List转为数组
            ActivityCompat.requestPermissions(this, permissions, MY_PERMISSIONS_REQUEST_CALL_CAMERA);
        }
    }




    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST_CALL_PHONE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this,"权限已申请",Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this,"权限已拒绝",Toast.LENGTH_LONG).show();
            }
        }else if (requestCode == MY_PERMISSIONS_REQUEST_CALL_CAMERA){
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    //判断是否勾选禁止后不再询问
                    boolean showRequestPermission = ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[i]);
                    if (showRequestPermission) {
                        Toast.makeText(this,"权限未申请",Toast.LENGTH_LONG).show();
                    }
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }



    /**
     * 接受返回的数据
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode){
            //第一个case是1，即对应之前startActivityResult()方法当中的请求码
            case 1:
                if (resultCode==2){
                    Bitmap test = BitmapUtil.getBitmapFromFile("test");
                    image.setImageBitmap(test);
                }
        }

    }


    /**
     * 识别测试
     */
    @SuppressLint("SetTextI18n")
    public void OcrTest(){
        IdentityUtils utils = new IdentityUtils(baseApi);
        Mat src=new Mat();
        Bitmap test = BitmapUtil.getBitmapFromFile("test");
        Utils.bitmapToMat(test, src);
        // 灰度化
        Imgproc.cvtColor(src, src, Imgproc.COLOR_BGRA2GRAY);
        String name = utils.name(src);
        Log.e("姓名",name);
//        String sex = utils.sex(src);
//        Log.e("性别",sex);
//        String nation = utils.nation(src);
//        Log.e("名族",nation);
//        String birthday = utils.birthday(src);
//        Log.e("出生日期",birthday);
//        String address = utils.address(src);
//        Log.e("地址",address);
//        String card = utils.card(src);
//        Log.e("身份证号",card);
        result.setText("姓名："+name+"\n");

    }



    /**
     * 转换为BitMap
     * @param inputFrame
     * @return
     */
    public Bitmap matToBitmap(Mat inputFrame) {
        Bitmap bitmap = Bitmap.createBitmap(inputFrame.width(), inputFrame.height(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(inputFrame, bitmap);
        return bitmap;
    }




    /**
     * 初始化加载opencv
     */
    @Override
    protected void onStart() {
        if (!OpenCVLoader.initDebug()) {
            Log.e(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION, this, mLoaderCallback);
        } else {
            Log.e(TAG, "OpenCV library found inside package. Using it!" + this.getClass().getName());
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
        super.onStart();
    }


    /**
     * opencv初始化结果
     */
    private final BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    Log.i(TAG, "OpenCV loaded successfully");
                }
                break;
                default: {
                    super.onManagerConnected(status);
                }
                break;
            }
        }
    };



    /**
     * 初始化训练模型
     * @throws IOException
     */
    private void initTessBaseAPI() throws IOException {
        baseApi = new TessBaseAPI();
        String datapath = SDCardUtil.getInnerSDCardPath(getApplicationContext()) + "/tesseract/";
        File dir = new File(datapath + "tessdata/");
        if (!dir.exists()) {
            boolean mkdirs = dir.mkdirs();
            Log.e(TAG, "load ..."+mkdirs);
            InputStream input = getResources().openRawResource(R.raw.chi_sim);
            File file = new File(dir, "nums.traineddata");
            FileOutputStream output = new FileOutputStream(file);
            byte[] buff = new byte[1024];
            int len = 0;
            while((len = input.read(buff)) != -1) {
                output.write(buff, 0, len);
            }
            input.close();
            output.close();
        }
        boolean success = baseApi.init(datapath, DEFAULT_LANGUAGE);
        if(success){
            Log.e(TAG, "load Tesseract OCR Engine successfully...");
        } else {
            Log.e(TAG, "WARNING:could not initialize Tesseract data...");
        }
    }







}
