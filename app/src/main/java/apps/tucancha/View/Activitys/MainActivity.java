package apps.tucancha.View.Activitys;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.File;

import apps.tucancha.Utils.Paint_screen;
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

    private int _xDelta;
    private int _yDelta;

    private IngresarJugadorFragment ingresarJugadorFragment;
    private ListaDeJugadoresFragment listaDeJugadoresFragment;
    private ListaDeClubesFragment listaDeClubesFragment;

    private ViewGroup rootLayout;

    private RelativeLayout.LayoutParams layoutParams;

    private String yaBuscoUnJugador = "";

    private Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        toolbar = findViewById(R.id.toolbar);
        rootLayout = findViewById(R.id.relativeLayoutContenedor_activitymain);
        imageViewAdd = findViewById(R.id.imageViewButtonAdd_activitymain);

        layoutParams = new RelativeLayout.LayoutParams(200, 200);


        toolbar.inflateMenu(R.menu.menuprincipal);

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.opcionDibujar:
                        Paint_screen paint_screen = new Paint_screen(getApplicationContext());
                        setContentView(paint_screen);
                        break;
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
                jugadorFragment.getView().setOnTouchListener(new TouchFragmentJugador(jugadorFragment));

            }
        }, 400);


    }

    /**
     * Este metodo se encarga de agregar un jugador sin foto a la cancha
     *
     * @param jugador objeto jugador
     */

    public void agregarFragmentJugadorSinFoto(Jugador jugador) {

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
                jugadorFragment.getView().setOnTouchListener(new TouchFragmentJugador(jugadorFragment));

            }
        }, 400);


    }


    /**
     * Esta Clase es la encargada de administrar el drag and drop de los fragments jugador
     */

    private final class TouchFragmentJugador implements View.OnTouchListener {
        private JugadorFragment jugadorFragment;

        public TouchFragmentJugador(JugadorFragment jugadorFragment) {
            this.jugadorFragment = jugadorFragment;
        }

        @Override
        public boolean onTouch(View view, MotionEvent event) {
            final int X = (int) event.getRawX();
            final int Y = (int) event.getRawY();
            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN:
                    RelativeLayout.LayoutParams lParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
                    _xDelta = X - lParams.leftMargin;
                    _yDelta = Y - lParams.topMargin;
                    break;
                case MotionEvent.ACTION_UP:
                    break;
                case MotionEvent.ACTION_POINTER_DOWN:
                    break;
                case MotionEvent.ACTION_POINTER_UP:
                    break;
                case MotionEvent.ACTION_MOVE:
                    RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view
                            .getLayoutParams();
                    layoutParams.leftMargin = X - _xDelta;
                    layoutParams.topMargin = Y - _yDelta;
                    layoutParams.rightMargin = -250;
                    layoutParams.bottomMargin = -250;
                    view.setLayoutParams(layoutParams);

                    if (X < 70 && Y > 1200) {
                        FragmentManager fragmentManager = getSupportFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.remove(jugadorFragment);
                        fragmentTransaction.commit();
                    }
                    break;
            }
            rootLayout.invalidate();
            return true;
        }
    }


    /**
     * Metodo de la Interfaz NotificadorDesdeClubesHaciaMainActivity se encarga de cargar el Fragment Lista De Clubes
     */

    @Override
    public void notificarCargarListaDeClubes() {
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
        super.onBackPressed();
        yaBuscoUnJugador = "";
    }
}
