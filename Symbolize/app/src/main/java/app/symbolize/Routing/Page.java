package app.symbolize.Routing;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;

import app.symbolize.Common.MusicController;
import app.symbolize.DataAccess.MetaDataAccess;
import app.symbolize.DataAccess.OptionsDataAccess;
import app.symbolize.DataAccess.ProgressDataAccess;
import app.symbolize.DataAccess.UnlocksDataAccess;
import app.symbolize.Dialog.SymbolizeDialog;
import app.symbolize.Game.GameUIView;
import app.symbolize.R;

/*
 * A simple interface to put common elements of all symbolize pages, as well as some generic static
 * methods to get commonly used elements like an activity, string, or context
 */
abstract public class Page extends FragmentActivity {
    // Constants
    //------------

    private static Context context;
    private static boolean current_page_is_game = false;


    // Feilds
    //--------

    public boolean continue_music;


    // Static methods
    //----------------

    /*
     * @return boolean: true if game page, false otherwise
     */
    public static boolean Is_Game_page() {
        return current_page_is_game;
    }

    /*
     * Set the current page to game
     */
    public static void Set_game_page() {
        current_page_is_game = true;
    }

    /*
     * Set the current page to not game
     */
    public static void Set_not_game_page() {
        current_page_is_game = false;
    }

    /*
     * Get attributes
     */
    public static WindowManager.LayoutParams Get_attributes() {
        return Get_window().getAttributes();
    }

    /*
     * Get window
     */
    public static Window Get_window() {
        return Get_activity().getWindow();
    }

    /*
     * Get the page context
     */
    public static Context Get_context() {
        return context;
    }

    /*
     * Get the page activity
     */
    public static Activity Get_activity() {
        return (Activity) context;
    }

    /*
     * Get a string from res/values/strings
     */
    public static String Get_resource_string( int id ) {
        return context.getString( id );
    }

    /*
       Used to fix bug, bug from API Level > 11
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //No call for super(). Bug on API Level > 11.
    }


    // Main method
    //-------------

    protected void onCreate( final Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );

        Page.context = this;
        SymbolizeDialog.dialog_manager = getFragmentManager();

        final OptionsDataAccess options_dao = OptionsDataAccess.Get_instance();

        GameUIView.Set_brightness( options_dao.Get_short_option( OptionsDataAccess.OPTION_BRIGHTNESS ) );
    }


    // Method for pausing/resuming the game
    //---------------------------------------

    @Override
    protected void onResume() {
        super.onResume();

        continue_music = false;
        MusicController.Start( this );
    }

    @Override
    protected void onPause() {
        super.onPause();

        final MetaDataAccess meta_dao = MetaDataAccess.Get_instance();
        meta_dao.Set_last_world();
        meta_dao.Set_last_draw_enabled();

        MetaDataAccess.Commit();
        OptionsDataAccess.Commit();
        ProgressDataAccess.Commit();
        UnlocksDataAccess.Commit();

        if( !continue_music ) MusicController.Pause();
    }
}
