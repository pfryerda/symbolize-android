package symbolize.app.Common;

import java.util.ArrayList;

import symbolize.app.Common.Enum.Action;
import symbolize.app.Game.GameActivity;

public class Posn {
    // Fields
    //-------

    private int first, second;


    // Constructors
    //--------------

    public Posn() {
        first = -1;
        second = -1;
    }

    public Posn(int x0, int y0) {
        first = x0;
        second = y0;
    }


    // Copy constructor
    //------------------

    public Posn clone() {
        return  new Posn(first, second);
    }


    // Methods
    //---------

    /*
     * Method that sees if the given posn is approximately equal to this posn
     */
    public boolean Approximately_equals( Posn point ) {
        return ( ( ( first - Line.DRAWINGTHRESHOLD ) <= point.x() ) && ( point.x() <= ( first + Line.DRAWINGTHRESHOLD ) ) &&
                 ( ( second - Line.DRAWINGTHRESHOLD ) <= point.y() ) && ( point.y() <= ( second + Line.DRAWINGTHRESHOLD ) ) );
    }

    /*
     * A lexigraphical less than
     *
     * @param Posn point: the point you are comparing against
     */
    public boolean lt( Posn point ) {
        if (first != point.x()) {
            return first < point.x();
        } else {
            return second < point.y();
        }
    }

    /*
     * Euclidean distance squared
     *
     * @param Posn point: the point you getting the distance with
     */
    public int distance_squared( Posn point ) {
        return ( first - point.x() ) * ( first - point.x() ) +
                ( second - point.y() ) * ( second - point.y() );
    }

    /*
     * Method used to snap posn to levels
     */
   public void Snap_to_levels( ArrayList<Posn> levels) {
       if ( !levels.isEmpty() ) {
           Posn match = levels.get( 0 );
           for ( Posn point : levels ) {
               if ( distance_squared( point ) < distance_squared( match ) ) {
                   match = point;
               }
           }
           first = match.x();
           second = match.y();
       }
   }


    // Geter Methods
    //--------------------

    public int x() {
        return first;
    }

    public int y() {
        return second;
    }


    // Action method
    //--------------

    public void Edit( Action action ) {
        int tmp;
        switch ( action ) {
            case Rotate_right:
                tmp = first;
                first = GameActivity.SCALING - second;
                second = tmp;
                break;

            case Rotate_left:
                tmp = second;
                second = GameActivity.SCALING - first;
                first = tmp;
                break;

            case Flip_horizontally:
                first = GameActivity.SCALING - first;
                break;

            case Flip_vertically:
                second = GameActivity.SCALING - second;
                break;
        }
    }


    // Developer Method
    //------------------

    /*
     * Method used to print the xml code to construct this Posn
     */
    public String printPosn( String tag ) {
        int stringLength = ( GameActivity.SCALING + "" ).length();
        return "<" + tag + ">" + "<x>" +  String.format( "%1$" + stringLength + "s", first ) + "</x>" + "<y>" + String.format( "%1$" + stringLength + "s", second ) + "</y>" + "</" + tag + ">";
    }
}
