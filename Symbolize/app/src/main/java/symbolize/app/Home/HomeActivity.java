package symbolize.app.Home;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import symbolize.app.Common.Options;
import symbolize.app.Common.Player;
import symbolize.app.Dialog.OptionsDialog;
import symbolize.app.Game.GameActivity;
import symbolize.app.R;


public class HomeActivity extends Activity {
    // Fields
    //--------

    private Player player;
    private Options options;

    // Main method
    //-------------

    @Override
    protected void onCreate( final Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_home );

        player = new Player( this.getSharedPreferences( getString( R.string.preference_unlocks_key ), Context.MODE_PRIVATE ) );
        options = new Options( this.getSharedPreferences( getString( R.string.preference_settings_key ), Context.MODE_PRIVATE ) );
    }


    // Button methods
    //----------------

    public void On_start_button_clicked( final View view ) {
        startActivity( new Intent ( getApplicationContext(), GameActivity.class ) );
    }

    public void On_mute_button_clicked( final View view ) {

    }

    public void On_settings_button_clicked( final View view ){
        OptionsDialog options_dialog = new OptionsDialog( this, options, player );
        options_dialog.Show_dialog();
    }
}
