package apps.tucancha.DAO;

import android.os.AsyncTask;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.xpath.XPath;

import apps.tucancha.Model.Jugador;
import apps.tucancha.Utils.ResultListener;

public class DAOScraping extends AsyncTask<String, ResultListener<List<Jugador>>, List<Jugador>> {

    private ResultListener<List<Jugador>> escuchador;

    public DAOScraping(ResultListener<List<Jugador>> escuchador){
        this.escuchador = escuchador;
    }

    public List<Jugador> obtenerListaDeJugadoresPorClub(String nombreDelClub) {
        String busqueda = "https://www.google.com.ar/search?q=jugadores+" + nombreDelClub;

        Document doc = null;

        List<Jugador> jugadores = new ArrayList<>();

        try {
            doc = Jsoup.connect(busqueda).get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Elements elements = doc.select("a.klitem");

        for (Element element : elements) {

            String nombreJugador;
            String imgJugador;

            Elements elementsImage = element.select("img");

            Element elementImage = elementsImage.get(0);

            nombreJugador = element.attributes().asList().get(1).getValue();
            imgJugador =elementImage.attributes().asList().get(0).getValue();

            Jugador jugador = new Jugador(nombreJugador, imgJugador, 3);

            jugadores.add(jugador);

        }

        return jugadores;
    }


    @Override
    protected void onPostExecute(List<Jugador> jugadores) {
        super.onPostExecute(jugadores);
        escuchador.finish(jugadores);
    }

    @Override
    protected List<Jugador> doInBackground(String... strings) {
        return obtenerListaDeJugadoresPorClub(strings[0]);

    }
}
