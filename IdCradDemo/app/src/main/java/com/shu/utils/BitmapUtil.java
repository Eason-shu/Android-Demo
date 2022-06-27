package com.shu.utils;

/**
 * @Author shu
 * @Version 1.0
 * @Date: 2022/05/30/ 12:56
 * @Description
 **/
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.Environment;

public class BitmapUtil
{
    /**
     * 保存Bitmap为文件;可能报空指针是因为没有配置权限
     *
     * @param bmp
     * @param filename
     * @return
     */
    public static void saveBitmap2file(Bitmap bmp, String filename){
        CompressFormat format = Bitmap.CompressFormat.JPEG;
        int quality = 100;
        OutputStream stream = null;
        try{
            stream = new FileOutputStream(Environment.getExternalStorageDirectory().getPath()
                    + "/"
                    + filename
                    + ".jpg");
        } catch (FileNotFoundException e){
            e.printStackTrace();
        }
        bmp.compress(format, quality, stream);
        try{
            stream.flush();
            stream.close();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    /**
     * 读取文件为Bitmap
     *
     * @param filename
     * @return
     * @throws FileNotFoundException
     */
    public static Bitmap getBitmapFromFile(String filename){
        try{
            return BitmapFactory.decodeStream(new FileInputStream(Environment.getExternalStorageDirectory().getPath()
                    + "/"
                    + filename
                    + ".jpg"));
        } catch (FileNotFoundException e){
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 返回文件路径
     * @param filename
     * @return
     */
    public static String path(String filename){
        return Environment.getExternalStorageDirectory().getPath()
                + "/"
                + filename
                + ".jpg";
    }

}
