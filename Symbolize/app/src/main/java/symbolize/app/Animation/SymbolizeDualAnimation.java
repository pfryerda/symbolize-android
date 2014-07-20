package symbolize.app.Animation;

import android.view.animation.Animation;
import android.widget.LinearLayout;
import java.util.ArrayList;
import java.util.LinkedList;
import symbolize.app.Common.Line;
import symbolize.app.Common.Posn;
import symbolize.app.Game.GameView;

abstract public class SymbolizeDualAnimation extends SymbolizeAnimation {
    // Inherited fields
    //------------------

    /*
    protected Animation animation;
    protected LinearLayout linearLayout;

    protected LinkedList<Line> graph;
    protected ArrayList<Posn> levels;
    */

    // Field
    //-------

    protected Animation animation_2;


    // Constructor
    //-------------

    public SymbolizeDualAnimation( final LinearLayout linearLayout ) {
        super( linearLayout );
    }


    // Public method
    //---------------

    /// @see SymbolizeAnimation::set_up_animation
    @Override
    public void Set_up_animation( final GameView game_view,
                                  final LinkedList<Line> graph, final ArrayList<Posn> levels )
    {
        animation.setFillAfter( true );
        animation_2.setFillAfter( true );
        this.animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                InAnimation = true;
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                linearLayout.clearAnimation();
                game_view.Render_foreground( graph, levels );
                linearLayout.startAnimation( animation_2 );
                InAnimation = false;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
    }
}
