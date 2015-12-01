package com.example.zieng.gl3dosmos;

import android.content.Context;
import android.util.Log;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static android.opengl.GLES20.GL_ARRAY_BUFFER;
import static android.opengl.GLES20.GL_STATIC_DRAW;
import static android.opengl.GLES20.GL_ELEMENT_ARRAY_BUFFER;
import static android.opengl.GLES20.GL_STREAM_DRAW;
import static android.opengl.GLES20.GL_TEXTURE0;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_LINES;
import static android.opengl.GLES20.GL_POINTS;
import static android.opengl.GLES20.GL_TRIANGLES;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetError;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniform1i;
import static android.opengl.GLES20.glUniform3f;
import static android.opengl.GLES20.glUniform4iv;
import static android.opengl.GLES20.glUniformMatrix4fv;
import static android.opengl.GLES20.glUseProgram;
import static android.opengl.GLES20.glVertexAttribPointer;
import static android.opengl.GLES20.glActiveTexture;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glGenBuffers;
import static android.opengl.GLES20.glBindBuffer;
import static android.opengl.GLES20.glBufferData;

import static android.opengl.Matrix.*;

/**
 * Created by zieng on 11/22/15.
 */


public class Planet
{
    private static final String TAG = "Planet";

    Context context;
    static boolean planetInitOK = false;
    static boolean plaentTermiate = false;

    private static int programID = -1;

    enum TYPE{
        NormalStar,
        CenterStar,
        InvisibleStar,
        SwallowStar,
        RepulsiveStar,
        SwiftStar,
        NutriStar,
        DarkStar,
        ChaosStar,
        BreatheStar,
        PlayerStar,
    }
    TYPE type;

    double radius;
    final double min_radius = 0.1;
    final double max_radius = 10;

//    static int worldXScale;
//    static int worldYScale;
//    static int worldZScale;

    static final int[] share_uvBuffer = new int[1];
    static final int [] share_normalBuffer = new int[1];
    static final int [] share_elementBuffer = new int[1];
    static final int [] share_vertexBuffer = new int[1];

    static int playerStarTexture  ,
            centerStarTexture   ,
            normalStarTexture   ,
            repulsiveStarTexture,
            invisibleStarTexture,
            swiftStarTexture    ,
            swallowStarTexture  ,
            nutriStarTexture    ,
            darkStarTexture     ,
            chaosStarTexture    ,
            breatheStarTexture  ,
            undefinedTexture    ;

    static int verticesNum = 0;
    static Buffer share_indices;
    static FloatBuffer share_vertices;
    static FloatBuffer share_uvs;
    static FloatBuffer share_normals;

    private float maxSpeed = 5;
    private Point3F worldLocation;
    private Point3F velocity;

    int textureId;
    int [] vertexBuffer = new int[1];
    int [] uvBuffer = new int[1];
    int [] normalBuffer = new int[1];

    boolean scaleChange = false;   // to reduce the matrix computation
    private float [] self_scaleMatrix = new float[16];
    private float [] self_modelMatrix = new float[16];

    boolean isActive = false;
    // TODO: 11/22/15 Planet class

    public Planet(double radius, TYPE t)
    {
        super();

        programID = GLManager.getGLProgram();
        if(programID == -1)
        {
            programID = GLManager.getGLProgram();
            glUseProgram(programID);   //tell opengl to use the programID

            GLManager.MVPID = glGetUniformLocation(programID,"MVP");
            GLManager.RenderID = glGetUniformLocation(programID, "choose");
            GLManager.MyTextureSamplerID = glGetUniformLocation(programID, "MyTextureSampler");
            GLManager.ColorID = glGetUniformLocation(programID, "FragmentColor");

            GLManager.glsl_vertexPosition = glGetAttribLocation(programID, "vertexPosition_modelspace");
            GLManager.glsl_vertexUV = glGetAttribLocation(programID,"vertexUV");

            Log.e("Planet::check location","vertex location = "+GLManager.glsl_vertexPosition+",uv location = "+GLManager.glsl_vertexUV);
        }

        worldLocation = new Point3F(0,0,0);
        setWorldLocation(0,0,0);

        velocity = new Point3F(0,0,0);

        this.radius = radius;
        type = t;
        isActive = true;


        if(planetInitOK == false || plaentTermiate == true)
        {
            Log.e("planet use error","Before you create a object, init the planet class\n");
            return ;
        }

        alloc_texture();

        uvBuffer = share_uvBuffer;
        normalBuffer = share_normalBuffer;
        vertexBuffer = share_vertexBuffer;

        Log.e(TAG,"create a plant with radius="+radius+",texture="+textureId);

    }

