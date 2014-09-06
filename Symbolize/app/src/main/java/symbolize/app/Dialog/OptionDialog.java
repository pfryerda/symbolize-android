package symbolize.app.Dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.WindowManager;
import symbolize.app.R;

abstract public class OptionDialog extends InfoDialog {
    // Main method
    //-------------

    /*
     * See SymbolzieDialog::onCreateDialog
     */
    @Override
    public Dialog onCreateDialog( Bundle save_instance_state ) {
        AlertDialog dialog = (AlertDialog) super.onCreateDialog( save_instance_state );

        dialog.getWindow().clearFlags( WindowManager.LayoutParams.FLAG_DIM_BEHIND );

        return dialog;
    }

    /*
     * See SymbolizeDialog::get_dialog_animation
     */
    @Override
    protected Integer get_dialog_animation() {
        return R.style.OptionDialogAnimation;
    }
}
