package apps.tucancha.View.Activitys;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import apps.tucancha.Model.Jugador;
import apps.tucancha.R;
import apps.tucancha.Utils.Helper;
import apps.tucancha.View.Fragments.ListaDeClubesFragment;

import java.util.ArrayList;
import java.util.List;

public class ListaDeClubesActivity extends AppCompatActivity implements ListaDeClubesFragment.NotificadorDesdeClubesHaciaSeleccionarJugadorActivity {

    public static final String CLAVE_CLUB = "club";
    public static final String CLAVE_JUGADOR = "jugador";


    private ListaDeClubesFragment listaDeClubesFragment;
    private List<String> listaDeEquipos;
    private String club;
    private EditText editTextFiltro;
    private Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seleccionar_jugador);

        toolbar = findViewById(R.id.toolbarfilter);

        editTextFiltro = toolbar.findViewById(R.id.editTextFiltro_toolbarfiltro);


        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);




        listaDeClubesFragment = new ListaDeClubesFragment();
        cargarFragment(listaDeClubesFragment);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);


    }

    public void cargarFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.linearLayoutContenedor_activityseleccionarjugador, fragment);
        fragmentTransaction.commit();
    }


    @Override
    public void notificarTouchCeldaClubCargarActivityListaDeJugadores(String nombreDelClub) {
        Intent intent = new Intent(ListaDeClubesActivity.this, MainActivity.class);

        Bundle bundle = new Bundle();

        bundle.putString(ListaDeJugadoresActivity.CLAVE_CLUB, nombreDelClub);

        intent.putExtras(bundle);

        setResult(Helper.RESULT_OK, intent);

        finish();


    }

    @Override
    public void notificarCargaTerminada() {
        filtradoEditText();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (resultCode == Helper.RESULT_OK) {

            Jugador jugador = (Jugador) data.getExtras().getSerializable(CLAVE_JUGADOR);
            String nombreClub = data.getExtras().getString(CLAVE_CLUB);

            Bundle bundle = new Bundle();

            bundle.putSerializable(MainActivity.CLAVE_JUGADOR, jugador);
            bundle.putString(MainActivity.CLAVE_CLUB, nombreClub);


            Intent intent = new Intent(ListaDeClubesActivity.this, MainActivity.class);

            intent.putExtras(bundle);

            setResult(Helper.RESULT_OK, intent);

            finish();


        }

        super.onActivityResult(requestCode, resultCode, data);


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

    private void filtradoEditText() {
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
        List<String> listaFiltrada = new ArrayList<>();

        for (String equipo : listaDeClubesFragment.getListaDeEquipos()) {
            if (equipo.toLowerCase().contains(text.toLowerCase())) {
                listaFiltrada.add(equipo);
            }
        }

        listaDeClubesFragment.getListaDeClubesAdapter().filtrarListaDeEquipos(listaFiltrada);
    }
}
