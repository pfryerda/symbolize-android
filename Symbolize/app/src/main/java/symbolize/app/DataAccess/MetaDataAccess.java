package symbolize.app.DataAccess;

import symbolize.app.Common.Page;
import symbolize.app.Common.Session;
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


    // Setter methods
    //----------------

    public static void Set_last_world() {
        dao.Set_property( Page.Get_context().getString( R.string.last_world ),
                          Session.Get_instance().Get_current_world() );
    }

    public static void Set_last_draw_enabled() {
        dao.Set_property( Page.Get_context().getString( R.string.last_draw ),
                Session.Get_instance().In_draw_mode() );
    }


    // Public methods
    //----------------

    /*
     * Method used to reset the last world back to 1, for deleting all data
     */
    public static void Reset_last_world() {
        dao.Set_property( Page.Get_context().getString( R.string.last_world ), 1 );
    }
}
