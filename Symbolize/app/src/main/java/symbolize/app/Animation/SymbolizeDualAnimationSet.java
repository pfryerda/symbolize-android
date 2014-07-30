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

    SymbolizeAnimation.SymbolizeAnimationListener listener_2;
    ArrayList<SymbolizeAnimation> animations_2;


    // Constructor
    //-------------

    public SymbolizeDualAnimationSet() {
        super();
        animations_2 = new ArrayList<SymbolizeAnimation>();
    }


    // Public methods
    //----------------


    public void Add_animation_2( SymbolizeAnimation animation ) {
        animations_2.add( animation );
    }

    @Override
    public void Animate( final LinearLayout linearLayout ) {
        final AnimationSet animationSet_1 = build_animationset( animations, linearLayout );
        final AnimationSet animationSet_2 = build_animationset( animations_2, linearLayout );
        animationSet_1.setAnimationListener( new Animation.AnimationListener() {
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
                linearLayout.startAnimation( animationSet_2 );
            }
            @Override
            public void onAnimationRepeat( Animation animation ) {}
        });
        animationSet_2.setAnimationListener( new Animation.AnimationListener() {
            @Override
            public void onAnimationStart( Animation animation ) {
                GameAnimationHandler.InAnimation = true;
            }
            @Override
            public void onAnimationEnd( Animation animation ) {
                linearLayout.clearAnimation();
                if ( listener_2 != null ) {
                    listener_2.onSymbolizeAnimationEnd();
                }
                GameAnimationHandler.InAnimation = false;
            }
            @Override
            public void onAnimationRepeat( Animation animation ) {}
        });

        linearLayout.startAnimation( animationSet_1 );
    }

    public void SetSymbolizeAnimationListener_2( SymbolizeAnimation.SymbolizeAnimationListener listener ) {
        this.listener_2 = listener;
    }
}
