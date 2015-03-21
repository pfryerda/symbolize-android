package app.symbolize.Routing;

import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.View;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;

import app.symbolize.Game.GamePage;
import app.symbolize.R;

public class LoadingPage extends Page {
    // Constants
    //-----------

    // Ironically having this too low (or not all) causes the 'loading' screen not to load or glitch
    private final short LOADING_TIME = 400;


    // Static fields
    //---------------

    public static Class next_page;


    // Main method
    //-------------

    @Override
    protected void onCreate( final Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_loading );

        if( GamePage.Ad_request == null ) {
            GamePage.Ad_request = new AdRequest.Builder()
                    .addTestDevice( AdRequest.DEVICE_ID_EMULATOR )
                    .addTestDevice( Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID ) ) // TODO: Manually put in our device ids for security
                    .build();
        }

        new Handler().postDelayed( new Runnable() {
            @Override
            public void run() {
                Router.Direct_route( getApplicationContext(), next_page );
                next_page = null;
            }
        }, LOADING_TIME );
    }
}
