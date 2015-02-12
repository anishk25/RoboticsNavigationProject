package com.anish.android.usb;

import android.content.Context;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import usb.android.anish.com.usblib.driver.UsbSerialDriver;
import usb.android.anish.com.usblib.driver.UsbSerialPort;
import usb.android.anish.com.usblib.driver.UsbSerialProber;
import usb.android.anish.com.usblib.util.HexDump;
import usb.android.anish.com.usblib.util.SerialInputOutputManager;


public class MainActivity extends ActionBarActivity {

    private UsbManager manager;
    private UsbDeviceConnection deviceConnection;
    private UsbSerialDriver usbSerialDriver;
    private UsbSerialPort usbSerialPort;
    private List<UsbSerialDriver> availableDrivers;
    private Button bOpenConnection,blinkLed;
    private TextView tvStatus,tvArduinoMessages;
    private SeekBar sbLedControl;
    private SerialInputOutputManager mSerialIoManager;
    private final ExecutorService mExecutor = Executors.newSingleThreadExecutor();

    private final SerialInputOutputManager.Listener mListener = new SerialInputOutputManager.Listener() {

        @Override
        public void onRunError(Exception e) {
            Log.d("USB ERROR", "Runner stopped.");
        }

        @Override
        public void onNewData(final byte[] data) {
            tvStatus.setText("Receving Message from Arduino!");
            MainActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    updateReceivedData(data);
                }
            });

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        manager = (UsbManager)getSystemService(Context.USB_SERVICE);
        setupUI();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopIoManager();
    }

    @Override
    protected  void onResume(){
        super.onResume();
        startIoManager();
    }

    private void setupUI(){
        bOpenConnection = (Button)findViewById(R.id.bOpenConnection);
        blinkLed = (Button)findViewById(R.id.bBlink);
        tvStatus = (TextView)findViewById(R.id.tvStatus);
        sbLedControl = (SeekBar)findViewById(R.id.sbLedControl);
        tvArduinoMessages = (TextView)findViewById(R.id.tvArduinoMsgs);

        bOpenConnection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connectToDevice();
            }
        });

        blinkLed.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                byte buffer[] = {(byte)(1 & 0xFF)};
                sendData(buffer);
            }
        });

        sbLedControl.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                byte buffer[] = {(byte)(progress & 0xFF)};
                sendData(buffer);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void connectToDevice(){
        availableDrivers = UsbSerialProber.getDefaultProber().findAllDrivers(manager);
        if(availableDrivers.isEmpty()){
            tvStatus.setText("No USB Devices Found!");
        }else{
            usbSerialDriver = availableDrivers.get(0);
            deviceConnection = manager.openDevice(usbSerialDriver.getDevice());

            if(deviceConnection == null){
                tvStatus.setText("Device doesn't have permission to attach");
            }else{
                tvStatus.setText("Device Attached! Vendor Id is " + usbSerialDriver.getDevice().getVendorId());
                usbSerialPort = usbSerialDriver.getPorts().get(0);
                startIoManager();
                try {
                    usbSerialPort.open(deviceConnection);
                    usbSerialPort.setParameters(9600, 8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE);
                } catch (IOException e) {
                    tvStatus.setText("Failed to Open Device");
                    e.printStackTrace();
                }
            }
        }
    }

    private void sendData(byte data[]){
        if(usbSerialPort != null) {
            try {
                usbSerialPort.write(data, 1000);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void updateReceivedData(byte[] data) {
        final String message = "Read " + data.length + " bytes: \n"
                + HexDump.dumpHexString(data) + "\n\n";
        tvArduinoMessages.setText(message);
    }

    private void stopIoManager() {
        if (mSerialIoManager != null) {
            mSerialIoManager.stop();
            mSerialIoManager = null;
        }
    }

    private void startIoManager() {
        if ( usbSerialPort != null) {
            mSerialIoManager = new SerialInputOutputManager( usbSerialPort, mListener);
            mExecutor.submit(mSerialIoManager);
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
