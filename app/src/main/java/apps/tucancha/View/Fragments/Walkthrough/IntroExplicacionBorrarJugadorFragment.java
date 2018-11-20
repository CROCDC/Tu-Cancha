package apps.tucancha.View.Fragments.Walkthrough;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import apps.tucancha.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class IntroExplicacionBorrarJugadorFragment extends android.support.v4.app.Fragment {


    public IntroExplicacionBorrarJugadorFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_intro_explicacion_borrar_jugador, container, false);
    }

}
