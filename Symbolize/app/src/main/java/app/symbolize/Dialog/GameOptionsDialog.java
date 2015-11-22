package app.symbolize.Dialog;

import android.view.View;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import app.symbolize.Common.Communication.Request;
import app.symbolize.Common.Communication.Response;
import app.symbolize.DataAccess.OptionsDataAccess;
import app.symbolize.Routing.Page;
import app.symbolize.Game.GameController;
import app.symbolize.Game.GameUIView;
import app.symbolize.R;

public class GameOptionsDialog extends OptionDialog {
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

        final CheckBox show_graph = (CheckBox) dialog_view.findViewById( R.id.options_show_grid );
        show_graph.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View view ) {
                options_dao.Toggle_boolean_option( OptionsDataAccess.OPTION_GRID );
                if ( Page.Is_Game_page() ) {
                    GameController.Get_instance().Handle_request( new Request( Request.Background_change ),
                                                                  new Response());
                }
            }
        });

        final CheckBox show_border = (CheckBox) dialog_view.findViewById( R.id.options_show_border );
        show_border.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View view ) {
                options_dao.Toggle_boolean_option( OptionsDataAccess.OPTION_BORDER );
                if ( Page.Is_Game_page() ) {
                    GameController.Get_instance().Handle_request( new Request( Request.Background_change ),
                                                                  new Response() );
                    GameUIView.Update_ui( null );
                }
            }
        } );

        final CheckBox snap_drawing = (CheckBox) dialog_view.findViewById( R.id.options_snap_drawing );
        snap_drawing.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View view ) {
                options_dao.Toggle_boolean_option( OptionsDataAccess.OPTION_SNAP_DRAWING );
            }
        } );

        dialog_view.findViewById( R.id.options_reset_to_default ).setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final ConfirmDialog confirmDialog = new ConfirmDialog();
                confirmDialog.Set_Button_Text( Page.Get_resource_string( R.string.revert ), Page.Get_resource_string( R.string.cancel ) );
                confirmDialog.Set_attrs( getString( R.string.revert_to_default_title ), getString( R.string.revert_to_default_message ) );
                confirmDialog.SetConfirmationListener( new ConfirmDialog.ConfirmDialogListener() {
                    @Override
                    public void OnDialogSuccess() {
                        options_dao.Reset_game_options();
                        init_dialog_view( dialog_view );
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
        final OptionsDataAccess options_dao = OptionsDataAccess.Get_instance();

        ( (CheckBox) dialog_view.findViewById( R.id.options_show_grid ) )
                .setChecked( options_dao.Get_boolean_option( OptionsDataAccess.OPTION_GRID ) );

        ( (CheckBox) dialog_view.findViewById( R.id.options_show_border ) )
                .setChecked( options_dao.Get_boolean_option( OptionsDataAccess.OPTION_BORDER ) );

        ( (CheckBox) dialog_view.findViewById( R.id.options_snap_drawing ) )
                .setChecked( options_dao.Get_boolean_option( OptionsDataAccess.OPTION_SNAP_DRAWING ) );
    }

    /*
     * See SymbolizeDialog::get_dialog_string_id
     */
    @Override
    protected String get_dialog_string_id() {
        return Page.Get_resource_string( R.string.game_options_dialog_id );
    }

    /*
     * See SymbolizeDialog::get_dialog_background_id
     */
    @Override
    protected int get_dialog_background_id() {
        return R.id.game_options_dialog;
    }

    /*
     * See SymbolizeDialog::get_dialog_id
     */
    @Override
    protected int get_dialog_id() {
        return R.layout.game_options_dialog;
    }
}
