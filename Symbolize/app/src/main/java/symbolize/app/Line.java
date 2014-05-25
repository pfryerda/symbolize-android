package symbolize.app;

import java.lang.Math;
import android.graphics.Color;

import static symbolize.app.Constants.*;

public class Line {
    // Fields
    //--------

    private Posn p1, p2;
    private int color;
    private final Owner owner;


    // Constructors
    //--------------

    public Line() {
        p1 = null;
        p2 = null;
        color = Integer.parseInt(null);
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
        color = hue;
        owner = Owner.App;
    }

    public Line( Posn pt1, Posn pt2, Owner creator ) {
        if (pt1.lt(pt2)) {
            p1 = pt1;
            p2 = pt2;
        } else {
            p1 = pt2;
            p2 = pt1;
        }
        color = Color.BLACK;
        owner = creator;
    }

    public Line( Posn pt1, Posn pt2, int hue, Owner creator ) {
        if (pt1.lt(pt2)) {
            p1 = pt1;
            p2 = pt2;
        } else {
            p1 = pt2;
            p2 = pt1;
        }
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
     * Method that sees if the given line is approximately equal to this line (used during soluntion check)
     *  @param Line soln: Line from the solution that we are checking against
     */
    public boolean eq( Line soln ) {
        return ( (
                ( ( p1.x() - DRAWINGWIGGLEROOM ) <= soln.getP1().x() ) && ( soln.getP1().x() <= ( p1.x() + DRAWINGWIGGLEROOM ) )   &&
                ( ( p1.y() - DRAWINGWIGGLEROOM ) <= soln.getP1().y() ) && ( soln.getP1().y() <= ( p1.y() + DRAWINGWIGGLEROOM ) )   &&
                ( ( p2.x() - DRAWINGWIGGLEROOM ) <= soln.getP2().x() ) && ( soln.getP2().x() <= ( p2.x() + DRAWINGWIGGLEROOM ) )   &&
                ( ( p2.y() - DRAWINGWIGGLEROOM ) <= soln.getP2().y() ) && ( soln.getP2().y() <= ( p2.y() + DRAWINGWIGGLEROOM ) ) ) ||
                (
                ( ( p2.x() - DRAWINGWIGGLEROOM ) <= soln.getP1().x() ) && ( soln.getP1().x() <= ( p2.x() + DRAWINGWIGGLEROOM ) )   &&
                ( ( p2.y() - DRAWINGWIGGLEROOM ) <= soln.getP1().y() ) && ( soln.getP1().y() <= ( p2.y() + DRAWINGWIGGLEROOM ) )   &&
                ( ( p1.x() - DRAWINGWIGGLEROOM ) <= soln.getP2().x() ) && ( soln.getP2().x() <= ( p1.x() + DRAWINGWIGGLEROOM ) )   &&
                ( ( p1.y() - DRAWINGWIGGLEROOM ) <= soln.getP2().y() ) && ( soln.getP2().y() <= ( p1.y() + DRAWINGWIGGLEROOM ) ) ) &&
                ( color == soln.getColor() ) );
    }

    /*
     * Method to see if given point roughly lines on the line. This method is used for
     * erasing and colour changing to see if the user's finger is onto of the line.
     *
     * @param Posn point: point of interest
     */
    public boolean intersect( Posn point ) {
        int x0 = point.x();
        int y0 = point.y();
        int x1 = getP1().x();
        int y1 = getP1().y();
        int x2 = getP2().x();
        int y2 = getP2().y();
        int result = Math.abs( ( y0 - y1 )*( x2 - x1 ) - ( x0 - x1 ) * ( y2 - y1 ) );
        return ( result <= ERASERWIGGLEROOM ) && ( Math.min( x1, x2 ) <= x0 ) &&
                ( x0 <= Math.max( x1, x2 ) ) && ( Math.min( y1, y2 ) <= y0 ) && ( y0 <= Math.max( y1, y2 ) );
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
        int x0 = p1.x();
        int y0 = p1.y();
        p1.setX( SCALING - y0 );
        p1.setY(x0);

        x0 = p2.x();
        y0 = p2.y();
        p2.setX( SCALING - y0 );
        p2.setY(x0);
    }

    public void rotateLeft() {
        int x0 = p1.x();
        int y0 = p1.y();
        p1.setX( y0 );
        p1.setY( SCALING - x0 );

        x0 = p2.x();
        y0 = p2.y();
        p2.setX( y0 );
        p2.setY( SCALING - x0 );
    }

    public void flipH() {
        int x0 = p1.x();
        p1.setX( SCALING - x0 );

        x0 = p2.x();
        p2.setX( SCALING - x0 );
    }

    public void flipV() {
        int y0 = p1.y();
        p1.setY( SCALING - y0 );

        y0 = p2.y();
        p2.setY( SCALING - y0 );
    }

    public void editColor() {
        color = COLORARRAY.get( ( COLORMAP.get(color) + 1 ) % COLORARRAY.size() );
    }
}
