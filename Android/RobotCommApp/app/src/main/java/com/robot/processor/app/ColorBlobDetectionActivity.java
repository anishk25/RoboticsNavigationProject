package com.robot.processor.app;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.robot.communication.UsbController;
import com.robot.constants.Constants;
import com.robot.processor.app.ColorBlobDetector;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.util.List;

/**
 * Created by anish_khattar25 on 4/14/15.
 */
public class ColorBlobDetectionActivity extends Activity implements CameraBridgeViewBase.CvCameraViewListener2,View.OnClickListener {
    private static final String  TAG              = "ColorBlob";
    private Mat                  mRgba;
    private ColorBlobDetector mDetector;
    private Scalar               CONTOUR_COLOR;
    private CameraBridgeViewBase mOpenCvCameraView;
    private TextView             tvColorStateInfo,tvArduinoDebug;
    private Button               bResetColorState;
    private boolean              stopSignalSent = false;


    private int numSignsToSearch  = 1;
    private UsbController usbController;

    private BaseLoaderCallback  mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i(TAG, "OpenCV loaded successfully");
                    mOpenCvCameraView.enableView();
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };

    public ColorBlobDetectionActivity() {
        Log.i(TAG, "Instantiated new Color Blob Detection");
    }

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "called onCreate");
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.color_blob_detection_surface_view);

        Bundle bundle = getIntent().getExtras();
        numSignsToSearch = bundle.getInt(Constants.NUM_SIGNS_BUNDLE_KEY);
        initUI();
    }

    private void initUI(){

        tvColorStateInfo = (TextView)findViewById(R.id.tvColorState);
        tvArduinoDebug = (TextView)findViewById(R.id.tvArduinoDebug);
        bResetColorState = (Button)findViewById(R.id.bResetColorState);
        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.color_blob_detection_activity_surface_view);
        mOpenCvCameraView.setCvCameraViewListener(this);

    }

    @Override
    public void onPause()
    {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_3, this, mLoaderCallback);
    }

    public void onDestroy() {
        super.onDestroy();
        if(usbController != null) {
            usbController.stopUSBThread();
            usbController = null;
        }

        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    public void onCameraViewStarted(int width, int height) {
        mRgba = new Mat(height, width, CvType.CV_8UC4);
        mDetector = new ColorBlobDetector(numSignsToSearch);
        CONTOUR_COLOR = new Scalar(255,0,0,255);
        sendSignal(Constants.ARDUINO_START_SIGNAL);

    }

    public void onCameraViewStopped() {
        mRgba.release();
    }


    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        mRgba = inputFrame.rgba();
        mDetector.searchForColor(mRgba);
        List<MatOfPoint> contours = mDetector.getContours();
        Imgproc.drawContours(mRgba, contours, -1, CONTOUR_COLOR);
        checkCurrColorState();
        return mRgba;
    }

    private void checkCurrColorState(){
        ColorBlobDetector.ColorState currState = mDetector.getCurrColorState();
        final String colorStr;
        switch (currState){
            case SEARCH_FIRST_STATE:
                colorStr = "Searching for first color";
                break;
            case RED_STATE:
                colorStr = "Searching for red color";
                break;
            case YELLOW_STATE:
                colorStr = "Searching for yellow color";
                break;
            case DONE_SEARCHING:
                colorStr = "Done Searching for colors";

                break;
            default:
                colorStr = "";
                break;
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tvColorStateInfo.setText(colorStr);
            }
        });

    }


    private void sendSignal(String signal){
        if(usbController == null){
            usbController = new UsbController(getApplicationContext(),getIntent(),tvArduinoDebug,this);
        }
        usbController.sendData(signal.getBytes());
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bResetColorState:
                mDetector.resetStateMachine();
                stopSignalSent = false;
                sendSignal(Constants.ARDUINO_START_SIGNAL);
        }
    }
}
