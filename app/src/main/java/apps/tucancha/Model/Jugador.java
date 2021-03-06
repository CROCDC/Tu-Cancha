package apps.tucancha.Model;

import java.io.File;
import java.io.Serializable;

/**
 * Clase Model de Jugador
 */
public class Jugador implements Serializable {
    private String nombre;
    private String urlFoto;
    private File foto;
    private Integer tipoJugador;
    private float x;
    private float y;



    public Jugador() {
    }

    public Jugador(String nombre, Integer tipoJugador) {
        this.nombre = nombre;
        this.tipoJugador = tipoJugador;
    }

    public Jugador(String nombre, String urlFoto, Integer tipoJugador) {
        this.nombre = nombre;
        this.tipoJugador = tipoJugador;
        this.urlFoto = urlFoto;
    }

    public Jugador(String nombre, File foto,Integer tipoJugador) {
        this.nombre = nombre;
        this.tipoJugador = tipoJugador;
        this.foto = foto;
    }

    public Jugador(String nombre, String urlFoto, Integer tipoJugador, float y, float x) {
        this.nombre = nombre;
        this.urlFoto = urlFoto;
        this.tipoJugador = tipoJugador;
        this.y = y;
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getX() {
        return x;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setUrlFoto(String urlFoto) {
        this.urlFoto = urlFoto;
    }

    public void setFoto(File foto) {
        this.foto = foto;
    }

    public void setTipoJugador(Integer tipoJugador) {
        this.tipoJugador = tipoJugador;
    }

    public void setX(float x) {
        this.x = x;

    }


    public Integer getTipoJugador() {
        return tipoJugador;
    }

    public File getFoto() {
        return foto;
    }

    public String getNombre() {
        return nombre;
    }

    public String getUrlFoto() {
        return urlFoto;
    }
}
