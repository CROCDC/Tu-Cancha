package apps.tucancha.Utils;
/*
  Creado por Camilo 05/06/2018.
 */


import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.LuminanceSource;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import com.google.zxing.qrcode.QRCodeWriter;

import net.glxn.qrgen.android.QRCode;

import java.util.ArrayList;
import java.util.List;

import apps.tucancha.Model.Jugador;

public class Helper {
    public static final String MEDIA_STORAGE = "https://firebasestorage.googleapis.com/v0/b/tu-cancha-c4289.appspot.com/o/Clubes%";
    public static final Integer RESULT_OK = 1;
    public static final Integer RESULT_ONBACKPRESS = 2;


    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static List<Jugador> parseJsonJugadores(String jsonLine) {
        List<Jugador> listaDeJugadores = new ArrayList<>();

        JsonElement jelement = new JsonParser().parse(jsonLine);
        JsonObject jsonObject = jelement.getAsJsonObject();
        JsonArray jsonArray = jsonObject.getAsJsonArray("ListaDeJugadores");
        Gson gson = new Gson();

        for (Integer i = 0; i < jsonArray.size(); i++) {
            listaDeJugadores.add(gson.fromJson(jsonArray.get(i).toString(), Jugador.class));
            listaDeJugadores.get(i).setUrlFoto(MEDIA_STORAGE + listaDeJugadores.get(i).getUrlFoto());

        }

        return listaDeJugadores;

    }

    public static Bitmap recortarImagenQR(Bitmap bitmap) {
        Integer x = 0;
        Integer heigthQR;

        for (Integer i = 0; i <= bitmap.getHeight(); i++) {


            if (bitmap.getPixel(x, i) == Color.WHITE) {
                heigthQR = i;


                return Bitmap.createBitmap(bitmap, x, heigthQR, bitmap.getWidth(), bitmap.getHeight() - heigthQR);
            }


        }

        return null;
    }

    public static Bitmap crearQR(String texto, Integer width, Integer height) {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = null;
        try {
            bitMatrix = qrCodeWriter.encode(texto, BarcodeFormat.QR_CODE, width, height);
        } catch (WriterException e) {
            e.printStackTrace();
        }

        Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                bmp.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
            }
        }


        return bmp;


    }

    public static Bitmap creaQR2(String texto, Integer width, Integer heigth) {
        heigth *= 90;


        return QRCode.from(texto).withSize(width, heigth).bitmap();
    }

    public static String leerQR(Bitmap bitmap) {
        int[] intArray = new int[bitmap.getWidth() * bitmap.getHeight()];
        bitmap.getPixels(intArray, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
        LuminanceSource source = new RGBLuminanceSource(bitmap.getWidth(), bitmap.getHeight(), intArray);
        BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(source));

        QRCodeReader qrCodeReader = new QRCodeReader();

        Result result = null;
        try {
            result = qrCodeReader.decode(binaryBitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result.getText();
    }

    public static Bitmap bitMap1BottomBitmap2(Bitmap bmp1, Bitmap bmp2) {
        Integer width = null;


        if (bmp1.getWidth() < bmp2.getWidth()) {
            width = bmp2.getWidth();
            Bitmap bmOverlay = Bitmap.createBitmap(width, bmp1.getHeight() + bmp2.getHeight(), bmp1.getConfig());
            Canvas canvas = new Canvas(bmOverlay);
            canvas.drawBitmap(bmp1, new Matrix(), null);


            canvas.drawBitmap(bmp2, width / 2 - bmp2.getWidth(), bmp1.getHeight(), null);
            return bmOverlay;
        } else {
            width = bmp1.getWidth();
            Bitmap bmOverlay = Bitmap.createBitmap(width, bmp1.getHeight() + bmp2.getHeight(), bmp1.getConfig());
            Canvas canvas = new Canvas(bmOverlay);
            canvas.drawBitmap(bmp1, new Matrix(), null);
            canvas.drawBitmap(bmp2, width / 2 - bmp2.getWidth() / 2, bmp1.getHeight(), null);
            return bmOverlay;
        }

    }


}




