package symbolize.app.Animation;


import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import symbolize.app.Common.Enum.Action;
import symbolize.app.Common.Line;
import symbolize.app.Common.Posn;
import symbolize.app.Common.Request;
import symbolize.app.Game.GameView;

public class GameAnimationHandler {
    // Static fields
    //----------------

    public static boolean InAnimation = false;
    public static final int ZOMMSCALING = 4;

    public static final int ROTATEDURATION = 450;
    public static final int FLIPDURATION = 450;
    public static final int FADEDURATION = 600;
    public static final int ZOOMDURATION = 600;
    public static final int TRANSLATEDURATION = 650;

    
    // Fields
    //-------

    private final HashMap<Action, SymbolizeAnimation> animations;
    private final HashMap<Action, SymbolizeAnimationSet> animationsets;
    private final ArrayList<SymbolizeZoomAnimation> zoom_animations;
    
    
    // Constructor
    //-------------
    
    public GameAnimationHandler( LinearLayout linearLayout ) {
        // Basic animations
        animations = new HashMap<Action, SymbolizeAnimation>();
        animations.put( Action.Rotate_right, new SymbolizeAnimation( linearLayout,
                new RotateAnimation( 0, 90, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f ),
                ROTATEDURATION ) );
        animations.put( Action.Rotate_left, new SymbolizeAnimation( linearLayout,
                new RotateAnimation( 0, -90, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f ),
                ROTATEDURATION ) );
        animations.put( Action.Flip_horizontally, new SymbolizeAnimation( linearLayout,
                new ScaleAnimation( 1, -1, 1, 1, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f ),
                FLIPDURATION ) );
        animations.put( Action.Flip_vertically, new SymbolizeAnimation( linearLayout,
                new ScaleAnimation( 1, 1, 1, -1, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f ),
                FLIPDURATION ) );
        animations.put( Action.Shift, new SymbolizeDualAnimation( linearLayout,
                new AlphaAnimation( 1, 0 ),
                FADEDURATION,
                true,
                new AlphaAnimation( 0, 1 ),
                FADEDURATION,
                true) );
        animations.put( Action.Load_world_left, new SymbolizeDualAnimation( linearLayout,
                new TranslateAnimation( Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 1,
                        Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0 ),
                TRANSLATEDURATION,
                false,
                new TranslateAnimation( Animation.RELATIVE_TO_SELF, -1, Animation.RELATIVE_TO_SELF, 0,
                        Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0 ),
                TRANSLATEDURATION,
                true ) );
        animations.put( Action.Load_world_right, new SymbolizeDualAnimation( linearLayout,
                new TranslateAnimation( Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, -1,
                        Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0 ),
                TRANSLATEDURATION,
                false,
                new TranslateAnimation( Animation.RELATIVE_TO_SELF, 1, Animation.RELATIVE_TO_SELF, 0,
                        Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0 ),
                TRANSLATEDURATION,
                true ) );

        animationsets = new HashMap<Action, SymbolizeAnimationSet>();

        // Zoom animations

        // Zoom in
        SymbolizeDualAnimationSet zoom_in_animation = new SymbolizeDualAnimationSet( linearLayout );
        zoom_in_animation.Add_animation(
                new SymbolizeAnimation( linearLayout, new AlphaAnimation( 1, 0 ), FADEDURATION, true )
        );
        SymbolizeZoomAnimation zoom_animation_1 =
                new SymbolizeZoomAnimation( linearLayout, 1, ZOMMSCALING, 1, ZOMMSCALING,
                        ZOOMDURATION, true );
        zoom_in_animation.Add_animation( zoom_animation_1 );
        zoom_in_animation.Add_animation_2(
                new SymbolizeAnimation( linearLayout, new AlphaAnimation( 0, 1 ), FADEDURATION, true )
        );
        SymbolizeZoomAnimation zoom_animation_2 =
                new SymbolizeZoomAnimation(linearLayout, ZOMMSCALING, 1, ZOMMSCALING, 1,
                        ZOOMDURATION, true );
        zoom_in_animation.Add_animation_2( zoom_animation_2 );
        animationsets.put( Action.Load_level, zoom_in_animation );

        // Zoom out
        SymbolizeDualAnimationSet zoom_out_animation = new SymbolizeDualAnimationSet( linearLayout );
        zoom_out_animation.Add_animation(
                new SymbolizeAnimation( linearLayout,  new AlphaAnimation( 1, 0 ), FADEDURATION, true )
        );
        SymbolizeZoomAnimation zoom_animation_3 =
                new SymbolizeZoomAnimation( linearLayout, 1, (float) 1/ZOMMSCALING, 1, (float) 1/ZOMMSCALING,
                        ZOOMDURATION, true );
        zoom_out_animation.Add_animation( zoom_animation_3 );
        zoom_out_animation.Add_animation_2(
                new SymbolizeAnimation( linearLayout, new AlphaAnimation( 0, 1 ), FADEDURATION, true )
        );
        SymbolizeZoomAnimation zoom_animation_4 =
                new SymbolizeZoomAnimation( linearLayout, ZOMMSCALING, 1, ZOMMSCALING, 1, ZOOMDURATION, true );
        zoom_out_animation.Add_animation_2( zoom_animation_4 );
        animationsets.put( Action.Load_world_via_level, zoom_out_animation );

        zoom_animations = new ArrayList<SymbolizeZoomAnimation>();
        zoom_animations.add( zoom_animation_1 );
        zoom_animations.add( zoom_animation_2 );
        zoom_animations.add( zoom_animation_3 );
        zoom_animations.add( zoom_animation_4 );
    }


    // Public methods
    //----------------

    public void Handle_request( final  Request request )
    {
        switch ( request.action ) {
            case Load_level:
            case Load_world_via_level:
                animationsets.get( request.action ).Animate( request.game_view, request.graph, request.levels );
                break;

            default:
                animations.get( request.action ).Animate( request.game_view, request.graph, request.levels );
        }
    }

    public void Set_zoom_pivots( final Posn pivot )
    {
        for ( SymbolizeZoomAnimation zoom_animation : zoom_animations ) {
            zoom_animation.Set_pivot( pivot );
        }
    }
}
