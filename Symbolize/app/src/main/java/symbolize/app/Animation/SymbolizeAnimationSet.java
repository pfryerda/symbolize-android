package symbolize.app.Animation;

import android.view.animation.AnimationSet;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.LinkedList;

import symbolize.app.Common.Line;
import symbolize.app.Common.Posn;
import symbolize.app.Game.GameView;

public class SymbolizeAnimationSet {
    // Fields
    //-------

    ArrayList<SymbolizeAnimation> animations;
    LinearLayout linearLayout;


    // Constructor
    //-------------

    public SymbolizeAnimationSet( LinearLayout linearLayout ) {
        this.linearLayout = linearLayout;
        this.animations = new ArrayList<SymbolizeAnimation>();
    }


    // Public methods
    //---------------

    public void Add_animation( final SymbolizeAnimation animation ) {
        animations.add( animation );
    }

    public void Animate( final GameView game_view,
                         final LinkedList<Line> graph, final ArrayList<Posn> levels )
    {
        AnimationSet animationSet = build_animationset( animations, game_view, graph, levels );
        linearLayout.startAnimation( animationSet );
    }


    // Getter methods
    //---------------

    public ArrayList<SymbolizeAnimation> Get_animations() {
        return animations;
    }


    // Protected method
    //-----------------

    protected AnimationSet build_animationset( final ArrayList<SymbolizeAnimation> sym_animations, final GameView game_view,
                                               final LinkedList<Line> graph, final ArrayList<Posn> levels )
    {
        AnimationSet animationSet = new AnimationSet( true );
        for ( SymbolizeAnimation animation : sym_animations ) {
            animation.Set_up_animation( game_view, graph, levels );
            animationSet.addAnimation( animation.Get_animation() );
        }
        return animationSet;
    }
}
