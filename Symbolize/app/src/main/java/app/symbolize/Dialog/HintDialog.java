package app.symbolize.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
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
        if( !set_tutorial( dialog_view ) ) {
            if( !set_mechanics( dialog_view ) ) dialog_view.findViewById( R.id.mechanics_layout ).setVisibility( View.GONE );
        } else {
            dialog_view.findViewById( R.id.mechanics_layout ).setVisibility( View.GONE );
        }

        ( (TextView) dialog_view.findViewById( R.id.Puzzle ) ).setText(title);
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
     * See SymbolizeDialog::get_dialog_background_id
     */
    @Override
    protected int get_dialog_background_id() {
        return R.id.hint_dialog;
    }

    /*
     * See SymbolizeDialog::get_dialog_id
     */
    @Override
    protected int get_dialog_id() {
        return R.layout.hint_dialog;
    }


    // Private methods
    //-----------------

    /*
     * Set's up the mechanics view
     */
    private boolean set_mechanics( View dialog_view ) {
        final Puzzle puzzle = Session.Get_instance().Get_current_puzzle();

        if ( puzzle.Has_mechanics() ) {
            WebView image = (WebView) dialog_view.findViewById( R.id.rotate_tutorial_image );
            if ( puzzle.Can_rotate() ) draw_tutorial_image( image, "file:///android_res/drawable/rotate_tutorial.gif" );
            else image.setVisibility( View.INVISIBLE );

            image = (WebView) dialog_view.findViewById( R.id.flip_tutorial_image );
            if ( puzzle.Can_flip() ) draw_tutorial_image( image, "file:///android_res/drawable/flip_tutorial.gif" );
            else image.setVisibility( View.INVISIBLE );

            image = (WebView) dialog_view.findViewById( R.id.shift_tutorial_image );
            if ( puzzle.Can_shift() ) draw_tutorial_image( image, "file:///android_res/drawable/shift_tutorial.gif" );
            else image.setVisibility( View.INVISIBLE );

            image = (WebView) dialog_view.findViewById( R.id.change_color_tutorial_image );
            if ( puzzle.Can_change_color() ) draw_tutorial_image( image, "file:///android_res/drawable/change_color_tutorial.gif" );
            else image.setVisibility( View.INVISIBLE );

            image = (WebView) dialog_view.findViewById( R.id.special_tutorial_image );
            if ( puzzle.Is_special_enabled() ) draw_tutorial_image( image, "file:///android_res/drawable/special_tutorial.gif" );
            else image.setVisibility( View.INVISIBLE );

            dialog_view.findViewById( R.id.mechanics_layout ).setVisibility( View.VISIBLE );
            return true;
        }
        return false;
    }

    /*
     * Sets tutorial text/gifs into the hint dialog if applicable
     */
    private boolean set_tutorial( View dialog_view ) {
        final Session session = Session.Get_instance();
        final Puzzle current_puzzle = session.Get_current_puzzle();
        final MetaDataAccess meta_dao = MetaDataAccess.Get_instance();

        TextView tutorial_text = (TextView) dialog_view.findViewById( R.id.Tutorial_text );

        if ( session.Is_in_world_view() && !meta_dao.Has_seen( MetaDataAccess.SEEN_WORLD ) ) {
            tutorial_text.setText( Page.Get_resource_string( R.string.world_tutorial ) );
            draw_tutorial_image( (WebView) dialog_view.findViewById( R.id.Tutorial_image ), "file:///android_res/drawable/world_tutorial.gif" );
        } else if ( ( current_puzzle.Get_draw_restriction() > 0 ) && !meta_dao.Has_seen( MetaDataAccess.SEEN_DRAW ) ) {
            tutorial_text.setText( Page.Get_resource_string( R.string.draw_tutorial ) );
            draw_tutorial_image( (WebView) dialog_view.findViewById( R.id.Tutorial_image ), "file:///android_res/drawable/draw_tutorial.gif" );
        } else if ( current_puzzle.Can_rotate() && !meta_dao.Has_seen( MetaDataAccess.SEEN_ROTATE ) ) {
            tutorial_text.setText( Page.Get_resource_string( R.string.rotate_tutorial ) );
            draw_tutorial_image( (WebView) dialog_view.findViewById( R.id.Tutorial_image ), "file:///android_res/drawable/rotate_tutorial.gif" );
        } else if ( ( current_puzzle.Get_erase_restriction() > 0 ) && !meta_dao.Has_seen( MetaDataAccess.SEEN_ERASE ) ) {
            tutorial_text.setText( Page.Get_resource_string( R.string.erase_tutorial ) );
            draw_tutorial_image( (WebView) dialog_view.findViewById( R.id.Tutorial_image ), "file:///android_res/drawable/erase_tutorial.gif" );
        } else if ( current_puzzle.Can_flip() && !meta_dao.Has_seen( MetaDataAccess.SEEN_FLIP ) ) {
            tutorial_text.setText( Page.Get_resource_string( R.string.flip_tutorial ) );
            draw_tutorial_image( (WebView) dialog_view.findViewById( R.id.Tutorial_image ), "file:///android_res/drawable/flip_tutorial.gif" );
        } else if ( current_puzzle.Can_shift() && !meta_dao.Has_seen( MetaDataAccess.SEEN_SHIFT ) ) {
            tutorial_text.setText( Page.Get_resource_string( R.string.shift_tutorial ) );
            draw_tutorial_image( (WebView) dialog_view.findViewById( R.id.Tutorial_image ), "file:///android_res/drawable/shift_tutorial.gif" );
        } else if ( ( current_puzzle.Get_drag_restriction() > 0 ) && !meta_dao.Has_seen( MetaDataAccess.SEEN_DRAG ) ) {
            tutorial_text.setText( Page.Get_resource_string( R.string.drag_tutorial ) );
            draw_tutorial_image( (WebView) dialog_view.findViewById( R.id.Tutorial_image ), "file:///android_res/drawable/drag_tutorial.gif" );
        } else if ( current_puzzle.Can_change_color() && !meta_dao.Has_seen( MetaDataAccess.SEEN_CHANGE_COLOR ) ) {
            tutorial_text.setText( Page.Get_resource_string( R.string.change_color_tutorial ) );
            draw_tutorial_image( (WebView) dialog_view.findViewById( R.id.Tutorial_image ), "file:///android_res/drawable/change_color_tutorial.gif" );
        } else if ( current_puzzle.Is_special_enabled() && !meta_dao.Has_seen( MetaDataAccess.SEEN_SPECIAL ) ) {
            tutorial_text.setText( Page.Get_resource_string( R.string.special_tutorial ) );
            draw_tutorial_image( (WebView) dialog_view.findViewById( R.id.Tutorial_image ), "file:///android_res/drawable/special_tutorial.gif" );
        } else {
            dialog_view.findViewById( R.id.Tutorial ).setVisibility( View.GONE );
            return false;
        }

        return true;
    }

    /*
     * A function used to display a gif on a webview
     */
    private void draw_tutorial_image( WebView view, String url ) {
        view.loadDataWithBaseURL( null, "<html><body><img style=\"width: 100%\" src=\"" + url + "\"></body></html>", "text/html", "UTF-8", null );
        view.setBackgroundColor( Color.TRANSPARENT );
    }
}
