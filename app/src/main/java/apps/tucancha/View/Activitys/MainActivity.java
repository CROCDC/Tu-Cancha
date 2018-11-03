package apps.tucancha.View.Activitys;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.FileProvider;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.ayush.imagesteganographylibrary.Text.AsyncTaskCallback.TextDecodingCallback;
import com.ayush.imagesteganographylibrary.Text.AsyncTaskCallback.TextEncodingCallback;
import com.ayush.imagesteganographylibrary.Text.ImageSteganography;
import com.ayush.imagesteganographylibrary.Text.TextEncoding;
import com.google.zxing.qrcode.QRCodeWriter;


import net.glxn.qrgen.android.QRCode;

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
import apps.tucancha.Utils.Helper;
import apps.tucancha.Utils.ResultListener;
import apps.tucancha.View.Fragments.IngresarJugadorFragment;
import apps.tucancha.Model.Jugador;
import apps.tucancha.R;
import apps.tucancha.View.Fragments.IngresarNombreDeLaCanchaFragment;
import apps.tucancha.View.Fragments.JugadorFragment;

/**
 * Esta Activity se encarga de recibir las imagenes de cargar los fragments y de poner a los jugadores en la cancha y encargarse del drag and drop
 */

public class MainActivity extends AppCompatActivity implements IngresarJugadorFragment.NotificadorHaciaMainActivity, IngresarNombreDeLaCanchaFragment.NotificadorHaciaMainActivity, TextEncodingCallback, TextDecodingCallback {


    private IngresarJugadorFragment ingresarJugadorFragment;

    private ViewGroup rootLayout;

    private RelativeLayout.LayoutParams layoutParams;

    private String yaBuscoUnJugador = "";

    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private Pizarra pizarra;
    private RelativeLayout linearLayoutContenedorPizarra;
    private ImageView imageViewAdd;
    private Bitmap bitmapScreenshot;

    private final List<JugadorFragment> listaDeJugadoresFragments = new ArrayList<>();
    private final List<Jugador> listaDeJugadores = new ArrayList<>();
    private List<Cancha> listaDeCanchas = new ArrayList<>();


    public static final String CLAVE_JUGADOR = "jugador";
    public static final String CLAVE_CLUB = "club";
    public static final String CLAVE_JSON = "json";

    private String nombreDelClubRecibido;
    private Jugador jugadorRecibido;

    private ControllerFirebase controllerFirebase;

    private NavigationView navigationView;

