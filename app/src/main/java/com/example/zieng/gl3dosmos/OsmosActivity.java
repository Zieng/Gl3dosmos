package com.example.zieng.gl3dosmos;

import android.graphics.Point;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Display;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

//// TODO: 11/22/15 if AppComatActivity not working, change it to Activity
public class OsmosActivity extends AppCompatActivity
{

    private GLSurfaceView osmosView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);


        // get a display object to access screen detail
        Display display = getWindowManager().getDefaultDisplay();

        //load the resolution into a point object
        Point resolution = new Point();
        display.getSize(resolution);

        osmosView = new OsmosView(this,resolution.x,resolution.y);
        setContentView(osmosView);
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
