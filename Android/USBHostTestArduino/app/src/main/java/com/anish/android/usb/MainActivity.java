package com.anish.android.usb;


import android.app.Activity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;




public class MainActivity extends ActionBarActivity implements View.OnClickListener {

    private UsbController usbController;
    private Button bStartTransfer,bBrightLED,bDarkLED;
    private SeekBar seekBar;
    private TextView tvDebug;
    final String brightMessage = "BRIT";
    final String darkMessage = "DARK";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initUI();

    }

    private void initUI(){
        bStartTransfer = (Button)findViewById(R.id.bStartTransfer);
        bBrightLED = (Button)findViewById(R.id.bBrightLED);
        bDarkLED = (Button)findViewById(R.id.bDarkLED);

        seekBar = (SeekBar)findViewById(R.id.seekBar);
        tvDebug = (TextView)findViewById(R.id.tvDebug);



        bStartTransfer.setOnClickListener(this);
        bBrightLED.setOnClickListener(this);
        bDarkLED.setOnClickListener(this);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser && usbController != null){
                    usbController.sendData(new byte[]{(byte)(progress)});
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
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

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.bStartTransfer:
                if(usbController == null){
                    usbController = new UsbController(getApplicationContext(),getIntent(),tvDebug,this);
                }else{
                    usbController.stopUSBThread();
                    usbController = new UsbController(getApplicationContext(),getIntent(),tvDebug,this);
                }
                break;
            case R.id.bDarkLED:
                usbController.sendData(darkMessage.getBytes());
                break;

            case R.id.bBrightLED:
                usbController.sendData(brightMessage.getBytes());
                break;
        }
    }
}
