package com.shu.utils;

import android.graphics.Bitmap;

import com.googlecode.tesseract.android.TessBaseAPI;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author shu
 * @Version 1.0
 * @Date: 2022/05/30/ 10:56
 * @Description 身份证识别工具
 **/
public class IdentityUtils {

    // 识别OCRApi
    TessBaseAPI baseApi;

    public IdentityUtils(TessBaseAPI baseApi) {
        this.baseApi = baseApi;
    }

    /**
     * 前置身份证处理
     * @param arg
     * @return
     */
    public static Bitmap frontIdentityCard(Bitmap arg,Bitmap template){
        Mat input = new Mat(); // 输入结果
        Mat src = new Mat(); // 压缩结果
        Mat tem=new Mat();
        Mat mr=new Mat();
        // 将bitMap转为Mat
        Utils.bitmapToMat(arg, input);
        Utils.bitmapToMat(template, tem);
        //无损压缩640*400
        Size fixSize = new Size(700, 500);
        Imgproc.resize(input, src, fixSize);
        //灰度
        src=OpencvUtil.gray(src);
        tem=OpencvUtil.gray(tem);

        Imgproc.matchTemplate(src, tem, mr, Imgproc.TM_CCORR_NORMED);
        Core.normalize(mr, mr, 0, 1, Core.NORM_MINMAX, -1);
        Core.MinMaxLocResult minMaxLocResult = Core.minMaxLoc(mr);
        Point maxLoc = minMaxLocResult.maxLoc;
        Bitmap.Config conf = Bitmap.Config.ARGB_8888; // see other conf types
        // find id number ROI
        Rect idNumberROI = new Rect((int)(maxLoc.x+tem.cols()), (int)maxLoc.y, (int)(src.cols() - (maxLoc.x+tem.cols())-40), tem.rows()-10);
        Mat idNumberArea = src.submat(idNumberROI);
        // 返回对象
        Bitmap bmp = Bitmap.createBitmap(idNumberArea.cols(), idNumberArea.rows(), conf);
        Utils.matToBitmap(idNumberArea, bmp);

        return bmp;
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





    /**
     * 获取姓名
     * @param mat
     * @return
     */
    public String name(Mat mat){
        Point point1=new Point(mat.cols()*0.18,mat.rows()*0.11);
        Point point2=new Point(mat.cols()*0.18,mat.rows()*0.24);
        Point point3=new Point(mat.cols()*0.4,mat.rows()*0.11);
        Point point4=new Point(mat.cols()*0.4,mat.rows()*0.24);
        List<Point> list=new ArrayList<>();
        list.add(point1);
        list.add(point2);
        list.add(point3);
        list.add(point4);
        Mat name= OpencvUtil.shear(mat,list);
        name=OpencvUtil.drawContours(name,50);
        baseApi.setImage(matToBitmap(name));
        return baseApi.getUTF8Text();
    }


    /**
     * 获取性别
     * @param mat
     * @return
     */
    public  String sex(Mat mat){
        Point point1=new Point(mat.cols()*0.18,mat.rows()*0.25);
        Point point2=new Point(mat.cols()*0.18,mat.rows()*0.35);
        Point point3=new Point(mat.cols()*0.25,mat.rows()*0.25);
        Point point4=new Point(mat.cols()*0.25,mat.rows()*0.35);
        List<Point> list=new ArrayList<>();
        list.add(point1);
        list.add(point2);
        list.add(point3);
        list.add(point4);
        Mat sex=  OpencvUtil.shear(mat,list);
        sex=OpencvUtil.drawContours(sex,50);
        baseApi.setImage(matToBitmap(sex));
        return baseApi.getUTF8Text();
    }


    /**
     * 获取民族
     * @param mat
     * @return
     */
    public String nation(Mat mat){
        Point point1=new Point(mat.cols()*0.39,mat.rows()*0.25);
        Point point2=new Point(mat.cols()*0.39,mat.rows()*0.36);
        Point point3=new Point(mat.cols()*0.55,mat.rows()*0.25);
        Point point4=new Point(mat.cols()*0.55,mat.rows()*0.36);
        List<Point> list=new ArrayList<>();
        list.add(point1);
        list.add(point2);
        list.add(point3);
        list.add(point4);
        Mat nation= OpencvUtil.shear(mat,list);
        baseApi.setImage(matToBitmap(nation));
        return baseApi.getUTF8Text();
    }

    /**
     * 初始日期
     * @param mat
     * @return
     */
    public String  birthday(Mat mat){
        Point point1=new Point(mat.cols()*0.18,mat.rows()*0.35);
        Point point2=new Point(mat.cols()*0.18,mat.rows()*0.35);
        Point point3=new Point(mat.cols()*0.55,mat.rows()*0.48);
        Point point4=new Point(mat.cols()*0.55,mat.rows()*0.48);
        List<Point> list=new ArrayList<>();
        list.add(point1);
        list.add(point2);
        list.add(point3);
        list.add(point4);
        Mat birthday= OpencvUtil.shear(mat,list);
        birthday=OpencvUtil.drawContours(birthday,50);
        //年份
        Point yearPoint1=new Point(0,0);
        Point yearPoint2=new Point(0,birthday.rows());
        Point yearPoint3=new Point(birthday.cols()*0.29,0);
        Point yearPoint4=new Point(birthday.cols()*0.29,birthday.rows());
        List<Point> yearList=new ArrayList<>();
        yearList.add(yearPoint1);
        yearList.add(yearPoint2);
        yearList.add(yearPoint3);
        yearList.add(yearPoint4);
        Mat year=  OpencvUtil.shear(birthday,yearList);
        baseApi.setImage(matToBitmap(year));
        String year_str = baseApi.getUTF8Text();
        //月份
        Point monthPoint1=new Point(birthday.cols()*0.44,0);
        Point monthPoint2=new Point(birthday.cols()*0.44,birthday.rows());
        Point monthPoint3=new Point(birthday.cols()*0.55,0);
        Point monthPoint4=new Point(birthday.cols()*0.55,birthday.rows());
        List<Point> monthList=new ArrayList<>();
        monthList.add(monthPoint1);
        monthList.add(monthPoint2);
        monthList.add(monthPoint3);
        monthList.add(monthPoint4);
        Mat month=  OpencvUtil.shear(birthday,monthList);
        baseApi.setImage(matToBitmap(month));
        String month_str = baseApi.getUTF8Text();
        //日期
        Point dayPoint1=new Point(birthday.cols()*0.69,0);
        Point dayPoint2=new Point(birthday.cols()*0.69,birthday.rows());
        Point dayPoint3=new Point(birthday.cols()*0.80,0);
        Point dayPoint4=new Point(birthday.cols()*0.80,birthday.rows());
        List<Point> dayList=new ArrayList<>();
        dayList.add(dayPoint1);
        dayList.add(dayPoint2);
        dayList.add(dayPoint3);
        dayList.add(dayPoint4);
        Mat day=  OpencvUtil.shear(birthday,dayList);
        baseApi.setImage(matToBitmap(day));
        String dayStr_str = baseApi.getUTF8Text();
        String birthdayStr=year_str+"年"+month_str+"月"+dayStr_str+"日";
        return birthdayStr+"\n";
    }

    /**
     * 地址
     * @param mat
     * @return
     */
    public  String address(Mat mat){
        Point point1=new Point(mat.cols()*0.17,mat.rows()*0.47);
        Point point2=new Point(mat.cols()*0.17,mat.rows()*0.47);
        Point point3=new Point(mat.cols()*0.61,mat.rows()*0.76);
        Point point4=new Point(mat.cols()*0.61,mat.rows()*0.76);
        List<Point> list=new ArrayList<>();
        list.add(point1);
        list.add(point2);
        list.add(point3);
        list.add(point4);
        Mat address= OpencvUtil.shear(mat,list);
        address=OpencvUtil.drawContours(address,50);
        baseApi.setImage(matToBitmap(address));
        return baseApi.getUTF8Text();
    }

    /**
     * 身份证号
     * @param mat
     * @return
     */
    public  String card(Mat mat){
        Point point1=new Point(mat.cols()*0.34,mat.rows()*0.75);
        Point point2=new Point(mat.cols()*0.34,mat.rows()*0.75);
        Point point3=new Point(mat.cols()*0.89,mat.rows()*0.91);
        Point point4=new Point(mat.cols()*0.89,mat.rows()*0.91);
        List<Point> list=new ArrayList<>();
        list.add(point1);
        list.add(point2);
        list.add(point3);
        list.add(point4);
        Mat card= OpencvUtil.shear(mat,list);
        card=OpencvUtil.drawContours(card,50);
        baseApi.setImage(matToBitmap(card));
        return baseApi.getUTF8Text();
    }





}
