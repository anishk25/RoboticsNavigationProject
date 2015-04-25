package com.robot.socket.app;

import android.app.Activity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;


public class MainActivity extends Activity {

    private TextView tvSocketMessage;
    private Socket mSocket;
    private static final String SERVER_URL = "http://floating-fortress-9962.herokuapp.com/";
    private static final String TAG = MainActivity.class.getCanonicalName();
    private static final String SOCKET_HANDLE = "robot_command";
    private Emitter.Listener onRobotMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvSocketMessage = (TextView)findViewById(R.id.tvSocketMessage);
        setupSocketIO();

    }


    private void setupSocketIO(){

        onRobotMessage = new Emitter.Listener(){
            @Override
            public void call(final Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        JSONObject data = (JSONObject) args[0];
                        String direction;
                        try {
                            direction = data.getString("direction");
                            tvSocketMessage.setText("Direction Received: " + direction);
                        } catch (JSONException e) {
                            return;
                        }
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
