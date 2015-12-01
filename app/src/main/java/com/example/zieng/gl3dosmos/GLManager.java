package com.example.zieng.gl3dosmos;

import android.util.Log;

import static android.opengl.GLES20.GL_VALIDATE_STATUS;
import static android.opengl.GLES20.GL_FRAGMENT_SHADER;
import static android.opengl.GLES20.GL_VERTEX_SHADER;
import static android.opengl.GLES20.glAttachShader;
import static android.opengl.GLES20.glCompileShader;
import static android.opengl.GLES20.glCreateProgram;
import static android.opengl.GLES20.glCreateShader;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glLinkProgram;
import static android.opengl.GLES20.glShaderSource;
import static android.opengl.GLES20.glValidateProgram;
import static android.opengl.GLES20.glGetProgramInfoLog;
import static android.opengl.GLES20.glGetProgramiv;
import static android.opengl.GLES20.glGetShaderInfoLog;

public class GLManager
{
    private static final String TAG = "GLManager";
//    public static final int COMPONENTS_PER_VERTEX = 3;
//    public static final int FLOAT_SIZE = 4;
//    public static final int STRIDE = (COMPONENTS_PER_VERTEX) *(FLOAT_SIZE);
//    public static final int ELEMENTS_PER_VERTEX = 3; //x,y,z


    // match the string above
    public static int MVPID;
    public static int RenderID;
    public static int MyTextureSamplerID;
    public static int ColorID;

    public static int glsl_vertexPosition = -1;
    public static int glsl_vertexUV = -1;


    private static int program;   // handle to our GL program

    public static int getGLProgram()
    {
        return program;
    }

    public static int buildProgram(String vertexShader, String fragmentShader)
    {
        long origThreadID = Thread.currentThread().getId();
        Log.e(TAG,"thread id = "+origThreadID);

        Log.e(TAG,"link program.....");
        return linkProgram(compileVertexShader(vertexShader), compileFragmentShader(fragmentShader));
    }

    private static int compileVertexShader(String vertexShader)
    {
        Log.e("GLManager","compile vertex shader....");
        return compileShader(GL_VERTEX_SHADER,vertexShader);
    }

    private static int compileFragmentShader(String fragmentShader)
    {
        Log.e("GLManager","compile fragment shader.....");
        return compileShader(GL_FRAGMENT_SHADER,fragmentShader);
    }

    private static int compileShader(int type,String shaderCode)
    {
        final int shader = glCreateShader(type);  // create a shader object and store its ID
        if(shader == 0)
        {
            Log.e("GLManager","glCreateShader() return 0");
        }
        glShaderSource(shader,shaderCode);     // pass the shadercode to shader
        glCompileShader(shader);  // compile specified shader

        Log.e("GLManager","compile shader return "+shader);

        return shader;
    }

    private static int linkProgram(int vertexShader,int fragmentShader)
    {
        program = glCreateProgram();   // handle to the glProgram
        if(program == 0)
        {
            Log.e(TAG,"glCreateProgram() return 0");
        }

        Log.e("check vertex shader",glGetShaderInfoLog(vertexShader));
        Log.e("check fragment shader", glGetShaderInfoLog(fragmentShader));

        glAttachShader(program, vertexShader);
        glAttachShader(program, fragmentShader);
        glLinkProgram(program);

        if( validateProgram(program) == false )
        {
            Log.e("GLManager","gl program not correct");
            return 0;
        }


        GLManager.MVPID = glGetUniformLocation(program,"MVP");
        GLManager.RenderID = glGetUniformLocation(program, "choose");
        GLManager.MyTextureSamplerID = glGetUniformLocation(program, "MyTextureSampler");
        GLManager.ColorID = glGetUniformLocation(program,"FragmentColor");

        GLManager.glsl_vertexPosition = glGetAttribLocation(program, "vertexPosition_modelspace");
        GLManager.glsl_vertexUV = glGetAttribLocation(program,"vertexUV");

        Log.e(TAG,"-------------------------------");
        Log.e(TAG,"link is ok");
        Log.e(TAG,"-------------------------------");

        return program;
    }

    public static boolean validateProgram(int programObjectId)
    {
        glValidateProgram(programObjectId);
        final int[] validateStatus = new int[1];

        glGetProgramiv(programObjectId, GL_VALIDATE_STATUS, validateStatus, 0);

        Log.e(TAG, "Results of validating program: " + validateStatus[0]);
        Log.e(TAG,"program info:"+glGetProgramInfoLog(programObjectId) );

        return validateStatus[0] != 0;
    }

}

