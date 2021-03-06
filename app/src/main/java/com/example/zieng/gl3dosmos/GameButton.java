package com.example.zieng.gl3dosmos;

import android.graphics.PointF;
import android.util.Log;

import java.nio.FloatBuffer;
import java.nio.*;
import static android.opengl.GLES20.*;
import static android.opengl.Matrix.*;

/**
 * Created by zieng on 11/23/15.
 */
public class GameButton
{
    private static final String TAG = "GameButton";

    private final float[] viewportMatrix = new float[16];
    private static int programID;  // handle to the GL programID
    private int numVertices;  // how many vertices to make the button
    private FloatBuffer vertices;  // hold vertices data

    private float top,left,bottom,right;

    public GameButton(GameManager gm, float top,float left,float bottom,float right)
    {
        this.top = top;
        this.left = left;
        this.bottom = bottom;
        this.right = right;


        orthoM(viewportMatrix,0,0,gm.screenWidth,gm.screenHeight,0,0,0.1f);

        //shrink the button visuals to make them less obtrusive while leaving the screen area they represent the same
        float width = (right-left)/2;
        float height = (top - bottom)/2;
        left += width/2;
        right -= width/2;
        top -= height/2;
        bottom += height/2;

        PointF p1 = new PointF();
        p1.x = left;
        p1.y = top;

        PointF p2 = new PointF();
        p2.x = right;
        p2.y = top;

        PointF p3 = new PointF();
        p3.x = right;
        p3.y = bottom;

        PointF p4 = new PointF();
        p4.x = left;
        p4.y = bottom;

        // add 4 points to an array
        float[] modelVertices = new float[]{
                p1.x,p1.y,0,
                p2.x,p2.y,0,

                p2.x,p2.y,0,
                p3.x,p3.y,0,

                p3.x,p3.y,0,
                p4.x,p4.y,0,

                p4.x,p4.y,0,
                p1.x,p1.y,0
        };

        final int ELEMENTS_PER_VERTEX=3;   //x,y,z
        int numElements = modelVertices.length;
        numVertices = numElements/ELEMENTS_PER_VERTEX;

        // initialize the vertices bytebuffer based on the ....
        vertices = ByteBuffer.allocateDirect(numElements * 4 ).order(ByteOrder.nativeOrder()).asFloatBuffer();

        vertices.put(modelVertices);   // add the button into the ByteBuffer object

        programID = GLManager.getGLProgram();
        if(programID == -1)
        {
            programID = GLManager.getGLProgram();
            glUseProgram(programID);   //tell opengl to use the programID

            Log.e("GameButton", "vertex location = " + GLManager.glsl_vertexPosition + ",uv location = " + GLManager.glsl_vertexUV);
        }
    }

    public void draw()
    {
//        Log.e(TAG,"drawing.....");
//        Log.e("button info","top="+top+",right="+right+",bottom="+bottom+",left="+left);
//
//        long origThreadID = Thread.currentThread().getId();
//        Log.e(TAG,"thread id = "+origThreadID);
//
//        Log.e(TAG, "vertex location = " + GLManager.glsl_vertexPosition + ",uv location = " + GLManager.glsl_vertexUV);

        glUseProgram(programID);

        glUniform1i(GLManager.RenderID, 0);  // no texture, no light, default white color
        glUniformMatrix4fv(GLManager.MVPID, 1, false, viewportMatrix, 0);
        glUniform3f(GLManager.ColorID,1,1,0);

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

        glDrawArrays(GL_LINES,0,numVertices);
    }
}
