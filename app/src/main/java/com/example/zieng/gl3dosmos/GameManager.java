package com.example.zieng.gl3dosmos;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by zieng on 11/22/15.
 */
public class GameManager
{
    private static final String TAG = "GameManager";
    Context context;
    float worldMaxX = 50;
    float worldMaxY = 50;
    float worldMaxZ = 50;
    float worldMinX = -50;
    float worldMinY = -50;
    float worldMinZ = -50;

    int screenWidth,screenHeight;
    int metresToShowX = 390;
    int metresToShowY = 220;

    int remainingEnemies;
    int starNum;
    Planet player;
    ArrayList<Planet> stars;
    WorldBorder WB;

    private boolean playing = false;
    boolean gameOver = false;
    boolean gameWin = false;

    // for render all game objects
    float[] viewportMatrix = new float[16];
    float[] projectionMatrix = new float[16];

    public GameManager(Context context,int screenWidth, int screenHeight)
    {
        this.context = context;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;


        //debug info
        Log.i("GameManager constructor","create GameMangager based on screenWidth="+screenWidth+",screenHeight="+screenHeight);
    }

    public void switch_play_status()
    {
        playing = ! playing;
    }

    public boolean is_playing()
    {
        return playing;
    }

    public void set_playing_status(boolean isPlaying)
    {
        this.playing = isPlaying;
    }

    public void init_(float worldScaleX,float worldScaleY,float worldScaleZ)
    {
        worldMaxX = worldScaleX;
        worldMinX = -worldScaleX;

        worldMaxY = worldScaleY;
        worldMinY = -worldScaleY;

        worldMaxZ = worldScaleZ;
        worldMinZ = -worldScaleZ;

        WB = new WorldBorder(worldScaleX,worldScaleY,worldScaleZ);

        Planet.init_planet(context);
        Log.e(TAG, "init finished");
    }

    public void set_projection_matrix(final float [] projectionMatrix)
    {
        this.projectionMatrix = projectionMatrix;
    }

    public void set_viewpor_matrix(final float [] viewportMatrix)
    {
        this.viewportMatrix = viewportMatrix;
    }




}
