package symbolize.app.Puzzle;

import android.content.res.Resources;
import android.content.res.XmlResourceParser;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;

import symbolize.app.Common.Enum.Owner;
import symbolize.app.Common.Line;
import symbolize.app.Common.Posn;
import symbolize.app.R;

/*
 * Expcetion class for xml parsing specific exceptions
 */
class InvalidXmlException extends Exception {
    // Constructors
    //--------------

    public InvalidXmlException( final int linenum, final int world_num, final int level_num, final String expected, final String actual ) {
        super( "Error in xml for level_" + world_num + "_" + level_num + ".xml::Line:" + linenum + ". Expected: <" + expected + "> Actual: " + ( ( actual == null ) ? "Not a Tag!" : "<" + actual + ">" ) );
    }

    public InvalidXmlException( final int linenum, final int world_num, final int level_num, final String invalid_text, final boolean isTag ) {
        super( "Error in xml for level_" + world_num + "_" + level_num + ".xml::Line:" + linenum + ". Given an unexpected " + ( ( isTag ) ? "tag: <" + invalid_text + ">" : "text:" + invalid_text ) );
    }
}


/*
 * Class used to parse stored xml files and load them into memory
 */
public class PuzzleDB {
    // Static field
    //--------------

    public static final int NUMBEROFWORLDS = 7;
    public static final int NUMBEROFLEVELSPERWORLD = 15;
    public static final int[] XMLMAP = new int[]{ R.xml.world_1, R.xml.level_1_1, R.xml.level_1_2, R.xml.level_1_3,
                                                  R.xml.level_1_4, R.xml.level_1_5, R.xml.level_1_6, R.xml.level_1_7,
                                                  R.xml.level_1_8, R.xml.level_1_9, R.xml.level_1_10, R.xml.level_1_11,
                                                  R.xml.level_1_12, R.xml.level_1_13, R.xml.level_1_14, R.xml.level_1_15,
                                                  R.xml.world_2, R.xml.level_2_1, R.xml.level_2_2, R.xml.level_2_3,
                                                  R.xml.level_2_4, R.xml.level_2_5, R.xml.level_2_6, R.xml.level_2_7,
                                                  R.xml.level_2_8, R.xml.level_2_9, R.xml.level_2_10, R.xml.level_2_11,
                                                  R.xml.level_2_12, R.xml.level_2_13, R.xml.level_2_14, R.xml.level_2_15,
                                                  R.xml.world_3, R.xml.level_3_1, R.xml.level_3_2, R.xml.level_3_3,
                                                  R.xml.level_3_4, R.xml.level_3_5, R.xml.level_3_6, R.xml.level_3_7,
                                                  R.xml.level_3_8, R.xml.level_3_9, R.xml.level_3_10, R.xml.level_3_11,
                                                  R.xml.level_3_12, R.xml.level_3_13, R.xml.level_3_14, R.xml.level_3_15,
                                                  R.xml.world_4, R.xml.level_4_1, R.xml.level_4_2, R.xml.level_4_3,
                                                  R.xml.level_4_4, R.xml.level_4_5, R.xml.level_4_6, R.xml.level_4_7,
                                                  R.xml.level_4_8, R.xml.level_4_9, R.xml.level_4_10, R.xml.level_4_11,
                                                  R.xml.level_4_12, R.xml.level_4_13, R.xml.level_4_14, R.xml.level_4_15,
                                                  R.xml.world_5, R.xml.level_5_1, R.xml.level_5_2, R.xml.level_5_3,
                                                  R.xml.level_5_4, R.xml.level_5_5, R.xml.level_5_6, R.xml.level_5_7,
                                                  R.xml.level_5_8, R.xml.level_5_9, R.xml.level_5_10, R.xml.level_5_11,
                                                  R.xml.level_5_12, R.xml.level_5_13, R.xml.level_5_14, R.xml.level_5_15,
                                                  R.xml.world_6, R.xml.level_6_1, R.xml.level_6_2, R.xml.level_6_3,
                                                  R.xml.level_6_4, R.xml.level_6_5, R.xml.level_6_6, R.xml.level_6_7,
                                                  R.xml.level_6_8, R.xml.level_6_9, R.xml.level_6_10, R.xml.level_6_11,
                                                  R.xml.level_6_12, R.xml.level_6_13, R.xml.level_6_14, R.xml.level_6_15,
                                                  R.xml.world_7, R.xml.level_1_1, R.xml.level_1_2, R.xml.level_1_3,
                                                  R.xml.level_7_4, R.xml.level_7_5, R.xml.level_7_6, R.xml.level_7_7,
                                                  R.xml.level_7_8, R.xml.level_7_9, R.xml.level_7_10 };


