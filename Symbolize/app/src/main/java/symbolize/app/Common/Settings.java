package symbolize.app.Common;

import android.content.SharedPreferences;

import symbolize.app.Common.Enum.Language;

public class Settings {
    // Fields
    //-------

    private SharedPreferences.Editor settings_editor;
    private boolean include_grid;
    private int volume;
    private Language language;

    // Constructor
    //-------------

    Settings( SharedPreferences settings_dao ) {
        this.settings_editor = settings_dao.edit();
        include_grid = settings_dao.getBoolean( "settings_grid", true );
        volume = settings_dao.getInt( "settings_volume", 100 );
        language = Language.values()[settings_dao.getInt( "settings_language", Language.EN.Get_value() )];
    }


    // Public method
    //--------------

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

    public void Set_language( Language language ) {
        this.language = language;
        settings_editor.putInt( "settings_language", language.Get_value() );
        settings_editor.commit();
    }
}
