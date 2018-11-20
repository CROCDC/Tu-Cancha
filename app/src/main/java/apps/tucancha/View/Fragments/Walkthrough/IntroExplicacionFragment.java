package apps.tucancha.View.Fragments.Walkthrough;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import apps.tucancha.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class IntroExplicacionFragment extends Fragment {


    public IntroExplicacionFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_intro_explicacion, container, false);
    }

}
