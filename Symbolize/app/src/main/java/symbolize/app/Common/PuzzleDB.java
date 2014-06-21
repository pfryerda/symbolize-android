package symbolize.app.Common;

import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.graphics.Color;
import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;

import symbolize.app.R;

/*
 * Expcetion class for xml parsing specific exceptions
 */
class InvalidXmlException extends Exception {
    // Constructors
    //--------------

    public InvalidXmlException( int worldNum, int levelNum, String expected, String actual ) {
        super( "Error in xml for puzzle_" + worldNum + "_" + levelNum + ".xml. Expected: <" + expected + "> Actual: <" + actual + ">" );
    }

    public InvalidXmlException( int worldNum, int levelNum, String invalid_tag ) {
        super( "Error in xml for puzzle_" + worldNum + "_" + levelNum + ".xml. Given unexpected tag: " + "<" + invalid_tag + ">" );
    }
}


/*
 * Class used to parse stoed xml files and load them into memory
 */
public class PuzzleDB {
    // Static fields
    //---------------
    private static final int NUMBEROFLEVELSPERWORLD = 15;

    // Field
    private Resources res;
    private int id_offset;


    // Constrcutor
    //-------------

    public PuzzleDB( Resources res ) {
        this.res = res;
        id_offset = R.xml.puzzle_1_1;
    }

    // Methods
    //---------

