package app.symbolize.Dialog;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;

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

        ( (Spinner) dialog_view.findViewById( R.id.options_audio_output_spinner ) ).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected( AdapterView<?> parent, View view, int pos, long id ) {
                options_dao.Set_short_option( OptionsDataAccess.OPTION_AUDIO_OUTPUT, (short) pos);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        } );


        final CheckBox mute_button = (CheckBox) dialog_view.findViewById( R.id.options_mute );
        mute_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                options_dao.Toggle_boolean_option(OptionsDataAccess.OPTION_IS_MUTED);
                init_dialog_view(dialog_view);
                if ( !Page.Is_Game_page() ) HomePage.Set_sound_image();
            }
        });

        final SeekBar volume_bar = (SeekBar) dialog_view.findViewById( R.id.options_volume_seekbar );
        final EditText volume_text = (EditText) dialog_view.findViewById( R.id.options_volume_seekbar_text );

        volume_bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            byte progress_change = 0;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progress_change = (byte) progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                options_dao.Set_short_option(OptionsDataAccess.OPTION_VOLUME, progress_change);
                volume_text.setText( progress_change + "" );
            }
        });


        volume_text.addTextChangedListener(new TextWatcher() {
             @Override
             public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

             @Override
             public void onTextChanged(CharSequence s, int start, int before, int count) {}

             @Override
             public void afterTextChanged(Editable s) {
                 String new_text = s.toString();
                 if( !new_text.equals("") ) {
                     short new_volume = (short) Math.min( 100, Math.max( 0, Short.parseShort( new_text ) ) );
                     options_dao.Set_short_option(OptionsDataAccess.OPTION_VOLUME, new_volume );
                     volume_bar.setProgress( Short.parseShort( new_text ) );
                 }
             }
         } );

        dialog_view.findViewById(R.id.options_reset_to_default).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final ConfirmDialog confirmDialog = new ConfirmDialog();
                confirmDialog.Set_attrs(getString(R.string.revert_to_default_title), getString(R.string.revert_to_default_message));
                confirmDialog.SetConfirmationListener(new ConfirmDialog.ConfirmDialogListener() {
                    @Override
                    public void OnDialogSuccess() {
                        options_dao.Reset_audio_options();
                        init_dialog_view(dialog_view);
                        if ( !Page.Is_Game_page() ) HomePage.Set_sound_image();
                    }

                    @Override
                    public void OnDialogFail() {
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

        ( (Spinner) dialog_view.findViewById( R.id.options_audio_output_spinner ) )
                .setSelection( options_dao.Get_short_option( OptionsDataAccess.OPTION_AUDIO_OUTPUT ) );

        final boolean is_muted = options_dao.Get_boolean_option( OptionsDataAccess.OPTION_IS_MUTED );
        ( (CheckBox) dialog_view.findViewById( R.id.options_mute ) )
                .setChecked( is_muted );

        final SeekBar volume_bar = (SeekBar) dialog_view.findViewById( R.id.options_volume_seekbar );
        volume_bar.setProgress( options_dao.Get_short_option( OptionsDataAccess.OPTION_VOLUME ) );
        volume_bar.setEnabled( !is_muted );

        final EditText volume_text = (EditText) dialog_view.findViewById( R.id.options_volume_seekbar_text );
        volume_text.setText( options_dao.Get_short_option( OptionsDataAccess.OPTION_VOLUME ) + "" );
        volume_text.setEnabled( !is_muted );

        if ( Session.VERSION != Session.VERSION_ALPHA ) {
            dialog_view.findViewById( R.id.alpha_audio_note ).setVisibility( View.GONE );
        }
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
