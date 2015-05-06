package com.robot.processor.app;

import com.robot.processor.constants.Constants;
import com.robot.processor.map.HallwayNavigator;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by anish_khattar25 on 4/14/15.
 */

public class ColorBlobDetector {
    // Lower and Upper bounds for range checking in HSV color space
    private Scalar mLowerBound = new Scalar(0);
    private Scalar mUpperBound = new Scalar(0);
    // Minimum contour area in percent for contours filtering
    private static double mMinContourArea = 3000;
    // Color radius for range checking in HSV color space
    private Scalar mColorRadius = new Scalar(20,20,20,0);
    private List<MatOfPoint> mContours = new ArrayList<MatOfPoint>();
    private static String TAG = ColorBlobDetector.class.getCanonicalName();
    private int foundSignCount = 0;
    private int numSignsToSearch;
    private HallwayNavigator hallwayNavigator;
    private ColorBlobDetectionActivity passingActivity;


    public static  enum RobotState{
        SEARCH_FIRST_STATE(-1),RED_STATE(0),YELLOW_STATE(1),TURN_STATE(25),DONE_TURN_STATE(26),DONE_SEARCHING(50),;

        private final int value;
        private RobotState(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }


    };

    private RobotState currRobotState = RobotState.SEARCH_FIRST_STATE;
    private RobotState nextRobotState;
    private static final Scalar RED_COLOR = new Scalar(177, 10, 14,255);
    private static final Scalar YELLOW_COLOR = new Scalar(170, 170, 40, 255);

    //private static final Scalar YELLOW_COLOR = new Scalar(203, 159, 24, 255);

    private static final Scalar[] COLORS_TO_SEARCH = {RED_COLOR,YELLOW_COLOR};
    private static final RobotState [] COLOR_STATES = {RobotState.RED_STATE, RobotState.YELLOW_STATE};



    // Cache
    Mat mPyrDownMat = new Mat();
    Mat mHsvMat = new Mat();
    Mat mMask = new Mat();
    Mat mDilatedMask = new Mat();
    Mat mHierarchy = new Mat();




    public ColorBlobDetector(int numSignsToSearch,ColorBlobDetectionActivity passingActivity){
        this.numSignsToSearch = numSignsToSearch;
        this.hallwayNavigator = new HallwayNavigator(1411,14172);
        this.passingActivity = passingActivity;
    }

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

    private void processColorInImage(Mat rgbaImage) {
        Imgproc.pyrDown(rgbaImage, mPyrDownMat);
        Imgproc.pyrDown(mPyrDownMat, mPyrDownMat);

        Imgproc.cvtColor(mPyrDownMat, mHsvMat, Imgproc.COLOR_RGB2HSV_FULL);

        Core.inRange(mHsvMat, mLowerBound, mUpperBound, mMask);
        Imgproc.dilate(mMask, mDilatedMask, new Mat());

        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();

        Imgproc.findContours(mDilatedMask, contours, mHierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

        Iterator<MatOfPoint> each = contours.iterator();

        each = contours.iterator();
        mContours.clear();
        while (each.hasNext()) {
            MatOfPoint contour = each.next();
            double contourArea = Imgproc.contourArea(contour);
            if (contourArea > mMinContourArea) {
                Core.multiply(contour, new Scalar(4, 4), contour);
                mContours.add(contour);
            }
        }
    }

    public void searchForColor(Mat rgbaImage){
        List<MatOfPoint> contours;
        switch(currRobotState){
            case SEARCH_FIRST_STATE:
                for(int i = 0; i < COLORS_TO_SEARCH.length; i++){
                    setHsvColor(convertScalarRgba2Hsv(COLORS_TO_SEARCH[i]));
                    processColorInImage(rgbaImage);
                    if(mContours.size() > 0){
                        currRobotState = COLOR_STATES[i];
                        foundSignCount = 1;
                        break;
                    }
                }
                break;
            case RED_STATE:
                setHsvColor(convertScalarRgba2Hsv(RED_COLOR));
                processColorInImage(rgbaImage);
                if(mContours.size() > 0){
                    hallwayNavigator.updatePosition();
                    if(hallwayNavigator.isNeedToTurn()){
                        passingActivity.sendMessageToArduino(Constants.ARDUINO_TURN_ROBOT_SIGNAL);
                        currRobotState = RobotState.TURN_STATE;
                        nextRobotState = RobotState.RED_STATE;
                    }else {
                        currRobotState = COLOR_STATES[(currRobotState.getValue() + 1) % COLOR_STATES.length];
                    }
                    foundSignCount++;
                }
                break;
            case YELLOW_STATE:
                setHsvColor(convertScalarRgba2Hsv(YELLOW_COLOR));
                processColorInImage(rgbaImage);
                if(mContours.size() > 0){
                    hallwayNavigator.updatePosition();
                    if(hallwayNavigator.isNeedToTurn()){
                        passingActivity.sendMessageToArduino(Constants.ARDUINO_TURN_ROBOT_SIGNAL);
                        currRobotState = RobotState.TURN_STATE;
                        nextRobotState = RobotState.YELLOW_STATE;
                    }else {
                        currRobotState = COLOR_STATES[(currRobotState.getValue() + 1) % COLOR_STATES.length];
                    }
                    foundSignCount++;
                }
                break;
            case TURN_STATE:
                break;
            case DONE_TURN_STATE:
                currRobotState = nextRobotState;
            case DONE_SEARCHING:
                break;
        }
        if(hallwayNavigator.getDestReached()){
            currRobotState = RobotState.DONE_SEARCHING;
        }
        /*if(foundSignCount >= numSignsToSearch){
            currColorState = ColorState.DONE_SEARCHING;
        }*/
    }

    public RobotState getCurrColorState(){
        return currRobotState;
    }

    public int getFoundSignCount(){
        return foundSignCount;
    }

    public void resetStateMachine(){
        currRobotState = RobotState.SEARCH_FIRST_STATE;
        foundSignCount = 0;
    }

    public void resetMap(int startRoom, int endRoom){
        hallwayNavigator.setStartEnd(startRoom, endRoom);
    }

    public String getDirectionFromMap(){
        switch(hallwayNavigator.getCurrDirection()){
            case FORWARD: return Constants.ARDUINO_FORWARD_SIGNAL;
            case BACKWARD:return Constants.ARDUINO_BACKWARD_SIGNAL;
            default: return "";
        }
    }



    public void setNumSignsToSearch(int numSigns){
        this.numSignsToSearch = numSigns;
    }

    private Scalar convertScalarRgba2Hsv(Scalar rgbaColor){
        Mat pointMatHsv = new Mat();
        Mat pointMatRgba = new Mat(1,1, CvType.CV_8UC4,rgbaColor);
        Imgproc.cvtColor(pointMatRgba, pointMatHsv, Imgproc.COLOR_RGB2HSV_FULL, 3);
        return new Scalar(pointMatHsv.get(0,0));
    }

    public List<MatOfPoint> getContours() {
        return mContours;
    }

    public boolean robotNeedsToTurn(){
        return hallwayNavigator.isNeedToTurn();
    }

    public void setTurnCompleted(){
        if(currRobotState == RobotState.TURN_STATE){
            currRobotState = RobotState.DONE_TURN_STATE;
        }
    }
}
