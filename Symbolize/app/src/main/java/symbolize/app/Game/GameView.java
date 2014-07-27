package symbolize.app.Game;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.widget.LinearLayout;
import java.util.ArrayList;
import java.util.LinkedList;
import symbolize.app.Animation.GameAnimationHandler;
import symbolize.app.Common.Line;
import symbolize.app.Common.Options;
import symbolize.app.Common.Posn;
import symbolize.app.Common.Request;


/*
 * Class in charge of updating the display/view with new alterations to the GameModel
 */
public class GameView {
    // Static Fields
    //-------------

    public static final int LINEWIDTH = GameActivity.SCALING / 17;
    public static final int POINTWIDTH = LINEWIDTH * 2;
    public static final int TEXTWIDTH = LINEWIDTH;
    public static final int GRIDWIDTH = LINEWIDTH / 10;
    public static final int BRODERWIDTH = LINEWIDTH;
    public static final int SHADOW = 80;


    // Fields
    //--------

    private final LinearLayout foreground;
    private final LinearLayout background;
    private final Canvas foreground_canvas;
    private final Canvas background_canvas;
    private final Paint paint;
    private final GameAnimationHandler animation_handler;

    // Constructor
    //-------------

    public GameView( final Context context, final LinearLayout foreground, final LinearLayout background,
                     final Bitmap foreground_bitmap, final Bitmap background_bitmap, final Options options )
    {
        this.foreground = foreground;
        this.background = background;
        this.foreground_canvas = new Canvas( foreground_bitmap );
        this.background_canvas = new Canvas( background_bitmap );

        if( android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN ) {
            foreground.setBackgroundDrawable( new BitmapDrawable( foreground_bitmap ) );
            background.setBackgroundDrawable( new BitmapDrawable( background_bitmap ) );
        } else {
            foreground.setBackground( new BitmapDrawable( context.getResources(), foreground_bitmap ) );
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

        animation_handler = new GameAnimationHandler( foreground );

        Render_background( options );
    }


    // Public methods
    //----------------

    public void Render( Request request ) {
        if ( request.type == Request.Background_change ) {
            Render_background( request.options );
        } else if ( request.Is_animation_action() ) {
            if( request.type == Request.Load_level_via_world ) {
                animation_handler.Set_zoom_pivots( request.request_point );
            }
            request.game_view = this;
            animation_handler.Handle_request( request );
        } else {
            Render_foreground( request.graph, request.levels );
            if( request.Is_shadow_action() ) {
                paint.setStyle( Paint.Style.STROKE );
                paint.setColor( ( request.type == Request.Shadow_line ) ? request.request_line.Get_color() : Color.BLACK );
                paint.setAlpha( SHADOW );
                paint.setStrokeWidth( ( request.type == Request.Shadow_line ) ? LINEWIDTH : POINTWIDTH );

                if ( request.type == Request.Shadow_line ) {
                    foreground_canvas.drawLine( request.request_line.Get_p1().x(), request.request_line.Get_p1().y(),
                                                request.request_line.Get_p2().x(), request.request_line.Get_p2().y(), paint );
                } else {
                    foreground_canvas.drawPoint( request.request_point.x(), request.request_point.y(), paint );
                }
                foreground.invalidate();
            }
        }
    }

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
            foreground_canvas.drawLine(line.Get_p1().x(), line.Get_p1().y(), line.Get_p2().x(), line.Get_p2().y(), paint);
        }

        // Draw level dots
        paint.setStrokeWidth( POINTWIDTH );
        for ( int i = 0; i < levels.size(); ++i ) {
            Posn point = levels.get(i);
            if ( point != null ) {

                // Draw Point
                paint.setStyle( Paint.Style.STROKE );
                paint.setColor( Color.BLACK );
                foreground_canvas.drawPoint( point.x(), point.y(), paint );

                // Draw Number
                paint.setStyle( Paint.Style.FILL );
                paint.setColor( Color.WHITE );
                foreground_canvas.drawText( Integer.toString(i + 1), point.x() - ( TEXTWIDTH / 2 ),
                        point.y() + ( TEXTWIDTH / 2 ), paint );
            }
        }

        foreground.invalidate();
    }

    /*
     * Simple method used to draw a grid in the background
     */
    public void Render_background( Options options ) {
        clear_background();
        paint.setStyle( Paint.Style.STROKE );

        if ( options.Show_grid() ) {
            paint.setColor( Color.LTGRAY );
            paint.setStrokeWidth( GRIDWIDTH );

            for ( int x = GameActivity.SCALING / 10; x < GameActivity.SCALING; x += GameActivity.SCALING / 10 ) {
                background_canvas.drawLine( x, 0, x, GameActivity.SCALING, paint );
            }

            for ( int y = GameActivity.SCALING / 10; y < GameActivity.SCALING; y += GameActivity.SCALING / 10 ) {
                background_canvas.drawLine( 0, y, GameActivity.SCALING, y, paint );
            }
        }

        if ( options.Show_border() ) {
            paint.setColor( Color.BLACK );
            paint.setStrokeWidth( BRODERWIDTH );

            background_canvas.drawLine( 0, 0, 0, GameActivity.SCALING, paint );
            background_canvas.drawLine( 0, 0, GameActivity.SCALING, 0, paint );
            background_canvas.drawLine( 0, GameActivity.SCALING, GameActivity.SCALING, GameActivity.SCALING, paint );
            background_canvas.drawLine( GameActivity.SCALING, 0, GameActivity.SCALING, GameActivity.SCALING, paint );
        }

        background.invalidate();
    }


    // Private methods
    //----------------

    /*
     * Methods used to clear all lines in the foreground
     */
    private void clear_foreground() {
        foreground_canvas.drawColor( 0, PorterDuff.Mode.CLEAR );
    }

    /*
     * Methods used to clear all lines in the foreground
     */
    private void clear_background() {
        background_canvas.drawColor( 0, PorterDuff.Mode.CLEAR );
    }
}
