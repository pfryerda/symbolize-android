package app.symbolize.Dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.graphics.PorterDuff;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import app.symbolize.Common.Session;
import app.symbolize.Routing.Page;
import app.symbolize.DataAccess.OptionsDataAccess;
import app.symbolize.R;

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
    private ImageButton button;

    // Setter methods
    //----------------

    public void Set_attrs( String title, String message ) {
        this.title = title;
        this.message = message;
    }

    public void Set_Button( ImageButton button ) {
        this.button = button;
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
        update_animations( dialog );

        return dialog;
    }


    // Public methods
    //----------------

    /*
     * Display the dialog onto the page
     */
    public void Show() {
        show( dialog_manager, get_dialog_string_id());
        if( button != null ) button.setColorFilter( Session.Get_instance().Get_hightlight_color(), PorterDuff.Mode.MULTIPLY );
    }

    /*
     * See DialogFragment::onDismiss
     */
    @Override
    public void onDismiss( DialogInterface dialog ) {
        if( button != null ) button.setColorFilter(null);
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


    /*
     * Function used to update the dialog's animations when the 'show animations' setting is switched
     */
    protected void update_animations( Dialog dialog ) {
        Integer anim_id = get_dialog_animation();
        if( anim_id != null && OptionsDataAccess.Get_instance().Get_boolean_option( OptionsDataAccess.OPTION_SHOW_ANIMATIONS ) ) {
            dialog.getWindow().setWindowAnimations( anim_id );
        } else {
            dialog.getWindow().setWindowAnimations( R.style.DialogNoAnimation );
        }
    }

    protected void update_animations() {
        update_animations( getDialog() );
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
