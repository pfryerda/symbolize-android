package symbolize.app.Dialog;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.TextView;

import org.w3c.dom.Text;

import symbolize.app.Common.Page;
import symbolize.app.Common.Session;
import symbolize.app.DataAccess.MetaDataAccess;
import symbolize.app.DataAccess.ProgressDataAccess;
import symbolize.app.DataAccess.UnlocksDataAccess;
import symbolize.app.Game.GamePage;
import symbolize.app.Home.HomePage;
import symbolize.app.Puzzle.PuzzleDB;
import symbolize.app.R;

public class DataOptionsDialog extends OptionDialog {
    // Static fields
    //---------------

    private static final int[] data_map = new int[7];
    private static final int[] level_complete_map = new int[7];
    private static final int[] world_complete_map = new int[7];


    // Static block
    //-------------

    static {
        data_map[0] = R.id.world_1_data;
        data_map[1] = R.id.world_2_data;
        data_map[2] = R.id.world_3_data;
        data_map[3] = R.id.world_4_data;
        data_map[4] = R.id.world_5_data;
        data_map[5] = R.id.world_6_data;
        data_map[6] = R.id.world_7_data;

        level_complete_map[0] = R.id.world_1_levels_complete;
        level_complete_map[1] = R.id.world_2_levels_complete;
        level_complete_map[2] = R.id.world_3_levels_complete;
        level_complete_map[3] = R.id.world_4_levels_complete;
        level_complete_map[4] = R.id.world_5_levels_complete;
        level_complete_map[5] = R.id.world_6_levels_complete;
        level_complete_map[6] = R.id.world_7_levels_complete;

        world_complete_map[0] = R.id.world_1_complete;
        world_complete_map[1] = R.id.world_2_complete;
        world_complete_map[2] = R.id.world_3_complete;
        world_complete_map[3] = R.id.world_4_complete;
        world_complete_map[4] = R.id.world_5_complete;
        world_complete_map[5] = R.id.world_6_complete;
        world_complete_map[6] = R.id.world_7_complete;
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

        dialog_view.findViewById( R.id.options_delete_data ).setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View view ) {
                final ConfirmDialog confirmDialog = new ConfirmDialog();
                confirmDialog.Set_attrs( getString( R.string.delete_all_data_title ), getString( R.string.delete_all_data_msg ) );
                confirmDialog.SetConfirmationListener( new ConfirmDialog.ConfirmDialogListener() {
                    @Override
                    public void OnDialogSuccess() {
                        UnlocksDataAccess.Get_instance().Remove_all_unlocks();
                        ProgressDataAccess.Get_instance().Remove_all_progress();
                        MetaDataAccess.Get_instance().Reset_meta_data_access();
                        Session.Get_instance().Reset();
                        startActivity( new Intent( GamePage.Get_context().getApplicationContext(), HomePage.class ) );
                    }

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

        for ( byte world = 1; world <= PuzzleDB.NUMBER_OF_WORLDS; ++world ) {
            if ( unlocks_dao.Is_unlocked( world ) ) {
                final TextView levels_complete = (TextView) dialog_view.findViewById( level_complete_map[world - 1] );
                levels_complete.append( progress_dao.Get_number_of_complete_levels_string( world ) );
                if ( progress_dao.Is_completed( world ) ) {
                    ( (CheckedTextView) dialog_view.findViewById( world_complete_map[world -1] ) ).setChecked( true );
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
     * See SymbolizeDialog::get_dialog_id
     */
    @Override
    protected int get_dialog_id() {
        return R.layout.data_options_dialog;
    }
}
