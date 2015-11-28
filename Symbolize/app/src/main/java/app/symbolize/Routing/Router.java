package app.symbolize.Routing;

import android.content.Context;
import android.content.Intent;
import app.symbolize.Game.GamePage;
import app.symbolize.Home.HomePage;
import app.symbolize.R;

/**
 * Classed use for switching between pages
 */
abstract public class Router {
    // Main methods
    //--------------

    /*
     * Methods used to send the user the loading page and save the next page to be sent to and make sure music does not restart
     */
    public static void Route( final Page current_page, final Class new_page ) {
        current_page.continue_music = true;
        Route( current_page.getApplicationContext(), new_page );
    }

    /*
     * Methods used to send the user the loading page and save the next page to be sent to
     */
    public static void Route( final Context context, final Class new_page ) {
        context.startActivity( get_intent( context, new_page ) );
        Page.Get_activity().overridePendingTransition( (new_page != HomePage.class) ? R.anim.fade_in : R.anim.fade_in_fast, R.anim.fade_out );
    }

    // Private methods
    //-----------------

    /*
     * Method used to build intent object, abstracts away setting flags
     */
    private static Intent get_intent( final Context current_page, final Class new_page ) {
        final Intent intent = new Intent( current_page, new_page );
        intent.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK );

        return intent;
    }
}
