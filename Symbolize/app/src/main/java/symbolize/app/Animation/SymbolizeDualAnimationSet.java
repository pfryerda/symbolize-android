package symbolize.app.Animation;


import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.LinkedList;

import symbolize.app.Common.Line;
import symbolize.app.Common.Posn;
import symbolize.app.Game.GameView;

public class SymbolizeDualAnimationSet extends SymbolizeAnimationSet {
    // Inherited Fields
    //--------------------

    /*
    ArrayList<SymbolizeAnimation> animations;
    LinearLayout linearLayout;
    */


    // Field
    //-------

    ArrayList<SymbolizeAnimation> animations_2;


    // Constructor
    //-------------

    public SymbolizeDualAnimationSet( final  LinearLayout linearLayout ) {
        super( linearLayout );
        animations_2 = new ArrayList<SymbolizeAnimation>();
    }


    // Public methods
    //----------------


    public void Add_animation_2( SymbolizeAnimation animation ) {
        animations_2.add( animation );
    }

    @Override
    public void Animate( final GameView game_view,
                         final LinkedList<Line> graph, final ArrayList<Posn> levels )
    {
        final AnimationSet animationSet_1 = build_animationset( animations, game_view, graph, levels);
        final AnimationSet animationSet_2 = build_animationset( animations_2, game_view, graph, levels );
        animationSet_1.setAnimationListener( new Animation.AnimationListener() {
            @Override
            public void onAnimationStart( Animation animation ) {
                GameAnimationHandler.InAnimation = true;
            }
            @Override
            public void onAnimationEnd( Animation animation ) {
                linearLayout.clearAnimation();
                game_view.Render_foreground( graph, levels );
                GameAnimationHandler.InAnimation = false;
                linearLayout.startAnimation( animationSet_2 );
            }
            @Override
            public void onAnimationRepeat( Animation animation ) {}
        });

        linearLayout.startAnimation( animationSet_1 );
    }
}
