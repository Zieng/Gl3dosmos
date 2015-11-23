package com.example.zieng.gl3dosmos;

import android.graphics.PointF;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import static android.opengl.GLES20.GL_TEXTURE0;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_LINES;
import static android.opengl.GLES20.GL_POINTS;
import static android.opengl.GLES20.GL_TRIANGLES;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniform1i;
import static android.opengl.GLES20.glUniform3f;
import static android.opengl.GLES20.glUniform4iv;
import static android.opengl.GLES20.glUniformMatrix4fv;
import static android.opengl.GLES20.glUseProgram;
import static android.opengl.GLES20.glVertexAttribPointer;
import static android.opengl.GLES20.glActiveTexture;
import static android.opengl.GLES20.glBindTexture;

import static android.opengl.Matrix.*;

/**
 * Created by zieng on 11/22/15.
 */

public class GameObject
{

    boolean isActive;
    private static int programID = -1;
    private int numElements;   //
    private int numVertices;   //

    private float [] modelVertices;  // hold the coordinates of vertices that define the model
    private float [] modelUvs;
    private float [] modelNormals;

    private float xVelocity = 0;
    private float yVelocity = 0;
    private float zVelocity = 0;

    private float speed = 0;
    private float maxSpeed = 200;

    private Point3F worldLocation = new Point3F();  // where is the object in the game world

    private FloatBuffer vertices;  // hold vertex data passed into OpenGL programID;
    private FloatBuffer uvs;
    private FloatBuffer normals;

    private final float [] self_modelMatrix = new float[16];  // to translate model to the game world

    private int textureID;

    public GameObject()
    {
        if(programID == -1)
        {
            programID = GLManager.getGLProgram();
            glUseProgram(programID);   //tell opengl to use the programID

            GLManager.MVPID = glGetUniformLocation(programID,"MVP");
            GLManager.lightPositionID = glGetUniformLocation(programID,"lightPosition_worldspace");
            GLManager.RenderID = glGetUniformLocation(programID,"choose");
            GLManager.MyTextureSamplerID = glGetUniformLocation(programID,"MyTextureSampler");
            GLManager.modelMatrixID = glGetUniformLocation(programID,"modelMatrix");
            GLManager.viewMatrixID = glGetUniformLocation(programID,"viewMatrix");

            GLManager.glsl_vertexPosition = glGetAttribLocation(programID, "vertexPosition_modelspace");
            GLManager.glsl_vertexColor = glGetAttribLocation(programID,"vertexColor");
            GLManager.glsl_vertexUV = glGetAttribLocation(programID,"vertexUV");
            GLManager.glsl_vertexNormal = glGetAttribLocation(programID,"vertexNormal_modelspace");

            // enable vertex attrib array
            glEnableVertexAttribArray(GLManager.glsl_vertexPosition);
            glEnableVertexAttribArray(GLManager.glsl_vertexUV);
            glEnableVertexAttribArray(GLManager.glsl_vertexNormal);
            glEnableVertexAttribArray(GLManager.glsl_vertexColor);
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
        programID = GLManager.getGLProgram();
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
        numVertices = numElements/3;  // x,y,z

        vertices = ByteBuffer.allocateDirect(modelVertices.length * 4 ).order(ByteOrder.nativeOrder()).asFloatBuffer();  // one float has 4 bytes

        vertices.put(modelVertices);

    }

    public void setUvs(float [] objectUVs)
    {
        modelUvs = new float[objectUVs.length];
        modelUvs = objectUVs;

        uvs = ByteBuffer.allocateDirect(modelUvs.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        uvs.put(modelUvs);
    }

    public void setNormals( float [] objectNormals)
    {
        modelNormals = new float[objectNormals.length];
        modelNormals = objectNormals;

        normals = ByteBuffer.allocateDirect(modelNormals.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        normals.put(modelNormals);
    }

    // TODO: 11/23/15 draw() opengl
    public void draw(float [] projectionMatrix,float [] viewMatrix, Point3F lightPos)
    {
        float [] MVP_matrix = new float[16];
        float [] tempMatrix = new float[16];

        glUseProgram(programID);

        vertices.position(0);   // set vertices to the first byte

        // calculate matrix
        setIdentityM(self_modelMatrix, 0);  // set self_modelMatrix to identity matrix
        translateM(self_modelMatrix, 0, worldLocation.x, worldLocation.y, worldLocation.z);   // model coordinate to world coordinate
        multiplyMM(tempMatrix, 0, viewMatrix, 0, self_modelMatrix, 0);  // tempMatrix = viewportModelMatrix = viewportMatrix * self_modelMatrix
        multiplyMM(MVP_matrix, 0, tempMatrix, 0, projectionMatrix, 0);

        // pass matrix to shader
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, textureID);
        glUniform1i(GLManager.MyTextureSamplerID, 0);
        glUniformMatrix4fv(GLManager.MVPID, 1, false, MVP_matrix, 0);
        glUniformMatrix4fv(GLManager.modelMatrixID,1,false,self_modelMatrix,0);
        glUniformMatrix4fv(GLManager.viewMatrixID,1,false,viewMatrix,0);
        glUniform3f(GLManager.lightPositionID, lightPos.x, lightPos.y, lightPos.z);
        glUniform1i(GLManager.RenderID,2);

        //vertex position data
        // stride = 3 * 4 (3 float x,y,z)
        glVertexAttribPointer(
                GLManager.glsl_vertexPosition,
                3,
                GL_FLOAT,
                false,
                12,
                vertices);

        // texture uv
        glVertexAttribPointer(
                GLManager.glsl_vertexUV,
                2,
                GL_FLOAT,
                false,
                8,
                uvs
        );

        // object normals
        glVertexAttribPointer(
                GLManager.glsl_vertexNormal,
                3,
                GL_FLOAT,
                false,
                12,
                normals
        );

        glDrawArrays(GL_TRIANGLES,0,numVertices);

//        setRotateM(self_modelMatrix, 0, facingAngle, 0, 0, 1.0f);
//        multiplyMM(rotateViewportModelMatrix, 0, viewportModelMatrix, 0, self_modelMatrix, 0);

//        glUniformMatrix4fv(GLManager.uMatrixLocation, 1, false, rotateViewportModelMatrix, 0);
//        glUniform4f(GLManager.uColorLocation, 1.0f, 1.0f, 1.0f, 1.0f);


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
