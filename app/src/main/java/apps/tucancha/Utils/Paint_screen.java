package apps.tucancha.Utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.MotionEvent;
import android.view.View;

public class Paint_screen extends View {

    private Float x;
    private Float y;
    private String accion;

    public Paint_screen(Context context) {
        super(context);
    }

    public void onDraw(Canvas canvas) {
        x = 50F;
        y = 50F;

        String accion = "";

        Path path = new Path();

        canvas.drawColor(Color.TRANSPARENT);

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(4);
        paint.setColor(Color.TRANSPARENT);

        if (accion == "down") {
            path.moveTo(x, y);
        }
        if (accion == "move") {
            path.lineTo(x, y);
        }
        canvas.drawPath(path, paint);

    }

    public boolean onTouch(MotionEvent event) {
        Integer axion = event.getAction();
        x = event.getX();
        y = event.getY();

        if (axion == MotionEvent.ACTION_DOWN){
            accion = "down";
        }
        if (axion == MotionEvent.ACTION_MOVE){
            accion = "move";
        }
        invalidate();
        return true;
    }
}
