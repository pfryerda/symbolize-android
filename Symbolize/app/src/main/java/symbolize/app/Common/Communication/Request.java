package symbolize.app.Common.Communication;

import symbolize.app.Common.Line;
import symbolize.app.Common.Posn;
import symbolize.app.DataAccess.OptionsDataAccess;

/*
 * A wrapper of common types that are sent along with a request, along with a request type
 */
public class Request {
    // Flags
    //-------

    public static final byte Log                  = -1;
    public static final byte Fetch_level          = 0;
    public static final byte Check_correctness    = 1;
    public static final byte Drag_start           = 2;
    public static final byte Undo                 = 3;
    public static final byte Drag_end             = 4;
    public static final byte None                 = 5;
    public static final byte Reset                = 6;
    public static final byte Background_change    = 7;
    public static final byte Shadow_point         = 8;
    public static final byte Shadow_line          = 9;
    public static final byte Change_color         = 10;
    public static final byte Draw                 = 11;
    public static final byte Erase                = 12;
    public static final byte Rotate_right         = 13;
    public static final byte Rotate_left          = 14;
    public static final byte Flip_horizontally    = 15;
    public static final byte Flip_vertically      = 16;
    public static final byte Shift                = 17;
    public static final byte Load_level_via_world = 18;
    public static final byte Load_world_via_level = 19;
    public static final byte Load_puzzle_left     = 20;
    public static final byte Load_puzzle_right    = 21;
    public static final byte Load_puzzle_start    = 22;

    public static final byte SPECIAL_NONE         = 100;
    public static final byte SPECIAL_SLOPE_ZERO   = 101;
    public static final byte SPECIAL_SLOPE_INF    = 102;


    // Static methods
    //----------------

    /*
     * Given a Level.SPECIAL_TYPE returns the corresponding Request.SPECIAL_TYPE
     *
     * @param int level_special_type: The Level.SPECIAL_TYPE
     * @return the Request.SPECIAL_TYPE
     */
    public static byte Get_request_type_from_special( byte level_special_type ) {
        return (byte) ( level_special_type + 100 );
    }


    // Fields
    //--------

    public byte type;

    public Line request_line;
    public Posn request_point;


    // Constructors
    //-------------

    public Request( byte type ) {
        this.type = type;

        this.request_line = null;
        this.request_point = null;
    }


    // Public Methods
    //---------------

    /*
     * @return boolean: true if the request requires an render after updating the model
     */
    public boolean Require_render() {
        return ( Undo <= type && type <= Load_puzzle_start )
            || ( SPECIAL_NONE <= type && type <= SPECIAL_SLOPE_INF );
    }

    /*
     * @return boolean: true if the request requires the model keep a backup for undo
     */
    public boolean Require_undo() {
        return ( Change_color <= type && type <= Shift )
            || ( SPECIAL_SLOPE_ZERO <= type && type <= SPECIAL_SLOPE_INF )
            || type == Drag_start;
    }

    /*
     * @return boolean: true if require a cleanup of the board before rendering
     */
    public boolean require_pre_render() {
        return Rotate_right <= type && type <= Load_puzzle_right
            && OptionsDataAccess.Get_instance().Get_boolean_option( OptionsDataAccess.OPTION_SHOW_ANIMATIONS );
    }

    /*
     * @return boolean: true if the request should perform the appropriate animation from
     * GameAnimationHandler before render
     */
    public boolean Is_animation_action() {
        return Rotate_right <= type && type <= Load_puzzle_start
            && OptionsDataAccess.Get_instance().Get_boolean_option( OptionsDataAccess.OPTION_SHOW_ANIMATIONS );
    }

    /*
     * @returns whether the request is not one of the flags defined above
     */
    public boolean Is_invalid_type() {
        return type < -1 || ( type > 22 && type < 100 ) || type > 102;
    }
}
