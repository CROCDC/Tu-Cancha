package apps.tucancha.View.Fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import java.util.List;
import java.util.concurrent.ExecutionException;

import apps.tucancha.Controller.ControllerFirebase;
import apps.tucancha.DAO.DAOScraping;
import apps.tucancha.Model.Jugador;
import apps.tucancha.R;
import apps.tucancha.Utils.ResultListener;
import apps.tucancha.View.Adapters.ListaDeJugadoresAdapter;

/**
 * Este fragment es el encargador de pedirle al controller que le de la lista de jugadores y de notificarle al MainActivity cual jugador fue tocado
 */
public class ListaDeJugadoresFragment extends Fragment {

    private RecyclerView recyclerView;
    private ControllerFirebase controllerFirebase;
    private ListaDeJugadoresAdapter listaDeJugadoresAdapter;
    private GridLayoutManager gridLayoutManager;
    private ProgressBar progressBar;


    private List<Jugador> listaDeJugadores;

    private NotificadorDesdeJugadoresHaciaSeleccionarJugadorActivity notificador;
    private String nombreDelClub;
    public static final String CLAVE_CLUB = "CLUB";


    public ListaDeJugadoresAdapter getListaDeJugadoresAdapter() {
        return listaDeJugadoresAdapter;
    }

    public List<Jugador> getListaDeJugadores() {
        return listaDeJugadores;
    }

    public ListaDeJugadoresFragment() {
        // Required empty public constructor
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        notificador = (NotificadorDesdeJugadoresHaciaSeleccionarJugadorActivity) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        Bundle bundle = getArguments();


        nombreDelClub = bundle.getString(CLAVE_CLUB);

        controllerFirebase = new ControllerFirebase();
        gridLayoutManager = new GridLayoutManager(getContext(), 2, LinearLayoutManager.VERTICAL, false);
        listaDeJugadoresAdapter = new ListaDeJugadoresAdapter(nombreDelClub, new ListaDeJugadoresAdapter.NotificadorHaciaImplementador() {
            @Override
            public void notificarTouchCeldaJugador(Jugador jugador, String nombreDelClub) {
                notificador.notificarTouchCeldaJugadorNotificarActualizarImagen(jugador, nombreDelClub);
            }
        });

        try {
            new DAOScraping(new ResultListener<List<Jugador>>() {
                @Override
                public void finish(List<Jugador> resultado) {
                    listaDeJugadoresAdapter.setListaDeJugadores(resultado);
                    notificador.notificarCargaTerminada();
                    progressBar.setVisibility(View.GONE);

                }
            }).execute(nombreDelClub);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_lista_de_jugadores, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewListaDeJugadores_fragmentlistadejugadores);
        progressBar = view.findViewById(R.id.progresBar_listadejugadoresfragment);

        recyclerView.setAdapter(listaDeJugadoresAdapter);

        recyclerView.setLayoutManager(gridLayoutManager);


        return view;
    }

    /**
     * Esta Interfaz se encarga de notificarle al MainActivity que Jugador fue tocado
     */
    public interface NotificadorDesdeJugadoresHaciaSeleccionarJugadorActivity {
        public void notificarTouchCeldaJugadorNotificarActualizarImagen(Jugador jugador, String nombreDelClub);

        public void notificarCargaTerminada();
    }


}
