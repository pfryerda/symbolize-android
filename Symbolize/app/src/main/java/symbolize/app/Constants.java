package symbolize.app;

import android.graphics.Color;
import android.graphics.Point;
import android.util.SparseIntArray;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;

public class Constants {
    // Constants
    //-----------

    public static final int SCALING = 1000;
    public static final int DRAWINGTHRESHOLD = 140;
    public static final int ERASINGTHRESHOLD = 75;
    public static final int FLIPPINGTHRESHOLD = 140;
    public static final int LINEWIDTH = 60;
    public static final int MINLINESIZESQR = 10000;
    public static final int ROTATEDURATION = 450;
    public static final int FLIPDURATION = 450;
    public static final int FADEDURATION = 450;
    public static final int SHAKETHRESHOLD = 8;
    public static final double SHAKEIDLETHRESHOLD = 1.15;
    public static final int SHAKESEPARATIONTIME = 500;
    public static final int ERASEDELAY = 250; // In milisceonds ?
    public static final ArrayList<Integer> COLORARRAY = new ArrayList( Arrays.asList( Color.BLACK, Color.RED, Color.YELLOW, Color.GREEN, Color.BLUE, Color.CYAN, Color.MAGENTA ) );
    public static final SparseIntArray COLORMAP = makeColorMap();
    public static final LinkedList<Line> GRID = makeGrid();
    public static final LinkedList<Line> BORDER = new LinkedList<Line>( Arrays.asList( new Line( new Posn( 0, 0 ), new Posn( SCALING, 0 ) ), new Line( new Posn( 0, 0 ), new Posn( 0, SCALING ) ), new Line( new Posn( SCALING, 0 ), new Posn( SCALING, SCALING ) ), new Line( new Posn( 0, SCALING ), new Posn( SCALING, SCALING ) ) ) );
    public static final String LUKE = "Awesome";
    public static Point SCREENSIZE;


    // Methods used to construct constants
    //-------------------------------------

    public final static SparseIntArray makeColorMap() {
        SparseIntArray colormap = new SparseIntArray();
        for ( int i = 0; i < COLORARRAY.size(); ++i ) {
            colormap.put( COLORARRAY.get(i), i );
        }
        return colormap;
    }

    public final static LinkedList<Line> makeGrid() {
        LinkedList<Line> grid = new LinkedList<Line>();
        for ( int x = SCALING/10; x < SCALING; x+=SCALING/10 ) {
            grid.addLast( new Line( new Posn( x, 0 ), new Posn( x, SCALING ), Color.LTGRAY ) );
        }

        for ( int y = SCALING/10; y < SCALING; y+=SCALING/10 ) {
            grid.addLast( new Line( new Posn( 0, y ), new Posn( SCALING, y ), Color.LTGRAY ) );
        }

        return grid;
    }
}