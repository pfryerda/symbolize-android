package app.symbolize.Dialog;

import android.view.View;
import android.widget.Button;

import app.symbolize.Routing.Page;
import app.symbolize.R;

/*
 * A generic dialog with two buttons at the bottom one for yes, and one for no
 */
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

    /*
     * See SymbolizeDialog::get_dialog_view
     */
    @Override
    protected View get_dialog_view() {
        final View dialog_view = super.get_dialog_view();

        Button positive_button = (Button) dialog_view.findViewById( R.id.Yes );
        positive_button.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View view ) {
                listener.OnDialogSuccess();
                ConfirmDialog.this.getDialog().dismiss();
            }
        } );

        Button negative_button = (Button) dialog_view.findViewById( R.id.No );
        negative_button.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View view ) {
                listener.OnDialogFail();
                ConfirmDialog.this.getDialog().dismiss();
            }
        } );

        return dialog_view;
    }

    /*
     * See SymbolizeDialog::get_dialog_string_id
     */
    @Override
    protected String get_dialog_string_id() {
        return Page.Get_resource_string( R.string.confirmation_dialog_id );
    }

    /*
 * See SymbolizeDialog::get_dialog_background_id
 */
    @Override
    protected int get_dialog_background_id() {
        return R.id.confirmation_dialog;
    }

    /*
     * See SymbolizeDialog::get_dialog_id
     */
    @Override
    protected int get_dialog_id() {
        return R.layout.confirmation_dialog;
    }
}