    // Fields
    //--------
    private final Resources res;
    private int world_num;
    private int level_num;
    private XmlResourceParser xpp;



    // Constructor
    //-------------

    public PuzzleDB( final Resources res ) {
        this.res = res;
    }

    // Methods
    //---------

    /*
     * Method used to get level from xml resource files
     */
    public Level Fetch_level( final int world_num, final int level_num ) {
        // Set up temp fields
        this.world_num = world_num;
        this.level_num = level_num;
        this.xpp = res.getXml( XMLMAP[( NUMBEROFLEVELSPERWORLD + 1 ) * ( world_num - 1 ) + level_num] );

        // Set up level variables
        String hint = "";
        int draw_restriction = 0;
        int erase_restriction = 0;
        int drag_restriction = 0;
        boolean rotate_enabled = false;
        boolean flip_enabled = false;
        boolean colour_enabled = false;
        ArrayList<LinkedList<Line>> boards = new ArrayList<LinkedList<Line>>( new LinkedList<LinkedList<Line>>() );
        ArrayList<LinkedList<Line>> solutions =  new ArrayList<LinkedList<Line>>( new LinkedList<LinkedList<Line>>() );

        try {
            // Set up temp variables
            ArrayList<LinkedList<Line>> tmpArray =  null;
            LinkedList<Line> tmpList = null;
            Line tmpLine = null;
            Posn tmpP1 = null;
            Posn tmpP2 = null;
            Integer tmpColor = null;
            Integer tmpX = null;
            Integer tmpY = null;

            // Manually parse preamble
            xpp.next();
            xpp.next();
            if ( !xpp.getName().equals( "Level" ) ) {
                bail_invalid_check( "Level" );
            }

            hint = parse_preamble( "hint" );
            draw_restriction = Integer.parseInt( parse_preamble("draw_restriction").trim());
            erase_restriction = Integer.parseInt( parse_preamble( "erase_restriction" ).trim() );
            drag_restriction = Integer.parseInt( parse_preamble( "drag_restriction" ).trim() );
            rotate_enabled = Boolean.valueOf( parse_preamble("rotate_enabled"));
            flip_enabled = Boolean.valueOf( parse_preamble( "flip_enabled" ) );
            colour_enabled = Boolean.valueOf( parse_preamble( "colour_enabled" ) );


            // Parse boarsd and solutions
            for ( int eventType = xpp.getEventType(); eventType != XmlResourceParser.END_DOCUMENT; eventType = xpp.next() ){

                switch ( eventType ) {

                    case ( XmlPullParser.START_TAG ): {
                        String topTag = xpp.getName();
                        if ( topTag.equals( "boards" ) || topTag.equals( "solutions" ) || topTag.equals( "graph" ) ||  topTag.equals( "Line" ) ||  topTag.equals( "p1" ) || topTag.equals( "p2" )  ) {
                            if ( topTag.equals( "boards" ) || topTag.equals( "solutions" ) ) {
                                if ( tmpArray != null || tmpList != null || tmpLine != null || tmpP1 != null || tmpP2 != null || tmpColor != null || tmpX != null || tmpY != null ) {
                                    bail_invalid_tag( topTag );
                                }
                                tmpArray = new ArrayList<LinkedList<Line>>();
                            } else if ( topTag.equals( "graph" ) ) {
                                if ( tmpArray == null || tmpList != null || tmpLine != null || tmpP1 != null || tmpP2 != null || tmpColor != null || tmpX != null || tmpY != null ) {
                                    bail_invalid_tag( topTag );
                                }
                                tmpList = new LinkedList<Line>();
                            } else if ( topTag.equals( "Line" ) ) {
                                if ( tmpArray == null || tmpList == null || tmpLine != null || tmpP1 != null || tmpP2 != null || tmpColor != null || tmpX != null || tmpY != null ) {
                                    bail_invalid_tag( topTag );
                                }
                                tmpLine = new Line();
                            } else if ( topTag.equals( "p1" ) ) {
                                if ( tmpArray == null || tmpList == null || tmpLine == null || tmpP1 != null || tmpX != null || tmpY != null ) {
                                    bail_invalid_tag( topTag );
                                }
                                tmpP1 = new Posn();
                            } else if ( topTag.equals( "p2" ) ) {
                                if ( tmpArray == null || tmpList == null || tmpLine == null || tmpP2 != null || tmpX != null || tmpY != null ) {
                                    bail_invalid_tag( topTag );
                                }
                                tmpP2 = new Posn();
                            }
                        } else {
                            xpp.next();
                            if ( topTag.equals( "x" ) ) {
                                if ( tmpArray == null || tmpList == null || tmpLine == null || ( tmpP1 == null && tmpP2 == null ) || tmpX != null ) {
                                    bail_invalid_tag( topTag );
                                }
                                tmpX = Integer.valueOf( xpp.getText().trim() );
                            } else if ( topTag.equals( "y" ) ) {
                                if ( tmpArray == null || tmpList == null || tmpLine == null || ( tmpP1 == null && tmpP2 == null ) || tmpY != null ) {
                                    bail_invalid_tag( topTag );
                                }
                                tmpY = Integer.valueOf( xpp.getText().trim() );
                            } else if ( topTag.equals( "color" ) ) {
                                if ( tmpArray == null || tmpList == null || tmpLine == null || tmpColor != null || tmpX != null || tmpY != null ) {
                                    bail_invalid_tag( topTag );
                                }
                                tmpColor = Integer.valueOf( xpp.getText() );
                            } else {
                                bail_invalid_tag( topTag );
                            }
                            xpp.next();

                            if ( !topTag.equals( xpp.getName() ) ) {
                                bail_invalid_check( topTag );
                            }
                        }
                        break;
                    }

                    case ( XmlPullParser.END_TAG ): {
                        if ( xpp.getName().equals( "boards" ) ) {
                            if ( tmpArray == null ) {
                                bail_invalid_tag( "/" +xpp.getName() );
                            }
                            boards = ( ArrayList<LinkedList<Line>> ) tmpArray.clone();
                            tmpArray = null;
                        } else if ( xpp.getName().equals( "solutions" ) ) {
                            if ( tmpArray == null ) {
                                bail_invalid_tag( "/" +xpp.getName() );
                            }
                            solutions = tmpArray;
                        } else if ( xpp.getName().equals( "graph" ) ) {
                            if (tmpList == null) {
                                bail_invalid_tag( "/" +xpp.getName() );
                            }
                            tmpArray.add( ( LinkedList<Line> ) tmpList.clone() );
                            tmpList = null;
                        } else if ( xpp.getName().equals( "p1" ) ) {
                            if ( tmpP1 == null || tmpX == null || tmpY == null ) {
                                bail_invalid_tag( "/" +xpp.getName() );
                            }
                            tmpP1 = new Posn( tmpX, tmpY );
                            tmpX = null;
                            tmpY = null;
                        } else if ( xpp.getName().equals( "p2" ) ) {
                            if ( tmpP2 == null || tmpX == null || tmpY == null ) {
                                bail_invalid_tag( "/" +xpp.getName() );
                            }
                            tmpP2 = new Posn( tmpX, tmpY );
                            tmpX = null;
                            tmpY = null;
                        } else if ( xpp.getName().equals( "Line" ) ) {
                            if ( tmpLine == null || tmpP1 == null || tmpP2 == null || tmpColor == null ) {
                                bail_invalid_tag( "/" +xpp.getName() );
                            }
                            tmpLine = new Line( tmpP1, tmpP2, tmpColor, Owner.App );
                            tmpList.add( tmpLine.clone() );
                            tmpLine = null;
                            tmpP1 = null;
                            tmpP2 = null;
                            tmpColor = null;
                        }
                        break;
                    }

                    case ( XmlPullParser.TEXT ): {
                        bail_invalid_text( xpp.getText() );
                    }

                    default: {
                        break;
                    }
                }
            }
        } catch ( XmlPullParserException e ) {
            e.printStackTrace();
        } catch ( IOException e  ) {
            e.printStackTrace();
        } catch ( InvalidXmlException e ) {
            e.printStackTrace();
        }

        return new Level( hint, draw_restriction, erase_restriction, drag_restriction, rotate_enabled, flip_enabled, colour_enabled, boards, solutions );
    }

