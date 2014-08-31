package symbolize.app.Dialog;

import android.view.View;

import symbolize.app.Common.Page;
import symbolize.app.R;

public class OptionsDialog extends InfoDialog {
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

        dialog_view.findViewById( R.id.game_options_button ).setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View view ) {
                GameOptionsDialog options_dialog = new GameOptionsDialog();
                options_dialog.Show();
            }
        } );

        dialog_view.findViewById( R.id.video_options_button ).setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View view ) {
                VideoOptionsDialog options_dialog = new VideoOptionsDialog();
                options_dialog.Show();
            }
        } );

        dialog_view.findViewById( R.id.audio_options_button ).setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View view ) {
                AudioOptionsDialog options_dialog = new AudioOptionsDialog();
                options_dialog.Show();
            }
        } );

        dialog_view.findViewById( R.id.data_options_button ).setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View view ) {
                DataOptionsDialog options_dialog = new DataOptionsDialog();
                options_dialog.Show();
            }
        } );

        dialog_view.findViewById( R.id.about_options_button ).setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View view ) {
                AboutDialog options_dialog = new AboutDialog();
                options_dialog.Show();
            }
        } );

        return dialog_view;
    }

    /*
     * See SymbolizeDialog::get_dialog_string_id
     */
    @Override
    protected String get_dialog_string_id() {
        return Page.Get_resource_string( R.string.options_dialog_id );
    }

    /*
     * See SymbolizeDialog::get_dialog_id
     */
    @Override
    protected int get_dialog_id() {
        return R.layout.options_dialog;
    }
}
