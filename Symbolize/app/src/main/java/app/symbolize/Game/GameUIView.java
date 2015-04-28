package app.symbolize.Game;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.drawable.GradientDrawable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.ads.AdSize;

import java.util.ArrayList;

import app.symbolize.Common.Communication.Request;
import app.symbolize.Common.Communication.Response;
import app.symbolize.Home.HomePage;
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
    public static final short CANVAS_MARGIN;
    public static final short TITLE_SIZE;
    public static final short BAR_HEIGHT;
    public static final short AD_HEIGHT;
    public static final short BOTTOM_BUTTON_WIDTH;
    public static final short TOP_BUTTON_WIDTH;

    public static final ArrayList<Integer> COLOR_ARRAY;
    public static final ArrayList<Integer> BRIGHT_COLOR_ARRAY;
    public static final ArrayList<Integer> LIGHT_COLOR_ARRAY;
    public static final SparseIntArray COLOR_MAP;

    public static final ArrayList<Integer> NUMBER_IMAGES_ARRAY;


    // Fields
    //--------

    private static RelativeLayout game_page;

    private static RelativeLayout top_bar;
    private static LinearLayout bottom_bar;

    private static ImageButton check_button;
    private static ImageButton undo_button;
    private static ImageButton hint_button;
    private static ImageButton draw_button;
    private static ImageButton erase_button;

    private static ImageButton left_button;
    private static ImageButton back_button;
    private static ImageButton right_button;

    private static ImageButton reset_button;
    private static ImageButton settings_button;

    private static LinearLayout title;
    private static ImageView title_state;
    private static ImageView title_number;


    // Static block
    //--------------

    static {
        COLOR_ARRAY = new ArrayList<Integer>();
        int[] color_array = Page.Get_context().getResources().getIntArray( R.array.color_array );

        for ( int color : color_array ) {
            COLOR_ARRAY.add( color );
        }

        SparseIntArray color_map = new SparseIntArray();
        for ( int i = 0; i < COLOR_ARRAY.size(); ++i ) {
            color_map.put( COLOR_ARRAY.get(i), i );
        }
        COLOR_MAP = color_map;

        BRIGHT_COLOR_ARRAY = new ArrayList<Integer>();
        color_array = Page.Get_context().getResources().getIntArray( R.array.bright_color_array );

        for ( int color : color_array ) {
            BRIGHT_COLOR_ARRAY.add( color );
        }

        LIGHT_COLOR_ARRAY = new ArrayList<Integer>();
        color_array = Page.Get_context().getResources().getIntArray( R.array.light_color_array );

        for ( int color : color_array ) {
            LIGHT_COLOR_ARRAY.add( color );
        }

        final Activity activity = GamePage.Get_activity();

        final Display DISPLAY = GamePage.Get_activity().getWindowManager().getDefaultDisplay();
        SCREEN_SIZE = new Point();
        DISPLAY.getSize( SCREEN_SIZE );

        AD_HEIGHT = (short) AdSize.BANNER.getHeightInPixels( activity );
        CANVAS_MARGIN = (short) ( SCREEN_SIZE.x / 68 );
        CANVAS_SIZE = (short) ( ( ( SCREEN_SIZE.y > SCREEN_SIZE.x ) ? SCREEN_SIZE.x : SCREEN_SIZE.y ) - 2 * CANVAS_MARGIN );
        TITLE_SIZE = (short) ( (float) CANVAS_SIZE / 3 );
        BAR_HEIGHT = (short) ( ( SCREEN_SIZE.y - CANVAS_SIZE - AD_HEIGHT - 2 * CANVAS_MARGIN ) / 2 );
        BOTTOM_BUTTON_WIDTH = (short) ( SCREEN_SIZE.x / 5 );
        TOP_BUTTON_WIDTH = (short) ( BOTTOM_BUTTON_WIDTH / 2 );

        NUMBER_IMAGES_ARRAY = new ArrayList<Integer>();
        NUMBER_IMAGES_ARRAY.add( R.drawable.image_1 );
        NUMBER_IMAGES_ARRAY.add( R.drawable.image_2 );
        NUMBER_IMAGES_ARRAY.add( R.drawable.image_3 );
        NUMBER_IMAGES_ARRAY.add( R.drawable.image_4 );
        NUMBER_IMAGES_ARRAY.add( R.drawable.image_5 );
        NUMBER_IMAGES_ARRAY.add( R.drawable.image_6 );
        NUMBER_IMAGES_ARRAY.add( R.drawable.image_7 );
        NUMBER_IMAGES_ARRAY.add( R.drawable.image_8 );
        NUMBER_IMAGES_ARRAY.add( R.drawable.image_9 );
        NUMBER_IMAGES_ARRAY.add( R.drawable.image_10 );
    }


    // Main method
    //-------------

    public static void Update_ui( Boolean can_undo ) {
        final Session session = Session.Get_instance();
        final Puzzle current_puzzle = session.Get_current_puzzle();
        final UnlocksDataAccess unlocks_dao = UnlocksDataAccess.Get_instance();

        boolean left_button_enabled;
        boolean right_button_enabled;

        if( session.Is_in_world_view() ) {
            left_button_enabled = unlocks_dao.Get_number_of_unlocked_worlds() != 1 &&
                                 ( ( unlocks_dao.Get_number_of_unlocked_worlds() == 2 && session.Get_current_world() == 2 ) ||
                                     unlocks_dao.Get_number_of_unlocked_worlds() > 2 );
            right_button_enabled = unlocks_dao.Get_number_of_unlocked_worlds() != 1 &&
                                ( ( unlocks_dao.Get_number_of_unlocked_worlds() == 2 && session.Get_current_world() == 1 ) ||
                                    unlocks_dao.Get_number_of_unlocked_worlds() > 2 );
        } else {
            left_button_enabled = unlocks_dao.Get_number_of_unlocked_levels( session.Get_current_world() ) != 1 &&
                                ( ( unlocks_dao.Get_number_of_unlocked_levels( session.Get_current_world() ) == 2 && session.Get_current_level() == 2 ) ||
                                    unlocks_dao.Get_number_of_unlocked_levels( session.Get_current_world() ) > 2 );
            right_button_enabled = unlocks_dao.Get_number_of_unlocked_levels( session.Get_current_world() ) != 1 &&
                                ( ( unlocks_dao.Get_number_of_unlocked_levels( session.Get_current_world() ) == 2 && session.Get_current_level() == 1 ) ||
                                    unlocks_dao.Get_number_of_unlocked_levels( session.Get_current_world() ) > 2 );
        }

        left_button.setEnabled( left_button_enabled );
        left_button.setAlpha( (left_button_enabled) ? 1 : BUTTON_FADE );

        right_button.setEnabled( right_button_enabled );
        right_button.setAlpha( (right_button_enabled) ? 1 : BUTTON_FADE );

        if ( can_undo != null ) { // If can_undo == null leave undo button as it was before
            undo_button.setEnabled( can_undo );
            undo_button.setAlpha( ( can_undo ) ? 1: BUTTON_FADE );

            reset_button.setEnabled( can_undo );
            reset_button.setAlpha( ( can_undo ) ? 1: BUTTON_FADE );
        }

        if ( ( current_puzzle.Get_erase_restriction() > 0 )
           || MetaDataAccess.Get_instance().Has_seen( MetaDataAccess.SEEN_ERASE ) ) {
            draw_button.setVisibility( View.VISIBLE );
            erase_button.setVisibility( View.VISIBLE );
        } else {
            draw_button.setVisibility( View.INVISIBLE );
            erase_button.setVisibility( View.INVISIBLE );
        }

        LinearLayout.LayoutParams state_params = (LinearLayout.LayoutParams) title_state.getLayoutParams();
        LinearLayout.LayoutParams number_params = (LinearLayout.LayoutParams) title_number.getLayoutParams();

        if( session.Is_in_world_view() ) {
            if( session.Get_current_world() >= 10 ) state_params.weight = 52f;
            else                                    state_params.weight = 40; // 25.66

            title_state.setImageResource( R.drawable.world );
            title_number.setImageResource( Get_number_image_resource( session.Get_current_world() ) );
            back_button.setImageResource( R.drawable.home );
        } else {
            if( session.Get_current_world() >= 10 ) state_params.weight = 56f;
            else                                    state_params.weight = 44;

            title_state.setImageResource( R.drawable.level );
            title_number.setImageResource( Get_number_image_resource( session.Get_current_level() ) );
            back_button.setImageResource( R.drawable.levels );
        }

        number_params.weight = 100 - state_params.weight;
        title_state.setLayoutParams( state_params );
        title_number.setLayoutParams( number_params );

        final boolean draw_border = OptionsDataAccess.Get_instance().Get_boolean_option( OptionsDataAccess.OPTION_BORDER ) ||
                                    Session.Get_instance().Get_current_puzzle().Is_border_forced();

        // Draw main color gradient
        final int[] colors = new int[2];
        colors[0] = session.Get_world_color();
        colors[1] = ( draw_border ) ? session.Get_world_color() : Color.TRANSPARENT;

        GradientDrawable gradient = new GradientDrawable( GradientDrawable.Orientation.TOP_BOTTOM, colors );
        gradient.setCornerRadius( 0f );
        if( android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN ) {
            top_bar.setBackgroundDrawable( gradient );
        } else {
            top_bar.setBackground( gradient );
        }
        top_bar.getBackground().setAlpha( draw_border ? 170 : 225 );

        gradient = new GradientDrawable( GradientDrawable.Orientation.BOTTOM_TOP, colors );
        gradient.setCornerRadius( 0f );
        if( android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN ) {
            bottom_bar.setBackgroundDrawable( gradient );
        } else {
            bottom_bar.setBackground( gradient );
        }
        bottom_bar.getBackground().setAlpha( draw_border ? 170 : 255 );
    }


    // Public methods
    //----------------

    /*
     * Sets all the game's ui dimensions, note: this is faster than doing it in xml
     */
    public static void Setup_ui() {
        Activity activity;
        if(Page.Is_Game_page()) {
            activity = GamePage.Get_activity();
            final Session session = Session.Get_instance();

            // Reset variable
            game_page = (RelativeLayout) activity.findViewById(R.id.page);

            top_bar = (RelativeLayout) activity.findViewById(R.id.top_bar);
            bottom_bar = (LinearLayout) activity.findViewById(R.id.bottom_bar);

            check_button = (ImageButton) activity.findViewById(R.id.Check);
            undo_button = (ImageButton) activity.findViewById(R.id.Undo);
            hint_button = (ImageButton) activity.findViewById(R.id.Hint);
            draw_button = (ImageButton) activity.findViewById(R.id.Draw);
            erase_button = (ImageButton) activity.findViewById(R.id.Erase);

            left_button = (ImageButton) activity.findViewById(R.id.Left);
            back_button = (ImageButton) activity.findViewById(R.id.Back);
            right_button = (ImageButton) activity.findViewById(R.id.Right);

            reset_button = (ImageButton) activity.findViewById(R.id.Reset);
            settings_button = (ImageButton) activity.findViewById(R.id.Settings);

            title = (LinearLayout) activity.findViewById(R.id.Title);
            title_state = (ImageView) activity.findViewById(R.id.Title_State);
            title_number = (ImageView) activity.findViewById(R.id.Title_number);

            // Set the buttons/layout width/height - 'Faster than doing it via xml'
            top_bar.getLayoutParams().height = BAR_HEIGHT;
            activity.findViewById(R.id.buttons).getLayoutParams().height = BAR_HEIGHT;
            title.getLayoutParams().width = TITLE_SIZE;

            check_button.getLayoutParams().width = BOTTOM_BUTTON_WIDTH;
            hint_button.getLayoutParams().width = BOTTOM_BUTTON_WIDTH;
            undo_button.getLayoutParams().width = BOTTOM_BUTTON_WIDTH;
            draw_button.getLayoutParams().width = BOTTOM_BUTTON_WIDTH;
            erase_button.getLayoutParams().width = BOTTOM_BUTTON_WIDTH;

            left_button.getLayoutParams().width = TOP_BUTTON_WIDTH;
            back_button.getLayoutParams().width = TOP_BUTTON_WIDTH;
            right_button.getLayoutParams().width = TOP_BUTTON_WIDTH;
            reset_button.getLayoutParams().width = TOP_BUTTON_WIDTH;
            settings_button.getLayoutParams().width = TOP_BUTTON_WIDTH;

            Highlight_current_mode();
            Set_touch_listener_highlight(draw_button, false);
            Set_touch_listener_highlight(erase_button, false);
            Set_touch_listener_highlight(left_button);
            Set_touch_listener_highlight(back_button);
            Set_touch_listener_highlight(right_button);
            Set_touch_listener_highlight(reset_button);
            Set_touch_listener_highlight(settings_button, false);
            Set_touch_listener_highlight(check_button);
            Set_touch_listener_highlight(undo_button);
            Set_touch_listener_highlight(hint_button, false);
        } else {
            activity = HomePage.Get_activity();
            Set_touch_listener_highlight((ImageButton) activity.findViewById(R.id.Mute));
            Set_touch_listener_highlight((ImageButton) activity.findViewById(R.id.Settings), false);
        }
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
    public static int Get_next_color( Integer color ) {
        if( color == null ) color = Color.DKGRAY;
        return COLOR_ARRAY.get( ( COLOR_MAP.get( color ) + 1 ) % COLOR_ARRAY.size() );
    }

    /*
     * Give a number and it will give the id for the drawable of that number (range: 1 - 10)
     *
     * @param int number: The number wanted
     *
     * @return int: The id of the drawable
     */
    public static int Get_number_image_resource( int number ) {
        return NUMBER_IMAGES_ARRAY.get( number - 1 );
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
     * @return LinearGradient: the linear gradient used for the horizontal grid lines
     */
    public static LinearGradient Get_horizontal_grid_gradient() {
        final int[] colors = new int[]{ Color.WHITE, Color.LTGRAY, Color.LTGRAY, Color.LTGRAY, Color.WHITE };
        final float[] positions = new float[]{ 0.0f, 0.05f, 0.5f, 0.95f, 1.0f };
        return new LinearGradient( 0, 0, SCREEN_SIZE.x, 0, colors, positions, Shader.TileMode.MIRROR );
    }

    /*
     * @return LinearGradient: the linear gradient used for the vertical grid lines
     */
    public static LinearGradient Get_vertical_grid_gradient() {
        final int[] colors = new int[]{ Color.WHITE, Color.LTGRAY, Color.WHITE };
        final float[] positions = new float[]{ 0.0f, (float) ( BAR_HEIGHT + CANVAS_SIZE / 2 ) / SCREEN_SIZE.y,
                                                     (float) ( CANVAS_SIZE + 2 * BAR_HEIGHT + CANVAS_MARGIN ) / SCREEN_SIZE.y };
        return new LinearGradient( 0, 0, 0, SCREEN_SIZE.y, colors, positions, Shader.TileMode.MIRROR );
    }

    /*
     * Resets all buttons so they are not highlighted (not including draw/erase)
     */
    public static void Reset_highlights() {
        left_button.setColorFilter( null );
        back_button.setColorFilter( null );
        right_button.setColorFilter( null );

        reset_button.setColorFilter( null );
        settings_button.setColorFilter( null );

        check_button.setColorFilter( null );
        undo_button.setColorFilter( null );
        hint_button.setColorFilter( null );
    }

    /*
     * Set's up a button so when touched glow's game color and when released stop's glowing
     *
     * @param final ImageButton button: Button of interest
     */
    public static void Set_touch_listener_highlight( final ImageButton button, final boolean revert ) {
        button.setOnTouchListener( new View.OnTouchListener() {
            @Override
            public boolean onTouch( View v, MotionEvent event ) {
                switch( event.getAction() ) {
                    case MotionEvent.ACTION_DOWN:
                        button.setColorFilter( Session.Get_instance().Get_hightlight_color(), PorterDuff.Mode.MULTIPLY );
                        break;

                    case MotionEvent.ACTION_UP:
                        if( revert ) button.setColorFilter( null );
                        break;

                    case MotionEvent.ACTION_MOVE:
                        final Rect bounds = new Rect();
                        button.getHitRect( bounds );
                        if( !bounds.contains(v.getLeft() + (int) event.getX(), v.getTop() + (int) event.getY()) ) {
                            button.setColorFilter( null );
                            if( Page.Is_Game_page() ) Highlight_current_mode();
                        }
                        break;
                }

                return false; // propagate listener to click
            }
        } );
    }

    public static void Set_touch_listener_highlight( final ImageButton button ) {
        Set_touch_listener_highlight( button, true );
    }


    // Private methods
    //-----------------

    /*
     * Methods use to temporarily show a small message at the bottom of the screen
     */
    private static void render_toast( int msg_id ) {
        if ( TOAST != null && TOAST.getView().getWindowVisibility() != View.VISIBLE ) {
            TOAST.setText( Page.Get_context().getResources().getString( msg_id ) );
            TOAST.show();
        }
    }
}
