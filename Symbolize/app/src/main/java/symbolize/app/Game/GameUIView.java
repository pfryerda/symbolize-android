package symbolize.app.Game;

import android.app.Activity;
import android.graphics.Point;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdSize;

import symbolize.app.Common.Communication.Request;
import symbolize.app.Common.Communication.Response;
import symbolize.app.Common.Page;
import symbolize.app.Common.Session;
import symbolize.app.DataAccess.MetaDataAccess;
import symbolize.app.DataAccess.UnlocksDataAccess;
import symbolize.app.R;

/*
 * An all static class used to update the game's ui elements as well as set up their dimensions on start up
 */
public class GameUIView {
    // Static fields
    //--------------

    private static final Toast TOAST = Toast.makeText( GamePage.Get_context(), "", Toast.LENGTH_SHORT );

    public static final Point SCREEN_SIZE;
    public static final int CANVAS_SIZE;
    public static final int BAR_HEIGHT;
    public static final int BOTTOM_BUTTON_WIDTH;
    public static final int TOP_BUTTON_WIDTH;

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

        CANVAS_SIZE = ( SCREEN_SIZE.y > SCREEN_SIZE.x ) ? SCREEN_SIZE.x : SCREEN_SIZE.y;
        BAR_HEIGHT = ( SCREEN_SIZE.y - CANVAS_SIZE - AdSize.BANNER.getHeightInPixels( activity ) ) / 2;
        BOTTOM_BUTTON_WIDTH = SCREEN_SIZE.x / 5;
        TOP_BUTTON_WIDTH = BOTTOM_BUTTON_WIDTH / 2;
    }


    // Main method
    //-------------

    public static void Update_ui() {
        Session session = Session.Get_instance();

        title.setText( session.Get_current_puzzle_text() );

        if ( UnlocksDataAccess.Is_unlocked( session.Get_previous_world() ) && session.Is_in_world_view() ) {
            left_button.setVisibility( View.VISIBLE );
        } else {
            left_button.setVisibility( View.INVISIBLE );
        }

        if ( UnlocksDataAccess.Is_unlocked( session.Get_next_world() ) && session.Is_in_world_view() ) {
            right_button.setVisibility( View.VISIBLE );
        } else {
            right_button.setVisibility( View.INVISIBLE );
        }

        if ( ( session.Get_current_puzzle().Get_erase_restriction() > 0 ) || MetaDataAccess.Has_seen_erase() ) {
            erase_button.setVisibility( View.VISIBLE );
        } else {
            erase_button.setVisibility( View.INVISIBLE );
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
        draw_button = (Button) activity.findViewById( R.id.Draw );
        erase_button = (Button) activity.findViewById( R.id.Erase );
        left_button = (Button) activity.findViewById( R.id.Left );
        right_button = (Button) activity.findViewById( R.id.Right );
        title = (TextView) activity.findViewById( R.id.Title );

        // Set canvas heights
        //activity.findViewById( R.id.background ).getLayoutParams().height = CANVAS_SIZE;
        //activity.findViewById( R.id.background ).getLayoutParams().width = CANVAS_SIZE;
        //activity.findViewById( R.id.foreground ).getLayoutParams().height = CANVAS_SIZE;
        //activity.findViewById( R.id.foreground ).getLayoutParams().width = CANVAS_SIZE;
        activity.findViewById( R.id.fake_canvas ).getLayoutParams().height = CANVAS_SIZE;
        activity.findViewById( R.id.fake_canvas ).setVisibility( View.INVISIBLE );

        // Set the buttons/layout width/height - 'Faster than doing it via xml'
        activity.findViewById( R.id.buttons ).getLayoutParams().height = BAR_HEIGHT;
        activity.findViewById( R.id.topbar ).getLayoutParams().height = BAR_HEIGHT;

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
