package symbolize.app.Common;

import java.lang.Math;
import java.util.ArrayList;
import android.graphics.Color;
import android.util.SparseIntArray;
import symbolize.app.Common.Communication.Request;
import symbolize.app.Game.GameView;

public class Line {
    // Flags
    //-------

    public static final int App = 0;
    public static final int User = 1;


    // Static Fields
    //---------------

    public static final int ERASING_THRESHOLD = GameView.SCALING / 13;
    public static final int ANGLE_THRESHOLD_1 = 10;
    public static final int ANGLE_THRESHOLD_2 = 5;
    public static final ArrayList<Integer> COLOR_ARRAY;
    public static final SparseIntArray COLOR_MAP;


    // Static block
    //--------------

    static {
        ArrayList<Integer> color_array = new ArrayList<Integer>();
        color_array.add( Color.BLACK );
        color_array.add( Color.RED );
        color_array.add( Color.YELLOW );
        color_array.add( Color.GREEN );
        color_array.add( Color.BLUE );
        color_array.add( Color.CYAN );
        color_array.add( Color.MAGENTA );
        COLOR_ARRAY = color_array;

        SparseIntArray color_map = new SparseIntArray();
        for ( int i = 0; i < COLOR_ARRAY.size(); ++i ) {
            color_map.put( COLOR_ARRAY.get(i), i );
        }
        COLOR_MAP = color_map;
    }


    // Fields
    //--------

    private Posn p1, p2;
    private Integer color;
    private final int owner;


    // Constructors
    //--------------

    public Line() {
        this.p1 = null;
        this.p2 = null;
        this.color = Color.BLACK;
        this.owner = Line.App;
    }

    public Line( final Posn pt1, final Posn pt2 ) {
        this( pt1, pt2, Color.BLACK, Line.App );
    }

    public Line( final Posn pt1, final Posn pt2, final int creator ) {
        this( pt1, pt2, Color.BLACK, creator );
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


    // Copy Constructor
    //------------------

    public Line clone() {
        return new Line( p1.clone(), p2.clone(), color, owner );
    }


    // Getter methods
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


    // Public methods
    //-----------------

    /*
     * Generic method that edits a line based off the request type
     *
     * @param int request_type: The type of the request which determine how the line should edit its target
     */
    public void Edit( final int request_type ) {
        if ( request_type == Request.Change_color ) {
            color = COLOR_ARRAY.get( ( COLOR_MAP.get( color ) + 1 ) % COLOR_ARRAY.size() );
        } else if ( request_type == Request.SPECIAL_SLOPE_ZERO ) {
            Posn mid_point = mid_point();
            p1.Set_y( mid_point.y() );
            p2.Set_y( mid_point.y() );
        } else if ( request_type == Request.SPECIAL_SLOPE_INF ) {
            Posn mid_point = mid_point();
            p1.Set_x( mid_point.x() );
            p2.Set_x( mid_point.x() );
        } else {
            p1.Edit( request_type );
            p2.Edit( request_type );
        }
    }

    /*
     * Checks to see if two lines are so close that they could be merged
     *
     * @paran Line line_2: The line you are checking against
     * @return boolean: true if they are mergable, flase otherwise
     */
    public boolean Mergeable( Line line_2 ) {
        if ( this == line_2 ) {
            return false;
        }

        Posn intersecting_point = Get_intersecting_point( line_2 );
        if ( intersecting_point != null ) {
            Posn vector_1 = p2.Subtract( intersecting_point );
            Posn vector_2 = line_2.p2.Subtract( intersecting_point );
            Posn vector_3 = p1.Subtract( intersecting_point );
            Posn vector_4 = line_2.p1.Subtract( intersecting_point );

            double[] angles = new double[4];
            angles[0] = vector_1.Angle( vector_2 );
            angles[1] = vector_3.Angle( vector_4 );
            angles[2] = vector_1.Angle( vector_4 );
            angles[3] = vector_2.Angle( vector_3 );

            double angle = angles[0];
            //boolean is_x_shaped = ( angle - ANGLE_THRESHOLD_2 ) <= 90 && 90 <= ( angle + ANGLE_THRESHOLD_2 );
            for ( int i = 1; i < angles.length; ++i ) {
                if( angles[i] == angles[i] ) {  // if angles[i] != NaN
                    //is_x_shaped = is_x_shaped && ( angles[i] - ANGLE_THRESHOLD_2 ) <= 90 && 90 <= ( angles[i] + ANGLE_THRESHOLD_2 );
                    if ( angle != angle ) {     // if angle == NaN
                        angle = angles[i];
                    } else {
                        angle = Math.min( angle, angles[i] );
                    }
                } else {
                    //is_x_shaped = false;
                }
            }

            return angle <= ANGLE_THRESHOLD_1; /*||
                    ( ( angle - ANGLE_THRESHOLD_2 ) <= 90 && 90 <= ( angle + ANGLE_THRESHOLD_2 ) && !is_x_shaped ) ;*/
        }
        return false;
    }

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
        if( Math.min( p1.x(), p2.x() ) - ERASING_THRESHOLD <= point.x() &&
                point.x() <= Math.max( p1.x(), p2.x() ) + ERASING_THRESHOLD &&
            Math.min( p1.y(), p2.y() ) - ERASING_THRESHOLD <= point.y() &&
                point.y() <= Math.max( p1.y(), p2.y() ) + ERASING_THRESHOLD )
        {
            if ( ( slope() != Float.POSITIVE_INFINITY ) && ( slope() != 0 ) ) {
                int x  = Math.round( ( point.y() - y_intercept() ) / slope() );
                int y = Math.round( slope() * point.x() + y_intercept() );
                return Math.abs( x - point.x() ) <= ERASING_THRESHOLD ||
                        ( Math.abs( y - point.y() ) <= ERASING_THRESHOLD );
            }
            return true;
        }
        return false;
    }

