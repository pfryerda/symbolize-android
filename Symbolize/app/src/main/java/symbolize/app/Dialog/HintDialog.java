package symbolize.app.Dialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import symbolize.app.Common.Session;
import symbolize.app.Common.Page;
import symbolize.app.DataAccess.MetaDataAccess;
import symbolize.app.Puzzle.Puzzle;
import symbolize.app.R;

public class HintDialog extends InfoDialog {
    // Inherited fields
    //------------------

    /*
    protected String title;
    protected String message;
    */


    // Setter methods
    //---------------

    /*
     * See SymbolizeDialog::Set_attrs
     */
    public void Set_attrs() {
        Session session = Session.Get_instance();
        Context context = Page.Get_context();
        Puzzle puzzle = session.Get_current_puzzle();

        String message = puzzle.Get_hint();

        message += "\n" + context.getString( R.string.draws_allowed ) + puzzle.Get_draw_restriction();

        if ( ( puzzle.Get_drag_restriction() > 0 ) || MetaDataAccess.Has_seen_drag() ) {
            message += "\n" + context.getString(R.string.drags_allowed) + puzzle.Get_drag_restriction();
        }

        if ( ( puzzle.Get_erase_restriction() > 0 ) || MetaDataAccess.Has_seen_erase() ) {
            message += "\n" + context.getString(R.string.erase_allowed) + puzzle.Get_erase_restriction();
        }

        if ( puzzle.Has_mechanics() ) {
            message += "\n" + context.getString(R.string.mechanics_allowed);
            if ( puzzle.Can_rotate() ) {
                message += "\n" + context.getString(R.string.rotate);
            }
            if ( puzzle.Can_flip() ) {
                message += "\n" + context.getString(R.string.flip);
            }
            if ( puzzle.Can_shift() ) {
                message += "\n" + context.getString(R.string.shift);
            }
            if ( puzzle.Can_change_color() ) {
                message += "\n" + context.getString(R.string.change_color);
            }
            if ( puzzle.Is_special_enabled() ) {
                message += "\n" + context.getString( R.string.special );
            }
        }

        super.Set_attrs( session.Get_current_puzzle_text(), message );
    }


    // Protected methods
    //-------------------

    /*
     * See SymbolizeDialog::get_dialog_view
     */
    @Override
    protected View get_dialog_view() {
        final View dialog_view = super.get_dialog_view();

        ( (TextView) dialog_view.findViewById( R.id.Puzzle ) ).setText( title );
        ( (TextView) dialog_view.findViewById( R.id.Content ) ).setText( message );

        set_tutorial( dialog_view );

        return dialog_view;
    }

    /*
     * See SymbolizeDialog::get_dialog_string_id
     */
    @Override
    protected String get_dialog_string_id() {
        return Page.Get_resource_string( R.string.hint_dialog_id );
    }

    /*
     * See SymbolizeDialog::get_dialog_id
     */
    @Override
    protected int get_dialog_id() {
        return R.layout.hint_dialog;
    }

    /*
     * Sets tutorial text/gifs into the hint dialog if applicable
     */
    private void set_tutorial( View dialog_view ) {
        Session session = Session.Get_instance();
        Puzzle current_puzzle = session.Get_current_puzzle();
        TextView tutorial_text = (TextView) dialog_view.findViewById( R.id.Tutorial_text );

        if ( session.Is_in_world_view() && !MetaDataAccess.Has_seen_world() ) {
            tutorial_text.setText( Page.Get_resource_string( R.string.world_tutorial ) );
        } else if ( ( current_puzzle.Get_draw_restriction() > 0 ) && !MetaDataAccess.Has_seen_draw() ) {
            tutorial_text.setText( Page.Get_resource_string( R.string.draw_tutorial ) );
        } else if ( current_puzzle.Can_rotate() && !MetaDataAccess.Has_seen_rotate() ) {
            tutorial_text.setText( Page.Get_resource_string( R.string.rotate_tutorial ) );
        } else if ( ( current_puzzle.Get_erase_restriction() > 0 ) && !MetaDataAccess.Has_seen_erase() ) {
            tutorial_text.setText( Page.Get_resource_string( R.string.erase_tutorial ) );
        } else if ( current_puzzle.Can_flip() && !MetaDataAccess.Has_seen_flip() ) {
            tutorial_text.setText( Page.Get_resource_string( R.string.flip_tutorial ) );
        } else if ( current_puzzle.Can_shift() && !MetaDataAccess.Has_seen_shift() ) {
            tutorial_text.setText( Page.Get_resource_string( R.string.shift_tutorial ) );
        } else if ( ( current_puzzle.Get_drag_restriction() > 0 ) && !MetaDataAccess.Has_seen_drag() ) {
            tutorial_text.setText( Page.Get_resource_string( R.string.drag_tutorial ) );
        } else if ( current_puzzle.Can_change_color() && !MetaDataAccess.Has_seen_drag() ) {
            tutorial_text.setText( Page.Get_resource_string( R.string.change_color_tutorial ) );
        } else if ( current_puzzle.Is_special_enabled() && !MetaDataAccess.Has_seen_special() ) {
            tutorial_text.setText( Page.Get_resource_string( R.string.special_tutorial ) );
        } else {
            tutorial_text.setText( "" );
        }
    }
}
