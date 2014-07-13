package symbolize.app.Animation;


import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.LinearLayout;

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

    public RotateSymbolizeAnimation( final LinearLayout linearLayout, final int rotation ) {
        super( linearLayout );
        animation = new RotateAnimation( 0, rotation,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f );
        animation.setDuration( ROTATEDURATION );
    }
}
