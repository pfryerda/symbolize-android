package symbolize.app.Dialog;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.SeekBar;
import symbolize.app.Common.Communication.Request;
import symbolize.app.Common.Communication.Response;
import symbolize.app.DataAccess.MetaDataAccess;
import symbolize.app.DataAccess.OptionsDataAccess;
import symbolize.app.Common.Page;
import symbolize.app.DataAccess.ProgressDataAccess;
import symbolize.app.DataAccess.UnlocksDataAccess;
import symbolize.app.Game.GameController;
import symbolize.app.Game.GamePage;
import symbolize.app.Game.GameUIView;
import symbolize.app.Home.HomePage;
import symbolize.app.R;

public class GameOptionsDialog extends InfoDialog {
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
        final OptionsDataAccess options_dao = OptionsDataAccess.Get_instance();

        final CheckedTextView show_graph = (CheckedTextView) dialog_view.findViewById( R.id.options_show_grid );
        show_graph.setChecked( options_dao.Get_boolean_option( OptionsDataAccess.OPTION_GRID ) );
        show_graph.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick( View view ) {
                options_dao.Toggle_boolean_option( OptionsDataAccess.OPTION_GRID );
                show_graph.setChecked( !show_graph.isChecked() );
                GameController.Get_instance().Handle_request( new Request( Request.Background_change ), new Response());
            }
        } );

        final CheckedTextView show_border = (CheckedTextView) dialog_view.findViewById( R.id.options_show_border );
        show_border.setChecked( options_dao.Get_boolean_option( OptionsDataAccess.OPTION_BORDER ) );
        show_border.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View view ) {
                options_dao.Toggle_boolean_option( OptionsDataAccess.OPTION_BORDER );
                show_border.setChecked( !show_border.isChecked() );
                GameController.Get_instance().Handle_request( new Request( Request.Background_change ), new Response() );
                GameUIView.Update_ui( null );
            }
        } );

        final CheckedTextView snap_drawing = (CheckedTextView) dialog_view.findViewById( R.id.options_snap_drawing );
        snap_drawing.setChecked( options_dao.Get_boolean_option( OptionsDataAccess.OPTION_SNAP_DRAWING ) );
        snap_drawing.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View view ) {
                options_dao.Toggle_boolean_option( OptionsDataAccess.OPTION_SNAP_DRAWING );
                snap_drawing.setChecked( !snap_drawing.isChecked() );
            }
        } );

        return dialog_view;
    }

    /*
     * See SymbolizeDialog::get_dialog_string_id
     */
    @Override
    protected String get_dialog_string_id() {
        return Page.Get_resource_string( R.string.game_options_dialog_id );
    }

    /*
     * See SymbolizeDialog::get_dialog_id
     */
    @Override
    protected int get_dialog_id() {
        return R.layout.game_options_dialog;
    }
}
