package symbolize.app.Animation;

import android.view.animation.Animation;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.LinkedList;

import symbolize.app.Common.Line;
import symbolize.app.Common.Posn;
import symbolize.app.Game.GameView;

public class SymbolizeAnimation {
    // Fields
    //--------

    protected SymbolizeAnimationListener listener;
    protected Animation animation;


    // Interface setup
    //-----------------

    public interface SymbolizeAnimationListener {
        public void onSymbolizeAnimationEnd();
    }


    // Constructor
    //--------------

    public SymbolizeAnimation( final Animation animation, final int duration, final boolean fill_after ) {
        this.animation = animation;
        animation.setDuration( duration );
        animation.setFillAfter( fill_after );
    }


    // Public method
    //---------------

    /*
     * Actually performs the animation and re renders the canvas
     *
     * @param LinkedList<Line> graph: The desired graph to be rendered
     * @param ArrayList<Posn> levels: The desired points to be rendered
     */
    public void Animate( final LinearLayout linearLayout ) {
        Set_up_animation( linearLayout );
        linearLayout.startAnimation( animation );
    }

    public void SetSymbolizeAnimationListener( SymbolizeAnimationListener listener ) {
        this.listener = listener;
    }


    // Getter method
    //---------------

    public Animation Get_animation() {
        return animation;
    }

    // Protected method
    //------------------

    /*
     * Used to set up the animation
     *    - sets up the InAnimation variable properly
     *    - sets up the rendering of the graph after the animation
     * @param: GameView game_view: The game view that will be rendered after the animation'
     */
    public void Set_up_animation( final LinearLayout linearLayout )
    {
        this.animation.setAnimationListener( new Animation.AnimationListener() {
            @Override
            public void onAnimationStart( Animation animation ) {
                GameAnimationHandler.InAnimation = true;
            }
            @Override
            public void onAnimationEnd( Animation animation ) {
                linearLayout.clearAnimation();
                if ( listener != null ) {
                    listener.onSymbolizeAnimationEnd();
                }
                GameAnimationHandler.InAnimation = false;
            }
            @Override
            public void onAnimationRepeat( Animation animation ) {}
        });
    }
}
