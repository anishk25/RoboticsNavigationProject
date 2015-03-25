package com.robot.tesstwotest;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.File;

public class MainActivity extends ActionBarActivity {

    private TextView tvInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvInfo = (TextView)findViewById(R.id.tvInfo);
        String data_path = "/storage/sdcard0/";
        File f = new File(data_path + "/TessTwoOcrLang/" + "eng.traineddata");

        tvInfo.setText(f.getTotalSpace()+"");

        Drawable d = getResources().getDrawable(R.drawable.ic_launcher);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        //File mfile=new File("/res/drawable/ic_launcher.png");
        //Bitmap bitmap = BitmapFactory.decodeFile(mfile.getAbsolutePath(), options);
        Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(),R.drawable.ic_launcher);
        TessBaseAPI baseApi = new TessBaseAPI();
        Log.d("Storage name",Environment.getExternalStorageDirectory().toString());
        //File sdcard = new File("/mnt/sdcard/");
        String SD_CARD_PATH = Environment.getExternalStorageDirectory().toString();
        //SD_CARD_PATH = "/mnt/sdcard/";
        SD_CARD_PATH=Environment.getExternalStorageDirectory().toString();
        //File sdcard = new File(SD_CARD_PATH + "/" + "tessdata/");
        File sdcard = Environment.getExternalStorageDirectory();
        File[] files = sdcard.listFiles();
        /*for(int i=0;i<files.length;i++){
            Log.d("File 1",files[i].getName());
        }*/
        //File sdcard2 = new File(sdcard.getAbsoluteFile().toString() + "/" + "tessdata");
        //Log.d("ufhkdsihfksdk",sdcard.getAbsoluteFile().toString());
        //File file = new File(getExternalCacheDir(), "tesseract-ocr-3.02.eng.tar" );
        baseApi.init(sdcard.getAbsoluteFile().toString(), "eng");
        baseApi.setVariable("tessedit_char_whitelist", "abcdefghijklmnopqrstuvwxyz");
        baseApi.setImage(bitmap);
        String recognizedText = baseApi.getUTF8Text();
        baseApi.end();
        Log.d("Recognized Text", recognizedText);
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
