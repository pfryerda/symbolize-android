package app.symbolize.Animation;

import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import app.symbolize.Common.Session;
import app.symbolize.Common.Communication.Request;
import app.symbolize.DataAccess.MetaDataAccess;
import app.symbolize.Game.GameUIView;
import app.symbolize.Game.GameView;

/*
 * An all static class used for getting an animation given a request type
 */
abstract public class GameAnimationHandler {
    // Constants
    //-----------

    public static final byte ZOOM_SCALING = 4;


    // Main method
    //-------------

    public static SymbolizeAnimation Handle_request( final Request request ) {
        final Session session = Session.Get_instance();
        final SymbolizeAnimation animation = new SymbolizeAnimation();
        final MetaDataAccess meta_dao = MetaDataAccess.Get_instance();

        switch ( request.type ) {
            case Request.Rotate_right:
                animation.Start_new_set();
                animation.Add_animation(
                        new RotateAnimation( 0, 90,
                                Animation.RELATIVE_TO_SELF, 0.5f, Animation.ABSOLUTE, GameUIView.BAR_HEIGHT + GameUIView.CANVAS_MARGIN + (float) GameUIView.CANVAS_SIZE / 2
                        ),
                        meta_dao.Get_duration( MetaDataAccess.DURATION_ROTATE ), false
                );
                break;

            case Request.Rotate_left:
                animation.Start_new_set();
                animation.Add_animation(
                        new RotateAnimation( 0, -90,
                                Animation.RELATIVE_TO_SELF, 0.5f, Animation.ABSOLUTE, GameUIView.BAR_HEIGHT + GameUIView.CANVAS_MARGIN + (float) GameUIView.CANVAS_SIZE / 2
                        ),
                        meta_dao.Get_duration( MetaDataAccess.DURATION_ROTATE ), false
                );
                break;

            case Request.Flip_horizontally:
                animation.Start_new_set();
                animation.Add_animation(
                        new ScaleAnimation( 1, -1, 1, 1,
                                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f
                        ),
                        meta_dao.Get_duration( MetaDataAccess.DURATION_FLIP ), false
                );
                break;

            case Request.Flip_vertically:
                animation.Start_new_set();
                animation.Add_animation(
                        new ScaleAnimation( 1, 1, 1, -1,
                                Animation.RELATIVE_TO_SELF, 0.5f, Animation.ABSOLUTE, GameUIView.BAR_HEIGHT + GameUIView.CANVAS_MARGIN + (float) GameUIView.CANVAS_SIZE / 2
                        ),
                        meta_dao.Get_duration( MetaDataAccess.DURATION_FLIP ), false
                );
                break;

            case Request.Shift:
                short shift_duration = meta_dao.Get_duration( MetaDataAccess.DURATION_SHIFT );
                animation.Start_new_set();
                animation.Add_animation( new AlphaAnimation( 1, 0 ), shift_duration, true );
                animation.Start_new_set();
                animation.Add_animation( new AlphaAnimation( 0, 1 ), shift_duration, true );
                break;

            case Request.Load_puzzle_left:
                short translate_duration = meta_dao.Get_duration( MetaDataAccess.DURATION_TRANSLATE );
                animation.Start_new_set();
                animation.Add_animation(
                    new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 1,
                            Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0
                    ),
                    translate_duration, false
                );
                animation.Start_new_set();
                animation.Add_animation(
                    new TranslateAnimation( Animation.RELATIVE_TO_SELF, -1, Animation.RELATIVE_TO_SELF, 0,
                            Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0
                    ),
                    translate_duration, true
                );
                break;

            case Request.Load_puzzle_right:
                translate_duration = meta_dao.Get_duration( MetaDataAccess.DURATION_TRANSLATE );
                animation.Start_new_set();
                animation.Add_animation(
                    new TranslateAnimation( Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, -1,
                            Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0
                    ),
                        translate_duration, false
                );
                animation.Start_new_set();
                animation.Add_animation(
                    new TranslateAnimation( Animation.RELATIVE_TO_SELF, 1, Animation.RELATIVE_TO_SELF, 0,
                            Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0
                    ),
                    translate_duration, true
                );
                break;

            case Request.Load_level_via_world:
                short zoom_duration = meta_dao.Get_duration( MetaDataAccess.DURATION_ZOOM );
                animation.Start_new_set();
                animation.Add_animation( new AlphaAnimation( 1, 0 ), zoom_duration, true );
                animation.Add_animation(
                        new ScaleAnimation( 1, ZOOM_SCALING, 1, ZOOM_SCALING,
                            Animation.ABSOLUTE, session.Get_current_pivot().Unscale().x(),
                            Animation.ABSOLUTE, session.Get_current_pivot().Unscale().y()
                        ),
                        zoom_duration, true
                );
                animation.Start_new_set();
                animation.Add_animation( new AlphaAnimation( 0, 1 ), zoom_duration, true );
                animation.Add_animation(
                        new ScaleAnimation( ZOOM_SCALING, 1, ZOOM_SCALING, 1,
                                Animation.ABSOLUTE, session.Get_current_pivot().Unscale().x(),
                                Animation.ABSOLUTE, session.Get_current_pivot().Unscale().y()
                        ),
                        zoom_duration, true
                );
                break;

            case Request.Load_world_via_level:
                zoom_duration = meta_dao.Get_duration( MetaDataAccess.DURATION_ZOOM );
                animation.Start_new_set();
                animation.Add_animation( new AlphaAnimation( 1, 0 ), zoom_duration, true );
                animation.Add_animation(
                        new ScaleAnimation( 1, (float) 1/ZOOM_SCALING, 1, (float) 1/ZOOM_SCALING,
                                Animation.ABSOLUTE, session.Get_current_pivot().Unscale().x(),
                                Animation.ABSOLUTE, session.Get_current_pivot().Unscale().y()
                        ),
                        zoom_duration, true
                );
                animation.Start_new_set();
                animation.Add_animation( new AlphaAnimation( 0, 1 ), zoom_duration, true );
                animation.Add_animation(
                        new ScaleAnimation( ZOOM_SCALING, 1, ZOOM_SCALING, 1,
                                Animation.ABSOLUTE, session.Get_current_pivot().Unscale().x(),
                                Animation.ABSOLUTE, session.Get_current_pivot().Unscale().y()
                        ),
                        zoom_duration, true
                );
                break;

            case Request.Load_puzzle_start:
                translate_duration = meta_dao.Get_duration( MetaDataAccess.DURATION_TRANSLATE );
                animation.Start_new_set();
                animation.Add_animation( new AlphaAnimation( 1, 0 ), translate_duration, true );
                animation.Add_animation( new TranslateAnimation( Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0,
                                Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, -0.1f
                        ),
                        translate_duration, true
                );
                animation.Start_new_set();
                animation.Add_animation( new AlphaAnimation( 0, 1 ), translate_duration, true );
                animation.Add_animation( new TranslateAnimation( Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0,
                                Animation.RELATIVE_TO_SELF, -0.1f, Animation.RELATIVE_TO_SELF, 0
                        ),
                        translate_duration, true
                );
                break;
        }

        return animation;
    }
}
