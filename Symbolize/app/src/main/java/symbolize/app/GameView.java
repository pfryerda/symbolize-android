package symbolize.app;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

import java.util.LinkedList;

import static symbolize.app.Constants.*;

public class GameView {
    // Fields
    private Context context;
    private LinearLayout linearlayout;
    private Bitmap bitmap;
    private Canvas canvas;
    private final LinkedList<Line> palyerGraph;
    private Paint paint;

    private Animation rotateRightAnimation;
    private Animation rotateLeftAnimation;
    private Animation flipHAnimation;
    private Animation flipVAnimation;

    // Constructor
    public GameView(Context ctx, LinearLayout ll, Bitmap bm, LinkedList<Line> pg) {
        // Set up main view fields
        context = ctx;
        linearlayout = ll;
        bitmap = bm;
        canvas = new Canvas(bm);
        palyerGraph = pg;

        if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            linearlayout.setBackgroundDrawable(new BitmapDrawable(bitmap));
        } else {
            linearlayout.setBackground(new BitmapDrawable(context.getResources(), bitmap));
        }

        // Set up paint
        paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setAntiAlias(true);
        paint.setStrokeWidth(LINESIZE);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);

        // Set up animations
        rotateRightAnimation = AnimationUtils.loadAnimation(context, R.anim.rotate_right);
        rotateLeftAnimation = AnimationUtils.loadAnimation(context, R.anim.rotate_left);
        flipHAnimation = AnimationUtils.loadAnimation(context, R.anim.flip_h);
        flipVAnimation = AnimationUtils.loadAnimation(context, R.anim.flip_v);
        addListener(rotateRightAnimation);
        addListener(rotateLeftAnimation);
        addListener(flipHAnimation);
        addListener(flipVAnimation);
    }

    // Methods
    public void clearCanvas() {
        canvas.save();
        canvas.drawColor(BACKGROUND_COLOR);
        drawBorder();
        // Draw grid
        canvas.restore();
    }
    public void renderGraph(LinkedList<Line> graph) {
        clearCanvas();
        for (Line l : graph) {
            renderLine(l);
        }
    }
    public void drawBorder() {
        for (Line l : BORDER) {
            renderLine(l);
        }
    }
    public void renderLine(Line l) {
        canvas.save();
        paint.setColor(l.getColor());
        canvas.drawLine(l.getP1().x(), l.getP1().y(), l.getP2().x(), l.getP2().y(), paint);
        canvas.restore();
        linearlayout.invalidate();
    }
    public void renderErase(Line l) {
        canvas.save();
        paint.setColor(BACKGROUND_COLOR);
        canvas.drawLine(l.getP1().x(), l.getP1().y(), l.getP2().x(), l.getP2().y(), paint);
        canvas.restore();
        linearlayout.invalidate();
    }
    public void renderRotateR(final LinkedList<Line> graph) {
        canvas.save();
        linearlayout.startAnimation(rotateRightAnimation);
        canvas.restore();
    }
    public void renderRotateL() {
        canvas.save();
        linearlayout.startAnimation(rotateLeftAnimation);
        canvas.restore();
    }
    public void renderFlipH() {
        canvas.save();
        linearlayout.startAnimation(flipHAnimation);
        canvas.restore();
    }
    public void renderFlipV() {
        canvas.save();
        linearlayout.startAnimation(flipVAnimation);
        canvas.restore();
    }
    public void addListener(Animation a) {
        a.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}
            @Override
            public void onAnimationEnd(Animation animation) { renderGraph(palyerGraph); }
            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
    }

}
