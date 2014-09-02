package symbolize.app.Dialog;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;

import symbolize.app.Common.Page;
import symbolize.app.Common.Session;
import symbolize.app.DataAccess.MetaDataAccess;
import symbolize.app.DataAccess.OptionsDataAccess;
import symbolize.app.DataAccess.ProgressDataAccess;
import symbolize.app.DataAccess.UnlocksDataAccess;
import symbolize.app.Game.GamePage;
import symbolize.app.Home.HomePage;
import symbolize.app.R;

public class AudioOptionsDialog extends InfoDialog {
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

        ( (SeekBar) dialog_view.findViewById( R.id.options_volume_seekbar ) ).setOnSeekBarChangeListener( new SeekBar.OnSeekBarChangeListener() {
            byte progress_change = 0;
            @Override
            public void onProgressChanged( SeekBar seekBar, int progress, boolean fromUser ) {
                progress_change = (byte) progress;
            }

            @Override
            public void onStartTrackingTouch( SeekBar seekBar ) {}

            @Override
            public void onStopTrackingTouch( SeekBar seekBar ) {
                options_dao.Set_short_option( OptionsDataAccess.OPTION_VOLUME, progress_change);
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
        ( (SeekBar) dialog_view.findViewById( R.id.options_volume_seekbar ) )
                .setProgress( OptionsDataAccess.Get_instance().Get_short_option( OptionsDataAccess.OPTION_VOLUME ) );
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
