package symbolize.app;

import android.content.Context;
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
    public GameController(Context ctx, LinearLayout linearlayout, Bitmap bitmap) {
        gameModel = new GameModel();
        currLevel = null;
        gameView = new GameView(ctx, linearlayout, bitmap, gameModel);
    }

    // Methods
    public void setLevel(Level l) {
        currLevel = l;
        gameModel.setGraph(l.getBoard());
        gameView.renderGraph();
    }
    public boolean isInDrawMode()  {  return  gameModel.canDraw(); }
    public boolean isInEraseMode() {  return gameModel.canErase(); }
    public void toogleModes()      {  gameModel.changeModes(); }
    public void reset()            {  setLevel(currLevel); }
    public boolean checkSolution() {
        return currLevel.checkCorrectness(gameModel.getGraph());
		/* Check last level      */
		/* setLevel(next level!) */
    }
    public void drawLine(Line l) {
        if (gameModel.getLinesDrawn() < currLevel.getDrawRestirction()) {
            gameModel.addLine(l);
            gameView.renderLine(l);
        } else {
            gameView.renderToast("Cannot draw any more lines ");
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
            gameView.renderToast("Cannot erase any more lines ");
        }
    }
    public void undo() {
        if (gameModel.getPastState() == null) {
            gameView.renderToast("There is nothing to undo");
        } else {
            gameView.renderToast("There is things to undo");
            gameModel = gameModel.getPastState();
            gameView.renderUndo();
        }
    }
    public void rotateRight() {
        if (currLevel.canRotate()) {
            gameModel.rotateGraphR();
            gameView.renderRotateR(gameModel.getGraph());
        }
    }
    public void rotateLeft() {
        if (currLevel.canRotate()) {
            gameModel.rotateGraphL();
            gameView.renderRotateL();
        }
    }
    public void flipHorizontally() {
        if (currLevel.canFlip()) {
            gameModel.flipGraphH();
            gameView.renderFlipH();
        }
    }
    public void flipVertically() {
        if (currLevel.canFlip()) {
            gameModel.flipGraphV();
            gameView.renderFlipV();
        }
    }
    public void shift() {
        if (currLevel.canShift()) {
            gameModel.shiftGraph(currLevel.incIt());
            gameView.renderGraph();
        }
    }
}