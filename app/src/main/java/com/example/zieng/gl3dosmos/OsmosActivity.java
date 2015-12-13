package com.example.zieng.gl3dosmos;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.opengl.GLSurfaceView;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

//// TODO: 11/22/15 if AppComatActivity not working, change it to Activity
// origin is AppCompatActivity
public class OsmosActivity extends Activity
{

    private GLSurfaceView osmosView;
    int level;
    public Intent i;
    private BackgroundSound BGM;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        BGM = new BackgroundSound();
//        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
//        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 20, 0);

        i = getIntent();
        level = i.getExtras().getInt("level");

        // get a display object to access screen detail
        Display display = getWindowManager().getDefaultDisplay();

        //load the resolution into a point object
        Point resolution = new Point();
        display.getSize(resolution);

        osmosView = new OsmosView(this,resolution.x,resolution.y, level);
        setContentView(osmosView);

//        osmosView.setBackgroundResource(R.mipmap.repulsive);
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        osmosView.onPause();
        BGM.cancel(true);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        osmosView.onResume();
        BGM.execute();
    }

    public class BackgroundSound extends AsyncTask<Void, Void, Void>
    {
        @Override
        protected Void doInBackground(Void... params)
        {
            Log.e("BGM", "do in background");
            int resID = OsmosActivity.this.getResources().getIdentifier("bg", "raw", OsmosActivity.this.getPackageName());
            MediaPlayer player = MediaPlayer.create(OsmosActivity.this, resID);
//            MediaPlayer player = MediaPlayer.create(OsmosActivity.this, R.raw.bg);
            player.setLooping(true); // Set looping
            player.setVolume(100,100);
            player.start();
            return null;
        }
    }

}