    public void alloc_texture()
    {
        switch (type)
        {
            case PlayerStar:
                textureId = playerStarTexture;
                break;
            case CenterStar:
                textureId = centerStarTexture;
                break;
            case NormalStar:
                textureId = normalStarTexture;
                break;
            case InvisibleStar:
                textureId = invisibleStarTexture;
                break;
            case RepulsiveStar:
                textureId = repulsiveStarTexture;
                break;
            case SwallowStar:
                textureId = swallowStarTexture;
                break;
            case SwiftStar:
                textureId = swiftStarTexture;
                break;
            case NutriStar:
                textureId = nutriStarTexture;
                break;
            case ChaosStar:
                textureId = chaosStarTexture;
                break;
            case DarkStar:
                textureId = darkStarTexture;
                break;
            case BreatheStar:
                textureId = breatheStarTexture;
                break;
            default:
                textureId = undefinedTexture;
                break;
        }
    }

    public void setWorldLocation(float x, float y, float z)
    {
        if( type == TYPE.CenterStar)
            return ;

        this.worldLocation.x = x;
        this.worldLocation.y = y;
        this.worldLocation.z = z;

        setIdentityM(self_modelMatrix, 0);
        scaleM(self_modelMatrix, 0, (float)radius,(float) radius,(float) radius);
        translateM(self_modelMatrix,0,worldLocation.x,worldLocation.y,worldLocation.z);
    }

    public Point3F getWorldLocation()
    {
        return worldLocation;
    }
    public void set_type(TYPE t)
    {
        type = t;
        alloc_texture();
    }

    public void set_velocity(Point3F v)
    {
        if(type == TYPE.CenterStar)
            return ;

        if(  v.get_length() >= maxSpeed)
        {
            v.x = maxSpeed;
            v.y = maxSpeed;
            v.z = maxSpeed;
        }

        velocity.x = v.x;
        velocity.y = v.y;
        velocity.z = v.z;
    }

    public final Point3F get_velocity()
    {
        return velocity;
    }

    public void set_radius( double r)
    {
        if(isActive == false)
            return ;

        if( r < min_radius)
            isActive = false;

//        Log.e(TAG,"Set radisu from "+radius+" to "+r);
        radius = (r>max_radius)?max_radius:r;

        setIdentityM(self_modelMatrix, 0);
        scaleM(self_modelMatrix, 0, (float)radius, (float)radius,(float) radius);
        translateM(self_modelMatrix,0,worldLocation.x,worldLocation.y,worldLocation.z);
    }

    public void set_active(boolean active)
    {
        isActive = active;
    }

    void move(float fps)
    {
//        Log.e(TAG,"fps = "+fps);
        float updataRate = 1 / fps ;

//        Log.e(TAG,"Move planet from "+worldLocation.toString());
        if(velocity.x != 0)
        {
//            Log.e(TAG,"update position-X");
            worldLocation.x += velocity.x * updataRate;
        }

        if(velocity.y != 0)
        {
//            Log.e(TAG,"update position-Y");
            worldLocation.y += velocity.y * updataRate;
        }

        if(velocity.z != 0)
        {
//            Log.e(TAG,"update position-Z");
            worldLocation.z += velocity.z * updataRate;
        }
//        Log.e(TAG,"\t to "+worldLocation.toString());

        // update the self matrix
        setIdentityM(self_modelMatrix, 0);
        scaleM(self_modelMatrix, 0, (float)radius,(float) radius,(float) radius);
        translateM(self_modelMatrix,0,worldLocation.x,worldLocation.y,worldLocation.z);

    }

