package symbolize.app.Game;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import symbolize.app.Common.Animation.SymbolizeAnimation;
import symbolize.app.Common.Enum.Action;
import symbolize.app.Common.Animation.FadeOutAndInSymbolizeAnimation;
import symbolize.app.Common.Animation.FlipSymbolizeAnimation;
import symbolize.app.Common.Animation.RotateSymbolizeAnimation;
import symbolize.app.Common.Line;
import symbolize.app.Common.Posn;


/*
 * Class in charge of updating the display/view with new alterations to the GameModel
 */
public class GameView {
    // Static Fields
    //-------------

    public static final int LINEWIDTH = GameActivity.SCALING / 17;
    public static final int POINTWIDTH = LINEWIDTH * 2;
    public static final int TEXTWIDTH = LINEWIDTH;
    public static final int SHADOW = 80;


    // Fields
    //--------

    private final LinearLayout foregound;
    private final LinearLayout background;
    private final Canvas foregound_canvas;
    private final Canvas background_canvas;
    private final Paint paint;
    private final HashMap<Action, SymbolizeAnimation> animations;


    // Constructor
    //-------------

    public GameView( final Context context, final LinearLayout foregound, final LinearLayout background,
                     final Bitmap foreground_bitmap, final Bitmap background_bitmap )
    {
        this.foregound = foregound;
        this.background = background;
        this.foregound_canvas = new Canvas( foreground_bitmap );
        this.background_canvas = new Canvas( background_bitmap );

        if( android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN ) {
            foregound.setBackgroundDrawable( new BitmapDrawable( foreground_bitmap ) );
            background.setBackgroundDrawable( new BitmapDrawable( background_bitmap ) );
        } else {
            foregound.setBackground( new BitmapDrawable( context.getResources(), foreground_bitmap ) );
            background.setBackground( new BitmapDrawable( context.getResources(), background_bitmap ) );
        }

        // Set up paint
        paint = new Paint();
        paint.setColor( Color.BLACK );
        paint.setAntiAlias( true );
        paint.setStyle( Paint.Style.STROKE );
        paint.setStrokeJoin( Paint.Join.ROUND );
        paint.setStrokeCap( Paint.Cap.ROUND );
        paint.setTextSize( TEXTWIDTH );

        // Set up animations
        animations = new HashMap<Action, SymbolizeAnimation>();
        animations.put( Action.Rotate_right, new RotateSymbolizeAnimation( foregound, 90 ) );
        animations.put( Action.Rotate_left, new RotateSymbolizeAnimation( foregound, -90 ) );
        animations.put( Action.Flip_horizontally, new FlipSymbolizeAnimation( foregound, -1, 1) );
        animations.put( Action.Flip_vertically, new FlipSymbolizeAnimation( foregound, 1, -1 ) );
        animations.put( Action.Shift, new FadeOutAndInSymbolizeAnimation( foregound ) );

        Render_background();
    }


    // Public methods
    //----------------

    /*
     * Update the view with the current board in the model
     */
    public void Render_foreground( final LinkedList<Line> graph, final ArrayList<Posn> levels ) {
        clear_foreground();
        paint.setStyle( Paint.Style.STROKE );

        // Draw graph lines
        paint.setStrokeWidth( LINEWIDTH );
        for ( Line line : graph ) {
            paint.setColor( line.Get_color() );
            foregound_canvas.drawLine(line.Get_p1().x(), line.Get_p1().y(), line.Get_p2().x(), line.Get_p2().y(), paint);
        }

        // Draw level dots
        paint.setStrokeWidth( POINTWIDTH );
        for ( int i = 0; i < levels.size(); ++i ) {
            Posn point = levels.get(i);

            // Draw Point
            paint.setStyle( Paint.Style.STROKE );
            paint.setColor( Color.BLACK );
            foregound_canvas.drawPoint(point.x(), point.y(), paint);

            // Draw Number
            paint.setStyle( Paint.Style.FILL );
            paint.setColor( Color.WHITE );
            foregound_canvas.drawText( Integer.toString( i + 1 ), point.x() - ( TEXTWIDTH / 2 ),
                    point.y() + ( TEXTWIDTH / 2 ), paint );
        }

        foregound.invalidate();
    }

    /*
     * Simple method used to draw a grid in the background
     */
    public void Render_background() {
        background_canvas.drawColor( Color.WHITE );
        paint.setColor( Color.LTGRAY );
        paint.setStrokeWidth( LINEWIDTH/10 );

        for ( int x = GameActivity.SCALING/10; x < GameActivity.SCALING; x+=GameActivity.SCALING/10 ) {
            background_canvas.drawLine( x, 0, x, GameActivity.SCALING, paint );
        }

        for ( int y = GameActivity.SCALING/10; y < GameActivity.SCALING; y+=GameActivity.SCALING/10 ) {
            background_canvas.drawLine( 0, y, GameActivity.SCALING, y, paint );
        }

        background.invalidate();
    }


    // Action methods
    //----------------

    public void Render_shadow( final Line line,
                               final LinkedList<Line> graph, final ArrayList<Posn> levels )
    {
        Render_foreground( graph, levels );
        paint.setStyle( Paint.Style.STROKE );
        paint.setColor( line.Get_color() );
        paint.setAlpha( SHADOW );
        paint.setStrokeWidth( LINEWIDTH );

        foregound_canvas.drawLine(line.Get_p1().x(), line.Get_p1().y(), line.Get_p2().x(), line.Get_p2().y(), paint);
        foregound.invalidate();
    }

    public void Render_shadow( final Posn point,
                               final LinkedList<Line> graph, final ArrayList<Posn> levels )
    {
        Render_foreground( graph, levels );
        paint.setStyle( Paint.Style.STROKE );
        paint.setColor( Color.BLACK );
        paint.setAlpha( SHADOW );
        paint.setStrokeWidth( POINTWIDTH );

        foregound_canvas.drawPoint( point.x(), point.y(), paint );
        foregound.invalidate();
    }

    public void Render_motion( final Action action,
                               final LinkedList<Line> graph, final ArrayList<Posn> levels )
    {
        animations.get( action ).Animate( this, graph, levels );
    }


    // Private methods
    //----------------

    /*
     * Methods used to clear all lines in the foreground
     */
    private void clear_foreground() {
        foregound_canvas.drawColor( 0, PorterDuff.Mode.CLEAR );
    }

    private void set_up_shadow( final LinkedList<Line> graph, final ArrayList<Posn> levels ) {
        Render_foreground( graph, levels );
        paint.setStyle( Paint.Style.STROKE );
        paint.setAlpha( SHADOW );
    }
}
