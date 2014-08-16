package symbolize.app.Dialog;


import android.app.AlertDialog;
import android.content.DialogInterface;

import symbolize.app.Common.Page;
import symbolize.app.Game.GamePage;
import symbolize.app.R;

public class ConfirmDialog extends SymbolizeDialog {
    // Inherited fields
    //------------------

    /*
    protected String title;
    protected String message;
    */


    // Fields
    //-------

    private ConfirmDialogListener listener;


    // Interface setup
    //-------------------

    public interface ConfirmDialogListener {
        public void OnDialogSuccess();
        public void OnDialogFail();
    }


    // Setter methods
    //----------------

    public void SetConfirmationListener( ConfirmDialogListener listener ) {
        this.listener = listener;
    }


    // Protected method
    //------------------

    @Override
    public AlertDialog.Builder get_builder() {
        final AlertDialog.Builder builder = super.get_builder();

        builder.setPositiveButton( GamePage.Get_resource_string( R.string.yes ), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick( DialogInterface dialogInterface, int id ) {
                        listener.OnDialogSuccess();
                    }
                } )
                .setNegativeButton( GamePage.Get_resource_string( R.string.no ), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        listener.OnDialogFail();
                    }
                } );

        return builder;
    }

    @Override
    protected String get_dialog_id() {
        return Page.Get_resource_string( R.string.confirmation_dialog_id );
    }
}
