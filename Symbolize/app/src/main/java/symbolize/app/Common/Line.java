package symbolize.app.Common;

import java.lang.Math;
import java.util.ArrayList;
import java.util.Arrays;
import android.graphics.Color;
import android.util.SparseIntArray;

import symbolize.app.Game.GameView;

public class Line {
    // Flags
    //-------

    public static final int App = 0;
    public static final int User = 1;

    // Static Fields
    //---------------

    public static final int ERASINGTHRESHOLD = GameView.SCALING / 13;
    public static final ArrayList<Integer> COLORARRAY =
            new ArrayList( Arrays.asList(Color.BLACK, Color.RED, Color.YELLOW,
                    Color.GREEN, Color.BLUE, Color.CYAN, Color.MAGENTA) );
    public static final SparseIntArray COLORMAP = make_color_map();


    // Fields
    //--------

    private Posn p1, p2;
    private int color;
    private final int owner;


    // Constructors
    //--------------

    public Line() {
        p1 = null;
        p2 = null;
        color = 0;
        owner = 0;
    }

    public Line( final Posn pt1, final Posn pt2, final int creator ) {
        if ( pt1.Less_than( pt2 ) ) {
            p1 = pt1;
            p2 = pt2;
        } else {
            p1 = pt2;
            p2 = pt1;
        }

        color = Color.BLACK;
        owner = creator;
    }

    public Line( final Posn pt1, final Posn pt2, final int hue, final int creator ) {
        if ( pt1.Less_than( pt2 ) ) {
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
        return new Line( p1.clone(), p2.clone(), color, owner );
    }


    // Public methods
    //-----------------

    /*
     * Method that calculates euclidean distance squared
     */
    public int Distance_squared() {
        return p1.Distance_squared( p2 );
    }

    /*
     * Method that translate a line via an x and y value
     *
     * @param int x: The value you wish to translate horizontally
     * @param int y: The value you wish to translate vertically
     */
    public void Translate( int x, int y ) {
        p1.Translate( x, y );
        p2.Translate( x, y );
    }

    /*
     * Method that sees if the given line is approximately equal to this line (used during solution check)
     *
     * @param Line soln: Line from the solution that we are checking against
     */
    public boolean Approximately_equals( final Line soln ) {
        return ( ( p1.Approximately_equals( soln.Get_p1() ) && p2.Approximately_equals( soln.Get_p2() ) )   ||
                 ( p1.Approximately_equals( soln.Get_p2() ) && p2.Approximately_equals( soln.Get_p1() ) ) ) &&
               ( color == soln.Get_color() );
    }

    /*
     * Method to see if given point roughly lines on the line. This method is used for
     * erasing and colour changing to see if the user's finger is onto of the line.
     *
     * @param Posn point: point of interest
     */
    public boolean Intersects( final Posn point ) {
        if( Math.min( p1.x(), p2.x() ) - ERASINGTHRESHOLD <= point.x() &&
                point.x() <= Math.max( p1.x(), p2.x() ) + ERASINGTHRESHOLD &&
            Math.min( p1.y(), p2.y() ) - ERASINGTHRESHOLD <= point.y() &&
                point.y() <= Math.max( p1.y(), p2.y() ) + ERASINGTHRESHOLD )
        {
            if ( ( slope() != Float.POSITIVE_INFINITY ) && ( slope() != 0 ) ) {
                int x  = Math.round( ( point.y() - y_intercept() ) / slope() );
                int y = Math.round( slope() * point.x() + y_intercept() );
                return Math.abs( x - point.x() ) <= ERASINGTHRESHOLD ||
                        ( Math.abs( y - point.y() ) <= ERASINGTHRESHOLD );
            }
            return true;
        }
        return false;
    }

    /*
     * Method used to calculate how close two lines are. This method is used during soluntion
     * checking when there is multiple matching lines. This is used to decide which to use.
     *
     * @param Line line: The Line you are checking against.
     */
    public int Score( final Line line ) {
        return Math.min( p1.Distance_squared( line.Get_p1() ) + p2.Distance_squared( line.Get_p2() ),
                p2.Distance_squared( line.Get_p1() ) + p1.Distance_squared( line.Get_p2() ) );
    }

    /*
     * Snaps line to nearest grid points
     */
   public void Snap() {
       p1.Snap();
       p2.Snap();
   }

    /*
     * Method used to snap line to level dot
     *
     * @param ArrayList<Posn> levels: Array of lines wanting to snap to
     */
    public void Snap_to_levels( final ArrayList<Posn> levels ) {
        p1.Snap_to_levels( levels );
        p2.Snap_to_levels( levels );
    }


    // Geter methods
    //---------------

    public Posn Get_p1() {
        return p1;
    }

    public Posn Get_p2() {
        return p2;
    }

    public int Get_color() {
        return color;
    }

    public int Get_owner() {
        return owner;
    }


    // Action Method
    //----------------

    public void Edit( final int request_type ) {
        if ( request_type == Request.Change_color ) {
            color = COLORARRAY.get( ( COLORMAP.get(color) + 1 ) % COLORARRAY.size() );
        } else {
            p1.Edit( request_type );
            p2.Edit( request_type );
        }
    }


    // Private methods
    //----------------

    /*
     * Calculates the slope of the line
     */
    private float slope() {
        int dx = p2.x() - p1.x();
        int dy = p2.y() - p1.y();
        return ( dx == 0 ) ? Float.POSITIVE_INFINITY : (float) dy / dx;
    }

    /*
     * Calculates the y intercept of the line
     */
    private float y_intercept() {
        int dx = p2.x() - p1.x();
        return ( dx == 0 ) ? Float.POSITIVE_INFINITY : p1.y() - ( slope() * p1.x() );
    }


    // Static methods
    //---------------

    private static SparseIntArray make_color_map() {
        SparseIntArray color_map = new SparseIntArray();
        for ( int i = 0; i < COLORARRAY.size(); ++i ) {
            color_map.put( COLORARRAY.get(i), i );
        }
        return color_map;
    }


    // Developer method
    //-----------------

    /*
     * Method used to print the xml code to construct a line
     */
    public String Print_line() {
        return "<Line>" + p1.Print_posn( "p1" ) + p2.Print_posn( "p2" ) +
               "<color>" + String.format( "%1$9s", color ) + "</color>" + "</Line>";
    }
}
