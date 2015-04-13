package app.symbolize.Home;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import app.symbolize.Routing.Page;
import app.symbolize.DataAccess.OptionsDataAccess;
import app.symbolize.Dialog.OptionsDialog;
import app.symbolize.Game.GamePage;
import app.symbolize.R;
import app.symbolize.Routing.Router;



/*
 * The main class in charge of setting up the home page as well as responding to client interactions on the home page
 */
public class HomePage extends Page {
    // Main method
    //-------------

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Page.Set_not_game_page();

        Set_sound_image();
    }

    // Button methods
    //----------------

    public void On_start_button_clicked(final View view) {
        Router.Route(getApplicationContext(), GamePage.class);
    }

    public void On_mute_button_clicked(final View view) {
        OptionsDataAccess.Get_instance().Toggle_boolean_option( OptionsDataAccess.OPTION_IS_MUTED );
        Set_sound_image();
    }

    public void On_settings_button_clicked(final View view) {
        OptionsDialog options_dialog = new OptionsDialog();
        options_dialog.Show();
    }


    // Static methods
    //----------------

    public static void Set_sound_image() {
        if ( OptionsDataAccess.Get_instance().Get_boolean_option( OptionsDataAccess.OPTION_IS_MUTED ) ) {
            ( (ImageButton) HomePage.Get_activity().findViewById( R.id.Mute ) ).setImageResource( R.drawable.mute );
        } else {
            ( (ImageButton) HomePage.Get_activity().findViewById( R.id.Mute ) ).setImageResource( R.drawable.sound );
        }
    }
}