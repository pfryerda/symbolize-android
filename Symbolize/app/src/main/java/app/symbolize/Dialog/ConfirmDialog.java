package app.symbolize.Dialog;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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

    //  Fields
    //---------

    private String positive_text = null;
    private String negative_text = null;


    // Fields
    //-------

    protected ConfirmDialogListener listener;


    // Interface setup
    //-------------------

    public interface ConfirmDialogListener {
        public void OnDialogSuccess();
        public void onDialogNeutral();
        public void OnDialogFail();
    }


    // Setter methods
    //----------------

    public void SetConfirmationListener( ConfirmDialogListener listener ) {
        this.listener = listener;
    }

    public void Set_Button_Text( final String positive_text, final String negative_text ) {
        this.positive_text = positive_text;
        this.negative_text = negative_text;
    }


    // Protected methods
    //------------------

    /*
     * See SymbolizeDialog::get_dialog_view
     */
    @Override
    protected View get_dialog_view() {
        final View dialog_view = super.get_dialog_view();

        Button positive_button = (Button) dialog_view.findViewById( R.id.Yes );
        if( positive_text != null ) ( (TextView) positive_button.findViewById( R.id.Yes ) ).setText( positive_text );
        positive_button.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View view ) {
                listener.OnDialogSuccess();
                Dismiss();
            }
        } );

        Button negative_button = (Button) dialog_view.findViewById( R.id.No );
        if( negative_text != null ) ( (TextView) negative_button.findViewById( R.id.No ) ).setText( negative_text );
        negative_button.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View view ) {
                listener.OnDialogFail();
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
