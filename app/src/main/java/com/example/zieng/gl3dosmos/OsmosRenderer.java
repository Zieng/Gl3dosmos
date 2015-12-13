package com.example.zieng.gl3dosmos;

import android.content.Context;
import android.graphics.RectF;
import android.opengl.GLSurfaceView.Renderer;
import android.util.Log;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.GL_DEPTH_TEST;
import static android.opengl.GLES20.GL_LESS;
import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_DEPTH_BUFFER_BIT;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glDepthFunc;
import static android.opengl.GLES20.glDrawElements;
import static android.opengl.GLES20.glViewport;
import static android.opengl.Matrix.perspectiveM;
import static android.opengl.Matrix.setLookAtM;

import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.glEnable;
import static android.opengl.GLES20.glUniform4iv;
import static android.opengl.GLES20.glUniformMatrix4fv;
import static android.opengl.GLES20.glVertexAttribPointer;
import static android.opengl.Matrix.translateM;

/**
 * Created by zieng on 11/22/15.
 */

public class OsmosRenderer implements Renderer
{
    private final static String TAG = "OsmosRenderer";

    final double G_CONST = 0.67f;
    Context context;

    boolean debugging = true;

    float speed = 1;
    private long fps;
    long frameCounter = 0;  // for monitoringand controlling the frames per second
    long averageFPS = 0;

    float[] viewportMatrix = new float[16];   // for convert into opengl coordinate(-1.0~1.0)
    float[] projectionMatrix = new float[16];

    Point3F lightPos = new Point3F();

    float viewDistance = 10.0f;

    private GameManager gm;   // help manage current game states
    private SoundManager sm;
    private InputController ic;


    private final GameButton[] gameButtons =new GameButton[4];

    float eyeX = 0 ;
    float eyeY = 0 ;
    float eyeZ = 0 ;

    float centerX = 0 ;
    float centerY = 0 ;
    float centerZ = 0 ;

    double lastCollisionTime;

    boolean inChaos = false;
    int gameLevel = 1;

    boolean isBiggest = false;
    double maxVolume = 0;

    // game type
    boolean becomeBiggest = false;
    boolean destroyEnemies = false;

    public OsmosRenderer(Context ctx,GameManager gameManager,SoundManager soundManager,InputController inputController, int level)
    {

        // TODO: 11/23/15 OsmosRenderer
        context = ctx;
        gm = gameManager;
        sm = soundManager;
        ic = inputController;
        gameLevel = level;

        // debug
        long origThreadID = Thread.currentThread().getId();
        Log.e("\tOsmosRenderer::", "thread id = " + origThreadID);

//        Log.e("init OsmosRenderer:", "screenWidth=" + gm.screenWidth + ",screenHeight=" + gm.screenHeight);
    }

    @Override
    public void onSurfaceCreated(GL10 glUnused, EGLConfig config)
    {
        glClearColor(0.f, 0.f, 0.f, 0.f);

//        Log.e("read vertex shader", LoadHelper.readTextFileFromRawResource(context, R.raw.vertex));
//        Log.e("read fragment shader", LoadHelper.readTextFileFromRawResource(context, R.raw.fragment));

        GLManager.buildProgram(
                LoadHelper.readTextFileFromRawResource(context, R.raw.vertex),
                LoadHelper.readTextFileFromRawResource(context, R.raw.fragment)
        );  // load shader program

        glEnable(GL_TEXTURE_2D);
        glEnable(GL_DEPTH_TEST);
        glDepthFunc(GL_LESS);
//        glEnable(GL_CULL_FACE);

        gm.init_(10,10,10);
        sm.init_();
        ic.init_();

        createObjects();

        lastCollisionTime = System.currentTimeMillis();
//        Log.e(TAG,"crashed after create objects??");
    }

    @Override
    public void onSurfaceChanged(GL10 glUnused,int width, int height)
    {
        glViewport(0, 0, width, height);   //  make full screen

//        orthoM(viewportMatrix, 0, 0, gm.metresToShowX, 0, gm.metresToShowY, 0f, 1f);

//        computeMatrix();


    }

