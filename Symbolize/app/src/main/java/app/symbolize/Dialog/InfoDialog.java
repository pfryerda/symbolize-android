package app.symbolize.Dialog;

import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import app.symbolize.Routing.Page;
import app.symbolize.R;

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
        View close_button = dialog_view.findViewById( R.id.Close );
        close_button.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View view ) {
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
        return Page.Get_resource_string( R.string.info_dialog_id );
    }

    /*
     * See SymbolizeDialog::get_dialog_background_id
     */
    @Override
    protected int get_dialog_background_id() {
        return R.id.info_dialog;
    }

    /*
     * See SymbolizeDialog::get_dialog_id
     */
    @Override
    protected int get_dialog_id() {
        return R.layout.info_dialog;
    }

}
