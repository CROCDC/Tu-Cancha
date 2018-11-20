package apps.tucancha.View.Fragments.Walkthrough;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import apps.tucancha.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class IntroExplicacionCompartirFragment extends android.support.v4.app.Fragment {

    private TextView textViewDescripcionFragment;

    public IntroExplicacionCompartirFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_intro_explicacion_compartir, container, false);

        textViewDescripcionFragment = view.findViewById(R.id.textViewDescripcionFragment_fragmentintroexplicacioncompartir);



        return view;
    }

}
