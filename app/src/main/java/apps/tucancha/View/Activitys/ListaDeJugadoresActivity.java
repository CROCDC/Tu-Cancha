package apps.tucancha.View.Activitys;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import apps.tucancha.Model.Jugador;
import apps.tucancha.R;
import apps.tucancha.Utils.Helper;
import apps.tucancha.View.Fragments.ListaDeJugadoresFragment;

public class ListaDeJugadoresActivity extends AppCompatActivity implements ListaDeJugadoresFragment.NotificadorDesdeJugadoresHaciaSeleccionarJugadorActivity {

    public static final String CLAVE_CLUB ="club";

    private ListaDeJugadoresFragment listaDeJugadoresFragment;

    private Toolbar toolbar;

    private String nombreDelClub;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_de_jugadores);

        toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();

        Bundle bundle = intent.getExtras();

        nombreDelClub = bundle.getString(CLAVE_CLUB);


        listaDeJugadoresFragment = new ListaDeJugadoresFragment();

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

        bundle.putSerializable(MainActivity.CLAVE_JUGADOR, jugador);

        intent.putExtras(bundle);

        setResult(Helper.RESULT_OK, intent);

        getSupportFragmentManager().beginTransaction().remove(listaDeJugadoresFragment).commit();

        finish();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ListaDeJugadoresActivity.this, MainActivity.class);

        setResult(Helper.RESULT_ONBACKPRESS,intent);

        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case 16908332:
                onBackPressed();
                break;

        }
        return super.onOptionsItemSelected(item);

    }
}
