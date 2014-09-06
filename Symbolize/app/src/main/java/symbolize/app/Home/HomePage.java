package symbolize.app.Home;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import symbolize.app.Routing.Page;
import symbolize.app.DataAccess.OptionsDataAccess;
import symbolize.app.Dialog.OptionsDialog;
import symbolize.app.Game.GamePage;
import symbolize.app.R;
import symbolize.app.Routing.Router;

/*
 * The main class in charge of setting up the home page as well as responding to client interactions on the home page
 */
public class HomePage extends Page {
    // Main method
    //-------------

    @Override
    protected void onCreate( final Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView(R.layout.activity_home);
        Page.Set_not_game_page();

        set_mute_text();
    }


    // Button methods
    //----------------

    public void On_start_button_clicked( final View view ) {
        Router.Route( getApplicationContext(), GamePage.class );
    }

    public void On_mute_button_clicked( final View view ) {
        OptionsDataAccess.Get_instance().Toggle_boolean_option( OptionsDataAccess.OPTION_IS_MUTED );
        set_mute_text();
    }

    public void On_settings_button_clicked( final View view ){
        OptionsDialog options_dialog = new OptionsDialog();
        options_dialog.Show();
    }


    // Private methods
    //----------------

    private void set_mute_text() {
        if ( OptionsDataAccess.Get_instance().Get_boolean_option( OptionsDataAccess.OPTION_IS_MUTED ) ) {
            ( (TextView) findViewById( R.id.Mute ) ).setText( "Unmute" );
        } else {
            ( (TextView) findViewById( R.id.Mute ) ).setText( "Mute" );
        }
    }
}
