package com.shu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    //  实例化自定义dialog
    CustomDialog dialog;
    //  最近版本号
    TextView update_new_version;
    //  最新版本大小
    TextView update_new_size;
    //  更新的内容（文本）
    TextView update_content;
    //  对话框的取消按钮
    ImageView update_cancel;
    //  下载进度条
    ProgressBar downProgressBar;
    // 下载百分比
    TextView update_bfb;
    // 下载进度
    private int progress;
    // 获取SD卡根目录
    private static final String ROOT = Environment.getExternalStorageDirectory().getPath();
    // 文件保存路径
    private static final String savePath = ROOT + "/MyAPP/";
    // 文件路径+文件名
    private static final String saveFilePath = savePath + "MyAPP.apk";
    // 下载标识
    private static final int DOWN_UPDATE = 0;
    // 结束下载标识
    private static final int DOWN_OVER = 1;
    // 下载错误标识
    private static final int DOWN_FAIL = 2;
    // 取消下载按钮标识
    private boolean interceptFlag = false;
    // 子线程
    private Thread downloadThread;
    // 全局实例
    private Context context;
    // 是否正在执行下载任务
    private boolean flag;





    /**
     * 绑定点击事件
     */
    View.OnClickListener listener = new View.OnClickListener() {
        @SuppressLint("NonConstantResourceId")
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                // 立即更新点击事件
                case R.id.update_now:
                    // 执行下载方法
                    downNewVersion();
                    break;
                //取消按钮点击事件
                case R.id.update_cancel:
                    dialog.dismiss();
                    break;
            }
        }
    };


    /**
     * 初始化
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeUpdateDialog();
        context=getApplicationContext();
        this.flag=false;
    }




    /**
     * 初始化更新框数据
     */
    @SuppressLint("SetTextI18n")
    private void  initializeUpdateDialog(){
        CustomDialog.Builder builder = new CustomDialog.Builder(MainActivity.this);
        dialog = builder
                .style(R.style.Dialog)
                .cancelTouchout(false)
                .widthdp(300)
                .heightdp(430)
                .view(R.layout.dialog_hxb_update)
                //立即更新
                .addViewOnclick(R.id.update_now, listener)
                //取消键
                .addViewOnclick(R.id.update_cancel,listener)
                .build();
        //        设置按返回键取消不了对话框
        dialog.setCancelable(false);
        dialog.show();
        // 最近版本号
        update_new_version = (TextView) dialog.findViewById(R.id.new_version);
        update_new_version.setText("发现新版v2.2.1可以下载啦！");
        //更新的内容
        update_content= (TextView) dialog.findViewById(R.id.update_description);
        update_content.setText("修改了布局样式，修复已知Bug");
        //  返回键
        update_cancel = (ImageView) dialog.findViewById(R.id.update_cancel);
        // 隐藏下载进度
        downProgressBar= (ProgressBar) dialog.findViewById(R.id.update_progressBar);
        downProgressBar.setVisibility(View.GONE);
        update_bfb=dialog.findViewById(R.id.update_bfb);
    }


    /**
     * 执行下载apk
     */
    public void downNewVersion(){
        checkPermission();
    }




    /**
     * 检查权限
     */
    public void checkPermission() {
        boolean isGranted = true;
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                isGranted = false;
            }
            if (ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                isGranted = false;
            }
            Log.i("cbs","isGranted == "+isGranted);
            if (!isGranted) {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission
                                .ACCESS_FINE_LOCATION,
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        102);
            }else {
                // 进度条可见
                downProgressBar.setVisibility(View.VISIBLE);
                if(!flag) {
                    downloadThread = new Thread(downAPKRunnable);
                    downloadThread.start();
                    this.flag=true;
                }
            }
        }else {
            // 进度条可见
            downProgressBar.setVisibility(View.VISIBLE);
            if(!flag) {
                downloadThread = new Thread(downAPKRunnable);
                downloadThread.start();
                this.flag=true;
            }
        }
    }


    /**
     * 请求权限结果
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode){
            case 102:
                if(grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    if(!flag) {
                        downloadThread = new Thread(downAPKRunnable);
                        downloadThread.start();
                        this.flag=true;
                    }
                }
                else {
                    Toast.makeText(this,"权限未通过",Toast.LENGTH_LONG).show();
                }
                break;
            default:
        }
    }



    // 子线程中执行下载
    private final Runnable downAPKRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                URL uri = new URL("http://192.168.0.144:8080/app-release.apk");
                HttpURLConnection urlConnection = (HttpURLConnection) uri.openConnection();
                urlConnection.connect();
                // 获取下载文件长度
                int apkLength = urlConnection.getContentLength();
                InputStream inputStream = urlConnection.getInputStream();
                // 创建文件保存路径
                File file = new File(savePath);
                // 注意安卓6.0之后需要申请权限才能对储存进行读写
                if (!file.exists()) {
                    file.mkdir();
                    Log.e("TAG", "文件夹创建成功");
                }
                else {
                    Log.e("TAG", "文件夹已创建");
                }
                File APKFile = new File(saveFilePath);
                FileOutputStream fileOutputStream = new FileOutputStream(
                        APKFile);
                // 已经下载的长度
                int count = 0;
                byte[] bs = new byte[1024];
                do {
                    int num = inputStream.read(bs);
                    count += num;
                    progress = (int) (((float) count / apkLength) * 100);
                    handler.sendEmptyMessage(DOWN_UPDATE);
                    if (num <= 0) {
                        handler.sendEmptyMessage(DOWN_OVER);
                        break;
                    }
                    fileOutputStream.write(bs, 0, num);
                }
                // 点击取消停止下载
                while (!interceptFlag);
                fileOutputStream.close();
                inputStream.close();
            } catch (Exception e) {
                handler.sendEmptyMessage(DOWN_FAIL);
                e.printStackTrace();
            }
        }
    };



    /**
     * 使用异步更新线程ui
     */
    @SuppressLint("HandlerLeak")
    private final Handler handler = new Handler() {
        @SuppressLint({"HandlerLeak", "SetTextI18n"})
        public void handleMessage(@NotNull Message msg) {
            switch (msg.what) {
                // 正在下载，更新进度
                case DOWN_UPDATE:
                    update_bfb.setText(progress+"%");
                    downProgressBar.setProgress(progress);
                    break;
                // 下载结束
                case DOWN_OVER:
                    installAPK();
                    dialog.dismiss();
                    break;
                case DOWN_FAIL:
                    Toast.makeText(context, "文件下载失败！", Toast.LENGTH_LONG).show();
                    break;
            }
        }

    };




    /**
     * 安装软件
     */
    private void installAPK() {
        File apkFile = new File(saveFilePath);
        if (!apkFile.exists()) {
            return;
        }
        Intent intent = new Intent(Intent.ACTION_VIEW);
        // 版本大于24
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Uri uri = FileProvider.getUriForFile(context,  "com.shu.fileprovider", apkFile);
            intent.setDataAndType(uri, "application/vnd.android.package-archive");
        } else {
            intent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }



}