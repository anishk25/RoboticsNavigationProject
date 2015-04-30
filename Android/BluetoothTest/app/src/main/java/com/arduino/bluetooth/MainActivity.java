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
    private static final String HC05_BLUETOOTH_ADDRESS2 = "98:D3:31:90:0C:7C";
    private static final String SMIRF_BLUETOOTH_ADDRESS = "00:06:66:64:35:A6";
    private BluetoothDevice pairedBluetoothDevice;
    private UUID pairedDeviceUUID;
    private BluetoothManager bluetoothManager;

    //UI Elements
    private TextView tvBluetooth,tvReceivedText;
    private Button bConnect,bSendMsg;
    private EditText etMessage;
    String receivedMessage;
    boolean receiveFirstTime = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initUI();
        enableBluetooth();
        findBluetoothDevice(HC05_BLUETOOTH_ADDRESS2);
        bluetoothManager = new BluetoothManager(pairedBluetoothDevice,mBluetoothAdapter,pairedDeviceUUID,this);
    }

    private void initUI(){
        tvBluetooth = (TextView)findViewById(R.id.tvBluetooth);
        tvReceivedText = (TextView)findViewById(R.id.tvReceivedText);
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

    private void findBluetoothDevice(String address){
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        String s = "";
        if(pairedDevices.size() > 0){
            for(BluetoothDevice device: pairedDevices){
               if(device.getAddress().equals(address)){
                    pairedBluetoothDevice = device;
                }
                //s+= "Device Name: " + device.getName() + " Address: " + device.getAddress()+"\n";

            }
        }
        if(pairedBluetoothDevice != null){
            pairedDeviceUUID = UUID.fromString(pairedBluetoothDevice.getUuids()[0].toString());
            tvBluetooth.setText("Found Device with name " + pairedBluetoothDevice.getName() + " and UUID " + pairedBluetoothDevice.getUuids()[0].toString() ) ;
        }
        //tvBluetooth.setText(s);
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
                    if(msg.length() > 0 && msg.length() < 4){
                        if(msg.length() == 1){
                            msg = "00" + msg;
                        }else if(msg.length() == 2){
                            msg = "0" + msg;
                        }
                        bluetoothManager.sendMessage(msg.getBytes());
                    }
                break;
        }
    }

    public void updateReceivedText(int bytesRead, byte[] buffer){
        String msg = new String(buffer,0,bytesRead);
        //tvReceivedText.setText(msg);
        if(receiveFirstTime){
            receivedMessage = msg;
            receiveFirstTime = false;
            if(msg.substring(msg.length()-1).equals(".")){
                receiveFirstTime = true;
                tvReceivedText.setText(receivedMessage);
            }
        }else{
            receivedMessage += msg;
            if(msg.substring(msg.length()-1).equals(".")){
                receiveFirstTime = true;
                tvReceivedText.setText(receivedMessage);
            }
        }
    }
}
