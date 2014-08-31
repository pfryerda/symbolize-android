package symbolize.app.Game;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.ads.AdSize;
import java.util.ArrayList;
import java.util.LinkedList;
import symbolize.app.Animation.SymbolizeAnimation;
import symbolize.app.Common.Line;
import symbolize.app.Common.Page;
import symbolize.app.DataAccess.OptionsDataAccess;
import symbolize.app.Common.Session;
import symbolize.app.Common.Posn;
import symbolize.app.DataAccess.ProgressDataAccess;
import symbolize.app.DataAccess.UnlocksDataAccess;
import symbolize.app.Dialog.HintDialog;
import symbolize.app.R;


/*
 * Class in charge of updating the display/view with new alterations to the GameModel
 */
public class GameView {
    // Static Fields
    //---------------

    public static final short SCALING = 10000;
    public static final short LINE_WIDTH = (short) ( GameUIView.CANVAS_SIZE / 17 );
    public static final short LINE_BORDER_WIDTH = (short) ( LINE_WIDTH / 3 );
    public static final short POINT_WIDTH = (short) ( ( LINE_WIDTH * 7 ) / 4 );
    public static final short POINT_BORDER_WIDTH = (short) ( POINT_WIDTH / 10 );
    public static final short TEXT_WIDTH = (short) ( POINT_WIDTH / 2 );
    public static final short GRID_WIDTH = (short) ( LINE_WIDTH / 10 );
    public static final short BORDER_WIDTH = LINE_WIDTH;
    public static final byte SHADOW = 80;


    // Fields
    //--------

    private final LinearLayout foreground;
    private final LinearLayout background;
    private final Canvas foreground_canvas;
    private final Canvas background_canvas;
    private final Paint paint;


    // Constructor
    //-------------

    public GameView() {
        this.background = (LinearLayout) GamePage.Get_activity().findViewById( R.id.background );
        this.foreground = (LinearLayout) GamePage.Get_activity().findViewById( R.id.foreground );

        // Set up bitmap's
        final Bitmap background_bitmap = Bitmap.createBitmap( GameUIView.SCREEN_SIZE.x, GameUIView.SCREEN_SIZE.y, Bitmap.Config.ARGB_8888 );
        final Bitmap foreground_bitmap = Bitmap.createBitmap( GameUIView.SCREEN_SIZE.x, GameUIView.SCREEN_SIZE.y, Bitmap.Config.ARGB_8888 );

        // Setup canvas's
        this.foreground_canvas = new Canvas( foreground_bitmap );
        this.background_canvas = new Canvas( background_bitmap );

        // Add bitmap's to linearlayout
        if( android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN ) {
            foreground.setBackgroundDrawable( new BitmapDrawable( foreground_bitmap ) );
            background.setBackgroundDrawable( new BitmapDrawable( background_bitmap ) );
        } else {
            foreground.setBackground( new BitmapDrawable( GamePage.Get_context().getResources(), foreground_bitmap ) );
            background.setBackground( new BitmapDrawable( GamePage.Get_context().getResources(), background_bitmap ) );
        }

        // Set up paint
        paint = new Paint();
        paint.setColor( Color.BLACK );
        paint.setAntiAlias( true );
        paint.setStyle( Paint.Style.STROKE );
        paint.setStrokeJoin( Paint.Join.ROUND );
        paint.setStrokeCap( Paint.Cap.ROUND );
        paint.setTextSize( TEXT_WIDTH );
        paint.setTextAlign( Paint.Align.CENTER );

        render_background();
    }


    // Main methods
    //--------------


    /*
     * Render the game =D
     */
    public void Render( final LinkedList<Line> graph, final ArrayList<Posn> levels, final boolean update_background ) {
        if ( update_background ) {
            render_background();
        }
        render_foreground( graph, levels );
    }

    public void Render( final LinkedList<Line> graph, final ArrayList<Posn> levels,
                        final SymbolizeAnimation animation, final boolean requires_hint_box )
    {
        animation.SetSymbolizeAnimationListener( new SymbolizeAnimation.SymbolizeAnimationListener() {
            @Override
            public void onSymbolizeAnimationClear() {
                foreground.clearAnimation();
            }
            @Override
            public void onSymbolizeAnimationMiddle() {
                render_foreground( graph, levels );
            }
            @Override
            public void onSymbolizeAnimationEnd() {
                Render( graph, levels, false );
                if( requires_hint_box ) {
                    HintDialog hint_dialog = new HintDialog();
                    hint_dialog.Set_attrs();
                    hint_dialog.Show();
                }
            }
        } );
        animation.Animate( foreground );
    }

    public void Render( final LinkedList<Line> graph, final ArrayList<Posn> levels, final Line shadow_line ) {
        Render( graph, levels, false );
        render_shadow( shadow_line );
    }

    public void Render( final LinkedList<Line> graph, final ArrayList<Posn> levels, final Posn shadow_posn ) {
        Render( graph, levels, false );
        render_shadow( shadow_posn );
    }


    // Private methods
    //----------------

