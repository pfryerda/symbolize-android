package app.symbolize.DataAccess;

import app.symbolize.Routing.Page;
import app.symbolize.Common.Session;
import app.symbolize.Puzzle.Puzzle;
import app.symbolize.R;

/*
 *  An all static data access API, for save metadata about the user
 */
public class MetaDataAccess {
    // Flags
    //------

    public static final byte SEEN_WORLD        = 0;
    public static final byte SEEN_DRAW         = 1;
    public static final byte SEEN_ROTATE       = 2;
    public static final byte SEEN_ERASE        = 3;
    public static final byte SEEN_FLIP         = 4;
    public static final byte SEEN_SHIFT        = 5;
    public static final byte SEEN_DRAG         = 6;
    public static final byte SEEN_CHANGE_COLOR = 7;
    public static final byte SEEN_SPECIAL      = 8;

    public static final byte DURATION_ROTATE    = 0;
    public static final byte DURATION_FLIP      = 1;
    public static final byte DURATION_SHIFT     = 2;
    public static final byte DURATION_ZOOM      = 3;
    public static final byte DURATION_TRANSLATE = 4;


    // Constants
    //-----------

    private static final byte ANIMATION_DECREASE_THRESHOLD = 1;

    private static final short ROTATE_DURATION_MAX = 450;
    private static final short ROTATE_DURATION_MIN = 290;

    private static final short FLIP_DURATION_MAX = 450;
    private static final short FLIP_DURATION_MIN = 290;

    private static final short SHIFT_DURATION_MAX = 576;
    private static final short SHIFT_DURATION_MIN = 414;

    private static final short ZOOM_DURATION_MAX = 580;
    private static final short ZOOM_DURATION_MIN = 420;

    private static final short TRANSLATE_DURATION_MAX = 724;
    private static final short TRANSLATE_DURATION_MIN = 566;


    // Static fields
    //---------------

    private static DataAccessObject dao = new DataAccessObject( R.string.preference_meta_key );
    private static final int[] duration_id_map = new int[5];
    private static final short[] min_duration_map = new short[5];


    // Static block
    //-------------

    static {
        duration_id_map[DURATION_ROTATE] = R.string.rotate_duration;
        duration_id_map[DURATION_FLIP] = R.string.flip_duration;
        duration_id_map[DURATION_SHIFT] = R.string.shift_duration;
        duration_id_map[DURATION_ZOOM] = R.string.zoom_duration;
        duration_id_map[DURATION_TRANSLATE] = R.string.translate_duration;

        min_duration_map[DURATION_ROTATE] = ROTATE_DURATION_MIN;
        min_duration_map[DURATION_FLIP] = FLIP_DURATION_MIN;
        min_duration_map[DURATION_SHIFT] = SHIFT_DURATION_MIN;
        min_duration_map[DURATION_ZOOM] = ZOOM_DURATION_MIN;
        min_duration_map[DURATION_TRANSLATE] = TRANSLATE_DURATION_MIN;
    }


    // Static methods
    //----------------

    /*
     * Get the last world the user was in last time they played
     */
    public static byte Get_last_world() {
        return (byte) dao.Get_property( Page.Get_resource_string( R.string.last_world ), 1 );
    }

    /*
     * Get whether the user was in draw mode last time they played
     */
    public static boolean Get_last_draw_enabled() {
        return dao.Get_property( Page.Get_resource_string( R.string.last_draw ), true );
    }

    /*
     * Commit changes to disk
     */
    public static void Commit() {
        dao.Commit();
    }


    // Fields
    //-------

    private boolean[] seens = new boolean[9];
    private short[] durations = new short[5];


    // Singleton setup
    //----------------

    private static final MetaDataAccess instance = new MetaDataAccess();

    public static MetaDataAccess Get_instance() {
        return instance;
    }


    // Constructor
    //------------

