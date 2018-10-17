package apps.tucancha.Model;

import java.util.List;

public class Cancha {
    private String nombre;
    private List<Jugador> listaDeJugadores;

    public Cancha(){

    }

    public Cancha(String nombre, List<Jugador> listaDeJugadores) {
        this.nombre = nombre;
        this.listaDeJugadores = listaDeJugadores;
    }


    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public List<Jugador> getListaDeJugadores() {
        return listaDeJugadores;
    }

    public void setListaDeJugadores(List<Jugador> listaDeJugadores) {
        this.listaDeJugadores = listaDeJugadores;
    }
}
