package apps.tucancha.View.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import apps.tucancha.Model.Jugador;
import apps.tucancha.R;
import com.bumptech.glide.Glide;
import de.hdodenhof.circleimageview.CircleImageView;


/**
 * Este es el fragment que se agrega a la cancha y esta encargado de carga el nombre del jugador y la imagen
 */
public class JugadorFragment extends Fragment {

    public static final String CLAVE_JUGADOR = "jugador";

    private Jugador jugador;

    public Jugador getJugador() {
        return jugador;
    }

    private CircleImageView circleImageViewJugador;
    private TextView textViewNombreDelJugador;

    /**
     * esta es la fabrica de fragment que se encarga de recibir un jugador meterlo en nu bundle y setearlo como argument para que en el oncreate view se puede
     * recibirlos datos del jugador
     * @param jugador
     * @return
     */
    public static JugadorFragment frabricaDeFragmentsJugador(Jugador jugador) {
        JugadorFragment jugadorFragment = new JugadorFragment();

        Bundle bundle = new Bundle();

        bundle.putSerializable(CLAVE_JUGADOR, jugador);

        jugadorFragment.setArguments(bundle);

        return jugadorFragment;

    }

    public JugadorFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_jugador, container, false);



        Bundle bundle = getArguments();

        jugador = (Jugador) bundle.getSerializable(CLAVE_JUGADOR);

        circleImageViewJugador = view.findViewById(R.id.circleImageViewJugador_fragmentjugador);
        textViewNombreDelJugador = view.findViewById(R.id.textViewNombreDelJugador_fragmentjugador);

        switch (jugador.getTipoJugador()){
            case 1:
                Glide.with(getContext())
                        .load(R.drawable.player)
                        .into(circleImageViewJugador);
            break;
            case 2:
                Glide.with(getContext())
                        .load(jugador.getFoto())
                        .into(circleImageViewJugador);
            break;
            case 3:
                Glide.with(getContext())
                        .load(jugador.getUrlFoto())
                        .into(circleImageViewJugador);
            break;
        }




        textViewNombreDelJugador.setText(jugador.getNombre());

        return view;
    }

}
