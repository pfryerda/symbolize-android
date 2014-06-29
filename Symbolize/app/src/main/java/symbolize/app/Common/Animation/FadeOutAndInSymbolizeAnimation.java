package symbolize.app.Common.Animation;

import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.LinearLayout;

import symbolize.app.Game.GameView;

public class FadeOutAndInSymbolizeAnimation extends  SymbolizeAnimation {
    // Fields
    //--------

    private AlphaAnimation fadeInAnimation;

    // Constructor
    //------------

    public FadeOutAndInSymbolizeAnimation(LinearLayout linearLayout, GameView gameView) {
        animation = new AlphaAnimation(1, 0);
        animation.setDuration( FADEDURATION );
        animation.setFillAfter( true );

        fadeInAnimation = new AlphaAnimation( 0, 1 );
        fadeInAnimation.setDuration( FADEDURATION );
        fadeInAnimation.setFillAfter( true );

        Set_up( linearLayout, gameView );
    }


    // Methods
    //--------

    protected void set_up_animation( final GameView gameView ) {

        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                InAnimation = true;
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                linearLayout.clearAnimation();
                gameView.renderGraph();
                linearLayout.startAnimation(fadeInAnimation);
                InAnimation = false;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
    }
}