    @Override
    public void onDrawFrame(GL10 glUnused)
    {
        long startFrameTime = System.currentTimeMillis();

        // compute the matrix
        computeMatrix();

        Iterator iter = gm.stars.iterator();
        while( iter.hasNext() )
        {
            if ( ((Planet)iter.next()).isActive == false )
            {
                iter.remove();
                gm.remainingEnemies--;
            }
        }

        if( gm.player.isActive == false)
        {
            gm.gameOver = true;

            Log.e(TAG,"You lose!");
            // TODO: 11/29/15 handle Game over
        }
        else if( gm.remainingEnemies == 0 && destroyEnemies )
        {
            gm.gameWin = true;


            Log.e(TAG,"You win! destroy all enemies ");
            //// TODO: 11/29/15 handle Game Win

        }
        else if ( isBiggest && becomeBiggest )
        {
            gm.gameWin = true;

            Log.e(TAG,"You win! you becomes the isBiggest!");
            //// TODO: 12/13/15 handle game win
        }


        if( gm.is_playing() )
        {
            // apply field effect
            for(int i= 0;i<gm.stars.size();i++)
            {
                field_effect(gm.stars.get(i), gm.player);
                for(int j=i+1;j<gm.stars.size();j++)
                {
                    field_effect(gm.stars.get(i),gm.stars.get(j));
                }
            }

            /*check collision*/

            //check with border
            inside_world(gm.player);
            for(int i=0;i<gm.stars.size();i++)
            {
                inside_world(gm.stars.get(i));
            }
            // check between planets
            for( int i= 0;i<gm.stars.size();i++)
            {
                if( gm.stars.get(i).check_collision(gm.player) )
                {
                    handle_collision(gm.stars.get(i), gm.player);
                }

                for(int j=i+1; j<gm.stars.size();j++)
                {
                    if(  gm.stars.get(i).check_collision(gm.stars.get(j)))
                    {
                        handle_collision(gm.stars.get(i), gm.stars.get(j));
                    }
                }
            }

            //update position
            update(fps);
        }



        // draw all
        draw();

        long timeThisFrame = System.currentTimeMillis() - startFrameTime;
        if(timeThisFrame >= 1)
            fps = 1000/timeThisFrame;

//        Log.e(TAG,",player at (" + gm.player.getWorldLocation().x + "," + gm.player.getWorldLocation().y+") with radius="+gm.player.get_radius());

        if(debugging)
        {
//            Log.e(TAG+frameCounter,"")
            frameCounter++;
            averageFPS += fps;
            if(frameCounter > 100)
            {
                averageFPS /= frameCounter;
                frameCounter = 0;
//                Log.i("averageFPS:", "" + averageFPS + ",player at (" + gm.player.getWorldLocation().x + "," + gm.player.getWorldLocation().y+")");
            }
        }
    }

