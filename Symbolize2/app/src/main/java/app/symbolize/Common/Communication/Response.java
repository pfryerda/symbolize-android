package app.symbolize.Common.Communication;

import app.symbolize.Common.Line;

/*
 * A wrapper of common types that is update after sending a request to report data back
 */

public class Response {
    // Fields
    //--------

    public Integer response_int;
    public Boolean response_boolean;
    public Line response_line;

    // Constructors
    //-------------

    public Response( ) {
        this.response_int = null;
        this.response_boolean = null;
        this.response_line = null;
    }
}
