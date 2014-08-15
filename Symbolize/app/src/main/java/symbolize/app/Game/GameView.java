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
import android.widget.Toast;

import com.google.android.gms.ads.AdSize;
import java.util.ArrayList;
import java.util.LinkedList;
import symbolize.app.Animation.GameAnimationHandler;
import symbolize.app.Animation.SymbolizeAnimation;
import symbolize.app.Common.Line;
import symbolize.app.Common.Page;
import symbolize.app.DataAccess.OptionsDataAccess;
import symbolize.app.Common.Player;
import symbolize.app.Common.Posn;
import symbolize.app.Common.Request;
import symbolize.app.DataAccess.ProgressDataAccess;
import symbolize.app.DataAccess.UnlocksDataAccess;
import symbolize.app.Dialog.HintDialog;
import symbolize.app.R;


/*
 * Class in charge of updating the display/view with new alterations to the GameModel
 */
public class GameView {
    // Static Fields
    //-------------

    private static final Toast TOAST = Toast.makeText( GamePage.Get_context(), "", Toast.LENGTH_SHORT );

    // Main sizes
    public static final int SCALING = 1000;
    public static final Point SCREEN_SIZE;
    public static final int CANVAS_SIZE;

    // Other sizes
    public static final int LINE_WIDTH = SCALING / 17;
    public static final int LINE_BORDER_WIDTH = SCALING / 50;
    public static final int POINT_WIDTH = ( LINE_WIDTH * 13 ) / 8;
    public static final int POINT_BORDER_WIDTH = POINT_WIDTH / 10;
    public static final int TEXT_WIDTH = POINT_WIDTH / 2;
    public static final int GRID_WIDTH = LINE_WIDTH / 10;
    public static final int BORDER_WIDTH = LINE_WIDTH;
    public static final int SHADOW = 80;


    // Static block
    //--------------

    static {
        final Display DISPLAY = GamePage.Get_activity().getWindowManager().getDefaultDisplay();
        SCREEN_SIZE = new Point();
        DISPLAY.getSize( SCREEN_SIZE );
        CANVAS_SIZE = ( SCREEN_SIZE.y > SCREEN_SIZE.x ) ? SCREEN_SIZE.x : SCREEN_SIZE.y;
    }


    // Fields
    //--------

    private final LinearLayout foreground;
    private final LinearLayout background;
    private final Canvas foreground_canvas;
    private final Canvas background_canvas;
    private final Paint paint;

    private final Button left_button;
    private final Button right_button;
    private final TextView title;


    // Constructor
    //-------------

    public GameView()
    {
        this.background = (LinearLayout) GamePage.Get_activity().findViewById( R.id.background );
        this.foreground = (LinearLayout) GamePage.Get_activity().findViewById( R.id.foreground );

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

        // Set up ui elements that are not static
        this.left_button = ( Button ) GamePage.Get_activity().findViewById( R.id.Left );
        this.right_button = ( Button ) GamePage.Get_activity().findViewById( R.id.Right );
        this.title = (TextView) GamePage.Get_activity().findViewById( R.id.Title );

        render_background();
    }


    // Main methods
    //--------------


    public void Render( final LinkedList<Line> graph, final ArrayList<Posn> levels, final boolean update_background ) {
        if ( update_background ) {
            render_background();
        }
        render_foreground( graph, levels );
        update_ui();
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
                    hint_dialog.Set_attrs( Player.Get_instance().Get_current_puzzle() );
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
            foreground_canvas.drawLine( line.Get_p1().x(), line.Get_p1().y(), line.Get_p2().x(), line.Get_p2().y(), paint );
        }

        paint.setStrokeWidth( LINE_WIDTH - LINE_BORDER_WIDTH );
        for ( Line line : graph ) {
            paint.setColor( line.Get_color() );
            foreground_canvas.drawLine( line.Get_p1().x(), line.Get_p1().y(), line.Get_p2().x(), line.Get_p2().y(), paint );
        }