    public void createObjects()
    {
        ArrayList<RectF> buttonsToDraw = ic.getButtons();

        // create buttons
        int i=0;
        for(RectF rect:buttonsToDraw)
        {
            Log.e(TAG,"create a button:top="+rect.top+",left="+rect.left+",bottom="+rect.bottom+",right="+rect.right);
            gameButtons[i] = new GameButton(gm,rect.top,rect.left,rect.bottom,rect.right);
            i++;
        }

        // create player
        gm.player = new Planet(1, Planet.TYPE.PlayerStar);
        gm.player.setWorldLocation(8, 0, 0);

        Random r = new Random();

        // create other planets
        gm.stars = new ArrayList<>(gm.starNum);
        Planet temp;

        //debug
//        gameLevel = 24 ;

        if(gameLevel <=12 )
            destroyEnemies = true;
        else if ( gameLevel >12 && gameLevel <= 24)
            becomeBiggest = true;

        ic.noChild = !becomeBiggest;

        switch (gameLevel)
        {
            case 1:case 13:
            {
                            /*    get to know how to play      */
                gm.remainingEnemies = 1;
                temp = new Planet(0.9, Planet.TYPE.NormalStar);
                temp.setWorldLocation(0,0,0);
                gm.stars.add(temp);
                break;
            }
            case 2:case 14:
            {
                            /*    eat others      */
                gm.remainingEnemies = 3;
                for(i = 0;i<gm.remainingEnemies;i++)
                {
                    boolean noCollision = true;
                    temp = new Planet(0.9, Planet.TYPE.NormalStar);
                    float xx = (r.nextBoolean())?r.nextInt(10):-r.nextInt(10);
                    float yy = (r.nextBoolean())?r.nextInt(10):-r.nextInt(10);
                    float zz = (r.nextBoolean())?r.nextInt(10):-r.nextInt(10);

                    temp.setWorldLocation(xx,yy,zz);
                    if( gm.player.check_collision(temp))
                    {
                        noCollision = false;
                    }
                    for(int j=0;j<gm.stars.size();j++)
                    {
                        if(noCollision == false)
                            break;
                        if( gm.stars.get(j).check_collision(temp))
                        {
                            noCollision = false;
                            break;
                        }
                    }
                    if(noCollision == false)
                    {
                        i--;
                        continue;
                    }
                    gm.stars.add(temp);
                }
                break;
            }
            case 3:case 15:
            {
                /* catch the moving planet  */
                gm.remainingEnemies = 1;
                temp = new Planet(0.9, Planet.TYPE.NormalStar);
                temp.setWorldLocation(5,5,0);
                temp.set_velocity(1, 1, -1);
                gm.stars.add(temp);
                break;
            }
            case 4:case 16:
            {
                gm.remainingEnemies = 1;
            /*    gravity      */
                temp = new Planet(2, Planet.TYPE.CenterStar);
                gm.stars.add(temp);

                temp = new Planet(0.9, Planet.TYPE.NormalStar);
                temp.setWorldLocation(6,4,0);
                gm.stars.add(temp);
                break;
            }
            case 5:case 17:
            {
                gm.remainingEnemies = 10;
                for(i = 0;i<gm.remainingEnemies;i++)
                {
                    boolean noCollision = true;
                    temp = new Planet(0.9, Planet.TYPE.NormalStar);
                    float xx = (r.nextBoolean())?r.nextInt(10):-r.nextInt(10);
                    float yy = (r.nextBoolean())?r.nextInt(10):-r.nextInt(10);
                    float zz = (r.nextBoolean())?r.nextInt(10):-r.nextInt(10);

                    temp.setWorldLocation(xx,yy,zz);
                    temp.set_velocity(xx,yy,zz);
                    if( gm.player.check_collision(temp))
                    {
                        noCollision = false;
                    }
                    for(int j=0;j<gm.stars.size();j++)
                    {
                        if(noCollision == false)
                            break;
                        if( gm.stars.get(j).check_collision(temp))
                        {
                            noCollision = false;
                            break;
                        }
                    }
                    if(noCollision == false)
                    {
                        i--;
                        continue;
                    }
                    gm.stars.add(temp);
                }
                break;
            }
            case 6:case 18:
            {
                /*    repulsive     */
                gm.remainingEnemies = 1;
                temp = new Planet(0.9, Planet.TYPE.RepulsiveStar);
                gm.stars.add(temp);
                break;
            }
            case 7:case 19:
            {
                temp = new Planet(2, Planet.TYPE.CenterStar);
                gm.stars.add(temp);

                gm.remainingEnemies = 3;
                for(i = 0;i<gm.remainingEnemies;i++)
                {
                    boolean noCollision = true;
                    temp = new Planet(0.9, Planet.TYPE.NormalStar);
                    float xx = (r.nextBoolean())?r.nextInt(10):-r.nextInt(10);
                    float yy = (r.nextBoolean())?r.nextInt(10):-r.nextInt(10);
                    float zz = (r.nextBoolean())?r.nextInt(10):-r.nextInt(10);

                    temp.setWorldLocation(xx,yy,zz);
                    if( gm.player.check_collision(temp))
                    {
                        noCollision = false;
                    }
                    for(int j=0;j<gm.stars.size();j++)
                    {
                        if(noCollision == false)
                            break;
                        if( gm.stars.get(j).check_collision(temp))
                        {
                            noCollision = false;
                            break;
                        }
                    }
                    if(noCollision == false)
                    {
                        i--;
                        continue;
                    }
                    gm.stars.add(temp);
                }
                break;
            }
            case 8:case 20:
            {
                gm.remainingEnemies = 4;
                for(i = 0;i<gm.remainingEnemies;i++)
                {
                    boolean noCollision = true;
                    if( i==0 )
                        temp = new Planet(0.9, Planet.TYPE.RepulsiveStar);
                    else
                        temp = new Planet(0.9, Planet.TYPE.NormalStar);
                    float xx = (r.nextBoolean())?r.nextInt(10):-r.nextInt(10);
                    float yy = (r.nextBoolean())?r.nextInt(10):-r.nextInt(10);
                    float zz = (r.nextBoolean())?r.nextInt(10):-r.nextInt(10);

                    temp.setWorldLocation(xx,yy,zz);
                    if( gm.player.check_collision(temp))
                    {
                        noCollision = false;
                    }
                    for(int j=0;j<gm.stars.size();j++)
                    {
                        if(noCollision == false)
                            break;
                        if( gm.stars.get(j).check_collision(temp))
                        {
                            noCollision = false;
                            break;
                        }
                    }
                    if(noCollision == false)
                    {
                        i--;
                        continue;
                    }
                    gm.stars.add(temp);
                }
                break;
            }
            case 9:case 21:
            {
                gm.remainingEnemies = 10;
                for(i = 0;i<gm.remainingEnemies;i++)
                {
                    boolean noCollision = true;
                    if( i<5 )
                        temp = new Planet(0.9, Planet.TYPE.RepulsiveStar);
                    else
                        temp = new Planet(0.9, Planet.TYPE.NormalStar);
                    float xx = (r.nextBoolean())?r.nextInt(10):-r.nextInt(10);
                    float yy = (r.nextBoolean())?r.nextInt(10):-r.nextInt(10);
                    float zz = (r.nextBoolean())?r.nextInt(10):-r.nextInt(10);

                    temp.setWorldLocation(xx,yy,zz);
                    if( gm.player.check_collision(temp))
                    {
                        noCollision = false;
                    }
                    for(int j=0;j<gm.stars.size();j++)
                    {
                        if(noCollision == false)
                            break;
                        if( gm.stars.get(j).check_collision(temp))
                        {
                            noCollision = false;
                            break;
                        }
                    }
                    if(noCollision == false)
                    {
                        i--;
                        continue;
                    }
                    gm.stars.add(temp);
                }
                break;
            }
            case 10:case 22:
            {
                temp = new Planet(1.2, Planet.TYPE.CenterStar);
                gm.stars.add(temp);

                gm.remainingEnemies = 10;
                for(i = 0;i<gm.remainingEnemies;i++)
                {
                    boolean noCollision = true;
                    if( i<3 )
                        temp = new Planet(0.9, Planet.TYPE.RepulsiveStar);
                    else
                        temp = new Planet(0.9, Planet.TYPE.NormalStar);
                    float xx = (r.nextBoolean())?r.nextInt(10):-r.nextInt(10);
                    float yy = (r.nextBoolean())?r.nextInt(10):-r.nextInt(10);
                    float zz = (r.nextBoolean())?r.nextInt(10):-r.nextInt(10);

                    temp.setWorldLocation(xx,yy,zz);
                    if( gm.player.check_collision(temp))
                    {
                        noCollision = false;
                    }
                    for(int j=0;j<gm.stars.size();j++)
                    {
                        if(noCollision == false)
                            break;
                        if( gm.stars.get(j).check_collision(temp))
                        {
                            noCollision = false;
                            break;
                        }
                    }
                    if(noCollision == false)
                    {
                        i--;
                        continue;
                    }
                    gm.stars.add(temp);
                }
            }
            case 11:case 23:
            {
                gm.remainingEnemies = 10;
                for(i = 0;i<gm.remainingEnemies;i++)
                {
                    boolean noCollision = true;
                    temp = new Planet(0.9, Planet.TYPE.RepulsiveStar);
                    float xx = (r.nextBoolean())?r.nextInt(10):-r.nextInt(10);
                    float yy = (r.nextBoolean())?r.nextInt(10):-r.nextInt(10);
                    float zz = (r.nextBoolean())?r.nextInt(10):-r.nextInt(10);

                    temp.setWorldLocation(xx,yy,zz);
                    if( gm.player.check_collision(temp))
                    {
                        noCollision = false;
                    }
                    for(int j=0;j<gm.stars.size();j++)
                    {
                        if(noCollision == false)
                            break;
                        if( gm.stars.get(j).check_collision(temp))
                        {
                            noCollision = false;
                            break;
                        }
                    }
                    if(noCollision == false)
                    {
                        i--;
                        continue;
                    }
                    gm.stars.add(temp);
                }
                break;
            }
            case 12:case 24:
            {
                gm.remainingEnemies = 10;
                for(i = 0;i<gm.remainingEnemies;i++)
                {
                    boolean noCollision = true;
                    temp = new Planet(r.nextInt(10)/5.f, Planet.TYPE.NormalStar);
                    float xx = (r.nextBoolean())?r.nextInt(10):-r.nextInt(10);
                    float yy = (r.nextBoolean())?r.nextInt(10):-r.nextInt(10);
                    float zz = (r.nextBoolean())?r.nextInt(10):-r.nextInt(10);

                    temp.setWorldLocation(xx,yy,zz);
                    temp.set_velocity(xx,yy,zz);

                    if( gm.player.check_collision(temp))
                    {
                        noCollision = false;
                    }
                    for(int j=0;j<gm.stars.size();j++)
                    {
                        if(noCollision == false)
                            break;
                        if( gm.stars.get(j).check_collision(temp))
                        {
                            noCollision = false;
                            break;
                        }
                    }
                    if(noCollision == false)
                    {
                        i--;
                        continue;
                    }
                    gm.stars.add(temp);
                }
                break;
            }

        }


    }

