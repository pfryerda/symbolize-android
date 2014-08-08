package symbolize.app.Common;

import android.content.Context;
import android.content.SharedPreferences;

import symbolize.app.Game.GameActivity;
import symbolize.app.R;

abstract public class Options {
    // Fields
    //-------

    private static final SharedPreferences settings_dao = SymbolizeActivity.Get_context()
            .getSharedPreferences( SymbolizeActivity.Get_context().getString( R.string.preference_unlocks_key ),
                    Context.MODE_PRIVATE );
    private final static SharedPreferences.Editor settings_editor = settings_dao.edit();


    // Getter methods
    //---------------

    public static boolean Show_grid() {
        return settings_dao.getBoolean(
                SymbolizeActivity.Get_context().getString( R.string.grid_settings ), true );
    }

    public static boolean Show_border() {
        return settings_dao.getBoolean(
                SymbolizeActivity.Get_context().getString( R.string.border_settings ), false );
    }

    public static boolean Is_snap_drawing() {
        return settings_dao.getBoolean(
                SymbolizeActivity.Get_context().getString( R.string.snap_settings ), false );
    }

    public static boolean Show_animations() {
        return settings_dao.getBoolean(
                SymbolizeActivity.Get_context().getString( R.string.animation_settings ), true );
    }

    public static int Get_volume() {
        return settings_dao.getInt(
                SymbolizeActivity.Get_context().getString( R.string.volume_settings ), 100 );
    }


    // Setter methods
    //----------------

    public static void Toggle_grid() {
        settings_editor.putBoolean(
                SymbolizeActivity.Get_context().getString( R.string.grid_settings ), !Show_grid() );
        settings_editor.commit();
    }

    public static void Toggle_border() {
        settings_editor.putBoolean(
                SymbolizeActivity.Get_context().getString( R.string.border_settings ), !Show_border() );
        settings_editor.commit();
    }

    public static void Toggle_snap() {
        settings_editor.putBoolean(
                SymbolizeActivity.Get_context().getString( R.string.snap_settings ), !Is_snap_drawing() );
        settings_editor.commit();
    }

    public static void Toggle_animations() {
        settings_editor.putBoolean(
                SymbolizeActivity.Get_context().getString( R.string.animation_settings ), !Show_animations() );
        settings_editor.commit();
    }

    public static void Set_volume( int volume ) {
        settings_editor.putInt(
                SymbolizeActivity.Get_context().getString( R.string.volume_settings ), volume );
        settings_editor.commit();
    }
}
