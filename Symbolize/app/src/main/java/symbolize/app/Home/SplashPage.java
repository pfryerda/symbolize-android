package symbolize.app.Home;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import symbolize.app.Common.Page;
import symbolize.app.Game.GamePage;
import symbolize.app.R;

public class SplashPage extends Page {
    // Constants
    //-----------

    private static short SPLASH_TIME = 3000;


    // Main method
    //-------------

    @Override
    protected void onCreate( final Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_splash );
        Page.Set_not_game_page();

        new Handler().postDelayed( new Runnable() {
            @Override
            public void run() {
                findViewById( R.id.splash_ok_button ).setVisibility( View.VISIBLE );
            }
        }, SPLASH_TIME );
    }


    // Button methods
    //----------------

    public void On_ok_button_clicked( final View view ) {
        startActivity( new Intent ( getApplicationContext(), HomePage.class ) );
        finish();
    }
}
