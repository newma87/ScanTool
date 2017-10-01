package com.newma.scantool.logic;

import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.SimpleFormatter;

/**
 * Created by newma on 17-9-16.
 */

public class Utils {
    private static final String log_file = Environment.getExternalStorageDirectory().toString() + "/scantool_runtime.log";
    private static final String prefix = "";
    private static final String endfix = "\n";
    public static void AddLog(String message) {
        String tt = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss ").format(new Date());
        String logStr  = prefix + tt + message + endfix;
        try {
            FileOutputStream fos = new FileOutputStream(new File(log_file));
            fos.write(logStr.getBytes());
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
