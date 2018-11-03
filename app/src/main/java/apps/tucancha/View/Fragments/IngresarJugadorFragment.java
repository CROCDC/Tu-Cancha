package apps.tucancha.View.Fragments;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;

import java.io.File;

import apps.tucancha.Model.Jugador;
import apps.tucancha.R;
import de.hdodenhof.circleimageview.CircleImageView;
import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;


/**
 * Este Fragment es el encargado de recibir el nombre del jugador y mostrarle la galeria de su celular y notificarle a la activity que tiene que cagar el fragment ListaDeClubes
 * ademas es el que le notifica a la activity que hay que agregar un jugador y le pasa los datos
 */
public class IngresarJugadorFragment extends Fragment {


    private FrameLayout frameLayoutContenedor;
    private LinearLayout linearLayoutContenedorOpciones;
    private CardView cardViewContenedor;
    private ImageView imageViewButtonDescargarFoto;
    private ImageView imageViewButtonAddFoto;
    private EditText editTextCampoUsuario;
    private ImageView imageViewButtonCheck;
    private CircleImageView circleImageViewPreview;
    private NotificadorHaciaMainActivity notificador;

    private Integer tipoDeFoto;
    private String nombre;
    private String fotoUrl;
    private File foto;

    public IngresarJugadorFragment() {
        // Required empty public constructor
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        notificador = (NotificadorHaciaMainActivity) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_ingresar_jugador, container, false);


        frameLayoutContenedor = view.findViewById(R.id.frameLayoutContenedor_fragmentingresarjugador);
        linearLayoutContenedorOpciones = view.findViewById(R.id.linearLayoutOpciones_fragmentingresarjugador);
        cardViewContenedor = view.findViewById(R.id.cardViewContenedorOpcionesDeIngreso_fragmentingresarjugador);
        imageViewButtonDescargarFoto = view.findViewById(R.id.imageViewButtonDescargarFoto_fragmentingresarjugador);
        imageViewButtonAddFoto = view.findViewById(R.id.imageViewButtonSubirFoto_fragmentingresarjugador);
        editTextCampoUsuario = view.findViewById(R.id.editTextCampoNombreDelJugador_fragmentingresarjugador);
        imageViewButtonCheck = view.findViewById(R.id.imageViewButtonCheck_fragmentingresarjugador);
        circleImageViewPreview = view.findViewById(R.id.circleImageViewPreview_fragmentingresarjugador);

        touchImageViewButtonDescargarImagen();
        touchImageViewButtonAddFoto();
        touchImageViewButtonCheck();
        touchFrameLayoutContenedor();
        touchCardViewContenedor();


        return view;
    }

    /**
     * Este metodo se encarga de que si tocan el addPhoto abrir la galeria del usuario para que pueda seleccionar una imagen
     */
    public void touchImageViewButtonAddFoto() {
        imageViewButtonAddFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EasyImage.openGallery(IngresarJugadorFragment.this, 666);


            }
        });
    }

    /**
     * Este metodo se encarga de que si tocan el descargarFoto le notifique a la activity que hay que cargar el fragment Lista de clubes
     */
    public void touchImageViewButtonDescargarImagen() {
        imageViewButtonDescargarFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notificador.notificarCargarListaDeClubes();
            }
        });
    }

    /**
     * este Metodo se encarga de parsarle los datos de un jugador a la acitivty para que lo agregue a la cancha
     */

    public void touchImageViewButtonCheck() {
        imageViewButtonCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nombre = editTextCampoUsuario.getText().toString();


                if (foto == null && fotoUrl == null){
                    tipoDeFoto = 1;
                }

                switch (tipoDeFoto) {
                    case 1:
                        notificador.notificarAgregarJugadorSinFoto(nombre);
                        break;
                    case 2:
                        notificador.notificarAgregarJugadorPorFile(foto, editTextCampoUsuario.getText().toString());
                        break;
                    case 3:
                        notificador.notificarAgregarJugadorConImagenStorage(fotoUrl, nombre);
                }
            }
        });
    }

    public void touchFrameLayoutContenedor(){
        frameLayoutContenedor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
    }

    public void touchCardViewContenedor(){
        cardViewContenedor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    /**
     * Este metodo es utilizado en el MainActivity para notificarle al fragment que tiene que refrescar la imagen del jugador ya que se eligio una desde
     * el fragment ListaDeJugadores
     * @param jugador
     * @param nombreDelClub
     */
    public void cargarImagenDelJugador(Jugador jugador, String nombreDelClub) {
        linearLayoutContenedorOpciones.setVisibility(View.GONE);
        circleImageViewPreview.setVisibility(View.VISIBLE);

        editTextCampoUsuario.setText(jugador.getNombre());

        tipoDeFoto = 3;

        Glide.with(getContext())
                .load(jugador.getUrlFoto())
                .into(circleImageViewPreview);

        fotoUrl = jugador.getUrlFoto();


    }

    /**
     * el onActivityResult esta overrideadeo para recibir la imagen de la galeria del usuario
     * @param requestCode
     * @param resultCode
     * @param data
     */

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        EasyImage.handleActivityResult(requestCode, resultCode, data, getActivity(), new DefaultCallback() {
            @Override
            public void onImagePickerError(Exception e, EasyImage.ImageSource source, int type) {
            }

            @Override
            public void onImagePicked(File imageFile, EasyImage.ImageSource source, int type) {
                linearLayoutContenedorOpciones.setVisibility(View.GONE);
                circleImageViewPreview.setVisibility(View.VISIBLE);
                foto = imageFile;
                Glide.with(getContext())
                        .load(foto)
                        .into(circleImageViewPreview);

                tipoDeFoto = 2;
            }


        });
    }


    /**
     * Esta interfaz es implementada por el Main Activity y se utiliza para notificarle los distintos sucesos que ocurran en este fragment
     */

    public interface NotificadorHaciaMainActivity {
        public void notificarAgregarJugadorSinFoto(String nombre);

        public void notificarAgregarJugadorPorFile(File file, String nombreDelJugador);

        public void notificarAgregarJugadorConImagenStorage(String url, String nombreDelJugador);

        public void notificarCargarListaDeClubes();

    }
}
