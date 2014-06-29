package symbolize.app.Common.Animation;


import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.LinearLayout;

import symbolize.app.Game.GameView;

public class FlipSymbolizeAnimation extends SymbolizeAnimation {
    // Constructor
    //------------

    public FlipSymbolizeAnimation( LinearLayout linearLayout, final GameView gameView, int scale_horizontal, int scale_vertical ) {
        animation = new ScaleAnimation( 1, scale_horizontal, 1, scale_vertical, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f );
        animation.setDuration( FLIPDURATION );
        Set_up( linearLayout, gameView );
    }
}
