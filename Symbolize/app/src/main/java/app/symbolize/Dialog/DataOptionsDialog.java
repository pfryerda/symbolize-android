package app.symbolize.Dialog;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.ScrollView;
import android.widget.TextView;

import app.symbolize.Routing.Page;
import app.symbolize.Common.Session;
import app.symbolize.DataAccess.MetaDataAccess;
import app.symbolize.DataAccess.ProgressDataAccess;
import app.symbolize.DataAccess.UnlocksDataAccess;
import app.symbolize.Game.GamePage;
import app.symbolize.Home.HomePage;
import app.symbolize.Puzzle.PuzzleDB;
import app.symbolize.R;
import app.symbolize.Routing.Router;

public class DataOptionsDialog extends OptionDialog {
    // Static fields
    //---------------

    private static final int[] data_map = new int[5];
    private static final int[] level_complete_map = new int[5];
    private static final int[] world_complete_map = new int[5];


    // Static block
    //-------------

    static {
        data_map[0] = R.id.world_1_data;
        data_map[1] = R.id.world_2_data;
        data_map[2] = R.id.world_3_data;
        data_map[3] = R.id.world_4_data;
        data_map[4] = R.id.world_5_data;

        level_complete_map[0] = R.id.world_1_levels_complete;
        level_complete_map[1] = R.id.world_2_levels_complete;
        level_complete_map[2] = R.id.world_3_levels_complete;
        level_complete_map[3] = R.id.world_4_levels_complete;
        level_complete_map[4] = R.id.world_5_levels_complete;

        world_complete_map[0] = R.id.world_1_complete;
        world_complete_map[1] = R.id.world_2_complete;
        world_complete_map[2] = R.id.world_3_complete;
        world_complete_map[3] = R.id.world_4_complete;
        world_complete_map[4] = R.id.world_5_complete;
    }

    // Inherited fields
    //------------------

    /*
    protected String title;
    protected String message;
    */


    // Protected methods
    //-------------------

    /*
     * See SymbolizeDialog::get_dialog_view
     */
    @Override
    protected View get_dialog_view() {
        final View dialog_view = super.get_dialog_view();

        dialog_view.findViewById( R.id.options_reset_to_default ).setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View view ) {
                final ConfirmDialog confirmDialog = new ConfirmDialog();
                confirmDialog.Set_Button_Text( Page.Get_resource_string( R.string.delete ), Page.Get_resource_string( R.string.cancel ) );
                confirmDialog.Set_attrs( getString( R.string.delete_all_data_title ), getString( R.string.delete_all_data_msg ) );
                confirmDialog.SetConfirmationListener( new ConfirmDialog.ConfirmDialogListener() {
                    @Override
                    public void OnDialogSuccess() {
                        UnlocksDataAccess.Get_instance().Remove_all_unlocks();
                        ProgressDataAccess.Get_instance().Remove_all_progress();
                        MetaDataAccess.Get_instance().Reset_meta_data_access();
                        Session.Get_instance().Reset();
                        Context context =
                                ( Page.Is_Game_page() ) ? GamePage.Get_context() : HomePage.Get_context();
                        Router.Direct_route( context, HomePage.class );
                    }

                    @Override
                    public void onDialogNeutral() {}

                    @Override
                    public void OnDialogFail() {}
                } );
                confirmDialog.Show();
            }
        } );

        return dialog_view;
    }

    /*
     * See SymbolizeDialog::init_dialog_view
     */
    @Override
    protected void init_dialog_view( final View dialog_view ) {
        final UnlocksDataAccess unlocks_dao = UnlocksDataAccess.Get_instance();
        final ProgressDataAccess progress_dao = ProgressDataAccess.Get_instance();

        // Edit seekbar glow (assume android doesn't change the name of their drawables....)
        final int glowDrawableId = Page.Get_context().getResources().getIdentifier( "overscroll_glow", "drawable", "android" );
        final Drawable androidGlow = Page.Get_context().getResources().getDrawable( glowDrawableId );
        final int edgeDrawableId = Page.Get_context().getResources().getIdentifier("overscroll_edge", "drawable", "android");
        final Drawable androidEdge = Page.Get_context().getResources().getDrawable(edgeDrawableId);
        if( androidGlow != null && androidEdge != null ) {
            androidGlow.setColorFilter( Session.Get_instance().Get_hightlight_color(), PorterDuff.Mode.SRC_IN );
            androidEdge.setColorFilter( Session.Get_instance().Get_hightlight_color(), PorterDuff.Mode.SRC_IN );
        } else {
            dialog_view.findViewById( R.id.data_scrollview ).setOverScrollMode( View.OVER_SCROLL_NEVER );
        }

        for ( byte world = 1; world <= PuzzleDB.NUMBER_OF_WORLDS; ++world ) {
            if ( unlocks_dao.Is_unlocked( world ) ) {
                final TextView levels_complete = (TextView) dialog_view.findViewById( level_complete_map[world - 1] );
                levels_complete.append( progress_dao.Get_number_of_complete_levels_string( world ) );
                if ( progress_dao.Is_completed( world ) && progress_dao.Get_number_of_completed_levels( world ) >= PuzzleDB.NUMBER_OF_LEVELS_PER_WORLD ) {
                    ( (CheckBox) dialog_view.findViewById( world_complete_map[world -1] ) ).setChecked( true );
                }
            } else {
                dialog_view.findViewById( data_map[world -1] ).setVisibility( View.GONE );
            }
        }
    }

    /*
     * See SymbolizeDialog::get_dialog_string_id
     */
    @Override
    protected String get_dialog_string_id() {
        return Page.Get_resource_string( R.string.data_options_dialog_id );
    }

    /*
     * See SymbolizeDialog::get_dialog_background_id
     */
    @Override
    protected int get_dialog_background_id() {
        return R.id.data_options_dialog;
    }

    /*
     * See SymbolizeDialog::get_dialog_id
     */
    @Override
    protected int get_dialog_id() {
        return R.layout.data_options_dialog;
    }
}
