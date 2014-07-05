package symbolize.app.Common.Animation;

import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.LinkedList;

import symbolize.app.Common.Line;
import symbolize.app.Common.Posn;
import symbolize.app.Game.GameView;

public class FadeOutAndInSymbolizeAnimation extends  SymbolizeAnimation {
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

    private final AlphaAnimation fadeInAnimation;


    // Constructor
    //------------

    public FadeOutAndInSymbolizeAnimation( final LinearLayout linearLayout )
    {
        super( linearLayout );
        animation = new AlphaAnimation(1, 0);
        animation.setDuration( FADEDURATION );
        animation.setFillAfter( true );

        fadeInAnimation = new AlphaAnimation( 0, 1 );
        fadeInAnimation.setDuration( FADEDURATION );
        fadeInAnimation.setFillAfter( true );
    }


    // Protected Methods
    //--------------------

    /// @see SymbolizeAnimation::set_up_animation
    @Override
    protected void set_up_animation( final GameView game_view,
                                     final LinkedList<Line> graph, final ArrayList<Posn> levels )
    {

        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                InAnimation = true;
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                linearLayout.clearAnimation();
                game_view.Render_foreground( graph, levels );
                linearLayout.startAnimation( fadeInAnimation );
                InAnimation = false;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
    }
}
