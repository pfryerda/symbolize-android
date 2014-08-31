package symbolize.app.DataAccess;

import symbolize.app.Common.Page;
import symbolize.app.R;

/*
 * An all static data access API, for save data about your settings
 */
abstract public class OptionsDataAccess {
    // Flags
    //------

    public static final byte OPTION_GRID            = 0;
    public static final byte OPTION_BORDER          = 1;
    public static final byte OPTION_SNAP_DRAWING    = 2;
    public static final byte OPTION_SHOW_ANIMATIONS = 3;

    public static final byte OPTION_VOLUME = 4;


    // Constants
    //-----------

    private static final byte DEFAULT_VOLUME = 100;
    private static final byte SHORT_OFFSET = 4;


    // Static fields
    //--------------

    private static final DataAccessObject dao = new DataAccessObject( R.string.preference_settings_key );

    private static int[] option_id_map = new int[5];

    private static boolean[] boolean_options = new boolean[4];
    private static short[] short_options = new short[1];


    // Static block
    //--------------

    static {
        option_id_map[OPTION_GRID] = R.string.grid_settings;
        option_id_map[OPTION_BORDER] = R.string.border_settings;
        option_id_map[OPTION_SNAP_DRAWING] = R.string.snap_settings;
        option_id_map[OPTION_SHOW_ANIMATIONS] = R.string.animation_settings;
        option_id_map[OPTION_VOLUME] = R.string.volume_settings;

        boolean_options[OPTION_GRID] =  dao.Get_property(
                Page.Get_context().getString( R.string.grid_settings ), true );
        boolean_options[OPTION_BORDER] = dao.Get_property(
                Page.Get_context().getString( R.string.border_settings ), false );
        boolean_options[OPTION_SNAP_DRAWING] = dao.Get_property(
                Page.Get_context().getString( R.string.snap_settings ), false );
        boolean_options[OPTION_SHOW_ANIMATIONS] = dao.Get_property(
                Page.Get_context().getString( R.string.animation_settings ), true );

        short_options[OPTION_VOLUME - SHORT_OFFSET] = (byte) dao.Get_property(
                Page.Get_context().getString( R.string.volume_settings ), DEFAULT_VOLUME );
    }


    // Getter methods
    //---------------

    public static boolean Get_boolean_option( final byte option ) {
        return boolean_options[option];
    }

    public static short Get_short_option( final byte option ) {
        return short_options[option - SHORT_OFFSET];
    }


    // Setter methods
    //----------------

    public static void Toggle_boolean_option( final byte option ) {
        boolean new_option = !Get_boolean_option( option );
        boolean_options[option] = new_option;
        dao.Set_property(
                Page.Get_context().getString( option_id_map[option] ), new_option );
    }

    public static void Set_short_option( final byte option, final short new_value ) {
        short_options[option - SHORT_OFFSET] = new_value;
        dao.Set_property(
                Page.Get_context().getString( option_id_map[option] ), new_value );
    }


    // Public methods
    //----------------

    /*
     * Commit changes to disk
     */
    public static void Commit() {
        dao.Commit();
    }
}
