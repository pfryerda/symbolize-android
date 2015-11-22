package app.symbolize.Dialog;

import android.graphics.Typeface;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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

    // Fields
    //--------

    private String neutral_text = null;


    // Setter method
    //---------------

    public void Set_Button_Text( final String positive_text, final String negative_text, final String neutral_text ) {
        super.Set_Button_Text( positive_text, negative_text );
        this.neutral_text = neutral_text;
    }


    // Protected methods
    //------------------

    /*
     * See SymbolizeDialog::get_dialog_view
     */
    @Override
    protected View get_dialog_view() {
        final View dialog_view = super.get_dialog_view();

        Button neutral_button = (Button) dialog_view.findViewById( R.id.Maybe );
        if( neutral_text != null ) neutral_button.setText( neutral_text );
        if( dominant_button == DominantButton.NEUTRAL ) neutral_button.setTypeface( null, Typeface.BOLD );
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