    /*
     * Method used to get level from xml resource files
     */
    public Level fetch_level( int worldNum, int levelNum ) {
        // Set up level variables
        String hint = "";
        int drawRestirction = 0;
        int eraseRestirction = 0;
        boolean rotateEnabled = false;
        boolean flipEnabled = false;
        boolean colourEnabled = false;
        ArrayList<LinkedList<Line>> boards = new ArrayList<LinkedList<Line>>( new LinkedList<LinkedList<Line>>() );
        ArrayList<LinkedList<Line>> solutions =  new ArrayList<LinkedList<Line>>( new LinkedList<LinkedList<Line>>() );

        int puzzle_id = id_offset + ( NUMBEROFLEVELSPERWORLD ) * ( worldNum - 1 ) + ( levelNum - 1 );
        XmlResourceParser xpp = res.getXml( puzzle_id );

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
            xpp.next();

            if ( !xpp.getName().equals( "hint" ) ) {
                throw new InvalidXmlException( worldNum, levelNum, xpp.getName() );
            }
            xpp.next();
            hint = xpp.getText();
            xpp.next();
            if ( !xpp.getName().equals( "hint" ) ) {
                throw new InvalidXmlException( worldNum, levelNum, "/"+xpp.getName() );
            }

            xpp.next();

            if ( !xpp.getName().equals( "drawRestirction" ) ) {
                throw new InvalidXmlException( worldNum, levelNum, xpp.getName() );
            }
            xpp.next();
            drawRestirction = Integer.parseInt( xpp.getText().trim() );
            xpp.next();
            if ( !xpp.getName().equals( "drawRestirction" ) ) {
                throw new InvalidXmlException( worldNum, levelNum, "/"+xpp.getName() );
            }

            xpp.next();

            if ( !xpp.getName().equals( "eraseRestirction" ) ) {
                throw new InvalidXmlException( worldNum, levelNum, xpp.getName() );
            }
            xpp.next();
            eraseRestirction = Integer.parseInt( xpp.getText().trim() );
            xpp.next();
            if ( !xpp.getName().equals( "eraseRestirction" ) ) {
                throw new InvalidXmlException( worldNum, levelNum, "/"+xpp.getName() );
            }

            xpp.next();

            if ( !xpp.getName().equals( "rotateEnabled" ) ) {
                throw new InvalidXmlException( worldNum, levelNum, xpp.getName() );
            }
            xpp.next();
            rotateEnabled = Boolean.valueOf( xpp.getText() );
            xpp.next();
            if ( !xpp.getName().equals( "rotateEnabled" ) ) {
                throw new InvalidXmlException( worldNum, levelNum, "/"+xpp.getName() );
            }

            xpp.next();

            if ( !xpp.getName().equals( "flipEnabled" ) ) {
                throw new InvalidXmlException( worldNum, levelNum, xpp.getName() );
            }
            xpp.next();
            flipEnabled = Boolean.valueOf( xpp.getText() );
            xpp.next();
            if ( !xpp.getName().equals( "flipEnabled" ) ) {
                throw new InvalidXmlException( worldNum, levelNum, "/"+xpp.getName() );
            }

            xpp.next();

            if ( !xpp.getName().equals( "colourEnabled" ) ) {
                throw new InvalidXmlException( worldNum, levelNum, xpp.getName() );
            }
            xpp.next();
            colourEnabled = Boolean.valueOf( xpp.getText() );
            xpp.next();
            if ( !xpp.getName().equals( "colourEnabled" ) ) {
                throw new InvalidXmlException( worldNum, levelNum, "/"+xpp.getName() );
            }

            // Parse boarsd and solutions
            for ( int eventType = xpp.getEventType(); eventType != XmlResourceParser.END_DOCUMENT; eventType = xpp.next() ){

                switch ( eventType ) {

                    case ( XmlPullParser.START_TAG ): {
                        String topTag = xpp.getName();
                        if ( topTag.equals( "Level" ) || topTag.equals( "boards" ) || topTag.equals( "solutions" ) || topTag.equals( "graph" ) ||  topTag.equals( "Line" ) ||  topTag.equals( "p1" ) || topTag.equals( "p2" )  ) {
                            if ( topTag.equals( "boards" ) || topTag.equals( "solutions" ) ) {
                                if ( tmpArray != null || tmpList != null || tmpLine != null || tmpP1 != null || tmpP2 != null || tmpColor != null || tmpX != null || tmpY != null ) {
                                    throw new InvalidXmlException( worldNum, levelNum, topTag );
                                }
                                tmpArray = new ArrayList<LinkedList<Line>>();
                            } else if ( topTag.equals( "graph" ) ) {
                                if ( tmpArray == null || tmpList != null || tmpLine != null || tmpP1 != null || tmpP2 != null || tmpColor != null || tmpX != null || tmpY != null ) {
                                    throw new InvalidXmlException( worldNum, levelNum, topTag );
                                }
                                tmpList = new LinkedList<Line>();
                            } else if ( topTag.equals( "Line" ) ) {
                                if ( tmpArray == null || tmpList == null || tmpLine != null || tmpP1 != null || tmpP2 != null || tmpColor != null || tmpX != null || tmpY != null ) {
                                    throw new InvalidXmlException( worldNum, levelNum, topTag );
                                }
                                tmpLine = new Line();
                            } else if ( topTag.equals( "p1" ) ) {
                                if ( tmpArray == null || tmpList == null || tmpLine == null || tmpP1 != null || tmpX != null || tmpY != null ) {
                                    throw new InvalidXmlException( worldNum, levelNum, topTag );
                                }
                                tmpP1 = new Posn();
                            } else if ( topTag.equals( "p2" ) ) {
                                if ( tmpArray == null || tmpList == null || tmpLine == null || tmpP2 != null || tmpX != null || tmpY != null ) {
                                    throw new InvalidXmlException( worldNum, levelNum, topTag );
                                }
                                tmpP2 = new Posn();
                            }
                        } else {
                            xpp.next();
                            if ( topTag.equals( "x" ) ) {
                                if ( tmpArray == null || tmpList == null || tmpLine == null || ( tmpP1 == null && tmpP2 == null ) || tmpX != null ) {
                                    throw new InvalidXmlException( worldNum, levelNum, topTag );
                                }
                                tmpX = Integer.valueOf( xpp.getText().trim() );
                            } else if ( topTag.equals( "y" ) ) {
                                if ( tmpArray == null || tmpList == null || tmpLine == null || ( tmpP1 == null && tmpP2 == null ) || tmpY != null ) {
                                    throw new InvalidXmlException( worldNum, levelNum, topTag );
                                }
                                tmpY = Integer.valueOf( xpp.getText().trim() );
                            } else if ( topTag.equals( "color" ) ) {
                                if ( tmpArray == null || tmpList == null || tmpLine == null || tmpColor != null || tmpX != null || tmpY != null ) {
                                    throw new InvalidXmlException( worldNum, levelNum, topTag );
                                }
                                tmpColor = Integer.valueOf( xpp.getText() );
                            } else {
                                throw new InvalidXmlException(worldNum, levelNum, topTag);
                            }
                            xpp.next();

                            String bottomTag = xpp.getName();
                            if (!topTag.equals(bottomTag)) {
                                throw new InvalidXmlException(worldNum, levelNum, topTag, "/"+bottomTag);
                            }
                        }
                        break;
                    }

                    case ( XmlPullParser.END_TAG ): {
                        if ( xpp.getName().equals( "boards" ) ) {
                            if ( tmpArray == null ) {
                                throw new InvalidXmlException( worldNum, levelNum, "/boards" );
                            }
                            boards = ( ArrayList<LinkedList<Line>> ) tmpArray.clone();
                            tmpArray = null;
                        } else if ( xpp.getName().equals( "solutions" ) ) {
                            if ( tmpArray == null ) {
                                throw new InvalidXmlException( worldNum, levelNum, "/solutions" );
                            }
                            solutions = tmpArray;
                        } else if ( xpp.getName().equals( "graph" ) ) {
                            if (tmpList == null) {
                                throw new InvalidXmlException(worldNum, levelNum, "/graph");
                            }
                            tmpArray.add( ( LinkedList<Line> ) tmpList.clone());
                            tmpList = null;
                        } else if ( xpp.getName().equals( "p1" ) ) {
                            if ( tmpP1 == null || tmpX == null || tmpY == null ) {
                                throw new InvalidXmlException(worldNum, levelNum, "/Posn");
                            }
                            tmpP1 = new Posn( tmpX, tmpY );
                            tmpX = null;
                            tmpY = null;
                        } else if ( xpp.getName().equals( "p2" ) ) {
                            if ( tmpP2 == null || tmpX == null || tmpY == null ) {
                                throw new InvalidXmlException(worldNum, levelNum, "/Posn");
                            }
                            tmpP2 = new Posn( tmpX, tmpY );
                            tmpX = null;
                            tmpY = null;
                        } else if ( xpp.getName().equals( "Line" ) ) {
                            if ( tmpLine == null || tmpP1 == null || tmpP2 == null || tmpColor == null ) {
                                throw new InvalidXmlException( worldNum, levelNum, "/Line" );
                            }
                            tmpLine = new Line( tmpP1, tmpP2, tmpColor );
                            tmpList.add( tmpLine.clone() );
                            tmpLine = null;
                            tmpP1 = null;
                            tmpP2 = null;
                            tmpColor = null;
                        }
                        break;
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

        return new Level(worldNum, levelNum, hint, drawRestirction, eraseRestirction, rotateEnabled, flipEnabled, colourEnabled, boards, solutions);
    }
}
