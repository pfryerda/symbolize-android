package app.symbolize.Game;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.Shader;
import android.graphics.drawable.GradientDrawable;
import android.util.SparseIntArray;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.ads.AdSize;

import java.util.ArrayList;

import app.symbolize.Common.Communication.Request;
import app.symbolize.Common.Communication.Response;
import app.symbolize.Routing.Page;
import app.symbolize.Common.Session;
import app.symbolize.DataAccess.MetaDataAccess;
import app.symbolize.DataAccess.OptionsDataAccess;
import app.symbolize.DataAccess.UnlocksDataAccess;
import app.symbolize.Puzzle.Puzzle;
import app.symbolize.R;

/*
 * An all static class used to update the game's ui elements as well as set up their dimensions on start up
 */
abstract public class GameUIView {
    // Constants
    //-----------

    private static final float BUTTON_FADE = 0.4f;

    private static final Toast TOAST = Toast.makeText( GamePage.Get_context(), "", Toast.LENGTH_SHORT );

    public static final Point SCREEN_SIZE;
    public static final short CANVAS_SIZE;
    public static final short BAR_HEIGHT;
    public static final short AD_HEIGHT;
    public static final short BOTTOM_BUTTON_WIDTH;
    public static final short TOP_BUTTON_WIDTH;

    public static final ArrayList<Integer> COLOR_ARRAY;
    public static final ArrayList<Integer> BRIGHT_COLOR_ARRAY;
    public static final SparseIntArray COLOR_MAP;


    // Fields
    //--------

    private static RelativeLayout game_page;

    private static LinearLayout top_bar;
    private static LinearLayout bottom_bar;

    private static ImageButton undo_button;
    private static ImageButton draw_button;
    private static ImageButton erase_button;

    private static Button left_button;
    private static Button right_button;

    private static TextView title;


    // Static block
    //--------------

    static {
        COLOR_ARRAY = new ArrayList<Integer>();
        int[] color_array = Page.Get_context().getResources().getIntArray( R.array.color_array );

        for ( int color : color_array ) {
            COLOR_ARRAY.add( color );
        }

        BRIGHT_COLOR_ARRAY = new ArrayList<Integer>();
        color_array = Page.Get_context().getResources().getIntArray( R.array.bright_color_array );

        for ( int color : color_array ) {
            BRIGHT_COLOR_ARRAY.add( color );
        }

        SparseIntArray color_map = new SparseIntArray();
        for ( int i = 0; i < COLOR_ARRAY.size(); ++i ) {
            color_map.put( COLOR_ARRAY.get(i), i );
        }
        COLOR_MAP = color_map;

        final Activity activity = GamePage.Get_activity();

        final Display DISPLAY = GamePage.Get_activity().getWindowManager().getDefaultDisplay();
        SCREEN_SIZE = new Point();
        DISPLAY.getSize( SCREEN_SIZE );

        AD_HEIGHT = (short) AdSize.BANNER.getHeightInPixels( activity );
        CANVAS_SIZE = (short) ( ( SCREEN_SIZE.y > SCREEN_SIZE.x ) ? SCREEN_SIZE.x : SCREEN_SIZE.y );
        BAR_HEIGHT = (short) ( ( SCREEN_SIZE.y - CANVAS_SIZE - AD_HEIGHT ) / 2 );
        BOTTOM_BUTTON_WIDTH = (short) ( SCREEN_SIZE.x / 5 );
        TOP_BUTTON_WIDTH = (short) ( BOTTOM_BUTTON_WIDTH / 2 );
    }


    // Main method
    //-------------

