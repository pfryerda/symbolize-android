package symbolize.app;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import symbolize.app.Level;
import symbolize.app.Line;
import symbolize.app.Posn;

public class Puzzles {
    // Levels definition
    //--------------------

    private final ArrayList<Level> levels = new ArrayList( Arrays.asList(


        new Level(1, 1, "This is a test hint", 3000, 3000, true, true, true,
            new ArrayList( Arrays.asList(
                new LinkedList<Line>( Arrays.asList( new Line( new Posn( 100, 100), new Posn( 500, 500), -16777216 ), new Line( new Posn( 100, 900), new Posn( 500, 500), -16777216 ) ) ),
                new LinkedList<Line>( Arrays.asList( new Line( new Posn( 100, 100), new Posn( 500, 500), -16777216 ), new Line( new Posn( 100, 900), new Posn( 500, 500), -16777216 ), new Line( new Posn( 500, 500), new Posn( 900, 500), -16777216 ) ) )
            ) ),
            new LinkedList( Arrays.asList(
                new LinkedList<Line>( Arrays.asList( new Line( new Posn( 100, 100), new Posn( 500, 500), -16777216 ), new Line( new Posn( 100, 900), new Posn( 500, 500), -16777216 ), new Line( new Posn( 500, 500), new Posn( 900, 500), -16777216 ) ) )
            ) )
        )

    ) );


    // Script to copy levels into xml files
    //--------------------------------------

    public static void main( String []args ) {

    }

}