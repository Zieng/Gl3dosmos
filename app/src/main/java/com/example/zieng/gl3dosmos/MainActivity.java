package com.example.zieng.gl3dosmos;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.IllegalFormatCodePointException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends Activity {
    private int level = 1;
    private TextView start;
    private TextView best;
    private TextView about;
    private TextView help;

    private BackgroundSound BGM;
    MediaPlayer mp;
    PickerView pv;


    private static final String TAG = "LifeCycleActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        pv = (PickerView) findViewById(R.id.pv);
        pv.setVisibility(View.VISIBLE);

        List<String> levelList = new ArrayList<String>();

        for (int i = 1; i < 25; i++) {
            levelList.add(String.valueOf(i));
        }

        pv.setData(levelList);
        pv.setOnSelectListener(new PickerView.onSelectListener() {
            @Override
            public void onSelect(String text) {
//                Toast.makeText(MainActivity.this, "Level " + text,
//                        Toast.LENGTH_SHORT).show();

                int level = Integer.parseInt(text);
                Level.setLevel(level);
            }
        });



        
        start = (TextView)findViewById(R.id.start);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, OsmosActivity.class);
                startActivity(i);
            }
        });



        help = (TextView)findViewById(R.id.help);
        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "“在漆黑孤寂的宇宙中，你只能靠自己。”",
                        Toast.LENGTH_LONG).show();
            }
        });


        best = (TextView)findViewById(R.id.best);
        best.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Best Score: " + String.valueOf(Score.getBest()),
                        Toast.LENGTH_LONG).show();
            }
        });


        about = (TextView)findViewById(R.id.about);
        about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "“开发者：杨煜溟，李杰。\n项目地址：https://github.com/Zieng/Gl3dosmos”",
                        Toast.LENGTH_LONG).show();
                //startActivity(new Intent(MainActivity.this, AboutActivity.class));
            }
        });

        //        mp = MediaPlayer.create(this, R.raw.bg);
//        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//
//            @Override
//            public void onCompletion(MediaPlayer mp) {
//                // TODO Auto-generated method stub
//                mp.release();
//            }
//
//        });
//        mp.start();

        BGM = new BackgroundSound();

    }



    //Activity创建或者从后台重新回到前台时被调用
    @Override
    protected void onStart() {
        super.onStart();
        Log.e(TAG, "onStart called.");
    }

    //Activity从后台重新回到前台时被调用
    @Override
    protected void onRestart() {
        super.onRestart();
        Log.e(TAG, "onRestart called.");
    }



    //Activity窗口获得或失去焦点时被调用,在onResume之后或onPause之后
    /*@Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        Log.i(TAG, "onWindowFocusChanged called.");
    }*/


    //退出当前Activity或者跳转到新Activity时被调用
    @Override
    protected void onStop() {
        super.onStop();
        Log.e(TAG, "onStop called.");
    }

    //退出当前Activity时被调用,调用之后Activity就结束了
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "onDestory called.");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        Log.e(TAG, "onPause called.");
        BGM.cancel(true);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        Log.e(TAG, "onResume called.");
        try{
            BGM.execute();
        }
        catch (IllegalStateException ex)
        {
            Log.e("BGM","Cannot execute task");
        }

    }

    public class BackgroundSound extends AsyncTask<Void, Void, Void>
    {
        @Override
        protected Void doInBackground(Void... params)
        {
//            Log.e("BGM", "do in background");
//            int resID = OsmosActivity.this.getResources().getIdentifier("bg", "raw", OsmosActivity.this.getPackageName());
//            MediaPlayer player = MediaPlayer.create(OsmosActivity.this, resID);

            mp = MediaPlayer.create(MainActivity.this, R.raw.bg);
            mp.setLooping(true); // Set looping
            mp.setVolume(100, 100);
            mp.start();
            return null;
        }
    }
}
