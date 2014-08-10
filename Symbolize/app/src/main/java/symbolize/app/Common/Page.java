package symbolize.app.Common;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;


public class Page extends FragmentActivity {
    // Static fields
    //---------------

    private static Context context;
    private static FragmentManager fragmentManager;


    // Main method
    //-------------

    protected void onCreate( final Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        Page.context = this;
        Page.fragmentManager = getFragmentManager();
    }


    // Static methods
    //----------------

    public static Context Get_context() {
        return context;
    }

    // BE CAREFUL WITH THIS METHOD
    public static Activity Get_activity() {
        return (Activity) context;
    }

    public static String Get_resource_string( int id ) {
        return context.getString( id );
    }

    public static FragmentManager Get_dialog_manager() {
        return fragmentManager;
    }


    // Used to fix bug, bug from API Level > 11
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //No call for super(). Bug on API Level > 11.
    }

}
