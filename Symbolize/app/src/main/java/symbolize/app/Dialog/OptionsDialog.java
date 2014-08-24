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

public class OptionsDialog extends InfoDialog {
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
        final LayoutInflater inflater = getActivity().getLayoutInflater();
        final View dialog_view =  inflater.inflate( R.layout.options_dialog, null );

        final CheckedTextView show_graph = (CheckedTextView) dialog_view.findViewById( R.id.options_show_grid );
        show_graph.setChecked( OptionsDataAccess.Show_grid() );
        show_graph.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick( View view ) {
                OptionsDataAccess.Toggle_grid();
                show_graph.setChecked( !show_graph.isChecked() );
                GameController.Get_instance().Handle_request( new Request( Request.Background_change ), new Response());
            }
        } );

        final CheckedTextView show_border = (CheckedTextView) dialog_view.findViewById( R.id.options_show_border );
        show_border.setChecked( OptionsDataAccess.Show_border() );
        show_border.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View view ) {
                OptionsDataAccess.Toggle_border();
                show_border.setChecked( !show_border.isChecked() );
                GameController.Get_instance().Handle_request( new Request( Request.Background_change ), new Response() );
                GameUIView.Update_ui();
            }
        } );

        final CheckedTextView snap_drawing = (CheckedTextView) dialog_view.findViewById( R.id.options_snap_drawing );
        snap_drawing.setChecked( OptionsDataAccess.Is_snap_drawing() );
        snap_drawing.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View view ) {
                OptionsDataAccess.Toggle_snap();
                snap_drawing.setChecked( !snap_drawing.isChecked() );
            }
        } );

        final CheckedTextView show_animation = (CheckedTextView) dialog_view.findViewById( R.id.options_show_animation );
        show_animation.setChecked( OptionsDataAccess.Show_animations() );
        show_animation.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View view ) {
                OptionsDataAccess.Toggle_animations();
                show_animation.setChecked( !show_animation.isChecked() );
            }
        } );

        Button delete_all_data_button = (Button) dialog_view.findViewById( R.id.options_delete_data );

        delete_all_data_button.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View view ) {
                ConfirmDialog confirmDialog = new ConfirmDialog();
                confirmDialog.Set_attrs( getString( R.string.delete_all_data_title ), getString( R.string.delete_all_data_msg ) );
                confirmDialog.SetConfirmationListener( new ConfirmDialog.ConfirmDialogListener() {
                    @Override
                    public void OnDialogSuccess() {
                        UnlocksDataAccess.Remove_all_unlocks();
                        ProgressDataAccess.Remove_all_progress();
                        MetaDataAccess.Reset_meta_data_access();
                        startActivity( new Intent( GamePage.Get_context().getApplicationContext(), HomePage.class ) );
                    }

                    @Override
                    public void OnDialogFail() {}
                } );
                confirmDialog.Show();
            }
        } );

        final SeekBar volume_bar = (SeekBar) dialog_view.findViewById( R.id.options_seekbar );
        volume_bar.setProgress( OptionsDataAccess.Get_volume() );

        volume_bar.setOnSeekBarChangeListener( new SeekBar.OnSeekBarChangeListener() {
            byte progress_change = 0;
            @Override
            public void onProgressChanged( SeekBar seekBar, int progress, boolean fromUser ) {
                progress_change = (byte) progress;
            }

            @Override
            public void onStartTrackingTouch( SeekBar seekBar ) {}

            @Override
            public void onStopTrackingTouch( SeekBar seekBar ) {
                OptionsDataAccess.Set_volume(progress_change);
            }
        } );

        return dialog_view;
    }

    /*
     * See SymbolizeDialog::get_dialog_id
     */
    @Override
    protected String get_dialog_id() {
        return Page.Get_resource_string( R.string.options_dialog_id );
    }
}
