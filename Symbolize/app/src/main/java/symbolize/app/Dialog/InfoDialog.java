package symbolize.app.Dialog;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.View;
import android.widget.Button;

import symbolize.app.Common.Page;
import symbolize.app.Game.GamePage;
import symbolize.app.R;

/*
 * A generic dialog with a button at the bottom for closing
 */
public class InfoDialog extends SymbolizeDialog {
    // Inherited fields
    //------------------

    /*
    protected String title;
    protected String message;
    */


    // Protected method
    //------------------

    /*
     * See SymbolizeDialog::get_dialog_view
     */
    @Override
    protected View get_dialog_view() {
        final View dialog_view = super.get_dialog_view();

        Button close_button = (Button) dialog_view.findViewById( R.id.Close );
        close_button.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View view ) {
                InfoDialog.this.getDialog().dismiss();
            }
        } );

        return dialog_view;
    }

    /*
     * See SymbolizeDialog::get_dialog_string_id
     */
    @Override
    protected String get_dialog_string_id() {
        return Page.Get_resource_string( R.string.info_dialog_id );
    }

    /*
     * See SymbolizeDialog::get_dialog_id
     */
    @Override
    protected int get_dialog_id() {
        return R.layout.info_dialog;
    }

}
