package symbolize.app.Dialog;

import android.content.Intent;
import android.view.View;
import android.widget.Button;

import symbolize.app.Common.Page;
import symbolize.app.DataAccess.MetaDataAccess;
import symbolize.app.DataAccess.ProgressDataAccess;
import symbolize.app.DataAccess.UnlocksDataAccess;
import symbolize.app.Game.GamePage;
import symbolize.app.Home.HomePage;
import symbolize.app.R;

public class DataOptionsDialog extends InfoDialog {
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

        Button delete_all_data_button = (Button) dialog_view.findViewById( R.id.options_delete_data );

        delete_all_data_button.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View view ) {
                final ConfirmDialog confirmDialog = new ConfirmDialog();
                confirmDialog.Set_attrs( getString( R.string.delete_all_data_title ), getString( R.string.delete_all_data_msg ) );
                confirmDialog.SetConfirmationListener( new ConfirmDialog.ConfirmDialogListener() {
                    @Override
                    public void OnDialogSuccess() {
                        UnlocksDataAccess.Get_instance().Remove_all_unlocks();
                        ProgressDataAccess.Get_instance().Remove_all_progress();
                        MetaDataAccess.Get_instance().Reset_meta_data_access();
                        startActivity( new Intent( GamePage.Get_context().getApplicationContext(), HomePage.class ) );
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
     * See SymbolizeDialog::get_dialog_string_id
     */
    @Override
    protected String get_dialog_string_id() {
        return Page.Get_resource_string( R.string.data_options_dialog_id );
    }

    /*
     * See SymbolizeDialog::get_dialog_id
     */
    @Override
    protected int get_dialog_id() {
        return R.layout.data_options_dialog;
    }
}
