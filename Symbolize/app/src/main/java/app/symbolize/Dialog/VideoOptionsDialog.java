package app.symbolize.Dialog;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.SeekBar;

import app.symbolize.Common.Communication.Request;
import app.symbolize.Common.Communication.Response;
import app.symbolize.Routing.Page;
import app.symbolize.DataAccess.OptionsDataAccess;
import app.symbolize.Game.GameController;
import app.symbolize.Game.GameUIView;
import app.symbolize.Game.GameView;
import app.symbolize.R;

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
        show_animation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                options_dao.Toggle_boolean_option(OptionsDataAccess.OPTION_SHOW_ANIMATIONS);
                show_animation.setChecked(!show_animation.isChecked());
            }
        });

        final SeekBar game_size_bar = (SeekBar) dialog_view.findViewById( R.id.options_game_size_seekbar );
        final EditText game_size_text = (EditText) dialog_view.findViewById( R.id.options_game_size_seekbar_text );
        game_size_bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            short progress_change;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progress_change = (short) ( progress + OptionsDataAccess.GAME_SIZE_MIN );
                GameView.Set_sizes(progress_change);
                if (Page.Is_Game_page()) {
                    GameController.Get_instance().Handle_request(new Request(Request.Background_change),
                            new Response());
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                options_dao.Set_short_option( OptionsDataAccess.OPTION_GAME_SIZE, progress_change );
                game_size_text.setText( progress_change + "" );
            }
        });

        game_size_text.addTextChangedListener( new TextWatcher() {
               @Override
               public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

               @Override
               public void onTextChanged(CharSequence s, int start, int before, int count) {}

               @Override
               public void afterTextChanged(Editable s) {
                   String new_text = s.toString();
                   if( !new_text.equals("") ) {
                       short new_game_size = (short) Math.min( 125, Math.max( 50, Short.parseShort( new_text ) ) );
                       options_dao.Set_short_option( OptionsDataAccess.OPTION_GAME_SIZE, new_game_size );
                       game_size_bar.setProgress( new_game_size - OptionsDataAccess.GAME_SIZE_MIN );
                   }
               }
           }
        );

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

        final SeekBar brightness_bar = (SeekBar) dialog_view.findViewById( R.id.options_brightness_seekbar );
        final EditText brightness_text = (EditText) dialog_view.findViewById( R.id.options_brightness_seekbar_text );
        brightness_bar.setOnSeekBarChangeListener( new SeekBar.OnSeekBarChangeListener() {
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
                brightness_text.setText( progress_change + "" );
            }
        } );

        brightness_text.addTextChangedListener( new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {}

                @Override
                public void afterTextChanged(Editable s) {
                    String new_text = s.toString();
                    if( !new_text.equals("") ) {
                        short new_brightness = (short) Math.min( OptionsDataAccess.BRIGHTNESS_SCALING,
                                                       Math.max( 0, Short.parseShort( new_text ) ) );
                        options_dao.Set_short_option( OptionsDataAccess.OPTION_BRIGHTNESS, new_brightness );
                        brightness_bar.setProgress( new_brightness );
                    }
                }
            }
        );

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
                .setChecked(options_dao.Get_boolean_option(OptionsDataAccess.OPTION_SHOW_ANIMATIONS ) );

        ( (SeekBar) dialog_view.findViewById( R.id.options_game_size_seekbar ) )
                .setProgress(options_dao.Get_short_option( OptionsDataAccess.OPTION_GAME_SIZE ) - OptionsDataAccess.GAME_SIZE_MIN );

        ( (EditText) dialog_view.findViewById( R.id.options_game_size_seekbar_text ) )
                .setText( options_dao.Get_short_option( OptionsDataAccess.OPTION_GAME_SIZE ) + "" );

        final boolean use_device_brightness = options_dao.Get_boolean_option( OptionsDataAccess.OPTION_USE_DEVICE_BRIGHTNESS );
        ( (CheckedTextView) dialog_view.findViewById( R.id.options_use_device_brightness ) )
                .setChecked( use_device_brightness );

        final SeekBar brightness_bar = (SeekBar) dialog_view.findViewById( R.id.options_brightness_seekbar );
        brightness_bar.setProgress( options_dao.Get_short_option( OptionsDataAccess.OPTION_BRIGHTNESS ) );
        brightness_bar.setEnabled( !use_device_brightness );

        final EditText brightness_text = (EditText) dialog_view.findViewById( R.id.options_brightness_seekbar_text );
        brightness_text.setText( options_dao.Get_short_option( OptionsDataAccess.OPTION_BRIGHTNESS ) + "" );
        brightness_text.setEnabled( !use_device_brightness );
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
