package symbolize.app.Home;

import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import symbolize.app.Common.Options;
import symbolize.app.Common.Player;
import symbolize.app.Common.SymbolizeActivity;
import symbolize.app.Dialog.ConfirmDialog;
import symbolize.app.Dialog.OptionsDialog;
import symbolize.app.Game.GameActivity;
import symbolize.app.R;


public class HomeActivity extends SymbolizeActivity {

    // Main method
    //-------------

    @Override
    protected void onCreate( final Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_home );
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
        options_dialog.Show();
    }
}