    public static void Update_ui( Boolean can_undo ) {
        final Session session = Session.Get_instance();
        final Puzzle current_puzzle = session.Get_current_puzzle();
        final UnlocksDataAccess unlocks_dao = UnlocksDataAccess.Get_instance();

        title.setText( session.Get_current_puzzle_text() );

        if ( unlocks_dao.Is_unlocked( session.Get_previous_world() ) && session.Is_in_world_view() ) {
            left_button.setVisibility( View.VISIBLE );
        } else {
            left_button.setVisibility( View.INVISIBLE );
        }

        if ( unlocks_dao.Is_unlocked( session.Get_next_world() ) && session.Is_in_world_view() ) {
            right_button.setVisibility( View.VISIBLE );
        } else {
            right_button.setVisibility( View.INVISIBLE );
        }

        if ( can_undo != null ) { // If can_undo == null leave undo button as it was before
            undo_button.setEnabled( can_undo );
            undo_button.setAlpha( ( can_undo ) ? 1: BUTTON_FADE );
        }

        if ( ( current_puzzle.Get_erase_restriction() > 0 )
           || MetaDataAccess.Get_instance().Has_seen( MetaDataAccess.SEEN_ERASE ) ) {
            draw_button.setVisibility( View.VISIBLE );
            erase_button.setVisibility( View.VISIBLE );
        } else {
            draw_button.setVisibility( View.INVISIBLE );
            erase_button.setVisibility( View.INVISIBLE );
        }

        final int[] colors = new int[2];
        colors[0] = session.Get_world_color();
        colors[1] = ( OptionsDataAccess.Get_instance().Get_boolean_option( OptionsDataAccess.OPTION_BORDER ) )
                ? Color.WHITE : Color.TRANSPARENT;

        GradientDrawable gradient = new GradientDrawable( GradientDrawable.Orientation.TOP_BOTTOM, colors );
        gradient.setCornerRadius( 0f );
        if( android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN ) {
            top_bar.setBackgroundDrawable( gradient );
        } else {
            top_bar.setBackground( gradient );
        }

        gradient = new GradientDrawable( GradientDrawable.Orientation.BOTTOM_TOP, colors );
        gradient.setCornerRadius( 0f );
        if( android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            bottom_bar.setBackgroundDrawable( gradient );
        } else {
            bottom_bar.setBackground( gradient );
        }
    }


    // Public methods
    //----------------

    /*
     * Sets all the game's ui dimensions, note: this is faster than doing it in xml
     */
    public static void Set_ui_dimensions() {
        final Activity activity = GamePage.Get_activity();

        // Reset variable
        game_page = (RelativeLayout) activity.findViewById( R.id.page );

        top_bar = (LinearLayout) activity.findViewById( R.id.top_bar );
        bottom_bar = (LinearLayout) activity.findViewById( R.id.bottom_bar );

        undo_button = (ImageButton) activity.findViewById( R.id.Undo );
        draw_button = (ImageButton) activity.findViewById( R.id.Draw );
        erase_button = (ImageButton) activity.findViewById( R.id.Erase );

        left_button = (Button) activity.findViewById( R.id.Left );
        right_button = (Button) activity.findViewById( R.id.Right );

        title = (TextView) activity.findViewById( R.id.Title );

        // Set the buttons/layout width/height - 'Faster than doing it via xml'
        activity.findViewById( R.id.top_bar ).getLayoutParams().height = BAR_HEIGHT;
        activity.findViewById( R.id.buttons ).getLayoutParams().height = BAR_HEIGHT;

        activity.findViewById( R.id.Check ).getLayoutParams().width = BOTTOM_BUTTON_WIDTH;
        activity.findViewById( R.id.Hint ).getLayoutParams().width = BOTTOM_BUTTON_WIDTH;
        activity.findViewById( R.id.Undo ).getLayoutParams().width = BOTTOM_BUTTON_WIDTH;
        activity.findViewById( R.id.Draw ).getLayoutParams().width = BOTTOM_BUTTON_WIDTH;
        activity.findViewById( R.id.Erase ).getLayoutParams().width = BOTTOM_BUTTON_WIDTH;

        activity.findViewById( R.id.Left ).getLayoutParams().width = TOP_BUTTON_WIDTH;
        activity.findViewById( R.id.Back ).getLayoutParams().width = TOP_BUTTON_WIDTH;
        activity.findViewById( R.id.Right ).getLayoutParams().width = TOP_BUTTON_WIDTH;
        activity.findViewById( R.id.Reset ).getLayoutParams().width = TOP_BUTTON_WIDTH;
        activity.findViewById( R.id.Settings ).getLayoutParams().width = TOP_BUTTON_WIDTH;

        activity.findViewById( R.id.Title ).getLayoutParams().width = SCREEN_SIZE.x - ( 5 * TOP_BUTTON_WIDTH );
        ( (TextView) activity.findViewById( R.id.Title ) ).setGravity( Gravity.CENTER );

        Highlight_current_mode();
    }

