package symbolize.app;

import android.graphics.Color;
import android.util.SparseIntArray;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;

public class Constants {
    public static final int SCALING = 1000;
    public static final int DRAWINGWIGGLEROOM = 140;
    public static final int ERASERWIGGLEROOM = 50;
    public static final int LINESIZE = 60;
    public static final int ROTATEDURATION = 2000;
    public static final int FLIPDURATION = 2000;
    public static final int FADEDURATION = 350;
    public static final ArrayList<Integer> COLORARRAY = new ArrayList(Arrays.asList(Color.BLACK, Color.RED, Color.YELLOW, Color.GREEN, Color.BLUE, Color.CYAN, Color.MAGENTA));
    public static final SparseIntArray COLORMAP = makeColorMap();
    public static final LinkedList<Line> GRID = makeGrid();
    public static final LinkedList<Line> BORDER = new LinkedList<Line>(Arrays.asList(new Line(new Posn(0, 0), new Posn(SCALING, 0)), new Line(new Posn(0, 0), new Posn(0, SCALING)), new Line(new Posn(SCALING, 0), new Posn(SCALING, SCALING)), new Line(new Posn(0, SCALING), new Posn(SCALING, SCALING))));
    public static final String LUKE = "Awesome";

    public static SparseIntArray makeColorMap() {
        SparseIntArray colormap = new SparseIntArray();
        for (int i = 0; i < COLORARRAY.size(); ++i) {
            colormap.put(COLORARRAY.get(i), i);
        }
        return colormap;
    }

    public static final LinkedList<Line> makeGrid() {
        LinkedList<Line> grid = new LinkedList<Line>();
        for (int x = SCALING/10; x < SCALING; x+=SCALING/10) {
            grid.addLast(new Line(new Posn(x, 0), new Posn(x, SCALING), Color.LTGRAY));
        }

        for (int y = SCALING/10; y < SCALING; y+=SCALING/10) {
            grid.addLast(new Line(new Posn(0, y), new Posn(SCALING, y), Color.LTGRAY));
        }

        return grid;
    }
}