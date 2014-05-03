package com.example.symbolize.app;

import android.app.Activity;

import android.os.Bundle;

import android.view.Menu;
import android.view.MenuItem;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;

import android.widget.LinearLayout;


public class MainActivity extends Activity {

    private Bitmap bg = Bitmap.createBitmap(480, 800, Bitmap.Config.ARGB_8888);
    private Canvas canvas = new Canvas(bg);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Paint paint = new Paint();
        paint.setColor(Color.RED);
        LinearLayout ll = (LinearLayout) findViewById(R.id.rect);
        ll.setBackgroundDrawable(new BitmapDrawable(bg));

        canvas.drawRect(50, 50, 200, 200, paint);
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
