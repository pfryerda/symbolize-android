package app.symbolize.Game;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.os.Bundle;
import com.google.android.gms.ads.*;
import android.provider.Settings;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import app.symbolize.Animation.SymbolizeAnimation;
import app.symbolize.Common.Line;
import app.symbolize.Common.Communication.Response;
import app.symbolize.Common.Session;
import app.symbolize.DataAccess.MetaDataAccess;
import app.symbolize.Common.Posn;
import app.symbolize.Common.Communication.Request;
import app.symbolize.Dialog.ChoiceDialog;
import app.symbolize.Routing.Page;
import app.symbolize.DataAccess.ProgressDataAccess;
import app.symbolize.DataAccess.UnlocksDataAccess;
import app.symbolize.Dialog.ConfirmDialog;
import app.symbolize.Dialog.HintDialog;
import app.symbolize.Dialog.InfoDialog;
import app.symbolize.Dialog.OptionsDialog;
import app.symbolize.Puzzle.Puzzle;
import app.symbolize.Puzzle.PuzzleDB;
import app.symbolize.Home.HomePage;
import app.symbolize.R;
import app.symbolize.Routing.Router;

/*
 * The main class in charge of setting up the game as well as sending requests based off client interactions
 */
public class GamePage extends Page
                      implements GameTouchHandler.GameTouchListener,
                                 GameShakeHandler.OnShakeListener {
    // Constants
    //-----------

    public final static String Luke = "AWESOME";


    // Static block
    //--------------

    static {
        UnlocksDataAccess.Get_instance().Unlock( (byte) 1 );
    }

    // Fields
    //--------

    public static AdRequest Ad_request = null;


    // Main method
    //--------------

    @Override
    protected void onCreate( final Bundle savedInstanceState ) {
        // Basic setup
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_game );
        Set_game_page();

        // Ad setup
        AdView adView = ( AdView ) this.findViewById( R.id.game_adspace );
        adView.loadAd( Ad_request );

        // Set ui dimensions - faster than xml
        GameUIView.Setup_ui();

        // Load las world used or '1' is none was last used
        Session.Get_instance().Update_puzzle();

        Request request = new Request( Request.Load_puzzle_start );
        GameController.Get_instance().Handle_request( request, new Response() );

        GameTouchHandler.Get_instance().Set_listener( this );
        GameShakeHandler.Get_instance().Set_listener( this );
        findViewById( R.id.foreground ).setOnTouchListener( this );
    }


    // Button methods
    // ---------------

    public void On_left_button_clicked( final View view ) {
        if ( !SymbolizeAnimation.InAnimation ) {
            Session session = Session.Get_instance();

            session.Decrease_world();
            load_world( Request.Load_puzzle_left );
        }
    }

    public void On_back_button_clicked( final View view ) {
        if ( !SymbolizeAnimation.InAnimation ) {
            Session session = Session.Get_instance();

            if ( session.Is_in_world_view() ) {
                Router.Direct_route( getApplicationContext(), HomePage.class );
            } else {
                load_world( Request.Load_world_via_level );
            }
        }
    }

    public void On_right_button_clicked( final View view ) {
        if ( !SymbolizeAnimation.InAnimation ) {
            Session session = Session.Get_instance();

            session.Increase_world();
            load_world( Request.Load_puzzle_right );
        }
    }

    public void On_settings_button_clicked( final View view ) {
        OptionsDialog options_dialog = new OptionsDialog();
        options_dialog.Set_Button( (ImageButton) findViewById( R.id.Settings ) );
        options_dialog.Show();
    }

    public void On_reset_button_clicked( final View view ) {
        if ( !SymbolizeAnimation.InAnimation ) {
            Request request = new Request( Request.Reset );
            GameController.Get_instance().Handle_request( request, new Response() );
        }
    }

    public void On_check_button_clicked( final View view ) {
        if ( !SymbolizeAnimation.InAnimation ) {
            final Session session = Session.Get_instance();
            final GameController controller = GameController.Get_instance();
            final Puzzle current_puzzle = session.Get_current_puzzle();

            if ( Session.DEV_MODE ) {
                Request request = new Request( Request.Log );

                controller.Handle_request( request, new Response() );
            }

            Request request = new Request( Request.Check_correctness );
            Response response = new Response();
            controller.Handle_request( request,response );

            if ( response.response_boolean  ) {
                final ConfirmDialog confirmDialog;
                ProgressDataAccess.Get_instance().Complete( session.Get_current_world(), session.Get_current_level() );
                MetaDataAccess.Get_instance().Update_mechanics_seen();

                if ( ( Session.VERSION == Session.VERSION_ALPHA ) && session.Is_in_world_view() ) {
                    InfoDialog info_dialog = new InfoDialog();
                    info_dialog.Set_attrs(getString(R.string.puzzle_complete_dialog_title),
                            getString( R.string.alpha_game_complete_dialog_msg ) );
                    info_dialog.Show();
                } else if ( session.Is_in_world_view() && session.Get_current_world() <= PuzzleDB.NUMBER_OF_WORLDS ) {
                    for ( byte unlock : current_puzzle.Get_unlocks() ) {
                        UnlocksDataAccess.Get_instance().Unlock( unlock );
                    }


                    confirmDialog = new ConfirmDialog();
                    confirmDialog.Set_Button_Text( Get_resource_string( R.string.next_world ), Get_resource_string( R.string.cancel) );
                    confirmDialog.Set_attrs( getString( R.string.puzzle_complete_dialog_title ), getString( R.string.world_complete_dialog_msg ) );
                    confirmDialog.Set_Button( (ImageButton) findViewById( R.id.Check ) );
                    confirmDialog.SetConfirmationListener( new ConfirmDialog.ConfirmDialogListener() {
                        @Override
                        public void OnDialogSuccess() {
                            session.Increase_world();
                            load_world( Request.Load_puzzle_right );
                        }

                        @Override
                        public void onDialogNeutral() {}

                        @Override
                        public void OnDialogFail() {}
                    });
                    confirmDialog.Show();
                } else if ( session.Is_in_world_view() && session.Get_current_world() > PuzzleDB.NUMBER_OF_WORLDS ) {
                    InfoDialog info_dialog = new InfoDialog();
                    info_dialog.Set_attrs( getString( R.string.puzzle_complete_dialog_title ),
                            getString( R.string.game_complete_dialog_msg ) );
                    info_dialog.Show();
                } else if ( !session.Is_in_world_view() ) {
                    for ( byte unlock : current_puzzle.Get_unlocks() ) {
                        UnlocksDataAccess.Get_instance().Unlock( session.Get_current_world(), unlock );
                    }

                    if(current_puzzle.Get_unlocks().size() == 1) {
                        confirmDialog = new ChoiceDialog();
                        ( (ChoiceDialog) confirmDialog ).Set_Button_Text( Get_resource_string( R.string.previous_world ), Get_resource_string( R.string.cancel ), Get_resource_string( R.string.next_level ) );
                        confirmDialog.Set_attrs(getString(R.string.puzzle_complete_dialog_title), getString(R.string.level_complete_one_unlock_dialog_msg));
                    } else {
                        confirmDialog = new ConfirmDialog();
                        confirmDialog.Set_Button_Text( Get_resource_string( R.string.previous_world ), Get_resource_string( R.string.cancel ) );
                        confirmDialog.Set_attrs(getString(R.string.puzzle_complete_dialog_title), getString(R.string.level_complete_dialog_msg));
                    }
                    confirmDialog.SetConfirmationListener(new ConfirmDialog.ConfirmDialogListener() {
                        @Override
                        public void OnDialogSuccess() {
                            load_world(Request.Load_world_via_level);
                        }

                        @Override
                        public void onDialogNeutral() {
                            load_level( current_puzzle.Get_unlocks().get( 0 ), Request.Load_puzzle_right );
                        }

                        @Override
                        public void OnDialogFail() {
                        }
                    });
                    confirmDialog.Show();
                }

            }
        }
    }

    public void On_hint_button_clicked( final View view ) {
        if ( !SymbolizeAnimation.InAnimation ) {
            final HintDialog hint_dialog = new HintDialog();
            hint_dialog.Set_Button( (ImageButton) findViewById( R.id.Hint ) );
            hint_dialog.Show();
        }
    }

    public void On_undo_button_clicked( final View view ) {
        if ( !SymbolizeAnimation.InAnimation ) {
            Request request = new Request( Request.Undo );
            GameController.Get_instance().Handle_request( request, new Response() );
        }
    }

    public void On_draw_button_clicked( final View view ) {
        Session.Get_instance().Set_draw_mode();
        GameUIView.Highlight_current_mode();
    }

    public void On_erase_button_clicked( final View view ) {
        Session.Get_instance().Set_erase_mode();
        GameUIView.Highlight_current_mode();
    }


    // Interface methods
    //-------------------

    // Touch methods
    @Override
    public boolean onTouch( final View v, final MotionEvent event ) {
        return GameTouchHandler.Get_instance().handle_touch( event );
    }

    @Override
    public void onDraw( Line line ) {
        if ( Session.Get_instance().In_draw_mode() ) {
            Request request = new Request( Request.Draw );
            request.request_line = line;
            GameController.Get_instance().Handle_request( request, new Response());
        }
    }

    @Override
    public void onErase( final Posn point ) {
        if( Session.Get_instance().In_erase_mode() ) {
            Request request = new Request( Request.Erase );
            request.request_point = point;
            GameController.Get_instance().Handle_request( request, new Response() );
        }
    }

    @Override
    public void onFingerUp() {
        GameController.Get_instance().Handle_request( new Request( Request.None ), new Response() );
    }

    @Override
    public void onFingerMove( final Line line, final Posn point ) {
        Session session = Session.Get_instance();
        GameController controller = GameController.Get_instance();

        if ( session.In_draw_mode() ) {
            Request request = new Request( Request.Shadow_line );
            request.request_line = line;
            controller.Handle_request( request, new Response() );
        } else {
            Request request = new Request( Request.Shadow_point );
            request.request_point = point;
            controller.Handle_request( request, new Response() );
        }
    }

    @Override
    public void onTap( final Posn point ) {
        GameController controller = GameController.Get_instance();
        Session session = Session.Get_instance();

        if( session.Is_in_world_view() ) {
            Request request = new Request( Request.Fetch_level );
            request.request_point = point;
            Response response = new Response();
            controller.Handle_request( request, response );
            if( response.response_int != null && response.response_int > 0 ) {
                load_level( (byte) (int) response.response_int, Request.Load_level_via_world );
            }
        } else {
            if ( session.Get_current_puzzle().Can_change_color() ) {
                Request request = new Request( Request.Change_color );
                request.request_point = point;
                controller.Handle_request( request, new Response() );
            }
        }
    }

    @Override
    public void onDoubleTap( final Posn point ) {
        Puzzle current_puzzle = Session.Get_instance().Get_current_puzzle();
        if ( current_puzzle.Is_special_enabled() ) {
            Request request = new Request( Request.Get_request_type_from_special( current_puzzle.Get_special_type() ) );
            request.request_point = point;
            GameController.Get_instance().Handle_request( request, new Response() );
        } else {
            onTap( point );
        }
    }

    @Override
    public Line onDragStart( Posn point ) {
        GameController controller = GameController.Get_instance();
        Session session = Session.Get_instance();

        if ( ( MetaDataAccess.Get_instance().Has_seen( MetaDataAccess.SEEN_DRAG ) || session.Get_current_puzzle().Get_drag_restriction() > 0 )
            && session.In_draw_mode() )
        {
            Request request = new Request( Request.Drag_start );
            request.request_point = point;
            Response response = new Response();
            controller.Handle_request( request, response );
            if ( response.response_line == null ) {
                return null;
            } else {
                return response.response_line.clone();
            }
        }
        return null;
    }

    @Override
    public void onDragEnd( Line line ) {
        Request request = new Request( Request.Drag_end );
        request.request_line = line;
        GameController.Get_instance().Handle_request( request, new Response() );
    }

    @Override
    public void onEnterDoubleTouch() {
        GameController.Get_instance().Handle_request( new Request( Request.None ), new Response() );
    }

    @Override
    public void onRotateRight() {
        if ( Session.Get_instance().Get_current_puzzle().Can_rotate() ) {
            GameController.Get_instance().Handle_request( new Request( Request.Rotate_right ), new Response() );
        }
    }

    @Override
    public void onRotateLeft() {
        if ( Session.Get_instance().Get_current_puzzle().Can_rotate() ) {
            GameController.Get_instance().Handle_request( new Request( Request.Rotate_left ), new Response() );
        }
    }

    @Override
    public void onFlipHorizontally() {
        if ( Session.Get_instance().Get_current_puzzle().Can_flip() ) {
            GameController.Get_instance().Handle_request( new Request( Request.Flip_horizontally ), new Response() );
        }
    }

    @Override
    public void onFlipVertically() {
        if ( Session.Get_instance().Get_current_puzzle().Can_flip() ) {
            GameController.Get_instance().Handle_request( new Request( Request.Flip_vertically ), new Response() );
        }
    }

    //Sensor methods
    @Override
    public void onSensorChanged( final SensorEvent event ) {
        GameShakeHandler.Get_instance().handle_shake(event);
    }

    @Override
    public void onShake() {
        Puzzle current_puzzle = Session.Get_instance().Get_current_puzzle();
        if ( current_puzzle.Can_shift() ) {
            Request request = new Request( Request.Shift );
            GameController.Get_instance().Handle_request( request, new Response() );
        }
    }

    @Override
    public void onAccuracyChanged( final Sensor sensor, final int accuracy ) {}


    // Private/Protected methods
    //--------------------------

    /*
     * Methods used for loading new levels/worlds, called once on game startup and again for any later
     * new level changes. Sets up GameModel, removes old data, and renders board to the screen
     *
     * @param Level level: The level that needs to be loaded
     */
    private void load_world( byte request_type ) {
        Session session = Session.Get_instance();
        session.Set_to_world();
        session.Update_puzzle();

        GameController.Get_instance().Handle_request( new Request( request_type ), new Response() );
    }

    private void load_level( byte level, byte request_type ) {
        Session session = Session.Get_instance();
        session.Set_current_level( level );
        session.Update_puzzle();

        Request request = new Request( request_type );
        request.request_bool = true;
        GameController.Get_instance().Handle_request( request, new Response() );
    }


    // Method for pausing/resuming the game
    //---------------------------------------

    @Override
    protected void onResume() {
        super.onResume();
        SensorManager sensor_manager = ( SensorManager ) getSystemService( SENSOR_SERVICE );
        sensor_manager.registerListener( this,
                sensor_manager.getDefaultSensor( Sensor.TYPE_ACCELEROMETER ),
                SensorManager.SENSOR_DELAY_NORMAL );
    }

    @Override
    protected void onPause() {
        super.onPause();
        SensorManager sensor_manager = ( SensorManager ) getSystemService( SENSOR_SERVICE );
        sensor_manager.unregisterListener( this );
    }
}
