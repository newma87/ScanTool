package com.newma.scantool.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.KeyEvent;
import android.widget.TextView;

import com.newma.scantool.R;

/**
 * Created by newma on 17-9-11.
 */

public class CustomDialog {
    Dialog m_dlg;
    TextView m_message;
    TextView m_title;

    public CustomDialog(Context context, String title) {
        m_dlg = new Dialog(context);
        m_dlg.setOnKeyListener(new Dialog.OnKeyListener() {

            @Override
            public boolean onKey(DialogInterface arg0, int keyCode,
                                 KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    return true;
                }
                return false;
            }
        });
        m_dlg.setContentView(R.layout.dialog_tess_process);

        m_title = (TextView)m_dlg.findViewById(R.id.dlg_txt_title);
        m_title.setText(title);
        m_message = (TextView) m_dlg.findViewById(R.id.dlg_txt_msg);
    }

    public void setMessage(String msg) {
        m_message.setText(msg);
    }

    public void setTitle(String title) {
        m_title.setText(title);
    }

    public void show() {
        m_dlg.show();
    }

    public void hide() {
        m_dlg.dismiss();
    }
}
