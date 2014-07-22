package symbolize.app.Common;

import android.content.SharedPreferences;

public class Options {
    // Fields
    //-------

    private SharedPreferences.Editor settings_editor;
    private boolean include_grid;
    private int volume;

    // Constructor
    //-------------

    public Options(SharedPreferences settings_dao) {
        this.settings_editor = settings_dao.edit();
        include_grid = settings_dao.getBoolean("settings_grid", true);
        volume = settings_dao.getInt( "settings_volume", 100 );
    }


    // Public method
    //---------------

    public void Toggle_grid() {
        include_grid = !include_grid;
        settings_editor.putBoolean( "settings_grid", include_grid );
        settings_editor.commit();
    }


    // Setter methods
    //----------------

    public void Set_volume( int volume ) {
        this.volume = volume;
        settings_editor.putInt( "settings_volume", volume );
        settings_editor.commit();
    }
}
