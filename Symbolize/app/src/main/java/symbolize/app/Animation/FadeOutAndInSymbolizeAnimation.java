package symbolize.app.Animation;

import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.LinkedList;

import symbolize.app.Common.Line;
import symbolize.app.Common.Posn;
import symbolize.app.Game.GameView;

public class FadeOutAndInSymbolizeAnimation extends  SymbolizeDualAnimation {
    // Inherited fields
    //------------------

    /*
    protected Animation animation;
    protected Animation animation_2;
    protected LinearLayout linearLayout;

    protected LinkedList<Line> graph;
    protected ArrayList<Posn> levels;
    */


    // Constructor
    //------------

    public FadeOutAndInSymbolizeAnimation( final LinearLayout linearLayout )
    {
        super( linearLayout );
        animation = new AlphaAnimation( 1, 0 );
        animation.setDuration( FADEDURATION );

        animation_2 = new AlphaAnimation( 0, 1 );
        animation_2.setDuration( FADEDURATION );
    }
}
