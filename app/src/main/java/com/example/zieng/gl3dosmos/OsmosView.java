package com.example.zieng.gl3dosmos;

import android.content.Context;
import android.content.Intent;
import android.opengl.GLSurfaceView;
import android.util.Log;
import android.view.MotionEvent;

import java.util.logging.Handler;

/**
 * Created by zieng on 11/22/15.
 */

public class OsmosView extends GLSurfaceView
{
    GameManager gm;
    SoundManager sm;
    InputController ic;

    public OsmosView(Context context, int screenX, int screenY , int level)
    {
        super(context);



        gm = new GameManager(context,screenX,screenY);
        sm = new SoundManager(context);
        sm.loadSound();
        ic = new InputController(context,screenX,screenY);



        setEGLContextClientVersion(2);
        setRenderer(new OsmosRenderer(context, gm, sm, ic,level));

//        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);

//        gm.init_(context);
        //debug info
        long origThreadID = Thread.currentThread().getId();
        Log.e("\tOsmosView::","thread id = "+origThreadID);
        Log.i("Init info", "Osmosview created based on screenX=" + screenX + ",screenY=" + screenY);

    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent)
    {
        ic.handleInput(motionEvent,gm,sm);
        return true;
    }

}
