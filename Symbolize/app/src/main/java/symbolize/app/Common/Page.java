package symbolize.app.Common;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import symbolize.app.DataAccess.MetaDataAccess;
import symbolize.app.DataAccess.OptionsDataAccess;
import symbolize.app.DataAccess.ProgressDataAccess;
import symbolize.app.DataAccess.UnlocksDataAccess;
import symbolize.app.Dialog.SymbolizeDialog;
import symbolize.app.Game.GameUIView;

/*
 * A simple interface to put common elements of all symbolize pages, as well as some generic static
 * methods to get commonly used elements like an activity, string, or context
 */
abstract public class Page extends FragmentActivity {
    // Constants
    //------------

    private static Context context;
    private static boolean current_page_is_game = false;


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
    protected void onPause() {
        super.onPause();

        final MetaDataAccess meta_dao = MetaDataAccess.Get_instance();
        meta_dao.Set_last_world();
        meta_dao.Set_last_draw_enabled();

        MetaDataAccess.Commit();
        OptionsDataAccess.Commit();
        ProgressDataAccess.Commit();
        UnlocksDataAccess.Commit();
    }
}
