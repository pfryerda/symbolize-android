package symbolize.app.Common;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;


public class SymbolizeActivity extends FragmentActivity {
    // Static fields
    //---------------

    private static Context context;


    // Main method
    //-------------

    protected void onCreate( final Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        SymbolizeActivity.context = this;
    }


    // Static methods
    //----------------

    public static Context Get_context() {
        return context;
    }

    public static String Get_resource_string( int id ) {
        return context.getString( id );
    }

}
