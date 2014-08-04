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
        public void OnToggleGrid();
        public void OnToggleBorder();
        public void OnToggleSnap();
        public void OnDeleteAllData();
        public void OnEditVolume( int volume );
    }

    @Override
    public void onAttach( Activity activity ){
       super.onAttach( activity );
       listener = ( OptionsDialogListener ) activity;
    }


    // Main method
    //-------------

    @Override
    public Dialog onCreateDialog( Bundle save_instance_state ) {
        final AlertDialog.Builder builder = new AlertDialog.Builder( getActivity() );
        final LayoutInflater inflater = getActivity().getLayoutInflater();
        final View dialog_view = inflater.inflate(R.layout.options_dialog, null);

        Button delete_all_data_button = (Button) dialog_view.findViewById( R.id.options_delete_data );

        SeekBar volume_bar = (SeekBar) dialog_view.findViewById( R.id.options_seekbar );
        volume_bar.setProgress( Options.Get_volume() );

        final String[] options_text =  GameActivity.Get_context().getResources().getStringArray( R.array.multi_choice_options );
        boolean[] selected_items = new boolean[options_text.length];
        selected_items[Options.SHOW_GRID]    = Options.Show_grid();
        selected_items[Options.SHOW_BORDER]  = Options.Show_border();
        selected_items[Options.SNAP_DRAWING] = Options.Is_snap_drawing();

        builder.setView( dialog_view )
                .setMultiChoiceItems(options_text, selected_items,
                        new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick( DialogInterface dialog, int which,
                                                 boolean isChecked)
                            {
                                switch ( which ) {
                                    case Options.SHOW_GRID:
                                        listener.OnToggleGrid();
                                        break;
                                    case Options.SHOW_BORDER:
                                        listener.OnToggleBorder();
                                        break;
                                    case Options.SNAP_DRAWING:
                                        listener.OnToggleSnap();
                                        break;
                                }
                            }
                        }
                )
                .setNeutralButton( "Close", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick( DialogInterface dialogInterface, int id ) {
                                OptionsDialog.this.getDialog().dismiss();
                            }
                        }
                );

        delete_all_data_button.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View view ) {
                listener.OnDeleteAllData();
            }
        } );

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
                listener.OnEditVolume( progress_change );
            }
        } );

        return builder.create();
    }


    // Protected methods
    //-------------------

    protected String Get_dialog_id() {
        return SymbolizeActivity.Get_resource_string( R.string.options_dialog_id );
    }


}
