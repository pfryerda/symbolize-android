package symbolize.app;

import android.app.Activity;
import android.graphics.Bitmap;
/*import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;*/
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

import java.util.LinkedList;


public class MainActivity extends Activity {

    private Posn startPoint;
    private Posn endPoint;
    private Game game;
    private LinearLayout ll;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*
        final Paint paint = new Paint();
        paint.setColor(Color.parseColor("#CD5C5C"));
        Bitmap bg = Bitmap.createBitmap(480, 800, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bg);
        //canvas.drawRect(50, 50, 200, 200, paint);
        LinearLayout ll = (LinearLayout) findViewById(R.id.rect);
        ll.setBackgroundDrawable(new BitmapDrawable(bg));
        */

        Bitmap bitMap = Bitmap.createBitmap(480, 800, Bitmap.Config.ARGB_8888);
        ll = (LinearLayout) findViewById(R.id.rect);
        ll.setBackgroundDrawable(new BitmapDrawable(bitMap));

        game = new Game(bitMap);
        // Build level 1
        LinkedList<Line> puzzle = new LinkedList<Line>();
        puzzle.addLast(new Line(new Posn(10, 10), new Posn(50, 50)));
        puzzle.addLast(new Line(new Posn(50, 50), new Posn(10, 90)));

        LinkedList<LinkedList<Line>> solns = new LinkedList<LinkedList<Line>>();
        LinkedList<Line> soln = new LinkedList<Line>();
        soln.addLast(new Line(new Posn(10, 10), new Posn(50, 50)));
        soln.addLast(new Line(new Posn(50, 50), new Posn(10, 90)));
        soln.addLast(new Line(new Posn(50, 50), new Posn(90, 50)));
        solns.addLast(soln);

        Level level = new Level(1, 1, puzzle, solns, 32767, 0, true, true, true, null);

        // Load level 1-1
        game.setLevel(level);

        ll.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    int touchX = Math.round(event.getX());
                    int touchY = Math.round(event.getY());
                    startPoint = new Posn(touchX, touchY);
                    Log.d("ACTION_DOWN", "("+startPoint.x()+","+startPoint.y()+")");
                    return true;
                }
                else if (event.getAction() == MotionEvent.ACTION_UP) {
                    int touchX = Math.round(event.getX());
                    int touchY = Math.round(event.getY());
                    endPoint = new Posn(touchX, touchY);
                    Log.d("ACTION_UP", "("+endPoint.x()+","+endPoint.y()+")");
                    final Game g = game;
                    g.drawLine(new Line(startPoint, endPoint, Owner.User));
                    ll.invalidate();
                    return true;
                }
                else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                    return  true;
                }
                return false;
            }
        });

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
