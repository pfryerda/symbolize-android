package symbolize.app.Dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import symbolize.app.Common.SymbolizeActivity;
import symbolize.app.Game.GameActivity;
import symbolize.app.Puzzle.Puzzle;
import symbolize.app.R;

public class HintDialog extends SymbolizeDialog {
    // Fields
    //--------
    private String hint;

    // Main method
    //-------------

    @Override
    public Dialog onCreateDialog( Bundle save_instance_state ) {
        super.onCreateDialog( save_instance_state );

        final AlertDialog.Builder builder = new AlertDialog.Builder( getActivity() );
        final LayoutInflater inflater = getActivity().getLayoutInflater();
        final View dialog_view = inflater.inflate( R.layout.hint_dialog, null );

        builder.setView( dialog_view )
                .setNeutralButton( GameActivity.Get_resource_string(R.string.close), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick( DialogInterface dialogInterface, int id ) {
                                HintDialog.this.getDialog().dismiss();
                            }
                        }
                );
        ( (TextView) dialog_view.findViewById( R.id.hint_content ) ).setText( hint );

        return builder.create();
    }


    // Public method
    //---------------

    public void Set_attr( Puzzle puzzle ) {
        this.hint = puzzle.Get_hint();
    }


    // Protected method
    //------------------

    protected String Get_dialog_id() {
        return SymbolizeActivity.Get_resource_string( R.string.hint_dialog_id );
    }
}