    public void inside_world(Planet planet)
    {
        Point3F v = planet.get_velocity();
        Point3F t = planet.getWorldLocation();
        float r = (float) planet.get_radius();
        float offset = 0.0001f;   // to avoid planet stuck at the boarder
        boolean changed = false;

        if( t.x < gm.worldMinX + r )
        {
            t.x = gm.worldMinX  + r + offset;
            v.x = -v.x;
            changed = true;
        }
        if( t.y < gm.worldMinY + r)
        {
            t.y = gm.worldMinY + r + offset;
            v.y = -v.y;
            changed = true;
        }
        if( t.z < gm.worldMinZ + r)
        {
            t.z = gm.worldMinZ + r + offset;
            v.z = -v.z;
            changed = true;
        }
        if( t.x > gm.worldMaxX - r)
        {
            t.x = gm.worldMaxX - r -offset;
            v.x = -v.x;
            changed = true;
        }
        if( t.y > gm.worldMaxY - r)
        {
            t.y = gm.worldMaxY - r -offset;
            v.y = -v.y;
            changed = true;
        }
        if( t.z > gm.worldMaxZ - r) {
            t.z = gm.worldMaxZ - r -offset;
            v.z = -v.z;
            changed = true;
        }

        if(changed)
        {
            Log.e(TAG,"Reach the border!!!! Bounce!!!\t"+t.toString());
            planet.set_velocity(v);
            planet.setWorldLocation(t.x,t.y,t.z);
        }
    }

