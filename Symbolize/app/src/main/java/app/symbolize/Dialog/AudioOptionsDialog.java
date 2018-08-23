package app.symbolize.Dialog;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;

import app.symbolize.Common.MusicController;
import app.symbolize.Home.HomePage;
import app.symbolize.Routing.Page;
import app.symbolize.Common.Session;
import app.symbolize.DataAccess.OptionsDataAccess;
import app.symbolize.R;

public class AudioOptionsDialog extends OptionDialog {
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

        final CheckBox mute_button = (CheckBox) dialog_view.findViewById( R.id.options_mute );
        mute_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                options_dao.Toggle_boolean_option(OptionsDataAccess.OPTION_IS_MUTED);
                init_dialog_view(dialog_view);
                if ( !Page.Is_Game_page() ) HomePage.Set_sound_image();
                MusicController.Set_volume();
            }
        });

        final SeekBar music_volume_bar = (SeekBar) dialog_view.findViewById( R.id.options_music_volume_seekbar );
        final EditText music_volume_text = (EditText) dialog_view.findViewById( R.id.options_music_volume_seekbar_text );

        music_volume_bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            byte progress_change = 0;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progress_change = (byte) progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                MusicController.PlaySound( Page.Get_context(), MusicController.CLICK_SOUND );
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                MusicController.PlaySound( Page.Get_context(), MusicController.CLICK_SOUND );
                options_dao.Set_short_option(OptionsDataAccess.OPTION_VOLUME_MUSIC, progress_change);
                music_volume_text.setText( progress_change + "" );
            }
        });


        music_volume_text.addTextChangedListener(new TextWatcher() {
             @Override
             public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

             @Override
             public void onTextChanged(CharSequence s, int start, int before, int count) {}

             @Override
             public void afterTextChanged(Editable s) {
                 String new_text = s.toString();
                 if( !new_text.equals("") ) {
                     short new_volume = (short) Math.min( 100, Math.max( 0, Short.parseShort( new_text ) ) );
                     options_dao.Set_short_option(OptionsDataAccess.OPTION_VOLUME_MUSIC, new_volume);
                     music_volume_bar.setProgress(Short.parseShort(new_text));
                     MusicController.Set_volume();
                 }
             }
         } );

        final SeekBar sound_volume_bar = (SeekBar) dialog_view.findViewById( R.id.options_sound_volume_seekbar );
        final EditText sound_volume_text = (EditText) dialog_view.findViewById( R.id.options_sound_volume_seekbar_text );

        sound_volume_bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            byte progress_change = 0;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progress_change = (byte) progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                MusicController.PlaySound( Page.Get_context(), MusicController.CLICK_SOUND );
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                MusicController.PlaySound( Page.Get_context(), MusicController.CLICK_SOUND );
                options_dao.Set_short_option(OptionsDataAccess.OPTION_VOLUME_SOUND, progress_change);
                sound_volume_text.setText( progress_change + "" );
            }
        });


        sound_volume_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String new_text = s.toString();
                if( !new_text.equals("") ) {
                    short new_volume = (short) Math.min( 100, Math.max( 0, Short.parseShort( new_text ) ) );
                    options_dao.Set_short_option(OptionsDataAccess.OPTION_VOLUME_SOUND, new_volume);
                    sound_volume_bar.setProgress(Short.parseShort(new_text));
                }
            }
        } );

        dialog_view.findViewById(R.id.options_reset_to_default).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MusicController.PlaySound( Page.Get_context(), MusicController.CLICK_SOUND );
                final ConfirmDialog confirmDialog = new ConfirmDialog();
                confirmDialog.Set_Button_Text( Page.Get_resource_string( R.string.revert ), Page.Get_resource_string( R.string.cancel ) );
                confirmDialog.Set_attrs(getString(R.string.revert_to_default_title), getString(R.string.revert_to_default_message));
                confirmDialog.SetConfirmationListener(new ConfirmDialog.ConfirmDialogListener() {
                    @Override
                    public void OnDialogSuccess() {
                        MusicController.PlaySound( Page.Get_context(), MusicController.CLICK_SOUND );
                        options_dao.Reset_audio_options();
                        init_dialog_view(dialog_view);
                        if ( !Page.Is_Game_page() ) HomePage.Set_sound_image();
                    }

                    @Override
                    public void onDialogNeutral() {
                        MusicController.PlaySound( Page.Get_context(), MusicController.CLICK_SOUND );
                    }

                    @Override
                    public void OnDialogFail() {
                        MusicController.PlaySound( Page.Get_context(), MusicController.CLICK_SOUND );
                    }
                });
                confirmDialog.Show();
            }
        });

        return dialog_view;
    }

    /*
     * See SymbolizeDialog::init_dialog_view
     */
    @Override
    protected void init_dialog_view( final View dialog_view ) {
        final OptionsDataAccess options_dao = OptionsDataAccess.Get_instance();

        final boolean is_muted = options_dao.Get_boolean_option( OptionsDataAccess.OPTION_IS_MUTED );
        ( (CheckBox) dialog_view.findViewById( R.id.options_mute ) )
                .setChecked( is_muted );

        final SeekBar music_volume_bar = (SeekBar) dialog_view.findViewById( R.id.options_music_volume_seekbar );
        music_volume_bar.setProgress( options_dao.Get_short_option( OptionsDataAccess.OPTION_VOLUME_MUSIC ) );
        music_volume_bar.setEnabled( !is_muted );

        final EditText music_volume_text = (EditText) dialog_view.findViewById( R.id.options_music_volume_seekbar_text );
        music_volume_text.setText( options_dao.Get_short_option( OptionsDataAccess.OPTION_VOLUME_MUSIC ) + "" );
        music_volume_text.setEnabled( !is_muted );

        final SeekBar sound_volume_bar = (SeekBar) dialog_view.findViewById( R.id.options_sound_volume_seekbar );
        sound_volume_bar.setProgress( options_dao.Get_short_option( OptionsDataAccess.OPTION_VOLUME_SOUND ) );
        sound_volume_bar.setEnabled( !is_muted );

        final EditText sound_volume_text = (EditText) dialog_view.findViewById( R.id.options_sound_volume_seekbar_text );
        sound_volume_text.setText( options_dao.Get_short_option( OptionsDataAccess.OPTION_VOLUME_SOUND ) + "" );
        sound_volume_text.setEnabled( !is_muted );
    }

    /*
     * See SymbolizeDialog::get_dialog_string_id
     */
    @Override
    protected String get_dialog_string_id() {
        return Page.Get_resource_string( R.string.audio_options_dialog_id );
    }

    /*
     * See SymbolizeDialog::get_dialog_background_id
     */
    @Override
    protected int get_dialog_background_id() {
        return R.id.audio_options_dialog;
    }

    /*
     * See SymbolizeDialog::get_dialog_id
     */
    @Override
    protected int get_dialog_id() {
        return R.layout.audio_options_dialog;
    }
}
