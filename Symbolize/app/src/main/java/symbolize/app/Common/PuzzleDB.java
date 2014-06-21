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

        String hint = "";
        int drawRestirction = 0;
        int eraseRestirction = 0;
        boolean rotateEnabled = false;
        boolean flipEnabled = false;
        boolean colourEnabled = false;
        ArrayList<LinkedList<Line>> boards = new ArrayList<LinkedList<Line>>( new LinkedList<LinkedList<Line>>() );
        ArrayList<LinkedList<Line>> solutions =  new ArrayList<LinkedList<Line>>( new LinkedList<LinkedList<Line>>() );

        try {
            int puzzle_id = id_offset + ( NUMBEROFLEVELSPERWORLD ) * ( worldNum - 1 ) + ( levelNum - 1 );
            XmlResourceParser xpp = res.getXml( puzzle_id );
            xpp.next();
            ArrayList<LinkedList<Line>> tmpArray =  null;
            LinkedList<Line> tmpList = null;
            Line tmpLine = null;
            Posn tmpP1 = null;
            Posn tmpP2 = null;
            int tmpColor = -1;

            for ( int eventType = xpp.getEventType(); eventType != XmlResourceParser.END_DOCUMENT; eventType = xpp.next() ){
                switch ( eventType ) {

                    case ( XmlPullParser.START_TAG ): {
                        String topTag = xpp.getName();
                        if ( topTag.equals( "graph" ) || topTag.equals( "Level" ) || topTag.equals( "Line" ) || topTag.equals( "boards" ) || topTag.equals( "solutions" ) ) {
                            if ( topTag.equals( "graph" ) ) {
                                tmpList = new LinkedList<Line>();
                            } else if ( topTag.equals( "Line" ) ) {
                                tmpLine = new Line();
                            } else if ( topTag.equals( "boards" ) || topTag.equals( "solutions" ) ) {
                                tmpArray = new ArrayList<LinkedList<Line>>();
                            }
                        } else {
                            xpp.next();
                            if ( topTag.equals( "hint" ) ) {
                                hint = xpp.getText();
                            } else if ( topTag.equals( "drawRestirction" ) ) {
                                drawRestirction = Integer.parseInt( xpp.getText().trim() );
                            } else if ( topTag.equals( "eraseRestirction" ) ) {
                                eraseRestirction = Integer.parseInt( xpp.getText().trim() );
                            } else if ( topTag.equals( "rotateEnabled" ) ) {
                                rotateEnabled = Boolean.valueOf( xpp.getText() );
                            } else if ( topTag.equals( "flipEnabled" ) ) {
                                flipEnabled = Boolean.valueOf( xpp.getText() );
                            } else if ( topTag.equals( "colourEnabled" ) ) {
                                colourEnabled = Boolean.valueOf( xpp.getText() );
                            } else if ( topTag.equals( "p1" ) || topTag.equals( "p2" ) ) {
                                xpp.next();
                                int first = Integer.parseInt( xpp.getText().trim() );
                                xpp.next();
                                xpp.next();
                                xpp.next();
                                int second = Integer.parseInt( xpp.getText().trim() );
                                xpp.next();
                                if (topTag.equals( "p1" ) ) {
                                    tmpP1 = new Posn( first, second );
                                } else {
                                    tmpP2 = new Posn( first, second );
                                }
                            } else if ( topTag.equals( "color" ) ) {
                                tmpColor = Integer.valueOf( xpp.getText() );
                            } else {
                                throw new InvalidXmlException(worldNum, levelNum, topTag);
                            }
                            xpp.next();

                            String bottomTag = xpp.getName();
                            if (!topTag.equals(bottomTag)) {
                                throw new InvalidXmlException(worldNum, levelNum, topTag, bottomTag);
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
                            if ( tmpList == null ) {
                                throw new InvalidXmlException( worldNum, levelNum, "/graph" );
                            }
                            tmpArray.add( ( LinkedList<Line> ) tmpList.clone() );
                            tmpList = null;
                        } else if ( xpp.getName().equals( "Line" ) ) {
                            if ( tmpLine == null || tmpP1 == null || tmpP2 == null || tmpColor == -1 ) {
                                throw new InvalidXmlException( worldNum, levelNum, "/Line" );
                            }
                            tmpLine = new Line( tmpP1, tmpP2, tmpColor );
                            tmpList.add( tmpLine.clone() );
                            tmpLine = null;
                            tmpP1 = null;
                            tmpP2 = null;
                            tmpColor = -1;
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
