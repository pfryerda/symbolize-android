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

    public static final int POP_SOUND = 10;
    public static final int ERASE_SOUND = 11;
    public static final int SWIWSH_SOUND = 12;
    public static final int SWISH2_SOUND = 13;
    public static final int CORRECT_SOUND = 14;
    public static final int CLICK_SOUND = 15;

    // Private fields
    //-----------------

    private static AudioManager audio_manager = (AudioManager) Page.Get_context().getSystemService( Context.AUDIO_SERVICE );
    private static HashMap music_players = new HashMap();
    private static int currentMusic = -1;

    private static HashMap sound_players = new HashMap();

    // Public methods
    //---------------

    public static void PlaySound( Context context, int sound ) {
        if( music_disabled ) return;

        MediaPlayer mp = (MediaPlayer) sound_players.get( sound );
        if ( mp == null ) {
            switch ( sound ) {
                case POP_SOUND:
                    mp = MediaPlayer.create( context, R.raw.pop );
                    break;

                case ERASE_SOUND:
                    mp = MediaPlayer.create( context, R.raw.erase );
                    break;

                case SWIWSH_SOUND:
                    mp = MediaPlayer.create( context, R.raw.swish1 );
                    break;

                case SWISH2_SOUND:
                    mp = MediaPlayer.create( context, R.raw.swish2 );
                    break;

                case CORRECT_SOUND:
                    mp = MediaPlayer.create( context, R.raw.complete );
                    break;

                case CLICK_SOUND:
                    mp = MediaPlayer.create( context, R.raw.click );
                    break;

                default:
                    return;
            }

            sound_players.put( sound, mp );
        }

        if ( mp.isPlaying() ) {
            mp.pause();
            mp.seekTo( 0 );
        }


        final  boolean mute = OptionsDataAccess.Get_instance().Get_boolean_option( OptionsDataAccess.OPTION_IS_MUTED );
        if( mute ) {
            mp.setVolume( 0, 0 );
        } else {
            final float volume = (float) OptionsDataAccess.Get_instance().Get_short_option( OptionsDataAccess.OPTION_VOLUME_SOUND ) / 1000;
            mp.setVolume( volume, volume );
        }

        mp.start();
    }

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
        MediaPlayer mp = (MediaPlayer) music_players.get( music );
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

            music_players.put( music, mp );

            if ( mp != null ) {
                mp.setLooping( true );
                mp.start();
            }

        }

        Set_volume();
    }

    /*
     * Simple method for pausing the current music.
     */
    public static void Pause() {
        if( music_disabled ) return;
        Collection<MediaPlayer> mps = music_players.values();
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
        Collection<MediaPlayer> mps = music_players.values();
        for ( MediaPlayer mp : mps ) {
            if ( mp.isPlaying() ) {
                mp.pause();
                mp.seekTo( 0 );
            }
        }

        currentMusic = -1;
    }

    /*
     * Simple method for adjusting the music volume.
     */
    public static void Set_volume() {
        if( music_disabled ) return;
        MediaPlayer mp = (MediaPlayer) music_players.get( currentMusic );

        final  boolean mute = OptionsDataAccess.Get_instance().Get_boolean_option( OptionsDataAccess.OPTION_IS_MUTED );
        if( mute ) {
            mp.setVolume( 0, 0 );
        } else {
            final float volume = (float) OptionsDataAccess.Get_instance().Get_short_option( OptionsDataAccess.OPTION_VOLUME_MUSIC ) / 1000;
            mp.setVolume( volume, volume );
        }
    }
}