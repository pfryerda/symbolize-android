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

public class PuzzleDB {
    // Static fields
    //---------------
    private static final int NUMBEROFLEVELSPERWORLD = 15;

    // Field
    private Resources res;
    private final int id_offset = 2130968576;


    // Constrcutor
    //-------------

    public PuzzleDB( Resources res ) {

        this.res = res;
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
            //XmlResourceParser xpp = res.getXml( R.xml.puzzle_1_1 );
            XmlResourceParser xpp = res.getXml( puzzle_id );
            xpp.next();
            ArrayList<LinkedList<Line>> tmpArray =  null;
            LinkedList<Line> tmpList = null;
            Line tmpLine = null;
            Posn tmpP1 = null;
            Posn tmpP2 = null;
            int tmpColor = Color.BLACK;

            for (int eventType = xpp.getEventType(); eventType != XmlResourceParser.END_DOCUMENT; eventType = xpp.next()){
                switch (eventType) {

                    case ( XmlPullParser.START_TAG ): {
                        if ( xpp.getName().equals( "hint" ) ) {
                            eventType = xpp.next();
                            hint = xpp.getText();
                            eventType = xpp.next();
                        } else if ( xpp.getName().equals( "drawRestirction" ) ) {
                            eventType = xpp.next();
                            drawRestirction = Integer.parseInt( xpp.getText() );
                            eventType = xpp.next();
                        } else if ( xpp.getName().equals( "eraseRestirction" ) ) {
                            eventType = xpp.next();
                            eraseRestirction = Integer.parseInt( xpp.getText() );
                            eventType = xpp.next();
                        } else if ( xpp.getName().equals( "rotateEnabled" ) ) {
                            eventType = xpp.next();
                            rotateEnabled = Boolean.valueOf(xpp.getText());
                            eventType = xpp.next();
                        } else if ( xpp.getName().equals( "flipEnabled" ) ) {
                            eventType = xpp.next();
                            flipEnabled = Boolean.valueOf(xpp.getText());
                            eventType = xpp.next();
                        } else if  ( xpp.getName().equals( "colourEnabled" ) ) {
                            eventType = xpp.next();
                            colourEnabled = Boolean.valueOf(xpp.getText());
                            eventType = xpp.next();
                        } else if ( xpp.getName().equals( "boards" ) || xpp.getName().equals( "solutions" ) ) {
                            tmpArray =  new ArrayList<LinkedList<Line>>();
                        } else if ( xpp.getName().equals( "graph" ) ) {
                            tmpList = new LinkedList<Line>();
                        } else if ( xpp.getName().equals( "Line" ) ) {
                            tmpLine = new Line();
                        } else if ( xpp.getName().equals( "p1" ) ) {
                            eventType = xpp.next();
                            eventType = xpp.next();
                            int first = Integer.parseInt( xpp.getText() );
                            eventType = xpp.next();
                            eventType = xpp.next();
                            eventType = xpp.next();
                            int second = Integer.parseInt( xpp.getText() );
                            eventType = xpp.next();
                            eventType = xpp.next();
                            tmpP1 = new Posn( first, second );
                        } else if ( xpp.getName().equals( "p2" ) ) {
                            eventType = xpp.next();
                            eventType = xpp.next();
                            int first = Integer.parseInt( xpp.getText() );
                            eventType = xpp.next();
                            eventType = xpp.next();
                            eventType = xpp.next();
                            int second = Integer.parseInt( xpp.getText() );
                            eventType = xpp.next();
                            eventType = xpp.next();
                            tmpP2 = new Posn( first, second );
                        } else if ( xpp.getName().equals( "color" ) ) {
                            eventType = xpp.next();
                            tmpColor = Integer.valueOf( xpp.getText() );
                            eventType = xpp.next();
                        }
                        break;
                    }

                    case ( XmlPullParser.END_TAG ): {
                        if ( xpp.getName().equals( "boards" ) ) {
                            boards = ( ArrayList<LinkedList<Line>> )tmpArray.clone();
                            tmpArray = null;
                        } else if ( xpp.getName().equals( "solutions" ) ) {
                            solutions = tmpArray;
                        } else if ( xpp.getName().equals( "graph" ) ) {
                            tmpArray.add( ( LinkedList<Line> ) tmpList.clone() );
                            tmpList = null;
                        } else if ( xpp.getName().equals( "line" ) ) {
                            tmpLine = new Line( tmpP1, tmpP2, tmpColor );
                            tmpList.add( tmpLine.clone() );
                            tmpLine = null;
                            tmpP1 = null;
                            tmpP2 = null;
                            tmpColor = Color.BLACK;
                        }
                        break;
                    }

                    default: {
                        break;
                    }
                }
            }
        } catch ( XmlPullParserException e  ) {
            e.printStackTrace();
        } catch (IOException e  ) {
            e.printStackTrace();
        }

        return new Level(worldNum, levelNum, hint, drawRestirction, eraseRestirction, rotateEnabled, flipEnabled, colourEnabled, boards, solutions);
    }
}
