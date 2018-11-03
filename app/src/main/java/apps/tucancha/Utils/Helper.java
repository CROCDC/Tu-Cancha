package apps.tucancha.Utils;
/*
  Creado por Camilo 05/06/2018.
 */


import android.graphics.Bitmap;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.List;

import apps.tucancha.Model.Jugador;

public class Helper {
    public static final String MEDIA_STORAGE = "https://firebasestorage.googleapis.com/v0/b/tu-cancha-c4289.appspot.com/o/Clubes%2FRiver%2FArmani.jpg?alt=media&token=";
    public static final Integer RESULT_OK = 1;
    public static final Integer RESULT_ONBACKPRESS = 2;


    public static List<Jugador> parseJsonJugadores(String jsonLine) {
        List<Jugador> listaDeJugadores = new ArrayList<>();

        JsonElement jelement = new JsonParser().parse(jsonLine);
        JsonObject jsonObject = jelement.getAsJsonObject();
        JsonArray jsonArray = jsonObject.getAsJsonArray("ListaDeJugadores");
        Gson gson = new Gson();

        for (Integer i = 0; i < jsonArray.size(); i++) {
            listaDeJugadores.get(9).setUrlFoto(MEDIA_STORAGE + listaDeJugadores.get(0).getUrlFoto());
            listaDeJugadores.add(gson.fromJson(jsonArray.get(i).toString(), Jugador.class));
        }

        return listaDeJugadores;

    }

    public static Bitmap recortarImagenQR(Bitmap bitmap){
        return Bitmap.createBitmap(bitmap,bitmap.getHeight() - 400,bitmap.getWidth() / 2,400,400);
    }


}




