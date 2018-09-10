package apps.tucancha.DAO;

import android.net.Uri;
import android.support.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import apps.tucancha.Model.Jugador;
import apps.tucancha.Utils.ResultListener;

/**
 * Clase encargada de recibir los pedidos del controller y hacerselos a firebase utiliza database y storage
 */
public class DAOFirebase {

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseStorage firebaseStorage;
    private StorageReference reference;



    public DAOFirebase() {
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        reference = firebaseStorage.getReference();
    }

    /**
     * Metodo encargado de pedirle la lista de clubes a database
     */

    public void pedirListaDeClubes(final ResultListener<List<String>> escuchadorDelControlador) {
        databaseReference = firebaseDatabase.getReference();


        databaseReference.child("Clubes").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<String> listaDeClubes = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String club = (String) snapshot.getValue();
                    listaDeClubes.add(club);

                }
                escuchadorDelControlador.finish(listaDeClubes);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    /**
     * Este metodo se encarga de pedir los nombres de los jugadores para luego pedir las imagenes
     * @param nombreDelClub
     * @param escuchadorDelControlador
     */

    public void pedirListaDeNombreJugadoresPorClub(String nombreDelClub, final ResultListener<List<String>> escuchadorDelControlador) {
        databaseReference = firebaseDatabase.getReference();

        databaseReference.child("Jugadores").child(nombreDelClub).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<String> listaDeJugadores = new ArrayList<>();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String club = (String) snapshot.getValue();


                    listaDeJugadores.add(club);
                }
                escuchadorDelControlador.finish(listaDeJugadores);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    /**
     * este metodo se encarga de buscar las imagenes y los nombres de los jugadores tanto en storage como en firebase
     * @param nombreDelClub
     * @param escuchadorDelControlador
     */

    public void pedirListaDeJugadoresPorClub(final String nombreDelClub, final ResultListener<List<Jugador>> escuchadorDelControlador) {
        reference = firebaseStorage.getReference();
        final List<Jugador> listaDeJugadores = new ArrayList<>();

        new DAOFirebase().pedirListaDeNombreJugadoresPorClub(nombreDelClub, new ResultListener<List<String>>() {
            @Override
            public void finish(final List<String> resultado) {
                String nombreDelJugador;
                for (Integer i = 0; i < resultado.size(); i++) {
                    nombreDelJugador = resultado.get(i);
                    final String finalNombreDelJugador = nombreDelJugador;
                    reference.child("Clubes").child(nombreDelClub).child(nombreDelJugador + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            listaDeJugadores.add(new Jugador(finalNombreDelJugador, uri.toString(), 3));

                            if (listaDeJugadores.size() == resultado.size()){
                                escuchadorDelControlador.finish(listaDeJugadores);
                            }
                        }
                    });
                }

            }


        });

    }
}