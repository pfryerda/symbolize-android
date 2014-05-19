package symbolize.app;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.LinearLayout;
import android.widget.Toast;

import static symbolize.app.Constants.*;
import java.util.LinkedList;


public class MainActivity extends Activity {

    private Posn startPoint;
    private Posn endPoint;
    private GameController gameController;
    private LinearLayout ll;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Set up Screen
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set up linerlayout and bitamp
        final Display DISPLAY = getWindowManager().getDefaultDisplay();
        final Point SCREENSIZE = new Point();
        DISPLAY.getSize(SCREENSIZE);
        Log.d("ScreenSize", "X:" + SCREENSIZE.x + " Y:" + SCREENSIZE.y);

        ll = (LinearLayout) findViewById(R.id.canvas);
        ll.getLayoutParams().height = SCREENSIZE.x;
        ll.getLayoutParams().width = SCREENSIZE.x;
        findViewById(R.id.buttons).getLayoutParams().height = (SCREENSIZE.y-SCREENSIZE.x)/2;
        findViewById(R.id.adspace).getLayoutParams().height = (SCREENSIZE.y-SCREENSIZE.x)/2;

        Bitmap bitMap = Bitmap.createScaledBitmap(Bitmap.createBitmap(SCREENSIZE.x, SCREENSIZE.x, Bitmap.Config.ARGB_8888), SCALING, SCALING, true);

        // Set up Game
        gameController = new GameController(this, ll, bitMap);
        // Build level 1
        LinkedList<Line> puzzle = new LinkedList<Line>();
        puzzle.addLast(new Line(new Posn(100, 100), new Posn(500, 500)));
        puzzle.addLast(new Line(new Posn(500, 500), new Posn(100, 900)));

        LinkedList<LinkedList<Line>> solns = new LinkedList<LinkedList<Line>>();
        LinkedList<Line> soln = new LinkedList<Line>();
        soln.addLast(new Line(new Posn(100, 100), new Posn(500, 500)));
        soln.addLast(new Line(new Posn(500, 500), new Posn(100, 900)));
        soln.addLast(new Line(new Posn(500, 500), new Posn(900, 500)));
        solns.addLast(soln);

        Level level = new Level(1, 1, puzzle, solns, 30000, 30000, true, true, true, null);

        gameController.setLevel(level);  // Load level 1-1

        // Set up event listeners
        ll.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final GameController controller = gameController;
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    int touchX = Math.round(event.getX());
                    int touchY = Math.round(event.getY());
                    startPoint = new Posn(touchX, touchY);
                    Log.d("ACTION_DOWN", "("+startPoint.x()+","+startPoint.y()+")");
                    if (controller.isInEraseMode()) {
                        controller.tryToErase(startPoint);
                    }
                    return true;
                }
                else if (event.getAction() == MotionEvent.ACTION_UP) {
                    int touchX = Math.round(event.getX());
                    int touchY = Math.round(event.getY());
                    endPoint = new Posn(touchX, touchY);
                    Log.d("ACTION_UP", "("+endPoint.x()+","+endPoint.y()+")");
                    if (controller.isInDrawMode()) {
                        controller.drawLine(new Line(startPoint, endPoint, Owner.User));
                    }
                    else if (controller.isInEraseMode()) {
                        controller.tryToErase(endPoint);
                    }
                    return true;
                }
                else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                    if (controller.isInEraseMode()) {
                        int touchX = Math.round(event.getX());
                        int touchY = Math.round(event.getY());
                        startPoint = new Posn(touchX, touchY);
                        //Log.d("ACTION_MOVE", "("+startPoint.x()+","+startPoint.y()+")");
                        controller.tryToErase(startPoint);
                    }
                    return  true;
                }
                return false;
            }
        });

    }

    public void onToggleButtonClicked(View view) { gameController.toogleModes(); }
    public void onCheckButtonClicked(View view) {
        if (gameController.checkSolution()) {
            Toast.makeText(this, "You are correct!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "You are incorrect", Toast.LENGTH_SHORT).show();
        }
    }
    public void onHintButtonClicked(View view) {
        gameController.rotateRight();
        //gameController.rotateLeft();
        //gameController.flipHorizontally();
        //gameController.flipVertically();
        /*Display hint box here;*/
    }
    public void onResetButtonClicked(View view) {
        gameController.reset();
        ll.invalidate();
    }
    public void onUndoButtonClicked(View view) {
        gameController.undo();
        ll.invalidate();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
