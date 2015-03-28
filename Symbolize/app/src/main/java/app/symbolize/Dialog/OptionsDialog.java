package app.symbolize.Dialog;

import android.view.View;
import android.widget.ImageButton;

import app.symbolize.Home.HomePage;
import app.symbolize.Routing.Page;
import app.symbolize.DataAccess.OptionsDataAccess;
import app.symbolize.R;

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
                GameOptionsDialog game_options_dialog = new GameOptionsDialog();
                game_options_dialog.Show();
            }
        } );

        dialog_view.findViewById( R.id.video_options_button ).setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View view ) {
                VideoOptionsDialog video_options_dialog = new VideoOptionsDialog();
                video_options_dialog.Set_parent_dialog( OptionsDialog.this );
                video_options_dialog.Show();
            }
        } );

        dialog_view.findViewById( R.id.audio_options_button ).setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View view ) {
                AudioOptionsDialog audio_options_dialog = new AudioOptionsDialog();
                audio_options_dialog.Show();
            }
        } );

        dialog_view.findViewById( R.id.data_options_button ).setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View view ) {
                DataOptionsDialog data_options_dialog = new DataOptionsDialog();
                data_options_dialog.Show();
            }
        } );

        dialog_view.findViewById( R.id.about_options_button ).setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View view ) {
                AboutDialog about_options_dialog = new AboutDialog();
                about_options_dialog.Show();
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
                        final OptionsDataAccess options_dao = OptionsDataAccess.Get_instance();
                        options_dao.Reset_game_options();
                        options_dao.Reset_audio_options();
                        options_dao.Reset_video_options();
                        if ( !Page.Is_Game_page() ) HomePage.Set_sound_image();
                        update_animations();
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
     * See SymbolizeDialog::get_dialog_animation
     */
    protected Integer get_dialog_animation() {
        if ( Page.Is_Game_page() ) {
            return R.style.OptionsDialogAnimation_2;
        } else {
            return R.style.OptionsDialogAnimation_1;
        }
    }

    /*
     * See SymbolizeDialog::get_dialog_string_id
     */
    @Override
    protected String get_dialog_string_id() {
        return Page.Get_resource_string( R.string.options_dialog_id );
    }

    /*
     * See SymbolizeDialog::get_dialog_background_id
     */
    @Override
    protected int get_dialog_background_id() {
        return R.id.options_dialog;
    }

    /*
     * See SymbolizeDialog::get_dialog_id
     */
    @Override
    protected int get_dialog_id() {
        return R.layout.options_dialog;
    }
}
