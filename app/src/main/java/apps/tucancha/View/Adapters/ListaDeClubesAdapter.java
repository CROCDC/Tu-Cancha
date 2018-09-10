package apps.tucancha.View.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import apps.tucancha.R;

public class ListaDeClubesAdapter extends RecyclerView.Adapter {

    private List<String> listaDeEquipos;
    private Context context;
    private NotificadorHaciaImplementador notificador;

    public ListaDeClubesAdapter(NotificadorHaciaImplementador notificador) {
        listaDeEquipos = new ArrayList<>();
        this.notificador = notificador;
    }


    public void setListaDeEquipos(List<String> listaDeEquipos) {
        this.listaDeEquipos = listaDeEquipos;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        context = viewGroup.getContext();

        LayoutInflater layoutInflater = LayoutInflater.from(context);

        View view = layoutInflater.inflate(R.layout.celda_club, viewGroup, false);

        ClubViewHolder clubViewHolder = new ClubViewHolder(view);

        return clubViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        String club = listaDeEquipos.get(i);

        ClubViewHolder clubViewHolder = (ClubViewHolder) viewHolder;

        clubViewHolder.cargarClub(club);
    }

    @Override
    public int getItemCount() {
        return listaDeEquipos.size();
    }


    public class ClubViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageViewEscudoDelClub;
        private TextView textViewNombreDelClub;
        private FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        private StorageReference reference = firebaseStorage.getReference();

        public ClubViewHolder(@NonNull View itemView) {
            super(itemView);

            imageViewEscudoDelClub = itemView.findViewById(R.id.imageViewEscudoDelClub_celdaclub);
            textViewNombreDelClub = itemView.findViewById(R.id.textViewNombreDelClub_celdaclub);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    notificador.notificarTouchCeldaClub(listaDeEquipos.get(getAdapterPosition()));
                }
            });
        }

        public void cargarClub(String nombreDelClub) {
            textViewNombreDelClub.setText(nombreDelClub);

            reference = reference.child("Clubes").child(nombreDelClub).child("escudo.png");
            Glide.with(context)
                    .using(new FirebaseImageLoader())
                    .load(reference)
                    .into(imageViewEscudoDelClub);
        }
    }

    public interface NotificadorHaciaImplementador {
        public void notificarTouchCeldaClub(String nombreDelClub);
    }
}
