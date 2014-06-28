package symbolize.app.Common;

import java.util.ArrayList;

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
    public boolean eq( Posn point ) {
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
    public int distSqr( Posn point ) {
        return ( first - point.x() ) * ( first - point.x() ) +
                ( second - point.y() ) * ( second - point.y() );
    }

    /*
     * Method used to snap posn to levels
     */
   public void snapToLevels( ArrayList<Posn> levels) {
       if ( !levels.isEmpty() ) {
           Posn match = levels.get( 0 );
           for ( Posn point : levels ) {
               if ( distSqr( point ) < distSqr( match ) ) {
                   match = point;
               }
           }
           first = match.x();
           second = match.y();
       }
   }


    // Geter/Seter Methods
    //--------------------

    public int x() {
        return first;
    }

    public int y() {
        return second;
    }

    public void setX(int x) {
        first = x;
    }

    public void setY(int y) {
        second = y;
    }


    // Action methods
    //---------------

    public void roateRight() {
        int tmp = first;
        first = GameActivity.SCALING - second;
        second = tmp;
    }

    public void roateLeft() {
        int tmp = second;
        second = GameActivity.SCALING - first;
        first = tmp;
    }

    public void flipH() {
        first = GameActivity.SCALING - first;
    }

    public void flipV() {
        second = GameActivity.SCALING - second;
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
