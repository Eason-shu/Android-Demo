
package com.shu;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.googlecode.tesseract.android.TessBaseAPI;
import com.shu.camera.CameraPreview;
import com.shu.utils.BitmapUtil;
import com.shu.utils.CardNumberROIFinderUtils;
import com.shu.utils.IdentityUtils;
import com.shu.utils.SDCardUtil;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @Author shu
 * @Version 1.0
 * @Date: 2022/05/28/ 15:28
 * @Description 自定义相机处理
 **/
public class CameraActivity extends Activity implements View.OnClickListener {
    /**
     * 拍摄类型-身份证正面
     */
    public final static int TYPE_IDCARD_FRONT = 1;
    /**
     * 拍摄类型-身份证反面
     */
    public final static int TYPE_IDCARD_BACK = 2;
    /**
     * 返回结果编码
     */
    public final static int RESULT_CODE = 2;
    /**
     * 自定义相机界面
     */
    private CameraPreview cameraPreview;
    /**
     * 是否是身份证正面，还是反面
     */
    private int type;
    private View containerView;
    private ImageView cropView;
    private ImageView flashImageView;
    private View optionView;
    private View resultView;
    private String TAG = "IDCARD";
    private Uri fileUri;
    private TessBaseAPI baseApi;// opencv
    private static final String DEFAULT_LANGUAGE = "nums";



    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 初始化训练模型
//            initTessBaseAPI();
        // 获取时前置，还是后置
        type = getIntent().getIntExtra("type", 1);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_camera);
        /**
         * 初始化相区域
         */
        cameraPreview = (CameraPreview) findViewById(R.id.camera_surface);
        //获取屏幕最小边，设置为cameraPreview较窄的一边
        float screenMinSize = Math.min(getResources().getDisplayMetrics().widthPixels, getResources().getDisplayMetrics().heightPixels);
        //根据screenMinSize，计算出cameraPreview的较宽的一边，长宽比为标准的16:9
        float maxSize = screenMinSize / 9.0f * 16.0f;
        RelativeLayout.LayoutParams layoutParams;
        layoutParams = new RelativeLayout.LayoutParams((int) maxSize, (int) screenMinSize);
        layoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
        cameraPreview.setLayoutParams(layoutParams);
        containerView = findViewById(R.id.camera_crop_container);
        cropView = (ImageView) findViewById(R.id.camera_crop);
        float height = (int) (screenMinSize * 0.75);
        float width = (int) (height * 75.0f / 47.0f);
        LinearLayout.LayoutParams containerParams = new LinearLayout.LayoutParams((int) width, ViewGroup.LayoutParams.MATCH_PARENT);
        LinearLayout.LayoutParams cropParams = new LinearLayout.LayoutParams((int) width, (int) height);
        containerView.setLayoutParams(containerParams);
        cropView.setLayoutParams(cropParams);
        switch (type) {
            case TYPE_IDCARD_FRONT:
                cropView.setImageResource(R.mipmap.camera_idcard_front);
                break;
            case TYPE_IDCARD_BACK:
                cropView.setImageResource(R.mipmap.camera_idcard_back);
                break;
        }
        flashImageView = (ImageView) findViewById(R.id.camera_flash);
        optionView = findViewById(R.id.camera_option);
        resultView = findViewById(R.id.camera_result);
        cameraPreview.setOnClickListener(this);
        findViewById(R.id.camera_close).setOnClickListener(this);
        findViewById(R.id.camera_take).setOnClickListener(this);
        flashImageView.setOnClickListener(this);
        findViewById(R.id.camera_result_ok).setOnClickListener(this);
        findViewById(R.id.camera_result_cancel).setOnClickListener(this);
    }




    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.camera_surface) {
            cameraPreview.focus();
        } else if (id == R.id.camera_close) {
            finish();
        } else if (id == R.id.camera_take) {
            takePhoto();
        } else if (id == R.id.camera_flash) {
            boolean isFlashOn = cameraPreview.switchFlashLight();
            flashImageView.setImageResource(isFlashOn ? R.mipmap.camera_flash_on : R.mipmap.camera_flash_off);
        } else if (id == R.id.camera_result_ok) {
            goBack();
        } else if (id == R.id.camera_result_cancel) {
            optionView.setVisibility(View.VISIBLE);
            cameraPreview.setEnabled(true);
            resultView.setVisibility(View.GONE);
            cameraPreview.startPreview();
        }
    }



    /**
     * 拍照
     */
    private void takePhoto() {
        optionView.setVisibility(View.GONE);
        cameraPreview.setEnabled(false);
        cameraPreview.takePhoto(new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(final byte[] data, Camera camera) {
                camera.stopPreview();
                //子线程处理图片，防止ANR
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            File originalFile = getOriginalFile();
                            FileOutputStream originalFileOutputStream = new FileOutputStream(originalFile);
                            originalFileOutputStream.write(data);
                            originalFileOutputStream.close();
                            Bitmap bitmap = BitmapFactory.decodeFile(originalFile.getPath());
                            //计算裁剪位置
                            float left, top, right, bottom;
                            left = ((float) containerView.getLeft() - (float) cameraPreview.getLeft()) / (float) cameraPreview.getWidth();
                            top = (float) cropView.getTop() / (float) cameraPreview.getHeight();
                            right = (float) containerView.getRight() / (float) cameraPreview.getWidth();
                            bottom = (float) cropView.getBottom() / (float) cameraPreview.getHeight();
                            //裁剪及保存到文件
                            Bitmap cropBitmap = Bitmap.createBitmap(bitmap,
                                    (int) (left * (float) bitmap.getWidth()),
                                    (int) (top * (float) bitmap.getHeight()),
                                    (int) ((right - left) * (float) bitmap.getWidth()),
                                    (int) ((bottom - top) * (float) bitmap.getHeight()));
                            final File cropFile = getCropFile();
                            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(cropFile));
                            cropBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                            bos.flush();
                            bos.close();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    resultView.setVisibility(View.VISIBLE);
                                }
                            });
                            return;
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                optionView.setVisibility(View.VISIBLE);
                                cameraPreview.setEnabled(true);
                            }
                        });
                    }
                }).start();

            }
        });
    }



    /**
     * @return 拍摄图片原始文件
     */
    private File getOriginalFile() {
        switch (type) {
            case TYPE_IDCARD_FRONT:
                return new File(getExternalCacheDir(), "idCardFront.jpg");
            case TYPE_IDCARD_BACK:
                return new File(getExternalCacheDir(), "idCardBack.jpg");
        }
        return new File(getExternalCacheDir(), "picture.jpg");
    }



    /**
     * @return 拍摄图片裁剪文件
     */
    private File getCropFile() {
        switch (type) {
            case TYPE_IDCARD_FRONT:
                return new File(getExternalCacheDir(), "idCardFrontCrop.jpg");
            case TYPE_IDCARD_BACK:
                return new File(getExternalCacheDir(), "idCardBackCrop.jpg");
        }
        return new File(getExternalCacheDir(), "pictureCrop.jpg");
    }




    /**
     * 点击对勾，使用拍照结果，返回对应图片路径
     */
    private void goBack() {
        recognizeCardId();
//        Intent intent = new Intent();
//        intent.putExtra("result", getCropFile().getPath());
//        setResult(RESULT_CODE, intent);
//        finish();
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



    /**
     * 识别身份证号信息
     */
    @SuppressLint("SetTextI18n")
    private void recognizeCardId() {
        Bitmap template = BitmapFactory.decodeResource(this.getResources(), R.drawable.cord);
        Bitmap cardImage = BitmapFactory.decodeFile(getCropFile().getPath());
        Bitmap card = IdentityUtils.frontIdentityCard(cardImage,template);
        Log.e("w",card.getWidth()+"");
        Log.e("h",card.getHeight()+"");
        BitmapUtil.saveBitmap2file(card,"test");
        Intent intent = new Intent();
        intent.putExtra("result", 123);
        setResult(RESULT_CODE, intent);
        finish();
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


}
