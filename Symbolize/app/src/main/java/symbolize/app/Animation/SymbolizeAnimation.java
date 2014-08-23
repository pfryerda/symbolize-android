package symbolize.app.Animation;

import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.widget.LinearLayout;
import java.util.ArrayList;

import symbolize.app.R;

/*
 * A Generic animation class that contains a list of animationsets to be played one after another,
 * where an animationset is a set of animations to be played together
 */
public class SymbolizeAnimation {
    // Static fields
    //--------------

    public static boolean InAnimation = false;


    // Fields
    //--------

    private SymbolizeAnimationListener listener;
    private ArrayList<AnimationSet> animations;


    // Interface setup
    //-----------------

    public interface SymbolizeAnimationListener {
        public void onSymbolizeAnimationClear();
        public void onSymbolizeAnimationMiddle();
        public void onSymbolizeAnimationEnd();
    }


    // Constructor
    //--------------

    public SymbolizeAnimation() {
        animations = new ArrayList<AnimationSet>();
    }


    // Setter methods
    //----------------

    public void SetSymbolizeAnimationListener( final SymbolizeAnimationListener listener ) {
        this.listener = listener;
    }


    // Main method
    //--------------

    public void Animate( final LinearLayout linearLayout ) {
        if ( animations.size() >= 0 && !InAnimation ) {
            if ( animations.size() == 1 ) {
                animations.get( 0 ).setAnimationListener( new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart( Animation animation ) {
                        linearLayout.findViewById( R.id.game_adspace ).clearAnimation();
                        InAnimation = true;
                    }

                    @Override
                    public void onAnimationEnd( Animation animation ) {
                        linearLayout.clearAnimation();
                        listener.onSymbolizeAnimationEnd();
                        InAnimation = false;
                    }

                    @Override
                    public void onAnimationRepeat( Animation animation ) {}
                } );
            } else {
                animations.get( 0 ).setAnimationListener( new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        InAnimation = true;
                    }

                    @Override
                    public void onAnimationEnd( Animation animation ) {
                        linearLayout.clearAnimation();
                        listener.onSymbolizeAnimationMiddle();
                        linearLayout.startAnimation( animations.get( 1 ) );
                    }

                    @Override
                    public void onAnimationRepeat( Animation animation ) {}
                } );

                for ( int i = 1; i < animations.size() - 1; ++i ) {
                    final AnimationSet current_animation_set = animations.get( i );
                    final AnimationSet next_animation_set = animations.get( i + 1 );

                    current_animation_set.setAnimationListener( new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart( Animation animation ) {}

                        @Override
                        public void onAnimationEnd( Animation animation ) {
                            linearLayout.clearAnimation();
                            listener.onSymbolizeAnimationMiddle();
                            linearLayout.startAnimation( next_animation_set );
                        }

                        @Override
                        public void onAnimationRepeat( Animation animation ) {}
                    } );
                }

                animations.get( animations.size() - 1 ).getAnimations().get( 0 ).setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart( Animation animation ) {
                    }

                    @Override
                    public void onAnimationEnd( Animation animation ) {
                        linearLayout.clearAnimation();
                        listener.onSymbolizeAnimationEnd();
                        InAnimation = false;
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }
                });
            }

            linearLayout.startAnimation( animations.get( 0 ) );
        }
    }


    // Public methods
    //----------------

    /*
     * Adds an animation to the current animation set to played along side the other
     * animations also in the set
     *
     * @param Animation animation: The animation you wish to add
     * @param int duration: The duration of the animation
     * @param boolean fill_after: Whether the affects of the animation should stay on the element afterwards
     */
    public void Add_animation( final Animation animation, final int duration, final boolean fill_after ) {
        animation.setDuration( duration );
        animation.setFillAfter( fill_after );
        animation.setAnimationListener( new Animation.AnimationListener() {
            @Override
            public void onAnimationStart( Animation animation ) {}

            @Override
            public void onAnimationEnd( Animation animation ) {
                listener.onSymbolizeAnimationClear();
            }

            @Override
            public void onAnimationRepeat( Animation animation ) {}
        } );

        AnimationSet animationSet = animations.get( animations.size() - 1 );
        animationSet.addAnimation( animation );
    }

    /*
     * Start a new animation set to start adding animations too, this new set will be played
     * immediately following the previous animation set.
     */
    public void Start_new_set() {
        AnimationSet animationSet = new AnimationSet( true );
        this.animations.add( animationSet );
    }
}
