package symbolize.app.Dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.os.Bundle;

import symbolize.app.Common.SymbolizeActivity;

abstract public class SymbolizeDialog extends DialogFragment {
    // Static fields
    //--------------

    protected static final FragmentManager dialog_manager = SymbolizeActivity.Get_dialog_manager();


    // Public methods
    //----------------

    public void Show() {
        this.show( dialog_manager, get_dialog_id() );
    }


    // Main method
    //-------------

    @Override
    public Dialog onCreateDialog( Bundle save_instance_state ) {
        return get_builder().create();

    }


    // Protected method
    //------------------

    protected AlertDialog.Builder get_builder() {
        return new AlertDialog.Builder( getActivity() );
    }



    // abstract methods
    //-----------------

    abstract protected String get_dialog_id();
}
