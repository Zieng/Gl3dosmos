package com.example.zieng.gl3dosmos;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView.Renderer;
import android.util.Log;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glViewport;
import static android.opengl.Matrix.orthoM;

/**
 * Created by zieng on 11/22/15.
 */
public class OsmosRenderer implements Renderer
{
    boolean debugging = true;

    private long fps;
    long frameCounter = 0;  // for monitoringand controlling the frames per second
    long averageFPS = 0;

    private final float[] viewportMatrix = new float[16];   // for convert into opengl coordinate(-1.0~1.0)

    private GameManager gm;   // help manage current game states
    private SoundManager sm;
    private InputController ic;

    private final GameButton[] gameButtons =new GameButton[3];

    public OsmosRenderer(GameManager gameManager,SoundManager soundManager,InputController inputController)
    {
        // TODO: 11/23/15 OsmosRenderer

        gm = gameManager;
        sm = soundManager;
        ic = inputController;


        // debug
        Log.e("init AsteroidsRenderer:","screenWidth="+gm.screenWidth+",screenHeight="+gm.screenHeight);
    }

    @Override
    public void onSurfaceCreated(GL10 glUnused, EGLConfig config)
    {
        glClearColor(0.f,0.f,0.f,0.f);

        GLManager.buildProgram();  // load shader program

        GLES20.glEnable(GLES20.GL_TEXTURE_2D);
        // Active the texture unit 0
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);

        createObjects();
    }

    @Override
    public void onSurfaceChanged(GL10 glUnused,int width, int height)
    {
        glViewport(0, 0, width, height);   //  make full screen

        orthoM(viewportMatrix, 0, 0, gm.metresToShowX, 0, gm.metresToShowY, 0f, 1f);
    }

    @Override
    public void onDrawFrame(GL10 glUnused)
    {
        long startFrameTime = System.currentTimeMillis();

        if( gm.is_playing() )
            update(fps);

        draw();

        long timeThisFrame = System.currentTimeMillis() - startFrameTime;
        if(timeThisFrame >= 1)
            fps = 1000/timeThisFrame;
        if(debugging)
        {
            frameCounter++;
            averageFPS += fps;
            if(frameCounter > 100)
            {
                averageFPS /= frameCounter;
                frameCounter = 0;
//                Log.e("averageFPS:", "" + averageFPS + ",ship.x=" + gm.ship.getWorldLocation().x + ",ship.y=" + gm.ship.getWorldLocation().y);
            }
        }
    }

    public void createObjects()
    {

    }

    // TODO: 11/23/15 update function
    private void update(long fps)
    {

    }

    // TODO: 11/23/15 Renderer draw() function
    private void draw()
    {



        glClear(GL_COLOR_BUFFER_BIT);

        //draw the ship

    }
}