        // Draw level dots
        Player player = Player.Get_instance();
        for ( int i = 0; i < levels.size(); ++i ) {
            if( UnlocksDataAccess.Is_unlocked( player.Get_current_world(), i + 1 ) ) {
                Posn point = levels.get( i );
                if ( point != null ) {

                    // Draw Point
                    paint.setStrokeWidth( POINT_WIDTH );
                    paint.setStyle( Paint.Style.STROKE );
                    paint.setColor( Color.BLACK );
                    foreground_canvas.drawPoint( point.x(), point.y(), paint );

                    // Draw 'complete' border
                    paint.setStrokeWidth( POINT_BORDER_WIDTH );
                    paint.setStyle( Paint.Style.STROKE );
                    if ( ProgressDataAccess.Is_completed( player.Get_current_world(), i + 1 ) ) {
                        paint.setColor( Color.GREEN );
                    } else {
                        paint.setColor( Color.RED );
                    }
                    foreground_canvas.drawCircle( point.x(), point.y(), POINT_WIDTH / 2, paint );

                    // Draw Number
                    paint.setStyle( Paint.Style.FILL );
                    paint.setColor( Color.WHITE );
                    foreground_canvas.drawText( Integer.toString( i + 1 ), point.x() - ( TEXT_WIDTH / 2 ),
                            point.y() + ( TEXT_WIDTH / 2 ), paint);
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

        if ( OptionsDataAccess.Show_grid() ) {
            paint.setColor( Color.LTGRAY );
            paint.setStrokeWidth( GRID_WIDTH );

            for ( int x = SCALING / 10; x < SCALING; x += SCALING / 10 ) {
                background_canvas.drawLine( x, 0, x, SCALING, paint );
            }

            for ( int y = SCALING / 10; y < SCALING; y += SCALING / 10 ) {
                background_canvas.drawLine( 0, y, SCALING, y, paint );
            }
        }

        if ( OptionsDataAccess.Show_border() ) {
            paint.setColor( Color.BLACK );
            paint.setStrokeWidth( BORDER_WIDTH );

            background_canvas.drawLine( 0, 0, 0, SCALING, paint );
            background_canvas.drawLine( 0, 0, SCALING, 0, paint );
            background_canvas.drawLine( 0, SCALING, SCALING, SCALING, paint );
            background_canvas.drawLine( SCALING, 0, SCALING, SCALING, paint );
        }

        background.invalidate();
    }

    private void render_shadow( final Line shadow_line ) {
        paint.setStyle( Paint.Style.STROKE );
        paint.setColor( shadow_line.Get_color() );
        paint.setAlpha( SHADOW );
        paint.setStrokeWidth( LINE_WIDTH );

            foreground_canvas.drawLine( shadow_line.Get_p1().x(), shadow_line.Get_p1().y(),
                    shadow_line.Get_p2().x(), shadow_line.Get_p2().y(), paint );
        foreground.invalidate();
    }

    private void render_shadow( final Posn shadow_point ) {
        paint.setStyle( Paint.Style.STROKE );
        paint.setColor( Color.BLACK );
        paint.setAlpha( SHADOW );
        paint.setStrokeWidth( POINT_WIDTH );

        foreground_canvas.drawPoint( shadow_point.x(), shadow_point.y(), paint );
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

    private void update_ui() {
        Player player = Player.Get_instance();

        title.setText( player.Get_current_puzzle_text() );

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

    public static void Render_toast( int msg_id ) {
        if ( TOAST == null || TOAST.getView().getWindowVisibility() != View.VISIBLE ) {
            TOAST.setText( Page.Get_context().getResources().getString( msg_id ) );
            TOAST.show();
        }
    }

    public static void Set_up_view() {
        Activity activity = GamePage.Get_activity();

        activity.findViewById( R.id.background ).getLayoutParams().height = CANVAS_SIZE;
        activity.findViewById( R.id.background ).getLayoutParams().width = CANVAS_SIZE;
        activity.findViewById( R.id.foreground ).getLayoutParams().height = CANVAS_SIZE;
        activity.findViewById( R.id.foreground ).getLayoutParams().width = CANVAS_SIZE;

        // Set the buttons/layout width/height - 'Faster than doing it via xml'
        int bar_height = ( SCREEN_SIZE.y - CANVAS_SIZE - AdSize.BANNER.getHeightInPixels( activity ) ) / 2;
        activity.findViewById( R.id.buttons ).getLayoutParams().height = bar_height;
        activity.findViewById( R.id.topbar ).getLayoutParams().height = bar_height;

        int button_width = SCREEN_SIZE.x / 5;
        activity.findViewById( R.id.Check ).getLayoutParams().width = button_width;
        activity.findViewById( R.id.Hint ).getLayoutParams().width = button_width;
        activity.findViewById( R.id.Undo ).getLayoutParams().width = button_width;
        activity.findViewById( R.id.Draw ).getLayoutParams().width = button_width;
        activity.findViewById( R.id.Erase ).getLayoutParams().width = button_width;
    }
}
