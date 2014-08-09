package symbolize.app.Game;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.google.android.gms.ads.AdSize;
import java.util.ArrayList;
import java.util.LinkedList;
import symbolize.app.Animation.GameAnimationHandler;
import symbolize.app.Common.Line;
import symbolize.app.DataAccess.OptionsDataAccess;
import symbolize.app.Common.Player;
import symbolize.app.Common.Posn;
import symbolize.app.Common.Request;
import symbolize.app.DataAccess.ProgressDataAccess;
import symbolize.app.DataAccess.UnlocksDataAccess;
import symbolize.app.R;


/*
 * Class in charge of updating the display/view with new alterations to the GameModel
 */
public class GameView {
    // Static Fields
    //-------------

    // Main sizes
    public static final int SCALING = 1000;
    public static Point SCREEN_SIZE;
    public static int CANVAS_SIZE;

    // Other sizes
    public static final int LINEWIDTH = SCALING / 17;
    public static final int POINTWIDTH = ( LINEWIDTH * 7 ) / 4;
    public static final int POINTBORDERWIDTH = POINTWIDTH / 10;
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

    private final Button left_button;
    private final Button right_button;
    private final TextView title;


    // Constructor
    //-------------

    public GameView( final LinearLayout foreground, final LinearLayout background )
    {
        this.animation_handler = new GameAnimationHandler();

        // Setup linearlayout's width/height - 'Guarantee a square'
        background.getLayoutParams().height = CANVAS_SIZE;
        background.getLayoutParams().width = CANVAS_SIZE;
        this.background = background;

        foreground.getLayoutParams().height = CANVAS_SIZE;
        foreground.getLayoutParams().width = CANVAS_SIZE;
        this.foreground = foreground;

        // Set up bitmap's
        final Bitmap background_bitmap = Bitmap.createScaledBitmap(
                Bitmap.createBitmap( CANVAS_SIZE, CANVAS_SIZE, Bitmap.Config.ARGB_8888 ), SCALING, SCALING, true );

        final Bitmap foreground_bitmap = Bitmap.createScaledBitmap(
                Bitmap.createBitmap( CANVAS_SIZE, CANVAS_SIZE, Bitmap.Config.ARGB_8888 ), SCALING, SCALING, true );

        // Setup canvas's
        this.foreground_canvas = new Canvas( foreground_bitmap );
        this.background_canvas = new Canvas( background_bitmap );

        // Add bitmap's to linearlayout
        if( android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN ) {
            foreground.setBackgroundDrawable( new BitmapDrawable( foreground_bitmap ) );
            background.setBackgroundDrawable( new BitmapDrawable( background_bitmap ) );
        } else {
            foreground.setBackground( new BitmapDrawable( GameActivity.Get_context().getResources(), foreground_bitmap ) );
            background.setBackground( new BitmapDrawable( GameActivity.Get_context().getResources(), background_bitmap ) );
        }

        // Set up paint
        paint = new Paint();
        paint.setColor( Color.BLACK );
        paint.setAntiAlias( true );
        paint.setStyle( Paint.Style.STROKE );
        paint.setStrokeJoin( Paint.Join.ROUND );
        paint.setStrokeCap( Paint.Cap.ROUND );
        paint.setTextSize( TEXTWIDTH );

        // Set up ui elements that are not static
        this.left_button = ( Button ) GameActivity.Get_activity().findViewById( R.id.Left );
        this.right_button = ( Button ) GameActivity.Get_activity().findViewById( R.id.Right );
        this.title = (TextView) GameActivity.Get_activity().findViewById( R.id.Title );

        Render_background();
    }


    // Public methods
    //----------------

