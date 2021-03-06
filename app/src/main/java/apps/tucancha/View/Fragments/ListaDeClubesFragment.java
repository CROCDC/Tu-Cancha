package apps.tucancha.View.Fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import apps.tucancha.Controller.ControllerFirebase;
import apps.tucancha.R;
import apps.tucancha.Utils.ResultListener;
import apps.tucancha.View.Adapters.ListaDeClubesAdapter;

import java.util.List;

/**
 * Este fragment es el encargador de mostrar la lista de clubes que se descarga mediante el controller y ademas notificalre al MainActivity que debe agregar el fragment
 * Lista demjugadores del Club que se toco en su recycler view
 */
public class ListaDeClubesFragment extends Fragment {


    private ControllerFirebase controllerFirebase;
    private ListaDeClubesAdapter listaDeClubesAdapter;
    private RecyclerView recyclerViewListaDeClubes;
    private LinearLayoutManager linearLayoutManager;
    private List<String> listaDeEquipos;
    private ImageView imageViewButtonSearch;

    private NotificadorDesdeClubesHaciaSeleccionarJugadorActivity notificador;

    private ProgressBar progressBar;

    public ListaDeClubesAdapter getListaDeClubesAdapter() {
        return listaDeClubesAdapter;
    }

    public List<String> getListaDeEquipos() {
        return listaDeEquipos;
    }

    public ListaDeClubesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        notificador = (NotificadorDesdeClubesHaciaSeleccionarJugadorActivity) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);

        controllerFirebase = new ControllerFirebase();

        listaDeClubesAdapter = new ListaDeClubesAdapter(new ListaDeClubesAdapter.NotificadorHaciaImplementador() {
            @Override
            public void notificarTouchCeldaClub(String nombreDelClub) {
                notificador.notificarTouchCeldaClubCargarActivityListaDeJugadores(nombreDelClub);

            }
        });


        controllerFirebase.pedirListaDeClubes(new ResultListener<List<String>>() {
            @Override
            public void finish(List<String> resultado) {
                listaDeClubesAdapter.setListaDeEquipos(resultado);
                progressBar.setVisibility(View.GONE);
                listaDeEquipos = resultado;
                notificador.notificarCargaTerminada();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_lista_de_equipos, container, false);

        recyclerViewListaDeClubes = view.findViewById(R.id.recyclerViewListaDeClubes_fragmentlistadeequipos);
        progressBar = view.findViewById(R.id.progressBar_listadeclubesfragment);


        recyclerViewListaDeClubes.setLayoutManager(linearLayoutManager);

        recyclerViewListaDeClubes.setAdapter(listaDeClubesAdapter);

        return view;
    }

    /**
     * Esta es la integaz encarga de notificar al MainAcivity que debe cargar el fragment Lista de Jugadores
     */
    public interface NotificadorDesdeClubesHaciaSeleccionarJugadorActivity {
        public void notificarTouchCeldaClubCargarActivityListaDeJugadores(String nombreDelClub);
        void notificarCargaTerminada();
    }



}
