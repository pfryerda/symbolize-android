package symbolize.app.Dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import symbolize.app.Common.Page;
import symbolize.app.R;

/*
 * A decorator type class for android Dialog's, abstract aways the complexities of maintaining a dialog manager
 */
abstract public class SymbolizeDialog extends DialogFragment {
    // Static fields
    //--------------

    public static FragmentManager dialog_manager;


    // Fields
    //-------

    protected String title;
    protected String message;

    // Setter methods
    //----------------

    public void Set_attrs( String title, String message ) {
        this.title = title;
        this.message = message;
    }


    // Main method
    //-------------

    /*
     * Called right before the dialog is shown on the screen
     */
    @Override
    public Dialog onCreateDialog( Bundle save_instance_state ) {
        AlertDialog.Builder builder = get_builder();

        AlertDialog dialog = builder.create();
        dialog.setView( get_dialog_view(), 0, 0, 0, 0 );

        return dialog;
    }


    // Public methods
    //----------------

    /*
     * Display the dialog onto the page
     */
    public void Show() {
        show( dialog_manager, get_dialog_id() );
    }


    // Protected methods
    //------------------

    /*
     * Construct the dialog builder, that is used to create the dialog
     *
     * @return AlertDialog.Builder: The dialog builder of interest
     */
    protected AlertDialog.Builder get_builder() {
        AlertDialog.Builder builder = new AlertDialog.Builder( getActivity() );
        builder.setCancelable( true );
        return new AlertDialog.Builder( getActivity() );
    }

    /*
     * Get the dialog view from rest/layouts and set it up accordingly
     *
     * @return View: The view of interest
     */
    protected View get_dialog_view() {
        final LayoutInflater inflater = getActivity().getLayoutInflater();
        final View dialog_view =  inflater.inflate( R.layout.generic_dialog, new LinearLayout( Page.Get_context() ) );

        ( (TextView) dialog_view.findViewById( R.id.Title ) ).setText( title );
        ( (TextView) dialog_view.findViewById( R.id.Message ) ).setText( message );

        return dialog_view;
    }

    // abstract methods
    //-----------------

    /*
     * Get the dialog's id string used to get the view from res/layouts
     *
     * @return String: The id of interest
     */
    abstract protected String get_dialog_id();
}
