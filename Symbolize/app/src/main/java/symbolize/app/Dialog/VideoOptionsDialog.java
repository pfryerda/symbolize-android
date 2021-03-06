package symbolize.app.Dialog;

import android.view.View;
import android.widget.CheckedTextView;
import android.widget.SeekBar;

import symbolize.app.Common.Communication.Request;
import symbolize.app.Common.Communication.Response;
import symbolize.app.Routing.Page;
import symbolize.app.DataAccess.OptionsDataAccess;
import symbolize.app.Game.GameController;
import symbolize.app.Game.GameUIView;
import symbolize.app.Game.GameView;
import symbolize.app.R;

public class VideoOptionsDialog extends OptionDialog {
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
        show_animation.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View view ) {
                options_dao.Toggle_boolean_option( OptionsDataAccess.OPTION_SHOW_ANIMATIONS );
                show_animation.setChecked( !show_animation.isChecked() );
            }
        } );

        ( (SeekBar) dialog_view.findViewById( R.id.options_game_size_seekbar ) ).setOnSeekBarChangeListener( new SeekBar.OnSeekBarChangeListener() {
            short progress_change;

            @Override
            public void onProgressChanged( SeekBar seekBar, int progress, boolean fromUser ) {
                progress_change = (short) ( progress  + OptionsDataAccess.GAME_SIZE_MIN );
                GameView.Set_sizes( progress_change );
                if ( Page.Is_Game_page() ) {
                    GameController.Get_instance().Handle_request( new Request( Request.Background_change ),
                                                                  new Response() );
                }
            }

            @Override
            public void onStartTrackingTouch( SeekBar seekBar ) {}

            @Override
            public void onStopTrackingTouch( SeekBar seekBar ) {
                options_dao.Set_short_option( OptionsDataAccess.OPTION_GAME_SIZE, progress_change );
            }
        } );

        final CheckedTextView use_device_brightness_button = (CheckedTextView) dialog_view.findViewById( R.id.options_use_device_brightness );
        use_device_brightness_button.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View view ) {
                options_dao.Toggle_boolean_option( OptionsDataAccess.OPTION_USE_DEVICE_BRIGHTNESS );
                use_device_brightness_button.setChecked( !use_device_brightness_button.isChecked() );
                init_dialog_view( dialog_view );
                GameUIView.Set_brightness();
            }
        } );

        ((SeekBar) dialog_view.findViewById( R.id.options_brightness_seekbar ) ).setOnSeekBarChangeListener( new SeekBar.OnSeekBarChangeListener() {
            short progress_change;

            @Override
            public void onProgressChanged( SeekBar seekBar, int progress, boolean fromUser ) {
                progress_change =  (short) progress;
                GameUIView.Set_brightness( progress_change );
            }

            @Override
            public void onStartTrackingTouch( SeekBar seekBar ) {}

            @Override
            public void onStopTrackingTouch( SeekBar seekBar ) {
                options_dao.Set_short_option( OptionsDataAccess.OPTION_BRIGHTNESS, progress_change );
            }
        } );

        dialog_view.findViewById( R.id.options_reset_to_default ).setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final ConfirmDialog confirmDialog = new ConfirmDialog();
                confirmDialog.Set_attrs( getString( R.string.revert_to_default_title ), getString( R.string.revert_to_default_message ) );
                confirmDialog.SetConfirmationListener( new ConfirmDialog.ConfirmDialogListener() {
                    @Override
                    public void OnDialogSuccess() {
                        options_dao.Reset_video_options();
                        init_dialog_view( dialog_view );
                    }

                    @Override
                    public void OnDialogFail() {}
                } );
                confirmDialog.Show();
            }
        } );

        return dialog_view;
    }

    /*
     * See SymbolizeDialog::init_dialog_view
     */
    @Override
    protected void init_dialog_view( final View dialog_view ) {
        OptionsDataAccess options_dao = OptionsDataAccess.Get_instance();

        ( (CheckedTextView) dialog_view.findViewById( R.id.options_show_animation ) )
                .setChecked( options_dao.Get_boolean_option( OptionsDataAccess.OPTION_SHOW_ANIMATIONS ) );

        ( (SeekBar) dialog_view.findViewById( R.id.options_game_size_seekbar ) )
                .setProgress( options_dao.Get_short_option( OptionsDataAccess.OPTION_GAME_SIZE ) - OptionsDataAccess.GAME_SIZE_MIN );

        final boolean use_device_brightness = options_dao.Get_boolean_option( OptionsDataAccess.OPTION_USE_DEVICE_BRIGHTNESS );
        ( (CheckedTextView) dialog_view.findViewById( R.id.options_use_device_brightness ) )
                .setChecked( use_device_brightness );

        final SeekBar brightness_bar = (SeekBar) dialog_view.findViewById( R.id.options_brightness_seekbar );
        brightness_bar.setProgress( options_dao.Get_short_option( OptionsDataAccess.OPTION_BRIGHTNESS ) );
        brightness_bar.setEnabled( !use_device_brightness );
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
