package com.example.zieng.gl3dosmos;

import android.util.Log;

/**
 * Created by zieng on 11/22/15.
 */
public class GameManager
{
    int worldMaxX = 50;
    int worldMaxY = 50;
    int worldMaxZ = 50;
    int worldMinX = -50;
    int worldMinY = -50;
    int worldMinZ = -50;

    int screenWidth,screenHeight;
    int metresToShowX = 390;
    int metresToShowY = 220;

    int starNum;
    Planet player;
    Planet [] stars;

    private boolean playing = false;

    public GameManager(int screenWidth, int screenHeight)
    {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;

        // TODO: 11/22/15 GameObject constructor

        //debug info
        Log.e("GameManager constructor","create GameMangager based on screenWidth="+screenWidth+",screenHeight="+screenHeight);
    }

    public void switch_play_status()
    {
        playing = ! playing;
    }

    public boolean is_playing()
    {
        return playing;
    }

}
