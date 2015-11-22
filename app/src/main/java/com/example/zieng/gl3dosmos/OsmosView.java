package com.example.zieng.gl3dosmos;

import android.content.Context;
import android.opengl.GLSurfaceView;

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

        ic = new InputController(screenX,screenY);

    }
}