    private MetaDataAccess() {
        seens[SEEN_WORLD] = dao.Get_property( Page.Get_resource_string( R.string.seen_world ), false );
        seens[SEEN_DRAW] = dao.Get_property( Page.Get_resource_string( R.string.seen_draw ), false );
        seens[SEEN_ROTATE] = dao.Get_property( Page.Get_resource_string( R.string.seen_rotate ), false );
        seens[SEEN_ERASE] = dao.Get_property( Page.Get_resource_string( R.string.seen_erase ), false );
        seens[SEEN_FLIP] = dao.Get_property( Page.Get_resource_string( R.string.seen_flip ), false );
        seens[SEEN_SHIFT] = dao.Get_property( Page.Get_resource_string( R.string.seen_shift ), false );
        seens[SEEN_DRAG] = dao.Get_property( Page.Get_resource_string( R.string.seen_drag ), false );
        seens[SEEN_CHANGE_COLOR] = dao.Get_property( Page.Get_resource_string( R.string.seen_color_change ), false );
        seens[SEEN_SPECIAL] = dao.Get_property( Page.Get_resource_string( R.string.seen_special ), false );

        durations[DURATION_ROTATE] = (short) dao.Get_property( Page.Get_resource_string( R.string.rotate_duration ), ROTATE_DURATION_MAX );
        durations[DURATION_FLIP] = (short) dao.Get_property( Page.Get_resource_string( R.string.flip_duration ), FLIP_DURATION_MAX );
        durations[DURATION_SHIFT] = (short) dao.Get_property( Page.Get_resource_string( R.string.shift_duration ), SHIFT_DURATION_MAX );
        durations[DURATION_ZOOM] = (short) dao.Get_property( Page.Get_resource_string( R.string.zoom_duration ), ZOOM_DURATION_MAX );
        durations[DURATION_TRANSLATE] = (short) dao.Get_property( Page.Get_resource_string( R.string.translate_duration ), TRANSLATE_DURATION_MAX );
    }


    // Getter methods
    //---------------

    public boolean Has_seen( final byte seen ) {
        return seens[seen] || Session.DEV_MODE;
    }

    public short Get_duration( final byte duration_type ) {
        final short duration_min = min_duration_map[duration_type];
        if ( Session.DEV_MODE ) {
            return duration_min;
        }

        short duration = durations[duration_type];
        if ( duration > duration_min ) {
            short new_duration = (short) ( duration - ANIMATION_DECREASE_THRESHOLD );
            durations[duration_type] = new_duration;
            dao.Set_property( Page.Get_resource_string( duration_id_map[duration_type] ), new_duration );
        }
        return duration;
    }


    // Setter methods
    //----------------

    public void Set_last_world() {
        if ( !Session.DEV_MODE ) {
            dao.Set_property( Page.Get_context().getString( R.string.last_world ),
                    Session.Get_instance().Get_current_world() );
        }
    }

    public void Set_last_draw_enabled() {
        if ( !Session.DEV_MODE ) {
            dao.Set_property( Page.Get_context().getString( R.string.last_draw ),
                    Session.Get_instance().In_draw_mode() );
        }
    }


    // Public methods
    //----------------