    /*
     * Method to get the intersecting point of two lines
     *
     * @param Line line_2: The other line in question
     * @return Posn: The intersecting point
     */
    public Posn Get_intersecting_point( final Line line_2 ) {
        Float x;
        Float y;

        float m1 = slope();
        float m2 = line_2.slope();

        if( m1 == Float.POSITIVE_INFINITY ) {
            if ( Math.min( line_2.p1.x(), line_2.p2.x() ) <= p1.x() && p1.x() <= Math.max( line_2.p1.x(), line_2.p2.x() ) ) {
                x = (float) p1.x();
                y = m2 * x + line_2.y_intercept();
                return new Posn( x, y );
            }
        } else if( m2 == Float.POSITIVE_INFINITY ) {
            if ( Math.min( p1.x(), p2.x() ) <= line_2.p1.x() && line_2.p1.x() <= Math.max( p1.x(), p2.x() ) ) {
                x = (float) line_2.p1.x();
                y = m1 * x + y_intercept();
                return new Posn( x, y );
            }
        } else if ( m1 != m2 ) {
            x = ( line_2.y_intercept() - y_intercept() ) / ( m1 - m2 );
            y = m1 * x + y_intercept();

            if ( Math.min( p1.x(), p2.x() ) <= x && x <= Math.max( p1.x(), p2.x() ) &&
                 Math.min( p1.y(), p2.y() ) <= y && y <= Math.max( p1.y(), p2.y() ) &&
                 Math.min( line_2.p1.x(), line_2.p2.x() ) <= x && x <= Math.max( line_2.p1.x(), line_2.p2.x() ) &&
                 Math.min( line_2.p1.y(), line_2.p2.y() ) <= y && y <= Math.max( line_2.p1.x(), line_2.p2.x() ) )
            {
                return new Posn( x, y );
            }
        }

        if( Intersects( line_2.Get_p1() ) ) {
            return line_2.Get_p1();
        } else if ( Intersects( line_2.Get_p2() ) ) {
            return line_2.Get_p2();
        } else if ( line_2.Intersects( p1 ) ) {
            return p1;
        } else if( line_2.Intersects( p2 ) ) {
            return p2;
        }

        return null;
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

    /*
     * Calculate the midpoint of the line
     */
    private Posn mid_point() {
        return new Posn( ( p1.x() + p2.x() ) / 2, ( p1.y() + p2.y() ) / 2 );
    }


    // Developer methods
    //------------------

    /*
     * Method used to print the xml code to construct a line
     */
    public String Print_line() {
        return "<Line>" + p1.Print_posn( "p1" ) + p2.Print_posn( "p2" ) +
               "<color>" + String.format( "%1$9s", color ) + "</color>" + "</Line>";
    }
}
