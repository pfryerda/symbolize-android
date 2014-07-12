package symbolize.app.Common;

import java.util.ArrayList;

import symbolize.app.Common.Enum.Action;
import symbolize.app.Game.GameActivity;

public class Posn {
    // Static field
    //--------------

    public static final int DRAWINGTHRESHOLD = GameActivity.SCALING / 10;


    // Fields
    //-------

    private int first, second;


    // Constructors
    //--------------

    public Posn() {
        first = -1;
        second = -1;
    }

    public Posn( int x0, int y0 ) {
        first = x0;
        second = y0;
    }


    // Copy constructor
    //------------------

    public Posn clone() {
        return  new Posn( first, second );
    }


    // Public Methods
    //----------------

    /*
     * Method that translate a point via an x and y value
     *
     * @param int x: The value you wish to translate horizontally
     * @param int y: The value you wish to translate vertically
     */
    public void Translate( int x, int y ) {
        first += x;
        second += y;
    }

    /*
     * Method that sees if the given posn is approximately equal to this posn
     */
    public boolean Approximately_equals( final Posn point ) {
        return ( ( ( first - DRAWINGTHRESHOLD ) <= point.x() ) &&
                    ( point.x() <= ( first + DRAWINGTHRESHOLD ) ) &&
                 ( ( second - DRAWINGTHRESHOLD ) <= point.y() ) &&
                    ( point.y() <= ( second + DRAWINGTHRESHOLD ) ) );
    }

    /*
     * A lexigraphical less than
     *
     * @param Posn point: the point you are comparing against
     */
    public boolean Less_than( final Posn point ) {
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
    public int Distance_squared( final Posn point ) {
        return ( first - point.x() ) * ( first - point.x() ) +
                ( second - point.y() ) * ( second - point.y() );
    }

    /*
     * Scale point according to the GameActivity.Scaling
     */
    public void Scale() {
        first = Math.min( GameActivity.SCALING,
                Math.max( 0, Math.round( first * GameActivity.SCALING / GameActivity.SCREENSIZE.x ) ) );
        second = Math.min( GameActivity.SCALING,
                Math.max( 0, Math.round( second * GameActivity.SCALING / GameActivity.SCREENSIZE.x ) ) );
    }

    /*
     * Snap's point to nerest grid points
     */
    public void Snap() {
        first = first - ( first % ( GameActivity.SCALING / 10 ) );
        second = second - ( second % ( GameActivity.SCALING / 10 ) );
    }

    /*
     * Method used to snap posn to levels
     */
    public void Snap_to_levels( final ArrayList<Posn> levels) {
       if ( !levels.isEmpty() ) {
           Posn match = levels.get( 0 );
           for ( Posn point : levels ) {
               if ( Distance_squared( point ) < Distance_squared( match ) ) {
                   match = point;
               }
           }
           first = match.x();
           second = match.y();
       }
   }


    // Geter Methods
    //---------------

    public int x() {
        return first;
    }

    public int y() {
        return second;
    }


    // Action method
    //--------------

    public void Edit( final Action action ) {
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
     *
     * @param String tag: Whether to print p1, or p2
     */
    public String Print_posn( final String tag ) {
        int stringLength = ( GameActivity.SCALING + "" ).length();
        return "<" + tag + ">" + "<x>" +  String.format( "%1$" + stringLength + "s", first ) + "</x>"
                + "<y>" + String.format( "%1$" + stringLength + "s", second ) + "</y>" + "</" + tag + ">";
    }
}
