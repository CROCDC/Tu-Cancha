package apps.tucancha.View.Activitys;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.FileProvider;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import apps.tucancha.Controller.ControllerFirebase;
import apps.tucancha.Elementos_Creados.Pizarra;
import apps.tucancha.Elementos_Creados.Screnshot;
import apps.tucancha.Elementos_Creados.SistemaDragAndDrop;
import apps.tucancha.Model.Cancha;
import apps.tucancha.Model.Jugador;
import apps.tucancha.R;
import apps.tucancha.Utils.Helper;
import apps.tucancha.Utils.ResultListener;
import apps.tucancha.View.Fragments.IngresarJugadorFragment;
import apps.tucancha.View.Fragments.IngresarNombreDeLaCanchaFragment;
import apps.tucancha.View.Fragments.JugadorFragment;
import apps.tucancha.View.Fragments.OpcionCompartirImagenFragment;
import apps.tucancha.connector.Connector;

/**
 * Esta Activity se encarga de recibir las imagenes de cargar los fragments y de poner a los jugadores en la cancha y encargarse del drag and drop
 */

public class MainActivity extends AppCompatActivity implements IngresarJugadorFragment.NotificadorHaciaMainActivity, IngresarNombreDeLaCanchaFragment.NotificadorHaciaMainActivity, OpcionCompartirImagenFragment.NotificadorHaciaMainActivity {


    private IngresarJugadorFragment ingresarJugadorFragment;

    private ViewGroup rootLayout;

    private RelativeLayout.LayoutParams layoutParams;

    private String yaBuscoUnJugador = "";

    private ImageView imageViewButtonCanchasGuardadas;
    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private Pizarra pizarra;
    private RelativeLayout linearLayoutContenedorPizarra;
    private ImageView imageViewAdd;


    private final List<JugadorFragment> listaDeJugadoresFragments = new ArrayList<>();
    private List<Cancha> listaDeCanchas = new ArrayList<>();


    public static final String CLAVE_JUGADOR = "jugador";
    public static final String CLAVE_CLUB = "club";
    public static final String CLAVE_JSON = "json";

    private String nombreDelClubRecibido;
    private Jugador jugadorRecibido;

    private ControllerFirebase controllerFirebase;

    private NavigationView navigationView;
    private View header;
    private ProgressBar progressBarHeaderNavigation;


