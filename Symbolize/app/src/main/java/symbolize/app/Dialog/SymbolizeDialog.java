package symbolize.app.Dialog;

import android.app.DialogFragment;
import android.app.FragmentManager;

import symbolize.app.Common.SymbolizeActivity;

abstract public class SymbolizeDialog extends DialogFragment {
    // Static fields
    //--------------

    protected static final FragmentManager dialog_manager = SymbolizeActivity.Get_dialog_manager();


    // Public methods
    //----------------

    public void Show() {
        this.show( dialog_manager, Get_dialog_id() );
    }


    // Protected method
    //------------------

    abstract protected String Get_dialog_id();
}
