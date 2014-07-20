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
        this.animations = new ArrayList<SymbolizeAnimation>();
        this.linearLayout = linearLayout;

    }


    // Public methods
    //---------------

    public void Add_animation( SymbolizeAnimation animation ) {
        animations.add( animation );
    }

    public void Animate( final GameView game_view,
                         final LinkedList<Line> graph, final ArrayList<Posn> levels )
    {
        AnimationSet animationSet = new AnimationSet( true );
        for ( SymbolizeAnimation animation : animations ) {
            animation.Set_up_animation( game_view, graph, levels );
            animationSet.addAnimation( animation.Get_animation() );
        }

        linearLayout.startAnimation( animationSet );
    }


    // Getter methods
    //---------------

    public ArrayList<SymbolizeAnimation> Get_animations() {
        return animations;
    }
}
