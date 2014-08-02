package symbolize.app.Animation;


import android.app.Fragment;
import android.content.res.Resources;
import android.util.Log;
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
import symbolize.app.Game.GameActivity;
import symbolize.app.R;

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

    public Posn current_pivot;

    
    // Constructor
    //-------------

    public GameAnimationHandler() {
        current_pivot = new Posn( 0, 0 );
    }


    // Main method
    //-------------

    public void Handle_request( final Request request ) {
        SymbolizeAnimation animation = new SymbolizeAnimation();
        if( request.type == Request.Load_level_via_world ) {
            animation.SetSymbolizeAnimationListener( new SymbolizeAnimation.SymbolizeAnimationListener() {
                @Override
                public void onSymbolizeAnimationClear() {
                    request.linearLayout.clearAnimation();
                }


                @Override
                public void onSymbolizeAnimationMiddle() {
                    request.game_view.Render_foreground( request.graph, request.levels );
                }

                @Override
                public void onSymbolizeAnimationEnd() {
                    request.game_view.Render_foreground( request.graph, request.levels );
                    request.dialog.show( request.dialog_fragment_manager,
                            GameActivity.Get_resource_string( R.string.hint_dialog_id ) );
                }
            } );
        } else {
            animation.SetSymbolizeAnimationListener( new SymbolizeAnimation.SymbolizeAnimationListener() {
                @Override
                public void onSymbolizeAnimationClear() {
                    request.linearLayout.clearAnimation();
                }

                @Override
                public void onSymbolizeAnimationMiddle() {
                    request.game_view.Render_foreground( request.graph, request.levels );
                }

                @Override
                public void onSymbolizeAnimationEnd() {
                    request.game_view.Render_foreground( request.graph, request.levels );
                }
            } );
        }

        switch ( request.type ) {
            case Request.Rotate_right:
                animation.Start_new_set();
                animation.Add_animation(
                        new RotateAnimation( 0, 90,
                                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f
                        ),
                        ROTATEDURATION, false
                );
                break;

            case Request.Rotate_left:
                animation.Start_new_set();
                animation.Add_animation(
                        new RotateAnimation( 0, -90,
                                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f
                        ),
                        ROTATEDURATION, false
                );
                break;

            case Request.Flip_horizontally:
                animation.Start_new_set();
                animation.Add_animation(
                        new ScaleAnimation( 1, -1, 1, 1,
                                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f
                        ),
                        FLIPDURATION, false
                );
                break;

            case Request.Flip_vertically:
                animation.Start_new_set();
                animation.Add_animation(
                        new ScaleAnimation( 1, 1, 1, -1,
                                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f
                        ),
                        FLIPDURATION, false
                );
                break;

            case Request.Shift:
                animation.Start_new_set();
                animation.Add_animation( new AlphaAnimation( 1, 0 ), FADEDURATION, true );
                animation.Start_new_set();
                animation.Add_animation( new AlphaAnimation( 0, 1 ), FADEDURATION, true );
                break;

            case Request.Load_puzzle_left:
                animation.Start_new_set();
                animation.Add_animation(
                    new TranslateAnimation( Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 1,
                            Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0
                    ),
                    TRANSLATEDURATION, false
                );
                animation.Start_new_set();
                animation.Add_animation(
                    new TranslateAnimation( Animation.RELATIVE_TO_SELF, -1, Animation.RELATIVE_TO_SELF, 0,
                            Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0
                    ),
                    TRANSLATEDURATION, true
                );
                break;

            case Request.Load_puzzle_right:
                animation.Start_new_set();
                animation.Add_animation(
                    new TranslateAnimation( Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, -1,
                            Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0
                    ),
                    TRANSLATEDURATION, false
                );
                animation.Start_new_set();
                animation.Add_animation(
                    new TranslateAnimation( Animation.RELATIVE_TO_SELF, 1, Animation.RELATIVE_TO_SELF, 0,
                            Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0
                    ),
                    TRANSLATEDURATION, true
                );
                break;

            case Request.Load_level_via_world:
                animation.Start_new_set();
                animation.Add_animation( new AlphaAnimation( 1, 0 ), FADEDURATION, true );
                animation.Add_animation(
                        new ScaleAnimation( 1, ZOMMSCALING, 1, ZOMMSCALING,
                            Animation.RELATIVE_TO_SELF, (float) current_pivot.x() / GameActivity.SCALING,
                            Animation.RELATIVE_TO_SELF, (float) current_pivot.y() / GameActivity.SCALING
                        ),
                        ZOOMDURATION, true
                );
                animation.Start_new_set();
                animation.Add_animation( new AlphaAnimation( 0, 1 ), FADEDURATION, true );
                animation.Add_animation(
                        new ScaleAnimation( ZOMMSCALING, 1, ZOMMSCALING, 1,
                                Animation.RELATIVE_TO_SELF, (float) current_pivot.x() / GameActivity.SCALING,
                                Animation.RELATIVE_TO_SELF, (float) current_pivot.y() / GameActivity.SCALING
                        ),
                        ZOOMDURATION, true
                );
                break;

            case Request.Load_world_via_level:
                animation.Start_new_set();
                animation.Add_animation( new AlphaAnimation( 1, 0 ), FADEDURATION, true );
                animation.Add_animation(
                        new ScaleAnimation( 1, (float) 1/ZOMMSCALING, 1, (float) 1/ZOMMSCALING,
                                Animation.RELATIVE_TO_SELF, (float) current_pivot.x() / GameActivity.SCALING,
                                Animation.RELATIVE_TO_SELF, (float) current_pivot.y() / GameActivity.SCALING
                        ),
                        ZOOMDURATION, true
                );
                animation.Start_new_set();
                animation.Add_animation( new AlphaAnimation( 0, 1 ), FADEDURATION, true );
                animation.Add_animation(
                        new ScaleAnimation( ZOMMSCALING, 1, ZOMMSCALING, 1,
                                Animation.RELATIVE_TO_SELF, (float) current_pivot.x() / GameActivity.SCALING,
                                Animation.RELATIVE_TO_SELF, (float) current_pivot.y() / GameActivity.SCALING
                        ),
                        ZOOMDURATION, true
                );
                break;
        }

        animation.Animate( request.linearLayout );
    }
}
