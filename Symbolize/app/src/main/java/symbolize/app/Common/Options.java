package symbolize.app.Common;

import android.content.SharedPreferences;

public class Options {
    // Flags
    //-------

    public static final int SHOW_GRID = 0;
    public static final int SHOW_BORDER = 1;
    public static final int SNAP_DRAWING = 2;


    // Fields
    //-------

    private SharedPreferences.Editor settings_editor;
    private boolean show_grid;
    private boolean show_border;
    private boolean snap_drawing;
    private int volume;

    // Constructor
    //-------------

    public Options( SharedPreferences settings_dao ) {
        this.settings_editor = settings_dao.edit();
        show_grid = settings_dao.getBoolean( "settings_grid", true );
        show_border = settings_dao.getBoolean( "settings_border", false );
        snap_drawing = settings_dao.getBoolean( "settings_snap", false );
        volume = settings_dao.getInt( "settings_volume", 100 );
    }


    // Getter method
    //---------------

    public boolean Show_grid() {
        return show_grid;
    }

    public boolean Show_border() {
        return show_border;
    }

    public boolean Is_snap_drawing() {
        return snap_drawing;
    }

    public int Get_volume() { return volume; }


    // Setter methods
    //----------------

    public void Toggle_grid() {
        show_grid = !show_grid;
        settings_editor.putBoolean( "settings_grid", show_grid );
        settings_editor.commit();
    }

    public void Toggle_border() {
        show_border = !show_border;
        settings_editor.putBoolean( "settings_border", show_border );
        settings_editor.commit();
    }

    public void Toggle_snap() {
        snap_drawing = !snap_drawing;
        settings_editor.putBoolean( "settings_snap", snap_drawing );
        settings_editor.commit();
    }

    public void Set_volume( int volume ) {
        this.volume = volume;
        settings_editor.putInt( "settings_volume", volume );
        settings_editor.commit();
    }
}
