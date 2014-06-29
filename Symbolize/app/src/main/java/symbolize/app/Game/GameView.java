package symbolize.app.Game;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.widget.LinearLayout;
import android.widget.Toast;

import symbolize.app.Common.Action;
import symbolize.app.Common.Animation.FadeOutAndInSymbolizeAnimation;
import symbolize.app.Common.Animation.FlipSymbolizeAnimation;
import symbolize.app.Common.Animation.RotateSymbolizeAnimation;
import symbolize.app.Common.Line;
import symbolize.app.Common.Posn;


/*
 * Class in charge of updating the display/view with new alterations to the GameModel
 */
public class GameView {
    // Satic Fields
    //-------------

    public static boolean InAnimation = false;
    public static final int LINEWIDTH = GameActivity.SCALING / 17;
    public static final int POINTWIDTH = LINEWIDTH * 2;
    public static final int TEXTWIDTH = LINEWIDTH;
    public static final int SHADOW = 80;
    public static final int FADEDURATION = 450;


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

    private RotateSymbolizeAnimation rotateRightAnimation;
    private RotateSymbolizeAnimation rotateLeftAnimation;
    private FlipSymbolizeAnimation flipHAnimation;
    private FlipSymbolizeAnimation flipVAnimation;
    private FadeOutAndInSymbolizeAnimation fadeOutAndInAnimation;


    // Constructor
    //-------------

    public GameView( Context context, LinearLayout foregound, LinearLayout background, Bitmap foregoundBitmap, Bitmap backgroundBitmap, GameModel gameModel ) {
        // Set up main view fields
        this.context = context;
        this.foregound = foregound;
        this.background = background;
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
        paint.setTextSize( TEXTWIDTH );

        // Set up animations
        rotateRightAnimation = new RotateSymbolizeAnimation( foregound, this, 90 );
        rotateLeftAnimation = new RotateSymbolizeAnimation( foregound, this, -90 );
        flipHAnimation = new FlipSymbolizeAnimation( foregound, this, -1, 1);
        flipVAnimation = new FlipSymbolizeAnimation( foregound, this, 1, -1 );
        fadeOutAndInAnimation = new FadeOutAndInSymbolizeAnimation( foregound, this );

        Render_background();
    }


    // Methods
    //----------

    /*
     * Methods used to clear all lines in the foreground
     */
    public void clearCanvas() {
        foregoundCanvas.drawColor( 0, PorterDuff.Mode.CLEAR );
    }

    /*
     * Update the view with the current board in the model
     */
    public void renderGraph() {
        clearCanvas();
        paint.setStyle( Paint.Style.STROKE );

        // Draw graph lines
        paint.setStrokeWidth( LINEWIDTH );
        for ( Line line : gameModel.getGraph() ) {
            paint.setColor( line.getColor() );
            foregoundCanvas.drawLine(line.getP1().x(), line.getP1().y(), line.getP2().x(), line.getP2().y(), paint);
        }

        // Draw level dots
        paint.setStrokeWidth( POINTWIDTH );
        for ( int i = 0; i < gameModel.getLevels().size(); ++i ) {
            Posn point = gameModel.getLevels().get(i);

            // Draw Point
            paint.setColor( Color.BLACK );
            paint.setStyle( Paint.Style.STROKE );
            foregoundCanvas.drawPoint(point.x(), point.y(), paint);

            // Draw Number
            paint.setStyle( Paint.Style.FILL );
            paint.setColor( Color.WHITE );
            foregoundCanvas.drawText( Integer.toString( i + 1 ), point.x() - ( TEXTWIDTH / 2 ), point.y() + ( TEXTWIDTH / 2 ), paint );
        }

        foregound.invalidate();
    }

    /*
     * Simple method used to draw a grid in the background
     */
    public void Render_background() {
        backgroundCanvas.drawColor( Color.WHITE );
        paint.setColor( Color.LTGRAY );
        paint.setStrokeWidth( LINEWIDTH/10 );

        for ( int x = GameActivity.SCALING/10; x < GameActivity.SCALING; x+=GameActivity.SCALING/10 ) {
            backgroundCanvas.drawLine( x, 0, x, GameActivity.SCALING, paint );
        }

        for ( int y = GameActivity.SCALING/10; y < GameActivity.SCALING; y+=GameActivity.SCALING/10 ) {
            backgroundCanvas.drawLine( 0, y, GameActivity.SCALING, y, paint );
        }

        background.invalidate();
    }

    // Button methods
    //----------------

    public void renderUndo() {
        gameModel = gameModel.getPastState();
        renderGraph();
    }


    // Action methods
    //----------------

    private void set_up_shadow() {
        renderGraph();
        paint.setStyle( Paint.Style.STROKE );
        paint.setColor( Color.BLACK );
        paint.setAlpha( SHADOW );
    }

    public void renderShadow( Line line ) {
        set_up_shadow();
        paint.setStrokeWidth( LINEWIDTH );

        foregoundCanvas.drawLine(line.getP1().x(), line.getP1().y(), line.getP2().x(), line.getP2().y(), paint);
        foregound.invalidate();
    }

    public void renderShadow( Posn point ) {
        set_up_shadow();
        paint.setStrokeWidth( POINTWIDTH );

        foregoundCanvas.drawPoint( point.x(), point.y(), paint );
        foregound.invalidate();
    }

    public void Render_motion( Action action ) {
        switch ( action ) {
            case Rotate_right:
                rotateRightAnimation.animate();
                break;

            case Rotate_left:
                rotateLeftAnimation.animate();
                break;

            case Flip_horizontally:
                flipHAnimation.animate();
                break;

            case Flip_vertically:
                flipVAnimation.animate();
                break;

            case Shift:
                fadeOutAndInAnimation.animate();
                break;
        }
    }
}
