package symbolize.app.Home;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import symbolize.app.Common.Page;
import symbolize.app.Dialog.OptionsDialog;
import symbolize.app.Game.GamePage;
import symbolize.app.R;


public class HomePage extends Page {

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
        startActivity( new Intent ( getApplicationContext(), GamePage.class ) );
    }

    public void On_mute_button_clicked( final View view ) {

    }

    public void On_settings_button_clicked( final View view ){
        OptionsDialog options_dialog = new OptionsDialog();
        options_dialog.Show();
    }
}
