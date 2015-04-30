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
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import com.google.android.gms.ads.AdRequest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import app.symbolize.Common.Session;
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

    private static final short INTRO_DURATION = 1250;
    private static final short FADE_IN_LENGTH_SHORT = 600;
    private static final short FADE_IN_LENGTH_LONG = 1250;
    private static final int[] INTRO_RES_IDS = new int[] {
            R.drawable.intro_text_1,
            R.drawable.intro_text_2,
            R.drawable.intro_text_3 };



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
        alpha_animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}
            @Override
            public void onAnimationRepeat(Animation animation) {}
            @Override
            public void onAnimationEnd(Animation animation) {
                placeholder.setVisibility( View.GONE );
                if( Session.Get_instance().Is_game_loaded() ) animate_buttons();
                else                                          intro_credits();
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
    }


    // Private methods
    //-----------------

    private void intro_credits() {
        final ImageButton icon = (ImageButton) findViewById( R.id.Start );
        final Timer timer = new Timer();

        final ArrayList<Animation> animations = new ArrayList<>();
        for( final int id : INTRO_RES_IDS ) {
            AlphaAnimation fade_in = new AlphaAnimation( 0.0f, 1.0f );
            fade_in.setDuration( FADE_IN_LENGTH_LONG );
            animations.add( fade_in );
        }

        for( int i = 0; i < INTRO_RES_IDS.length; ++i ) {
            final int id = INTRO_RES_IDS[i];
            final int index = i;
            final AlphaAnimation fade_out = new AlphaAnimation( 1.0f, 0.0f );
            fade_out.setDuration( FADE_IN_LENGTH_LONG );

            animations.get( i ).setAnimationListener( new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    current_animation = animation;
                    icon.setImageResource( id );
                    //icon.setVisibility( View.VISIBLE );
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    new Handler().postDelayed(
                        new Runnable() {
                              @Override
                              public void run() {
                                  icon.startAnimation(fade_out);
                              }
                        }, INTRO_DURATION );
                }

                @Override
                public void onAnimationRepeat(Animation animation) {}
            } );

            fade_out.setAnimationListener( new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    current_animation = animation;
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    //icon.setVisibility( View.INVISIBLE );
                    if(index == animations.size() - 1) animate_buttons();
                    else icon.startAnimation( animations.get( index + 1 ) );
                }

                @Override
                public void onAnimationRepeat(Animation animation) {}
            } );
        }

        icon.setVisibility( View.VISIBLE );
        icon.startAnimation( animations.get( 0 ) );
    }

    private void animate_buttons() {
        is_intro_done = true;

        AnimationSet animation_set = new AnimationSet( true );
        animation_set.addAnimation( new AlphaAnimation( 0.0f, 1.0f ) );
        animation_set.addAnimation( new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, -0.125f, Animation.RELATIVE_TO_SELF, 0 ) );
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
                ( (ImageButton) findViewById( R.id.Start ) ).setImageResource( R.drawable.icon );
                findViewById( R.id.Start ).setVisibility( View.VISIBLE );
                findViewById( R.id.Mute_bubble ).setVisibility( View.VISIBLE );
                findViewById( R.id.Settings_bubble ).setVisibility( View.VISIBLE );
                findViewById( R.id.Mute ).setVisibility( View.VISIBLE );
                findViewById( R.id.Settings ).setVisibility( View.VISIBLE );
            }
            @Override
            public void onAnimationRepeat( Animation animation ) {}

            @Override
            public void onAnimationEnd( Animation animation ) {
                intro_over();
            }
        } );

        findViewById( R.id.Start ).startAnimation( main_animation_set );
        findViewById( R.id.Mute_bubble ).startAnimation( animation_set );
        findViewById( R.id.Settings_bubble ).startAnimation( animation_set );
        findViewById( R.id.Mute ).startAnimation( animation_set );
        findViewById( R.id.Settings ).startAnimation( animation_set );
    }

    public void intro_over() {
        if( current_animation != null ) {
            current_animation.setAnimationListener( null );
            current_animation.cancel();
        }
        final ImageButton icon = (ImageButton) findViewById( R.id.Start );
        icon.clearAnimation();
        icon.setImageResource( R.drawable.icon );
        icon.setVisibility( View.VISIBLE );

        findViewById( R.id.Mute_bubble ).setVisibility( View.VISIBLE );
        findViewById( R.id.Settings_bubble ).setVisibility( View.VISIBLE );
        findViewById( R.id.Mute ).setVisibility( View.VISIBLE );
        findViewById( R.id.Settings ).setVisibility( View.VISIBLE );

        is_intro_done = true;
        Session.Get_instance().Game_loaded();
    }

    // Button methods
    //----------------

    public void On_start_button_clicked(final View view) {
        if( is_intro_done ) Router.Route( getApplicationContext(), GamePage.class );
        else                intro_over();
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