package symbolize.app;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.util.Log;
import android.view.animation.AlphaAnimation;
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
    private LinearLayout foregound;
    private LinearLayout background;
    private Bitmap bitmap_fg;
    private Bitmap bitmap_bg;
    private Canvas canvas_fg;
    private Canvas canvas_bg;
    private GameModel gameModel;
    private Paint paint;

    private Animation rotateRightAnimation;
    private Animation rotateLeftAnimation;
    private Animation flipHAnimation;
    private Animation flipVAnimation;
    private Animation fadeOutAndInAnimation;
    private Animation fadeInAnimation;

    // Constructor
    public GameView(Context ctx, LinearLayout fg, LinearLayout bg, Bitmap bm_fg, Bitmap bm_bg, GameModel gm) {
        // Set up main view fields
        context = ctx;
        foregound = fg;
        background = bg;
        bitmap_fg = bm_fg;
        bitmap_bg = bm_bg;
        canvas_fg = new Canvas(bitmap_fg);
        canvas_bg = new Canvas(bitmap_bg);
        gameModel = gm;

        if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            foregound.setBackgroundDrawable(new BitmapDrawable(bitmap_fg));
            background.setBackgroundDrawable(new BitmapDrawable(bitmap_bg));
        } else {
            foregound.setBackground(new BitmapDrawable(context.getResources(), bitmap_fg));
            background.setBackground(new BitmapDrawable(context.getResources(), bitmap_bg));
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
        rotateRightAnimation.setDuration(ROTATEDURATION);
        setUpAnimation(rotateRightAnimation);
        rotateLeftAnimation = new RotateAnimation(0, -90, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotateLeftAnimation.setDuration(ROTATEDURATION);

        setUpAnimation(rotateLeftAnimation);
        flipHAnimation = new ScaleAnimation(1, -1, 1, 1, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        flipHAnimation.setDuration(FLIPDURATION);
        setUpAnimation(flipHAnimation);
        flipVAnimation = new ScaleAnimation(1, 1, 1, -1, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        flipVAnimation.setDuration(FLIPDURATION);
        setUpAnimation(flipVAnimation);

        fadeOutAndInAnimation = new AlphaAnimation(1, 0);
        fadeOutAndInAnimation.setDuration(FADEDURATION);
        fadeOutAndInAnimation.setFillAfter(true);
        fadeOutAndInAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}
            @Override
            public void onAnimationEnd(Animation animation) {
                foregound.clearAnimation();
                renderGraph();
                foregound.startAnimation(fadeInAnimation);
            }
            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
        fadeInAnimation = new AlphaAnimation(0, 1);
        fadeInAnimation.setDuration(FADEDURATION);
        fadeInAnimation.setFillAfter(true);

        paint.setStrokeWidth(LINESIZE/10);
        drawBackgroundImage(GRID);
        paint.setStrokeWidth(LINESIZE);
        drawBackgroundImage(BORDER);
    }

    // Methods
    public void clearCanvas() {
        canvas_fg.drawColor(0, PorterDuff.Mode.CLEAR);
        // Draw grid
    }
    public void renderGraph() {
        clearCanvas();
        for (Line l : gameModel.getGraph()) {
            renderLine(l);
        }
        foregound.invalidate();
    }
    public void drawBackgroundImage(LinkedList<Line> g) {
        for (Line l : g) {
            paint.setColor(l.getColor());
            canvas_bg.drawLine(l.getP1().x(), l.getP1().y(), l.getP2().x(), l.getP2().y(), paint);
            background.invalidate();
        }
    }
    public void renderLine(Line l) {
        paint.setColor(l.getColor());
        canvas_fg.drawLine(l.getP1().x(), l.getP1().y(), l.getP2().x(), l.getP2().y(), paint);
        foregound.invalidate();
    }
    public void renderRotateR() { foregound.startAnimation(rotateRightAnimation); }
    public void renderRotateL() {
        foregound.startAnimation(rotateLeftAnimation);
    }
    public void renderFlipH() {
        foregound.startAnimation(flipHAnimation);
    }
    public void renderFlipV() {
        foregound.startAnimation(flipVAnimation);
    }
    public void renderUndo() {
        gameModel = gameModel.getPastState();
        renderGraph();
    }
    public void renderShift() { foregound.startAnimation(fadeOutAndInAnimation); }
    public void renderToast(String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    private void setUpAnimation(Animation a) {
        a.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}
            @Override
            public void onAnimationEnd(Animation animation) {
                foregound.clearAnimation();
                renderGraph();
            }
            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
    }
}
