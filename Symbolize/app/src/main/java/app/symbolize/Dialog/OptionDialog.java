package app.symbolize.Dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;

import app.symbolize.Game.GameUIView;
import app.symbolize.R;

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


    // Protected methods
    //-------------------

    /*
     * See SymbolizeDialog::get_dialog_view
     */
    @Override
    protected View get_dialog_view() {
        final View dialog_view = super.get_dialog_view();

        ImageButton button = (ImageButton) dialog_view.findViewById( R.id.Close );
        if( button != null ) GameUIView.Set_touch_listener_highlight( button );

        button = (ImageButton) dialog_view.findViewById( R.id.options_reset_to_default );
        if( button != null ) GameUIView.Set_touch_listener_highlight( button );

        return dialog_view;
    }

    /*
     * See SymbolizeDialog::get_dialog_animation
     */
    @Override
    protected Integer get_dialog_animation() {
        return R.style.OptionDialogAnimation;
    }
}
