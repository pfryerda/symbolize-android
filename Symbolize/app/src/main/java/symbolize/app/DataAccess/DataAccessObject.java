package symbolize.app.DataAccess;
import android.content.Context;
import android.content.SharedPreferences;

import symbolize.app.Common.SymbolizeActivity;


public class DataAccessObject {
    // Fields
    //---------

    protected final SharedPreferences dao;
    protected final SharedPreferences.Editor dao_editor;


    // Constructor
    //-------------

     public DataAccessObject( int id ) {
         dao = SymbolizeActivity
                 .Get_context()
                 .getSharedPreferences( SymbolizeActivity.Get_resource_string( id ),
                         Context.MODE_PRIVATE );
         dao_editor = dao.edit();
     }


    // Public methods
    //-------------------

    /*
     * Gets the data at 'key', if not there returns initial value
     */
    public int Get_property( String key, int initial_value ) {
       return dao.getInt( key, initial_value );
    }

    public boolean Get_property( String key, boolean initial_value ) {
        return dao.getBoolean( key, initial_value );
    }

    /*
     * Sets the given value at the 'key'
     */
    public void Set_property( String key, int value, boolean commit ) {
        dao_editor.putInt( key, value );
        if ( commit ) {
            dao_editor.commit();
        }
    }

    public void Set_property( String key, boolean value, boolean commit ) {
        dao_editor.putBoolean( key, value );
        if( commit ) {
            dao_editor.commit();
        }
    }

    public void Set_property( String key, int value ) {
        dao_editor.putInt( key, value );
        dao_editor.commit();
    }

    public void Set_property( String key, boolean value ) {
        dao_editor.putBoolean( key, value );
        dao_editor.commit();
    }

    public void Commit() {
        dao_editor.commit();
    }
}
