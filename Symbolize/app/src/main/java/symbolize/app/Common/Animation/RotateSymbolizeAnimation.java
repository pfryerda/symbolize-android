package symbolize.app.Common.Animation;


import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.LinearLayout;

import symbolize.app.Game.GameView;

public class RotateSymbolizeAnimation extends SymbolizeAnimation {
    // Constructor
    //------------

    public RotateSymbolizeAnimation( LinearLayout linearLayout, final GameView gameView, int rotation ) {
        animation = new RotateAnimation( 0, rotation, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f );
        animation.setDuration( ROTATEDURATION );
        Set_up( linearLayout, gameView );
    }
}
