package symbolize.app.Common.Animation;

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

    protected LinkedList<Line> graph;
    protected ArrayList<Posn> levels;


    // Public Methods
    //----------------

    /*
     * Used during construction simplifies constructor of concrete classes
     *
     * @param LinearLayout linearlayour: The linearlayout that will be animated
     * @param GameView gameView: The game view that will be render after the animation
     */
    public void Set_up( LinearLayout linearlayour, GameView gameView ) {
        this.linearLayout = linearlayour;
        set_up_animation( gameView );
    }

    /*
     * Actually performs the animation and re renders the canvas
     *
     * @param LinkedList<Line> graph: The desired graph to be rendered
     * @param ArrayList<Posn> levels: The desired points to be rendered
     */
    public void Animate( LinkedList<Line> graph, ArrayList<Posn> levels ) {
        this.graph = graph;
        this.levels = levels;
        linearLayout.startAnimation( animation );
    }


    // Protected methods
    //-------------------

    /*
     * Used to set up the animation
     *    - sets up the InAnimation variable properly
     *    - sets up the rendering of the graph after the animation
     * @param: GameView gameView: The game view that will be rendered after the animation'
     */
    protected void set_up_animation( final GameView gameView ) {
        this.animation.setAnimationListener( new Animation.AnimationListener() {
            @Override
            public void onAnimationStart( Animation animation ) {
                InAnimation = true;
            }
            @Override
            public void onAnimationEnd( Animation animation ) {
                linearLayout.clearAnimation();
                gameView.Render_foreground( graph, levels );
                InAnimation = false;
            }
            @Override
            public void onAnimationRepeat( Animation animation ) {}
        });
    }
}
