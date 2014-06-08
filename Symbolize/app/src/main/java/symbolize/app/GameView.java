package symbolize.app;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.LinkedList;


/*
 * Class in charge of updating the display/view with new alterations to the GameModel
 */
public class GameView {
    // Satic Fields
    //-------------

    public static final int LINEWIDTH = 60;
    public static final int SHADOW = 80;
    public static final int ROTATEDURATION = 450;
    public static final int FLIPDURATION = 450;
    public static final int FADEDURATION = 450;
    public static final LinkedList<Line> GRID = makeGrid();


    // Fields
    //--------

    private Context context;
    private LinearLayout foregound;
    private LinearLayout background;
    private Bitmap foregoundBitmap;
    private Bitmap backgroundBitmap;
    private Canvas foregoundCanvas;
    private Canvas backgroundCanvas;
    private GameModel gameModel;
    private Paint paint;

    private Animation rotateRightAnimation;
    private Animation rotateLeftAnimation;
    private Animation flipHAnimation;
    private Animation flipVAnimation;
    private Animation fadeOutAndInAnimation;
    private Animation fadeInAnimation;


    // Constructor
    //-------------

    public GameView(Context context, LinearLayout fg, LinearLayout bg, Bitmap foregoundBitmap, Bitmap backgroundBitmap, GameModel gameModel) {
        // Set up main view fields
        this.context = context;
        this.foregound = fg;
        this.background = bg;
        this.foregoundBitmap = foregoundBitmap;
        this.backgroundBitmap = backgroundBitmap;
        this.foregoundCanvas = new Canvas( foregoundBitmap );
        this.backgroundCanvas = new Canvas( backgroundBitmap );
        this.gameModel = gameModel;

        if( android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN ) {
            foregound.setBackgroundDrawable( new BitmapDrawable( foregoundBitmap ) );
            background.setBackgroundDrawable( new BitmapDrawable( backgroundBitmap ) );
        } else {
            foregound.setBackground( new BitmapDrawable( context.getResources(), foregoundBitmap ) );
            background.setBackground( new BitmapDrawable( context.getResources(), backgroundBitmap ) );
        }


        // Set up paint
        paint = new Paint();
        paint.setColor( Color.BLACK );
        paint.setAntiAlias( true );
        paint.setStrokeWidth( LINEWIDTH );
        paint.setStyle( Paint.Style.STROKE );
        paint.setStrokeJoin( Paint.Join.ROUND );
        paint.setStrokeCap( Paint.Cap.ROUND );

        // Set up animations
        rotateRightAnimation = new RotateAnimation( 0, 90, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f );
        rotateRightAnimation.setDuration( ROTATEDURATION );
        setUpAnimation(rotateRightAnimation);
        rotateLeftAnimation = new RotateAnimation( 0, -90, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f );
        rotateLeftAnimation.setDuration( ROTATEDURATION );

        setUpAnimation(rotateLeftAnimation);
        flipHAnimation = new ScaleAnimation( 1, -1, 1, 1, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f );
        flipHAnimation.setDuration( FLIPDURATION );
        setUpAnimation( flipHAnimation );
        flipVAnimation = new ScaleAnimation( 1, 1, 1, -1, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f );
        flipVAnimation.setDuration( FLIPDURATION );
        setUpAnimation( flipVAnimation );

        fadeOutAndInAnimation = new AlphaAnimation( 1, 0 );
        fadeOutAndInAnimation.setDuration( FADEDURATION );
        fadeOutAndInAnimation.setFillAfter( true );
        fadeOutAndInAnimation.setAnimationListener( new Animation.AnimationListener() {
            @Override
            public void onAnimationStart( Animation animation ) {}
            @Override
            public void onAnimationEnd( Animation animation ) {
                foregound.clearAnimation();
                renderGraph();
                foregound.startAnimation( fadeInAnimation );
            }
            @Override
            public void onAnimationRepeat( Animation animation ) {}
        });
        fadeInAnimation = new AlphaAnimation( 0, 1 );
        fadeInAnimation.setDuration( FADEDURATION );
        fadeInAnimation.setFillAfter( true );

        paint.setStrokeWidth( LINEWIDTH/10 );
        drawBackgroundImage( GRID );
        paint.setStrokeWidth( LINEWIDTH );
    }


