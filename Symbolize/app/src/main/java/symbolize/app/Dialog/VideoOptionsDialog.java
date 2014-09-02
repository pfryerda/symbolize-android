package symbolize.app.Dialog;

import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckedTextView;
import android.widget.SeekBar;

import symbolize.app.Common.Communication.Request;
import symbolize.app.Common.Communication.Response;
import symbolize.app.Common.Page;
import symbolize.app.DataAccess.OptionsDataAccess;
import symbolize.app.Game.GameController;
import symbolize.app.Game.GameUIView;
import symbolize.app.Game.GameView;
import symbolize.app.R;

public class VideoOptionsDialog extends InfoDialog {
    // Inherited fields
    //------------------

    /*
    protected String title;
    protected String message;
    */


    // Protected methods
    //-------------------

    /*
     * See SymbolizeDialog::get_dialog_view
     */
    @Override
    protected View get_dialog_view() {
        final View dialog_view = super.get_dialog_view();
        final OptionsDataAccess options_dao = OptionsDataAccess.Get_instance();

        final CheckedTextView show_animation = (CheckedTextView) dialog_view.findViewById( R.id.options_show_animation );
        show_animation.setChecked( options_dao.Get_boolean_option( OptionsDataAccess.OPTION_SHOW_ANIMATIONS ) );
        show_animation.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View view ) {
                options_dao.Toggle_boolean_option( OptionsDataAccess.OPTION_SHOW_ANIMATIONS );
                show_animation.setChecked( !show_animation.isChecked() );
            }
        } );

        final SeekBar game_size_bar = (SeekBar) dialog_view.findViewById( R.id.options_game_size_seekbar );
        game_size_bar.setProgress( options_dao.Get_short_option( OptionsDataAccess.OPTION_GAME_SIZE ) - OptionsDataAccess.GAME_SIZE_MIN );

        game_size_bar.setOnSeekBarChangeListener( new SeekBar.OnSeekBarChangeListener() {
            short progress_change;

            @Override
            public void onProgressChanged( SeekBar seekBar, int progress, boolean fromUser ) {
                progress_change = (short) ( progress  + OptionsDataAccess.GAME_SIZE_MIN );
                GameView.Set_sizes( progress_change );
                GameController.Get_instance().Handle_request( new Request( Request.Background_change ),
                                                              new Response() );
            }

            @Override
            public void onStartTrackingTouch( SeekBar seekBar ) {}

            @Override
            public void onStopTrackingTouch( SeekBar seekBar ) {
                options_dao.Set_short_option( OptionsDataAccess.OPTION_GAME_SIZE, progress_change );
            }
        } );

        final SeekBar brightness_bar = (SeekBar) dialog_view.findViewById( R.id.options_brightness_seekbar );
        brightness_bar.setProgress( options_dao.Get_short_option( OptionsDataAccess.OPTION_BRIGHTNESS ) );

        brightness_bar.setOnSeekBarChangeListener( new SeekBar.OnSeekBarChangeListener() {
            short progress_change;

            @Override
            public void onProgressChanged( SeekBar seekBar, int progress, boolean fromUser ) {
                progress_change = (short) Math.max( progress, OptionsDataAccess.MIN_BRIGHTNESS );
                GameUIView.Set_brightness( progress_change );
            }

            @Override
            public void onStartTrackingTouch( SeekBar seekBar ) {}

            @Override
            public void onStopTrackingTouch( SeekBar seekBar ) {
                options_dao.Set_short_option( OptionsDataAccess.OPTION_BRIGHTNESS, progress_change );
            }
        } );

        return dialog_view;
    }

    /*
     * See SymbolizeDialog::get_dialog_string_id
     */
    @Override
    protected String get_dialog_string_id() {
        return Page.Get_resource_string( R.string.video_options_dialog_id );
    }

    /*
     * See SymbolizeDialog::get_dialog_id
     */
    @Override
    protected int get_dialog_id() {
        return R.layout.video_options_dialog;
    }
}
