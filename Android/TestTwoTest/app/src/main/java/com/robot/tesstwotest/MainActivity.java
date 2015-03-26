package com.robot.tesstwotest;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.File;

public class MainActivity extends ActionBarActivity implements View.OnClickListener{

    private TextView tvRecognizedText;
    private Button bTakePhoto;
    private TessBaseAPI tessBaseAPI;

    private static final String DATA_PATH = "/storage/sdcard0/TessTwoOcrLang/";
    //private static final String CHAR_LIST = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ123456790";
    private static final String CHAR_LIST = "123456790";

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    public static final String LANG = "eng";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initUIAndApi();
    }

    private void initUIAndApi(){
        tvRecognizedText = (TextView)findViewById(R.id.tvRecognizedText);
        bTakePhoto = (Button)findViewById(R.id.bTakePhoto);
        bTakePhoto.setOnClickListener(this);

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
            case R.id.bTakePhoto:
                takePhoto();
                break;

        }
    }

    private void takePhoto(){
        Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(takePhotoIntent.resolveActivity((getPackageManager()))!= null){
            startActivityForResult(takePhotoIntent,REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK){
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            processBitmap(imageBitmap);
        }
    }

    private void processBitmap(Bitmap bitmap){
        bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        tessBaseAPI = new TessBaseAPI();
        tessBaseAPI.setDebug(true);
        tessBaseAPI.init(DATA_PATH,LANG);
        tessBaseAPI.setVariable(TessBaseAPI.VAR_CHAR_WHITELIST,CHAR_LIST);
        tessBaseAPI.setImage(bitmap);
        String recognizedText = tessBaseAPI.getUTF8Text();
        tessBaseAPI.end();
        tvRecognizedText.setText("Recognized text: " + recognizedText );
    }
}
