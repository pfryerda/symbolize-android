package symbolize.app.Common;

import android.content.res.Resources;
import android.content.res.XmlResourceParser;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;

import symbolize.app.Common.Enum.Owner;
import symbolize.app.R;

/*
 * Expcetion class for xml parsing specific exceptions
 */
class InvalidXmlException extends Exception {
    // Constructors
    //--------------

    public InvalidXmlException( int linenum, int worldNum, int levelNum, String expected, String actual ) {
        super( "Error in xml for level_" + worldNum + "_" + levelNum + ".xml::Line:" + linenum + ". Expected: <" + expected + "> Actual: " + ( ( actual == null ) ? "Not a Tag!" : "<" + actual + ">" ) );
    }

    public InvalidXmlException( int linenum, int worldNum, int levelNum, String invalid_text, boolean isTag ) {
        super( "Error in xml for level_" + worldNum + "_" + levelNum + ".xml::Line:" + linenum + ". Given an unexpected " + ( ( isTag ) ? "tag: + \"<\" + invalid_text + \">\"" : "text:" + invalid_text ) );
    }
}


/*
 * Class used to parse stoed xml files and load them into memory
 */
public class PuzzleDB {
    // Static fields
    //---------------
    public static final int NUMBEROFLEVELSPERWORLD = 15;

    // Fields
    //--------
    private Resources res;
    private int level_id_offset;
    private int world_id_offset;
    private int worldNum;
    private int levelNum;
    private XmlResourceParser xpp;



    // Constrcutor
    //-------------

    public PuzzleDB( Resources res ) {
        this.res = res;
        level_id_offset = R.xml.level_1_1;
        world_id_offset = R.xml.world_1;
    }

    // Methods
    //---------

    /*
     * Method used to get level from xml resource files
     */
    public Level fetch_level( int worldNum, int levelNum ) {
        // Set up temp fields
        this.worldNum = worldNum;
        this.levelNum = levelNum;
        this.xpp = res.getXml( level_id_offset + ( NUMBEROFLEVELSPERWORLD ) * ( worldNum - 1 ) + ( levelNum - 1 ) );

        // Set up level variables
        String hint = "";
        int draw_restriction = 0;
        int erase_restriction = 0;
        boolean rotateEnabled = false;
        boolean flipEnabled = false;
        boolean colourEnabled = false;
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
            draw_restriction = Integer.parseInt(parse_preamble("draw_restriction").trim());
            erase_restriction = Integer.parseInt( parse_preamble( "erase_restriction" ).trim() );
            rotateEnabled = Boolean.valueOf(parse_preamble("rotateEnabled"));
            flipEnabled = Boolean.valueOf( parse_preamble( "flipEnabled" ) );
            colourEnabled = Boolean.valueOf( parse_preamble( "colourEnabled" ) );


            // Parse boarsd and solutions
            for ( int eventType = xpp.getEventType(); eventType != XmlResourceParser.END_DOCUMENT; eventType = xpp.next() ){

                switch ( eventType ) {

                    case ( XmlPullParser.START_TAG ): {
                        String topTag = xpp.getName();
                        if ( topTag.equals( "Level" ) || topTag.equals( "boards" ) || topTag.equals( "solutions" ) || topTag.equals( "graph" ) ||  topTag.equals( "Line" ) ||  topTag.equals( "p1" ) || topTag.equals( "p2" )  ) {
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

        return new Level( hint, draw_restriction, erase_restriction, rotateEnabled, flipEnabled, colourEnabled, boards, solutions );
    }


    // Helper methods
    //---------------

    private void bail_invalid_check( String actual ) throws InvalidXmlException {
        throw new InvalidXmlException( xpp.getLineNumber(), worldNum, levelNum, xpp.getName(), actual );
    }

    private void bail_invalid_tag( String invalid_tag ) throws InvalidXmlException {
        throw new InvalidXmlException( xpp.getLineNumber(), worldNum, levelNum, invalid_tag, true );
    }

    private void bail_invalid_text( String invalid_text ) throws InvalidXmlException {
        throw new InvalidXmlException( xpp.getLineNumber(), worldNum, levelNum, invalid_text, false );
    }

    private String parse_preamble( String expected ) throws XmlPullParserException, IOException, InvalidXmlException {
        String returnval = "";
        xpp.next();
        if ( !xpp.getName().equals( expected ) ) {
            bail_invalid_check( expected );
        }
        xpp.next();
        returnval = xpp.getText();
        xpp.next();
        if (!xpp.getName().equals(expected)) {
            bail_invalid_check( "/" + expected );
        }
        return returnval;
    }
}