    /*
     * Updates the 'Has_seen_xxx' now that a level has been finished
     */
    public void Update_mechanics_seen() {
        if ( !Session.DEV_MODE ) {
            final Puzzle finished_puzzle = Session.Get_instance().Get_current_puzzle();

            if ( Session.Get_instance().Is_in_world_view() && !Has_seen( SEEN_WORLD ) ) {
                seens[SEEN_WORLD] = true;
                dao.Set_property( Page.Get_resource_string( R.string.seen_world ), true);
            }

            if ( ( finished_puzzle.Get_draw_restriction() > 0 ) && !Has_seen( SEEN_DRAW ) ) {
                seens[SEEN_DRAW] = true;
                dao.Set_property( Page.Get_resource_string( R.string.seen_draw ), true);
            }

            if ( finished_puzzle.Can_rotate() && !Has_seen( SEEN_ROTATE ) ) {
                seens[SEEN_ROTATE] = true;
                dao.Set_property( Page.Get_resource_string( R.string.seen_rotate ), true );
            }

            if ( ( finished_puzzle.Get_erase_restriction() > 0 ) && !Has_seen( SEEN_ERASE ) ) {
                seens[SEEN_ERASE] = true;
                dao.Set_property( Page.Get_resource_string( R.string.seen_erase  ), true );
            }

            if ( finished_puzzle.Can_flip() && !Has_seen( SEEN_FLIP ) ) {
                seens[SEEN_FLIP] = true;
                dao.Set_property( Page.Get_resource_string(R.string.seen_flip ), true);
            }

            if ( finished_puzzle.Can_shift() && !Has_seen( SEEN_SHIFT ) ) {
                seens[SEEN_SHIFT] = true;
                dao.Set_property( Page.Get_resource_string(R.string.seen_shift ), true);
            }

            if ( ( finished_puzzle.Get_drag_restriction() > 0 ) && !Has_seen( SEEN_DRAG ) ) {
                seens[SEEN_DRAG] = true;
                dao.Set_property( Page.Get_resource_string(R.string.seen_drag ), true );
            }

            if ( finished_puzzle.Can_change_color() && !Has_seen( SEEN_CHANGE_COLOR ) ) {
                seens[SEEN_CHANGE_COLOR] = true;
                dao.Set_property( Page.Get_resource_string( R.string.seen_color_change ), true );
            }

            if( finished_puzzle.Is_special_enabled() && !Has_seen( SEEN_SPECIAL ) ) {
                seens[SEEN_SPECIAL] = true;
                dao.Set_property( Page.Get_resource_string( R.string.seen_special ), true );
            }
        }
    }

    public void Reset_meta_data_access() {
        dao.Set_property( Page.Get_context().getString( R.string.last_world ), 1 );
        dao.Set_property( Page.Get_context().getString( R.string.last_draw ), true );

        dao.Set_property( Page.Get_resource_string( R.string.seen_world ), false);
        dao.Set_property(Page.Get_resource_string( R.string.seen_draw ), false);
        dao.Set_property( Page.Get_resource_string( R.string.seen_rotate ), false );
        dao.Set_property( Page.Get_resource_string( R.string.seen_erase  ), false );
        dao.Set_property( Page.Get_resource_string( R.string.seen_flip ), false);
        dao.Set_property( Page.Get_resource_string( R.string.seen_shift ), false);
        dao.Set_property( Page.Get_resource_string( R.string.seen_drag ), false );
        dao.Set_property( Page.Get_resource_string( R.string.seen_color_change ), false );
        dao.Set_property( Page.Get_resource_string( R.string.seen_special ), false );

        seens[SEEN_WORLD] = false;
        seens[SEEN_DRAW] = false ;
        seens[SEEN_ROTATE] = false;
        seens[SEEN_ERASE] = false;
        seens[SEEN_FLIP] = false;
        seens[SEEN_SHIFT] = false;
        seens[SEEN_DRAG] = false;
        seens[SEEN_CHANGE_COLOR] = false;
        seens[SEEN_SPECIAL] = false;

        dao.Set_property( Page.Get_resource_string( R.string.rotate_duration ), ROTATE_DURATION_MAX );
        dao.Set_property( Page.Get_resource_string( R.string.flip_duration ), FLIP_DURATION_MAX );
        dao.Set_property( Page.Get_resource_string( R.string.shift_duration ), SHIFT_DURATION_MAX );
        dao.Set_property( Page.Get_resource_string( R.string.zoom_duration ), ZOOM_DURATION_MAX );
        dao.Set_property( Page.Get_resource_string( R.string.translate_duration ), TRANSLATE_DURATION_MAX );

        durations[DURATION_ROTATE] = ROTATE_DURATION_MAX;
        durations[DURATION_FLIP] = FLIP_DURATION_MAX;
        durations[DURATION_SHIFT] = SHIFT_DURATION_MAX;
        durations[DURATION_ZOOM] = ZOOM_DURATION_MAX;
        durations[DURATION_TRANSLATE] = TRANSLATE_DURATION_MAX;
    }
}
