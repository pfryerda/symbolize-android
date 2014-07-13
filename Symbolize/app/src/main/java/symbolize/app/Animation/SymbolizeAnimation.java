package symbolize.app.Animation;

import android.view.animation.Animation;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.LinkedList;

import symbolize.app.Common.Line;
import symbolize.app.Common.Posn;
import symbolize.app.Game.GameView;

abstract public class SymbolizeAnimation {
    // Static Fields
    //--------------
    public static boolean InAnimation = false;
    public static final int ROTATEDURATION = 450;
    public static final int FLIPDURATION = 450;
    public static final int FADEDURATION = 450;

    // Fields
    //--------
    protected Animation animation;
    protected LinearLayout linearLayout;


    // Constructor
    //--------------

    SymbolizeAnimation( LinearLayout linarLayout ) {
        this.linearLayout = linarLayout;
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
        set_up_animation( game_view, graph, levels );
        linearLayout.startAnimation( animation );
    }


    // Protected method
    //------------------

    /*
     * Used to set up the animation
     *    - sets up the InAnimation variable properly
     *    - sets up the rendering of the graph after the animation
     * @param: GameView game_view: The game view that will be rendered after the animation'
     */
    protected void set_up_animation( final GameView game_view,
                                     final LinkedList<Line> graph, final ArrayList<Posn> levels )
    {
        this.animation.setAnimationListener( new Animation.AnimationListener() {
            @Override
            public void onAnimationStart( Animation animation ) {
                InAnimation = true;
            }
            @Override
            public void onAnimationEnd( Animation animation ) {
                linearLayout.clearAnimation();
                game_view.Render_foreground( graph, levels );
                InAnimation = false;
            }
            @Override
            public void onAnimationRepeat( Animation animation ) {}
        });
    }
}
