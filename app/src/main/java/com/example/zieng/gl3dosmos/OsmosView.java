package com.example.zieng.gl3dosmos;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.Log;
import android.view.MotionEvent;

/**
 * Created by zieng on 11/22/15.
 */

public class OsmosView extends GLSurfaceView
{
    GameManager gm;
    SoundManager sm;
    InputController ic;

    public OsmosView(Context context, int screenX, int screenY )
    {
        super(context);

        gm = new GameManager(screenX,screenY);
        sm = new SoundManager();
        sm.loadSound(context);
        ic = new InputController(screenX,screenY);

        setEGLContextClientVersion(2);
        setRenderer(new OsmosRenderer(gm,sm,ic));

        //debug info
        Log.e("Init info","Osmosview created based on screenX="+screenX+",screenY="+screenY);
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent)
    {
        ic.handleInput(motionEvent,gm,sm);
        return true;
    }

}
