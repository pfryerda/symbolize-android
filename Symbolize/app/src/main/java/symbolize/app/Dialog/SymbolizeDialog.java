package symbolize.app.Dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import symbolize.app.Common.SymbolizeActivity;
import symbolize.app.R;

abstract public class SymbolizeDialog extends DialogFragment {
    // Static fields
    //--------------

    protected static final FragmentManager dialog_manager = SymbolizeActivity.Get_dialog_manager();


    // Public methods
    //----------------

    public void Show() {
        new Thread( new Runnable() {
            @Override
            public void run() {
                SymbolizeActivity.Get_activity().runOnUiThread(new Runnable() {
                   @Override
                   public void run() {
                       show( dialog_manager, get_dialog_id() );
                   }
               } );
            }
        } ).start();
    }


    // Main method
    //-------------

    @Override
    public Dialog onCreateDialog( Bundle save_instance_state ) {
        AlertDialog.Builder builder = get_builder();
        builder.setView( get_dialog_view() );
        return builder.create();

    }


    // Protected method
    //------------------

    protected AlertDialog.Builder get_builder() {
        return new AlertDialog.Builder( getActivity() );
    }



    // abstract methods
    //-----------------

    abstract protected View get_dialog_view();

    abstract protected String get_dialog_id();
}
