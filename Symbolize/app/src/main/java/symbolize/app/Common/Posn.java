package symbolize.app.Common;

import java.util.ArrayList;
import symbolize.app.Common.Communication.Request;
import symbolize.app.Game.GameUIView;
import symbolize.app.Game.GameView;

public class Posn {
    // Static field
    //--------------

    public static final int DRAWING_THRESHOLD = GameView.SCALING / 10;


    // Fields
    //-------

    private Integer first, second;


    // Constructors
    //--------------

    public Posn() {
        this( null, null );
    }

    public Posn( Integer x0, Integer y0 ) {
        first = x0;
        second = y0;
    }


    // Copy constructor
    //------------------

    public Posn clone() {
        return  new Posn( first, second );
    }


    // Getter Methods
    //---------------

    public int x() {
        return first;
    }

    public int y() {
        return second;
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
        return ( ( ( first - DRAWING_THRESHOLD ) <= point.x() ) &&
                    ( point.x() <= ( first + DRAWING_THRESHOLD ) ) &&
                 ( ( second - DRAWING_THRESHOLD ) <= point.y() ) &&
                    ( point.y() <= ( second + DRAWING_THRESHOLD ) ) );
    }

    /*
     * A lexicographical less than
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
        first = Math.min( GameView.SCALING,
                Math.max( 0, Math.round( first * GameView.SCALING / GameUIView.CANVAS_SIZE ) ) );
        second = Math.min( GameView.SCALING,
                Math.max( 0, Math.round( second * GameView.SCALING / GameUIView.CANVAS_SIZE ) ) );
    }

    /*
     * Snap's point to nearest grid points
     */
    public void Snap() {
        first = first - ( first % ( GameView.SCALING / 10 ) );
        second = second - ( second % ( GameView.SCALING / 10 ) );
    }

    /*
     * Method used to snap posn to levels
     */
    public void Snap_to_levels( final ArrayList<Posn> levels ) {
       if ( !levels.isEmpty() ) {
           Posn match = levels.get( 0 );
           for ( Posn point : levels ) {
               if ( point != null ) {
                   if ( Distance_squared(point) < Distance_squared( match ) ) {
                       match = point;
                   }
               }
           }
           first = match.x();
           second = match.y();
       }
   }

    /*
     * Generic method that edits the posn based off the request type
     *
     * @param int request_type: The type of request which determines how to edit the line
     */
    public void Edit( final int request_type ) {
        int tmp;
        switch ( request_type ) {
            case Request.Rotate_right:
                tmp = first;
                first = GameView.SCALING - second;
                second = tmp;
                break;

            case Request.Rotate_left:
                tmp = second;
                second = GameView.SCALING - first;
                first = tmp;
                break;

            case Request.Flip_horizontally:
                first = GameView.SCALING - first;
                break;

            case Request.Flip_vertically:
                second = GameView.SCALING - second;
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
        int stringLength = ( GameView.SCALING + "" ).length();
        return "<" + tag + ">" + "<x>" +  String.format( "%1$" + stringLength + "s", first ) + "</x>"
                + "<y>" + String.format( "%1$" + stringLength + "s", second ) + "</y>" + "</" + tag + ">";
    }
}
