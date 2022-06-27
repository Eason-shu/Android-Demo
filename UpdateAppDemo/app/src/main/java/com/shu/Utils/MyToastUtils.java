package com.shu.Utils;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.shu.R;


/**
 * @Author shu
 * @Version 1.0
 * @Date: 2022/04/17/ 20:26
 * @Description 自定义Toast
 **/
public class MyToastUtils {
    protected static Toast toast=null;

    /**
     * 成功提示
     * @param context
     * @param message
     */
    public static void showSuccessToast(Context context, String message){
        View view = LayoutInflater.from(context).inflate(R.layout.customtoast,null);
        TextView showText=view.findViewById(R.id.tv_toast);
        ImageView imageView= view.findViewById(R.id.alter_icon);
        toast=new Toast(context); //创建toast实例
        toast.setView(view);//设置布局
        showText.setText(message);// 消息
        showText.setTextColor(Color.parseColor("#4CAF50"));// 颜色
        imageView.setImageResource(R.drawable.alter_success); // 图标
        toast.setDuration(Toast.LENGTH_SHORT);//设置toast的显示时间
        toast.setGravity(Gravity.TOP,0,10);
        toast.show();
    }

    /**
     * 警告提示
     * @param context
     * @param message
     */
    public static void showWarningToast(Context context, String message){
        View view = LayoutInflater.from(context).inflate(R.layout.customtoast,null);
        TextView showText=view.findViewById(R.id.tv_toast);
        ImageView imageView= view.findViewById(R.id.alter_icon);
        toast=new Toast(context); //创建toast实例
        toast.setView(view);//设置布局
        showText.setText(message);// 消息
        showText.setTextColor(Color.parseColor("#F95710"));// 颜色
        imageView.setImageResource(R.drawable.alter_wran); // 图标
        toast.setDuration(Toast.LENGTH_SHORT);//设置toast的显示时间
        toast.setGravity(Gravity.TOP,0,10);
        toast.show();
    }


    /**
     * 信息提示
     * @param context
     * @param message
     */
    public static void showInfoToast(Context context, String message){
        View view = LayoutInflater.from(context).inflate(R.layout.customtoast,null);
        TextView showText=view.findViewById(R.id.tv_toast);
        ImageView imageView= view.findViewById(R.id.alter_icon);
        toast=new Toast(context); //创建toast实例
        toast.setView(view);//设置布局
        showText.setText(message);// 消息
        showText.setTextColor(Color.parseColor("#2D8CEF"));// 颜色
        imageView.setImageResource(R.drawable.alter_info); // 图标
        toast.setDuration(Toast.LENGTH_SHORT);//设置toast的显示时间
        toast.setGravity(Gravity.TOP,0,10);
        toast.show();
    }


    /**
     * 错误提示
     * @param context
     * @param message
     */
    public static void showErrorToast(Context context, String message){
        View view = LayoutInflater.from(context).inflate(R.layout.customtoast,null);
        TextView showText=view.findViewById(R.id.tv_toast);
        ImageView imageView= view.findViewById(R.id.alter_icon);
        toast=new Toast(context); //创建toast实例
        toast.setView(view);//设置布局
        showText.setText(message);// 消息
        showText.setTextColor(Color.parseColor("#FD6483"));// 颜色
        imageView.setImageResource(R.drawable.alter_error); // 图标
        toast.setDuration(Toast.LENGTH_SHORT);//设置toast的显示时间
        toast.setGravity(Gravity.TOP,0,10);
        toast.show();
    }












}
