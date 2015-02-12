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




public class MainActivity extends ActionBarActivity {

    private UsbController usbController;
    private Button bStartTransfer;
    private SeekBar seekBar;
    private TextView tvDebug;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initUI();

    }

    private void initUI(){
        bStartTransfer = (Button)findViewById(R.id.bStartTransfer);
        seekBar = (SeekBar)findViewById(R.id.seekBar);
        tvDebug = (TextView)findViewById(R.id.tvDebug);

        final Activity activity = this;

        bStartTransfer.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(usbController == null){
                    usbController = new UsbController(getApplicationContext(),getIntent(),tvDebug,activity);
                }else{
                    usbController.stopUSBThread();
                    usbController = new UsbController(getApplicationContext(),getIntent(),tvDebug,activity);
                }
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser && usbController != null){
                    usbController.sendData((byte)(progress & 0xFF));
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
}
