package com.robot.socket.app;

import android.app.Activity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;


public class MainActivity extends Activity implements View.OnClickListener {

    private TextView tvSocketMessage,tvArduinoDebug;
    private Button bStartTransfer,bSendMessage;
    private Socket mSocket;
    private static final String SERVER_URL = "http://floating-fortress-9962.herokuapp.com/";
    private static final String TAG = MainActivity.class.getCanonicalName();
    private static final String SOCKET_HANDLE = "robot_command";
    private UsbController usbController;
    private Emitter.Listener onRobotMessage;
    private String directionString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initUI();
        setupSocketIO();

    }

    private void initUI(){
        tvSocketMessage = (TextView)findViewById(R.id.tvSocketMessage);
        tvArduinoDebug = (TextView)findViewById(R.id.tvArduinoDebug);
        bStartTransfer = (Button)findViewById(R.id.bStartTransfer);
        bStartTransfer.setOnClickListener(this);
        bSendMessage = (Button)findViewById(R.id.bSendMessage);
        bSendMessage.setOnClickListener(this);
    }


    private void setupSocketIO(){

        onRobotMessage = new Emitter.Listener(){
            @Override
            public void call(final Object... args) {
                JSONObject data = (JSONObject) args[0];
                final String direction;
                try {
                    direction = data.getString("direction");
                    directionString = direction;
                    sendMessageToArduino(direction);
                } catch (JSONException e) {
                    return;
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                         tvSocketMessage.setText("Direction Received: " + direction);
                    }
                });
            }
        };

        try{
            mSocket = IO.socket(SERVER_URL);
            mSocket.on(SOCKET_HANDLE,onRobotMessage);
            mSocket.connect();
        }catch (URISyntaxException e){
            Log.d(TAG,e.getMessage());
        }
    }

    private void sendMessageToArduino(String msg){
        if(usbController != null){
            usbController.sendData(msg.getBytes());
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
        switch (v.getId()){
            case R.id.bStartTransfer:
                if(usbController == null){
                    usbController = new UsbController(getApplicationContext(),getIntent(),tvArduinoDebug,this);
                }else{
                    usbController.stopUSBThread();
                    usbController = new UsbController(getApplicationContext(),getIntent(),tvArduinoDebug,this);
                }
                break;
            case R.id.bSendMessage:
                if(usbController != null) {
                    usbController.sendData(directionString.getBytes());
                }
                break;
        }
    }
}
