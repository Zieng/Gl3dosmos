package com.example.zieng.gl3dosmos;

import android.graphics.Rect;
import android.view.MotionEvent;

import java.util.ArrayList;

/**
 * Created by zieng on 11/22/15.
 */
public class InputController
{
    Rect thrust;
    Rect brake;
    Rect pause;

    public InputController(int screenWidth,int screenHeight)
    {
        int buttonWidth=screenWidth/8;
        int buttonHeight=screenHeight/7;
        int buttonPadding = screenWidth/80;

        thrust=new Rect(buttonPadding,
                screenHeight-buttonHeight-buttonPadding,
                buttonWidth,
                screenHeight-buttonPadding);
        brake= new Rect(buttonWidth+buttonPadding,
                screenHeight-buttonHeight-buttonPadding,
                buttonWidth+buttonPadding+buttonWidth,
                screenHeight-buttonPadding);

        pause= new Rect(screenWidth-buttonPadding-buttonWidth,
                buttonPadding,
                screenWidth-buttonPadding,
                buttonPadding+buttonHeight);
    }

    public ArrayList getButtons()
    {
        ArrayList<Rect> currentButtonList = new ArrayList<>();
        currentButtonList.add(thrust);
        currentButtonList.add(brake);
        currentButtonList.add(pause);

        return currentButtonList;
    }

    public void handleInput(MotionEvent motionEvent,GameManager gm,SoundManager sm)
    {
        // TODO: 11/22/15 handle Input
    }


}
