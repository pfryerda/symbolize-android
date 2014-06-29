package symbolize.app.Common.Animation;

import android.view.animation.Animation;
import android.widget.LinearLayout;
import symbolize.app.Game.GameView;

abstract public class SymbolizeAnimation {
    // Static Fields
    //--------------
    boolean InAnimation = false;
    public static final int ROTATEDURATION = 450;
    public static final int FLIPDURATION = 450;
    public static final int FADEDURATION = 450;

    // Fields
    //--------
    protected Animation animation;
    protected LinearLayout linearLayout;


    // Methods
    //----------

    public void Set_up( LinearLayout linearlayour, GameView gameView ) {
        this.linearLayout = linearlayour;
        set_up_animation( gameView );
    }

    public void animate() {
        linearLayout.startAnimation( animation );
    }

    protected void set_up_animation( final GameView gameView ) {
        this.animation.setAnimationListener( new Animation.AnimationListener() {
            @Override
            public void onAnimationStart( Animation animation ) {
                InAnimation = true;
            }
            @Override
            public void onAnimationEnd( Animation animation ) {
                linearLayout.clearAnimation();
                gameView.renderGraph();
                InAnimation = false;
            }
            @Override
            public void onAnimationRepeat( Animation animation ) {}
        });
    }
}
