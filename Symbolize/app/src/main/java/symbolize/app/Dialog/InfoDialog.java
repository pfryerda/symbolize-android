package symbolize.app.Dialog;


import android.app.AlertDialog;
import android.content.DialogInterface;

import symbolize.app.Common.Page;
import symbolize.app.Game.GamePage;
import symbolize.app.R;

public class InfoDialog extends SymbolizeDialog {
    // Protected method
    //------------------

    @Override
    protected AlertDialog.Builder get_builder() {
        final AlertDialog.Builder builder = super.get_builder();

        builder.setNeutralButton( GamePage.Get_resource_string(R.string.close), new DialogInterface.OnClickListener() {
            @Override
            public void onClick( DialogInterface dialogInterface, int id ) {
                InfoDialog.this.getDialog().dismiss();
            }
        } );

        return builder;
    }

    @Override
    protected String get_dialog_id() {
        return Page.Get_resource_string(R.string.info_dialog_id);
    }

}
