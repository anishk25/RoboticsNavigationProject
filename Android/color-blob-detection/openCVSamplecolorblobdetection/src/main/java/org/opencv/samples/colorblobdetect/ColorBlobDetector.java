package org.opencv.samples.colorblobdetect;

import android.util.Log;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

public class ColorBlobDetector {
    // Lower and Upper bounds for range checking in HSV color space
    private Scalar mLowerBound = new Scalar(0);
    private Scalar mUpperBound = new Scalar(0);
    // Minimum contour area in percent for contours filtering
    private static double mMinContourArea = 500;
    // Color radius for range checking in HSV color space
    private Scalar mColorRadius = new Scalar(40,40,40,0);
    private List<MatOfPoint> mContours = new ArrayList<MatOfPoint>();
    private static String TAG = ColorBlobDetector.class.getCanonicalName();


    // Cache
    Mat mPyrDownMat = new Mat();
    Mat mHsvMat = new Mat();
    Mat mMask = new Mat();
    Mat mDilatedMask = new Mat();
    Mat mHierarchy = new Mat();

    public void setColorRadius(Scalar radius) {
        mColorRadius = radius;
    }

    private void setHsvColor(Scalar hsvColor) {
        double minH = (hsvColor.val[0] >= mColorRadius.val[0]) ? hsvColor.val[0]-mColorRadius.val[0] : 0;
        double maxH = (hsvColor.val[0]+mColorRadius.val[0] <= 255) ? hsvColor.val[0]+mColorRadius.val[0] : 255;

        mLowerBound.val[0] = minH;
        mUpperBound.val[0] = maxH;

        mLowerBound.val[1] = hsvColor.val[1] - mColorRadius.val[1];
        mUpperBound.val[1] = hsvColor.val[1] + mColorRadius.val[1];

        mLowerBound.val[2] = hsvColor.val[2] - mColorRadius.val[2];
        mUpperBound.val[2] = hsvColor.val[2] + mColorRadius.val[2];

        mLowerBound.val[3] = 0;
        mUpperBound.val[3] = 255;
    }

    public void setMinContourArea(double area) {
        mMinContourArea = area;
    }

    private List<MatOfPoint> processColorInImage(Mat rgbaImage) {
        Imgproc.pyrDown(rgbaImage, mPyrDownMat);
        Imgproc.pyrDown(mPyrDownMat, mPyrDownMat);

        Imgproc.cvtColor(mPyrDownMat, mHsvMat, Imgproc.COLOR_RGB2HSV_FULL);

        Core.inRange(mHsvMat, mLowerBound, mUpperBound, mMask);
        Imgproc.dilate(mMask, mDilatedMask, new Mat());

        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();

        Imgproc.findContours(mDilatedMask, contours, mHierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

        // Find max contour area
        /*double maxArea = 0;
        Iterator<MatOfPoint> each = contours.iterator();
        while (each.hasNext()) {
            MatOfPoint wrapper = each.next();
            double area = Imgproc.contourArea(wrapper);
            if (area > maxArea)
                maxArea = area;
        }*/

        // Filter contours by area and resize to fit the original image size
        Iterator<MatOfPoint> each = contours.iterator();
        List<MatOfPoint> filteredContours = new ArrayList<MatOfPoint>();

        each = contours.iterator();
        while (each.hasNext()) {
            MatOfPoint contour = each.next();
            double contourArea = Imgproc.contourArea(contour);
            if (contourArea > mMinContourArea) {
                Core.multiply(contour, new Scalar(4,4), contour);
                filteredContours.add(contour);
            }
        }
        return filteredContours;
    }

    public void findSignColorsInImage(Mat rgbaImage){
        int foundColorCount = 0;
        ArrayList<Integer> colorIndices = new ArrayList<Integer>();
        mContours.clear();
        for(int i = 0; i < SignColors.SIGN_COLORS.length; i++){
            setHsvColor(convertScalarRgba2Hsv(SignColors.SIGN_COLORS[i]));
            List<MatOfPoint> contours = processColorInImage(rgbaImage);
            if(!contours.isEmpty()){
                mContours.addAll(contours);
                colorIndices.add(i);
                foundColorCount++;
                if(foundColorCount >= 2){
                    break;
                }
            }
        }
        for(Integer i: colorIndices){
            Log.i(TAG,"Found sign color: " + SignColors.SIGN_COLORS_STRINGS[i]);
        }

    }

    private Scalar convertScalarRgba2Hsv(Scalar rgbaColor){
        Mat pointMatHsv = new Mat();
        Mat pointMatRgba = new Mat(1,1,CvType.CV_8UC4,rgbaColor);
        Imgproc.cvtColor(pointMatRgba,pointMatHsv,Imgproc.COLOR_RGB2HSV_FULL,3);
        return new Scalar(pointMatHsv.get(0,0));
    }

    public List<MatOfPoint> getContours() {
        return mContours;
    }
}