    /*
     * Updates the draw and erase button accordingly
     */
    public static void Highlight_current_mode() {
        Session session = Session.Get_instance();
        int color = session.Get_hightlight_color();

        if ( Session.Get_instance().In_draw_mode() ) {
            draw_button.setColorFilter( color, PorterDuff.Mode.MULTIPLY );
            erase_button.setColorFilter( null );
        } else {
            draw_button.setColorFilter( null );
            erase_button.setColorFilter( color, PorterDuff.Mode.MULTIPLY );
        }
    }

    /*
     * Set phones brightness level
     *
     * @param short brightness: The brightness you wish to set
     */
    public static void Set_brightness( final short brightness ) {
        Window window = Page.Get_window();
        WindowManager.LayoutParams layout_params = Page.Get_attributes();

        if ( OptionsDataAccess.Get_instance().Get_boolean_option( OptionsDataAccess.OPTION_USE_DEVICE_BRIGHTNESS ) ) {
            layout_params.screenBrightness = -1;
        } else {
            layout_params.screenBrightness = (float) Math.max( OptionsDataAccess.MIN_BRIGHTNESS, brightness ) / OptionsDataAccess.BRIGHTNESS_SCALING;
        }
        window.setAttributes( layout_params );
    }

    public static void Set_brightness() {
        Set_brightness(
                OptionsDataAccess.Get_instance().Get_short_option( OptionsDataAccess.OPTION_BRIGHTNESS ));
    }

    /*
     * Given your current color get the next color in the COLORARRAY
     *
     * @param int color: Your current color
     *
     * @return int: The next color
     */
    public static int Get_next_color( final int color ) {
        return COLOR_ARRAY.get( ( COLOR_MAP.get( color ) + 1 ) % COLOR_ARRAY.size() );
    }

    /*
     * Show an error message via toast
     *
     * @param int request_type: The type of request that yield an error, will define the error message
     */
    public static void Toast_error_request( int request_type ) {
        switch ( request_type ) {
            case Request.Check_correctness:
                render_toast( R.string.incorrect );
                break;

            case Request.Undo:
                render_toast( R.string.nothing_to_undo );
                break;

            case Request.Draw:
                render_toast( R.string.out_of_lines );
                break;

            case Request.Erase:
                render_toast( R.string.out_of_erase );
                break;

            case Request.Drag_end:
                render_toast( R.string.out_of_drag );
                GameController.Get_instance().Handle_request( new Request( Request.Undo ), new Response() );
                break;

            default:
                break;
        }
    }

    /*
     * @return LinearGradient: the linear gradient used for the vertical grid lines
     */
    public static LinearGradient Get_grid_gradient() {
        int[] colors = new int[]{ Color.WHITE, Color.LTGRAY, Color.WHITE };
        float[] positions = new float[]{ 0.0f,
                (float) ( BAR_HEIGHT + CANVAS_SIZE / 2) / SCREEN_SIZE.y,
                ( 2.0f * BAR_HEIGHT + CANVAS_SIZE ) / SCREEN_SIZE.y };
        return new LinearGradient( 0, 0, 0, SCREEN_SIZE.y, colors, positions, Shader.TileMode.MIRROR );
    }


    // Private methods
    //-----------------

    /*
     * Methods use to temporarily show a small message at the bottom of the screen
     */
    private static void render_toast( int msg_id ) {
        if ( TOAST == null || TOAST.getView().getWindowVisibility() != View.VISIBLE ) {
            TOAST.setText( Page.Get_context().getResources().getString( msg_id ) );
            TOAST.show();
        }
    }
}
