package symbolize.app.Animation;

import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.LinearLayout;
import symbolize.app.Common.Posn;
import symbolize.app.Game.GameActivity;

public class SymbolizeZoomAnimation extends SymbolizeAnimation {
    // Inherited fields
    //-------------------

    /*
    protected Animation animation;
    protected LinearLayout linearLayout;
    */

    // Fields
    //-------

    final float toX, fromX, toY, fromY;
    final int duration;
    final boolean fill_after;


    // Constructor
    //-------------

    public SymbolizeZoomAnimation( final LinearLayout linarLayout, final float fromX, final float toX,
                                   final float fromY, final float toY, final int duration, final boolean fill_after )
    {
        super( linarLayout, new ScaleAnimation( fromX, toX, fromY, toY, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f ), duration, fill_after );

        this.duration = duration;
        this.fill_after = fill_after;

        this.toX = toX;
        this.fromX = fromX;
        this.toY = toY;
        this.fromY = fromY;
    }


    // Public method
    //---------------

    public void Set_pivot( Posn pivot ) {
        animation = new ScaleAnimation( fromX, toX, fromY, toY,
                Animation.RELATIVE_TO_SELF, (float) pivot.x() / GameActivity.SCALING,
                Animation.RELATIVE_TO_SELF, (float) pivot.y() / GameActivity.SCALING );
        animation.setDuration( duration );
        animation.setFillAfter( fill_after );
    }

}
