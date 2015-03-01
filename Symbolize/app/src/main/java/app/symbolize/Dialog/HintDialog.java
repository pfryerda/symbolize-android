package app.symbolize.Dialog;

import android.content.Context;
import android.view.View;
import android.widget.TextView;
import app.symbolize.Common.Session;
import app.symbolize.Routing.Page;
import app.symbolize.DataAccess.MetaDataAccess;
import app.symbolize.Puzzle.Puzzle;
import app.symbolize.R;

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
        final Session session = Session.Get_instance();
        final Context context = Page.Get_context();
        final Puzzle puzzle = session.Get_current_puzzle();
        final MetaDataAccess meta_dao = MetaDataAccess.Get_instance();

        String message = puzzle.Get_hint();

        if ( !session.Is_in_world_view() ) {
            message += "\n";
            message += "\n" + context.getString( R.string.draws_allowed ) + puzzle.Get_draw_restriction();

            if ( ( puzzle.Get_drag_restriction() > 0 ) || meta_dao.Has_seen( MetaDataAccess.SEEN_DRAG ) ) {
                message += "\n" + context.getString( R.string.drags_allowed ) + puzzle.Get_drag_restriction();
            }

            if ( ( puzzle.Get_erase_restriction() > 0 ) || meta_dao.Has_seen( MetaDataAccess.SEEN_ERASE ) ) {
                message += "\n" + context.getString( R.string.erase_allowed ) + puzzle.Get_erase_restriction();
            }
        }


        if ( puzzle.Has_mechanics() ) {
            message +=  "\n\n" + context.getString( R.string.mechanics_allowed ) + "\n"; // BUG: THIS STOPS ANIMATION WORKING PROPERLY MOVE TO XML
            if ( puzzle.Can_rotate() ) {
                message += context.getString( R.string.rotate ) + " ";
            }
            if ( puzzle.Can_flip() ) {
                message += context.getString( R.string.flip ) + " ";
            }
            if ( puzzle.Can_shift() ) {
                message += context.getString( R.string.shift ) + " ";
            }
            if ( puzzle.Can_change_color() ) {
                message += context.getString( R.string.change_color ) + " ";
            }
            if ( puzzle.Is_special_enabled() ) {
                message += context.getString( R.string.special ) + " ";
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

        Set_attrs();
        set_tutorial( dialog_view );

        ( (TextView) dialog_view.findViewById( R.id.Puzzle ) ).setText( title );
        ( (TextView) dialog_view.findViewById( R.id.Content ) ).setText( message );

        return dialog_view;
    }

    /*
     * See SymbolizeDialog::get_dialog_animation
     */
    @Override
    protected Integer get_dialog_animation() {
        return R.style.HintDialogAnimation;
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
        final Session session = Session.Get_instance();
        final Puzzle current_puzzle = session.Get_current_puzzle();
        final MetaDataAccess meta_dao = MetaDataAccess.Get_instance();

        TextView tutorial_text = (TextView) dialog_view.findViewById( R.id.Tutorial_text );

        if ( session.Is_in_world_view() && !meta_dao.Has_seen( MetaDataAccess.SEEN_WORLD ) ) {
            tutorial_text.setText( Page.Get_resource_string( R.string.world_tutorial ) );
        } else if ( ( current_puzzle.Get_draw_restriction() > 0 ) && !meta_dao.Has_seen( MetaDataAccess.SEEN_DRAW ) ) {
            tutorial_text.setText( Page.Get_resource_string( R.string.draw_tutorial ) );
        } else if ( current_puzzle.Can_rotate() && !meta_dao.Has_seen( MetaDataAccess.SEEN_ROTATE ) ) {
            tutorial_text.setText( Page.Get_resource_string( R.string.rotate_tutorial ) );
        } else if ( ( current_puzzle.Get_erase_restriction() > 0 ) && !meta_dao.Has_seen( MetaDataAccess.SEEN_ERASE ) ) {
            tutorial_text.setText( Page.Get_resource_string( R.string.erase_tutorial ) );
        } else if ( current_puzzle.Can_flip() && !meta_dao.Has_seen( MetaDataAccess.SEEN_FLIP ) ) {
            tutorial_text.setText( Page.Get_resource_string( R.string.flip_tutorial ) );
        } else if ( current_puzzle.Can_shift() && !meta_dao.Has_seen( MetaDataAccess.SEEN_SHIFT ) ) {
            tutorial_text.setText( Page.Get_resource_string( R.string.shift_tutorial ) );
        } else if ( ( current_puzzle.Get_drag_restriction() > 0 ) && !meta_dao.Has_seen( MetaDataAccess.SEEN_DRAG ) ) {
            tutorial_text.setText( Page.Get_resource_string( R.string.drag_tutorial ) );
        } else if ( current_puzzle.Can_change_color() && !meta_dao.Has_seen( MetaDataAccess.SEEN_CHANGE_COLOR ) ) {
            tutorial_text.setText( Page.Get_resource_string( R.string.change_color_tutorial ) );
        } else if ( current_puzzle.Is_special_enabled() && !meta_dao.Has_seen( MetaDataAccess.SEEN_SPECIAL ) ) {
            tutorial_text.setText( Page.Get_resource_string( R.string.special_tutorial ) );
        } else {
            tutorial_text.setText( "" );
        }
    }
}
