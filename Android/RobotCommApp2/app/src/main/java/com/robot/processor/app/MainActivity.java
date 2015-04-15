package com.robot.processor.app;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.robot.processor.constants.Constants;


public class MainActivity extends ActionBarActivity implements View.OnClickListener {

    private Button bStartCamera;
    private EditText etNumSigns;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initUI();
    }

    private void initUI(){
        bStartCamera = (Button)findViewById(R.id.bStartCamera);
        etNumSigns = (EditText)findViewById(R.id.etNumSigns);
        bStartCamera.setOnClickListener(this);

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
            case R.id.bStartCamera:
                Intent intent = new Intent(this,ColorBlobDetectionActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt(Constants.NUM_SIGNS_BUNDLE_KEY,Integer.parseInt(etNumSigns.getText().toString()));
                intent.putExtras(bundle);
                startActivity(intent);
                break;
        }
    }
}
