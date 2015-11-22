package com.example.zieng.gl3dosmos;

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

    int screenX,screenY;

    int starNum;
    Planet player;
    Planet [] stars;

    private boolean playing = false;

    public GameManager(int xx, int yy)
    {
        screenX = xx;
        screenY = yy;

        // TODO: 11/22/15 GameObject constructor
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
