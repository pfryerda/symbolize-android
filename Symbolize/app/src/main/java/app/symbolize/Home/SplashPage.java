package app.symbolize.Home;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.View;
import android.widget.ImageView;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.ads.AdRequest;

import app.symbolize.Common.MusicController;
import app.symbolize.DataAccess.MetaDataAccess;
import app.symbolize.DataAccess.OptionsDataAccess;
import app.symbolize.DataAccess.ProgressDataAccess;
import app.symbolize.DataAccess.UnlocksDataAccess;
import app.symbolize.Game.GamePage;
import app.symbolize.Routing.Page;
import app.symbolize.R;
import app.symbolize.Routing.Router;
import io.fabric.sdk.android.Fabric;

public class SplashPage extends Page {
    // Constants
    //-----------

    private static final short SPLASH_TIME = 3333;


    // Main method
    //-------------

    @Override
    protected void onCreate( final Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );

        Fabric.with(this, new Crashlytics());
        setContentView( R.layout.activity_splash );
        Page.Set_not_game_page();

        if( GamePage.Ad_request == null ) {
            GamePage.Ad_request = new AdRequest.Builder()
                    .addTestDevice( AdRequest.DEVICE_ID_EMULATOR )
                    .addTestDevice( Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID ) ) // TODO: Manually put in our device ids for security
                    .build();
        }

        final Page self = this;
        new Handler().postDelayed( new Runnable() {
            @Override
            public void run() {
                Router.Route( self, HomePage.class );
                finish();
            }
        }, SPLASH_TIME );
    }
}