    // Methods
    //----------

    /*
     * Methods used to clear all lines in the foreground
     */
    public void clearCanvas() {
        foregoundCanvas.drawColor( 0, PorterDuff.Mode.CLEAR );
        // Draw grid
    }

    /*
     * Update the view with the current board in the model
     */
    public void renderGraph() {
        clearCanvas();
        for ( Line line : gameModel.getGraph() ) {
            renderLine( line );
        }
        foregound.invalidate();
    }

    /*
     * Simple method used to draw background elements in the backgroud i.e. border/grid
     *
     *  @param: LinkedList<Line> element: The target element we wish to draw
     */
    private void drawBackgroundImage(LinkedList<Line> element) {
        for (Line line : element) {
            paint.setColor( line.getColor() );
            backgroundCanvas.drawLine( line.getP1().x(), line.getP1().y(), line.getP2().x(), line.getP2().y(), paint );
            background.invalidate();
        }
    }

    /*
     * Display a toast with the given message
     *
     * @param: String msg: The message we want to output
     */
    public void renderToast(String msg) {
        Toast.makeText( context, msg, Toast.LENGTH_SHORT ).show();
    }

    /*
     * Method used to setup an animation so that once completed it prints the updated graph
     *
     * @param: Animation a: The animation you wish to set up
     */
    private void setUpAnimation( Animation a ) {
        a.setAnimationListener( new Animation.AnimationListener() {
            @Override
            public void onAnimationStart( Animation animation ) {}
            @Override
            public void onAnimationEnd( Animation animation ) {
                foregound.clearAnimation();
                renderGraph();
            }
            @Override
            public void onAnimationRepeat( Animation animation ) {}
        });
    }


    // Button methods
    //----------------

    public void renderUndo() {
        gameModel = gameModel.getPastState();
        renderGraph();
    }


    // Action methods
    //----------------

    public void renderLine( Line l ) {
        paint.setColor(l.getColor());
        foregoundCanvas.drawLine(l.getP1().x(), l.getP1().y(), l.getP2().x(), l.getP2().y(), paint);
        foregound.invalidate();
    }

    public void renderShadowLine( Line l ) {
        renderGraph();
        paint.setColor( Color.BLACK );
        paint.setAlpha( SHADOW );
        foregoundCanvas.drawLine(l.getP1().x(), l.getP1().y(), l.getP2().x(), l.getP2().y(), paint);
        foregound.invalidate();
    }

    public void renderShadowPoint( Posn point ) {
        renderGraph();
        paint.setColor( Color.BLACK );
        paint.setAlpha( SHADOW );
        paint.setStrokeWidth( LINEWIDTH * 2);
        foregoundCanvas.drawPoint( point.x(), point.y(), paint );
        foregound.invalidate();
        paint.setStrokeWidth( LINEWIDTH );
    }

    public void renderRotateR() {
        foregound.startAnimation(rotateRightAnimation);
    }

    public void renderRotateL() {
        foregound.startAnimation(rotateLeftAnimation);
    }

    public void renderFlipH() {
        foregound.startAnimation(flipHAnimation);
    }

    public void renderFlipV() {
        foregound.startAnimation(flipVAnimation);
    }

    public void renderShift() {
        foregound.startAnimation(fadeOutAndInAnimation);
    }


    // Static methods
    //----------------

    public final static LinkedList<Line> makeGrid() {
        LinkedList<Line> grid = new LinkedList<Line>();
        for ( int x = GameActivity.SCALING/10; x < GameActivity.SCALING; x+=GameActivity.SCALING/10 ) {
            grid.addLast( new Line( new Posn( x, 0 ), new Posn( x, GameActivity.SCALING ), Color.LTGRAY ) );
        }

        for ( int y = GameActivity.SCALING/10; y < GameActivity.SCALING; y+=GameActivity.SCALING/10 ) {
            grid.addLast( new Line( new Posn( 0, y ), new Posn( GameActivity.SCALING, y ), Color.LTGRAY ) );
        }

        return grid;
    }
}