    private IngresarNombreDeLaCanchaFragment ingresarNombreDeLaCanchaFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);

        drawerLayout = findViewById(R.id.drawerLayoutContenedor_activitymain);
        rootLayout = findViewById(R.id.relativeLayoutContenedor_activitymain);
        pizarra = findViewById(R.id.pizarra_activitymain);
        imageViewAdd = findViewById(R.id.imageViewButtonAdd_activitymain);
        linearLayoutContenedorPizarra = findViewById(R.id.linearLayoutContenedorSoloPizarra_activitymain);
        navigationView = findViewById(R.id.navigationView_activitymain);


        if (getIntent() != null) {
            Bundle bundle = getIntent().getExtras();
            if (bundle != null) {
                String json = bundle.getString(CLAVE_JSON);

                List<Jugador> jugadorList = Helper.parseJsonJugadores(json);

                for (Integer i = 0; i < jugadorList.size(); i++) {
                    agregarFragmentJugadorConImageStorageYUbicacion(jugadorList.get(i));
                }


            }
        }

        controllerFirebase = new ControllerFirebase();

        toolbar.inflateMenu(R.menu.menuprincipal);

        pedirLaListaDeCanchas();

        toolbar.setOnMenuItemClickListener(new TouchListenerToolbar());
        navigationView.setNavigationItemSelectedListener(new TouchListenerNavigationView());


        touchImageViewButtonAdd();


    }


    public void pedirLaListaDeCanchas() {
        controllerFirebase.pedirListaDeCanchas(new ResultListener<List<Cancha>>() {
            @Override
            public void finish(List<Cancha> resultado) {
                listaDeCanchas = resultado;

                agregrarCanchasGuardadaslNavigationView(resultado);
            }


        });
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

    public void agregarFragmentJugadorConFoto(final Jugador jugador) {

        final JugadorFragment jugadorFragment = JugadorFragment.frabricaDeFragmentsJugador(jugador);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.linearLayoutContenedorSoloPizarra_activitymain, jugadorFragment).addToBackStack("Camilo");


        fragmentTransaction.remove(ingresarJugadorFragment);


        listaDeJugadoresFragments.add(jugadorFragment);
        listaDeJugadores.add(jugador);

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
                        listaDeJugadores.remove(jugador);
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
        listaDeJugadores.add(jugador);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                //HAGO ESTO PARA QUE LE DE TIEMPO A CREAR LA VISTA
                jugadorFragment.getView().setOnTouchListener(new SistemaDragAndDrop(jugadorFragment, new ResultListener<JugadorFragment>() {
                    @Override
                    public void finish(JugadorFragment jugadorFragmentAEliminar) {
                        listaDeJugadoresFragments.remove(jugadorFragmentAEliminar);
                        listaDeJugadores.remove(jugador);
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
        listaDeJugadores.add(jugador);

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
                        listaDeJugadores.remove(jugador);
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


    @SuppressLint("NewApi")
    private void agregrarCanchasGuardadaslNavigationView(List<Cancha> listaDeCanchas) {
        Menu menu = navigationView.getMenu();


        menu.clear();

        menu.add("Canchas Guardadas");


        for (Cancha cancha : listaDeCanchas) {
            menu.add(cancha.getNombre());
        }

        navigationView.invalidate();
    }

    @Override
    public void notificarGuardarCancha(String nombreDeLaCancha) {
        getSupportFragmentManager().beginTransaction().remove(ingresarNombreDeLaCanchaFragment).commit();


        for (Integer i = 0; i < listaDeJugadores.size(); i++) {

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
    public void onStartTextEncoding() {

    }

    @Override
    public void onCompleteTextEncoding(ImageSteganography result) {


    }


    private class TouchListenerNavigationView implements NavigationView.OnNavigationItemSelectedListener {
        @SuppressLint("NewApi")
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            for (Integer i = 0; i < listaDeCanchas.size(); i++) {
                if (listaDeCanchas.get(i).getNombre().equals(item.getTitle())) {
                    cargarCanchaSeleccionada(i);
                }
            }

            drawerLayout.closeDrawers();
            return false;
        }
    }

    private class TouchListenerToolbar implements Toolbar.OnMenuItemClickListener {
        @SuppressLint("NewApi")
        @Override
        public boolean onMenuItemClick(MenuItem item) {
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
                    bitmapScreenshot = Screnshot.hacerScreenshotFormatoBitmap(linearLayoutContenedorPizarra);
                    guardarImagenEnElAlmacenamientoInterno(bitmapScreenshot);
                    bitMap1BottomBitmap2(bitmapScreenshot, crearQR(escribirLaCanchaEnJson()));
                    compartirCancha(bitMap1BottomBitmap2(bitmapScreenshot, crearQR(escribirLaCanchaEnJson())));

                    break;

                case R.id.opcionGuardar:
                    ingresarNombreDeLaCanchaFragment = new IngresarNombreDeLaCanchaFragment();

                    FragmentManager fragmentManager = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.add(R.id.frameLayoutContenedor_activitymain, ingresarNombreDeLaCanchaFragment);
                    fragmentTransaction.commitAllowingStateLoss();


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
                        urlFoto + "\"" + jugadorFragment.getJugador().getUrlFoto() + "\"" + "," +
                        x + jugadorFragment.getView().getX() + "," +
                        y + jugadorFragment.getView().getY() + "}";
            } else {
                canchaJson += ",";
                jugadorJson = "{" + nombreJugador + "\"" + jugadorFragment.getJugador().getNombre() + "\"" + "," +
                        tipoJugador + jugadorFragment.getJugador().getTipoJugador() + "," +
                        urlFoto + "\"" + jugadorFragment.getJugador().getUrlFoto() + "\"" + "," +
                        x + jugadorFragment.getView().getX() + "," +
                        y + jugadorFragment.getView().getY() + "}";
            }


            canchaJson += jugadorJson;
        }
        canchaJson += "]}";

        return canchaJson;
    }

    private void escribirDatosEnLaImagen(Bitmap bitmap) {

        String canchaJson = escribirLaCanchaEnJson();

        ImageSteganography imageSteganography = new ImageSteganography(canchaJson,
                "110599",
                bitmap);

        TextEncoding textEncoding = new TextEncoding(MainActivity.this,
                MainActivity.this);

        textEncoding.execute(imageSteganography);

    }

    private void convertirBitmapEnPDF(Bitmap bitmap) {
        PdfDocument document = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(bitmap.getWidth(), bitmap.getHeight(), 1).create();
        PdfDocument.Page page = document.startPage(pageInfo);


        Canvas canvas = page.getCanvas();


        Paint paint = new Paint();

        paint.setColor(Color.parseColor("#ffffff"));

        canvas.drawPaint(paint);


        bitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth(), bitmap.getHeight(), true);

        paint.setColor(Color.BLUE);

        canvas.drawBitmap(bitmap, 0, 0, null);

        document.finishPage(page);


        String targetPdf = this.getExternalCacheDir() + "/cancha.pdf";
        File filePath = new File(targetPdf);

        try {
            document.writeTo(new FileOutputStream(filePath));

        } catch (IOException e) {

        }

        document.close();

        Uri uriFromFile = FileProvider.getUriForFile(getBaseContext(), "apps.tucancha.Utils.GenericFileProvider", filePath);

        Intent share = new Intent();
        share.setAction(Intent.ACTION_SEND);
        share.setType("application/pdf");
        share.putExtra(Intent.EXTRA_STREAM, uriFromFile);
        share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        share.setPackage("com.whatsapp");

        this.startActivity(share);

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

    private Bitmap crearQR(String json) {
        return QRCode.from(json).withSize(800, 800).bitmap();

    }

    private Bitmap bitMap1BottomBitmap2(Bitmap bmp1, Bitmap bmp2) {
        Bitmap bmOverlay = Bitmap.createBitmap(bmp1.getWidth(), bmp1.getHeight() + bmp2.getHeight(), bmp1.getConfig());
        Canvas canvas = new Canvas(bmOverlay);
        canvas.drawBitmap(bmp1, new Matrix(), null);
        canvas.drawBitmap(bmp2, bmp1.getWidth() / 2 - bmp2.getWidth() / 2, bmp1.getHeight(), null);
        return bmOverlay;
    }

    @SuppressLint("NewApi")
    public void cargarCanchaSeleccionada(Integer iCancha) {
        Cancha cancha = listaDeCanchas.get(iCancha);

        for (Jugador jugador : cancha.getListaDeJugadores()) {
            agregarFragmentJugadorConImageStorageYUbicacion(jugador);
        }


    }

}





