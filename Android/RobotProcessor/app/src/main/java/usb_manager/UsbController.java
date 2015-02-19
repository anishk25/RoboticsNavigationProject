package usb_manager;

import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;

import java.util.Objects;

/**
 * Created by anish_khattar25 on 2/17/15.
 */
public class UsbController {
    private UsbManager mUsbManager;
    private UsbDevice mUsbDevice;
    private UsbDeviceConnection mDeviceConnection;
    private UsbEndpoint endPointIN,endPointOUT;
    private final Object usbSendLock = new Object();
    private Thread sendThread,receiveThread;


    private byte[] usbSendData;
    private boolean stopUSBThreads = false;


    public UsbController(Context context, Intent intent) throws USBDeviceConnectionException {
        mUsbManager = (UsbManager)context.getSystemService(Context.USB_SERVICE);
        mUsbDevice = (UsbDevice)intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
        if(mUsbDevice != null && mUsbManager.hasPermission(mUsbDevice)){
            startController();
        }else{
            throw new USBDeviceConnectionException("Couldn't initiate USB device");
        }
    }

    private void startController() throws USBDeviceConnectionException {
        if(sendThread != null){
            // threads already running
            return;
        }
        openUSBConnection();
        stopUSBThreads = false;
        sendThread = new Thread(new UsbSendRunnable());
        receiveThread = new Thread(new UsbReceiveRunnable());

        sendThread.start();
        receiveThread.start();
    }

    public void stopUSBThread() {
        stopUSBThreads = true;
        synchronized (usbSendLock) {
           usbSendLock.notify();
        }
        try {
            if(sendThread != null) {
                sendThread.join();
            }
            if(receiveThread != null){
                receiveThread.join();
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        sendThread = null;
        receiveThread = null;
    }

    public void sendData(byte [] data){
        usbSendData = data.clone();
        usbSendLock.notify();
    }

    private void openUSBConnection() throws USBDeviceConnectionException {
        mDeviceConnection = mUsbManager.openDevice(mUsbDevice);
        if(!mDeviceConnection.claimInterface(mUsbDevice.getInterface(1),true)){
            throw new USBDeviceConnectionException("Couldn't Claim USB Interface!");
        }

        mDeviceConnection.controlTransfer(0x21, 34, 0, 0, null, 0, 0);
        mDeviceConnection.controlTransfer(0x21,32,0,0,new byte[]{(byte)0x80,0x25,0x00, 0x00, 0x00, 0x00, 0x08},7,0);
        mDeviceConnection.controlTransfer(0x40, 0x03, 0x4138, 0, null, 0, 0); //Baudrate 9600

        UsbInterface usbItf = mUsbDevice.getInterface(1);

        for(int i = 0; i < usbItf.getEndpointCount(); i++){
            UsbEndpoint currEndPt = usbItf.getEndpoint(i);
            if(currEndPt.getType() == UsbConstants.USB_ENDPOINT_XFER_BULK)
                if(currEndPt.getDirection() == UsbConstants.USB_DIR_IN){
                    endPointIN = currEndPt;
                }else{
                    endPointOUT = currEndPt;
                }
        }
    }

    private class UsbSendRunnable implements Runnable{

        @Override
        public void run() {
            for(;;){
                synchronized (usbSendLock){
                    try{
                        usbSendLock.wait();
                        if(usbSendData != null) {
                            mDeviceConnection.bulkTransfer(endPointOUT, usbSendData, usbSendData.length, 20);
                            usbSendData = null;
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                if(stopUSBThreads){
                    return;
                }
            }

        }
    }

    private class UsbReceiveRunnable implements Runnable{

        @Override
        public void run() {
            for(;;){
                final byte[] data = new byte[50];
                int len = mDeviceConnection.bulkTransfer(endPointIN,data,50,20);
                if(len > 0){
                    // send data here to particle filter
                }
                if(stopUSBThreads){
                    return;
                }
            }
        }
    }

}
