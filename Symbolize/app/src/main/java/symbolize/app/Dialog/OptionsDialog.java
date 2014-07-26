package symbolize.app.Dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;

import java.util.ArrayList;

import symbolize.app.Common.Enum.Action;
import symbolize.app.Common.Options;
import symbolize.app.Common.Player;
import symbolize.app.Common.Request;
import symbolize.app.Game.GameModel;
import symbolize.app.Puzzle.World;
import symbolize.app.R;

public class OptionsDialog extends SymbolizeDialog {
    // Constructors
    //-------------

    public OptionsDialog( final Activity activity, final Options options, final Player player ) {
        final AlertDialog.Builder builder = new AlertDialog.Builder( activity );
        final LayoutInflater inflater = activity.getLayoutInflater();

        final View dialog_view = inflater.inflate(R.layout.options_dialog, null);
        Button delete_all_data_button = (Button) dialog_view.findViewById( R.id.options_delete_data );
        SeekBar volume_bar = (SeekBar) dialog_view.findViewById( R.id.options_seekbar );

        volume_bar.setProgress( options.Get_volume() );

        final String[] options_text = { "Show grid", "Show border", "Snap" };

        final boolean[] selected_items = new boolean[options_text.length];
        selected_items[Options.SHOW_GRID] = options.Show_grid();
        selected_items[Options.SHOW_BORDER] = options.Show_border();
        selected_items[Options.SNAP_DRAWING] = options.Is_snap_drawing();

        builder.setView( dialog_view )
            .setMultiChoiceItems(options_text, selected_items,
                    new DialogInterface.OnMultiChoiceClickListener() {
                        @Override
                        public void onClick( DialogInterface dialog, int which,
                                             boolean isChecked)
                        {
                            switch ( which ) {
                                case Options.SHOW_GRID:
                                    options.Toggle_grid();
                                    break;
                                case Options.SHOW_BORDER:
                                    options.Toggle_border();
                                    break;
                                case Options.SNAP_DRAWING:
                                    options.Toggle_snap();
                                    break;
                            }
                        }
                    }
            )
            .setNeutralButton( "Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick( DialogInterface dialogInterface, int id ) {
                        Close_dialog();
                    }
                }
            );

        dialog = builder.create();

        delete_all_data_button.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View view ) {
                player.Delete_all_data();
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
                options.Set_volume( progress_change );
            }
        } );
    }

    public OptionsDialog( final Activity activity, final Options options, final Player player, final GameModel game_model ) {
        final AlertDialog.Builder builder = new AlertDialog.Builder( activity );
        final LayoutInflater inflater = activity.getLayoutInflater();

        final View dialog_view = inflater.inflate(R.layout.options_dialog, null);
        Button delete_all_data_button = (Button) dialog_view.findViewById( R.id.options_delete_data );
        SeekBar volume_bar = (SeekBar) dialog_view.findViewById( R.id.options_seekbar );

        volume_bar.setProgress( options.Get_volume() );

        final String[] options_text = { "Show grid", "Show border", "Snap" };

        final boolean[] selected_items = new boolean[options_text.length];
        selected_items[Options.SHOW_GRID] = options.Show_grid();
        selected_items[Options.SHOW_BORDER] = options.Show_border();
        selected_items[Options.SNAP_DRAWING] = options.Is_snap_drawing();

        builder.setView( dialog_view )
                .setMultiChoiceItems(options_text, selected_items,
                        new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick( DialogInterface dialog, int which,
                                                 boolean isChecked)
                            {
                                Request request;
                                switch ( which ) {
                                    case Options.SHOW_GRID:
                                        options.Toggle_grid();
                                        request = new Request( Action.Background_change );
                                        request.options = options;
                                        game_model.Handle_request( request );
                                        break;
                                    case Options.SHOW_BORDER:
                                        options.Toggle_border();
                                        request = new Request( Action.Background_change );
                                        request.options = options;
                                        game_model.Handle_request( request );
                                        break;
                                    case Options.SNAP_DRAWING:
                                        options.Toggle_snap();
                                        break;
                                }
                            }
                        }
                )
                .setNeutralButton( "Close", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick( DialogInterface dialogInterface, int id ) {
                                Close_dialog();
                            }
                        }
                );

        dialog = builder.create();

        delete_all_data_button.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View view ) {
                player.Delete_all_data();
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
                options.Set_volume( progress_change );
            }
        } );
    }

    // Private methods
    //-----------------

    private void Set_up() {
    }
}
