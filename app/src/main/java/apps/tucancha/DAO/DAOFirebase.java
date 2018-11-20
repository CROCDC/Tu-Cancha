package apps.tucancha.DAO;

import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import apps.tucancha.Model.Cancha;
import apps.tucancha.Model.Jugador;
import apps.tucancha.Utils.ResultListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.*;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

/**
 * Clase encargada de recibir los pedidos del controller y hacerselos a firebase utiliza database y storage
 */
public class DAOFirebase {

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseStorage firebaseStorage;
    private StorageReference reference;
    String numeroDeSerie;


    public DAOFirebase() {
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        reference = firebaseStorage.getReference();
        numeroDeSerie = Build.SERIAL;
    }

    /**
     * Metodo encargado de pedirle la lista de clubes a database
     */

    public void pedirListaDeClubes(final ResultListener<List<String>> escuchadorDelControlador) {
        databaseReference = firebaseDatabase.getReference();


        databaseReference.child("Clubes").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<String> listaDeClubes = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String club = (String) snapshot.getValue();
                    listaDeClubes.add(club);

                }
                escuchadorDelControlador.finish(listaDeClubes);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

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
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<String> listaDeJugadores = new ArrayList<>();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String club = (String) snapshot.getValue();


                    listaDeJugadores.add(club);
                }
                escuchadorDelControlador.finish(listaDeJugadores);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

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

    public void guardarLaCancha(Cancha cancha, final ResultListener<Boolean> escuchadorDelControlador){

        databaseReference = firebaseDatabase.getReference();

        databaseReference.child("CanchasGuardadas").child(numeroDeSerie).push().setValue(cancha);

        escuchadorDelControlador.finish(true);



    }

    public void borrarCancha(Cancha cancha ,final  ResultListener<Boolean> escuchadorDelControlador){
        databaseReference = firebaseDatabase.getReference();
        databaseReference.child("CanchasGuardadas").child(numeroDeSerie).child(cancha.getPosicion()).setValue(null);
        escuchadorDelControlador.finish(true);

    }

    public void pedirListaDeCanchasGuardadas(final  ResultListener<List<Cancha>> escuchadorDelControlador){
        databaseReference = firebaseDatabase.getReference();

        databaseReference.child("CanchasGuardadas").child(numeroDeSerie).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Cancha> listaDeCanchas = new ArrayList<>();
                Integer i =0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    listaDeCanchas.add(snapshot.getValue(Cancha.class));
                    listaDeCanchas.get(i).setPosicion(snapshot.getKey());
                    i+=1;
                }

                escuchadorDelControlador.finish(listaDeCanchas);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}