    public void Render( final Request request ) {
        if ( request.type == Request.Background_change ) {
            Render_background();
        } else if ( request.Is_animation_action() ) {
            if( request.type == Request.Load_level_via_world ) {
                animation_handler.current_pivot = request.request_point;
            }
            request.linearLayout = foreground;
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
        update_ui();
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
        Player player = Player.Get_instance();
        for ( int i = 0; i < levels.size(); ++i ) {
            if( UnlocksDataAccess.Is_unlocked( player.Get_current_world(), i + 1 ) ) {
                Log.d( "GameView - TEST", (i + 1) + " unlocked");
                Posn point = levels.get( i );
                if ( point != null ) {

                    // Draw Point
                    paint.setStrokeWidth( POINTWIDTH );
                    paint.setStyle( Paint.Style.STROKE );
                    paint.setColor( Color.BLACK );
                    foreground_canvas.drawPoint( point.x(), point.y(), paint );

                    // Draw 'complete' border
                    paint.setStrokeWidth( POINTBORDERWIDTH );
                    paint.setStyle( Paint.Style.STROKE );
                    if ( ProgressDataAccess.Is_completed( player.Get_current_world(), i + 1 ) ) {
                        paint.setColor( Color.GREEN );
                    } else {
                        paint.setColor( Color.RED );
                    }
                    foreground_canvas.drawCircle( point.x(), point.y(), POINTWIDTH / 2, paint );

                    // Draw Number
                    paint.setStyle( Paint.Style.FILL );
                    paint.setColor( Color.WHITE );
                    foreground_canvas.drawText( Integer.toString( i + 1 ), point.x() - ( TEXTWIDTH / 2 ),
                            point.y() + ( TEXTWIDTH / 2 ), paint);
                }
            }
        }

        foreground.invalidate();
    }

    /*
     * Simple method used to draw a grid in the background
     */
    public void Render_background() {
        clear_background();
        paint.setStyle( Paint.Style.STROKE );

        if ( OptionsDataAccess.Show_grid() ) {
            paint.setColor( Color.LTGRAY );
            paint.setStrokeWidth( GRIDWIDTH );

            for ( int x = SCALING / 10; x < SCALING; x += SCALING / 10 ) {
                background_canvas.drawLine( x, 0, x, SCALING, paint );
            }

            for ( int y = SCALING / 10; y < SCALING; y += SCALING / 10 ) {
                background_canvas.drawLine( 0, y, SCALING, y, paint );
            }
        }

        if ( OptionsDataAccess.Show_border() ) {
            paint.setColor( Color.BLACK );
            paint.setStrokeWidth( BRODERWIDTH );

            background_canvas.drawLine( 0, 0, 0, SCALING, paint );
            background_canvas.drawLine( 0, 0, SCALING, 0, paint );
            background_canvas.drawLine( 0, SCALING, SCALING, SCALING, paint );
            background_canvas.drawLine( SCALING, 0, SCALING, SCALING, paint );
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

    private void update_ui() {
        Player player = Player.Get_instance();

        if ( player.Is_in_world_view() ) {
            title.setText( GameActivity.Get_resource_string( R.string.world ) + ": " + player.Get_current_world() );
        } else {
            title.setText( GameActivity.Get_resource_string( R.string.level ) + ": " + player.Get_current_world() + "-" + player.Get_current_level() );
        }

        if ( UnlocksDataAccess.Is_unlocked(player.Get_previous_world()) && player.Is_in_world_view() ) {
            left_button.setVisibility( View.VISIBLE );
        } else {
            left_button.setVisibility( View.GONE );
        }

        if ( UnlocksDataAccess.Is_unlocked(player.Get_next_world()) && player.Is_in_world_view() ) {
            right_button.setVisibility( View.VISIBLE );
        } else {
            right_button.setVisibility( View.GONE );
        }
    }


    // Static methods
    //----------------

    public static void Set_up_sizes() {
        Activity activity = GameActivity.Get_activity();

        // Get screen size and calculate canvas size
        final Display DISPLAY = activity.getWindowManager().getDefaultDisplay();
        SCREEN_SIZE = new Point();
        DISPLAY.getSize( SCREEN_SIZE );
        CANVAS_SIZE = ( SCREEN_SIZE.y > SCREEN_SIZE.x ) ? SCREEN_SIZE.x : SCREEN_SIZE.y;

        // Set the buttons/layout width/height - 'Faster than doing it via xml'
        int bar_height = ( SCREEN_SIZE.y - CANVAS_SIZE - AdSize.BANNER.getHeightInPixels( activity ) ) / 2;
        activity.findViewById(R.id.buttons).getLayoutParams().height = bar_height;
        activity.findViewById(R.id.topbar).getLayoutParams().height = bar_height;

        int button_width = SCREEN_SIZE.x / 5;
        activity.findViewById( R.id.Check ).getLayoutParams().width = button_width;
        activity.findViewById( R.id.Hint ).getLayoutParams().width = button_width;
        activity.findViewById( R.id.Undo ).getLayoutParams().width = button_width;
        activity.findViewById( R.id.Draw ).getLayoutParams().width = button_width;
        activity.findViewById( R.id.Erase ).getLayoutParams().width = button_width;
    }
}
