package symbolize.app.Common;

import java.lang.Math;
import java.util.ArrayList;
import java.util.Arrays;

import android.graphics.Color;
import android.util.Log;
import android.util.SparseIntArray;

import symbolize.app.Game.GameActivity;

public class Line {
    // Static Fields
    //---------------

    public static final int DRAWINGTHRESHOLD = GameActivity.SCALING / 7;
    public static final int ERASINGTHRESHOLD = GameActivity.SCALING / 13;
    public static final ArrayList<Integer> COLORARRAY = new ArrayList( Arrays.asList(Color.BLACK, Color.RED, Color.YELLOW, Color.GREEN, Color.BLUE, Color.CYAN, Color.MAGENTA) );
    public static final SparseIntArray COLORMAP = makeColorMap();


    // Fields
    //--------

    private Posn p1, p2;
    private int color;
    private final Owner owner;
    private float slope;
    private float y_intercept;


    // Constructors
    //--------------

    public Line() {
        p1 = null;
        p2 = null;
        slope = Float.POSITIVE_INFINITY;
        y_intercept = Float.POSITIVE_INFINITY;
        color = 0;
        owner = null;
    }

    public Line( Posn pt1, Posn pt2 ) {
        if ( pt1.lt( pt2 ) ) {
            p1 = pt1;
            p2 = pt2;
        } else {
            p1 = pt2;
            p2 = pt1;
        }

        updateSlopeAndYIntercept();
        color = Color.BLACK;
        owner = Owner.App;
    }

    public Line( Posn pt1, Posn pt2, int hue ) {
        if ( pt1.lt( pt2 ) ) {
            p1 = pt1;
            p2 = pt2;
        } else {
            p1 = pt2;
            p2 = pt1;
        }

        updateSlopeAndYIntercept();
        color = hue;
        owner = Owner.App;
    }

    public Line( Posn pt1, Posn pt2, Owner creator ) {
        if ( pt1.lt( pt2 ) ) {
            p1 = pt1;
            p2 = pt2;
        } else {
            p1 = pt2;
            p2 = pt1;
        }

        updateSlopeAndYIntercept();
        color = Color.BLACK;
        owner = creator;
    }

    public Line( Posn pt1, Posn pt2, int hue, Owner creator ) {
        if ( pt1.lt( pt2 ) ) {
            p1 = pt1;
            p2 = pt2;
        } else {
            p1 = pt2;
            p2 = pt1;
        }

        updateSlopeAndYIntercept();
        color = hue;
        owner = creator;
    }


    // Copy Consturctor
    //------------------

    public Line clone() {
        return new Line(p1.clone(), p2.clone(), color, owner);
    }


    // Methods
    //----------


    /*
     * Method used to caluclate the slope and y intercept
     */
    public void updateSlopeAndYIntercept() {
        int dx = p2.x() - p1.x();
        int dy = p2.y() - p1.y();
        if( dx == 0 ) {
            slope = Float.POSITIVE_INFINITY;
            y_intercept = Float.POSITIVE_INFINITY;
        } else {
            slope = (float) dy / dx;
            y_intercept = p1.y() - ( slope * p1.x() );
        }
    }

    /*
     * Method that calculates euclidean distance squared
     */
    public int distSqr() {
        return p1.distSqr( p2 );
    }

