package symbolize.app.Dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.SeekBar;
import symbolize.app.Common.Options;
import symbolize.app.Common.SymbolizeActivity;
import symbolize.app.Game.GameActivity;
import symbolize.app.R;

public class OptionsDialog extends SymbolizeDialog {
    // Fields
    //-------

    private OptionsDialogListener listener;


    // Interface setup
    //-------------------

    public interface OptionsDialogListener {
        public void OnDeleteAllData();
    }

    @Override
    public void onAttach( Activity activity ){
       super.onAttach( activity );
    }


    // Protected methods
    //-------------------

    @Override
    protected AlertDialog.Builder get_builder() {
        final AlertDialog.Builder builder = super.get_builder();

        builder.setNeutralButton( SymbolizeActivity.Get_resource_string( R.string.close ), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int id) {
                OptionsDialog.this.getDialog().dismiss();
            }
        } );

        return builder;
    }

    @Override
    protected View get_dialog_view() {
        final LayoutInflater inflater = getActivity().getLayoutInflater();
        final View dialog_view =  inflater.inflate( R.layout.options_dialog, null );

        final CheckedTextView show_graph = (CheckedTextView) dialog_view.findViewById( R.id.options_show_graph );
        show_graph.setChecked( Options.Show_grid() );
        show_graph.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick( View view ) {
                Options.Toggle_grid();
                show_graph.setChecked( !show_graph.isChecked() );
            }
        } );

        final CheckedTextView show_border = (CheckedTextView) dialog_view.findViewById( R.id.options_show_border );
        show_border.setChecked( Options.Show_border() );
        show_border.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick( View view ) {
                Options.Toggle_border();
                show_border.setChecked( !show_border.isChecked() );
            }
        } );

        final CheckedTextView snap_drawing = (CheckedTextView) dialog_view.findViewById( R.id.options_snap_drawing );
        snap_drawing.setChecked( Options.Is_snap_drawing() );
        snap_drawing.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View view ) {
                Options.Toggle_snap();
                snap_drawing.setChecked( !snap_drawing.isChecked() );
            }
        } );

        final CheckedTextView show_animation = (CheckedTextView) dialog_view.findViewById( R.id.options_show_animation );
        show_animation.setChecked( Options.Show_animations() );
        show_animation.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View view ) {
                Options.Toggle_animations();
                show_animation.setChecked( !show_animation.isChecked() );
            }
        } );

        Button delete_all_data_button = (Button) dialog_view.findViewById( R.id.options_delete_data );

        delete_all_data_button.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View view ) {
                listener.OnDeleteAllData();
            }
        } );

        final SeekBar volume_bar = (SeekBar) dialog_view.findViewById( R.id.options_seekbar );
        volume_bar.setProgress( Options.Get_volume() );

        volume_bar.setOnSeekBarChangeListener( new SeekBar.OnSeekBarChangeListener() {
            int progress_change = 0;
            @Override
            public void onProgressChanged( SeekBar seekBar, int progress, boolean fromUser ) {
                progress_change = progress;
            }

            @Override
            public void onStartTrackingTouch( SeekBar seekBar ) {}

            @Override
            public void onStopTrackingTouch( SeekBar seekBar ) {
                Options.Set_volume( progress_change );
            }
        } );

        return dialog_view;
    }

    @Override
    protected String get_dialog_id() {
        return SymbolizeActivity.Get_resource_string( R.string.options_dialog_id );
    }
}
