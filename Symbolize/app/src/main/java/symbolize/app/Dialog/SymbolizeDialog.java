package symbolize.app.Dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import symbolize.app.Common.Page;
import symbolize.app.Game.GamePage;
import symbolize.app.R;

abstract public class SymbolizeDialog extends DialogFragment {
    // Static fields
    //--------------

    public static FragmentManager dialog_manager;


    // Fields
    //-------

    protected String title;
    protected String message;


    // Main method
    //-------------

    @Override
    public Dialog onCreateDialog( Bundle save_instance_state ) {
        AlertDialog.Builder builder = get_builder();

        AlertDialog dialog = builder.create();
        dialog.setView( get_dialog_view(), 0, 0, 0, 0 );

        return dialog;
    }


    // Public methods
    //----------------

    public void Show() {
        show( dialog_manager, get_dialog_id() );
    }

    public void Set_attrs( String title, String message ) {
        this.title = title;
        this.message = message;
    }


    // Protected methods
    //------------------

    protected AlertDialog.Builder get_builder() {
        AlertDialog.Builder builder = new AlertDialog.Builder( getActivity() );
        builder.setCancelable( true );
        return new AlertDialog.Builder( getActivity() );
    }

    protected View get_dialog_view() {
        final LayoutInflater inflater = getActivity().getLayoutInflater();
        final View dialog_view =  inflater.inflate( R.layout.generic_dialog, new LinearLayout( Page.Get_context() ) );

        ( (TextView) dialog_view.findViewById( R.id.Title ) ).setText( title );
        ( (TextView) dialog_view.findViewById( R.id.Message ) ).setText( message );

        return dialog_view;
    }

    // abstract methods
    //-----------------

    abstract protected String get_dialog_id();
}
