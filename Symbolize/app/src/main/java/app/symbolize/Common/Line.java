package app.symbolize.Common;

import java.lang.Math;
import java.util.ArrayList;
import java.util.Arrays;

import android.graphics.Color;
import android.util.Log;
import android.util.SparseIntArray;
import app.symbolize.Common.Communication.Request;
import app.symbolize.Game.GameUIView;
import app.symbolize.Game.GameView;
import app.symbolize.R;

public class Line {
    // Flags
    //-------

    public static final int App_drawn = 0;
    public static final int User_drawn = 1;
    public static final int User_dragged = 2;


    // Constants
    //------------

    public static final short ERASING_THRESHOLD = GameView.SCALING / 16;
    public static final byte ANGLE_THRESHOLD_1 = 5; // Degrees


    // Fields
    //--------

    private Posn p1, p2;
    private Integer color;
    private int owner;


    // Constructors
    //--------------

    public Line() {
        this.p1 = null;
        this.p2 = null;
        this.color = null;
        this.owner = App_drawn;
    }

    public Line( final Posn pt1, final Posn pt2 ) {
        this( pt1, pt2, null, Line.App_drawn );
    }

    public Line( final Posn pt1, final Posn pt2, final int creator ) {
        this( pt1, pt2, null, creator );
    }

    public Line( final Posn pt1, final Posn pt2, final Integer hue, final int creator ) {
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

    public Integer Get_color() {
        return color;
    }

    public int Get_owner() {
        return owner;
    }


    // Setter methods
    //---------------

    public void Set_owner( final int owner ) { this.owner = owner; }


    // Public methods
    //-----------------

    /*
     * Calculate the midpoint of the line
     */
    public Posn Mid_point() {
        return new Posn( ( p1.x() + p2.x() ) / 2, ( p1.y() + p2.y() ) / 2 );
    }


    /*
     * Checks if line a dud
     */
    public boolean Is_dud() {
        return p1.Is_dud() || p2.Is_dud() || Distance_squared() <= 0;
    }

    /*
     * Generic method that edits a line based off the request type
     *
     * @param int request_type: The type of the request which determine how the line should edit its target
     */
    public void Edit( final int request_type ) {
        if ( request_type == Request.Change_color ) {
            color = GameUIView.Get_next_color( color );
        } else if ( request_type == Request.SPECIAL_SLOPE_ZERO ) {
            Posn mid_point = Mid_point();
            p1.Set_y( mid_point.y() );
            p2.Set_y( mid_point.y() );
        } else if ( request_type == Request.SPECIAL_SLOPE_INF ) {
            Posn mid_point = Mid_point();
            p1.Set_x( mid_point.x( ));
            p2.Set_x( mid_point.x() );
        } else if ( request_type == Request.SPECIAL_INVERT_SELF ) {
            final short temp = p1.y();
            p1.Set_y(p2.y());
            p2.Set_y( temp );
        } else {
            p1.Edit( request_type );
            p2.Edit( request_type );
        }
    }

    /*
     * Checks to see if two lines are so close that they could be merged
     *
     * @paran Line line_2: The line you are checking against
     * @return boolean: true if they are mergable, false otherwise
     */
    public boolean Mergeable( Line line_2 ) {
        if ( this == line_2 ) {
            return false;
        }

        if ( Intersects( line_2.Get_p1() ) || Intersects( line_2.Get_p2() ) || line_2.Intersects(p1) || line_2.Intersects( p2 ) ) {
            Posn intersecting_point = Get_intersecting_point( line_2 );

            if ( intersecting_point == null ) {
                return true;
            }

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
            for ( int i = 1; i < angles.length; ++i ) {
                if( angles[i] == angles[i] ) {  // if angles[i] != NaN
                    if ( angle != angle ) {     // if angle == NaN
                        angle = angles[i];
                    } else {
                        angle = Math.min( angle, angles[i] );
                    }
                }
            }

            return ( angle == angle ) && angle <= ANGLE_THRESHOLD_1; // angle is less than threshold and not NaN
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
    public void Translate( short x, short y ) {
        p1.Translate( x, y );
        p2.Translate( x, y );
    }

    /*
     * Method that see's if two lines have the same coords
     */
    public boolean Equals( final  Line line ) {
        return p1.Equals( line.Get_p1() ) && p2.Equals( line.Get_p2() );
    }

    /*
     * Method that sees if the given line is approximately equal to this line (used during solution check)
     *
     * @param Line soln: Line from the solution that we are checking against
     */
    public boolean Approximately_equals( final Line soln ) {
        return ( ( p1.Approximately_equals( soln.Get_p1() ) && p2.Approximately_equals( soln.Get_p2() ) )   ||
                 ( p1.Approximately_equals( soln.Get_p2() ) && p2.Approximately_equals( soln.Get_p1() ) ) ) &&
                 ( soln.Get_color() == null || color.equals( soln.Get_color() ) );
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
            if ( ( Slope() != Float.POSITIVE_INFINITY ) && ( Slope() != 0 ) ) {
                int x  = Math.round( ( point.y() - y_intercept() ) / Slope() );
                int y = Math.round( Slope() * point.x() + y_intercept() );
                return Math.abs( x - point.x() ) <= ERASING_THRESHOLD ||
                        ( Math.abs( y - point.y() ) <= ERASING_THRESHOLD );
            }
            return true;
        }
        return false;
    }

    /*
     * Treats the two line segments as lines and gets their intersecting point
     *
     * @param final Line line_2: The line you want to get the intersecting point with
     */
    public Posn Get_intersecting_point( final Line line_2 ) {
        final float m1 = Slope();
        final float m2 = line_2.Slope();

        final float x;
        final float y;

        if ( m1 == m2 ) {
            return null;
        } else if ( m1 == Float.POSITIVE_INFINITY ) {
            x = p1.x();
            y = m2 * x + line_2.y_intercept();
        } else if ( m2 == Float.POSITIVE_INFINITY ) {
            x = line_2.p1.x();
            y = m1 * x + y_intercept();
        } else {
            x = ( y_intercept() - line_2.y_intercept() ) / (  m2 - m1 );
            y = m1 * x + y_intercept();
        }
        return new Posn( x, y );
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
     * Method used to snap line to level dot
     *
     * @param ArrayList<Posn> levels: Array of lines wanting to snap to
     */
    public void Snap_to_levels( final ArrayList<Posn> levels ) {
        p1.Snap_to_levels( levels );
        p2.Snap_to_levels( levels );
    }

    /*
     * Calculates the slope of the line
     */
    public float Slope() {
        int dx = p2.x() - p1.x();
        int dy = p2.y() - p1.y();
        return ( dx == 0 ) ? Float.POSITIVE_INFINITY : (float) dy / dx;
    }


    // Private methods
    //----------------

    /*
     * Calculates the y intercept of the line
     */
    private float y_intercept() {
        int dx = p2.x() - p1.x();
        return ( dx == 0 ) ? Float.POSITIVE_INFINITY : p1.y() - ( Slope() * p1.x() );
    }

    // Developer methods
    //------------------

    /*
     * Method used to print the xml code to construct a line
     */
    public String Print_line() {
        String color_text = ( ( color == null ) ? "" : "<color>" + String.format( "%1$9s", color ) + "</color>" ) + "</Line>";
        return "<Line>" + p1.Print_posn( "p1" ) + p2.Print_posn( "p2" ) + color_text;
    }
}
