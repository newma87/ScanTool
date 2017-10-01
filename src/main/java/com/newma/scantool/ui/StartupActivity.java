package com.newma.scantool.ui;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Bundle;
import android.os.Process;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Toast;

import com.newma.scantool.MyApplicatioin;
import com.newma.scantool.R;
import com.googlecode.tesseract.android.TessBaseAPI;
import com.newma.scantool.logic.Utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class StartupActivity extends Activity {
    static private final int ACCESS_EXTERNAL_FILE_CODE = 1;

    public class TrainDataLostException extends Exception {
        public TrainDataLostException(String debugStr) {
            super(debugStr);
        }
    }

    class InitTask extends AsyncTask<Void, Void, TessBaseAPI> {
        // tess-two
        final  String BASE_PATH = Environment.getExternalStorageDirectory().toString() + "/ScanTool";
        final  String DEFAULT_LAN = "chi_sim";
        final  String TRAIN_DATA_FILE = BASE_PATH + "/tessdata/" + DEFAULT_LAN + ".traineddata";

        private boolean hasSDCard() {
            boolean ret = false;
            if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
                ret = true;
            }
            return ret;
        }

        private void checkFile() throws TrainDataLostException, IOException {
            if (!hasSDCard()) {
                throw new TrainDataLostException("Can't not found sd card!");
            }
            File dir = new File(BASE_PATH);
            if (!dir.exists()) {
                if (!dir.mkdir()) {
                    throw new TrainDataLostException("can't create train data directory on external storage.");
                }
            }
            dir = new File(BASE_PATH + "/tessdata/");
            if (!dir.exists()){
                if (!dir.mkdir()) {
                    throw new TrainDataLostException("can't create train data directory on external storage.");
                }
            }
            File datafile = new File(TRAIN_DATA_FILE);
            if (!datafile.exists()) {
                copyFiles();
            }
        }

        private void copyFiles() throws TrainDataLostException, IOException {
            try {
                AssetManager assetManager = StartupActivity.this.getAssets();

                InputStream instream = assetManager.open(DEFAULT_LAN + ".traineddata");
                OutputStream outstream = new FileOutputStream(TRAIN_DATA_FILE);

                byte[] buffer = new byte[1024];
                int read;
                while ((read = instream.read(buffer)) != -1) {
                    outstream.write(buffer, 0, read);
                }

                outstream.flush();
                outstream.close();
                instream.close();

                File file = new File(TRAIN_DATA_FILE);
                if (!file.exists()) {
                    throw new FileNotFoundException();
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                throw new TrainDataLostException("setup trained data language file failed!");
            }
        }

        @Override
        protected TessBaseAPI doInBackground(Void... voids) {
            try {
                checkFile();
            } catch (Exception e) {
                e.printStackTrace();
                Utils.AddLog(e.getMessage());
                Process.killProcess(Process.myPid());
            }
            TessBaseAPI tess = new TessBaseAPI();
            tess.init(BASE_PATH, DEFAULT_LAN);
            return tess;
        }

        @Override
        protected void onPostExecute(TessBaseAPI tessBaseAPI) {
            MyApplicatioin app = (MyApplicatioin) StartupActivity.this.getApplication();
            app.setTess(tessBaseAPI);

            Intent intent = new Intent(StartupActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private InitTask mInitTask;
    private View mContentView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_startup);

        mContentView = findViewById(R.id.fullscreen_content);

        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

        requestPermission();
    }

    @Override
    public void onBackPressed() {
        return;
    }

    public void requestPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                    ACCESS_EXTERNAL_FILE_CODE);
        } else {            mInitTask = new InitTask();
            mInitTask.execute();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == ACCESS_EXTERNAL_FILE_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mInitTask = new InitTask();
                mInitTask.execute();
            } else {
                Toast.makeText(this, "请授权允许读写本地文件权限", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
