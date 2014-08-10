package symbolize.app.Animation;


import android.util.Log;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;

import symbolize.app.Common.Player;
import symbolize.app.Common.Posn;
import symbolize.app.Common.Request;
import symbolize.app.Game.GameView;


public class GameAnimationHandler {
    // Static fields
    //----------------

    public static boolean InAnimation = false;
    public static final int ZOOM_SCALING = 4;

    public static final int ROTATE_DURATION = 450;
    public static final int FLIP_DURATION = 450;
    public static final int FADE_DURATION = 600;
    public static final int ZOOM_DURATION = 600;
    public static final int TRANSLATE_DURATION = 650;


    // Main method
    //-------------

    public void Handle_request( final Request request ) {
        Player player = Player.Get_instance();
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
                    request.dialog.Show();
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
                        ROTATE_DURATION, false
                );
                break;

            case Request.Rotate_left:
                animation.Start_new_set();
                animation.Add_animation(
                        new RotateAnimation( 0, -90,
                                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f
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
                                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f
                        ),
                        FLIP_DURATION, false
                );
                break;

            case Request.Shift:
                animation.Start_new_set();
                animation.Add_animation( new AlphaAnimation( 1, 0 ), FADE_DURATION, true );
                animation.Start_new_set();
                animation.Add_animation( new AlphaAnimation( 0, 1 ), FADE_DURATION, true );
                break;

            case Request.Load_puzzle_left:
                animation.Start_new_set();
                animation.Add_animation(
                    new TranslateAnimation( Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 1,
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
                animation.Add_animation( new AlphaAnimation( 1, 0 ), FADE_DURATION, true );
                animation.Add_animation(
                        new ScaleAnimation( 1, ZOOM_SCALING, 1, ZOOM_SCALING,
                            Animation.RELATIVE_TO_SELF, (float) player.Get_current_pivot().x() / GameView.SCALING,
                            Animation.RELATIVE_TO_SELF, (float) player.Get_current_pivot().y() / GameView.SCALING
                        ),
                        ZOOM_DURATION, true
                );
                animation.Start_new_set();
                animation.Add_animation( new AlphaAnimation( 0, 1 ), FADE_DURATION, true );
                animation.Add_animation(
                        new ScaleAnimation( ZOOM_SCALING, 1, ZOOM_SCALING, 1,
                                Animation.RELATIVE_TO_SELF, (float) player.Get_current_pivot().x() / GameView.SCALING,
                                Animation.RELATIVE_TO_SELF, (float) player.Get_current_pivot().y() / GameView.SCALING
                        ),
                        ZOOM_DURATION, true
                );
                break;

            case Request.Load_world_via_level:
                animation.Start_new_set();
                animation.Add_animation( new AlphaAnimation( 1, 0 ), FADE_DURATION, true );
                animation.Add_animation(
                        new ScaleAnimation( 1, (float) 1/ZOOM_SCALING, 1, (float) 1/ZOOM_SCALING,
                                Animation.RELATIVE_TO_SELF, (float) player.Get_current_pivot().x() / GameView.SCALING,
                                Animation.RELATIVE_TO_SELF, (float) player.Get_current_pivot().y() / GameView.SCALING
                        ),
                        ZOOM_DURATION, true
                );
                animation.Start_new_set();
                animation.Add_animation( new AlphaAnimation( 0, 1 ), FADE_DURATION, true );
                animation.Add_animation(
                        new ScaleAnimation( ZOOM_SCALING, 1, ZOOM_SCALING, 1,
                                Animation.RELATIVE_TO_SELF, (float) player.Get_current_pivot().x() / GameView.SCALING,
                                Animation.RELATIVE_TO_SELF, (float) player.Get_current_pivot().y() / GameView.SCALING
                        ),
                        ZOOM_DURATION, true
                );
                break;
        }

        animation.Animate( request.linearLayout );
    }
}
