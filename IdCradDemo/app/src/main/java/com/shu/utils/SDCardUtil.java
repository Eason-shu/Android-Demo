package com.shu.utils;

/**
 * @Author shu
 * @Version 1.0
 * @Date: 2022/05/24/ 21:30
 * @Description 路径获取工具类
 *
 **/
import android.content.Context;
import android.os.Environment;
import java.io.File;
public class SDCardUtil {

    /**
     * 获取存储路径，version>28
     * @param context
     * @return
     */
    public static String getInnerSDCardPath(Context context) {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File external = context.getExternalFilesDir(null);
            if (external != null) {
                return external.getAbsolutePath() + File.separator;
            }
        }
        return context.getFilesDir().getAbsolutePath() + File.separator;
    }


}

