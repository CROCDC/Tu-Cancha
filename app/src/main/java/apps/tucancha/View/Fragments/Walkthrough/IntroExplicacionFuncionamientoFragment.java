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
public class IntroExplicacionFuncionamientoFragment extends android.support.v4.app.Fragment {


    public IntroExplicacionFuncionamientoFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_intro_explicacion_funcionamiento, container, false);
    }


}
