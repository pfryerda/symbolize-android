package symbolize.app.Dialog;


import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import symbolize.app.R;

abstract public class GenericDialog extends SymbolizeDialog {
    // Fields
    //-------

    private String title;
    private String message;


    // Public method
    //---------------

    public void Set_attrs( String title, String message ) {
        this.title = title;
        this.message = message;
    }


    // Protected method
    //------------------

    @Override
    protected AlertDialog.Builder get_builder() {
        final AlertDialog.Builder builder = super.get_builder();
        final LayoutInflater inflater = getActivity().getLayoutInflater();
        final View dialog_view = inflater.inflate( R.layout.generic_dialog, null );

        builder.setView( dialog_view );

        ( (TextView) dialog_view.findViewById( R.id.Title ) ).setText( title );
        ( (TextView) dialog_view.findViewById( R.id.Message ) ).setText( message );

        return builder;
    }


    // abstract methods
    //------------------

    @Override
    abstract protected String get_dialog_id();
}
