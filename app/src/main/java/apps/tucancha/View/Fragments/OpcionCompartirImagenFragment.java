package apps.tucancha.View.Fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import apps.tucancha.R;
import it.sephiroth.android.library.tooltip.Tooltip;

/**
 * A simple {@link Fragment} subclass.
 */
public class OpcionCompartirImagenFragment extends Fragment {


    public OpcionCompartirImagenFragment() {
        // Required empty public constructor
    }

    private RelativeLayout relativeLayoutContenedor;
    private CardView cardViewContenedorOpciones;
    private LinearLayout linearLayoutOpcionConQR;
    private LinearLayout linearLayoutOpcionSinQR;
    private ImageView imageViewButtonInformacionConQR;
    private ImageView imageViewButtonInformacionSinQR;
    private ProgressBar progressBar;

    private NotificadorHaciaMainActivity notificador;

    private Boolean fragmentDeMuestra = false;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            notificador = (NotificadorHaciaMainActivity) context;

        } catch (Exception e) {
            fragmentDeMuestra = true;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_opcion_compartir_imagen, container, false);

        relativeLayoutContenedor = view.findViewById(R.id.relativeLayoutContenedor_fragmentopcioncompartirimagen);
        cardViewContenedorOpciones = view.findViewById(R.id.cardViewContenedorOpcionesCompartir_fragmentingresarnombredelacancha);
        linearLayoutOpcionConQR = view.findViewById(R.id.linearLayoutContenedorConQR_fragmentopcioncompartirimagen);
        linearLayoutOpcionSinQR = view.findViewById(R.id.linearLayoutContendorSinQR_fragmentopcioncompartirimagen);
        imageViewButtonInformacionConQR = view.findViewById(R.id.imageViewButtonInformacionConQR_fragmentopcioncompartirimagen);
        imageViewButtonInformacionSinQR = view.findViewById(R.id.imageViewButtonInformacionSinQR_fragmentopcioncompartirimagen);
        progressBar = view.findViewById(R.id.progressBar_fragmentopcioncompartirimagen);


        if (fragmentDeMuestra) {
            imageViewButtonInformacionConQR.setOnClickListener(new ClickImageViewButtonInformacionConQR());
            imageViewButtonInformacionSinQR.setOnClickListener(new ClickImageViewButtonInformacionSinQR());

        } else {

            relativeLayoutContenedor.setOnClickListener(new ClickRelativeLayoutContenedor());
            cardViewContenedorOpciones.setOnClickListener(new ClickCardViewContenedorOpciones());
            linearLayoutOpcionConQR.setOnClickListener(new ClickLinearLayoutOpcionConQR());
            linearLayoutOpcionSinQR.setOnClickListener(new ClickLinearLayoutOpcionSinQR());
            imageViewButtonInformacionConQR.setOnClickListener(new ClickImageViewButtonInformacionConQR());
            imageViewButtonInformacionSinQR.setOnClickListener(new ClickImageViewButtonInformacionSinQR());

        }
        return view;
    }

    private class ClickRelativeLayoutContenedor implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            getActivity().onBackPressed();
        }
    }

    private class ClickCardViewContenedorOpciones implements View.OnClickListener {
        @Override
        public void onClick(View v) {

        }
    }

    private class ClickLinearLayoutOpcionConQR implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            notificador.CompartirConCodigoQR();
            irse();
        }
    }

    private class ClickLinearLayoutOpcionSinQR implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            notificador.CompartirSinCodigoQR();
            irse();
        }
    }

    private class ClickImageViewButtonInformacionConQR implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            View view = imageViewButtonInformacionConQR;
            Tooltip.make(getContext(),
                    new Tooltip.Builder(101)
                            .anchor(v, Tooltip.Gravity.BOTTOM)
                            .closePolicy(new Tooltip.ClosePolicy()
                                    .insidePolicy(true, false)
                                    .outsidePolicy(true, false), 6000)
                            .activateDelay(10000)
                            .showDelay(100)
                            .text("Si compartes la Cancha con un codigo QR tus amigos podran copiar tu cancha sin esfuerzo al compartirla de nuevo a la aplicacion")
                            .maxWidth(500)
                            .withArrow(true)
                            .withOverlay(true)
                            .floatingAnimation(Tooltip.AnimationBuilder.DEFAULT)
                            .build()
            ).show();
        }
    }

    private class ClickImageViewButtonInformacionSinQR implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Tooltip.make(getContext(),
                    new Tooltip.Builder(101)
                            .anchor(v, Tooltip.Gravity.BOTTOM)
                            .closePolicy(new Tooltip.ClosePolicy()
                                    .insidePolicy(true, false)
                                    .outsidePolicy(true, false), 6000)
                            .activateDelay(10000)
                            .showDelay(100)
                            .text("Si compartes la Cancha sin un codigo QR tus amigos no podran copiar tu cancha al compartirla de nuevo con la aplicacion")
                            .maxWidth(500)
                            .withArrow(true)
                            .withOverlay(true)
                            .floatingAnimation(Tooltip.AnimationBuilder.DEFAULT)
                            .build()
            ).show();


        }
    }

    public interface NotificadorHaciaMainActivity {
        void CompartirConCodigoQR();

        void CompartirSinCodigoQR();
    }

    public void detenerProgressBar() {
        progressBar.setVisibility(View.GONE);

    }

    private void irse() {
        progressBar.setVisibility(View.VISIBLE);
        onDestroy();
    }
}
