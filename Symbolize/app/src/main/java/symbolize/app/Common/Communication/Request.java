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

    public static final int Log                  = -1;
    public static final int Fetch_level          = 0;
    public static final int Check_correctness    = 1;
    public static final int Drag_start           = 2;
    public static final int Undo                 = 3;
    public static final int Drag_end             = 4;
    public static final int None                 = 5;
    public static final int Reset                = 6;
    public static final int Background_change    = 7;
    public static final int Shadow_point         = 8;
    public static final int Shadow_line          = 9;
    public static final int Change_color         = 10;
    public static final int Draw                 = 11;
    public static final int Erase                = 12;
    public static final int Rotate_right         = 13;
    public static final int Rotate_left          = 14;
    public static final int Flip_horizontally    = 15;
    public static final int Flip_vertically      = 16;
    public static final int Shift                = 17;
    public static final int Load_level_via_world = 18;
    public static final int Load_world_via_level = 19;
    public static final int Load_puzzle_left     = 20;
    public static final int Load_puzzle_right    = 21;
    public static final int Load_puzzle_start    = 22;


    // Fields
    //--------

    public int type;

    public Line request_line;
    public Posn request_point;


    // Constructors
    //-------------

    public Request( int type ) {
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
        return Undo <= type && type <= Load_puzzle_start;
    }

    /*
     * @return boolean: true if the request requires the model keep a backup for undo
     */
    public boolean Require_undo() {
        return ( Change_color <= type && type <= Shift ) || type == Drag_start;
    }

    /*
     * @return boolean: true if the request should perform the appropriate animation from
     * GameAnimationHandler before render
     */
    public boolean Is_animation_action() {
        return Rotate_right <= type && type <= Load_puzzle_right && OptionsDataAccess.Show_animations();
    }

    /*
     * @returns whether the request is not one of the flags defined above
     */
    public boolean Is_invalid_type() {
        return type < -1 || type > 22;
    }
}
