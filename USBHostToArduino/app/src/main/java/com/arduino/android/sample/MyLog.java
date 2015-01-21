package com.arduino.android.sample;

import android.util.Log;

/**
 * Created by anish_khattar25 on 1/21/15.
 */
public class MyLog {
    public static void d(Object o){
        Log.d(">==< USB Controller >==<", String.valueOf(o));
    }

    public static void e(Object o){
        Log.e(">==< USB Controller >==<", String.valueOf(o));
    }
}
