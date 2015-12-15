package com.example.zieng.gl3dosmos;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.opengl.GLSurfaceView;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import java.util.logging.Handler;
import java.util.logging.LogRecord;

//// TODO: 11/22/15 if AppComatActivity not working, change it to Activity
// origin is AppCompatActivity
public class OsmosActivity extends Activity
{

    private GLSurfaceView osmosView;
    int level;
    public Intent i;



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);


//        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
//        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 20, 0);

        //i = getIntent();
        //level = i.getExtras().getInt("level");
        level = Level.getLevel();
        Score.setScore(0);


        // get a display object to access screen detail
        final Display display = getWindowManager().getDefaultDisplay();

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
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        osmosView.onResume();
    }



}
