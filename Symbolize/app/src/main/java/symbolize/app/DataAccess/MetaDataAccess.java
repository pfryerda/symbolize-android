package symbolize.app.DataAccess;

import symbolize.app.Common.Page;
import symbolize.app.Common.Session;
import symbolize.app.Puzzle.Puzzle;
import symbolize.app.R;

/*
 *  An all static data access API, for save metadata about the user
 */
abstract public class MetaDataAccess {
    // Static fields
    //--------------

    private static DataAccessObject dao = new DataAccessObject( R.string.preference_meta_key );


    // Getter methods
    //---------------

    public static int Get_last_world() {
        return dao.Get_property( Page.Get_resource_string( R.string.last_world ), 1 );
    }

    public static boolean Get_last_draw_enabled() {
        return dao.Get_property( Page.Get_resource_string( R.string.last_draw ), true );
    }

    public static boolean Has_seen_world() {
        return dao.Get_property( Page.Get_resource_string( R.string.seen_world ), false ) || Session.DEV_MODE;
    }

    public static boolean Has_seen_draw() {
        return dao.Get_property( Page.Get_resource_string( R.string.seen_draw ), false ) || Session.DEV_MODE;
    }

    public static boolean Has_seen_rotate() {
        return dao.Get_property( Page.Get_resource_string( R.string.seen_rotate ), false ) || Session.DEV_MODE;
    }

    public static boolean Has_seen_erase() {
        return dao.Get_property( Page.Get_resource_string( R.string.seen_erase ), false ) || Session.DEV_MODE;
    }

    public static boolean Has_seen_flip() {
        return dao.Get_property( Page.Get_resource_string( R.string.seen_flip ), false ) || Session.DEV_MODE;
    }

    public static boolean Has_seen_shift() {
        return dao.Get_property( Page.Get_resource_string( R.string.seen_shift ), false ) || Session.DEV_MODE;
    }

    public static boolean Has_seen_drag() {
        return dao.Get_property( Page.Get_resource_string( R.string.seen_drag ), false ) || Session.DEV_MODE;
    }

    public static boolean Has_seen_color_change() {
        return dao.Get_property( Page.Get_resource_string( R.string.seen_color_change ), false ) || Session.DEV_MODE;
    }

    public static boolean Has_seen_special() {
        return dao.Get_property( Page.Get_resource_string( R.string.seen_special ), false ) || Session.DEV_MODE;
    }


    // Setter methods
    //----------------

    public static void Set_last_world() {
        if ( !Session.DEV_MODE ) {
            dao.Set_property( Page.Get_context().getString( R.string.last_world ),
                    Session.Get_instance().Get_current_world() );
        }
    }

    public static void Set_last_draw_enabled() {
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
    public static void Update_mechanics_seen() {
        if ( !Session.DEV_MODE ) {
            final Puzzle finished_puzzle = Session.Get_instance().Get_current_puzzle();

            if ( Session.Get_instance().Is_in_world_view() && !Has_seen_world() ) {
                dao.Set_property( Page.Get_resource_string(R.string.seen_world ), true);
            }

            if ( ( finished_puzzle.Get_draw_restriction() > 0 ) && !Has_seen_draw() ) {
                dao.Set_property(Page.Get_resource_string(R.string.seen_draw ), true);
            }

            if ( finished_puzzle.Can_rotate() && !Has_seen_rotate() ) {
                dao.Set_property( Page.Get_resource_string( R.string.seen_rotate ), true );
            }

            if ( ( finished_puzzle.Get_erase_restriction() > 0 ) && !Has_seen_erase() ) {
                dao.Set_property( Page.Get_resource_string(R.string.seen_erase  ), true );
            }

            if ( finished_puzzle.Can_flip() && !Has_seen_flip() ) {
                dao.Set_property( Page.Get_resource_string(R.string.seen_flip ), true);
            }

            if ( finished_puzzle.Can_shift() && !Has_seen_shift() ) {
                dao.Set_property( Page.Get_resource_string(R.string.seen_shift ), true);
            }

            if ( ( finished_puzzle.Get_drag_restriction() > 0 ) && !Has_seen_drag() ) {
                dao.Set_property( Page.Get_resource_string(R.string.seen_drag ), true );
            }

            if ( finished_puzzle.Can_change_color() && !Has_seen_color_change() ) {
                dao.Set_property( Page.Get_resource_string( R.string.seen_color_change ), true );
            }

            if( finished_puzzle.Is_special_enabled() && !Has_seen_special() ) {
                dao.Set_property( Page.Get_resource_string( R.string.seen_special ), true );
            }
        }
    }

    public static void Reset_meta_data_access() {
        dao.Set_property( Page.Get_context().getString( R.string.last_world ), 1 );
        dao.Set_property( Page.Get_context().getString( R.string.last_draw ), true );

        dao.Set_property( Page.Get_resource_string(R.string.seen_world ), false);
        dao.Set_property(Page.Get_resource_string(R.string.seen_draw ), false);
        dao.Set_property( Page.Get_resource_string( R.string.seen_rotate ), false );
        dao.Set_property( Page.Get_resource_string(R.string.seen_erase  ), false );
        dao.Set_property( Page.Get_resource_string(R.string.seen_flip ), false);
        dao.Set_property( Page.Get_resource_string(R.string.seen_shift ), false);
        dao.Set_property( Page.Get_resource_string(R.string.seen_drag ), false );
        dao.Set_property( Page.Get_resource_string( R.string.seen_color_change ), false );
        dao.Set_property( Page.Get_resource_string( R.string.seen_special ), false );
    }
}