    private IngresarNombreDeLaCanchaFragment ingresarNombreDeLaCanchaFragment;
    private OpcionCompartirImagenFragment opcionCompartirImagenFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);

        imageViewButtonCanchasGuardadas = toolbar.findViewById(R.id.imageViewButtonCanchasGuardadas_toolbar);
        drawerLayout = findViewById(R.id.drawerLayoutContenedor_activitymain);
        rootLayout = findViewById(R.id.relativeLayoutContenedor_activitymain);
        pizarra = findViewById(R.id.pizarra_activitymain);
        imageViewAdd = findViewById(R.id.imageViewButtonAdd_activitymain);
        linearLayoutContenedorPizarra = findViewById(R.id.linearLayoutContenedorSoloPizarra_activitymain);
        navigationView = findViewById(R.id.navigationView_activitymain);
        header = navigationView.getHeaderView(0);
        progressBarHeaderNavigation = header.findViewById(R.id.progressBar_headernavigatioview);


        recibirImagenCompartida();

        controllerFirebase = new ControllerFirebase();

        toolbar.inflateMenu(R.menu.menuprincipal);

        pedirLaListaDeCanchas();


        toolbar.setOnMenuItemClickListener(new TouchListenerToolbar());
        navigationView.setNavigationItemSelectedListener(new TouchListenerNavigationView());
        imageViewButtonCanchasGuardadas.setOnClickListener(new ClickImageViewButtonCanchaGuardadas());


        touchImageViewButtonAdd();


    }

    private void recibirImagenCompartida() {
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (type != null) {
            if (type.startsWith("image/")) {
                Uri imageUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);

                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);

                    List<Jugador> listaDeJugadores = Helper.parseJsonJugadores(leerQR(bitmap));

                    for (Jugador jugador : listaDeJugadores) {
                        agregarFragmentJugadorConImageStorageYUbicacion(jugador);
                    }

                } catch (Exception e) {
                    e.printStackTrace();


                }
            }
        }
    }

    public void pedirLaListaDeCanchas() {
        progressBarHeaderNavigation.setVisibility(View.VISIBLE);
        controllerFirebase.pedirListaDeCanchas(new ResultListener<List<Cancha>>() {
            @Override
            public void finish(List<Cancha> resultado) {
                listaDeCanchas = resultado;
                agregrarCanchasGuardadaslNavigationView(resultado);
                progressBarHeaderNavigation.setVisibility(View.GONE);
            }


        });
    }

    private String leerQR(Bitmap bitmap) {

        bitmap = Helper.recortarImagenQR(bitmap);

        if (bitmap == null) {
            Toast.makeText(this, "La imagen que nos dio no contiene un codigo QR valido", Toast.LENGTH_SHORT).show();
        }
        int[] intArray = new int[bitmap.getWidth() * bitmap.getHeight()];
        bitmap.getPixels(intArray, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
        LuminanceSource source = new RGBLuminanceSource(bitmap.getWidth(), bitmap.getHeight(), intArray);
        BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(source));

        MultiFormatReader multiFormatReader = new MultiFormatReader();


        Result result = null;
        try {
            result = multiFormatReader.decode(binaryBitmap);
        } catch (Exception e) {
            Toast.makeText(this, "Se encontro un problema en la lectura del codigo QR", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

        return result.getText();
    }

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

    public void agregarFragmentJugadorConFoto(final Jugador jugador) {

        final JugadorFragment jugadorFragment = JugadorFragment.frabricaDeFragmentsJugador(jugador);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.linearLayoutContenedorSoloPizarra_activitymain, jugadorFragment).addToBackStack("Camilo");

        fragmentTransaction.remove(ingresarJugadorFragment);

        listaDeJugadoresFragments.add(jugadorFragment);

        fragmentTransaction.commitAllowingStateLoss();


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                //HAGO ESTO PARA QUE LE DE TIEMPO A CREAR LA VISTA
                jugadorFragment.getView().setOnTouchListener(new SistemaDragAndDrop(jugadorFragment, new ResultListener<JugadorFragment>() {
                    @Override
                    public void finish(JugadorFragment jugadorFragmentAEliminar) {
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.remove(jugadorFragment);
                        fragmentTransaction.commit();
                        listaDeJugadoresFragments.remove(jugadorFragmentAEliminar);
                    }
                }, (FrameLayout.LayoutParams) rootLayout.getLayoutParams()));

            }
        }, 400);


    }

    /**
     * Este metodo se encarga de agregar un jugador sin foto a la cancha
     *
     * @param jugador objeto jugador
     */

    public void agregarFragmentJugadorSinFoto(final Jugador jugador) {


        final JugadorFragment jugadorFragment = JugadorFragment.frabricaDeFragmentsJugador(jugador);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.linearLayoutContenedorSoloPizarra_activitymain, jugadorFragment).addToBackStack("Camilo");

        fragmentTransaction.remove(ingresarJugadorFragment);

        fragmentTransaction.commitAllowingStateLoss();

        listaDeJugadoresFragments.add(jugadorFragment);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                //HAGO ESTO PARA QUE LE DE TIEMPO A CREAR LA VISTA
                jugadorFragment.getView().setOnTouchListener(new SistemaDragAndDrop(jugadorFragment, new ResultListener<JugadorFragment>() {
                    @Override
                    public void finish(JugadorFragment jugadorFragmentAEliminar) {
                        listaDeJugadoresFragments.remove(jugadorFragmentAEliminar);
                    }
                }, (FrameLayout.LayoutParams) rootLayout.getLayoutParams()));

            }
        }, 400);


    }

    public void agregarFragmentJugadorConImageStorageYUbicacion(Jugador jugador) {
        final JugadorFragment jugadorFragment = JugadorFragment.frabricaDeFragmentsJugador(jugador);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.linearLayoutContenedorSoloPizarra_activitymain, jugadorFragment).addToBackStack("Camilo");

        if (ingresarJugadorFragment != null) {
            fragmentTransaction.remove(ingresarJugadorFragment);
        }

        fragmentTransaction.commitAllowingStateLoss();

        listaDeJugadoresFragments.add(jugadorFragment);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                //HAGO ESTO PARA QUE LE DE TIEMPO A CREAR LA VISTA
                jugadorFragment.getView().setOnTouchListener(new SistemaDragAndDrop(jugadorFragment, new ResultListener<JugadorFragment>() {
                    @Override
                    public void finish(JugadorFragment jugadorFragmentAEliminar) {
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.remove(jugadorFragment);
                        fragmentTransaction.commit();

                        listaDeJugadoresFragments.remove(jugadorFragmentAEliminar);
                    }
                }, jugador.getX(), jugador.getY(), (FrameLayout.LayoutParams) rootLayout.getLayoutParams()));

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
            Intent intent = new Intent(MainActivity.this, ListaDeJugadoresActivity.class);

            Bundle bundle = new Bundle();

            bundle.putString(ListaDeJugadoresActivity.CLAVE_CLUB, yaBuscoUnJugador);

            intent.putExtras(bundle);

            startActivityForResult(intent, 10);


        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode) {
            case 1:
                if (resultCode == Helper.RESULT_OK) {
                    Bundle bundleRecibido = data.getExtras();
                    nombreDelClubRecibido = bundleRecibido.getString(CLAVE_CLUB);
                    yaBuscoUnJugador = nombreDelClubRecibido;

                    Intent intent = new Intent(MainActivity.this, ListaDeJugadoresActivity.class);

                    Bundle bundleAEnviar = new Bundle();

                    bundleAEnviar.putString(ListaDeJugadoresActivity.CLAVE_CLUB, nombreDelClubRecibido);

                    intent.putExtras(bundleAEnviar);

                    startActivityForResult(intent, 10);

                    break;
                }


            case 10:
                if (resultCode == Helper.RESULT_OK) {
                    Bundle bundleRecibido1 = data.getExtras();
                    Jugador jugador = (Jugador) bundleRecibido1.getSerializable(CLAVE_JUGADOR);

                    agregarFragmentJugadorConFoto(jugador);

                    break;
                } else if (resultCode == Helper.RESULT_ONBACKPRESS) {
                    Intent intent = new Intent(MainActivity.this, ListaDeClubesActivity.class);
                    startActivityForResult(intent, 1);
                    yaBuscoUnJugador = "";
                }


        }

        super.onActivityResult(requestCode, resultCode, data);


    }

    private void compartirCancha(Bitmap bitmap) {


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

    private void agregrarCanchasGuardadaslNavigationView(List<Cancha> listaDeCanchasM) {
        Menu menu = navigationView.getMenu();
        menu.clear();


        for (Integer i = 0; i < listaDeCanchasM.size(); i++) {
            menu.add("");
            menu.getItem(i).setActionView(R.layout.celda_item_navigation_view);
            View view = menu.getItem(i).getActionView();
            TextView textView = view.findViewById(R.id.textViewNombreDeLaCancha_celdaitemnavigationview);
            ImageView imageView = view.findViewById(R.id.imageViewButtonBorrar_celdaitemnavigationview);
            textView.setText(listaDeCanchasM.get(i).getNombre() + "");

            Integer finalI = i;

            ClickImageViewButtonBorrarItemNavigationView clickImageViewButtonBorrarItemNavigationView = new ClickImageViewButtonBorrarItemNavigationView();
            clickImageViewButtonBorrarItemNavigationView.setI(finalI);
            imageView.setOnClickListener(clickImageViewButtonBorrarItemNavigationView);

            navigationView.invalidate();


        }
    }

    private class ClickImageViewButtonBorrarItemNavigationView implements View.OnClickListener {

        Integer i;

        @Override
        public void onClick(View v) {
            progressBarHeaderNavigation.setVisibility(View.VISIBLE);
            controllerFirebase.borrarCancha(listaDeCanchas.get(i), new ResultListener<Boolean>() {
                @Override
                public void finish(Boolean resultado) {
                    controllerFirebase.pedirListaDeCanchas(new ResultListener<List<Cancha>>() {
                        @Override
                        public void finish(List<Cancha> resultado) {
                            agregrarCanchasGuardadaslNavigationView(resultado);
                            Toast.makeText(MainActivity.this, "Cancha Borrada", Toast.LENGTH_SHORT).show();
                            progressBarHeaderNavigation.setVisibility(View.INVISIBLE);
                        }
                    });
                }
            });
        }

        public void setI(Integer i) {
            this.i = i;
        }
    }

    @Override
    public void notificarGuardarCancha(String nombreDeLaCancha) {
        getSupportFragmentManager().beginTransaction().remove(ingresarNombreDeLaCanchaFragment).commit();

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        if (listaDeJugadoresFragments.size() == 0) {
            Toast.makeText(this, "No puede guardar una cancha sin jugadores", Toast.LENGTH_SHORT).show();
            return;
        }


        List<Jugador> listaDeJugadores = new ArrayList<>();

        for (Integer i = 0; i < listaDeJugadoresFragments.size(); i++) {
            Jugador jugador = new Jugador(
                    listaDeJugadoresFragments.get(i).getJugador().getNombre(),
                    listaDeJugadoresFragments.get(i).getJugador().getUrlFoto(),
                    listaDeJugadoresFragments.get(i).getJugador().getTipoJugador()
            );

            listaDeJugadores.add(jugador);

            listaDeJugadores.get(i).setX(listaDeJugadoresFragments.get(i).getView().getX());
            listaDeJugadores.get(i).setY(listaDeJugadoresFragments.get(i).getView().getY());

        }


        controllerFirebase.guardarCancha(new Cancha(nombreDeLaCancha, listaDeJugadores), new ResultListener<Boolean>() {
            @Override
            public void finish(Boolean resultado) {

            }
        });

        controllerFirebase.pedirListaDeCanchas(new ResultListener<List<Cancha>>() {
            @Override
            public void finish(List<Cancha> resultado) {
                agregrarCanchasGuardadaslNavigationView(resultado);

                listaDeCanchas = resultado;
            }
        });
    }

    @Override
    public void CompartirConCodigoQR() {

        if (listaDeJugadoresFragments.size() >= 15) {
            Toast.makeText(this, "No se recomienda compartir una cancha con " + listaDeJugadoresFragments.size() + " Jugadores", Toast.LENGTH_SHORT).show();
        }
        Bitmap bitmap = Screnshot.hacerScreenshotFormatoBitmap(linearLayoutContenedorPizarra);
        Bitmap bimapQR = Helper.creaQR2(escribirLaCanchaEnJson(), bitmap.getWidth(), listaDeJugadoresFragments.size());
        compartirCancha(Helper.bitMap1BottomBitmap2(bitmap, bimapQR));
        opcionCompartirImagenFragment.detenerProgressBar();

    }

    @Override
    public void CompartirSinCodigoQR() {
        Bitmap bitmap = Screnshot.hacerScreenshotFormatoBitmap(linearLayoutContenedorPizarra);
        compartirCancha(bitmap);
        opcionCompartirImagenFragment.detenerProgressBar();
    }

    private class TouchListenerNavigationView implements NavigationView.OnNavigationItemSelectedListener {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            String nombreDeLaCancha;
            View view = item.getActionView();

            TextView textView = view.findViewById(R.id.textViewNombreDeLaCancha_celdaitemnavigationview);

            nombreDeLaCancha = (String) textView.getText();

            for (Integer i = 0; i < listaDeCanchas.size(); i++) {
                if (listaDeCanchas.get(i).getNombre().equals(nombreDeLaCancha)) {
                    cargarCanchaSeleccionada(i);
                }
            }

            drawerLayout.closeDrawers();
            return false;
        }
    }

    private class TouchListenerToolbar implements Toolbar.OnMenuItemClickListener {
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction().addToBackStack("Camilo");
            switch (item.getItemId()) {

                case R.id.opcionDibujar:
                    if (pizarra.isHabilitado()) {
                        pizarra.deshabilitarPizarra();
                        pizarra.borrarDibujo();
                        toolbar.getMenu().getItem(0).setIcon(R.drawable.pencilblack);

                    } else {
                        pizarra.habilitarPizarra();
                        toolbar.getMenu().getItem(0).setIcon(R.drawable.pencilwhite);
                    }

                    break;
                case R.id.opcionCompartir:
                    if (listaDeJugadoresFragments.size() == 0) {
                        Toast.makeText(MainActivity.this, "No se puede compartir una cancha vacia", Toast.LENGTH_SHORT).show();

                    } else {
                        opcionCompartirImagenFragment = new OpcionCompartirImagenFragment();
                        fragmentTransaction.add(R.id.frameLayoutContenedor_activitymain, opcionCompartirImagenFragment);
                        fragmentTransaction.commit();
                    }


                    break;

                case R.id.opcionGuardar:
                    if (listaDeJugadoresFragments.size() == 0) {
                        Toast.makeText(MainActivity.this, "No se puede guardar una cancha vacia", Toast.LENGTH_SHORT).show();
                    } else {
                        ingresarNombreDeLaCanchaFragment = new IngresarNombreDeLaCanchaFragment();
                        fragmentTransaction.add(R.id.frameLayoutContenedor_activitymain, ingresarNombreDeLaCanchaFragment).addToBackStack("Camilo");
                        fragmentTransaction.commitAllowingStateLoss();
                    }


                    break;
            }
            return true;
        }
    }

    private String escribirLaCanchaEnJson() {
        String canchaJson = "{ \"ListaDeJugadores\" : [";

        String jugadorJson = null;

        String nombreJugador = "\"nombre\" :";
        String tipoJugador = "\"tipoJugador\" :";
        String urlFoto = "\"urlFoto\" :";
        String x = "\"x\" :";
        String y = "\"y\" :";


        for (Integer i = 0; i < listaDeJugadoresFragments.size(); i++) {
            JugadorFragment jugadorFragment = listaDeJugadoresFragments.get(i);


            if (i == 0) {
                jugadorJson = "{" + nombreJugador + "\"" + jugadorFragment.getJugador().getNombre() + "\"" + "," +
                        tipoJugador + jugadorFragment.getJugador().getTipoJugador() + "," +
                        urlFoto + "\"" + jugadorFragment.getJugador().getUrlFoto().replace("https://firebasestorage.googleapis.com/v0/b/tu-cancha-c4289.appspot.com/o/Clubes%", "") + "\"" + "," +
                        x + jugadorFragment.getView().getX() + "," +
                        y + jugadorFragment.getView().getY() + "}";
            } else {
                canchaJson += ",";
                jugadorJson = "{" + nombreJugador + "\"" + jugadorFragment.getJugador().getNombre() + "\"" + "," +
                        tipoJugador + jugadorFragment.getJugador().getTipoJugador() + "," +
                        urlFoto + "\"" + jugadorFragment.getJugador().getUrlFoto().replace("https://firebasestorage.googleapis.com/v0/b/tu-cancha-c4289.appspot.com/o/Clubes%", "") + "\"" + "," +
                        x + jugadorFragment.getView().getX() + "," +
                        y + jugadorFragment.getView().getY() + "}";
            }


            canchaJson += jugadorJson;
        }
        canchaJson += "]}";

        return canchaJson;
    }

    private void guardarImagenEnElAlmacenamientoInterno(Bitmap bitmapImage) {
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        // Create imageDir
        File mypath = new File(directory, "cancha_" + UUID.randomUUID().toString() + ".jpg");

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void cargarCanchaSeleccionada(Integer iCancha) {
        Cancha cancha = listaDeCanchas.get(iCancha);

        for (Jugador jugador : cancha.getListaDeJugadores()) {
            agregarFragmentJugadorConImageStorageYUbicacion(jugador);
        }


    }

    private class ClickImageViewButtonCanchaGuardadas implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            drawerLayout.openDrawer(Gravity.START);
        }
    }


}





