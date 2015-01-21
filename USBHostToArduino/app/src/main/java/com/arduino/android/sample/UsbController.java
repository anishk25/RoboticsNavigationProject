package com.arduino.android.sample;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by anish_khattar25 on 1/21/15.
 */
public class UsbController {
    private Context mApplicationContext;
    private UsbManager mUsbManager;
    private final String ACTION_USB_PERMISSION= "com.arduino.android.sample.USB_PERMISSION";
    private UsbBroadcastReceiver usbBroadcastReceiver;
    private TextView tvDebug;

    public UsbController(Context context,TextView tvDebug){
        mApplicationContext = context;
        mUsbManager = (UsbManager)mApplicationContext.getSystemService(Context.USB_SERVICE);
        this.tvDebug = tvDebug;
        usbBroadcastReceiver = new UsbBroadcastReceiver();
        initUSB();
    }

    public void initUSB(){
        HashMap<String,UsbDevice> devList = mUsbManager.getDeviceList();
        Iterator<UsbDevice> deviceIterator = devList.values().iterator();
        while(deviceIterator.hasNext()){
            UsbDevice d = deviceIterator.next();
            MyLog.e("Found Device " + "Vendor Id:" + d.getVendorId() + " Product Id:" +  d.getProductId());
            if(d.getVendorId() == Constants.ARDUINO_VID && d.getProductId() == Constants.ARDUINO_PID){
                // check if arduino has permission to connect
                if(mUsbManager.hasPermission(d)){
                    tvDebug.setText("USB device has permission!");
                    // start thread here
                    return;
                }else{
                    requestPermissionForUSB(d);
                }
                break;
            }
        }
    }


    private void requestPermissionForUSB(UsbDevice d){
        PendingIntent pendingIntent = PendingIntent.getBroadcast(mApplicationContext,0, new Intent(ACTION_USB_PERMISSION),0);
        IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
        mApplicationContext.registerReceiver(usbBroadcastReceiver,filter);
        mUsbManager.requestPermission(d,pendingIntent);
    }


    private class UsbBroadcastReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action.equals(ACTION_USB_PERMISSION)){
                synchronized (this){
                    if(intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED,false)){
                        MyLog.e("Permission Granted to USB Device!");
                        UsbDevice usbDevice = (UsbDevice)intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                        if(usbDevice != null && usbDevice.getVendorId() == Constants.ARDUINO_VID && usbDevice.getProductId() == Constants.ARDUINO_PID){
                            MyLog.e("Arduino MEGA Found!");
                            tvDebug.setText("Arduino Granted Permission");
                            // start new thread here
                        }else{
                            MyLog.e("Unknown usb device attached");
                        }

                    }else{
                        MyLog.e("permission denied for USB Device");
                    }
                }
            }

        }
    }






}
