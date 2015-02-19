package com.android.bluetooth.ble;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.os.Handler;
import android.widget.TextView;

/**
 * Created by anish_khattar25 on 2/12/15.
 */
public class BluetoothScan {

    private BluetoothAdapter mBluetoothAdapter;
    private Handler mHandler;
    private static final long SCAN_PERIOD = 2000;
    private TextView tvScanResult;
    private static final Object[] scanLock = new Object[]{};
    private Activity backActivity;
    private ScanThread scanRun;
    private Thread scanThread;

    public BluetoothScan( BluetoothAdapter adapter,TextView results,Activity activity){
        this.mBluetoothAdapter = adapter;
        this.tvScanResult = results;
        mHandler = new Handler();
        this.backActivity = activity;
    }

    public void startInitialScan(){
        startThread();
       synchronized (scanLock) {
           scanLock.notify();
       }

    }

    public void stopThread(){
        try {
            if(scanThread != null) {
                scanThread.join();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void startHandler() {
            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
                    scanLock.notify();

                }
            }, SCAN_PERIOD);
    }

    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {
                @Override
                public void onLeScan(final BluetoothDevice device, final int rssi,
                                     byte[] scanRecord) {
                    backActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            String text =  tvScanResult.getText().toString();
                            text += "Device Name: " + device.getName() + " RSSI:" + rssi + "\n";
                            tvScanResult.setText(text);
                        }
                    });
                }
            };

    private void startThread() {
        scanRun = new ScanThread();
        scanThread = new Thread(scanRun);
        scanThread.start();
    }

    private class ScanThread implements  Runnable{

        @Override
        public void run() {
            for(;;){
                synchronized (scanLock){
                    try {
                        scanLock.wait();
                        backActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tvScanResult.setText("");
                            }
                        });
                        mBluetoothAdapter.startLeScan(mLeScanCallback);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
