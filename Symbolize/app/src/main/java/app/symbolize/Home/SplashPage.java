package app.symbolize.Home;

import android.content.Intent;
import android.graphics.PixelFormat;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.io.IOException;

import app.symbolize.Game.GamePage;
import app.symbolize.Routing.Page;
import app.symbolize.R;
import app.symbolize.Routing.Router;

public class SplashPage extends Page  implements SurfaceHolder.Callback, MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener {
    // Fields
    //-------

    private MediaPlayer mp = null;
    private SurfaceView surface_view = null;
    private SurfaceHolder holder = null;
    private boolean prepared = false;


    // Main method
    //-------------

    @Override
    protected void onCreate( final Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_splash );
        Page.Set_not_game_page();

        surface_view = (SurfaceView) findViewById( R.id.surface );
        holder = surface_view.getHolder();
        holder.addCallback(this);
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        mp = new MediaPlayer();

        if( GamePage.Ad_request == null ) {
            GamePage.Ad_request = new AdRequest.Builder()
                    .addTestDevice( AdRequest.DEVICE_ID_EMULATOR )
                    .addTestDevice( Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID ) ) // TODO: Manually put in our device ids for security
                    .build();
        }
    }


    // Interface methods
    //-------------------

    @Override
    public void surfaceCreated( SurfaceHolder holder ) {
        mp.setDisplay( holder );

        float density = getResources().getDisplayMetrics().density;
        int raw_id = R.raw.home_mdpi;
        if     ( density <= 1.25 )                   raw_id = R.raw.splash_mdpi;
        else if( 1.25 < density && density <= 1.75 ) raw_id = R.raw.splash_hdpi;
        else if( 1.75 < density && density <= 2.5 )  raw_id = R.raw.splash_xhdpi;
        else if( 2.5 < density )                     raw_id = R.raw.splash_xxhdpi;

        final Uri video = Uri.parse( "android.resource://" + getPackageName() + "/" + raw_id );

        mp.setOnCompletionListener( this );
        mp.setOnPreparedListener( this );


        try {
            mp.setDataSource( getApplicationContext(), video );
            prepared = false;
            mp.prepare();
        }
        catch ( IllegalArgumentException e ) { e.printStackTrace(); }
        catch ( IllegalStateException e )    { e.printStackTrace(); }
        catch ( IOException e )              { e.printStackTrace(); }
    }

    @Override
    public void surfaceDestroyed( SurfaceHolder holder ) {
        if( mp.isPlaying() ) mp.stop();
    }

    @Override
    public void surfaceChanged( SurfaceHolder holder, int format, int width, int height ) {}

    @Override
    public void onPrepared( MediaPlayer player ) {
        prepared = true;
        player.start();
    }

    @Override
    public void onCompletion( MediaPlayer player ) {
        Router.Route( getApplicationContext(), HomePage.class );
        finish();
    }


    // Method for pausing/resuming the game
    //---------------------------------------

    @Override
    public void onPause() {
        super.onPause();

        if( mp != null && mp.isPlaying() ) mp.pause();
    }

    @Override
    public void onResume() {
        super.onResume();

        if( mp != null && prepared ) mp.start();
    }
}
