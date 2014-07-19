package symbolize.app.Animation;


import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;

public class TranslateSymbolizeAnimation extends SymbolizeAnimation {
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
    public TranslateSymbolizeAnimation( final LinearLayout linearLayout,
                                        final int delta_x, final int delta_y )
    {
        super( linearLayout );
        animation = new TranslateAnimation( Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, delta_x,
                                            Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, delta_y );
        animation.setDuration( TRANSLATEDURATION );
    }
}
