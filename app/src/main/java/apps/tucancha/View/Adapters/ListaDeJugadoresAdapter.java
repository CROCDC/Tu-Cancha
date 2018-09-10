package apps.tucancha.View.Adapters;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import apps.tucancha.Model.Jugador;
import apps.tucancha.R;

public class ListaDeJugadoresAdapter extends RecyclerView.Adapter {
    private List<Jugador> listaDeJugadores;
    private Context context;
    private String nombreDelClub;

    private NotificadorHaciaImplementador notificador;

    public ListaDeJugadoresAdapter(String nombreDelClub, NotificadorHaciaImplementador notificador) {
        listaDeJugadores = new ArrayList<>();
        this.nombreDelClub = nombreDelClub;

        this.notificador = notificador;
    }

    public void setListaDeJugadores(List<Jugador> listaDeJugadores) {
        this.listaDeJugadores = listaDeJugadores;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        context = viewGroup.getContext();

        LayoutInflater layoutInflater = LayoutInflater.from(context);

        View view = layoutInflater.inflate(R.layout.celda_jugador, viewGroup, false);


        JugadorViewHolder jugadorViewHolder = new JugadorViewHolder(view);

        return jugadorViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        Jugador jugador = listaDeJugadores.get(i);

        JugadorViewHolder jugadorViewHolder = (JugadorViewHolder) viewHolder;

        jugadorViewHolder.cargarJugador(jugador);
    }

    @Override
    public int getItemCount() {
        return listaDeJugadores.size();
    }

    public class JugadorViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageViewJugador;
        private TextView textViewNombreDelJugador;
        private FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        private StorageReference reference = firebaseStorage.getReference();

        public JugadorViewHolder(@NonNull View itemView) {
            super(itemView);

            imageViewJugador = itemView.findViewById(R.id.imageViewJugador_celdajugador);
            textViewNombreDelJugador = itemView.findViewById(R.id.textViewNombreDelJugador_celdajugador);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    notificador.notificarTouchCeldaJugador(listaDeJugadores.get(getAdapterPosition()), nombreDelClub);
                }
            });

        }

        public void cargarJugador(Jugador jugador) {
            textViewNombreDelJugador.setText(jugador.getNombre());

            Glide.with(context)
                    .load(jugador.getUrlFoto())
                    .into(imageViewJugador);

        }
    }

    public interface NotificadorHaciaImplementador {
        public void notificarTouchCeldaJugador(Jugador jugador, String nombreDelClub);
    }
}
