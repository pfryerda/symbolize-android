package app.symbolize.Home;

import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageButton;

import java.io.IOException;

import app.symbolize.Game.GameUIView;
import app.symbolize.Routing.Page;
import app.symbolize.DataAccess.OptionsDataAccess;
import app.symbolize.Dialog.OptionsDialog;
import app.symbolize.Game.GamePage;
import app.symbolize.R;
import app.symbolize.Routing.Router;



/*
 * The main class in charge of setting up the home page as well as responding to client interactions on the home page
 */
public class HomePage extends Page implements SurfaceHolder.Callback, MediaPlayer.OnPreparedListener {
    // Fields
    //-------

    private MediaPlayer mp = null;
    private SurfaceView surface_view = null;
    private SurfaceHolder holder = null;
    private boolean prepared = false;


    // Main method
    //-------------

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Page.Set_not_game_page();
        GameUIView.Setup_ui();

        surface_view = (SurfaceView) findViewById( R.id.surface );
        holder = surface_view.getHolder();
        holder.addCallback(this);
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        mp = new MediaPlayer();

        Set_sound_image();
    }

    // Button methods
    //----------------

    public void On_start_button_clicked(final View view) {
        Router.Route(getApplicationContext(), GamePage.class);
    }

    public void On_mute_button_clicked(final View view) {
        OptionsDataAccess.Get_instance().Toggle_boolean_option( OptionsDataAccess.OPTION_IS_MUTED );
        Set_sound_image();
    }

    public void On_settings_button_clicked(final View view) {
        OptionsDialog options_dialog = new OptionsDialog();
        options_dialog.Set_Button( (ImageButton) findViewById( R.id.Settings ) );
        options_dialog.Show();
    }


    // Static methods
    //----------------

    public static void Set_sound_image() {
        if ( OptionsDataAccess.Get_instance().Get_boolean_option( OptionsDataAccess.OPTION_IS_MUTED ) ) {
            ( (ImageButton) HomePage.Get_activity().findViewById( R.id.Mute ) ).setImageResource( R.drawable.mute );
        } else {
            ( (ImageButton) HomePage.Get_activity().findViewById( R.id.Mute ) ).setImageResource( R.drawable.sound );
        }
    }

    // Interface methods
    //-------------------

    @Override
    public void surfaceCreated( SurfaceHolder holder ) {
        mp.setDisplay( holder );

        float density = getResources().getDisplayMetrics().density;
        int raw_id = R.raw.home_mdpi;
        if     ( density <= 1.25 )                   raw_id = R.raw.home_mdpi;
        else if( 1.25 < density && density <= 1.75 ) raw_id = R.raw.home_hdpi;
        else if( 1.75 < density && density <= 2.5 )  raw_id = R.raw.home_xhdpi;
        else if( 2.5 < density )                     raw_id = R.raw.home_xxhdpi;

        final Uri video = Uri.parse( "android.resource://" + getPackageName() + "/" + raw_id );

        mp.setLooping( true );
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