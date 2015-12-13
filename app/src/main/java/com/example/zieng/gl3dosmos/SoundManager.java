package com.example.zieng.gl3dosmos;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.Log;

import java.io.IOException;

/**
 * Created by zieng on 11/22/15.
 */
public class SoundManager
{
    private static final String TAG = "SoundManager";
    Context context;
    private SoundPool soundPool;

    private int thrust = -1;
    private int absorption = -1;
    private int win = -1;
    private int gameover = -1;
    private int bounce = -1;
    private int brake = -1;

    public SoundManager(Context context)
    {
        this.context = context;
    }
    // TODO: 11/22/15 add sound files
    public void loadSound()
    {
        soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC,0);
        try
        {
            AssetManager assetManager = context.getAssets();
            AssetFileDescriptor descriptor;

            descriptor = assetManager.openFd("thrust.wav");    //create fx
            thrust = soundPool.load(descriptor, 0);

            descriptor = assetManager.openFd("thrust.wav");
            thrust = soundPool.load(descriptor, 0);

            descriptor=assetManager.openFd("absorption.wav");
            absorption = soundPool.load(descriptor, 0);

            descriptor=assetManager.openFd("bounce.wav");
            bounce=soundPool.load(descriptor, 0);

            descriptor=assetManager.openFd("brake.wav");
            brake=soundPool.load(descriptor, 0);

            descriptor=assetManager.openFd("win.wav");
            win=soundPool.load(descriptor, 0);

            descriptor=assetManager.openFd("gameover.wav");
            gameover=soundPool.load(descriptor,0);


        }
        catch (IOException e)
        {
            Log.e("error", "failed to load sound files");
        }
    }

    public void playSound(String sound)
    {
        switch (sound)
        {
            case "thrust":
                soundPool.play(thrust,1,1,0,0,1);
                break;
            case "absorption":
                soundPool.play(absorption,1,1,0,0,1);
                break;
            case "win":
                soundPool.play(win,1,1,0,0,1);
                break;
            case "gameover":
                soundPool.play(gameover,1,1,0,0,1);
                break;
            case "bounce":
                soundPool.play(bounce,1,1,0,0,1);
                break;
            case "brake":
                soundPool.play(brake,1,1,0,0,1);
                break;
        }
    }

    public void init_()
    {
        Log.e(TAG,"init finished");
    }
}
