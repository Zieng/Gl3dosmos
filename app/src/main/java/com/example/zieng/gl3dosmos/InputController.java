package com.example.zieng.gl3dosmos;

import android.content.Context;
import android.graphics.RectF;
import android.support.v4.view.MotionEventCompat;
import android.util.Log;
import android.view.MotionEvent;

import java.util.ArrayList;

import static android.opengl.Matrix.perspectiveM;
import static android.opengl.Matrix.setLookAtM;

/**
 * Created by zieng on 11/22/15.
 */
public class InputController
{
    boolean noChild = false ;

    private static final String TAG = "InputController";
    Context context;

    RectF thrust;
    RectF brake;
    RectF backwards;
    RectF pause;
    RectF dragArea;

    // record the delta value of drag
    float lastTouchX = 0,lastTouchY = 0;
    float dx,dy;
    boolean isClick;
    private final float SCROLL_THRESHOLD = 10;

    private float horizontalAngle;
    private float verticalAngle;
    private float angleSpeed;

    boolean thrustPressed = false;
    boolean backwardsPressed = false;
    boolean pausePressed = false;

    boolean fingerDrag = false;   // to avoid toggle buttons while drag the screen

    // 3 important directions
    Point3F right,face,up;

    float speed = 1 ;

    public InputController(Context context,int screenWidth,int screenHeight)
    {
//        Log.e(TAG,"constructor: width="+screenWidth+",heigth="+screenHeight);

        this.context = context;
        horizontalAngle = 3.14159f;
        verticalAngle = 0;
        angleSpeed = 0.001f;

        right = new Point3F();
        up = new Point3F();
        face = new Point3F();

        calculate_vector();


        float buttonWidth=screenWidth/8;
        float buttonHeight=screenHeight/7;
        float buttonPadding = screenWidth/200;

//        RectF rectF = new RectF(0,buttonWidth,);
//        thrust = new RectF();
        thrust=new RectF(buttonPadding,                               // left
                screenHeight-buttonHeight-buttonPadding,            // top
                buttonWidth + buttonPadding,                                        // right
                screenHeight-buttonPadding);                        // bottom

        brake = new RectF(buttonWidth+buttonPadding,
                screenHeight-buttonHeight-buttonPadding,
                buttonWidth+buttonPadding+buttonWidth,
                screenHeight-buttonPadding);

        backwards = new RectF(
                buttonWidth*2 + buttonPadding,
                screenHeight-buttonHeight-buttonPadding,
                buttonWidth*3+buttonPadding,
                screenHeight-buttonPadding
        );

        pause= new RectF(screenWidth-buttonPadding-buttonWidth,
                buttonPadding,
                screenWidth-buttonPadding,
                buttonPadding+buttonHeight);

//        left = new RectF(
//                screenWidth - (buttonWidth*3 + buttonPadding),
//                screenHeight-buttonHeight-buttonPadding,
//                screenWidth - ()
//
//        );




        dragArea = new RectF();
        dragArea.top = screenHeight * 0.3f;
        dragArea.left = screenWidth / 2.0f;
        dragArea.right = screenWidth;
        dragArea.bottom = screenHeight;
    }

    public ArrayList getButtons()
    {
        ArrayList<RectF> currentButtonList = new ArrayList<>();

        currentButtonList.add(thrust);
        currentButtonList.add(brake);
        currentButtonList.add(backwards);
        currentButtonList.add(pause);
//        currentButtonList.add(dragArea);

        return currentButtonList;
    }

    public void calculate_vector()
    {
        face.x =  (float)Math.cos(verticalAngle)*(float)Math.sin(horizontalAngle);
        face.y =  (float)Math.sin(verticalAngle);
        face.z =  (float)Math.cos(verticalAngle)*(float)Math.cos(horizontalAngle);

        right.x = (float)Math.sin(horizontalAngle - 1.571f);
        right.y = 0;
        right.z = (float)Math.cos(horizontalAngle - 1.571f);

        up.x =  - right.z * face.y;
        up.y = right.z*face.x - right.x * face.z;
        up.z = right.x * face.y;
    }

