package symbolize.app.Dialog;

import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckedTextView;
import android.widget.SeekBar;
import android.widget.Spinner;

import symbolize.app.Routing.Page;
import symbolize.app.Common.Session;
import symbolize.app.DataAccess.OptionsDataAccess;
import symbolize.app.R;

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


        final CheckedTextView mute_button = (CheckedTextView) dialog_view.findViewById( R.id.options_mute );
        mute_button.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View view ) {
                options_dao.Toggle_boolean_option( OptionsDataAccess.OPTION_IS_MUTED );
                mute_button.setChecked( !mute_button.isChecked() );
                init_dialog_view( dialog_view );
            }
        } );

        ( (SeekBar) dialog_view.findViewById(R.id.options_volume_seekbar)).setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
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
            }
        });

        dialog_view.findViewById( R.id.options_reset_to_default ).setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final ConfirmDialog confirmDialog = new ConfirmDialog();
                confirmDialog.Set_attrs( getString( R.string.revert_to_default_title ), getString( R.string.revert_to_default_message ) );
                confirmDialog.SetConfirmationListener( new ConfirmDialog.ConfirmDialogListener() {
                    @Override
                    public void OnDialogSuccess() {
                        options_dao.Reset_audio_options();
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
        final OptionsDataAccess options_dao = OptionsDataAccess.Get_instance();

        ( (Spinner) dialog_view.findViewById( R.id.options_audio_output_spinner ) )
                .setSelection( options_dao.Get_short_option( OptionsDataAccess.OPTION_AUDIO_OUTPUT ) );

        final boolean is_muted = options_dao.Get_boolean_option( OptionsDataAccess.OPTION_IS_MUTED );
        ( (CheckedTextView) dialog_view.findViewById( R.id.options_mute ) )
                .setChecked( is_muted );

        final SeekBar volume_bar = (SeekBar) dialog_view.findViewById( R.id.options_volume_seekbar );
        volume_bar.setProgress( options_dao.Get_short_option( OptionsDataAccess.OPTION_VOLUME ) );
        volume_bar.setEnabled( !is_muted );

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
     * See SymbolizeDialog::get_dialog_id
     */
    @Override
    protected int get_dialog_id() {
        return R.layout.audio_options_dialog;
    }
}
