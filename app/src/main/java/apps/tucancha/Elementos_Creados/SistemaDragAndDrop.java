package apps.tucancha.Elementos_Creados;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import apps.tucancha.View.Fragments.JugadorFragment;

/**
 * Esta Clase es la encargada de administrar el drag and drop de los fragments jugador
 */
public class SistemaDragAndDrop implements View.OnTouchListener {

    private int _xDelta;
    private int _yDelta;
    private FragmentManager fragmentManager;

    private JugadorFragment jugadorFragment;

    private ViewGroup rootLayout;

    public SistemaDragAndDrop(JugadorFragment jugadorFragment, FragmentManager fragmentManager, ViewGroup rootLayout) {
        this.jugadorFragment = jugadorFragment;
        this.fragmentManager = fragmentManager;
        this.rootLayout = rootLayout;
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        final int X = (int) event.getRawX();
        final int Y = (int) event.getRawY();
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                RelativeLayout.LayoutParams lParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
                _xDelta = X - lParams.leftMargin;
                _yDelta = Y - lParams.topMargin;
                break;
            case MotionEvent.ACTION_UP:
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                break;
            case MotionEvent.ACTION_POINTER_UP:
                break;
            case MotionEvent.ACTION_MOVE:
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view
                        .getLayoutParams();
                layoutParams.leftMargin = X - _xDelta;
                layoutParams.topMargin = Y - _yDelta;
                layoutParams.rightMargin = -250;
                layoutParams.bottomMargin = -250;
                view.setLayoutParams(layoutParams);

                if (X < 70 && Y > 1200) {
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.remove(jugadorFragment);
                    fragmentTransaction.commit();
                }
                break;
        }
        rootLayout.invalidate();
        return true;
    }
}