    public void handleInput(MotionEvent motionEvent,GameManager gm,SoundManager sm)
    {

        // TODO: 11/22/15 handle Input
        int pointerCount = motionEvent.getPointerCount();
        boolean togglePause = false;

//        Log.e(TAG,"pointerCount = "+pointerCount);

        for(int i=0;i<pointerCount;i++)
        {
            float x =  motionEvent.getX(i);
            float y = motionEvent.getY(i);

            switch (motionEvent.getAction() & MotionEvent.ACTION_MASK)
            {
                case MotionEvent.ACTION_DOWN:
                case MotionEvent.ACTION_POINTER_DOWN:
                {
                    lastTouchX = x;
                    lastTouchY = y;

                    if(thrust.contains(x,y) && gm.is_playing() )   // button thrust is pressed
                    {
                        // TODO: 11/25/15 thrust action
                        thrustPressed = true;
                        backwardsPressed = false;
//                        Log.e(TAG,"you click the thrust at ("+x+","+y+")");


//                        Log.e(TAG,"update velocity for thrust");
                        Point3F v = gm.player.get_velocity();

                        v.x += face.x * speed;
                        v.y += face.y * speed;
                        v.z += face.z * speed;

                        generate_child_planet(gm,true);

                        gm.player.set_velocity(v);

                        sm.playSound("thrust");

                    }
                    else if( brake.contains(x,y) && gm.is_playing() )
                    {
                        if( gm.player.get_velocity().get_length() <= 0.1)
                        {
                            gm.player.set_velocity( new Point3F(0,0,0));
                        }
                        else
                        {
                            Point3F v = gm.player.get_velocity();
                            v = v.multiply(0.9f);
                            gm.player.set_velocity(  v  );
                        }
                        sm.playSound("brake");
                    }
                    else if( backwards.contains(x,y) && gm.is_playing() )
                    {
                        //// TODO: 11/25/15 backwards action
                        thrustPressed = false;
                        backwardsPressed = true;
//                        Log.e(TAG, "you click the backwards at (" + x + "," + y + ")");


//                        Log.e(TAG,"update velocity for backwards");
                        Point3F v = gm.player.get_velocity();

                        v.x -= face.x * speed;
                        v.y -= face.y * speed;
                        v.z -= face.z * speed;

                        generate_child_planet(gm,false);

                        gm.player.set_velocity(v);
                        sm.playSound("thrust");

                    }
                    else if( pause.contains(x,y))
                    {
                        // TODO: 11/25/15 pause action
                        gm.switch_play_status();
                        thrustPressed = false;
                        backwardsPressed = false;
                        Log.e(TAG,"clicked pause, current state = "+ gm.is_playing() );

//                        Log.e(TAG,"hori-angle="+horizontalAngle);
//                        Log.e(TAG,"vert-angle="+verticalAngle);
                    }
                    break;
                }
                case MotionEvent.ACTION_MOVE:
                {
                    if( dragArea.contains(x,y) )
                    {
                        fingerDrag = true;
                        final float dx = x - lastTouchX;
                        final float dy = y - lastTouchY;

//                        Log.e(TAG, "Move from(" + lastTouchX + "," + lastTouchY + ") to (" + x + "," + y + ")");
//                        Log.e(TAG, "dx= " + dx + "\tdy= " + dy);

                        lastTouchX = x;
                        lastTouchY = y;

//                        Log.e(TAG,"previous h-angle="+horizontalAngle+"\t,v-angle="+verticalAngle);
                        //compute the view and projection matrix according the new angle
                        horizontalAngle -= angleSpeed * dx;
                        verticalAngle   -= angleSpeed * dy;
//                        Log.e(TAG,"subsequent h-angle="+horizontalAngle+"\t,v-angle="+verticalAngle);

                        calculate_vector();

                    }
                    else
                        fingerDrag = false;

                    break;
                }
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_POINTER_UP:
                {

                    thrustPressed = false;
                    backwardsPressed = false;

                    break;
                }
                default:
                    Log.e("undefined touch event","orz");
                    break;
            }

        }
//        Log.e(TAG,"handle a motion event finished");

    }

    public final Point3F getFace()
    {
        return face;
    }

    public final Point3F getRight()
    {
        return right;
    }

    public final Point3F getUp()
    {
        return up;
    }

    public void init_()
    {
        Log.e(TAG,"init finished");
    }

    public void generate_child_planet(GameManager gm, boolean isThrust)
    {
        if(gm.player.type != Planet.TYPE.PlayerStar || gm.is_playing() == false || noChild )
            return ;

        double r = gm.player.get_radius();
        if( r < 0.3 )
        {
            Log.e(TAG,"too small to generate child planet");
            return ;
        }

        Point3F v = new Point3F(  gm.player.get_velocity()  );
        Point3F pos = new Point3F( gm.player.getWorldLocation() );
        double volume = Math.pow( gm.player.get_radius() , 3 );

        r *= 0.3;
        v.multiply(2);

        gm.player.set_radius(      Math.pow( volume-Math.pow(r,3)  ,  1.0/3.0)     );
        double R = gm.player.get_radius();

        Planet child = new Planet(r, Planet.TYPE.NormalStar );
        Point3F offset = new Point3F(face.x, face.y , face.z);

        if( isThrust )
        {
            v = v.reverse();

            offset = offset.multiply((float) (R + r));
            pos = pos.subtract( offset );

        }
        else
        {
            offset = offset.multiply( (float) (R +r));
            pos = pos.add(offset);
        }

        child.setWorldLocation(pos);
        child.set_velocity(v);

        gm.stars.add(child);
    }

}
