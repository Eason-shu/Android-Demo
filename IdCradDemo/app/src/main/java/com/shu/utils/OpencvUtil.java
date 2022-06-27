package com.shu.utils;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.RotatedRect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @Author shu
 * @Version 1.0
 * @Date: 2022/05/30/ 13:30
 * @Description
 **/
public class OpencvUtil {
    private static final int BLACK = 0;
    private static final int WHITE = 255;


    /**
     * 灰化处理
     *
     * @return
     */
    public static Mat gray(Mat mat) {
        Mat gray = new Mat();
        Imgproc.cvtColor(mat, gray, Imgproc.COLOR_BGR2GRAY, 1);
        return gray;
    }


    /**
     * 二值化处理
     *
     * @return
     */
    public static Mat binary(Mat mat) {
        Mat binary = new Mat();
        Imgproc.adaptiveThreshold(mat, binary, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY_INV, 25, 10);
        return binary;
    }

    /**
     * 模糊处理
     *
     * @param mat
     * @return
     */
    public static Mat blur(Mat mat) {
        Mat blur = new Mat();
        Imgproc.blur(mat, blur, new Size(5, 5));
        return blur;
    }


    /**
     * 膨胀
     *
     * @param mat
     * @return
     */
    public static Mat dilate(Mat mat, int size) {
        Mat dilate = new Mat();
        Mat element = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(size, size));
        //膨胀
        Imgproc.dilate(mat, dilate, element, new Point(-1, -1), 1);
        return dilate;
    }


    /**
     * 腐蚀
     *
     * @param mat
     * @return
     */
    public static Mat erode(Mat mat, int size) {
        Mat erode = new Mat();
        Mat element = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(size, size));
        //腐蚀
        Imgproc.erode(mat, erode, element, new Point(-1, -1), 1);
        return erode;
    }

    /**
     * 边缘检测
     *
     * @param mat
     * @return
     */
    public static Mat carry(Mat mat) {
        Mat dst = new Mat();
        //高斯平滑滤波器卷积降噪
        Imgproc.GaussianBlur(mat, dst, new Size(3, 3), 0);
        //边缘检测
        Imgproc.Canny(mat, dst, 50, 150);
        return dst;
    }

    /**
     * 轮廓检测
     *
     * @param mat
     * @return
     */
    public static List<MatOfPoint> findContours(Mat mat) {
        List<MatOfPoint> contours = new ArrayList<>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(mat, contours, hierarchy, Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);
        return contours;
    }


    /**
     * 清除小面积轮廓
     *
     * @param mat
     * @param size
     * @return
     */
    public static Mat drawContours(Mat mat, int size) {
        List<MatOfPoint> cardContours = OpencvUtil.findContours(mat);
        for (int i = 0; i < cardContours.size(); i++) {
            double area = OpencvUtil.area(cardContours.get(i));
            if (area < size) {
                Imgproc.drawContours(mat, cardContours, i, new Scalar(0, 0, 0), -1);
            }
        }
        return mat;
    }


    /**
     * 获取轮廓的面积
     *
     * @param contour
     * @return
     */
    public static double area(MatOfPoint contour) {
        MatOfPoint2f mat2f = new MatOfPoint2f();
        contour.convertTo(mat2f, CvType.CV_32FC1);
        RotatedRect rect = Imgproc.minAreaRect(mat2f);
        return rect.boundingRect().area();
    }


    /**
     * 根据四点坐标截取模板图片
     *
     * @param mat
     * @param pointList
     * @return
     */
    public static Mat shear(Mat mat, List<Point> pointList) {
        int x = minX(pointList);
        int y = minY(pointList);
        int xl = Math.min(xLength(pointList), mat.cols() - x);
        int yl = Math.min(yLength(pointList), mat.rows() - y);
        Rect re = new Rect(x, y, xl, yl);
        return new Mat(mat, re);
    }


    /**
     * 获取最小的X坐标
     *
     * @param points
     * @return
     */
    public static int minX(List<Point> points) {
        Collections.sort(points, new XComparator(false));
        return (int) (points.get(0).x > 0 ? points.get(0).x : -points.get(0).x);
    }

    /**
     * 获取最小的Y坐标
     *
     * @param points
     * @return
     */
    public static int minY(List<Point> points) {
        Collections.sort(points, new YComparator(false));
        return (int) (points.get(0).y > 0 ? points.get(0).y : -points.get(0).y);
    }

    /**
     * 获取最长的X坐标距离
     *
     * @param points
     * @return
     */
    public static int xLength(List<Point> points) {
        Collections.sort(points, new XComparator(false));
        return (int) (points.get(3).x - points.get(0).x);
    }

    /**
     * 获取最长的Y坐标距离
     *
     * @param points
     * @return
     */
    public static int yLength(List<Point> points) {
        Collections.sort(points, new YComparator(false));
        return (int) (points.get(3).y - points.get(0).y);
    }


    //集合排序规则（根据X坐标排序）
    public static class XComparator implements Comparator<Point> {
        private boolean reverseOrder; // 是否倒序

        public XComparator(boolean reverseOrder) {
            this.reverseOrder = reverseOrder;
        }

        public int compare(Point arg0, Point arg1) {
            if (reverseOrder)
                return (int) arg1.x - (int) arg0.x;
            else
                return (int) arg0.x - (int) arg1.x;
        }
    }


    //集合排序规则（根据Y坐标排序）
    public static class YComparator implements Comparator<Point> {
        private boolean reverseOrder; // 是否倒序

        public YComparator(boolean reverseOrder) {
            this.reverseOrder = reverseOrder;
        }

        public int compare(Point arg0, Point arg1) {
            if (reverseOrder)
                return (int) arg1.y - (int) arg0.y;
            else
                return (int) arg0.y - (int) arg1.y;
        }
    }


    /**
     * 裁剪图片
     * @param image
     * @return
     */
    public static Mat cutRect(Mat image) {
        Mat clone=image.clone();
        Mat src=image.clone();
        Imgproc.GaussianBlur(clone, clone, new Size(3, 3), 0, 0);
        Imgproc.cvtColor(clone, clone,Imgproc.COLOR_BGR2GRAY);
        int lowThresh=20;
        //边缘检测
        Imgproc.Canny(clone, clone, lowThresh*3,3);
        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        Mat hierarchy = new Mat();
        // 寻找轮廓
        Imgproc.findContours(clone, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_NONE);
        // 找出匹配到的最大轮廓
        double area = Imgproc.boundingRect(contours.get(0)).area();
        int index = 0;
        // 找出匹配到的最大轮廓
        for (int i = 0; i < contours.size(); i++) {
            double tempArea = Imgproc.boundingRect(contours.get(i)).area();
            if (tempArea > area) {
                area = tempArea;
                index = i;
            }
        }
        MatOfPoint2f matOfPoint2f = new MatOfPoint2f(contours.get(index).toArray());
        RotatedRect rect = Imgproc.minAreaRect(matOfPoint2f);
        Mat temp = new Mat(src , rect.boundingRect());
        Mat t = new Mat();
        temp.copyTo(t);
        return t;
    }







}