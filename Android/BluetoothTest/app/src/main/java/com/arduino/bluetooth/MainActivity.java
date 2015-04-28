package com.arduino.bluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.ParcelUuid;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.Set;
import java.util.UUID;


public class MainActivity extends Activity implements View.OnClickListener{

    private static final int REQUEST_ENABLE_BT = 1;
    private BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private static final String HC05_BLUETOOTH_ADDRESS = "98:D3:31:40:10:9E";
    private BluetoothDevice hc05Device;
    private UUID hc05_UUID;
    private BluetoothManager bluetoothManager;

    //UI Elements
    private TextView tvBluetooth;
    private Button bConnect,bSendMsg;
    private EditText etMessage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initUI();
        enableBluetooth();
        findHC05Device();
        bluetoothManager = new BluetoothManager(hc05Device,mBluetoothAdapter,hc05_UUID);
    }

    private void initUI(){
        tvBluetooth = (TextView)findViewById(R.id.tvBluetooth);
        bConnect = (Button)findViewById(R.id.bStartBluetoothConnection);
        bSendMsg = (Button)findViewById(R.id.bSendMessage);
        etMessage = (EditText)findViewById(R.id.etMessage);

        bConnect.setOnClickListener(this);
        bSendMsg.setOnClickListener(this);

    }

    private void enableBluetooth(){
        if(mBluetoothAdapter != null && !mBluetoothAdapter.isEnabled()){
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent,REQUEST_ENABLE_BT);
        }
    }

    private void findHC05Device(){
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        if(pairedDevices.size() > 0){
            for(BluetoothDevice device: pairedDevices){
                if(device.getAddress().equals(HC05_BLUETOOTH_ADDRESS)){
                    hc05Device = device;
                }
            }
        }
        if(hc05Device != null){
            hc05_UUID = UUID.fromString(hc05Device.getUuids()[0].toString());
            tvBluetooth.setText("Found Device with name " + hc05Device.getName() + " and UUID " + hc05Device.getUuids()[0].toString() ) ;
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

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.bStartBluetoothConnection:
                    bluetoothManager.connectToDevice();
                break;
            case R.id.bSendMessage:
                    String msg = etMessage.getText().toString();
                    if(msg.length() > 0){
                        bluetoothManager.sendMessage(msg.getBytes());
                    }
                break;
        }
    }
}
