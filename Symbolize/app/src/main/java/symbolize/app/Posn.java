package symbolize.app;

public class Posn {
    // Fields
    //-------

    private int first, second;


    // Constructor
    //-------------

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
}
