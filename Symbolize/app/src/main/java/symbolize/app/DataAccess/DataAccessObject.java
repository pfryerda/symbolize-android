package symbolize.app.DataAccess;

import android.content.Context;
import android.content.SharedPreferences;
import symbolize.app.Routing.Page;

/*
 * A wrapper for android's SharedPreferences, which allows storing key values pairs on disc for saved data
 */
public class DataAccessObject {
    // Fields
    //---------

    private final SharedPreferences dao;
    private final SharedPreferences.Editor dao_editor;


    // Constructor
    //-------------

     public DataAccessObject( int id ) {
         dao = Page
                 .Get_context()
                 .getSharedPreferences( Page.Get_resource_string(id),
                         Context.MODE_PRIVATE );
         dao_editor = dao.edit();
     }


    // Getter methods
    //-----------------

    /*
     * Gets the data at 'key', if not there returns initial value
     */
    public int Get_property( String key, int initial_value ) {
       return dao.getInt( key, initial_value );
    }

    public boolean Get_property( String key, boolean initial_value ) {
        return dao.getBoolean( key, initial_value );
    }


    // Setter methods
    //----------------

    /*
     * Sets the given value at the 'key'
     */
    public void Set_property( String key, int value ) {
        dao_editor.putInt( key, value );
    }

    public void Set_property( String key, boolean value ) {
        dao_editor.putBoolean( key, value );
    }


    // Public methods
    //----------------

    /*
     * Force commit changes to this data access object
     */
    public void Commit() {
        dao_editor.commit();
    }
}
