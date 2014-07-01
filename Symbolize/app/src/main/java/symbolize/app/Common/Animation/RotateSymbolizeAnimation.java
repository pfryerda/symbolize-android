package symbolize.app.Common.Animation;


import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.LinearLayout;

import symbolize.app.Game.GameView;

public class RotateSymbolizeAnimation extends SymbolizeAnimation {
    // Inherited fields
    //------------------

    /*
    protected Animation animation;
    protected LinearLayout linearLayout;

    protected LinkedList<Line> graph;
    protected ArrayList<Posn> levels;
    */


    // Constructor
    //------------

    public RotateSymbolizeAnimation( LinearLayout linearLayout, final GameView game_view, int rotation ) {
        animation = new RotateAnimation( 0, rotation, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f );
        animation.setDuration( ROTATEDURATION );
        Set_up( linearLayout, game_view );
    }
}