    public void draw(float [] projectionMatrix, float [] viewportMatrix )
    {
//        Log.e(TAG,"draw planet........");
//        Log.e(TAG,"planet has texure="+textureId);
//        Log.e(TAG,"planet at "+getWorldLocation().toString());

//        print_data();


//        Log.e(TAG,"planet vertices size="+planet.share_vertices.capacity());
//        Log.e(TAG,"planet uvs size="+planet.share_uvs.capacity());

        float [] tempMatrix = new float[16];
        float [] MVP_matrix = new float[16];

        multiplyMM(tempMatrix, 0, viewportMatrix, 0, self_modelMatrix , 0);  // tempMatrix = viewportModelMatrix = viewportMatrix * self_modelMatrix
        multiplyMM(MVP_matrix, 0, projectionMatrix, 0, tempMatrix, 0);


        glUseProgram(programID);



        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, textureId);

        glUniform1i(GLManager.MyTextureSamplerID, 0);
        glUniformMatrix4fv(GLManager.MVPID, 1, false, MVP_matrix, 0);
        glUniform1i(GLManager.RenderID, 1 );

        glUniform3f(GLManager.ColorID,1,1,1);


        glEnableVertexAttribArray(GLManager.glsl_vertexPosition);
        glEnableVertexAttribArray(GLManager.glsl_vertexUV);

        share_vertices.position(0);

        //vertex position data
        glVertexAttribPointer(
                GLManager.glsl_vertexPosition,
                3,
                GL_FLOAT,
                false,
                12,
                share_vertices
        );

        share_uvs.position(0);

