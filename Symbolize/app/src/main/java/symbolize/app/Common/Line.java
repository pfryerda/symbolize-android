package symbolize.app.Common;

import java.lang.Math;
import java.util.ArrayList;
import java.util.Arrays;

import android.graphics.Color;
import android.util.SparseIntArray;

import symbolize.app.Common.Enum.Action;
import symbolize.app.Common.Enum.Owner;
import symbolize.app.Game.GameActivity;

public class Line {
    // Static Fields
    //---------------

    public static final int ERASINGTHRESHOLD = GameActivity.SCALING / 13;
    public static final ArrayList<Integer> COLORARRAY =
            new ArrayList( Arrays.asList(Color.BLACK, Color.RED, Color.YELLOW,
                    Color.GREEN, Color.BLUE, Color.CYAN, Color.MAGENTA) );
    public static final SparseIntArray COLORMAP = make_color_map();


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

    public Line( final Posn pt1, final Posn pt2, final Owner creator ) {
        if ( pt1.Less_than( pt2 ) ) {
            p1 = pt1;
            p2 = pt2;
        } else {
            p1 = pt2;
            p2 = pt1;
        }

        update_attributes();
        color = Color.BLACK;
        owner = creator;
    }

    public Line( final Posn pt1, final Posn pt2, final int hue, final Owner creator ) {
        if ( pt1.Less_than( pt2 ) ) {
            p1 = pt1;
            p2 = pt2;
        } else {
            p1 = pt2;
            p2 = pt1;
        }

        update_attributes();
        color = hue;
        owner = creator;
    }


    // Copy Consturctor
    //------------------

    public Line clone() {
        return new Line(p1.clone(), p2.clone(), color, owner);
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
                point.y() <= Math.max( p1.y(), p2.y() ) + ERASINGTHRESHOLD ) {
            if ( ( slope != Float.POSITIVE_INFINITY ) && ( slope != 0 ) ) {
                int x  = Math.round( ( point.y() - y_intercept ) /slope );
                int y = Math.round( slope * point.x() + y_intercept );
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

    public Owner Get_owner() {
        return owner;
    }


    // Action Method
    //----------------

    public void Edit( final Action action ) {
        if ( action == Action.Change_color ) {
            color = COLORARRAY.get( ( COLORMAP.get(color) + 1 ) % COLORARRAY.size() );
        } else {
            p1.Edit( action );
            p2.Edit( action );
        }
        update_attributes();
    }


    // Private method
    //----------------

    /*
     * Method used to calculate the slope and y intercept
     */
    private void update_attributes() {
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
