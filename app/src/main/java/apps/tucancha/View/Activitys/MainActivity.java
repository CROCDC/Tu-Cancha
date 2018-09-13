package apps.tucancha.View.Activitys;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import apps.tucancha.BuildConfig;
import apps.tucancha.Elementos_Creados.Pizarra;
import apps.tucancha.Elementos_Creados.Screnshot;
import apps.tucancha.Elementos_Creados.SistemaDragAndDrop;
import apps.tucancha.View.Fragments.IngresarJugadorFragment;
import apps.tucancha.Model.Jugador;
import apps.tucancha.R;
import apps.tucancha.View.Fragments.JugadorFragment;
import apps.tucancha.View.Fragments.ListaDeClubesFragment;
import apps.tucancha.View.Fragments.ListaDeJugadoresFragment;

/**
 * Esta Activity se encarga de recibir las imagenes de cargar los fragments y de poner a los jugadores en la cancha y encargarse del drag and drop
 */

public class MainActivity extends AppCompatActivity implements IngresarJugadorFragment.NotificadorHaciaMainActivity, ListaDeClubesFragment.NotificadorDesdeClubesHaciaMainActivity, ListaDeJugadoresFragment.NotificadorDesdeJugadoresHaciaMainActivity {


    private ImageView imageViewAdd;


    private IngresarJugadorFragment ingresarJugadorFragment;
    private ListaDeJugadoresFragment listaDeJugadoresFragment;
    private ListaDeClubesFragment listaDeClubesFragment;

    private ViewGroup rootLayout;

    private RelativeLayout.LayoutParams layoutParams;

    private String yaBuscoUnJugador = "";

    private Toolbar toolbar;
    private Pizarra pizarra;

    private Bitmap bitmapScreenshot;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        rootLayout = findViewById(R.id.relativeLayoutContenedor_activitymain);
        pizarra = findViewById(R.id.pizarra_activitymain);
        imageViewAdd = findViewById(R.id.imageViewButtonAdd_activitymain);

        layoutParams = new RelativeLayout.LayoutParams(200, 200);


        toolbar.inflateMenu(R.menu.menuprincipal);


        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.opcionDibujar:
                        if (pizarra.isHabilitado()) {
                            pizarra.deshabilitarPizarra();
                            pizarra.borrarDibujo();
                            toolbar.getMenu().getItem(0).setIcon(R.drawable.pencil);

                        } else {
                            pizarra.habilitarPizarra();
                            toolbar.getMenu().getItem(0).setIcon(R.drawable.pencilwhite);
                        }

