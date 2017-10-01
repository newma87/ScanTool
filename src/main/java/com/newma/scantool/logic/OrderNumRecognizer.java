package com.newma.scantool.logic;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by newma on 17-9-11.
 */

public class OrderNumRecognizer {
    static final String LOG_TAG = OrderNumRecognizer.class.getName();
    public static String DEF_SPLIT_CHAR = "    ";

    static boolean isValidateOrderNum(String str) {
        if (str.length() != 12) {
            Log.d(LOG_TAG, "order number length invalidate!");
            return false;
        }

        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if ( c < '0' || c > '9') {
                Log.d(LOG_TAG, "order number must be made up by digital. ");
                return false;
            }
        }

        return true;
    }

    public static int encodeRecognizeResult(String result, StringBuffer retCode) {
        int validate = 0;
        String[] lines = result.split("\n");
        for (String line : lines) {
            String[] words = line.split(" ");
            String codeLine = "";
            for (String word : words) {
                if (isValidateOrderNum(word)) {  // order number
                    codeLine += word;
                    validate++;
                } else if (!codeLine.equals("") && word.contains("元")) { // money
                    codeLine += DEF_SPLIT_CHAR + word;
                }
            }
            if (!codeLine.equals("")) {
                retCode.append(codeLine + "\n");
            }
        }

        return validate;
    }

    public static void decodeRecognizeResult(String result, List<String>ordList, List<String> moneyList) {
        String[] lines = result.split("\n");
        for (String line : lines) {
            if (line.equals("")) {
                continue;
            }

            String[] infos =line.split(DEF_SPLIT_CHAR);
            ordList.add(infos[0]);
            moneyList.add(infos[1]);
        }
    }
}
