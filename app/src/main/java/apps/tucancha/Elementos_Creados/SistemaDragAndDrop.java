package apps.tucancha.Elementos_Creados;

import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import apps.tucancha.Utils.ResultListener;
import apps.tucancha.View.Fragments.JugadorFragment;

/**
 * Esta Clase es la encargada de administrar el drag and drop de los fragments jugador
 */
public class SistemaDragAndDrop implements View.OnTouchListener {


    private JugadorFragment jugadorFragment;
    private ResultListener esuchadorDeEliminacion;
    private FrameLayout.LayoutParams layoutParams;


    public SistemaDragAndDrop(JugadorFragment jugadorFragment, final ResultListener<JugadorFragment> escuchadorDeEliminacion,FrameLayout.LayoutParams layoutParams) {
        this.jugadorFragment = jugadorFragment;
        this.esuchadorDeEliminacion = escuchadorDeEliminacion;
        this.layoutParams = layoutParams;
    }

    public SistemaDragAndDrop(JugadorFragment jugadorFragment, final ResultListener<JugadorFragment> esuchadorDeEliminacion, float X, float Y,FrameLayout.LayoutParams layoutParams) {
        this.jugadorFragment = jugadorFragment;
        this.esuchadorDeEliminacion = esuchadorDeEliminacion;
        this.layoutParams = layoutParams;


        jugadorFragment.getView().setY(Y);
        jugadorFragment.getView().setX(X);

        jugadorFragment.getView().invalidate();

    }


    @Override
    public boolean onTouch(View view, MotionEvent event) {

        final int X = (int) event.getRawX();
        final int Y = (int) event.getRawY();

        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_UP:
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                break;
            case MotionEvent.ACTION_POINTER_UP:
                break;
            case MotionEvent.ACTION_MOVE:


                jugadorFragment.getView().setX(X - view.getWidth() / 2);
                jugadorFragment.getView().setY(Y - layoutParams.topMargin * 2);


                if (X >= 20 && Y >= 1200) {
                    esuchadorDeEliminacion.finish(jugadorFragment);
                }

                break;
        }

        return true;
    }


}

