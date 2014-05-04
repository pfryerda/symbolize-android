package symbolize.app;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;

import java.util.LinkedList;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*
        Paint paint = new Paint();
        paint.setColor(Color.RED);
        LinearLayout ll = (LinearLayout) findViewById(R.id.rect);
        ll.setBackgroundDrawable(new BitmapDrawable(bg));

        canvas.drawRect(50, 50, 200, 200, paint);
         */

        Bitmap bitMap = Bitmap.createBitmap(480, 800, Bitmap.Config.ARGB_8888);
        LinearLayout ll = (LinearLayout) findViewById(R.id.rect);
        ll.setBackgroundDrawable(new BitmapDrawable(bitMap));

        Game game = new Game(bitMap);
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

        Level level = new Level(1, 1, puzzle, solns, 3, 0, true, true, true, null);

        // Load level 1-1
        game.setLevel(level);
        game.drawLine(new Line(new Posn(50, 50), new Posn(90, 50), Owner.User));	// Draw line

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
