package com.shu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.widget.TextView;

import com.shouzhong.scanner.Callback;
import com.shouzhong.scanner.Result;
import com.shouzhong.scanner.ScannerView;
import com.shu.ViewFinder.QtViewFinder;

public class QrActivity extends AppCompatActivity {

    private static final String TAG = "QrActivity";
    private static final int REQUEST_CODE = 10;
    private static final int INT_REQUEST_CODE = 20;
    // 权限集合
    private static final String[] PERMISSIONS = {
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.INTERNET};
    private ScannerView scannerView;
    private TextView tvResult;
    private Vibrator vibrator;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr);
        if (!this.isGranted(Manifest.permission.CAMERA)) {
            this.requestPermission(QrActivity.PERMISSIONS, QrActivity.INT_REQUEST_CODE);
            return;
        }
        scannerView = findViewById(R.id.sv);
        tvResult = findViewById(R.id.tv_result);
        scannerView.setShouldAdjustFocusArea(true);
        scannerView.setEnableZXing(true);
        scannerView.setViewFinder(new QtViewFinder(this));
        scannerView.setSaveBmp(false);
        scannerView.setRotateDegree90Recognition(true);
        scannerView.setCallback(new Callback() {
            @Override
            public void result(Result result) {
                tvResult.setText("识别结果：\n" + result.toString());
                startVibrator();
                scannerView.restartPreviewAfterDelay(2000);
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        scannerView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        scannerView.onPause();
    }

    @Override
    protected void onDestroy() {
        if (vibrator != null) {
            vibrator.cancel();
            vibrator = null;
        }
        super.onDestroy();
    }

    /**
     * 振动
     */
    private void startVibrator() {
        if (vibrator == null)
            vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(300);
    }



    private boolean isGranted(String permission) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        } else {
            int checkSelfPermission = this.checkSelfPermission(permission);
            return checkSelfPermission == PackageManager.PERMISSION_GRANTED;
        }
    }



    private boolean requestPermission(String[] permissions, int requestCode) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (!this.isGranted(permissions[0])) {
            this.requestPermissions(permissions, requestCode);
        }
        return true;
    }


    /**
     * 权限申请结果
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode != QrActivity.REQUEST_CODE) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }

        if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.d(QrActivity.TAG, "Camera permission granted - initialize the lensEngine");


            return;
        }
    }






}