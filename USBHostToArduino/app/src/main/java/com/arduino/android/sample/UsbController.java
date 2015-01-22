package com.arduino.android.sample;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbConfiguration;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by anish_khattar25 on 1/21/15.
 */
public class UsbController {
    private Context mApplicationContext;
    private Intent mIntent;
    private UsbManager mUsbManager;
    private UsbDevice usbDevice;
    private final String ACTION_USB_PERMISSION= "com.arduino.android.sample.USB_PERMISSION";
    private TextView tvDebug;
    private UsbRunnable mLoop;
    private Thread mUsbThread;

    public UsbController(Context context,Intent intent,TextView tvDebug){
        mApplicationContext = context;
        mIntent = intent;
        mUsbManager = (UsbManager)mApplicationContext.getSystemService(Context.USB_SERVICE);
        usbDevice = (UsbDevice)mIntent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
        this.tvDebug = tvDebug;

        if(usbDevice != null && mUsbManager.hasPermission(usbDevice)){
            tvDebug.setText("Arduino has permission!");
            startHandler();
        }
    }



    private void startHandler() {
        if (mLoop != null) {
            tvDebug.setText("Thread is already running!");
            return;
        }
        mLoop = new UsbRunnable();
        mUsbThread = new Thread(mLoop);
        mUsbThread.start();

    }

    public void sendData(byte data) {
        mData = data;
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
            if(mUsbThread != null)
                mUsbThread.join();
        } catch (InterruptedException e) {
           e.printStackTrace();
        }
        mStop = false;
        mLoop = null;
        mUsbThread = null;
    }

    // MAIN LOOP
    private static final Object[] sSendLock = new Object[]{};
    private boolean mStop = false;
    private byte mData = 0x00;

    private class UsbRunnable implements Runnable{


        @Override
        public void run() {
            UsbDeviceConnection connection = mUsbManager.openDevice(usbDevice);
            if(!connection.claimInterface(usbDevice.getInterface(1),true)){
                return;
            }
            connection.controlTransfer(0x21,34,0,0,null,0,0);
            connection.controlTransfer(0x21,32,0,0,new byte[]{(byte)0x80,0x25,0x00, 0x00, 0x00, 0x00, 0x08},7,0);
            connection.controlTransfer(0x40, 0x03, 0x4138, 0, null, 0, 0); //Baudrate 9600

            UsbEndpoint epIN = null;
            UsbEndpoint epOUT = null;

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

            for(;;){
                synchronized (sSendLock){
                    try{
                        sSendLock.wait();
                    }catch (InterruptedException e) {
                        if (mStop) {
                            tvDebug.setText("Usb Stopped");
                            return;
                        }
                        e.printStackTrace();
                    }
                }
                connection.bulkTransfer(epOUT,new byte[]{mData},1,0);

                if(mStop){
                    tvDebug.setText("Usb Stopped");
                    return;
                }
            }
        }
    }












}
