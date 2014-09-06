package symbolize.app.DataAccess;

import symbolize.app.Common.Communication.Request;
import symbolize.app.Common.Communication.Response;
import symbolize.app.Routing.Page;
import symbolize.app.Game.GameController;
import symbolize.app.Game.GameUIView;
import symbolize.app.Game.GameView;
import symbolize.app.R;

/*
 * An all static data access API, for save data about your settings
 */
public class OptionsDataAccess {
    // Flags
    //------

    public static final byte OPTION_GRID                  = 0;
    public static final byte OPTION_BORDER                = 1;
    public static final byte OPTION_SNAP_DRAWING          = 2;
    public static final byte OPTION_SHOW_ANIMATIONS       = 3;
    public static final byte OPTION_IS_MUTED              = 4;
    public static final byte OPTION_USE_DEVICE_BRIGHTNESS = 5;

    public static final byte OPTION_VOLUME          = 6;
    public static final byte OPTION_GAME_SIZE       = 7;
    public static final byte OPTION_BRIGHTNESS      = 8;
    public static final byte OPTION_AUDIO_OUTPUT    = 9;


    public static final byte AUDIO_AUTO       = 0;
    public static final byte AUDIO_SPEAKERS   = 1;
    public static final byte AUDIO_HEADPHONES = 2;


    // Constants
    //-----------

    public static final short GAME_SIZE_MIN = 75;

    public static final short BRIGHTNESS_SCALING = 10000;
    public static final byte MIN_BRIGHTNESS = 100;

    public static final short DEFAULT_AUDIO_OUTPUT = AUDIO_AUTO;
    public static final byte DEFAULT_VOLUME = 100;
    public static final short DEFAULT_BRIGHTNESS = (short) ( BRIGHTNESS_SCALING / 2 );
    public static final short DEFAULT_GAME_SIZE = (short) 100;

    // Static fields
    //--------------

    private static final DataAccessObject dao = new DataAccessObject( R.string.preference_settings_key );
    private static int[] option_id_map = new int[10];


    // Static block
    //--------------

    static {
        option_id_map[OPTION_GRID] = R.string.grid_settings;
        option_id_map[OPTION_BORDER] = R.string.border_settings;
        option_id_map[OPTION_SNAP_DRAWING] = R.string.snap_settings;
        option_id_map[OPTION_SHOW_ANIMATIONS] = R.string.animation_settings;
        option_id_map[OPTION_IS_MUTED] = R.string.mute_settings;
        option_id_map[OPTION_USE_DEVICE_BRIGHTNESS] = R.string.use_device_brightness_settings;

        option_id_map[OPTION_VOLUME] = R.string.volume_settings;
        option_id_map[OPTION_GAME_SIZE] = R.string.game_size_settings;
        option_id_map[OPTION_BRIGHTNESS] = R.string.brightness_settings;
        option_id_map[OPTION_AUDIO_OUTPUT] = R.string.audio_output_settings;
    }


    // Static methods
    //----------------

    /*
     * Commit changes to disk
     */
    public static void Commit() {
        dao.Commit();
    }


    // Fields
    //--------

    private final byte SHORT_OFFSET;

    private boolean[] boolean_options = new boolean[6];
    private short[] short_options = new short[4];


    // Singleton setup
    //-----------------

    private static final OptionsDataAccess instance = new OptionsDataAccess();

    public static OptionsDataAccess Get_instance() {
        return instance;
    }


    // Constructor
    //-------------

    private OptionsDataAccess() {
        boolean_options[OPTION_GRID] =  dao.Get_property(
                Page.Get_resource_string( R.string.grid_settings ), true );
        boolean_options[OPTION_BORDER] = dao.Get_property(
                Page.Get_resource_string( R.string.border_settings ), false );
        boolean_options[OPTION_SNAP_DRAWING] = dao.Get_property(
                Page.Get_resource_string( R.string.snap_settings ), false );
        boolean_options[OPTION_SHOW_ANIMATIONS] = dao.Get_property(
                Page.Get_resource_string( R.string.animation_settings ), true );
        boolean_options[OPTION_IS_MUTED] = dao.Get_property(
                Page.Get_resource_string( R.string.mute_settings ), false );
        boolean_options[OPTION_USE_DEVICE_BRIGHTNESS] = dao.Get_property(
                Page.Get_resource_string( R.string.use_device_brightness_settings ), true );

        SHORT_OFFSET = (byte) boolean_options.length;
        short_options[OPTION_VOLUME - SHORT_OFFSET] = (short) dao.Get_property(
                Page.Get_resource_string( R.string.volume_settings ), DEFAULT_VOLUME );
        short_options[OPTION_GAME_SIZE - SHORT_OFFSET] = (short) dao.Get_property(
                Page.Get_resource_string( R.string.game_size_settings ), DEFAULT_GAME_SIZE );
        short_options[OPTION_BRIGHTNESS - SHORT_OFFSET] = (short) dao.Get_property(
                Page.Get_resource_string( R.string.brightness_settings ), DEFAULT_BRIGHTNESS );
        short_options[OPTION_AUDIO_OUTPUT - SHORT_OFFSET] = (short) dao.Get_property(
                Page.Get_resource_string( R.string.audio_output_settings ), DEFAULT_AUDIO_OUTPUT );
    }


    // Getter methods
    //---------------

    public boolean Get_boolean_option( final byte option ) {
        return boolean_options[option];
    }

    public short Get_short_option( final byte option ) {
        return short_options[option - SHORT_OFFSET];
    }


    // Setter methods
    //----------------

    public void Toggle_boolean_option( final byte option ) {
        boolean new_option = !Get_boolean_option( option );
        boolean_options[option] = new_option;
        dao.Set_property(
                Page.Get_context().getString( option_id_map[option] ), new_option );
    }

    public void Set_boolean_option( final byte option, final boolean new_option ) {
        boolean_options[option] = new_option;
        dao.Set_property(
                Page.Get_context().getString( option_id_map[option] ), new_option );
    }

    public void Set_short_option( final byte option, final short new_value ) {
        short_options[option - SHORT_OFFSET] = new_value;
        dao.Set_property(
                Page.Get_context().getString( option_id_map[option] ), new_value );
    }


    // Public methods
    //----------------

    public void Reset_game_options() {
        Set_boolean_option( OPTION_GRID, true );
        Set_boolean_option( OPTION_BORDER, false );
        Set_boolean_option( OPTION_SNAP_DRAWING, false );

        if ( Page.Is_Game_page() ) {
            GameController.Get_instance().Handle_request( new Request( Request.Background_change ),
                                                          new Response() );
        }
    }

    public void Reset_video_options() {
        Set_boolean_option( OPTION_SHOW_ANIMATIONS, true );
        Set_short_option( OPTION_GAME_SIZE, DEFAULT_GAME_SIZE );
        Set_boolean_option( OPTION_USE_DEVICE_BRIGHTNESS, true );
        Set_short_option( OPTION_BRIGHTNESS, DEFAULT_BRIGHTNESS );

        GameView.Set_sizes( Get_short_option( OPTION_GAME_SIZE ) );

        if ( Page.Is_Game_page() ) {
            GameController.Get_instance().Handle_request( new Request( Request.Background_change ),
                                                          new Response() );
        }

        GameUIView.Set_brightness();
    }

    public void Reset_audio_options() {
        Set_boolean_option( OPTION_IS_MUTED, false );
        Set_short_option( OPTION_VOLUME, DEFAULT_VOLUME );
        Set_short_option( OPTION_AUDIO_OUTPUT, DEFAULT_AUDIO_OUTPUT );
    }
}
