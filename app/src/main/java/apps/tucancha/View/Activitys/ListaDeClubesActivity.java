package apps.tucancha.View.Activitys;

import android.content.Intent;
import android.support.annotation.Nullable;
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
import apps.tucancha.View.Fragments.ListaDeClubesFragment;
import apps.tucancha.View.Fragments.ListaDeJugadoresFragment;

public class ListaDeClubesActivity extends AppCompatActivity implements ListaDeClubesFragment.NotificadorDesdeClubesHaciaSeleccionarJugadorActivity {

    public static final String CLAVE_CLUB = "club";
    public static final String CLAVE_JUGADOR = "jugador";


    private String club;

    private Toolbar toolbar;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seleccionar_jugador);

        toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        ListaDeClubesFragment listaDeClubesFragment = new ListaDeClubesFragment();
        cargarFragment(listaDeClubesFragment);


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

        setResult(Helper.RESULT_OK,intent);

        finish();



    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (resultCode == Helper.RESULT_OK){

            Jugador jugador = (Jugador) data.getExtras().getSerializable(CLAVE_JUGADOR);
            String nombreClub = data.getExtras().getString(CLAVE_CLUB);

            Bundle bundle = new Bundle();

            bundle.putSerializable(MainActivity.CLAVE_JUGADOR,jugador);
            bundle.putString(MainActivity.CLAVE_CLUB,nombreClub);


            Intent intent = new Intent(ListaDeClubesActivity.this,MainActivity.class);

            intent.putExtras(bundle);

            setResult(Helper.RESULT_OK,intent);

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
}
