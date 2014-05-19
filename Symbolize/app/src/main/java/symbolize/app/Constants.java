package symbolize.app;

import android.graphics.Color;

import java.util.Arrays;
import java.util.LinkedList;

public class Constants {
    public static final int SCALING = 1000;
    public static final int DRAWINGWIGGLEROOM = 140;
    public static final int ERASERWIGGLEROOM = 10;
    public static final int BACKGROUND_COLOR = Color.WHITE;
    public static final int LINESIZE = 60;
    public static final int ANIMATIONDURATION = 2000;
    public static final LinkedList<Line> BORDER = new LinkedList<Line>(Arrays.asList(new Line(new Posn(0, 0), new Posn(SCALING, 0)), new Line(new Posn(0, 0), new Posn(0, SCALING)), new Line(new Posn(SCALING, 0), new Posn(SCALING, SCALING)), new Line(new Posn(0, SCALING), new Posn(SCALING, SCALING))));
    public static final String LUKE = "Awesome";
}