        // texture uv
        glVertexAttribPointer(
                GLManager.glsl_vertexUV,
                2,
                GL_FLOAT,
                false,
                8,
                share_uvs
        );

//        Log.e(TAG,"glErrorCode="+glGetError());
//        glDrawElements(GL_TRIANGLES,planet.share_indices.capacity(),GL_FLOAT,0);
//        glDrawArrays(GL_TRIANGLES, 0, planet.share_vertices.capacity() / 3);
        glDrawArrays(GL_TRIANGLES,0, verticesNum );

//        Log.e(TAG,"glErrorCode="+glGetError());
    }


    public boolean check_collision( Planet other)
    {

        if(isActive == false || other.isActive == false)
        {
            return false;
        }

        double distance = worldLocation.distance( other.worldLocation );

        if(other.radius+radius >= distance)
            return true;

        return false;
    }

    public boolean get_active_status()
    {
        return isActive;
    }

    public double get_radius()
    {
        return radius;
    }

    public float[] get_model_matrix()
    {
        return self_modelMatrix;
    }

    public static int get_glProgramId()
    {
        return programID;
    }

    public boolean has_field()
    {
        if ( type == TYPE.CenterStar || type== TYPE.ChaosStar || type == TYPE.RepulsiveStar || type ==TYPE.DarkStar)
            return true;

        return false;
    }

    public static boolean init_planet(Context context)
    {
//        this.context = context;

        playerStarTexture = LoadHelper.loadTexture(context,R.mipmap.player);
        centerStarTexture = LoadHelper.loadTexture(context,R.mipmap.sun);
        normalStarTexture = LoadHelper.loadTexture(context,R.mipmap.normal);
        repulsiveStarTexture = LoadHelper.loadTexture(context,R.mipmap.repulsive);
        invisibleStarTexture = LoadHelper.loadTexture(context,R.mipmap.invisible);
        swiftStarTexture = LoadHelper.loadTexture(context,R.mipmap.swift);
        swallowStarTexture = LoadHelper.loadTexture(context,R.mipmap.swallow);
        nutriStarTexture = LoadHelper.loadTexture(context,R.mipmap.nutri);
        darkStarTexture = LoadHelper.loadTexture(context,R.mipmap.dark);
        chaosStarTexture = LoadHelper.loadTexture(context,R.mipmap.chaos);
        breatheStarTexture = LoadHelper.loadTexture(context,R.mipmap.breathe);
        undefinedTexture = LoadHelper.loadTexture(context,R.mipmap.undefined);

        long origThreadID = Thread.currentThread().getId();
        Log.e("\t\tPlanet::","thread id = "+origThreadID);

        try
        {
            Map<String, List<Float>> objData = LoadHelper.loadObject(context, R.raw.ball);

            List<Float> verticesList = objData.get("vertex");
            List<Float> uvList = objData.get("uv");
            List<Float> normalList = objData.get("normal");

            if( verticesList == null || uvList == null || normalList == null)
            {
                Log.e("get obj error","objData include  null list ");
            }

            float [] verticesArray = new float[verticesList.size()];
            float [] uvsArray = new float[uvList.size()];
            float [] normalsArray = new float[normalList.size()];

            for(int i=0;i<verticesList.size();i++)
                verticesArray[i] = verticesList.get(i)/2.0f;

            for(int i=0;i<uvList.size();i++)
                uvsArray[i] = uvList.get(i);

            for(int i=0;i<normalList.size();i++)
                normalsArray[i] = normalList.get(i);


            share_vertices = ByteBuffer.allocateDirect(verticesArray.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
            share_vertices.put(verticesArray);

            share_uvs = ByteBuffer.allocateDirect(uvsArray.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
            share_uvs.put(uvsArray);

            share_normals = ByteBuffer.allocateDirect(normalsArray.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
            share_normals.put(normalsArray);

            verticesNum = verticesArray.length / 3;

//            share_uvs.position(0);
//            glGenBuffers(1,share_uvBuffer,0);
//            glBindBuffer(GL_ARRAY_BUFFER,share_uvBuffer[0]);
//            glBufferData(GL_ARRAY_BUFFER,share_uvs.capacity() * 4 ,share_uvs,GL_STATIC_DRAW);
//
//            share_normals.position(0);
//            glGenBuffers(1,share_normalBuffer,0);
//            glBindBuffer(GL_ARRAY_BUFFER,share_normalBuffer[0]);
//            glBufferData(GL_ARRAY_BUFFER,share_normals.capacity() * 4 ,share_normals,GL_STATIC_DRAW);
//
////            glGenBuffers(1,share_elementBuffer,0);
////            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER,share_elementBuffer[0]);
////            glBufferData(GL_ELEMENT_ARRAY_BUFFER,share_indices.capacity() * 4 , share_indices,GL_STATIC_DRAW);
//            share_vertices.position(0);
//            glGenBuffers(1,share_vertexBuffer,0);
//            glBindBuffer(GL_ARRAY_BUFFER,share_vertexBuffer[0]);
//            glBufferData(GL_ARRAY_BUFFER, share_vertices.capacity() * 4, share_vertices, GL_STREAM_DRAW);

            Log.e(TAG,"init planet ok");
            planetInitOK = true;
        }
        catch (InterruptedException ex)
        {
            ex.printStackTrace();
        }



        return planetInitOK;
    }

    public void setWorldLocation(Point3F position)
    {
        worldLocation = new Point3F(position);
    }

    public void print_data()
    {
        share_vertices.position(0);

        for(int i=0;i<share_vertices.capacity();i++)
        {
            Log.e(TAG,"vertex data="+share_vertices.get(i));
        }
    }

    public static void terminate_planet()
    {

    }
}
