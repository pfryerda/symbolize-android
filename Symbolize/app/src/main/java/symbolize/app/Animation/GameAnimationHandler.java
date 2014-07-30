package symbolize.app.Animation;


import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;
import java.util.ArrayList;
import java.util.HashMap;
import symbolize.app.Common.Posn;
import symbolize.app.Common.Request;

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

    private final HashMap<Integer, SymbolizeAnimation> animations;
    private final HashMap<Integer, SymbolizeDualAnimation> dual_animations;
    private final HashMap<Integer, SymbolizeAnimationSet> animationsets;
    private final HashMap<Integer, SymbolizeDualAnimationSet> dual_animationsets;
    private final ArrayList<SymbolizeZoomAnimation> zoom_animations;
    
    
    // Constructor
    //-------------
    
    public GameAnimationHandler() {
        // Basic animations
        animations = new HashMap<Integer, SymbolizeAnimation>();
        dual_animations = new HashMap<Integer, SymbolizeDualAnimation>();
        animations.put( Request.Rotate_right, new SymbolizeAnimation(
                new RotateAnimation( 0, 90, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f ),
                ROTATEDURATION, false ) );
        animations.put( Request.Rotate_left, new SymbolizeAnimation(
                new RotateAnimation( 0, -90, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f ),
                ROTATEDURATION, false ) );
        animations.put( Request.Flip_horizontally, new SymbolizeAnimation(
                new ScaleAnimation( 1, -1, 1, 1, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f ),
                FLIPDURATION, false ) );
        animations.put( Request.Flip_vertically, new SymbolizeAnimation(
                new ScaleAnimation( 1, 1, 1, -1, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f ),
                FLIPDURATION, false ) );
        dual_animations.put( Request.Shift, new SymbolizeDualAnimation(
                new AlphaAnimation( 1, 0 ),
                FADEDURATION,
                true,
                new AlphaAnimation( 0, 1 ),
                FADEDURATION,
                true) );
        dual_animations.put( Request.Load_puzzle_left, new SymbolizeDualAnimation(
                new TranslateAnimation( Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 1,
                        Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0 ),
                TRANSLATEDURATION,
                false,
                new TranslateAnimation( Animation.RELATIVE_TO_SELF, -1, Animation.RELATIVE_TO_SELF, 0,
                        Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0 ),
                TRANSLATEDURATION,
                true ) );
        dual_animations.put( Request.Load_puzzle_right, new SymbolizeDualAnimation(
                new TranslateAnimation( Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, -1,
                        Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0 ),
                TRANSLATEDURATION,
                false,
                new TranslateAnimation( Animation.RELATIVE_TO_SELF, 1, Animation.RELATIVE_TO_SELF, 0,
                        Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0 ),
                TRANSLATEDURATION,
                true ) );

        animationsets = new HashMap<Integer, SymbolizeAnimationSet>();
        dual_animationsets = new HashMap<Integer, SymbolizeDualAnimationSet>();

        // Zoom animations

        // Zoom in
        SymbolizeDualAnimationSet zoom_in_animation = new SymbolizeDualAnimationSet();
        zoom_in_animation.Add_animation(
                new SymbolizeAnimation( new AlphaAnimation( 1, 0 ), FADEDURATION, true )
        );
        SymbolizeZoomAnimation zoom_animation_1 =
                new SymbolizeZoomAnimation( 1, ZOMMSCALING, 1, ZOMMSCALING,
                        ZOOMDURATION, true );
        zoom_in_animation.Add_animation( zoom_animation_1 );
        zoom_in_animation.Add_animation_2(
                new SymbolizeAnimation( new AlphaAnimation( 0, 1 ), FADEDURATION, true )
        );
        SymbolizeZoomAnimation zoom_animation_2 =
                new SymbolizeZoomAnimation( ZOMMSCALING, 1, ZOMMSCALING, 1,
                        ZOOMDURATION, true );
        zoom_in_animation.Add_animation_2( zoom_animation_2 );
        dual_animationsets.put( Request.Load_level_via_world, zoom_in_animation );

        // Zoom out
        SymbolizeDualAnimationSet zoom_out_animation = new SymbolizeDualAnimationSet();
        zoom_out_animation.Add_animation(
                new SymbolizeAnimation( new AlphaAnimation( 1, 0 ), FADEDURATION, true )
        );
        SymbolizeZoomAnimation zoom_animation_3 =
                new SymbolizeZoomAnimation( 1, (float) 1/ZOMMSCALING, 1, (float) 1/ZOMMSCALING,
                        ZOOMDURATION, true );
        zoom_out_animation.Add_animation( zoom_animation_3 );
        zoom_out_animation.Add_animation_2(
                new SymbolizeAnimation( new AlphaAnimation( 0, 1 ), FADEDURATION, true )
        );
        SymbolizeZoomAnimation zoom_animation_4 =
                new SymbolizeZoomAnimation( ZOMMSCALING, 1, ZOMMSCALING, 1, ZOOMDURATION, true );
        zoom_out_animation.Add_animation_2( zoom_animation_4 );
        dual_animationsets.put( Request.Load_world_via_level, zoom_out_animation );

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
        switch ( request.type ) {
            case Request.Load_level_via_world:
            case Request.Load_world_via_level:
                SymbolizeDualAnimationSet dual_animation_set = dual_animationsets.get( request.type ); // TODO: THIS IS BAD, design needs to change on this guy
                dual_animation_set.SetSymbolizeAnimationListener( new SymbolizeAnimation.SymbolizeAnimationListener() {
                    @Override
                    public void onSymbolizeAnimationEnd() {
                        request.game_view.Render_foreground( request.graph, request.levels );
                    }
                } );
                dual_animation_set.SetSymbolizeAnimationListener_2( new SymbolizeAnimation.SymbolizeAnimationListener() {
                    @Override
                    public void onSymbolizeAnimationEnd() {
                        request.game_view.Render_foreground(request.graph, request.levels);
                        if ( request.type == Request.Load_level_via_world ) {
                            request.dialog.show( request.dialog_fragment_manager, "hint_dialog" );
                        }
                    }
                });
                dual_animation_set.Animate( request.linearLayout );
                break;

            case Request.Shift:
            case Request.Load_puzzle_left:
            case Request.Load_puzzle_right:
                SymbolizeDualAnimation dual_animation = dual_animations.get(request.type);
                dual_animation.SetSymbolizeAnimationListener( new SymbolizeAnimation.SymbolizeAnimationListener() {
                    @Override
                    public void onSymbolizeAnimationEnd() {
                        request.game_view.Render_foreground( request.graph, request.levels );
                    }
                } );
                dual_animation.SetSymbolizeAnimationListener_2( new SymbolizeAnimation.SymbolizeAnimationListener() {
                    @Override
                    public void onSymbolizeAnimationEnd() {
                        request.game_view.Render_foreground( request.graph, request.levels );
                    }
                } );
                dual_animation.Animate( request.linearLayout );
                break;

            default:
                SymbolizeAnimation animation = animations.get(request.type);
                animation.SetSymbolizeAnimationListener( new SymbolizeAnimation.SymbolizeAnimationListener() {
                    @Override
                    public void onSymbolizeAnimationEnd() {
                        request.game_view.Render_foreground( request.graph, request.levels );
                    }
                } );
                animation.Animate( request.linearLayout );
        }
    }

    public void Set_zoom_pivots( final Posn pivot )
    {
        for ( SymbolizeZoomAnimation zoom_animation : zoom_animations ) {
            zoom_animation.Set_pivot( pivot );
        }
    }
}
