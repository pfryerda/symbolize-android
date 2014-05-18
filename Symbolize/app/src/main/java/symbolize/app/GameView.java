package symbolize.app;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.widget.LinearLayout;

import java.util.LinkedList;

import static symbolize.app.Constants.*;

public class GameView {
    // Fields
    private LinearLayout linearlayout;
    private Bitmap bitmap;
    private Canvas canvas;
    private Paint paint;

    // Constructor
    public GameView(LinearLayout ll, Bitmap bm) {
        linearlayout = ll;
        bitmap = bm;
        canvas = new Canvas(bm);
        paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setAntiAlias(true);
        paint.setStrokeWidth(LINESIZE);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
    }

    // Methods
    public void clearCanvas() {
        canvas.drawColor(BACKGROUND_COLOR);
        drawBorder();
        // Draw grid
    }
    public void drawBorder() {
        for (Line l : BORDER) {
            paint.setColor(l.getColor());
            canvas.drawLine(l.getP1().x(), l.getP1().y(), l.getP2().x(), l.getP2().y(), paint);
        }
        linearlayout.invalidate();
    }
    public void renderLine(Line l) {
        paint.setColor(l.getColor());
        canvas.drawLine(l.getP1().x(), l.getP1().y(), l.getP2().x(), l.getP2().y(), paint);
        linearlayout.invalidate();
    }
    public void renderErase(Line l) {
        paint.setColor(BACKGROUND_COLOR);
        canvas.drawLine(l.getP1().x(), l.getP1().y(), l.getP2().x(), l.getP2().y(), paint);
        linearlayout.invalidate();
    }

}
