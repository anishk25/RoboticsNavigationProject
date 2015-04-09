package org.opencv.samples.colorblobdetect;

import org.opencv.core.Scalar;

/**
 * Created by anish_khattar25 on 4/4/15.
 */
public class SignColors {

    public static int BLUE_INDEX = 0;
    public static int YELLOW_INDEX = 1;
    public static int RED_INDEX = 2;
    public static int GREEN_INDEX = 3;
    public static int CYAN_INDEX = 4;
    public static int BROWN_INDEX = 5;
    public static int ORANGE_INDEX = 6;


    public static Scalar SIGN_COLORS[] = {
            new Scalar(0,0,255,255),    // BLUE
            new Scalar(255,255,0,255),  // YELLOW
            new Scalar(255,0,0,255),    // RED
            new Scalar(0,153,0,255),    // GREEN
            new Scalar(0,255,255,255),  // CYAN
            new Scalar(153,102,51,255), // BROWN
            new Scalar(255,128,0,255)   // ORANGE
    };


    public static String SIGN_COLORS_STRINGS[] = {
            "BLUE",
            "YELLOW",
            "RED",
            "GREEN",
            "CYAN",
            "BROWN",
            "ORANGE"
    };


}
