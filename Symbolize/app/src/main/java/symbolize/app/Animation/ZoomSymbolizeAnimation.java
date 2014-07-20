package symbolize.app.Animation;


import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.LinkedList;

import symbolize.app.Common.Line;
import symbolize.app.Common.Posn;
import symbolize.app.Game.GameActivity;
import symbolize.app.Game.GameView;

public class ZoomSymbolizeAnimation extends SymbolizeDualAnimation {
    // Inherited fields
    //------------------

    /*
    protected Animation animation;
    proteceted Animation animation_2;
    protected LinearLayout linearLayout;

    protected LinkedList<Line> graph;
    protected ArrayList<Posn> levels;
    */


    // Fields
    //-------

    private float scale_horizontal_1;
    private float scale_vertical_1;
    private float scale_horizontal_2;
    private float scale_vertical_2;


    // Constructor
    //------------
    public ZoomSymbolizeAnimation( final LinearLayout linearLayout,
                                   final float scale_horizontal_1, final float scale_vertical_1,
                                   final float scale_horizontal_2, final float scale_vertical_2 )
    {
        super( linearLayout );
        this.scale_horizontal_1 = scale_horizontal_1;
        this.scale_vertical_1 = scale_vertical_1;
        this.scale_horizontal_2 = scale_horizontal_2;
        this.scale_vertical_2 = scale_vertical_2;
    }


    // Setter method
    //---------------

    public void Set_pivot( Posn pivot ) {
        animation = new ScaleAnimation( 1, scale_horizontal_1, 1, scale_vertical_1,
                Animation.RELATIVE_TO_SELF, (float) pivot.x() / GameActivity.SCALING ,
                Animation.RELATIVE_TO_SELF, (float) pivot.y() / GameActivity.SCALING  );
        animation.setDuration( ZOOMDURATION );
        this.animation_2 = new ScaleAnimation( scale_horizontal_2, 1, scale_vertical_2, 1,
                Animation.RELATIVE_TO_SELF, (float) pivot.x() / GameActivity.SCALING ,
                Animation.RELATIVE_TO_SELF, (float) pivot.y() / GameActivity.SCALING  );
        this.animation_2.setDuration( ZOOMDURATION );
    }
}
