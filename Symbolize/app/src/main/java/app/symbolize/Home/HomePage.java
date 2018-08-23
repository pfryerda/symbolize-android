package app.symbolize.Home;

import android.content.res.AssetFileDescriptor;
import android.graphics.PixelFormat;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import com.google.android.gms.ads.AdRequest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import app.symbolize.Common.MusicController;
import app.symbolize.Common.Session;
import app.symbolize.DataAccess.MetaDataAccess;
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
    // Constants
    //----------

    private static final short FADE_IN_LENGTH_SHORT = 600;
    private static final short FADE_IN_LENGTH_LONG = 1100;

    private static final short PULSE_DELAY = 7000;
    private static final short PULSE_LENGTH = 1150;


    // Fields
    //-------

    private boolean is_intro_done = false;
    private Animation current_animation = null;

    private FrameLayout placeholder;
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

        placeholder = (FrameLayout) findViewById( R.id.home_placeholder );
        placeholder.setVisibility( View.VISIBLE );

        surface_view = (SurfaceView) findViewById( R.id.surface );
        holder = surface_view.getHolder();
        holder.addCallback( this );
        holder.setType( SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS );
        mp = new MediaPlayer();

        Set_sound_image();

        animate_buttons();
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

        final AlphaAnimation alpha_animation = new AlphaAnimation( 1.0f, 0.0f );
        alpha_animation.setDuration( FADE_IN_LENGTH_SHORT );
        alpha_animation.setAnimationListener( new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}
            @Override
            public void onAnimationRepeat(Animation animation) {}
            @Override
            public void onAnimationEnd(Animation animation) {
                placeholder.setVisibility( View.GONE );
                placeholder.setVisibility( View.GONE );
            }
        } );

        player.setOnInfoListener( new MediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo( MediaPlayer player, int type, int extra ) {
                if ( type == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START ) {
                    placeholder.startAnimation( alpha_animation );
                    return true;
                }
                return false;
            }
        } );
        player.start();
        prepared = true;
    }


    // Private methods
    //-----------------

    private void animate_buttons() {
        is_intro_done = true;

        AnimationSet animation_set = new AnimationSet( true );
        animation_set.addAnimation( new AlphaAnimation( 0.0f, 1.0f ) );
        animation_set.addAnimation( new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, -0.125f, Animation.RELATIVE_TO_SELF, 0) );
        animation_set.setDuration( FADE_IN_LENGTH_LONG );

        AnimationSet main_animation_set = new AnimationSet( true );
        main_animation_set.addAnimation( new AlphaAnimation( 0.0f, 1.0f ) );
        main_animation_set.addAnimation( new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, -0.1f, Animation.RELATIVE_TO_SELF, 0 ) );
        main_animation_set.setDuration( FADE_IN_LENGTH_LONG );

        main_animation_set.setAnimationListener( new Animation.AnimationListener() {
            @Override
            public void onAnimationStart( Animation animation ) {
                byte currentWorld = MetaDataAccess.Get_instance().Get_last_world();
                if ( currentWorld == 2 ) {
                    ( (ImageButton) findViewById( R.id.Start ) ).setImageResource( R.drawable.icon_2 );
                } else if ( currentWorld == 3 ) {
                    ( (ImageButton) findViewById( R.id.Start ) ).setImageResource( R.drawable.icon_3 );
                } else if ( currentWorld == 4 ) {
                    ( (ImageButton) findViewById( R.id.Start ) ).setImageResource( R.drawable.icon_4 );
                } else if ( currentWorld == 5 ) {
                    ( (ImageButton) findViewById( R.id.Start ) ).setImageResource( R.drawable.icon_5 );
                } else {
                    ( (ImageButton) findViewById( R.id.Start ) ).setImageResource( R.drawable.icon_1 );
                }

                findViewById( R.id.Start ).setVisibility( View.VISIBLE );
                findViewById( R.id.Mute_bubble ).setVisibility( View.VISIBLE );
                findViewById( R.id.Settings_bubble ).setVisibility( View.VISIBLE );
                findViewById( R.id.Mute ).setVisibility( View.VISIBLE );
                findViewById( R.id.Settings ).setVisibility( View.VISIBLE );
            }

            @Override
            public void onAnimationRepeat( Animation animation ) {
            }

            @Override
            public void onAnimationEnd( Animation animation ) {
                intro_over();
            }
        });

        findViewById( R.id.Start ).startAnimation( main_animation_set );
        findViewById( R.id.Mute_bubble ).startAnimation( animation_set );
        findViewById( R.id.Settings_bubble ).startAnimation( animation_set );
        findViewById( R.id.Mute ).startAnimation( animation_set );
        findViewById( R.id.Settings ).startAnimation( animation_set );

        // Pulsing animation
        final ScaleAnimation pulse_out_animation = new ScaleAnimation( 1.0f, 1.08f, 1.0f, 1.08f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f );
        pulse_out_animation.setDuration( PULSE_LENGTH );
        final ScaleAnimation pulse_in_animation = new ScaleAnimation( 1.08f, 1.0f, 1.08f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f );
        pulse_in_animation.setDuration( PULSE_LENGTH );

        pulse_out_animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}
            @Override
            public void onAnimationEnd(Animation animation) {
                findViewById( R.id.Start ).startAnimation( pulse_in_animation );
            }
            @Override
            public void onAnimationRepeat(Animation animation) {}
        });

        pulse_in_animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}
            @Override
            public void onAnimationEnd(Animation animation) {
                findViewById( R.id.Start ).startAnimation( pulse_out_animation );
            }
            @Override
            public void onAnimationRepeat(Animation animation) {}
        });

        final Timer timer = new Timer();
        timer.schedule( new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        findViewById( R.id.Start ).startAnimation( pulse_out_animation );
                    }
                });
            }
        }, PULSE_DELAY );
    }

    public void intro_over() {
        if( current_animation != null ) {
            current_animation.setAnimationListener( null );
            current_animation.cancel();
        }
        final ImageButton icon = (ImageButton) findViewById( R.id.Start );
        icon.clearAnimation();
        icon.setVisibility( View.VISIBLE );

        findViewById( R.id.Mute_bubble ).setVisibility( View.VISIBLE );
        findViewById( R.id.Settings_bubble ).setVisibility( View.VISIBLE );
        findViewById( R.id.Mute ).setVisibility( View.VISIBLE );
        findViewById( R.id.Settings ).setVisibility( View.VISIBLE );

        is_intro_done = true;
    }

    // Button methods
    //----------------

    public void On_start_button_clicked(final View view) {
        if( is_intro_done ) {
            MusicController.PlaySound( this, MusicController.CLICK_SOUND );
            MusicController.Reset();
            Router.Route(this, GamePage.class);
        }
        else  {
            intro_over();
        }
    }

    public void On_mute_button_clicked(final View view) {
        OptionsDataAccess.Get_instance().Toggle_boolean_option( OptionsDataAccess.OPTION_IS_MUTED );
        Set_sound_image();
        MusicController.Set_volume();
    }

    public void On_settings_button_clicked(final View view) {
        OptionsDialog options_dialog = new OptionsDialog();
        options_dialog.Set_Button( (ImageButton) findViewById( R.id.Settings ) );
        options_dialog.Show();
        MusicController.PlaySound( this, MusicController.CLICK_SOUND );
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


    // Method for pausing/resuming the game
    //---------------------------------------

    @Override
    public void onPause() {
        super.onPause();

        if( mp != null && mp.isPlaying() ) mp.pause();
        MusicController.Pause();
    }

    @Override
    public void onResume() {
        super.onResume();

        if( mp != null && prepared ) mp.start();
        MusicController.Start( this, MusicController.MENU_MUSIC );
    }
}