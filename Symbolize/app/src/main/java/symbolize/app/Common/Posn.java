package symbolize.app.Common;

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
    public int distSqr(Posn point) {
        return ( first - point.x() ) * ( first - point.x() ) +
                ( second - point.y() ) * ( second - point.y() );
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