                        break;
                    case R.id.opcionCompartir:
                        View view = getWindow().getDecorView().getRootView();
                        bitmapScreenshot = Screnshot.hacerScreenshotFormatoBitmap(view);
                        Screnshot.createFolder("Tu cancha");
                        try {
                            Screnshot.guardarScreenshot("1",bitmapScreenshot,getApplicationContext());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }


                }
                return true;
            }
        });

        touchImageViewButtonAdd();


    }


    /**
     * Este metodo se encarga de agregar el fragment IngresarJugador que es el que recibe los datos de imagenes y nombre del usuario
     */

    public void touchImageViewButtonAdd() {
        imageViewAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ingresarJugadorFragment = new IngresarJugadorFragment();
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction().addToBackStack("Camilo");
                fragmentTransaction.add(R.id.frameLayoutContenedor_activitymain, ingresarJugadorFragment);
                fragmentTransaction.commit();

            }
        });
    }

    /**
     * este metodo es de la interfaz de IngresarJugadorFragment y le notifica a la activity que tiene que agregar un jugador con nombre pero sin imagen
     *
     * @param nombreDelJugador
     */

    @Override
    public void notificarAgregarJugadorSinFoto(String nombreDelJugador) {
        Jugador jugador = new Jugador(nombreDelJugador, 1);
        agregarFragmentJugadorSinFoto(jugador);
    }

    /**
     * Este Metodo es de la intefaz de IngresarJugadorFragment y le notifica a la activity que tiene que agregar un jugador con nombreDelJugador y la imagen subida por el usuario
     *
     * @param file             imagen subida por el usuario
     * @param nombreDelJugador
     */
    @Override
    public void notificarAgregarJugadorPorFile(File file, String nombreDelJugador) {
        Jugador jugador = new Jugador(nombreDelJugador, file, 2);
        agregarFragmentJugadorConFoto(jugador);
    }

    /**
     * Este Metodo es de la intefaz de IngresarJugadorFragment y le notifica a la activity que tiene que agregar un jugador con nombreDelJugador y una imagen que esta guardada en storage
     *
     * @param url              url de la imagen guardada en storage
     * @param nombreDelJugador
     */

    @Override
    public void notificarAgregarJugadorConImagenStorage(String url, String nombreDelJugador) {
        toolbar.setVisibility(View.VISIBLE);

        Jugador jugador = new Jugador(nombreDelJugador, url, 3);
        agregarFragmentJugadorConFoto(jugador);
    }

    /**
     * Este metodo se encarga de agregar un jugador a la cancha
     *
     * @param jugador objeto jugador
     */

    public void agregarFragmentJugadorConFoto(Jugador jugador) {

        final JugadorFragment jugadorFragment = JugadorFragment.frabricaDeFragmentsJugador(jugador);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.relativeLayoutContenedor_activitymain, jugadorFragment).addToBackStack("Camilo");

        fragmentTransaction.remove(ingresarJugadorFragment);


        fragmentTransaction.commit();


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                //HAGO ESTO PARA QUE LE DE TIEMPO A CREAR LA VISTA
                jugadorFragment.getView().setOnTouchListener(new SistemaDragAndDrop(jugadorFragment, getSupportFragmentManager(), rootLayout));

            }
        }, 400);


    }

    /**
     * Este metodo se encarga de agregar un jugador sin foto a la cancha
     *
     * @param jugador objeto jugador
     */

    public void agregarFragmentJugadorSinFoto(Jugador jugador) {
        toolbar.setVisibility(View.VISIBLE);

        final JugadorFragment jugadorFragment = JugadorFragment.frabricaDeFragmentsJugador(jugador);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.relativeLayoutContenedor_activitymain, jugadorFragment).addToBackStack("Camilo");

        fragmentTransaction.remove(ingresarJugadorFragment);

        fragmentTransaction.commit();


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                //HAGO ESTO PARA QUE LE DE TIEMPO A CREAR LA VISTA
                jugadorFragment.getView().setOnTouchListener(new SistemaDragAndDrop(jugadorFragment, getSupportFragmentManager(), rootLayout));

            }
        }, 400);


    }


    /**
     * Metodo de la Interfaz NotificadorDesdeClubesHaciaMainActivity se encarga de cargar el Fragment Lista De Clubes
     */

    @Override
    public void notificarCargarListaDeClubes() {
        toolbar.setVisibility(View.GONE);
        if (yaBuscoUnJugador.equals("")) {
            listaDeClubesFragment = new ListaDeClubesFragment();
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(R.id.frameLayoutContenedor_activitymain, listaDeClubesFragment).addToBackStack("Camilo");
            fragmentTransaction.commit();
        } else {
            listaDeClubesFragment = new ListaDeClubesFragment();
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(R.id.frameLayoutContenedor_activitymain, listaDeClubesFragment).addToBackStack("Camilo");
            fragmentTransaction.commit();
            notificarTouchCeldaClubCargarFragmentListaDeJugadores(yaBuscoUnJugador);
        }

    }


    /**
     * Metodo de la Interfaz NotificadorDesdeJugadorHaciaMainActivity se encarga de cargar el Fragment Lista De Jugadores
     */

    @Override
    public void notificarTouchCeldaClubCargarFragmentListaDeJugadores(String nombreDelClub) {
        listaDeJugadoresFragment = new ListaDeJugadoresFragment();

        Bundle bundle = new Bundle();

        bundle.putString(ListaDeJugadoresFragment.CLAVE_CLUB, nombreDelClub);
        listaDeJugadoresFragment.setArguments(bundle);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.frameLayoutContenedor_activitymain, listaDeJugadoresFragment).addToBackStack("Camilo");
        fragmentTransaction.commit();

    }


    /**
     * Este metodo es de la Intefaz ListaDeJugadoresFragment le notifica a la activity que se toco un jugador y mediante un metodo la Activity le notifica al
     * fragment Ingresar Jugador que debe recargar la image de preview y el nombre ademas borra los fragments ListaDeClubes y ListaDeJugadores De La Pantalla
     *
     * @param jugador
     * @param nombreDelClub
     */
    @Override
    public void notificarTouchCeldaJugadorNotificarActualizarImagen(Jugador jugador, String nombreDelClub) {
        toolbar.setVisibility(View.VISIBLE);

        yaBuscoUnJugador = nombreDelClub;

        ingresarJugadorFragment.cargarImagenDelJugador(jugador, nombreDelClub);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.remove(listaDeClubesFragment);
        fragmentTransaction.remove(listaDeJugadoresFragment);
        fragmentTransaction.commit();

        fragmentManager.popBackStack();
    }

    @Override
    public void onBackPressed() {
       yaBuscoUnJugador = "";
       if (listaDeJugadoresFragment == null){
           if (listaDeJugadoresFragment.isVisible()){
               toolbar.setVisibility(View.GONE);
           }else {
               toolbar.setVisibility(View.VISIBLE);
           }
       }

        super.onBackPressed();



    }



}
