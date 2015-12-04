package app.symbolize.Common;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;

import java.util.Collection;
import java.util.HashMap;

import app.symbolize.DataAccess.OptionsDataAccess;
import app.symbolize.R;
import app.symbolize.Routing.Page;

public class MusicController {
    // Constants
    //----------

    public static final int MUSIC_PREVIOUS = -1;
    public static final int MENU_MUSIC = 0;
    public static final int WORLD_1 = 1;
    public static final int WORLD_2 = 2;
    public static final int WORLD_3 = 3;
    public static final int WORLD_4 = 4;
    public static final int WORLD_5 = 5;


    // Private fields
    //-----------------

    private static HashMap players = new HashMap();
    private static int currentMusic = -1;
    private static int previousMusic = -1;


    // Public methods
    //---------------

    public static void Start( Context context, int music ) {
        if ( currentMusic > -1 ) {
            // already playing some music and not forced to change
            return;
        }

        if ( music == MUSIC_PREVIOUS ) {
            music = previousMusic;
        }
        if ( currentMusic == music ) {
            // already playing this music
            return;
        }
        if ( currentMusic != -1 ) {
            previousMusic = currentMusic;
            // playing some other music, pause it and change
            Pause();
        }
        currentMusic = music;
        MediaPlayer mp = (MediaPlayer) players.get( music );
        if ( mp != null ) {
            if ( !mp.isPlaying() ) {
                mp.start();
            }
        } else {
            switch ( music ) {
                case WORLD_2:
                    mp = MediaPlayer.create( context, R.raw.onepointwofive );
                    break;

                default:
                    return; // Nothing to do here.
            }

            players.put( music, mp );
            Set_output();
            Set_volume();
            if ( mp != null ) {
                mp.setLooping( true );
                mp.start();
            }

        }
    }

    /*
     * Simple method for pausing the current music.
     */
    public static void Pause() {
        Collection<MediaPlayer> mps = players.values();
        for ( MediaPlayer mp : mps ) {
            if ( mp.isPlaying() ) {
                mp.pause();
            }
        }
        // previousMusic should always be something valid
        if ( currentMusic != -1 ) {
            previousMusic = currentMusic;
        }
        currentMusic = -1;
    }

    /*
     * Simple method for pausing the current music and resting back to start.
     */
    public static void Reset() {
        Collection<MediaPlayer> mps = players.values();
        for ( MediaPlayer mp : mps ) {
            if ( mp.isPlaying() ) {
                mp.pause();
                mp.seekTo( 0 );
            }
        }
        // previousMusic should always be something valid
        if ( currentMusic != -1 ) {
            previousMusic = currentMusic;
        }
        currentMusic = -1;
    }

    /*
     * Simple method for adjusting the volume.
     */
    public static void Set_volume() {
        MediaPlayer mp = (MediaPlayer) players.get( currentMusic );

        final  boolean mute = OptionsDataAccess.Get_instance().Get_boolean_option( OptionsDataAccess.OPTION_IS_MUTED );
        if( mute ) {
            mp.setVolume( 0, 0 );
        } else {
            final float volume = (float) OptionsDataAccess.Get_instance().Get_short_option( OptionsDataAccess.OPTION_VOLUME ) / 1000;
            mp.setVolume( volume, volume );
        }
    }

    /*
     * Simple method for setting the audio output method.
     */
    public static void Set_output() {
        final short output = OptionsDataAccess.Get_instance().Get_short_option( OptionsDataAccess.OPTION_AUDIO_OUTPUT );
        AudioManager audio_manager = (AudioManager) Page.Get_context().getSystemService( Context.AUDIO_SERVICE );

        switch ( output ) {
            case OptionsDataAccess.AUDIO_SPEAKERS:
                audio_manager.setMode( AudioManager.MODE_IN_CALL );
                audio_manager.setSpeakerphoneOn( true );
                break;

            case OptionsDataAccess.AUDIO_HEADPHONES:
                audio_manager.setMode( AudioManager.MODE_IN_CALL );
                audio_manager.setSpeakerphoneOn( false );
                break;

            default:
                audio_manager.setMode( AudioManager.MODE_NORMAL );
                audio_manager.setSpeakerphoneOn( true );
                break;
        }
    }
}