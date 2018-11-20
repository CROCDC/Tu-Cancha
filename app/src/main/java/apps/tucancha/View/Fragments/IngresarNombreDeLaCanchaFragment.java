package apps.tucancha.View.Fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import apps.tucancha.R;


public class IngresarNombreDeLaCanchaFragment extends Fragment {


    private EditText editTextNombreDeLaCancha;
    private ImageView imageViewButtonCheck;

    private NotificadorHaciaMainActivity notificadorHaciaMainActivity;

    public IngresarNombreDeLaCanchaFragment() {
        // Required empty public constructor
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        notificadorHaciaMainActivity = (NotificadorHaciaMainActivity) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_ingresar_nombre_de_la_cancha, container, false);

        editTextNombreDeLaCancha = view.findViewById(R.id.editTextNombreDeLaCancha_fragmentnomnredelacancha);
        imageViewButtonCheck = view.findViewById(R.id.imageViewButtonCheck_fragmentingresarnombredelacancha);

        editTextNombreDeLaCancha.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (editTextNombreDeLaCancha.getText().toString().equals("")) {
                    Toast.makeText(getContext(), "Ingrese un nombre por favor", Toast.LENGTH_SHORT).show();
                } else {
                    notificadorHaciaMainActivity.notificarGuardarCancha(editTextNombreDeLaCancha.getText().toString());


                }
                return false;
            }
        });

        imageViewButtonCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editTextNombreDeLaCancha.getText().toString().equals("")) {
                    Toast.makeText(getContext(), "Ingrese un nombre por favor", Toast.LENGTH_SHORT).show();
                } else {
                    notificadorHaciaMainActivity.notificarGuardarCancha(editTextNombreDeLaCancha.getText().toString());

                }
            }
        });


        return view;
    }


    public interface NotificadorHaciaMainActivity {
        public void notificarGuardarCancha(String nombreDeLaCancha);
    }
}
