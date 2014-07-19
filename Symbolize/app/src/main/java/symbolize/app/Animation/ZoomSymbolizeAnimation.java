package symbolize.app.Animation;


import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.LinearLayout;

import symbolize.app.Common.Posn;
import symbolize.app.Game.GameActivity;

public class ZoomSymbolizeAnimation extends SymbolizeAnimation {
    // Inherited fields
    //------------------

    /*
    protected Animation animation;
    protected LinearLayout linearLayout;

    protected LinkedList<Line> graph;
    protected ArrayList<Posn> levels;
    */

    // Field
    //------

    Posn pivot;
    float scale_horizontal;
    float scale_vertical;


    // Constructor
    //------------
    public ZoomSymbolizeAnimation( final LinearLayout linearLayout,
                                   final float scale_horizontal, final float scale_vertical )
    {
        super( linearLayout );
        this.scale_horizontal = scale_horizontal;
        this.scale_vertical = scale_vertical;
    }


    // Setter method
    //---------------

    public void Set_pivot( Posn pivot ) {
        this.pivot = pivot;
        animation = new ScaleAnimation( 1, scale_horizontal, 1, scale_vertical,
                Animation.RELATIVE_TO_SELF, (float) pivot.x() / GameActivity.SCALING ,
                Animation.RELATIVE_TO_SELF, (float) pivot.y() / GameActivity.SCALING  );
        animation.setDuration( ZOOMDURATION );
    }
}
