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

    public static final boolean music_disabled = false;

    public static final int MENU_MUSIC = 0;
    public static final int GAME_MUSIC = 1;


    // Private fields
    //-----------------

    private static AudioManager audio_manager = (AudioManager) Page.Get_context().getSystemService( Context.AUDIO_SERVICE );
    private static HashMap players = new HashMap();
    private static int currentMusic = -1;


    // Public methods
    //---------------

    public static void Start( Context context, int music ) {
        if( music_disabled ) return;
        if ( currentMusic > -1 ) {
            // already playing some music and not forced to change
            return;
        }

        if ( currentMusic == music ) {
            // already playing this music
            return;
        }

        currentMusic = music;
        MediaPlayer mp = (MediaPlayer) players.get( music );
        if ( mp != null ) {
            if ( !mp.isPlaying() ) {
                mp.start();
            }
        } else {
            switch ( music ) {
                case MENU_MUSIC:
                    mp = MediaPlayer.create( context, R.raw.puzzlepieces );
                    break;

                case GAME_MUSIC:
                    mp = MediaPlayer.create( context, R.raw.andsothen );
                    break;

                default:
                    return;
            }

            players.put( music, mp );

            if ( mp != null ) {
                mp.setLooping( true );
                mp.start();
            }

        }

        Set_output();
        Set_volume();
    }

    /*
     * Simple method for pausing the current music.
     */
    public static void Pause() {
        if( music_disabled ) return;
        Collection<MediaPlayer> mps = players.values();
        for ( MediaPlayer mp : mps ) {
            if ( mp.isPlaying() ) {
                mp.pause();
            }
        }

        currentMusic = -1;
    }

    /*
     * Simple method for pausing the current music and resting back to start.
     */
    public static void Reset() {
        if( music_disabled ) return;
        Collection<MediaPlayer> mps = players.values();
        for ( MediaPlayer mp : mps ) {
            if ( mp.isPlaying() ) {
                mp.pause();
                mp.seekTo( 0 );
            }
        }

        currentMusic = -1;
    }

    /*
     * Simple method for adjusting the volume.
     */
    public static void Set_volume() {
        if( music_disabled ) return;
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
        if( music_disabled ) return;
        final short output = OptionsDataAccess.Get_instance().Get_short_option( OptionsDataAccess.OPTION_AUDIO_OUTPUT );

        switch ( output ) {
            case OptionsDataAccess.AUDIO_SPEAKERS:
                audio_manager.setMode( AudioManager.MODE_IN_CALL );
                audio_manager.setSpeakerphoneOn( true );
                break;

            case OptionsDataAccess.AUDIO_HEADPHONES:
                // ?
                break;

            default:
                audio_manager.setMode( AudioManager.MODE_NORMAL );
                audio_manager.setSpeakerphoneOn( true );
                break;
        }
    }
}