    /*
     * Update the view with the current board in the model
     */
    private void render_foreground( final LinkedList<Line> graph, final ArrayList<Posn> levels ) {
        clear_foreground();
        paint.setStyle( Paint.Style.STROKE );

        // Draw graph lines
        paint.setStrokeWidth( LINE_WIDTH );
        paint.setColor( Color.BLACK );
        for ( Line line : graph ) {
            render_line( foreground_canvas, line );
        }

        paint.setStrokeWidth( LINE_WIDTH - LINE_BORDER_WIDTH );
        for ( Line line : graph ) {
            paint.setColor( line.Get_color() );
            render_line( foreground_canvas, line );
        }

        // Draw level dots
        Session session = Session.Get_instance();
        for ( int i = 0; i < levels.size(); ++i ) {
            if( UnlocksDataAccess.Is_unlocked( session.Get_current_world(), i + 1 ) ) {
                Posn point = levels.get( i );
                if ( point != null ) {
                    // Draw 'complete' border
                    paint.setStrokeWidth( POINT_WIDTH );
                    paint.setStyle( Paint.Style.STROKE );
                    if ( ProgressDataAccess.Is_completed( session.Get_current_world(), i + 1 ) ) {
                        paint.setColor( Color.GREEN );
                    } else {
                        paint.setColor( Color.RED );
                    }
                    render_point( foreground_canvas, point );

                    // Draw Point
                    paint.setStrokeWidth( POINT_WIDTH - POINT_BORDER_WIDTH );
                    paint.setStyle( Paint.Style.STROKE );
                    paint.setColor( Color.BLACK );
                    render_point( foreground_canvas, point );

                    // Draw Number
                    paint.setStyle( Paint.Style.FILL );
                    paint.setColor( Color.WHITE );
                    render_text( foreground_canvas, point, Integer.toString( i + 1 ) );
                }
            }
        }

        foreground.invalidate();
    }

    /*
     * Simple method used to draw a grid in the background
     */
    private void render_background() {
        clear_background();
        paint.setStyle( Paint.Style.STROKE );

        if ( OptionsDataAccess.Get_boolean_option( OptionsDataAccess.OPTION_GRID ) ) {
            paint.setColor( Color.LTGRAY );
            paint.setStrokeWidth( GRID_WIDTH );

            for ( short y = SCALING / 10; y < SCALING; y += SCALING / 10 ) {
                render_line( background_canvas, new Line( new Posn( (short) 0, y ), new Posn( SCALING, y ) ) );
            }

            if ( !OptionsDataAccess.Get_boolean_option( OptionsDataAccess.OPTION_BORDER ) ) {
                paint.setShader( GameUIView.Get_grid_gradient() );
            }

            short bar_height_scaled = (short) ( GameUIView.BAR_HEIGHT * SCALING / GameUIView.CANVAS_SIZE );
            for ( short x = SCALING / 10; x < SCALING; x += SCALING / 10 ) {
                render_line( background_canvas, new Line( new Posn( x, (short) ( -1 * bar_height_scaled ) ),
                                                          new Posn( x, (short) ( SCALING + bar_height_scaled ) ) ) );
            }

            paint.setShader( null );
        }

        if ( OptionsDataAccess.Get_boolean_option( OptionsDataAccess.OPTION_BORDER ) ) {
            paint.setColor( Color.BLACK );
            paint.setStrokeWidth( BORDER_WIDTH );

            render_line( background_canvas, new Line( new Posn( 0, 0 ), new Posn( (short) 0, SCALING ) ) );
            render_line( background_canvas, new Line( new Posn( 0, 0 ), new Posn( SCALING, (short) 0 ) ) );
            render_line( background_canvas, new Line( new Posn( (short) 0, SCALING ), new Posn( SCALING, SCALING ) ) );
            render_line( background_canvas, new Line( new Posn( SCALING, (short) 0 ), new Posn( SCALING, SCALING ) ) );
        }
    }

    /*
     * Unsclaes a line/point and then draws it on the canvas
     */
    private void render_line( final Canvas canvas, final Line line ) {
        canvas.drawLine( line.Get_p1().Unscale().x(), line.Get_p1().Unscale().y(),
                         line.Get_p2().Unscale().x(), line.Get_p2().Unscale().y(), paint );
    }

    private void render_point( final Canvas canvas, final Posn point ) {
        canvas.drawPoint( point.Unscale().x(), point.Unscale().y(), paint );
    }

    private void render_text( final Canvas canvas, final Posn point, final String text ) {
        canvas.drawText( text, point.Unscale().x(), point.Unscale().y() + ( TEXT_WIDTH / 2 ), paint );
    }

    /*
     * Render a shadow to the view
     *
     * @param Line shadow_line: The line you wish to draw a shadow for
     * @param Posn shadow_point: The point you wish to draw a shdow for
     */
    private void render_shadow( final Line shadow_line ) {
        paint.setStyle( Paint.Style.STROKE );
        paint.setColor( shadow_line.Get_color() );
        paint.setAlpha( SHADOW );
        paint.setStrokeWidth( LINE_WIDTH );

        render_line( foreground_canvas, shadow_line );
        foreground.invalidate();
    }

    private void render_shadow( final Posn shadow_point ) {
        paint.setStyle( Paint.Style.STROKE );
        paint.setColor( Color.BLACK );
        paint.setAlpha( SHADOW );
        paint.setStrokeWidth( POINT_WIDTH );

        render_point( foreground_canvas, shadow_point );
        foreground.invalidate();
    }

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
