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
     * Methods used to send the user the loading page and save the next page to be sent to
     */
    public static void Route( final Context current_page, final Class new_page ) {
        LoadingPage.next_page = new_page;
        Direct_route( current_page, LoadingPage.class );
    }

    /*
     * Method called if you want to skip the loading page all together
     */
    public static void Direct_route( final Context current_page, final Class new_page ) {
        current_page.startActivity( get_intent( current_page, new_page ) );
        Page.Get_activity().overridePendingTransition( R.anim.fade_in, R.anim.fade_out );
    }


    // Private methods
    //-----------------

    /*
     * Method used to build intent object, abstracts away setting flags
     */
    private static Intent get_intent( final Context current_page, final Class new_page ) {
        final Intent intent = new Intent( current_page, new_page );
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        return intent;
    }
}
