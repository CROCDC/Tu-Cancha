package apps.tucancha.View.Activitys;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import apps.tucancha.Model.Jugador;
import apps.tucancha.R;
import apps.tucancha.Utils.Helper;
import apps.tucancha.View.Fragments.ListaDeJugadoresFragment;

public class ListaDeJugadoresActivity extends AppCompatActivity implements ListaDeJugadoresFragment.NotificadorDesdeJugadoresHaciaSeleccionarJugadorActivity {

    public static final String CLAVE_CLUB ="club";


    private String nombreDelClub;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_de_jugadores);

        Intent intent = getIntent();

        Bundle bundle = intent.getExtras();

        nombreDelClub = bundle.getString(CLAVE_CLUB);


        ListaDeJugadoresFragment listaDeJugadoresFragment = new ListaDeJugadoresFragment();

        Bundle bundleFragment = new Bundle();

        bundleFragment.putString(ListaDeJugadoresFragment.CLAVE_CLUB,nombreDelClub);


        listaDeJugadoresFragment.setArguments(bundleFragment);

        cargarFragment(listaDeJugadoresFragment);
    }
    public void cargarFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.linearLayoutContenedor_activitylistadejugadores, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public void notificarTouchCeldaJugadorNotificarActualizarImagen(Jugador jugador, String nombreDelClub) {
        Intent intent = new Intent(ListaDeJugadoresActivity.this, MainActivity.class);

        Bundle bundle = new Bundle();

        bundle.putSerializable(ListaDeClubesActivity.CLAVE_JUGADOR, jugador);
        bundle.putString(ListaDeJugadoresActivity.CLAVE_CLUB, nombreDelClub);

        intent.putExtras(bundle);

        setResult(Helper.RESULT_OK, intent);

        finish();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ListaDeJugadoresActivity.this, ListaDeClubesActivity.class);
        startActivityForResult(intent,10000);
        finish();
    }
}
