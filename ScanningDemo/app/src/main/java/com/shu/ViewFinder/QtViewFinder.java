package com.shu.ViewFinder;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.view.View;

import com.shouzhong.scanner.IViewFinder;

/**
 * @Author shu
 * @Version 1.0
 * @Date: 2022/06/15/ 13:36
 * @Description 条形码扫描界面
 **/
public class QtViewFinder extends View implements IViewFinder {
    private Rect framingRect;//扫码框所占区域
    private float widthRatio = 0.9f;//扫码框宽度占view总宽度的比例
    private float heightRatio = 0.8f;
    private float heightWidthRatio = 1f;//扫码框的高宽比
    private int leftOffset = -1;//扫码框相对于左边的偏移量，若为负值，则扫码框会水平居中
    private int topOffset = -1;//扫码框相对于顶部的偏移量，若为负值，则扫码框会竖直居中
    private int laserColor = 0xff008577;// 扫描线颜色
    private int maskColor = 0x60000000;// 阴影颜色
    private int borderColor = 0xffffffff;// 边框颜色
    private int borderStrokeWidth = 12;// 边框宽度
    private int borderLineLength = 72;// 边框长度
    private Paint laserPaint;// 扫描线
    private Paint maskPaint;// 阴影遮盖画笔
    private Paint borderPaint;// 边框画笔
    private int position;

    /**
     * 构造器
     * @param context
     */
    public QtViewFinder(Context context) {
        super(context);
        setWillNotDraw(false);//需要进行绘制
        laserPaint = new Paint();
        laserPaint.setColor(laserColor);
        laserPaint.setStyle(Paint.Style.FILL);
        maskPaint = new Paint();
        maskPaint.setColor(maskColor);
        borderPaint = new Paint();
        borderPaint.setColor(borderColor);
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(borderStrokeWidth);
        borderPaint.setAntiAlias(true);
    }

    @Override
    protected void onSizeChanged(int xNew, int yNew, int xOld, int yOld) {
        updateFramingRect();
    }


    /**
     * 开始绘制图像
     * @param canvas
     */
    @Override
    public void onDraw(Canvas canvas) {
        if (getFramingRect() == null) {
            return;
        }
        drawViewFinderMask(canvas);
        drawViewFinderBorder(canvas);
        drawLaser(canvas);
    }


    private void drawLaser(Canvas canvas) {
        Rect framingRect = getFramingRect();
        int top = framingRect.top + 10 + position;
        canvas.drawRect(framingRect.left + 10, top, framingRect.right - 10, top + 5, laserPaint);
        position = framingRect.bottom - framingRect.top - 25 < position ? 0 : position + 2;
        //区域刷新
        postInvalidateDelayed(20, framingRect.left + 10, framingRect.top + 10, framingRect.right - 10, framingRect.bottom - 10);
    }

    /**
     * 绘制扫码框四周的阴影遮罩
     */
    private void drawViewFinderMask(Canvas canvas) {
        // 绘制四角
        int width = canvas.getWidth();
        int height = canvas.getHeight();
        Rect framingRect = getFramingRect();
        canvas.drawRect(0, 0, width, framingRect.top, maskPaint);//扫码框顶部阴影
        canvas.drawRect(0, framingRect.top, framingRect.left, framingRect.bottom, maskPaint);//扫码框左边阴影
        canvas.drawRect(framingRect.right, framingRect.top, width, framingRect.bottom, maskPaint);//扫码框右边阴影
        canvas.drawRect(0, framingRect.bottom, width, height, maskPaint);//扫码框底部阴影

        // 绘制文字
        String testString = "请对条形码对准屏幕正中央扫描框";
        Paint mPaint = new Paint();
        mPaint.setStrokeWidth(3);
        mPaint.setTextSize(50);
        mPaint.setColor(Color.WHITE);
        mPaint.setTextAlign(Paint.Align.LEFT);
        Rect bounds = new Rect();
        mPaint.getTextBounds(testString, 0, testString.length(), bounds);
        Paint.FontMetricsInt fontMetrics = mPaint.getFontMetricsInt();
        int baseline = (getMeasuredHeight() - fontMetrics.bottom + fontMetrics.top) / 2 - fontMetrics.top;
        canvas.drawText(testString,getMeasuredWidth() / 2 - bounds.width() / 2, baseline, mPaint);
    }

    /**
     * 绘制扫码框的边框
     */
    private void drawViewFinderBorder(Canvas canvas) {
        Rect framingRect = getFramingRect();

        // Top-left corner
        Path path = new Path();
        path.moveTo(framingRect.left, framingRect.top + borderLineLength);
        path.lineTo(framingRect.left, framingRect.top);
        path.lineTo(framingRect.left + borderLineLength, framingRect.top);
        canvas.drawPath(path, borderPaint);

        // Top-right corner
        path.moveTo(framingRect.right, framingRect.top + borderLineLength);
        path.lineTo(framingRect.right, framingRect.top);
        path.lineTo(framingRect.right - borderLineLength, framingRect.top);
        canvas.drawPath(path, borderPaint);

        // Bottom-right corner
        path.moveTo(framingRect.right, framingRect.bottom - borderLineLength);
        path.lineTo(framingRect.right, framingRect.bottom);
        path.lineTo(framingRect.right - borderLineLength, framingRect.bottom);
        canvas.drawPath(path, borderPaint);

        // Bottom-left corner
        path.moveTo(framingRect.left, framingRect.bottom - borderLineLength);
        path.lineTo(framingRect.left, framingRect.bottom);
        path.lineTo(framingRect.left + borderLineLength, framingRect.bottom);
        canvas.drawPath(path, borderPaint);


    }

    /**
     * 设置framingRect的值（扫码框所占的区域）
     */
    private synchronized void updateFramingRect() {
        Point viewSize = new Point(getWidth(), getHeight());
        int width = getWidth() * 801 / 1080, height = getWidth() * 811 / 1080;
        width = (int) (getWidth() * widthRatio);
//            height = (int) (getHeight() * heightRatio);
        height = (int) (heightWidthRatio * width);

        int left, top;
        if (leftOffset < 0) {
            left = (viewSize.x - width) / 2;//水平居中
        } else {
            left = leftOffset;
        }
        if (topOffset < 0) {
            top = (viewSize.y - height) / 2;//竖直居中
        } else {
            top = topOffset;
        }
        framingRect = new Rect(left, top, left + width, top + height);
    }

    @Override
    public Rect getFramingRect() {
        return framingRect;
    }
}



