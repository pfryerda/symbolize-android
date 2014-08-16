package symbolize.app.Common;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import symbolize.app.Dialog.SymbolizeDialog;

/*
 * A simple interface to put common elements of all symbolize pages, as well as some generic static
 * methods to get commonly used elements like an activity, string, or context
 */
public class Page extends FragmentActivity {
    // Static fields
    //---------------

    private static Context context;


    // Main method
    //-------------

    protected void onCreate( final Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        Page.context = this;
        SymbolizeDialog.dialog_manager = getFragmentManager();
    }


    // Static methods
    //----------------

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

}
