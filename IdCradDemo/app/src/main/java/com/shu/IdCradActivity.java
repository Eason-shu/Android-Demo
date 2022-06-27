package com.shu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.googlecode.tesseract.android.TessBaseAPI;
import com.shu.utils.CardNumberROIFinderUtils;
import com.shu.utils.ImageSelectUtils;
import com.shu.utils.SDCardUtil;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;



public class IdCradActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String DEFAULT_LANGUAGE = "nums";
    private String TAG = "OcrDemoActivity";
    private final int REQUEST_CAPTURE_IMAGE = 1;
    private int option;
    private Uri fileUri;
    private TessBaseAPI baseApi;// opencv
    private static final int STORAGE_PERMISSION = 0x20;// 动态申请存储权限标识


    @Override
    protected void onStart() {
        if (!OpenCVLoader.initDebug()) {
            Log.e(TAG,"Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION, this, mLoaderCallback);
        } else {
            Log.e(TAG,"OpenCV library found inside package. Using it!"+this.getClass().getName());
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
        super.onStart();
    }


    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
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





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_idcrad);
        Button selectBtn = (Button)this.findViewById(R.id.select_image_btn);
        Button ocrRecogBtn = (Button)this.findViewById(R.id.ocr_recognize_btn);
        selectBtn.setOnClickListener(this);
        ocrRecogBtn.setOnClickListener(this);
        requestStoragePermission();
        option = getIntent().getIntExtra("TYPE", 2);

        if(option == 2) {
            this.setTitle("身份证号码识别演示");
        } else if(option == 3) {
            this.setTitle("偏斜校正演示");
            ocrRecogBtn.setText("校正");
        }
        else {
            this.setTitle("Tesseract OCR文本识别演示");
        }

    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.select_image_btn:
                pickUpImage();
                break;
            case R.id.ocr_recognize_btn:
                if(option == 2) {
                    recognizeCardId();
                } else if(option == 3) {
                    deSkewTextImage();
                }else {
                    recognizeTextImage();
                }
                break;
            default:
                break;
        }
    }



    /**
     * 偏斜校正图片
     */
    private void deSkewTextImage() {
        Mat src = Imgcodecs.imread(fileUri.getPath());
        if(src.empty()){
            return;
        }
        Mat dst = new Mat();
        CardNumberROIFinderUtils.deSkewText(src, dst);
        // 转换为Bitmap，显示
        Bitmap bm = Bitmap.createBitmap(src.cols(), src.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(dst, bm);

        // show
        ImageView iv = this.findViewById(R.id.chapter8_imageView);
        iv.setImageBitmap(bm);

        // 释放内存
        dst.release();
        src.release();
    }



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
            InputStream input = getResources().openRawResource(R.raw.nums);
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


    /**
     * 识别身份证号信息
     */
    @SuppressLint("SetTextI18n")
    private void recognizeCardId() {
        Bitmap template = BitmapFactory.decodeResource(this.getResources(), R.drawable.cord);
        Bitmap cardImage = BitmapFactory.decodeFile(fileUri.getPath());
        Bitmap temp = CardNumberROIFinderUtils.extractNumberROI(cardImage.copy(Bitmap.Config.ARGB_8888, true), template);
        baseApi.setImage(temp);
        String myIdNumber = baseApi.getUTF8Text();
        TextView txtView = findViewById(R.id.text_result_id);
        txtView.setText("身份证号码为:" + myIdNumber);
        ImageView imageView = findViewById(R.id.chapter8_imageView);
        imageView.setImageBitmap(temp);
    }


    /**4
     * 识别文本信息
     */
    private void recognizeTextImage() {
        if(fileUri == null) return;
        Bitmap bmp = BitmapFactory.decodeFile(fileUri.getPath());
        System.out.println("xxx"+bmp);
        baseApi.setImage(bmp);
        String recognizedText = baseApi.getUTF8Text();
        TextView txtView = findViewById(R.id.text_result_id);
        System.out.println("xxxx识别结果"+recognizedText);
        if(recognizedText!=null) {
            txtView.append("识别结果:\n"+recognizedText);
        }else {
            txtView.append("无结果");
        }
    }


    private void pickUpImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "图像选择..."), REQUEST_CAPTURE_IMAGE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CAPTURE_IMAGE && resultCode == RESULT_OK) {
            if(data != null) {
                Uri uri = data.getData();
                File f = new File(ImageSelectUtils.getRealPath(uri, getApplicationContext()));
                fileUri = Uri.fromFile(f);
            }
        }
        // display it
        displaySelectedImage();
    }


    /**
     * 选择图片，对图片进行降采样处理，就是对图片进行缩放，高斯金字塔
     */
    private void displaySelectedImage() {
        if(fileUri == null) return;
        ImageView imageView = (ImageView)this.findViewById(R.id.chapter8_imageView);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(fileUri.getPath(), options);
        int w = options.outWidth;
        int h = options.outHeight;
        int inSample = 1;
        if(w > 1000 || h > 1000) {
            while(Math.max(w/inSample, h/inSample) > 1000) {
                inSample *=4;
            }
        }
        options.inJustDecodeBounds = false;
        options.inSampleSize = inSample;
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap bm = BitmapFactory.decodeFile(fileUri.getPath(), options);
        imageView.setImageBitmap(bm);
    }


    /**
     * 申请权限
     */
    private void requestStoragePermission() {
        int hasCameraPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        Log.e("TAG","开始" + hasCameraPermission);
        if (hasCameraPermission == PackageManager.PERMISSION_GRANTED){
            // 拥有权限，可以执行涉及到存储权限的操作
            Log.e("TAG", "你已经授权了该组权限");
            try {
                initTessBaseAPI();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }else {
            // 没有权限，向用户申请该权限
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Log.e("TAG", "向用户申请该组权限");
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION);
            }
        }

    }

    /**
     * 动态申请权限的结果回调
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == STORAGE_PERMISSION){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                // 用户同意，执行相应操作
                Log.e("TAG","用户已经同意了存储权限");
                try {
                    initTessBaseAPI();
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
            }else {
                // 用户不同意，向用户展示该权限作用
            }
        }

    }


}