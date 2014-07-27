package symbolize.app.Home;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import symbolize.app.Common.Enum.Action;
import symbolize.app.Common.Options;
import symbolize.app.Common.Player;
import symbolize.app.Common.Request;
import symbolize.app.Dialog.ConfirmDialog;
import symbolize.app.Dialog.OptionsDialog;
import symbolize.app.Game.GameActivity;
import symbolize.app.R;


public class HomeActivity extends FragmentActivity
                          implements OptionsDialog.OptionsDialogListener{
    // Fields
    //--------

    private Player player;
    private Options options;
    private FragmentManager dialog_fragment_manager;

    // Main method
    //-------------

    @Override
    protected void onCreate( final Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_home );

        player = new Player( this.getSharedPreferences( getString( R.string.preference_unlocks_key ), Context.MODE_PRIVATE ) );
        options = new Options( this.getSharedPreferences( getString( R.string.preference_settings_key ), Context.MODE_PRIVATE ) );
        dialog_fragment_manager = getFragmentManager();
    }


    // Button methods
    //----------------

    public void On_start_button_clicked( final View view ) {
        startActivity( new Intent ( getApplicationContext(), GameActivity.class ) );
    }

    public void On_mute_button_clicked( final View view ) {

    }

    public void On_settings_button_clicked( final View view ){
        OptionsDialog options_dialog = new OptionsDialog();
        options_dialog.Set_up( options );
        options_dialog.show( dialog_fragment_manager, "options_dialog" );
    }


    // Interface methods
    //-------------------

    @Override
    public void OnToggleGrid() {
        options.Toggle_grid();
    }

    @Override
    public void OnToggleBorder() {
        options.Toggle_border();
    }

    @Override
    public void OnToggleSnap() {
        options.Is_snap_drawing();
    }

    @Override
    public void OnDeleteAllData() {
        ConfirmDialog confirmDialog = new ConfirmDialog();
        confirmDialog.Set_attr( "Delete all data", "Are you sure you would like to clear all your progress. This cannot be reverted" );
        confirmDialog.SetConfirmationListener( new ConfirmDialog.ConfirmDialogListener() {
            @Override
            public void OnDialogSuccess() {
                player.Delete_all_data();
            }

            @Override
            public void OnDialogFail() {}
        } );
        confirmDialog.show(dialog_fragment_manager, "delete_all_data_dialog");
    }

    @Override
    public void OnEditVolume( int volume ) {
        options.Set_volume( volume );
    }
}
