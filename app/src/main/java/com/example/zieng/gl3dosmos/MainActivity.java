package com.example.zieng.gl3dosmos;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.IllegalFormatCodePointException;

public class MainActivity extends Activity {
    private int level = 1;
    private TextView start;
    private TextView option;
    private TextView help;
    private BackgroundSound BGM;
    MediaPlayer mp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        start = (TextView)findViewById(R.id.start);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, OsmosActivity.class);
                i.putExtra("level", level);
                System.out.println("on start button");
                startActivityForResult(i, 1);
            }
        });

        option = (TextView)findViewById(R.id.option);
        option.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, OptionActivity.class);
                startActivityForResult(i, 3);
            }
        });

        help = (TextView)findViewById(R.id.help);
        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, HelpActivity.class));
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

    @Override protected void onActivityResult(int requestCode,int resultCode,Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        switch (requestCode){
            case 3:
                level = data.getExtras().getInt("level");
                System.out.println(level);
                break;
        }
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
        BGM.cancel(true);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
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
