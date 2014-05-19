package symbolize.app;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.LinkedList;

import static symbolize.app.Constants.*;

public class GameView {
    // Fields
    private Context context;
    private LinearLayout linearlayout;
    private Bitmap bitmap;
    private Canvas canvas;
    private GameModel gameModel;
    private Paint paint;

    private Animation rotateRightAnimation;
    private Animation rotateLeftAnimation;
    private Animation flipHAnimation;
    private Animation flipVAnimation;

    // Constructor
    public GameView(Context ctx, LinearLayout ll, Bitmap bm, GameModel gm) {
        // Set up main view fields
        context = ctx;
        linearlayout = ll;
        bitmap = bm;
        canvas = new Canvas(bm);
        gameModel = gm;

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
        rotateRightAnimation = new RotateAnimation(0, 90, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        addListener(rotateRightAnimation);
        rotateLeftAnimation = new RotateAnimation(0, -90, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        addListener(rotateLeftAnimation);
        flipHAnimation = new ScaleAnimation(1, -1, 1, 1, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        addListener(flipHAnimation);
        flipVAnimation = new ScaleAnimation(1, 1, 1, -1, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        addListener(flipVAnimation);
    }

    // Methods
    public void clearCanvas() {
        canvas.drawColor(BACKGROUND_COLOR);
        drawBorder();
        // Draw grid
    }
    public void renderGraph() {
        clearCanvas();
        for (Line l : gameModel.getGraph()) {
            renderLine(l);
        }
    }
    public void drawBorder() {
        for (Line l : BORDER) {
            renderLine(l);
        }
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
    public void renderRotateR(final LinkedList<Line> graph) {
        linearlayout.startAnimation(rotateRightAnimation);
    }
    public void renderRotateL() {
        linearlayout.startAnimation(rotateLeftAnimation);
    }
    public void renderFlipH() {
        linearlayout.startAnimation(flipHAnimation);
    }
    public void renderFlipV() {
                   linearlayout.startAnimation(flipVAnimation);
    }
    public void renderUndo() {
        gameModel = gameModel.getPastState();
        renderGraph();
    }
    public void renderToast(String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    private void addListener(Animation a) {
        a.setDuration(ANIMATIONDURATION);
        a.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}
            @Override
            public void onAnimationEnd(Animation animation) {
                linearlayout.clearAnimation();
                renderGraph();
            }
            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
    }

}
