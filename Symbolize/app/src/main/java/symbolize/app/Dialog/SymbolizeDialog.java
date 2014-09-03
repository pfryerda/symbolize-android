package symbolize.app.Dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.RotateAnimation;
import android.widget.LinearLayout;
import android.widget.TextView;
import symbolize.app.Common.Page;
import symbolize.app.DataAccess.OptionsDataAccess;
import symbolize.app.Game.GameUIView;
import symbolize.app.R;

/*
 * A decorator type class for android Dialog's, abstract aways the complexities of maintaining a dialog manager
 */
abstract public class SymbolizeDialog extends DialogFragment {
    // Static fields
    //--------------

    public static FragmentManager dialog_manager;


    // Fields
    //-------

    protected String title;
    protected String message;

    // Setter methods
    //----------------

    public void Set_attrs( String title, String message ) {
        this.title = title;
        this.message = message;
    }


    // Main method
    //-------------

    /*
     * Called right before the dialog is shown on the screen
     */
    @Override
    public Dialog onCreateDialog( Bundle save_instance_state ) {
        AlertDialog.Builder builder = get_builder();

        AlertDialog dialog = builder.create();
        dialog.setView( get_dialog_view(), 0, 0, 0, 0 );
        Integer anim_id = get_dialog_animation();

        if ( ( anim_id ) != null &&
                OptionsDataAccess.Get_instance().Get_boolean_option( OptionsDataAccess.OPTION_SHOW_ANIMATIONS ) )
        {
            dialog.getWindow().getAttributes().windowAnimations = anim_id;
        } else {
            dialog.getWindow().getAttributes().windowAnimations = R.style.DialogNoAnimation;
        }

        return dialog;
    }


    // Public methods
    //----------------

    /*
     * Display the dialog onto the page
     */
    public void Show() {
        show( dialog_manager, get_dialog_string_id() );
    }


    // Protected methods
    //------------------

    /*
     * Construct the dialog builder, that is used to create the dialog
     *
     * @return AlertDialog.Builder: The dialog builder of interest
     */
    protected AlertDialog.Builder get_builder() {
        AlertDialog.Builder builder = new AlertDialog.Builder( getActivity() );
        builder.setCancelable( true );
        return new AlertDialog.Builder( getActivity() );
    }

    /*
     * Get the dialog view from rest/layouts and set it up accordingly
     *
     * @return View: The view of interest
     */
    protected View get_dialog_view() {
        final LayoutInflater inflater = getActivity().getLayoutInflater();
        final View dialog_view =  inflater.inflate( get_dialog_id(), new LinearLayout( Page.Get_context() ) );
        init_dialog_view( dialog_view );

        TextView title_view = (TextView) dialog_view.findViewById( R.id.Title );
        if ( title_view != null ) {
            title_view.setText( title );
        }

        TextView message_view = (TextView) dialog_view.findViewById( R.id.Message );
        if ( message_view != null ) {
            message_view.setText( message );
        }

        return dialog_view;
    }

    /*
     * Use this method to set seekbar and check boxes to correct state
     */
    protected void init_dialog_view( final View dialog_view ) {}

    /*
     * Get the dialog's animation in/out id value
     */
    protected Integer get_dialog_animation() {
        return R.style.DialogNoAnimation;
    }


    // abstract methods
    //-----------------

    /*
     * Get the dialog's id string used to get the view from res/layouts
     *
     * @return String: The id of interest
     */
    abstract protected String get_dialog_string_id();

    /*
     * Get dialog id
     */
    abstract protected int get_dialog_id();
}
