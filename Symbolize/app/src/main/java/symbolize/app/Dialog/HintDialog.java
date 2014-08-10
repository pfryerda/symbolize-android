package symbolize.app.Dialog;

import android.content.Context;

import symbolize.app.Common.Player;
import symbolize.app.Common.Page;
import symbolize.app.Puzzle.Puzzle;
import symbolize.app.R;

public class HintDialog extends InfoDialog {

    // Public method
    //---------------

    public void Set_attrs( Puzzle puzzle ) {
        Player player = Player.Get_instance();
        Context context = Page.Get_context();

        String message = puzzle.Get_hint();
        message += "\n" + context.getString( R.string.draws_allowed ) + puzzle.Get_draw_restriction();
        message += "\n" + context.getString( R.string.drags_allowed ) + puzzle.Get_drag_restriction();
        message += "\n" + context.getString( R.string.erase_allowed ) + puzzle.Get_erase_restriction();
        message += "\n" + context.getString( R.string.mechanics_allowed );
        if ( puzzle.Can_rotate() ) {
            message += "\n" + context.getString( R.string.rotate );
        }
        if( puzzle.Can_flip() ) {
            message += "\n" + context.getString( R.string.flip );
        }
        if( puzzle.Can_shift() ) {
            message += "\n" + context.getString( R.string.shift );
        }
        if( puzzle.Can_change_color() ) {
            message += "\n" + context.getString( R.string.change_color );
        }

        super.Set_attrs( player.Get_current_puzzle_text(), message );
    }
}
