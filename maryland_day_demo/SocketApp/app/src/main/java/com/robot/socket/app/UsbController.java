package com.robot.socket.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.widget.TextView;

/**
 * Created by anish_khattar25 on 1/21/15.
 */
public class UsbController {
    private Context mApplicationContext;
    private Intent mIntent;
    private Activity mActivity;
    private UsbManager mUsbManager;
    private UsbDevice usbDevice;
    private TextView tvDebug;
    private UsbSendThread mUsbSend;
    private Thread mUsbSendThread;
    private  UsbEndpoint epIN,epOUT;
    private UsbDeviceConnection connection;


    public UsbController(Context context, Intent intent, TextView tvDebug, Activity activity){
        mApplicationContext = context;
        mIntent = intent;
         mUsbManager = (UsbManager)mApplicationContext.getSystemService(Context.USB_SERVICE);
        usbDevice = (UsbDevice)mIntent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
        mActivity = activity;
        this.tvDebug = tvDebug;

        if(usbDevice != null && mUsbManager.hasPermission(usbDevice)){
            tvDebug.setText("Arduino has permission!");
            startHandler();
        }else{
            tvDebug.setText("No Device Connected!");
        }
    }



    private void startHandler() {
        if (mUsbSend != null) {
            tvDebug.setText("Thread is already running!");
            return;
        }
        openUSBConnection();
        mUsbSend = new UsbSendThread();
        mUsbSendThread = new Thread(mUsbSend);
        mUsbSendThread.start();

    }

    private void openUSBConnection(){
        connection = mUsbManager.openDevice(usbDevice);
        if(!connection.claimInterface(usbDevice.getInterface(1),true)){
            return;
        }
        connection.controlTransfer(0x21,34,0,0,null,0,0);
        connection.controlTransfer(0x21,32,0,0,new byte[]{(byte)0x80,0x25,0x00, 0x00, 0x00, 0x00, 0x08},7,0);
        connection.controlTransfer(0x40, 0x03, 0x4138, 0, null, 0, 0); //Baudrate 9600


        UsbInterface usbItf = usbDevice.getInterface(1);

        for(int i = 0; i < usbItf.getEndpointCount(); i++){
            UsbEndpoint currEndPt = usbItf.getEndpoint(i);
            if(currEndPt.getType() == UsbConstants.USB_ENDPOINT_XFER_BULK)
                if(currEndPt.getDirection() == UsbConstants.USB_DIR_IN){
                    epIN = currEndPt;
                }else{
                    epOUT = currEndPt;
                }
        }
    }



    public void sendData(byte[] data) {
        mData = data.clone();
        synchronized (sSendLock) {
            sSendLock.notify();
        }
    }



    public void stopUSBThread() {
        mStop = true;
        synchronized (sSendLock) {
            sSendLock.notify();
        }
        try {
            if(mUsbSendThread != null) {
                mUsbSendThread.join();
            }

        } catch (InterruptedException e) {
           e.printStackTrace();
        }
        mStop = false;
        mUsbSend = null;
        mUsbSendThread = null;
    }

    // MAIN LOOP
    private static final Object[] sSendLock = new Object[]{};

    private boolean mStop = false;
    private byte mData[];

    private class UsbSendThread implements Runnable{


        @Override
        public void run() {
            for(;;){
                synchronized (sSendLock) {
                    try {
                        sSendLock.wait();
                        connection.bulkTransfer(epOUT, mData, mData.length, 20);
                    } catch (InterruptedException e) {
                        if (mStop) {
                            tvDebug.setText("Usb Stopped");
                            return;
                        }
                        e.printStackTrace();
                    }
                }
                if(mStop){
                    tvDebug.setText("Usb Stopped");
                    return;
                }
            }

        }
    }














}
