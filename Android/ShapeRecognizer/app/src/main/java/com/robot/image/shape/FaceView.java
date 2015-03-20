package com.robot.image.shape;

import android.content.Context;
import android.graphics.Canvas;
import android.hardware.Camera;
import android.util.Log;
import android.view.View;

import java.nio.ByteBuffer;

import static org.bytedeco.javacpp.opencv_core.*;
import static org.bytedeco.javacpp.opencv_imgproc.*;



/**
 * Created by anish_khattar25 on 3/19/15.
 */
public class FaceView extends View implements Camera.PreviewCallback {

    private static final String TAG = FaceView.class.getCanonicalName();
    public static final int SUBSAMPLING_FACTOR = 4;

    private CvMemStorage storage;
    private IplImage grayImage;


    public FaceView(Context context) {
        super(context);
        storage = CvMemStorage.create(0);

    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        try {
            Camera.Size size = camera.getParameters().getPreviewSize();
            processImage(data, size.width, size.height);
            camera.addCallbackBuffer(data);
        } catch (RuntimeException e) {
            // The camera has probably just been released, ignore.
        }

    }

    private void processImage(byte[] data, int width, int height){
        if(grayImage == null || grayImage.width() != width/SUBSAMPLING_FACTOR || grayImage.height() != height/SUBSAMPLING_FACTOR){
            grayImage = IplImage.create(width/SUBSAMPLING_FACTOR,height/SUBSAMPLING_FACTOR,IPL_DEPTH_8U,1);
        }
        int imageWidth  = grayImage.width();
        int imageHeight = grayImage.height();
        int dataStride = SUBSAMPLING_FACTOR*width;
        int imageStride = grayImage.widthStep();
        ByteBuffer imageBuffer = grayImage.asByteBuffer();
        for(int y = 0; y < imageHeight; y++){
            int dataLine = y*dataStride;
            int imageLine = y*imageStride;
            for (int x = 0; x < imageWidth; x++) {
                imageBuffer.put(imageLine + x, data[dataLine + SUBSAMPLING_FACTOR*x]);
            }
        }
        cvClearMemStorage(storage);
        findShapes(grayImage);
    }

    private void findShapes(IplImage grayImage){

        // Smooth image to remove noise
        cvSmooth(grayImage,grayImage);

        //thresholding the grayscale image to get better results
        cvThreshold(grayImage,grayImage,50,255,CV_THRESH_BINARY_INV);

        CvSeq contour = new CvSeq();
        CvSeq result;

        cvFindContours(grayImage,storage,contour);

        while(contour != null){
            result = cvApproxPoly(contour,contour.header_size(),storage,CV_POLY_APPROX_DP,cvContourPerimeter(contour)*0.02,0);
            if(result.total() == 4){
                Log.d(TAG,"Found a rectangle in the image!");
                break;
            }
            contour = contour.h_next();
        }


    }





    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }
}
