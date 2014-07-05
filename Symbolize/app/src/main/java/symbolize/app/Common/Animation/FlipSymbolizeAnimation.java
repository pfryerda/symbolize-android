package symbolize.app.Common.Animation;


import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.LinearLayout;

import symbolize.app.Game.GameView;

public class FlipSymbolizeAnimation extends SymbolizeAnimation {
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

    public FlipSymbolizeAnimation( final LinearLayout linearLayout,
                                   final int scale_horizontal, final int scale_vertical )
    {
        super( linearLayout );
        animation = new ScaleAnimation( 1, scale_horizontal, 1, scale_vertical,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f );
        animation.setDuration( FLIPDURATION );
    }
}
