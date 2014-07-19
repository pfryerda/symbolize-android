package symbolize.app.Home;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import symbolize.app.Game.GameActivity;
import symbolize.app.R;


public class HomeActivity extends Activity {
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

    }
}