    public void handle_collision(Planet p1, Planet p2)
    {

        Log.e(TAG,"----------------Handle Collision----------------");

        double currentCollisionTIme = System.currentTimeMillis();

        if(p1.isActive== false || p2.isActive== false)
            return ;

        boolean flag = p1.get_radius()>=p2.get_radius();

        Planet bigPlanet = flag?p1:p2;
        Planet smallPlanet = flag?p2:p1;

        //todo handle collision
        double deltaTime = currentCollisionTIme - lastCollisionTime;
        Log.e(TAG,"handle_collision::deltaTime="+deltaTime);

        //todo how to use glm::distance
//        float  distance = glm::distance( p1.worldLocation,  p2.worldLocation);
        double distance = bigPlanet.getWorldLocation().distance( smallPlanet.getWorldLocation() );

        double bigVolume = Math.pow( bigPlanet.get_radius() , 3 );
        double smallVolume = Math.pow(  smallPlanet.get_radius(), 3 );
        double wholeVolume = bigVolume + smallVolume;
        double M1 = bigVolume;
        double m1 = smallVolume;

        Log.e(TAG,"P1 is at "+p1.getWorldLocation().toString());
        Log.e(TAG,"P2 is at "+p2.getWorldLocation().toString());
        Log.e(TAG,"P1 has radius = "+p1.get_radius()+"\t P2 has radius = "+p2.get_radius()+"\tsum = "+( p1.get_radius()+p2.get_radius() ));
        Log.e(TAG,"Distance between them are:"+distance);

        if( distance < bigPlanet.radius )
        {
            Log.e(TAG, "+++++++++++++++++Merge++++++++++++++++");
            bigPlanet.set_radius(   Math.pow( wholeVolume, 1 / 3.0 )   );
            smallPlanet.set_radius(0);
            smallPlanet.set_active(false);
        }
        else
        {
            Log.e(TAG,"-------------------Absorption------------------");

            bigPlanet.set_radius(Math.sqrt(wholeVolume / 3 / distance - Math.pow(distance, 2) / 12.0) + distance / 2);
            smallPlanet.set_radius(distance - bigPlanet.get_radius());

            Log.e(TAG, "After Absorption::P1 has radius = " + p1.get_radius() + "\t P2 has radius = " + p2.get_radius() + "\tsum = " + (p1.get_radius() + p2.get_radius())) ;
            Log.e(TAG, "P1 is at " + p1.getWorldLocation().toString());
            Log.e(TAG, "P2 is at " + p2.getWorldLocation().toString());
        }


//    //todo set velocity change

//        Point3F v1 = bigPlanet.get_velocity();
//        Point3F v2 = smallPlanet.get_velocity();
//        Point3F v3 = new Point3F();
//
//        v3 = v1.multiply(  (float )M1 ).add( v2.multiply( (float)m1 ) );
//        v3 = v3.multiply( (float) (1/(M1+m1)));

//        double v1x = v1.x;
//        double v1y = v1.y;
//        double v1z = v1.z;
//        double v2x = v2.x;
//        double v2y = v2.y;
//        double v2z = v2.z;
//
//        double v3x =  (M1 * v1x + m1 * v2x) / (M1 + m1);
//        double v3y = (M1 * v1y + m1 * v2y ) / (M1 + m1);
//        double v3z = (M1 * v1z + m1 * v2z ) / (M1 + m1);
//
//        v3.x = (float ) v3x;
//        v3.y = (float ) v3y;
//        v3.z = (float ) v3z;

//        bigPlanet.set_velocity( v3 );

        lastCollisionTime = currentCollisionTIme;
    }

