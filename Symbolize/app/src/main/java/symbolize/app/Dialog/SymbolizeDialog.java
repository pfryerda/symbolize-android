package symbolize.app.Dialog;


import android.app.AlertDialog;
import android.app.Dialog;

abstract public class SymbolizeDialog {
    // Field
    //------

    protected Dialog dialog;

    // Method
    //--------

    public void Show_dialog() {
        dialog.show();
    }

    public void Close_dialog() {
        dialog.dismiss();
    }
}
