package apps.tucancha.View.Activitys;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.io.File;
import java.io.FileOutputStream;

import apps.tucancha.Elementos_Creados.Pizarra;
import apps.tucancha.Elementos_Creados.Screnshot;
import apps.tucancha.Elementos_Creados.SistemaDragAndDrop;
import apps.tucancha.Utils.Helper;
import apps.tucancha.View.Fragments.IngresarJugadorFragment;
import apps.tucancha.Model.Jugador;
import apps.tucancha.R;
import apps.tucancha.View.Fragments.JugadorFragment;
import apps.tucancha.View.Fragments.ListaDeClubesFragment;
import apps.tucancha.View.Fragments.ListaDeJugadoresFragment;

/**
 * Esta Activity se encarga de recibir las imagenes de cargar los fragments y de poner a los jugadores en la cancha y encargarse del drag and drop
 */

public class MainActivity extends AppCompatActivity implements IngresarJugadorFragment.NotificadorHaciaMainActivity {


    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123;


    private IngresarJugadorFragment ingresarJugadorFragment;
    private ListaDeJugadoresFragment listaDeJugadoresFragment;
    private ListaDeClubesFragment listaDeClubesFragment;

    private ViewGroup rootLayout;

    private RelativeLayout.LayoutParams layoutParams;

    private String yaBuscoUnJugador = "";

    private Toolbar toolbar;
    private Pizarra pizarra;
    private RelativeLayout linearLayoutContenedorPizarra;
    private ImageView imageViewAdd;
    private Bitmap bitmapScreenshot;

    public static final String CLAVE_JUGADOR = "jugador";
    public static final String CLAVE_CLUB = "club";
    public static final String CLAVE_BUNDLE ="bundle";

    private String nombreDelClubRecibido;
    private Jugador jugadorRecibido;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        rootLayout = findViewById(R.id.relativeLayoutContenedor_activitymain);
        pizarra = findViewById(R.id.pizarra_activitymain);
        imageViewAdd = findViewById(R.id.imageViewButtonAdd_activitymain);
        linearLayoutContenedorPizarra = findViewById(R.id.linearLayoutContenedorSoloPizarra_activitymain);
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
                        bitmapScreenshot = Screnshot.hacerScreenshotFormatoBitmap(linearLayoutContenedorPizarra);

                        startShare(bitmapScreenshot);


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
        fragmentTransaction.add(R.id.linearLayoutContenedorSoloPizarra_activitymain, jugadorFragment).addToBackStack("Camilo");

        fragmentTransaction.remove(ingresarJugadorFragment);


        fragmentTransaction.commit();


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                //HAGO ESTO PARA QUE LE DE TIEMPO A CREAR LA VISTA
                jugadorFragment.getView().setOnTouchListener(new SistemaDragAndDrop(jugadorFragment, getSupportFragmentManager(), linearLayoutContenedorPizarra));

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
        fragmentTransaction.add(R.id.linearLayoutContenedorSoloPizarra_activitymain, jugadorFragment).addToBackStack("Camilo");

        fragmentTransaction.remove(ingresarJugadorFragment);

        fragmentTransaction.commit();


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                //HAGO ESTO PARA QUE LE DE TIEMPO A CREAR LA VISTA
                jugadorFragment.getView().setOnTouchListener(new SistemaDragAndDrop(jugadorFragment, getSupportFragmentManager(), linearLayoutContenedorPizarra));

            }
        }, 400);


    }


    /**
     * Metodo de la Interfaz NotificadorDesdeClubesHaciaSeleccionarJugadorActivity se encarga de cargar la Activity SeleccionarJugador
     */

    @Override
    public void notificarCargarListaDeClubes() {

        if (yaBuscoUnJugador.equals("")) {
            Intent intent = new Intent(MainActivity.this, ListaDeClubesActivity.class);
            startActivityForResult(intent, 1);


        } else {
            Intent intent = new Intent(MainActivity.this,ListaDeJugadoresActivity.class);

            Bundle bundle = new Bundle();

            bundle.putString(ListaDeJugadoresActivity.CLAVE_CLUB, yaBuscoUnJugador);

            intent.putExtras(bundle);

            startActivityForResult(intent,10);


        }

    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode){
            case 10:

        }

        super.onActivityResult(requestCode, resultCode, data);


    }


    private void startShare(Bitmap bitmap) {


        if (Build.VERSION.SDK_INT >= 24) {
            try {

                File file = new File(this.getExternalCacheDir(), "cancha.png");
                FileOutputStream fOut = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
                fOut.flush();
                fOut.close();
                file.setReadable(true, false);
                final Intent intent = new Intent(android.content.Intent.ACTION_SEND);
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                Uri uriFromFile = FileProvider.getUriForFile(getBaseContext(), "apps.tucancha.Utils.GenericFileProvider", file);
                intent.putExtra(Intent.EXTRA_STREAM, uriFromFile);
                intent.setType("image/png");
                startActivity(Intent.createChooser(intent, "Compartir via"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {

            try {
                File file = new File(this.getExternalCacheDir(), "cancha.png");
                FileOutputStream fOut = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
                fOut.flush();
                fOut.close();
                file.setReadable(true, false);
                final Intent intent = new Intent(android.content.Intent.ACTION_SEND);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
                intent.setType("image/png");
                startActivity(Intent.createChooser(intent, "Compartir via"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


}





