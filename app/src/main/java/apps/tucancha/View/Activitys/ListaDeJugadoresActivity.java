package apps.tucancha.View.Activitys;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import apps.tucancha.Model.Jugador;
import apps.tucancha.R;
import apps.tucancha.Utils.Helper;
import apps.tucancha.View.Fragments.ListaDeJugadoresFragment;

public class ListaDeJugadoresActivity extends AppCompatActivity implements ListaDeJugadoresFragment.NotificadorDesdeJugadoresHaciaSeleccionarJugadorActivity {

    public static final String CLAVE_CLUB ="club";

    private ListaDeJugadoresFragment listaDeJugadoresFragment;

    private Toolbar toolbar;
    private EditText editTextFiltro;
    private ImageView imageViewButtonSearch;

    private String nombreDelClub;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_de_jugadores);

        toolbar = findViewById(R.id.toolbarfilter);
        editTextFiltro = toolbar.findViewById(R.id.editTextFiltro_toolbarfiltro);
        imageViewButtonSearch = toolbar.findViewById(R.id.imageViewButtonSearch_toolbarfiltro);

        listaDeJugadoresFragment = new ListaDeJugadoresFragment();

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();

        Bundle bundle = intent.getExtras();

        nombreDelClub = bundle.getString(CLAVE_CLUB);




        Bundle bundleFragment = new Bundle();

        bundleFragment.putString(ListaDeJugadoresFragment.CLAVE_CLUB,nombreDelClub);


        listaDeJugadoresFragment.setArguments(bundleFragment);

        cargarFragment(listaDeJugadoresFragment);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);




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
    public void notificarCargaTerminada() {
        filtradoEditText();
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


    private void filtradoEditText(){
        editTextFiltro.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                filtrarJugadores(s.toString());

            }
        });
    }

    private void filtrarJugadores(String text) {
        List<Jugador> listaFiltrada = new ArrayList<>();

        for (Jugador jugador : listaDeJugadoresFragment.getListaDeJugadores()) {
            if (jugador.getNombre().toLowerCase().contains(text.toLowerCase())) {
                listaFiltrada.add(jugador);
            }
        }

        listaDeJugadoresFragment.getListaDeJugadoresAdapter().filtrarListaDeJugadores(listaFiltrada);
    }
}
