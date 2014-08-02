package symbolize.app.Common;

import android.content.Context;
import android.content.SharedPreferences;

import symbolize.app.Game.GameActivity;
import symbolize.app.R;

public class Options {
    // Flags
    //-------

    public static final int SHOW_GRID = 0;
    public static final int SHOW_BORDER = 1;
    public static final int SNAP_DRAWING = 2;


    // Fields
    //-------

    private Context context;
    private SharedPreferences.Editor settings_editor;
    private boolean show_grid;
    private boolean show_border;
    private boolean snap_drawing;
    private int volume;

    // Constructor
    //-------------

    public Options( SharedPreferences settings_dao, Context context) {
        this.context = context;
        this.settings_editor = settings_dao.edit();
        show_grid = settings_dao.getBoolean( context.getString( R.string.grid_settings ), true );
        show_border = settings_dao.getBoolean( context.getString( R.string.border_settings ), false );
        snap_drawing = settings_dao.getBoolean( context.getString( R.string.snap_settings ), false );
        volume = settings_dao.getInt( context.getString( R.string.volume_settings ), 100 );
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
        settings_editor.putBoolean( context.getString( R.string.grid_settings ), show_grid );
        settings_editor.commit();
    }

    public void Toggle_border() {
        show_border = !show_border;
        settings_editor.putBoolean( context.getString( R.string.border_settings ), show_border );
        settings_editor.commit();
    }

    public void Toggle_snap() {
        snap_drawing = !snap_drawing;
        settings_editor.putBoolean( context.getString( R.string.snap_settings ), snap_drawing );
        settings_editor.commit();
    }

    public void Set_volume( int volume ) {
        this.volume = volume;
        settings_editor.putInt( context.getString( R.string.volume_settings ), volume );
        settings_editor.commit();
    }
}
