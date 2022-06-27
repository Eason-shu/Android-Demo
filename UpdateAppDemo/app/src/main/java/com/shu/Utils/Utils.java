package com.shu.Utils;

import android.content.Context;
import android.util.TypedValue;

/**
 * @author shu
 * @date 2022/4/14
 * @description 像素计算
 */
public class Utils {
    /**
     * dp2px
     */
    public static int dp2px(Context context, float dpValue) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue,
                context.getResources().getDisplayMetrics());
    }

    public static int sp2px(Context context, float sp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp,
                context.getResources().getDisplayMetrics());
    }

    public static int px2dp(Context context, float pxValue) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, pxValue,
                context.getResources().getDisplayMetrics());

    }

    public static int getStatusBarHeight(Context context) {
        int statusBarHeight1 = -1;
//获取status_bar_height资源的ID
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            //根据资源ID获取响应的尺寸值
            statusBarHeight1 = context.getResources().getDimensionPixelSize(resourceId);
        }
        return statusBarHeight1;
    }
}
