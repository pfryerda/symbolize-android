package symbolize.app;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.Log;
import android.view.Display;
import android.widget.LinearLayout;

import java.util.Iterator;
import java.util.LinkedList;

import static symbolize.app.Constants.*;

public class GameController {
    // Fields
    private GameModel gameModel;
    private Level currLevel;
    private GameView gameView;

    // Consturctor
    public GameController(LinearLayout linearlayout, Bitmap bitmap) {
        gameModel = new GameModel();
        currLevel = null;
        gameView = new GameView(linearlayout, bitmap);
    }

    // Methods
    public void setLevel(Level l) {
        currLevel = l;
        gameModel.setGraph(l.getBoard());
        gameView.clearCanvas();
        drawGraph();
    }
    public boolean isInDrawMode()  {  return  gameModel.canDraw(); }
    public boolean isInEraseMode() {  return gameModel.canErase(); }
    public void toogleModes()      { gameModel.changeModes(); }
    public void reset() {
        setLevel(currLevel);
    }
    public boolean checkSolution() {
        return currLevel.checkCorrectness(gameModel.getGraph());
		/* Check last level      */
		/* setLevel(next level!) */
    }
    public void drawGraph() {
        for (Line l : gameModel.getGraph()) {
            gameView.renderLine(l);
        }
    }
    public void drawLine(Line l) {
        if (gameModel.getLinesDrawn() < currLevel.getDrawRestirction()) {
            gameModel.addLine(l);
            gameView.renderLine(l);
        } else {
            // [Display error box]
        }
    }
    public void tryToErase(Posn p) {
        Iterator<Line> it = gameModel.getGraph().iterator();
        Line l;
        while (it.hasNext()) {
            l = it.next();
            if (l.intersect(p)) {
                eraseLine(l);
            }
        }
    }
    public void eraseLine(Line l) {
        if ((gameModel.getLinesErased() < currLevel.getEraseRestirction()) || (l.getOwner() == Owner.User)) {
            gameModel.removeLine(l);
            gameView.renderErase(l);
        } else {
            // [Display error box]
        }
    }
    public void undo() {
        if (gameModel.getPastState() == null) {
            // [Display error box]
        } else {
            gameModel = gameModel.getPastState();
            gameView.clearCanvas();
            drawGraph();
        }
    }
    public void rotateRight() {
        if (currLevel.canRotate()) {
            gameModel.rotateGraphR();
            // <Canvas rotate 90 degree animation>
        }
    }
    public void rotateLeft() {
        if (currLevel.canRotate()) {
            gameModel.rotateGraphL();
            // <Canvas rotate -90 degree animation>
        }
    }
    public void flipHorizontally() {
        if (currLevel.canFlip()) {
            gameModel.flipGraphH();
            // <Canvas scale negative animation>
        }
    }
    public void flipVertically() {
        if (currLevel.canFlip()) {
            gameModel.flipGraphV();
            // <Canvas scale negative animation>
        }
    }
    public void shift() {
        if (currLevel.canShift()) {
            gameModel.shiftGraph(currLevel.incIt());
            gameView.clearCanvas();
            drawGraph();
        }
    }
}