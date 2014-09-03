package symbolize.app.Dialog;

import symbolize.app.R;

abstract public class OptionDialog extends InfoDialog {
    /*
     * See SymbolizeDialog::get_dialog_animation
     */
    @Override
    protected Integer get_dialog_animation() {
        return R.style.OptionDialogAnimation;
    }
}