    /*
     * Method that sees if the given line is approximately equal to this line (used during soluntion check)
     * @param Line soln: Line from the solution that we are checking against
     */
    public boolean eq( Line soln ) {
        return ( ( (
                ( ( p1.x() - DRAWINGTHRESHOLD ) <= soln.getP1().x() ) && ( soln.getP1().x() <= ( p1.x() + DRAWINGTHRESHOLD ) )     &&
                ( ( p1.y() - DRAWINGTHRESHOLD ) <= soln.getP1().y() ) && ( soln.getP1().y() <= ( p1.y() + DRAWINGTHRESHOLD ) )     &&
                ( ( p2.x() - DRAWINGTHRESHOLD ) <= soln.getP2().x() ) && ( soln.getP2().x() <= ( p2.x() + DRAWINGTHRESHOLD ) )     &&
                ( ( p2.y() - DRAWINGTHRESHOLD ) <= soln.getP2().y() ) && ( soln.getP2().y() <= ( p2.y() + DRAWINGTHRESHOLD ) ) )   ||
                (
                ( ( p2.x() - DRAWINGTHRESHOLD ) <= soln.getP1().x() ) && ( soln.getP1().x() <= ( p2.x() + DRAWINGTHRESHOLD ) )     &&
                ( ( p2.y() - DRAWINGTHRESHOLD ) <= soln.getP1().y() ) && ( soln.getP1().y() <= ( p2.y() + DRAWINGTHRESHOLD ) )     &&
                ( ( p1.x() - DRAWINGTHRESHOLD ) <= soln.getP2().x() ) && ( soln.getP2().x() <= ( p1.x() + DRAWINGTHRESHOLD ) )     &&
                ( ( p1.y() - DRAWINGTHRESHOLD ) <= soln.getP2().y() ) && ( soln.getP2().y() <= ( p1.y() + DRAWINGTHRESHOLD ) ) ) ) &&
                ( color == soln.getColor() ) );
    }

    /*
     * Method to see if given point roughly lines on the line. This method is used for
     * erasing and colour changing to see if the user's finger is onto of the line.
     *
     * @param Posn point: point of interest
     */
    public boolean intersect( Posn point ) {
        if( Math.min( p1.x(), p2.x() ) - ERASINGTHRESHOLD <= point.x() && point.x() <= Math.max( p1.x(), p2.x() ) + ERASINGTHRESHOLD &&
            Math.min( p1.y(), p2.y() ) - ERASINGTHRESHOLD <= point.y() && point.y() <= Math.max( p1.y(), p2.y() ) + ERASINGTHRESHOLD ) {
            if ( ( slope != Float.POSITIVE_INFINITY ) && ( slope != 0 ) ) {
                int x  = Math.round( ( point.y() - y_intercept ) /slope );
                int y = Math.round( slope * point.x() + y_intercept );
                return Math.abs( x - point.x() ) <= ERASINGTHRESHOLD ||( Math.abs( y - point.y() ) <= ERASINGTHRESHOLD );
            }
            return true;
        }
        return false;
    }

    /*
     * Method used to calculate how close two lines are. This method is used during soluntion
     * checking when there is multiple matching lines. This is used to decide which to use.
     * @param Line line: The Line you are checking against.
     */
    public int score(Line line) {
        return Math.min( p1.distSqr( line.getP1() ) + p2.distSqr( line.getP2() ),
                p2.distSqr( line.getP1() ) + p1.distSqr( line.getP2() ) );
    }

    /*
     * Method used to snap line to level dot
     */
    public void snapToLevels( ArrayList<Posn> levels ) {
        p1.snapToLevels( levels );
        p2.snapToLevels( levels );
    }


    // Geter methods
    //---------------

    public Posn getP1() {
        return p1;
    }

    public Posn getP2() {
        return p2;
    }

    public int getColor() {
        return color;
    }

    public Owner getOwner() {
        return owner;
    }


    // Action Methods
    //------------------

    public void rotateRight() {
        p1.roateRight();
        p2.roateRight();

        updateSlopeAndYIntercept();
    }

    public void rotateLeft() {
        p1.roateLeft();
        p2.roateLeft();

        updateSlopeAndYIntercept();
    }

    public void flipH() {
        p1.flipH();
        p2.flipH();

        updateSlopeAndYIntercept();
    }

    public void flipV() {
        p1.flipV();
        p2.flipV();

        updateSlopeAndYIntercept();
    }

    public void editColor() {
        color = COLORARRAY.get( ( COLORMAP.get(color) + 1 ) % COLORARRAY.size() );
    }


    // Developer method
    //-----------------

    /*
     * Method used to print the xml code to consturct a line
     */
    public String printLine() {
        return "<Line>" + p1.printPosn( "p1" ) + p2.printPosn( "p2" ) +
               "<color>" + String.format( "%1$9s", color ) + "</color>" + "</Line>";
    }


    // Static methods
    //---------------

    public final static SparseIntArray makeColorMap() {
        SparseIntArray colormap = new SparseIntArray();
        for ( int i = 0; i < COLORARRAY.size(); ++i ) {
            colormap.put( COLORARRAY.get(i), i );
        }
        return colormap;
    }
}
