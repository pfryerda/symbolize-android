package symbolize.app.Animation;

import android.view.animation.Animation;
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

    SymbolizeAnimation.SymbolizeAnimationListener listener;
    ArrayList<SymbolizeAnimation> animations;


    // Constructor
    //-------------

    public SymbolizeAnimationSet() {
        this.animations = new ArrayList<SymbolizeAnimation>();
    }


    // Public methods
    //---------------

    public void Add_animation( final SymbolizeAnimation animation ) {
        animations.add( animation );
    }

    public void Animate( final LinearLayout linearLayout )
    {
        AnimationSet animationSet = build_animationset( animations, linearLayout );
        animationSet.setAnimationListener( new Animation.AnimationListener() {
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

        linearLayout.startAnimation( animationSet );
    }

    public void SetSymbolizeAnimationListener( SymbolizeAnimation.SymbolizeAnimationListener listener ) {
        this.listener = listener;
    }


    // Getter methods
    //---------------

    public ArrayList<SymbolizeAnimation> Get_animations() {
        return animations;
    }


    // Protected method
    //-----------------

    protected AnimationSet build_animationset( final ArrayList<SymbolizeAnimation> sym_animations,
                                               final LinearLayout linearLayout )
    {
        AnimationSet animationSet = new AnimationSet( true );
        for ( SymbolizeAnimation animation : sym_animations ) {
            animation.Set_up_animation( linearLayout );
            animationSet.addAnimation( animation.Get_animation() );
        }
        return animationSet;
    }
}
