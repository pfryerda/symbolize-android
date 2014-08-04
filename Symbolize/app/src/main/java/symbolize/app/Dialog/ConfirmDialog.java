package symbolize.app.Dialog;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;

import symbolize.app.Common.SymbolizeActivity;
import symbolize.app.Game.GameActivity;
import symbolize.app.R;

public class ConfirmDialog extends GenericDialog {
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

        builder.setPositiveButton( GameActivity.Get_resource_string( R.string.yes ), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick( DialogInterface dialogInterface, int id ) {
                        listener.OnDialogSuccess();
                    }
                } )
                .setNegativeButton( GameActivity.Get_resource_string( R.string.no ), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        listener.OnDialogFail();
                    }
                } );

        return builder;
    }

    @Override
    protected String get_dialog_id() {
        return SymbolizeActivity.Get_resource_string( R.string.confirmation_dialog_id );
    }
}
