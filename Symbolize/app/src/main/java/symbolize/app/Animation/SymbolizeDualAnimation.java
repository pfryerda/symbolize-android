package symbolize.app.Animation;

import android.view.animation.Animation;
import android.widget.LinearLayout;
import java.util.ArrayList;
import java.util.LinkedList;
import symbolize.app.Common.Line;
import symbolize.app.Common.Posn;
import symbolize.app.Game.GameView;

public class SymbolizeDualAnimation extends SymbolizeAnimation {
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

    protected SymbolizeAnimationListener listener_2;
    protected Animation animation_2;


    // Constructor
    //-------------

    public SymbolizeDualAnimation( final Animation animation_1, final int duration_1, final boolean fill_after_1,
                                   final Animation animation_2, final int duration_2, final boolean fill_after_2 ) {
        super( animation_1, duration_1, fill_after_1 );
        this.animation_2 = animation_2;
        this.animation_2.setDuration( duration_2 );
        this.animation_2.setFillAfter( fill_after_2 );
    }


    // Public method
    //---------------

    /// @see SymbolizeAnimation::set_up_animation
    @Override
    public void Set_up_animation( final LinearLayout linearLayout )
    {
        this.animation.setAnimationListener( new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                GameAnimationHandler.InAnimation = true;
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                linearLayout.clearAnimation();
                if ( listener != null ) {
                    listener.onSymbolizeAnimationEnd();
                }
                linearLayout.startAnimation( animation_2 );
                GameAnimationHandler.InAnimation = false;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        } );

        this.animation_2.setAnimationListener( new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                GameAnimationHandler.InAnimation = true;
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                linearLayout.clearAnimation();
                if ( listener_2 != null ) {
                    listener_2.onSymbolizeAnimationEnd();
                }
                GameAnimationHandler.InAnimation = false;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        } );
    }

    public void SetSymbolizeAnimationListener_2( SymbolizeAnimationListener listener ) {
        this.listener_2 = listener;
    }
}
