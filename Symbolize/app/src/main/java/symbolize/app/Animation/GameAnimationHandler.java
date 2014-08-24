package symbolize.app.Animation;

import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import symbolize.app.Common.Session;
import symbolize.app.Common.Communication.Request;
import symbolize.app.Game.GameUIView;
import symbolize.app.Game.GameView;

/*
 * An all static class used for getting an animation given a request type
 */
abstract public class GameAnimationHandler {
    // Static fields
    //----------------

    public static final int ZOOM_SCALING = 4;

    public static final int ROTATE_DURATION = 450;
    public static final int FLIP_DURATION = 450;
    public static final int SHIFT_DURATION = 600;
    public static final int ZOOM_DURATION = 600;
    public static final int TRANSLATE_DURATION = 650;


    // Main method
    //-------------

    public static SymbolizeAnimation Handle_request( final Request request ) {
        Session session = Session.Get_instance();
        SymbolizeAnimation animation = new SymbolizeAnimation();


        switch ( request.type ) {
            case Request.Rotate_right:
                animation.Start_new_set();
                animation.Add_animation(
                        new RotateAnimation( 0, 90,
                                Animation.RELATIVE_TO_SELF, 0.5f, Animation.ABSOLUTE, GameUIView.BAR_HEIGHT + (float) GameUIView.CANVAS_SIZE / 2
                        ),
                        ROTATE_DURATION, false
                );
                break;

            case Request.Rotate_left:
                animation.Start_new_set();
                animation.Add_animation(
                        new RotateAnimation( 0, -90,
                                Animation.RELATIVE_TO_SELF, 0.5f, Animation.ABSOLUTE, GameUIView.BAR_HEIGHT + (float) GameUIView.CANVAS_SIZE / 2
                        ),
                        ROTATE_DURATION, false
                );
                break;

            case Request.Flip_horizontally:
                animation.Start_new_set();
                animation.Add_animation(
                        new ScaleAnimation( 1, -1, 1, 1,
                                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f
                        ),
                        FLIP_DURATION, false
                );
                break;

            case Request.Flip_vertically:
                animation.Start_new_set();
                animation.Add_animation(
                        new ScaleAnimation( 1, 1, 1, -1,
                                Animation.RELATIVE_TO_SELF, 0.5f, Animation.ABSOLUTE, GameUIView.BAR_HEIGHT + (float) GameUIView.CANVAS_SIZE / 2
                        ),
                        FLIP_DURATION, false
                );
                break;

            case Request.Shift:
                animation.Start_new_set();
                animation.Add_animation( new AlphaAnimation( 1, 0 ), SHIFT_DURATION, true );
                animation.Start_new_set();
                animation.Add_animation( new AlphaAnimation( 0, 1 ), SHIFT_DURATION, true );
                break;

            case Request.Load_puzzle_left:
                animation.Start_new_set();
                animation.Add_animation(
                    new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 1,
                            Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0
                    ),
                    TRANSLATE_DURATION, false
                );
                animation.Start_new_set();
                animation.Add_animation(
                    new TranslateAnimation( Animation.RELATIVE_TO_SELF, -1, Animation.RELATIVE_TO_SELF, 0,
                            Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0
                    ),
                    TRANSLATE_DURATION, true
                );
                break;

            case Request.Load_puzzle_right:
                animation.Start_new_set();
                animation.Add_animation(
                    new TranslateAnimation( Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, -1,
                            Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0
                    ),
                    TRANSLATE_DURATION, false
                );
                animation.Start_new_set();
                animation.Add_animation(
                    new TranslateAnimation( Animation.RELATIVE_TO_SELF, 1, Animation.RELATIVE_TO_SELF, 0,
                            Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0
                    ),
                    TRANSLATE_DURATION, true
                );
                break;

            case Request.Load_level_via_world:
                animation.Start_new_set();
                animation.Add_animation( new AlphaAnimation( 1, 0 ), ZOOM_DURATION, true );
                animation.Add_animation(
                        new ScaleAnimation( 1, ZOOM_SCALING, 1, ZOOM_SCALING,
                            Animation.ABSOLUTE, session.Get_current_pivot().Unscale().x(),
                            Animation.ABSOLUTE, session.Get_current_pivot().Unscale().y()
                        ),
                        ZOOM_DURATION, true
                );
                animation.Start_new_set();
                animation.Add_animation( new AlphaAnimation( 0, 1 ), ZOOM_DURATION, true );
                animation.Add_animation(
                        new ScaleAnimation( ZOOM_SCALING, 1, ZOOM_SCALING, 1,
                                Animation.ABSOLUTE, session.Get_current_pivot().Unscale().x(),
                                Animation.ABSOLUTE, session.Get_current_pivot().Unscale().y()
                        ),
                        ZOOM_DURATION, true
                );
                break;

            case Request.Load_world_via_level:
                animation.Start_new_set();
                animation.Add_animation( new AlphaAnimation( 1, 0 ), ZOOM_DURATION, true );
                animation.Add_animation(
                        new ScaleAnimation( 1, (float) 1/ZOOM_SCALING, 1, (float) 1/ZOOM_SCALING,
                                Animation.ABSOLUTE, session.Get_current_pivot().Unscale().x(),
                                Animation.ABSOLUTE, session.Get_current_pivot().Unscale().y()
                        ),
                        ZOOM_DURATION, true
                );
                animation.Start_new_set();
                animation.Add_animation( new AlphaAnimation( 0, 1 ), ZOOM_DURATION, true );
                animation.Add_animation(
                        new ScaleAnimation( ZOOM_SCALING, 1, ZOOM_SCALING, 1,
                                Animation.ABSOLUTE, session.Get_current_pivot().Unscale().x(),
                                Animation.ABSOLUTE, session.Get_current_pivot().Unscale().y()
                        ),
                        ZOOM_DURATION, true
                );
                break;

            case Request.Load_puzzle_start:
                animation.Start_new_set();
                animation.Add_animation( new AlphaAnimation( 1, 0 ), TRANSLATE_DURATION, true );
                animation.Add_animation( new TranslateAnimation( Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0,
                                Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, -0.1f
                        ),
                        TRANSLATE_DURATION, true
                );
                animation.Start_new_set();
                animation.Add_animation( new AlphaAnimation( 0, 1 ), TRANSLATE_DURATION, true );
                animation.Add_animation( new TranslateAnimation( Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0,
                                Animation.RELATIVE_TO_SELF, -0.1f, Animation.RELATIVE_TO_SELF, 0
                        ),
                        TRANSLATE_DURATION, true
                );
                break;
        }

        return animation;
    }
}
