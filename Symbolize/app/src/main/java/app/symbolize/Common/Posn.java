package app.symbolize.Common;

import java.util.ArrayList;
import app.symbolize.Common.Communication.Request;
import app.symbolize.Game.GameUIView;
import app.symbolize.Game.GameView;

public class Posn {
    // Constants
    //-----------

    public static final short DRAWING_THRESHOLD = GameView.SCALING / 8;


    // Fields
    //-------

    private Short first, second;


    // Constructors
    //--------------

    public Posn() {
        this( (Short) null, null );
    }

    public Posn( Short x0, Short y0 ) {
        first = x0;
        second = y0;
    }

    public Posn( Integer x0, Integer y0 ) {
        this( (short) (int) x0, (short) (int) y0 );
    }

    public Posn( Float x0, Float y0 ) {
        this( (short) Math.round( x0 ), (short) Math.round( y0 ) );
    }


    // Copy constructor
    //------------------

    public Posn clone() {
        return  new Posn( first, second );
    }


    // Getter Methods
    //---------------

    public short x() {
        return first;
    }

    public short y() {
        return second;
    }


    // Setter methods
    //---------------

    public void Set_x( short x ) { first = x; }

    public void Set_y( short y ) { second = y; }


    // Public Methods
    //----------------

    /*
     * Checks if posn is a dud i.e. invalid
     */
    public boolean Is_dud() {
        return first < 0 || second < 0 || first > GameView.SCALING || second > GameView.SCALING;
    }

    /*
     * Method that translate a point via an x and y value
     *
     * @param int x: The value you wish to translate horizontally
     * @param int y: The value you wish to translate vertically
     */
    public void Translate( short x, short y ) {
        first = (short) ( first + x );
        second = (short) ( second + y );
    }

    /*
     * Method that see's if two posn have the same coords
     */
    public boolean Equals( final Posn posn ) {
        return first == posn.x() && second == posn.y();
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
     * Add two points
     *
     * @param Posn point_2: Point you are adding
     * @return Posn: The resulting added point
     */
    public Posn Add( final Posn posn_2 ) {
        return new Posn( first + posn_2.first, second + posn_2.second );
    }

    /*
     * Subtract two points
     *
     * @param Posn point_2: Point you are subtracting
     * @return Posn: The resulting subtracted point
     */
    public Posn Subtract( final Posn posn_2 ) {
        return new Posn( first - posn_2.first, second - posn_2.second );
    }

    /*
     * Mult a point by a scalar
     *
     * @param Posn scalar: Scalar value
     * @return Posn: The resulting multiplied point
     */
    public Posn Mult( final int scalar ) {
        return new Posn( first * scalar, second * scalar );
    }

    /*
     * Treat the posn's as vectors and calculate the dot product of the two points
     *
     * @param Posn vector: the 'vector' you are dot producting against
     * @return int: The dot product
     */
    public int Dot( final Posn vector ) {
        return first * vector.first + second * vector.second;
    }

    /*
     * Treates the posn's as vectors and gets the angle between them
     *
     * @param Posn vector: The 'vector' you are getting the angle with
     * @return double: The angle in degrees
     */
    public double Angle( final Posn vector ) {
        return ( Math.toDegrees( Math.atan(
                ( (float) ( first * vector.second - second * vector.first ) /
                        Dot( vector )  ) ) ) + 360 ) % 360;
    }

    /*
     * Euclidean distance squared
     *
     */
    public int Distance_squared( final Posn point ) {
        return ( first - point.x() ) * ( first - point.x() ) +
                ( second - point.y() ) * ( second - point.y() );
    }

    public int Distance_squared( final Line line ) {
        /*final int line_length_squared = line.Distance_squared();
        if(line_length_squared == 0 ) return Distance_squared(line.Get_p1());
        final int t = Subtract( line.Get_p1() ).Dot( line.Get_p2().Subtract( line.Get_p1() ) ) / line_length_squared;
        if(t < 0) return Distance_squared( line.Get_p1() );
        if(t > 1) return Distance_squared( line.Get_p2() );
        final Posn projection = line.Get_p1().Add( line.Get_p2().Subtract( line.Get_p1() ).Mult( t ) );
        return Distance_squared( projection );*/
        final int px = line.Get_p2().x() - line.Get_p1().x();
        final int py = line.Get_p2().y() - line.Get_p1().y();
        final int line_length_squared = px*px + py*py;

        float u = (float) ((x() - line.Get_p1().x()) * px + (y() - line.Get_p1().y()) * py) / line_length_squared;

        if ( u > 1 )      u = 1;
        else if ( u < 0 ) u = 0;

        final float x = line.Get_p1().x() + u * px;
        final float y = line.Get_p1().y() + u * py;

        final float dx = x - x();
        final float dy = y - y();

        return Math.round( dx*dx + dy*dy );
    }

    /*
     * Scale point according to the GameActivity.Scaling
     */
    public void Scale() {
        first = (short) Math.min( GameView.SCALING,
                Math.max( 0, Math.round( (first - GameUIView.CANVAS_MARGIN ) * ( (float) GameView.SCALING / GameUIView.CANVAS_SIZE ) ) ) );
        second = (short) Math.min( GameView.SCALING,
                Math.max( 0, Math.round( ( second - GameUIView.BAR_HEIGHT - GameUIView.CANVAS_MARGIN ) * ( (float) GameView.SCALING / GameUIView.CANVAS_SIZE ) ) ) );
    }

    /*
     * Return an unscaled version of the point according to the GameActivity.Scaling
     */
    public Posn Unscale() {
        return new Posn( first * ( (float) GameUIView.CANVAS_SIZE / GameView.SCALING ) + GameUIView.CANVAS_MARGIN,
                         second * ( (float) GameUIView.CANVAS_SIZE /  GameView.SCALING ) + GameUIView.BAR_HEIGHT + GameUIView.CANVAS_MARGIN );
    }

    /*
     * Snap's point to nearest grid points
     */
    public Posn Snap( boolean round ) {
        short scale_factor = GameView.SCALING / 10;
        if ( round ) {
            return new Posn( Math.round( (float) first / scale_factor ) * scale_factor,
                             Math.round( (float) second / scale_factor ) * scale_factor );
        } else {
            return new Posn( first - ( first % scale_factor ),
                             second - ( second % scale_factor ) );
        }
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
           if( Distance_squared( match ) > DRAWING_THRESHOLD * DRAWING_THRESHOLD ) {
               first = -1 * GameView.SCALING;
               second = -1 * GameView.SCALING;
           } else {
               first = match.x();
               second = match.y();
           }
       }
   }

    /*
     * Generic method that edits the posn based off the request type
     *
     * @param int request_type: The type of request which determines how to edit the line
     */
    public void Edit( final int request_type ) {
        short tmp;
        switch ( request_type ) {
            case Request.Rotate_right:
                tmp = first;
                first = (short) ( GameView.SCALING - second );
                second = tmp;
                break;

            case Request.Rotate_left:
                tmp = second;
                second = (short) ( GameView.SCALING - first );
                first = tmp;
                break;

            case Request.Flip_horizontally:
                first = (short) ( GameView.SCALING - first );
                break;

            case Request.Flip_vertically:
                second = (short) ( GameView.SCALING - second );
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
