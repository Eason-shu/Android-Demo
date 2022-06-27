package com.shu.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;

/**
 * @Author shu
 * @Version 1.0
 * @Date: 2022/05/28/ 15:28
 * @Description 自定义相机工具类
 **/
public class CameraUtils {
    /**
     * Check if this device has a camera
     */
    public static boolean hasCamera(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }


    /**
     * 打开相机
     * @return
     */
    public static Camera openCamera() {
        Camera c = null;
        try {
            c = Camera.open(); // attempt to get a Camera instance
        } catch (Exception e) {
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }
}
