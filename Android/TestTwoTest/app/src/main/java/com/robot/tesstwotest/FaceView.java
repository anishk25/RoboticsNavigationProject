package com.robot.tesstwotest;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.ByteArrayOutputStream;

/**
 * Created by anish_khattar25 on 3/31/15.
 */
public class FaceView extends View implements Camera.PreviewCallback {

    private static final String TAG = "Tess Two Camera";

    private static final String DATA_PATH = "/storage/sdcard0/TessTwoOcrLang/";
    private static final String CHAR_LIST = "123456790";
    public static final String LANG = "eng";
    private TextView tvRecognizedText;


    private TessBaseAPI tessBaseAPI;

    public FaceView(Context context,TextView tvRecognizedText) {
        super(context);
        this.tvRecognizedText = tvRecognizedText;
        initTess();


    }

    public void initTess(){
        tessBaseAPI = new TessBaseAPI();
        tessBaseAPI.setDebug(true);
        tessBaseAPI.init(DATA_PATH,LANG);
        tessBaseAPI.setVariable(TessBaseAPI.VAR_CHAR_WHITELIST,CHAR_LIST);
    }

    public void endTessApi(){
        if(tessBaseAPI != null) {
            tessBaseAPI.end();
            tessBaseAPI = null;
        }
    }


    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        try {
            Camera.Size size = camera.getParameters().getPreviewSize();
            Bitmap bitmap = decodeByteImage(data,size);
            getTextFromFrame(bitmap);
            camera.addCallbackBuffer(data);
        } catch (RuntimeException e) {
            // The camera has probably just been released, ignore.
        }
    }


    private void getTextFromFrame(Bitmap bitmap){

        Log.v(TAG,"Getting text from frame!");
        if(tessBaseAPI == null) {
            initTess();
        }
        bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        tessBaseAPI.setImage(bitmap);
        String recognizedText = tessBaseAPI.getUTF8Text();
        Log.i(TAG,"Recognized text: " + recognizedText);
        //tvRecognizedText.setText("Recognized text: " + recognizedText);

    }


    private Bitmap decodeByteImage(byte [] data,Camera.Size size){
        YuvImage yuvImage = new YuvImage(data, ImageFormat.NV21,size.width,size.height,null);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Rect rect = new Rect(0,0,size.width,size.height);

        yuvImage.compressToJpeg(rect,100,baos);
        byte [] imageData = baos.toByteArray();
        Bitmap bitmap = BitmapFactory.decodeByteArray(imageData,0,imageData.length);
        return bitmap;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }
}