    /*
     * Method used to get level from xml resource files
     */
    public World Fetch_world( final int world_num ) {
        // Set up temp fields
        this.world_num = world_num;
        this.level_num = 0;
        this.xpp = res.getXml( XMLMAP[( NUMBEROFLEVELSPERWORLD + 1 ) * ( world_num - 1 ) + level_num] );

        // Set up level variables
        String hint = "";
        boolean rotate_enabled = false;
        boolean flip_enabled = false;
        boolean colour_enabled = false;
        ArrayList<Posn> levels = null;
        ArrayList<LinkedList<Line>> solutions = null;

        try {
            // Set up temp variables
            LinkedList<Line> tmpList = null;
            Line tmpLine = null;
            Posn tmpP = null;
            Posn tmpP1 = null;
            Posn tmpP2 = null;
            Integer tmpColor = null;
            Integer tmpX = null;
            Integer tmpY = null;

            // Manually parse preamble
            xpp.next();
            xpp.next();
            if ( !xpp.getName().equals( "World" ) ) {
                bail_invalid_check( "World" );
            }

            hint = parse_preamble( "hint" );
            rotate_enabled = Boolean.valueOf( parse_preamble("rotate_enabled"));
            flip_enabled = Boolean.valueOf( parse_preamble( "flip_enabled" ) );
            colour_enabled = Boolean.valueOf( parse_preamble( "colour_enabled" ) );


            // Parse boarsd and solutions
            for ( int eventType = xpp.getEventType(); eventType != XmlResourceParser.END_DOCUMENT; eventType = xpp.next() ){
                switch ( eventType ) {

                    case ( XmlPullParser.START_TAG ): {
                        String topTag = xpp.getName();
                        if ( topTag.equals( "levels" ) || topTag.equals( "solutions" ) || topTag.equals( "graph" ) ||  topTag.equals( "Line" ) ||  topTag.equals( "p" ) || topTag.equals( "p1" ) || topTag.equals( "p2" )  ) {
                            if( topTag.equals( "levels" ) ) {
                                if ( levels != null || solutions != null || tmpList != null || tmpLine != null || tmpP != null || tmpP1 != null || tmpP2 != null || tmpColor != null || tmpX != null || tmpY != null ) {
                                    bail_invalid_tag( topTag );
                                }
                                levels = new ArrayList<Posn>();
                            } else if ( topTag.equals( "p" ) ) {
                                if ( levels == null || solutions != null || tmpList != null || tmpLine != null || tmpP != null || tmpP1 != null || tmpP2 != null || tmpX != null || tmpY != null ) {
                                    bail_invalid_tag( topTag );
                                }
                                tmpP = new Posn();
                            } else if ( topTag.equals( "solutions" ) ) {
                                if ( levels == null || solutions != null || tmpList != null || tmpLine != null || tmpP != null || tmpP1 != null || tmpP2 != null || tmpColor != null || tmpX != null || tmpY != null ) {
                                    bail_invalid_tag( topTag );
                                }
                                solutions = new ArrayList<LinkedList<Line>>();
                            } else if ( topTag.equals( "graph" ) ) {
                                if ( levels == null || solutions == null || tmpList != null || tmpLine != null || tmpP != null || tmpP1 != null || tmpP2 != null || tmpColor != null || tmpX != null || tmpY != null ) {
                                    bail_invalid_tag( topTag );
                                }
                                tmpList = new LinkedList<Line>();
                            } else if ( topTag.equals( "Line" ) ) {
                                if (levels == null || solutions == null || tmpList == null || tmpLine != null || tmpP != null || tmpP1 != null || tmpP2 != null || tmpColor != null || tmpX != null || tmpY != null) {
                                    bail_invalid_tag( topTag );
                                }
                                tmpLine = new Line();

                            } else if ( topTag.equals( "p1" ) ) {
                                if ( levels == null || solutions == null || tmpList == null || tmpLine == null || tmpP != null || tmpP1 != null || tmpX != null || tmpY != null ) {
                                    bail_invalid_tag( topTag );
                                }
                                tmpP1 = new Posn();
                            } else if ( topTag.equals( "p2" ) ) {
                                if ( levels == null || solutions == null || tmpList == null || tmpLine == null || tmpP != null || tmpP2 != null || tmpX != null || tmpY != null ) {
                                    bail_invalid_tag( topTag );
                                }
                                tmpP2 = new Posn();
                            }
                        } else {
                            xpp.next();
                            if ( topTag.equals( "x" ) ) {
                                if ( ( levels == null && solutions == null )  || ( tmpP1 == null && tmpP2 == null && tmpP == null ) || tmpX != null ) {
                                    bail_invalid_tag( topTag );
                                }
                                tmpX = Integer.valueOf( xpp.getText().trim() );
                            } else if ( topTag.equals( "y" ) ) {
                                if ( ( levels == null && solutions == null ) || ( tmpP1 == null && tmpP2 == null && tmpP == null ) || tmpY != null ) {
                                    bail_invalid_tag( topTag );
                                }
                                tmpY = Integer.valueOf( xpp.getText().trim() );
                            } else if ( topTag.equals( "color" ) ) {
                                if ( levels == null || solutions == null || tmpList == null || tmpLine == null || tmpColor != null || tmpX != null || tmpY != null ) {
                                    bail_invalid_tag( topTag );
                                }
                                tmpColor = Integer.valueOf( xpp.getText() );
                            } else {
                                bail_invalid_tag( topTag );
                            }
                            xpp.next();

                            if ( !topTag.equals( xpp.getName() ) ) {
                                bail_invalid_check( topTag );
                            }
                        }
                        break;
                    }

                    case ( XmlPullParser.END_TAG ): {
                        if ( xpp.getName().equals( "levels" ) ) {
                            if ( levels == null ) {
                                bail_invalid_tag( "/" + xpp.getName() );
                            }
                        }
                        else if ( xpp.getName().equals( "solutions" ) ) {
                            if ( solutions == null ) {
                                bail_invalid_tag( "/" + xpp.getName() );
                            }
                        } else if ( xpp.getName().equals( "graph" ) ) {
                            if (tmpList == null) {
                                bail_invalid_tag("/" + xpp.getName());
                            }
                            solutions.add((LinkedList<Line>) tmpList.clone());
                            tmpList = null;
                        } else if ( xpp.getName().equals( "p" ) ) {
                            if ( tmpP == null || tmpX == null || tmpY == null ) {
                                bail_invalid_tag( "/" + xpp.getName() );
                            }
                            tmpP = new Posn( tmpX, tmpY );
                            levels.add( tmpP.clone() );
                            tmpP = null;
                            tmpX = null;
                            tmpY = null;
                        } else if ( xpp.getName().equals( "p1" ) ) {
                            if ( tmpP1 == null || tmpX == null || tmpY == null ) {
                                bail_invalid_tag( "/" + xpp.getName() );
                            }
                            tmpP1 = new Posn( tmpX, tmpY );
                            tmpX = null;
                            tmpY = null;
                        } else if ( xpp.getName().equals( "p2" ) ) {
                            if ( tmpP2 == null || tmpX == null || tmpY == null ) {
                                bail_invalid_tag( "/" +xpp.getName() );
                            }
                            tmpP2 = new Posn( tmpX, tmpY );
                            tmpX = null;
                            tmpY = null;
                        } else if ( xpp.getName().equals( "Line" ) ) {
                            if ( tmpLine == null || tmpP1 == null || tmpP2 == null || tmpColor == null ) {
                                bail_invalid_tag( "/" +xpp.getName() );
                            }
                            tmpLine = new Line( tmpP1, tmpP2, tmpColor, Owner.App );
                            tmpList.add( tmpLine.clone() );
                            tmpLine = null;
                            tmpP1 = null;
                            tmpP2 = null;
                            tmpColor = null;
                        }
                        break;
                    }

                    case ( XmlPullParser.TEXT ): {
                        bail_invalid_text( xpp.getText() );
                    }

                    default: {
                        break;
                    }
                }
            }
        } catch ( XmlPullParserException e ) {
            e.printStackTrace();
        } catch ( IOException e  ) {
            e.printStackTrace();
        } catch ( InvalidXmlException e ) {
            e.printStackTrace();
        }

        return new World( hint,rotate_enabled, flip_enabled, colour_enabled, levels, solutions );
    }


    // Helper methods
    //---------------

    private void bail_invalid_check( final String actual ) throws InvalidXmlException {
        throw new InvalidXmlException( xpp.getLineNumber(), world_num, level_num, xpp.getName(), actual );
    }

    private void bail_invalid_tag( final String invalid_tag ) throws InvalidXmlException {
        throw new InvalidXmlException( xpp.getLineNumber(), world_num, level_num, invalid_tag, true );
    }

    private void bail_invalid_text( final String invalid_text ) throws InvalidXmlException {
        throw new InvalidXmlException( xpp.getLineNumber(), world_num, level_num, invalid_text, false );
    }

    private String parse_preamble( final String expected ) throws XmlPullParserException, IOException, InvalidXmlException {
        String returnval = "";
        xpp.next();
        if ( !xpp.getName().equals( expected ) ) {
            bail_invalid_check( expected );
        }
        xpp.next();
        returnval = xpp.getText();
        xpp.next();
        if ( !xpp.getName().equals( expected ) ) {
            bail_invalid_check( "/" + expected );
        }
        return returnval;
    }
}