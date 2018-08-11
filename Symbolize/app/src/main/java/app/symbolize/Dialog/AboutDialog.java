package app.symbolize.Dialog;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.TextView;

import app.symbolize.Common.Session;
import app.symbolize.Routing.Page;
import app.symbolize.R;

public class AboutDialog extends OptionDialog {
    // Inherited fields
    //------------------

    /*
    protected String title;
    protected String message;
    */


    // Protected methods
    //-------------------

    /*
     * See SymbolizeDialog::init_dialog_view
     */
    @Override
    protected void init_dialog_view( final View dialog_view) {
        // Edit seekbar glow (assume android doesn't change the name of their drawables....)
        final int glowDrawableId = Page.Get_context().getResources().getIdentifier( "overscroll_glow", "drawable", "android" );
        final int edgeDrawableId = Page.Get_context().getResources().getIdentifier("overscroll_edge", "drawable", "android");

        if( glowDrawableId > 0 && edgeDrawableId > 0 ) {
            final Drawable androidGlow = Page.Get_context().getResources().getDrawable( glowDrawableId );
            final Drawable androidEdge = Page.Get_context().getResources().getDrawable( edgeDrawableId );

            if ( androidGlow != null && androidEdge != null ) {
                androidGlow.setColorFilter( Session.Get_instance().Get_hightlight_color(), PorterDuff.Mode.SRC_IN );
                androidEdge.setColorFilter( Session.Get_instance().Get_hightlight_color(), PorterDuff.Mode.SRC_IN );
            } else {
                dialog_view.findViewById( R.id.about_scrollview ).setOverScrollMode( View.OVER_SCROLL_NEVER );
            }
        }

        // This is needed for links to work
        TextView textWithLink = (TextView) dialog_view.findViewById(R.id.about_music);
        textWithLink.setMovementMethod(LinkMovementMethod.getInstance());

        textWithLink = (TextView) dialog_view.findViewById(R.id.about_music_source);
        textWithLink.setMovementMethod(LinkMovementMethod.getInstance());

        textWithLink = (TextView) dialog_view.findViewById(R.id.about_music_licence);
        textWithLink.setMovementMethod(LinkMovementMethod.getInstance());
    }

    /*
     * See SymbolizeDialog::get_dialog_string_id
     */
    @Override
    protected String get_dialog_string_id() {
        return Page.Get_resource_string( R.string.about_options_dialog_id );
    }

    /*
     * See SymbolizeDialog::get_dialog_background_id
     */
    @Override
    protected int get_dialog_background_id() {
        return R.id.about_dialog;
    }

    /*
     * See SymbolizeDialog::get_dialog_id
     */
    @Override
    protected int get_dialog_id() {
        return R.layout.about_dialog;
    }
}