    public void field_effect(Planet p1, Planet p2)
    {
        boolean flag = p1.has_field();
        Planet  source = flag?p1:p2;
        Planet  target = flag?p2:p1;


        if(source.get_active_status()==false || target.get_active_status() ==false)
        {
//        cout<<"no need check non-active planet"<<endl;
            return ;
        }
        if(source.has_field() == false )
        {
            return ;
        }

        if( target.has_field() )  // field won't affect planet which has field
        {
            return ;
        }

        Point3F sourcePos = source.getWorldLocation();
        Point3F targetPos = target.getWorldLocation();
        double sourceM = Math.pow(source.get_radius(), 3);
        double targetM = Math.pow(target.get_radius(), 3);
        double distance = source.getWorldLocation().distance(target.getWorldLocation());

        Point3F direction = sourcePos.subtract( targetPos ).normalize();

//        if(direction.get_length() != 1)
//        {
//            Log.e(TAG,"normalize return vector with length = "+direction.get_length());
//        }

//        Point3F direction = glm::normalize(sourcePos - targetPos);

        if(source.type == Planet.TYPE.ChaosStar && target.type == Planet.TYPE.PlayerStar)
        {
            inChaos = (distance <= 3* source.get_radius() )? true: false;
        }
        else if(source.type == Planet.TYPE.CenterStar && distance > 2)   // attraction
        {
            Point3F v = target.get_velocity();
            double factor = (    G_CONST  *   sourceM  / Math.pow(distance, 2)  );

            Log.e(TAG,"gravity factor = "+factor);

            v.x += factor * direction.x;
            v.y += factor * direction.y;
            v.z += factor * direction.z;

            target.set_velocity( v  );
        }
        else if(source.type == Planet.TYPE.RepulsiveStar && distance > 4 )
        {
            Point3F v = target.get_velocity();
            double factor = (   0.1 * G_CONST  *   sourceM  / Math.pow(distance, 2)  );

            v.x -= factor * direction.x;
            v.y -= factor * direction.y;
            v.z -= factor * direction.z;

            target.set_velocity( v  );

            v = source.get_velocity();
            factor = (    G_CONST  *   targetM  / Math.pow(distance, 2)  );

            v.x += factor * direction.x;
            v.y += factor * direction.y;
            v.z += factor * direction.z;

            source.set_velocity( v );
        }

    }

