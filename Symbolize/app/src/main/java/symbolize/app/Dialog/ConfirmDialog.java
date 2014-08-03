package symbolize.app.Dialog;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;

import symbolize.app.Game.GameActivity;
import symbolize.app.R;

public class ConfirmDialog extends DialogFragment {
    // Fields
    //-------

    private ConfirmDialogListener listener;
    private String title;
    private String message;


    // Interface setup
    //-------------------

    public interface ConfirmDialogListener {
        public void OnDialogSuccess();
        public void OnDialogFail();
    }


    // Main method
    //-------------

    @Override
    public Dialog onCreateDialog( Bundle save_instance_state ) {
        super.onCreateDialog( save_instance_state );

        final AlertDialog.Builder builder = new AlertDialog.Builder( getActivity() );
        final LayoutInflater inflater = getActivity().getLayoutInflater();

        builder.setTitle( title )
            .setMessage( message )
            .setPositiveButton( GameActivity.Get_resource_string( R.string.yes ), new DialogInterface.OnClickListener() {
                @Override
                public void onClick( DialogInterface dialogInterface, int id ) {
                    listener.OnDialogSuccess();
                }
            } )
            .setNeutralButton( GameActivity.Get_resource_string( R.string.no ), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    listener.OnDialogFail();
                }
            } );

        return builder.create();
    }


    // Setter methods
    //----------------

    public void Set_attr( String title, String msg ) {
        this.title = title;
        this.message = msg;
    }

    public void SetConfirmationListener( ConfirmDialogListener listener ) {
        this.listener = listener;
    }
}
