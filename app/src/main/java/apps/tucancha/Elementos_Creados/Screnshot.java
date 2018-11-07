package apps.tucancha.Elementos_Creados;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.view.View;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

public class Screnshot {

    /**
     * este metodo se encarga de tomar la screnshot en formato file
     *
     * @param view necesita una view que abarque toda la pantalla se hace con este metodo getWindow().getDecorView().getRootView();
     */
    public static File hacerScreenshotFormatoFile(View view) {
        Date now = new Date();
        android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", now);

        try {
            // image naming and path  to include sd card  appending name you choose for file
            String mPath = Environment.getExternalStorageDirectory().toString() + "/" + now + ".jpg";

            // create bitmap screen capture
            view.setDrawingCacheEnabled(true);
            Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
            view.setDrawingCacheEnabled(false);

            File imageFile = new File(mPath);

            FileOutputStream outputStream = new FileOutputStream(imageFile);
            int quality = 200;
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
            outputStream.flush();
            outputStream.close();

            //Mi imagen imagefile

            return imageFile;

        } catch (Throwable e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * este metodo se encarga de tomar la screnshot en formato bitmab
     *
     * @param view necesita una view que abarque toda la pantalla se hace con este metodo getWindow().getDecorView().getRootView();
     */

    public static Bitmap hacerScreenshotFormatoBitmap(View view) {
        Date now = new Date();
        android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", now);

        try {
            // image naming and path  to include sd card  appending name you choose for file

            // create bitmap screen capture
            view.setDrawingCacheEnabled(true);
            Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
            view.setDrawingCacheEnabled(false);

            return bitmap;
        } catch (Exception e) {
            return null;
        }
    }

    public static Bitmap resizearScreenshot(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;

        Matrix matrix = new Matrix();

        matrix.postScale(scaleWidth, scaleHeight);

        Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);

        return resizedBitmap;
    }

    public static void guardarScreenshot(String imgName, Bitmap bm, Context context)
            throws IOException {

        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES + "/Tucancha"); //Creates app specific folder
        path.mkdirs();
        File imageFile = new File(path, imgName + ".png"); // Imagename.png
        FileOutputStream out = new FileOutputStream(imageFile);
        try {
            bm.compress(Bitmap.CompressFormat.PNG, 100, out); // Compress Image
            out.flush();
            out.close();

            // Tell the media scanner about the new file so that it is
            // immediately available to the user.
            MediaScannerConnection.scanFile(context, new String[]{imageFile.getAbsolutePath()}, null, new MediaScannerConnection.OnScanCompletedListener() {
                public void onScanCompleted(String path, Uri uri) {

                }
            });
        } catch (Exception e) {
            throw new IOException();
        }
    }

    public static void createFolder(String fname) {
        String myfolder = Environment.getExternalStorageDirectory() +Environment.DIRECTORY_PICTURES + "/" + fname;
        File f = new File(myfolder);

    }


}

