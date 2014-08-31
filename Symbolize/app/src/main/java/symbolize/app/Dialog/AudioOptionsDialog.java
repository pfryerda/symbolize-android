package symbolize.app.Dialog;

import android.view.View;
import android.widget.SeekBar;

import symbolize.app.Common.Page;
import symbolize.app.DataAccess.OptionsDataAccess;
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

        final SeekBar volume_bar = (SeekBar) dialog_view.findViewById( R.id.options_volume_seekbar );
        volume_bar.setProgress( OptionsDataAccess.Get_volume() );

        volume_bar.setOnSeekBarChangeListener( new SeekBar.OnSeekBarChangeListener() {
            byte progress_change = 0;
            @Override
            public void onProgressChanged( SeekBar seekBar, int progress, boolean fromUser ) {
                progress_change = (byte) progress;
            }

            @Override
            public void onStartTrackingTouch( SeekBar seekBar ) {}

            @Override
            public void onStopTrackingTouch( SeekBar seekBar ) {
                OptionsDataAccess.Set_volume(progress_change);
            }
        } );

        return dialog_view;
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
