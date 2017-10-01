package com.newma.scantool.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.newma.scantool.MyApplicatioin;
import com.newma.scantool.logic.OrderNumRecognizer;
import com.newma.scantool.R;
import com.newma.scantool.logic.TessProcessTask;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements TessProcessTask.TessProcessCallback {
    final static String LOG_TAG = MainActivity.class.getName();

    public static final String CODE_LIST = "code list";

    final static int PICK_IMAGE_REQUEST = 1;
    Button m_btnRecognize;
    Button m_btnQRGen;
    ImageView m_imgSelected;
    Bitmap m_image;
    EditText m_txtResults;

    CustomDialog m_dlg;

    TessProcessTask m_task;
    String m_srcCode;
    String m_encodeCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar bar = getSupportActionBar();
        bar.setTitle(getString(R.string.app_name));

        m_txtResults = (EditText)findViewById(R.id.edt_result);
        m_btnRecognize = (Button) findViewById(R.id.btn_recognize);
        m_btnRecognize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.this.doScanText();
            }
        });
        m_btnQRGen = (Button)findViewById(R.id.btn_QRGen);
        m_btnQRGen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String temp = m_txtResults.getText().toString();
                String[] datas = temp.split("\n");
                if (datas.length == 0 || (datas.length == 1 && datas[0].equals(""))) {
                    Toast.makeText(MainActivity.this, "亲，没有订单号可生成", Toast.LENGTH_SHORT).show();
                } else{
                    Intent intent = new Intent(MainActivity.this, SliderActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString(CODE_LIST, temp);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            }
        });

        m_imgSelected = (ImageView) findViewById(R.id.img_picture);
        m_imgSelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(Intent.createChooser(intent, "请选择图片"), PICK_IMAGE_REQUEST);
            }
        });
    }

    private void doScanText() {
        if (m_image == null) {
            Toast.makeText(this, "请先选择要识别的图片", Toast.LENGTH_LONG).show();
            return;
        }
        m_dlg = new CustomDialog(this, "图片识别中，需要几分钟，请耐心等候...");
        m_dlg.setMessage("");
        m_dlg.show();
        m_btnRecognize.setEnabled(false);
        MyApplicatioin app = (MyApplicatioin)getApplication();
        m_task = new TessProcessTask(app.getTess());
        m_task.setCallback(this);
        m_task.execute(m_image);
    }

    @Override
    public void onProgressCallback(int processed, int total) {
        m_dlg.setMessage(processed + " / " + total);
    }

    @Override
    public void onResultCallback(ArrayList<String> results) {
        m_srcCode = results.get(0);
        m_dlg.hide();
        m_btnRecognize.setEnabled(true);

        Log.d(LOG_TAG, "recognize result: \n" + m_srcCode);
        StringBuffer buf = new StringBuffer();
        int count = OrderNumRecognizer.encodeRecognizeResult(m_srcCode, buf);

        m_encodeCode = buf.toString();
        m_txtResults.setText(m_encodeCode);
        m_task = null;
        Toast.makeText(this, "识别完成，共识别出" + count + "个单号", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Log.d(LOG_TAG, "on active result");
            Uri uri = data.getData();
            try {
                m_image = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                m_imgSelected.setImageBitmap(m_image);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