    private void update(long fps)
    {
//        Log.e(TAG,"update fps="+fps);
        gm.player.move(fps);
        for(int i=0;i<gm.stars.size();i++)
        {
            gm.stars.get(i).move(fps);
        }
    }

    private void draw()
    {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
//        glUseProgram(GLManager.getGLProgram());





        gm.player.draw(projectionMatrix,viewportMatrix);
        // draw other planets
        for(int i=0;i<gm.stars.size();i++)
        {
            if(gm.stars.get(i).isActive)
            {
                gm.stars.get(i).draw(projectionMatrix,viewportMatrix);
                if( gm.stars.get(i).getVolume() > maxVolume )
                    maxVolume = gm.stars.get(i).getVolume();
            }
        }


        gm.WB.draw(projectionMatrix,viewportMatrix);


        // draw buttons
        for(int i=0;i<gameButtons.length;i++)
        {
//            Log.e("OsmosRenderer","draw gameButtons"+i);
            gameButtons[i].draw();
        }



        isBiggest = ( gm.player.getVolume() > maxVolume );


    }

    private void computeMatrix()
    {
        Point3F face = ic.getFace();
        Point3F up = ic.getUp();

        float xx = gm.player.getWorldLocation().x;
        float yy = gm.player.getWorldLocation().y;
        float zz = gm.player.getWorldLocation().z;

        eyeX = xx - viewDistance * face.x;
        eyeY = yy - viewDistance * face.y;
        eyeZ = zz - viewDistance * face.z;

//        centerX = eyeX + face.x;
//        centerY = eyeY + face.y;
//        centerZ = eyeZ + face.z;

        setLookAtM(viewportMatrix,0,eyeX,eyeY,eyeZ,xx,yy,zz,up.x,up.y,up.z);

        perspectiveM(projectionMatrix,0,45.0f,(float)gm.screenWidth/(float)gm.screenHeight,0.1f,100.0f);

    }







}
