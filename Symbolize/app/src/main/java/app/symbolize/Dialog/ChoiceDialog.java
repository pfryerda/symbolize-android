package app.symbolize.Dialog;

import android.view.View;
import android.widget.Button;

import app.symbolize.Routing.Page;
import app.symbolize.R;

/*
 * A generic dialog with two buttons at the bottom one for yes, and one for no
 */
public class ChoiceDialog extends ConfirmDialog {
    // Inherited fields
    //------------------

    /*
    protected String title;
    protected String message;
    protected ChoiceDialogListener listener;
    */

    // Protected methods
    //------------------

    /*
     * See SymbolizeDialog::get_dialog_view
     */
    @Override
    protected View get_dialog_view() {
        final View dialog_view = super.get_dialog_view();

        Button neutral_button = (Button) dialog_view.findViewById( R.id.Maybe );
        neutral_button.setOnClickListener( new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               listener.onDialogNeutral();
               Dismiss();
           }
       } );

        return dialog_view;
    }

    /*
     * See SymbolizeDialog::get_dialog_string_id
     */
    @Override
    protected String get_dialog_string_id() {
        return Page.Get_resource_string( R.string.choice_dialog_id );
    }

    /*
 * See SymbolizeDialog::get_dialog_background_id
 */
    @Override
    protected int get_dialog_background_id() {
        return R.id.choice_dialog;
    }

    /*
     * See SymbolizeDialog::get_dialog_id
     */
    @Override
    protected int get_dialog_id() {
        return R.layout.choice_dialog;
    }
}
