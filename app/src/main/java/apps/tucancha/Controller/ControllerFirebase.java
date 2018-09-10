package apps.tucancha.Controller;

import java.util.List;

import apps.tucancha.DAO.DAOFirebase;
import apps.tucancha.Model.Jugador;
import apps.tucancha.Utils.ResultListener;
/**
 * Clase encargada de administrar los pedidos de la vista al DAO tanto de database como storage
 */

public class ControllerFirebase {


    private DAOFirebase daoFirebase;

    public ControllerFirebase() {
        daoFirebase = new DAOFirebase();
    }

    /**
     * metodo encargado de pedir al DAO la lista de clubes almacenada en firebase
     * @param escuchadorDeLaVista
     */

    public void pedirListaDeClubes(final ResultListener<List<String>> escuchadorDeLaVista){
        daoFirebase.pedirListaDeClubes(new ResultListener<List<String>>() {
            @Override
            public void finish(List<String> resultado) {
                escuchadorDeLaVista.finish(resultado);
            }
        });
    }

    /**
     * metodo encargado de pedir al DAO la lista de jugadores por club
     * @param nombreDelClub
     * @param escuchadorDeLaVista
     */

    public void pedirListaDeJugadoresPorClub(String nombreDelClub, final ResultListener<List<Jugador>> escuchadorDeLaVista){
        new DAOFirebase().pedirListaDeJugadoresPorClub(nombreDelClub, new ResultListener<List<Jugador>>() {
            @Override
            public void finish(List<Jugador> resultado) {
                escuchadorDeLaVista.finish(resultado);
            }
        });
    }

}
