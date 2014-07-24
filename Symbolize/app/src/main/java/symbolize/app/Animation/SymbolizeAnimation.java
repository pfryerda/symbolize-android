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
    protected Animation animation;
    protected LinearLayout linearLayout;


    // Constructor
    //--------------

    public SymbolizeAnimation( final LinearLayout linarLayout, final Animation animation,
                               final int duration, final boolean fill_after )
    {
        this.linearLayout = linarLayout;
        this.animation = animation;
        animation.setDuration( duration );
        animation.setFillAfter( fill_after );
    }

    public SymbolizeAnimation( final LinearLayout linarLayout, final Animation animation,
                               final int duration )
    {
        this.linearLayout = linarLayout;
        this.animation = animation;
        this.animation.setDuration( duration );
        this.animation.setFillAfter( false );
    }


    // Public method
    //---------------

    /*
     * Actually performs the animation and re renders the canvas
     *
     * @param LinkedList<Line> graph: The desired graph to be rendered
     * @param ArrayList<Posn> levels: The desired points to be rendered
     */
    public void Animate( final GameView game_view, final LinkedList<Line> graph, final ArrayList<Posn> levels ) {
        Set_up_animation( game_view, graph, levels );
        linearLayout.startAnimation( animation );
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
    public void Set_up_animation( final GameView game_view,
                                     final LinkedList<Line> graph, final ArrayList<Posn> levels )
    {
        this.animation.setAnimationListener( new Animation.AnimationListener() {
            @Override
            public void onAnimationStart( Animation animation ) {
                GameAnimationHandler.InAnimation = true;
            }
            @Override
            public void onAnimationEnd( Animation animation ) {
                linearLayout.clearAnimation();
                game_view.Render_foreground( graph, levels );
                GameAnimationHandler.InAnimation = false;
            }
            @Override
            public void onAnimationRepeat( Animation animation ) {}
        });
    }
}
