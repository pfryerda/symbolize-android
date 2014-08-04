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
    protected View get_dialog_view() {
        final LayoutInflater inflater = getActivity().getLayoutInflater();
        final View dialog_view =  inflater.inflate( R.layout.generic_dialog, null );

        ( (TextView) dialog_view.findViewById( R.id.Title ) ).setText( title );
        ( (TextView) dialog_view.findViewById( R.id.Message ) ).setText( message );

        return dialog_view;
    }


    // abstract methods
    //------------------

    @Override
    abstract protected String get_dialog_id();

}
