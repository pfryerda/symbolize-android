package symbolize.app.Home;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import symbolize.app.Game.GameActivity;
import symbolize.app.R;


public class HomeActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
    }

    public void onStartGame( View view) {
        startActivity( new Intent(getApplicationContext(), GameActivity.class ) );
    }
}
