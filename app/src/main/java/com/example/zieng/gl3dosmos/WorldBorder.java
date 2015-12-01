package com.example.zieng.gl3dosmos;

import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_LINES;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glUniform1i;
import static android.opengl.GLES20.glUniform3f;
import static android.opengl.GLES20.glUniformMatrix4fv;
import static android.opengl.GLES20.glUseProgram;
import static android.opengl.GLES20.glVertexAttribPointer;
import static android.opengl.Matrix.multiplyMM;
import static android.opengl.Matrix.setIdentityM;

/**
 * Created by zieng on 11/28/15.
 */
public class WorldBorder
{
    private static final String TAG = "WorldBorder";

    private static int programID;  // handle to the GL programID
    private int numVertices;  // how many vertices to make the button
    private FloatBuffer vertices;  // hold vertices data

    final float[] self_modelMatrix = new float[16];

    public WorldBorder(float scaleX, float scaleY, float scaleZ)
    {
        setIdentityM(self_modelMatrix, 0);

        float[] modelVertices = new float[]
                {
                        scaleX, -scaleY, -scaleZ,
                        scaleX, scaleY, -scaleZ,

                        scaleX, scaleY, -scaleZ,
                        -scaleX, scaleY, -scaleZ,

                        -scaleX, scaleY, -scaleZ,
                        -scaleX, -scaleY, -scaleZ,

                        -scaleX, -scaleY, -scaleZ,
                        scaleX, -scaleY, -scaleZ,


                        scaleX, -scaleY, -scaleZ,
                        scaleX, -scaleY, scaleZ,

                        scaleX, scaleY, -scaleZ,
                        scaleX, scaleY, scaleZ,

                        -scaleX, scaleY, -scaleZ,
                        -scaleX, scaleY, scaleZ,

                        -scaleX, -scaleY, -scaleZ,
                        -scaleX, -scaleY, scaleZ,


                        scaleX, -scaleY, scaleZ,
                        scaleX, scaleY, scaleZ,

                        scaleX, scaleY, scaleZ,
                        -scaleX, scaleY, scaleZ,

                        -scaleX, scaleY, scaleZ,
                        -scaleX, -scaleY, scaleZ,

                        -scaleX, -scaleY, scaleZ,
                        scaleX, -scaleY, scaleZ
                };

        final int ELEMENTS_PER_VERTEX = 3;   //x,y,z
        int numElements = modelVertices.length;
        numVertices = numElements / ELEMENTS_PER_VERTEX;

        // initialize the vertices bytebuffer based on the ....
        vertices = ByteBuffer.allocateDirect(numElements * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();

        vertices.put(modelVertices);   // add the button into the ByteBuffer object

        programID = GLManager.getGLProgram();
        if (programID == -1)
        {
            programID = GLManager.getGLProgram();
            glUseProgram(programID);   //tell opengl to use the programID

            Log.e(TAG, "vertex location = " + GLManager.glsl_vertexPosition + ",uv location = " + GLManager.glsl_vertexUV);
        }

    }


    public void draw(final float[] projectionMatrix, final float[] viewportMatrix)
    {
//        Log.e(TAG,"drawing.....");
//        Log.e("button info","top="+top+",right="+right+",bottom="+bottom+",left="+left);
//
//        long origThreadID = Thread.currentThread().getId();
//        Log.e(TAG,"thread id = "+origThreadID);
//
//        Log.e(TAG, "vertex location = " + GLManager.glsl_vertexPosition + ",uv location = " + GLManager.glsl_vertexUV);

        float[] tempMatrix = new float[16];
        float[] MVP_matrix = new float[16];

        multiplyMM(tempMatrix, 0, viewportMatrix, 0, self_modelMatrix, 0);  // tempMatrix = viewportModelMatrix = viewportMatrix * self_modelMatrix
        multiplyMM(MVP_matrix, 0, projectionMatrix, 0, tempMatrix, 0);

        glUseProgram(programID);

        glUniform1i(GLManager.RenderID, 0);  // no texture, no light
        glUniformMatrix4fv(GLManager.MVPID, 1, false, viewportMatrix, 0);
        glUniform3f(GLManager.ColorID, 1, 1, 1);

        vertices.position(0);

        glEnableVertexAttribArray(GLManager.glsl_vertexPosition);

        // vertex position data
        glVertexAttribPointer(
                GLManager.glsl_vertexPosition,
                3,
                GL_FLOAT,
                false,
                12,
                vertices
        );

        glDrawArrays(GL_LINES, 0, numVertices);
    }
}
