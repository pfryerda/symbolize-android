package symbolize.app.DataAccess;

import android.content.Context;
import android.content.SharedPreferences;

import symbolize.app.Common.SymbolizeActivity;
import symbolize.app.Game.GameActivity;
import symbolize.app.R;

abstract public class OptionsDataAccess {
    // Static fields
    //--------------

    private static DataAccessObject dao = new DataAccessObject( R.string.preference_settings_key );


    // Getter methods
    //---------------

    public static boolean Show_grid() {
        return dao.Get_property(
                SymbolizeActivity.Get_context().getString( R.string.grid_settings ), true );
    }

    public static boolean Show_border() {
        return dao.Get_property(
                SymbolizeActivity.Get_context().getString( R.string.border_settings ), false );
    }

    public static boolean Is_snap_drawing() {
        return dao.Get_property(
                SymbolizeActivity.Get_context().getString( R.string.snap_settings ), false );
    }

    public static boolean Show_animations() {
        return dao.Get_property(
                SymbolizeActivity.Get_context().getString( R.string.animation_settings ), true );
    }

    public static int Get_volume() {
        return dao.Get_property(
                SymbolizeActivity.Get_context().getString( R.string.volume_settings ), 100 );
    }


    // Setter methods
    //----------------

    public static void Toggle_grid() {
        dao.Set_property(
                SymbolizeActivity.Get_context().getString( R.string.grid_settings ), !Show_grid() );
    }

    public static void Toggle_border() {
        dao.Set_property(
                SymbolizeActivity.Get_context().getString( R.string.border_settings ), !Show_border() );
    }

    public static void Toggle_snap() {
        dao.Set_property(
                SymbolizeActivity.Get_context().getString( R.string.snap_settings ), !Is_snap_drawing() );
    }

    public static void Toggle_animations() {
        dao.Set_property(
                SymbolizeActivity.Get_context().getString( R.string.animation_settings ), !Show_animations() );
    }

    public static void Set_volume( int volume ) {
        dao.Set_property(
                SymbolizeActivity.Get_context().getString( R.string.volume_settings ), volume );
    }
}
