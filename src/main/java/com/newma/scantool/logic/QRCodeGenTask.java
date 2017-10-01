package com.newma.scantool.logic;

import android.graphics.Bitmap;
import android.os.AsyncTask;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.util.ArrayList;

/**
 * Created by newma on 17-9-11.
 */

public class QRCodeGenTask extends AsyncTask<String, Integer, ArrayList<Bitmap>> {
    public final static int QRcodeWidth = 500 ;

    public interface QRCodeGenCallback {
        void onQRProgressCallback(int processed, int total);
        void onQRResults(ArrayList<Bitmap> bitmaps);
    }

    QRCodeGenCallback m_callback;

    public void setCallback(QRCodeGenCallback callback) {
        m_callback = callback;
    }

    @Override
    protected ArrayList<Bitmap> doInBackground(String... strings) {
        ArrayList<Bitmap> results = new ArrayList<>();
        int i = 0;
        for (String str : strings) {
            Bitmap bm = null;
            try {
                bm = textToQRCode(str);
            } catch (WriterException e) {
                e.printStackTrace();
            }
            results.add(bm);
            publishProgress(++i, strings.length);
        }
        return results;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        if (m_callback != null) {
            m_callback.onQRProgressCallback(values[0], values[1]);
        }
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(ArrayList<Bitmap> bitmaps) {
        if (m_callback != null) {
            m_callback.onQRResults(bitmaps);
        }
        super.onPostExecute(bitmaps);
    }

    Bitmap textToQRCode(String Value) throws WriterException {
        BitMatrix bitMatrix;
        try {
            bitMatrix = new MultiFormatWriter().encode(
                    Value,
                    BarcodeFormat.DATA_MATRIX.QR_CODE,
                    QRcodeWidth, QRcodeWidth, null
            );
        } catch (IllegalArgumentException Illegalargumentexception) {
            return null;
        }
        int bitMatrixWidth = bitMatrix.getWidth();
        int bitMatrixHeight = bitMatrix.getHeight();

        int[] pixels = new int[bitMatrixWidth * bitMatrixHeight];

        for (int y = 0; y < bitMatrixHeight; y++) {
            int offset = y * bitMatrixWidth;

            for (int x = 0; x < bitMatrixWidth; x++) {
                pixels[offset + x] = bitMatrix.get(x, y) ? 0xff000000 : 0xffffffff;
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(bitMatrixWidth, bitMatrixHeight, Bitmap.Config.ARGB_4444);

        bitmap.setPixels(pixels, 0, 500, 0, 0, bitMatrixWidth, bitMatrixHeight);
        return bitmap;
    }

}
