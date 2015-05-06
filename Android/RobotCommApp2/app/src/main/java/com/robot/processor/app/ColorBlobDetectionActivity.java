package com.robot.processor.app;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


import com.robot.processor.communication.BluetoothManager;
import com.robot.processor.constants.Constants;

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
import java.util.Set;
import java.util.UUID;

/**
 * Created by anish_khattar25 on 4/14/15.
 */
public class ColorBlobDetectionActivity extends Activity implements CameraBridgeViewBase.CvCameraViewListener2,View.OnClickListener {
    private static final String  TAG              = "ColorBlob";
    private Mat mRgba;
    private ColorBlobDetector mDetector;
    private Scalar CONTOUR_COLOR;
    private CameraBridgeViewBase mOpenCvCameraView;
    private TextView             tvColorStateInfo;
    private EditText             etStartRoom,etEndRoom;
    private Button               bResetColorState,bStartBluetooth;
    private boolean              stopSignalSent = false;
    boolean receiveFirstTime = true;
    String receivedMessage;




    private int numSignsToSearch  = 5;
    private BluetoothManager bluetoothManager;
    private BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private BluetoothDevice pairedBluetoothDevice;


    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
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
        initUI();
        enableBluetooth();
        connectToBluetoothDevice();
    }

    private void initUI(){

        tvColorStateInfo = (TextView)findViewById(R.id.tvColorState);
       // etNumSigns = (EditText)findViewById(R.id.etNumSigns);

        etStartRoom = (EditText)findViewById(R.id.etStartNum);
        etEndRoom = (EditText)findViewById(R.id.etEndNum);

        bResetColorState = (Button)findViewById(R.id.bResetColorState);
        bResetColorState.setOnClickListener(this);
        bStartBluetooth = (Button)findViewById(R.id.bStartBluetooth);
        bStartBluetooth.setOnClickListener(this);

        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.color_blob_detection_activity_surface_view);
        mOpenCvCameraView.setCvCameraViewListener(this);

    }

    private void enableBluetooth(){
        if(mBluetoothAdapter != null && !mBluetoothAdapter.isEnabled()){
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent,1);
        }
    }

    private void connectToBluetoothDevice(){
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        if(pairedDevices.size() > 0){
            for(BluetoothDevice device: pairedDevices){
                if(device.getAddress().equals(Constants.HC05_BLUETOOTH_ADDRESS2)){
                    pairedBluetoothDevice = device;
                    break;
                }
            }
        }

        if(pairedBluetoothDevice != null){
            UUID uuid = UUID.fromString(Constants.HC05_UUID);
            bluetoothManager = new BluetoothManager(pairedBluetoothDevice,mBluetoothAdapter,uuid,this);
        }
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
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    public void onCameraViewStarted(int width, int height) {
        mRgba = new Mat(height, width, CvType.CV_8UC4);
        mDetector = new ColorBlobDetector(numSignsToSearch,this);
        CONTOUR_COLOR = new Scalar(255,0,0,255);
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
        ColorBlobDetector.RobotState currState = mDetector.getCurrColorState();
        final String colorStr;
        switch (currState){
            case SEARCH_FIRST_STATE:
                colorStr = "Searching for first color";
                break;
            case RED_STATE:
                colorStr = "Searching for red color, count: " + mDetector.getFoundSignCount()  ;
                break;
            case YELLOW_STATE:
                colorStr = "Searching for yellow color, count: " + mDetector.getFoundSignCount();
                break;
            case DONE_SEARCHING:
                colorStr = "Done Searching for colors";
                if(!stopSignalSent){
                    sendMessageToArduino(Constants.ARDUINO_STOP_SIGNAL);
                    stopSignalSent = true;
                }
                break;
            case TURN_STATE:
                colorStr = "Robot Turning";
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


    public void sendMessageToArduino(String msg){
        if(bluetoothManager != null){
            bluetoothManager.sendMessage(msg.getBytes());
        }
    }


    public void parseReceivedMessage(int bytesRead, byte[] buffer){
        String msg = new String(buffer,0,bytesRead);
        if(receiveFirstTime){
            receivedMessage = msg;
            receiveFirstTime = false;
            if(msg.substring(msg.length()-1).equals(".")){
                receiveFirstTime = true;
                if(receivedMessage.equals(Constants.ARDUINO_TURN_COMPLETE_SIGNAL)){
                    mDetector.setTurnCompleted();
                }
            }
        }else{
            receivedMessage += msg;
            if(msg.substring(msg.length()-1).equals(".")){
                receiveFirstTime = true;
                if(receivedMessage.equals(Constants.ARDUINO_TURN_COMPLETE_SIGNAL)){
                    mDetector.setTurnCompleted();
                }
            }
        }
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bResetColorState:
                mDetector.resetStateMachine();
                stopSignalSent = false;
                //numSignsToSearch = Integer.parseInt(etNumSigns.getText().toString());
                //mDetector.setNumSignsToSearch(numSignsToSearch);
                int start = Integer.parseInt(etStartRoom.getText().toString());
                int end = Integer.parseInt(etEndRoom.getText().toString());
                mDetector.resetMap(start,end);
                sendMessageToArduino(mDetector.getDirectionFromMap());
            case R.id.bStartBluetooth:
                bluetoothManager.connectToDevice();
        }
    }
}
