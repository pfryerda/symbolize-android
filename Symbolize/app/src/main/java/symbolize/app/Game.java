package symbolize.app;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.Log;
import android.view.Display;

import java.util.Iterator;
import java.util.LinkedList;

import static symbolize.app.Constants.*;

public class Game {
    // Fields
    private Player player;
    private Level currLevel;
    private Canvas canvas;
    private Paint paint;

    // Consturctor
    public Game(Bitmap bitmap) {
        player = new Player();
        currLevel = null;
        canvas = new Canvas(bitmap);
        paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setAntiAlias(true);
        paint.setStrokeWidth(LINESIZE);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
    }

    // Methods
    public Canvas getCanvas() { return  canvas; }
    public void clearCanvas() {
        canvas.drawColor(BACKGROUND_COLOR);
        drawGraph(BORDER);
        // Draw grid
    }
    public void setLevel(Level l) {
        currLevel = l;
        player.setGraph(l.getBoard());
        clearCanvas();
        drawGraph();
    }
    public boolean isInDrawMode() {  return  player.canDraw(); }
    public boolean isInEraseMode() {  return player.canErase(); }
    public void toogleModes()      { player.changeModes(); }
    public void reset() {
        setLevel(currLevel);
    }
    public boolean checkSolution() {
        return currLevel.checkCorrectness(player.getGraph());
		/* Check last level      */
		/* setLevel(next level!) */
    }
    public void drawGraph() {
        for (Line l : player.getGraph()) {
            paint.setColor(l.getColor());
            canvas.drawLine(l.getP1().x(), l.getP1().y(), l.getP2().x(), l.getP2().y(), paint);
        }
    }
    public void drawGraph(LinkedList<Line> g) {
        for (Line l : g) {
            paint.setColor(l.getColor());
            canvas.drawLine(l.getP1().x(), l.getP1().y(), l.getP2().x(), l.getP2().y(), paint);
        }
    }
    public void drawLine(Line l) {
        if (player.getLinesDrawn() < currLevel.getDrawRestirction()) {
            player.addLine(l);
            paint.setColor(l.getColor());
            canvas.drawLine(l.getP1().x(), l.getP1().y(), l.getP2().x(), l.getP2().y(), paint);
        } else {
            // [Display error box]
        }
    }
    public void tryToErase(Posn p) {
        Iterator<Line> it = player.getGraph().iterator();
        Line l;
        while (it.hasNext()) {
            l = it.next();
            if (l.intersect(p)) {
                eraseLine(l);
            }
        }
    }
    public void eraseLine(Line l) {
        if ((player.getLinesErased() < currLevel.getEraseRestirction()) || (l.getOwner() == Owner.User)) {
            player.removeLine(l);
            paint.setColor(BACKGROUND_COLOR);
            canvas.drawLine(l.getP1().x(), l.getP1().y(), l.getP2().x(), l.getP2().y(), paint);
        } else {
            // [Display error box]
        }
    }
    public void undo() {
        if (player.getPastState() == null) {
            // [Display error box]
        } else {
            player = player.getPastState();
            clearCanvas();
            drawGraph();
        }
    }
    public void rotateRight() {
        if (currLevel.canRotate()) {
            player.rotateGraphR();
            // <Canvas rotate 90 degree animation>
        }
    }
    public void rotateLeft() {
        if (currLevel.canRotate()) {
            player.rotateGraphL();
            // <Canvas rotate -90 degree animation>
        }
    }
    public void flipHorizontally() {
        if (currLevel.canFlip()) {
            player.flipGraphH();
            // <Canvas scale negative animation>
        }
    }
    public void flipVertically() {
        if (currLevel.canFlip()) {
            player.flipGraphV();
            // <Canvas scale negative animation>
        }
    }
    public void shift() {
        if (currLevel.canShift()) {
            player.shiftGraph(currLevel.incIt());
            clearCanvas();
             drawGraph();
        }
    }
}