package symbolize.app.Game;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Point;
import android.graphics.Shader;
import android.graphics.drawable.GradientDrawable;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.ads.AdSize;
import symbolize.app.Common.Communication.Request;
import symbolize.app.Common.Communication.Response;
import symbolize.app.Common.Page;
import symbolize.app.Common.Session;
import symbolize.app.DataAccess.MetaDataAccess;
import symbolize.app.DataAccess.OptionsDataAccess;
import symbolize.app.DataAccess.UnlocksDataAccess;
import symbolize.app.Puzzle.Puzzle;
import symbolize.app.R;

/*
 * An all static class used to update the game's ui elements as well as set up their dimensions on start up
 */
abstract public class GameUIView {
    // Constants
    //-----------

    private static final Toast TOAST = Toast.makeText( GamePage.Get_context(), "", Toast.LENGTH_SHORT );

    public static final Point SCREEN_SIZE;
    public static final short CANVAS_SIZE;
    public static final short BAR_HEIGHT;
    public static final short AD_HEIGHT;
    public static final short BOTTOM_BUTTON_WIDTH;
    public static final short TOP_BUTTON_WIDTH;


    // Fields
    //--------

    private static RelativeLayout game_page;

    private static LinearLayout top_bar;
    private static LinearLayout bottom_bar;

    private static Button undo_button;
    private static Button draw_button;
    private static Button erase_button;

    private static Button left_button;
    private static Button right_button;

    private static TextView title;


    // Static block
    //--------------

    static {
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
            if ( can_undo ) {
                //undo_button.setColorFilter( 0xA6A6A6A6, PorterDuff.Mode.SRC_ATOP );
            } else {
                //undo_button.setColorFilter( null );
            }
        }

        if ( ( current_puzzle.Get_erase_restriction() > 0 )
           || MetaDataAccess.Get_instance().Has_seen( MetaDataAccess.SEEN_ERASE ) ) {
            draw_button.setVisibility( View.VISIBLE );
            erase_button.setVisibility( View.VISIBLE );
        } else {
            draw_button.setVisibility( View.INVISIBLE );
            erase_button.setVisibility( View.INVISIBLE );
        }

        /*if ( OptionsDataAccess.Show_border() ) {
            top_bar.setBackgroundColor( Color.WHITE );
            bottom_bar.setBackgroundColor( Color.WHITE );
        } else {
            top_bar.setBackgroundColor( Color.TRANSPARENT );
            bottom_bar.setBackgroundColor( Color.TRANSPARENT );
        }*/

        int[] colors = new int[2];
        colors[0] = GamePage.Get_activity().getResources().getColor( R.color.green );
        colors[1] = ( OptionsDataAccess.Get_instance().Get_boolean_option( OptionsDataAccess.OPTION_BORDER ) )
                ? Color.WHITE : Color.TRANSPARENT;

        GradientDrawable gradient = new GradientDrawable( GradientDrawable.Orientation.TOP_BOTTOM, colors );
        gradient.setCornerRadius( 0f );
        top_bar.setBackgroundDrawable( gradient );

        gradient = new GradientDrawable( GradientDrawable.Orientation.BOTTOM_TOP, colors );
        gradient.setCornerRadius( 0f );
        bottom_bar.setBackgroundDrawable( gradient );

    }


    // Public methods
    //----------------

    /*
     * Sets all the game's ui dimensions, note: this is faster than doing it in xml
     */
    public static void Set_ui_dimensions() {
        final Activity activity = GamePage.Get_activity();

        // Reset variable
        game_page = (RelativeLayout) activity.findViewById( R.id.game_page );

        top_bar = (LinearLayout) activity.findViewById( R.id.top_bar );
        bottom_bar = (LinearLayout) activity.findViewById( R.id.bottom_bar );

        undo_button = (Button) activity.findViewById( R.id.Undo );
        draw_button = (Button) activity.findViewById( R.id.Draw );
        erase_button = (Button) activity.findViewById( R.id.Erase );

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
    }

    /*
     * Set phones brightness level
     */
    public static void Set_brightness( short brightness ) {
        Window window = Page.Get_window();
        WindowManager.LayoutParams layout_params = Page.Get_attributes();

        layout_params.screenBrightness = (float) brightness / OptionsDataAccess.BRIGHTNESS_SCALING;
        window.setAttributes( layout_params );
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
