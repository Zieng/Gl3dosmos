package com.example.zieng.gl3dosmos;

import android.graphics.PointF;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_LINES;
import static android.opengl.GLES20.GL_POINTS;
import static android.opengl.GLES20.GL_TRIANGLES;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniform4f;
import static android.opengl.GLES20.glUniformMatrix4fv;
import static android.opengl.GLES20.glUseProgram;
import static android.opengl.GLES20.glVertexAttribPointer;

import static android.opengl.Matrix.*;

/**
 * Created by zieng on 11/22/15.
 */

public class GameObject
{

    boolean isActive;
    private static int glProgram = -1;
    private int numElements;   //
    private int numVertices;   //

    private float [] modelVertices;  // hold the coordinates of vertices that define the model

    private float xVelocity = 0;
    private float yVelocity = 0;
    private float zVelocity = 0;

    private float speed = 0;
    private float maxSpeed = 200;

    private Point3F worldLocation = new Point3F();  // where is the object in the game world

    private FloatBuffer vertices;  // hold vertex data passed into OpenGL glProgram;

    private final float [] modelMatrix = new float[16];  // to translate model to the game world

    float [] viewportModelMatrix = new float[16];
    float [] rotateViewportModelMatrix = new float[16];


    public GameObject()
    {
        if(glProgram == -1)
        {
            glProgram = GLManager.getGLProgram();
            glUseProgram(glProgram);   //tell opengl to use the glProgram

            GLManager.MVPID = glGetUniformLocation(glProgram,"MVP");
            GLManager.lightPositionID = glGetUniformLocation(glProgram,"lightPosition_worldspace");
            GLManager.RenderID = glGetUniformLocation(glProgram,"choose");
            GLManager.MyTextureSamplerID = glGetUniformLocation(glProgram,"MyTextureSampler");
            GLManager.modelMatrixID = glGetUniformLocation(glProgram,"modelMatrix");
            GLManager.viewMatrixID = glGetUniformLocation(glProgram,"viewMatrix");

        }

        isActive =true;
    }

    public boolean isActive()
    {
        return isActive;
    }

    public void setActive(boolean isActive)
    {
        this.isActive = isActive;
    }

    public void setGLProgram()
    {
        glProgram = GLManager.getGLProgram();
    }



    public PointF getWorldLocation()
    {
        return worldLocation;
    }

    public void setWorldLocation(float x, float y, float z)
    {
        this.worldLocation.x = x;
        this.worldLocation.y = y;
        this.worldLocation.z = z;
    }

    public void setVertices(float [] objectVertices)
    {
        modelVertices = new float[objectVertices.length];
        modelVertices = objectVertices;

        numElements = modelVertices.length;
        numVertices = numElements/GLManager.ELEMENTS_PER_VERTEX;

        vertices = ByteBuffer.allocateDirect(numElements * GLManager.FLOAT_SIZE).order(ByteOrder.nativeOrder()).asFloatBuffer();

        vertices.put(modelVertices);

    }

    public void draw(float [] viewportMatrix)
    {
        glUseProgram(glProgram);

        vertices.position(0);   // set vertices to the first byte

        glVertexAttribPointer(
                GLManager.aPositionLocation,
                GLManager.COMPONENTS_PER_VERTEX,
                GL_FLOAT,
                false,
                GLManager.STRIDE,
                vertices);

        glEnableVertexAttribArray(GLManager.aPositionLocation);

        setIdentityM(modelMatrix, 0);
        translateM(modelMatrix, 0, worldLocation.x, worldLocation.y, 0);   // model coordinate to world coordinate

        multiplyMM(viewportModelMatrix, 0, viewportMatrix, 0, modelMatrix, 0);  //viewportModelMatrix = viewportMatrix * modelMatrix

        setRotateM(modelMatrix, 0, facingAngle, 0, 0, 1.0f);
        multiplyMM(rotateViewportModelMatrix, 0, viewportModelMatrix, 0, modelMatrix, 0);

        glUniformMatrix4fv(GLManager.uMatrixLocation, 1, false, rotateViewportModelMatrix, 0);
        glUniform4f(GLManager.uColorLocation, 1.0f, 1.0f, 1.0f, 1.0f);


    }


    public float getxVelocity()
    {
        return xVelocity;
    }
    public float getyVelocity()
    {
        return yVelocity;
    }
    public float getzVelocity()
    {
        return zVelocity;
    }
    public void setxVelocity(float xVelocity)
    {
        this.xVelocity = xVelocity;
    }
    public void setyVelocity(float yVelocity)
    {
        this.yVelocity = yVelocity;
    }
    public void setzVelocity(float zVelocity)
    {
        this.zVelocity = zVelocity;
    }

    public float getSpeed()
    {
        return speed;
    }
    public void setSpeed(float speed)
    {
        this.speed = speed ;
    }
    public float getMaxSpeed()
    {
        return maxSpeed;
    }
    public void setMaxSpeed(float maxSpeed)
    {
        this.maxSpeed = maxSpeed;
    }

    void move(float fps)
    {
        if(xVelocity != 0)
        {
            worldLocation.x += xVelocity/fps;
        }
        if(yVelocity != 0)
        {
            worldLocation.y += yVelocity/fps;
        }
        if(zVelocity != 0)
        {
            worldLocation.z += zVelocity / fps;
        }

    }


}
