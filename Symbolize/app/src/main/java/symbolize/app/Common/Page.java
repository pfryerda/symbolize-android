package symbolize.app.Common;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import symbolize.app.Dialog.SymbolizeDialog;


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

    public static Context Get_context() {
        return context;
    }

    public static Activity Get_activity() {
        return (Activity) context;
    }

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
