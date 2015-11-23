package com.example.zieng.gl3dosmos;

import static android.opengl.GLES20.GL_FRAGMENT_SHADER;
import static android.opengl.GLES20.GL_VERTEX_SHADER;
import static android.opengl.GLES20.glAttachShader;
import static android.opengl.GLES20.glCompileShader;
import static android.opengl.GLES20.glCreateProgram;
import static android.opengl.GLES20.glCreateShader;
import static android.opengl.GLES20.glLinkProgram;
import static android.opengl.GLES20.glShaderSource;

public class GLManager
{
//    public static final int COMPONENTS_PER_VERTEX = 3;
//    public static final int FLOAT_SIZE = 4;
//    public static final int STRIDE = (COMPONENTS_PER_VERTEX) *(FLOAT_SIZE);
//    public static final int ELEMENTS_PER_VERTEX = 3; //x,y,z


    // match the string above
    public static int MVPID;
    public static int RenderID;
    public static int MyTextureSamplerID;
    public static int modelMatrixID;
    public static int viewMatrixID;
    public static int lightPositionID;

    public static int glsl_vertexPosition;
    public static int glsl_vertexColor;
    public static int glsl_vertexUV;
    public static int glsl_vertexNormal;

    // vertex shader program packed in the string
    private static String vertexShader=
    "attribute vec3 vertexPosition_modelspace;"+
    "attribute vec3 vertexColor;"+
    "attribute vec2 vertexUV;"+
    "attribute vec3 vertexNormal_modelspace;"+
    "\n"+
    "uniform int choose;"+
    "uniform mat4 MVP;"+
    "uniform mat4 viewMatrix;"+
    "uniform mat4 modelMatrix;"+
    "uniform vec3 lightPosition_worldspace;"+
    "\n"+
    "varying vec3 FragmentColor;"+
    "varying vec2 UV;"+
    "varying vec3 position_worldspace;"+
    "varying vec3 normal_cameraspace;"+
    "varying vec3 eyeDirection_cameraspace;"+
    "varying vec3 lightDirection_cameraspace;"+
    "\n"+
    "void main()"+
    "{"+
        "gl_PointSize = 4;"+
        "if(choose!=0)"+
        "{"+
            "if(choose!=1)"+
            "{"+
                "position_worldspace=(modelMatrix * vec4(vertexPosition_modelspace,1)).xyz;"+
                "eyeDirection_cameraspace=vec3(0,0,0)-(viewMatrix * modelMatrix * vec4(vertexPosition_modelspace,1)).xyz;"+
                "vec3 lightPosition_cameraspace = ( viewMatrix * vec4(lightPosition_worldspace,1)).xyz;"+
                "lightDirection_cameraspace = lightPosition_cameraspace + eyeDirection_cameraspace;"+
                "normal_cameraspace = ( viewMatrix * modelMatrix * vec4(vertexNormal_modelspace,0)).xyz;"+
            "}"+
            "UV=vertexUV;"+
        "}"+
        "gl_Position=MVP*vec4(vertexPosition_modelspace,1);"+
        "FragmentColor=vec3(1,1,1);"+
    "}";



    //fragment shader packed in string(no source code in the text,so I write it below)
    private static String fragmentShader=
            "precision mediump float;"+
    "varying vec3 FragmentColor;"+
    "varying vec2 UV;"+
    "varying vec3 position_worldspace;"+
    "varying vec3 normal_cameraspace;"+
    "varying vec3 eyeDirection_cameraspace;"+
    "varying vec3 lightDirection_cameraspace;"+
    "\n"+
    "uniform sampler2D MyTextureSampler;"+
    "uniform int choose;"+
    "uniform vec3 lightPosition_worldspace;"+
    "\n"+
    "void main()"+
    "{"+
        "if (choose==0)"+
        "{"+
            "gl_FragColor=FragmentColor;"+
        "}"+
        "else if(choose == 1)"+
        "{"+
            "gl_FragColor=texture2D(MyTextureSampler,UV).rgb;"+
        "}"+
        "else"+
        "{"+
            "vec3 lightColor=vec3(1,1,1);"+
            "float lightPower=50.f;"+
            "vec3 materialDiffuseColor=texture2D(MyTextureSampler,UV).rgb;"+
            "vec3 materialAmbientColor=vec3(0.8,0.8,0.8) * materialDiffuseColor;"+
            "vec3 materialSpecularColor=vec3(0.4,0.3,0.3);"+
            "float d=length(lightPosition_worldspace - position_worldspace);"+
            "vec3 n=normalize( normal_cameraspace);"+
            "vec3 l=normalize( lightDirection_cameraspace );"+
            "float cosTheta = clamp( dot(n,l),0,1);"+
            "vec3 E=normalize( eyeDirection_cameraspace );"+
            "vec3 R=reflect(-l,n);"+
            "float cosAlpha=clamp(dot(E,R),0,1);"+
            "gl_FragColor=materialAmbientColor"+
                    "+ materialDiffuseColor * lightColor * lightPower * cosTheta/(d*d)"+
                    "+ materialSpecularColor * lightColor * lightPower * pow(cosAlpha,5)/(d*d);"+
        "}"+
    "}";

    private static int program;   // handle to our GL program

    public static int getGLProgram()
    {
        return program;
    }

    public static int buildProgram()
    {
        return linkProgram(compileVertexShader(),compileFragmentShader());
    }

    private static int compileVertexShader()
    {
        return compileShader(GL_VERTEX_SHADER,vertexShader);
    }

    private static int compileFragmentShader()
    {
        return compileShader(GL_FRAGMENT_SHADER,fragmentShader);
    }

    private static int compileShader(int type,String shaderCode)
    {
        final int shader = glCreateShader(type);  // create a shader object and store its ID
        glShaderSource(shader,shaderCode);     // pass the shadercode to shader
        glCompileShader(shader);  // compile specified shader

        return shader;
    }

    private static int linkProgram(int vertexShader,int fragmentShader)
    {
        program = glCreateProgram();   // handle to the glProgram
        glAttachShader(program,vertexShader);
        glAttachShader(program,fragmentShader);
        glLinkProgram(program);

        return program;
    }
}

