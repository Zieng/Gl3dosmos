package com.example.zieng.gl3dosmos;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.opengl.GLES20;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import static android.opengl.GLES20.GL_LINEAR;
import static android.opengl.GLES20.GL_REPEAT;
import static android.opengl.GLES20.GL_TEXTURE_WRAP_S;
import static android.opengl.GLES20.GL_TEXTURE_WRAP_T;
import static android.opengl.GLES20.GL_LINEAR_MIPMAP_LINEAR;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.GL_TEXTURE_MAG_FILTER;
import static android.opengl.GLES20.GL_TEXTURE_MIN_FILTER;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glDeleteTextures;
import static android.opengl.GLES20.glGenTextures;
import static android.opengl.GLES20.glGenerateMipmap;
import static android.opengl.GLES20.glTexParameterf;
import static android.opengl.GLES20.glTexParameteri;
import static android.opengl.GLUtils.texImage2D;

/**
 * Created by zieng on 11/23/15.
 */
public class LoadHelper
{
    private static final String TAG = "Loadhelper";

    float [] vertices;
    float [] uvs;
    float [] normals;

    public static int loadTexture(final Context context, int resourceId)
    {
        final int[] textureObjectIds = new int[1];

        glGenTextures(1, textureObjectIds, 0);

        if (textureObjectIds[0] == 0)
        {
            Log.e("load texture", "Could not generate a new OpenGL texture object.");
            return 0;
        }

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;

        // Read in the resource
        final Bitmap bitmap = BitmapFactory.decodeResource(   context.getResources(),   resourceId,   options);

//        final Bitmap bitmap = BitmapFactory.decodeResource( context.getResources() , resourceId );

        if (bitmap == null)
        {
            Log.w("load texture", "Resource ID " + resourceId + " could not be decoded.");

            glDeleteTextures(1, textureObjectIds, 0);
            return 0;
        }

        // Bind to the texture in OpenGL

        glBindTexture(GL_TEXTURE_2D, textureObjectIds[0]);

        // Set filtering: a default must be set, or the texture will be
        // black.
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);



//        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
//        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);

//        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
//        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
//        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
//        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);



//        Log.e(TAG,"Texture Error:"+GLES20.glGetError());
        // Load the bitmap into the bound texture.
        texImage2D(GL_TEXTURE_2D, 0, bitmap, 0);

        // Note: Following code may cause an error to be reported in the
        // ADB log as follows: E/IMGSRV(20095): :0: HardwareMipGen:
        // Failed to generate texture mipmap levels (error=3)
        // No OpenGL error will be encountered (glGetError() will return
        // 0). If this happens, just squash the source image to be
        // square. It will look the same because of texture coordinates,
        // and mipmap generation will work.

        glGenerateMipmap(GL_TEXTURE_2D);


//
//        texImage2D(GL_TEXTURE_2D, 0, bitmap, 0);
//
//        glGenerateMipmap(GL_TEXTURE_2D);
        // Recycle the bitmap, since its data has been loaded into
        // OpenGL.
        bitmap.recycle();

        // Unbind from the texture.
//        glBindTexture(GL_TEXTURE_2D, 0);
//        Log.e(TAG,"Generate a texture with ID="+textureObjectIds[0]);

        return textureObjectIds[0];
    }

    public static Map<String , List<Float>> loadObject(Context context, int sourceId) throws InterruptedException
    {
        ArrayList<Integer> vertexIndices = new ArrayList<>();
        ArrayList<Integer> uvIndices = new ArrayList<>();
        ArrayList<Integer> normalIndices = new ArrayList<>();
        ArrayList<Point3F> v = new ArrayList<>();
        ArrayList<Point3F> vn = new ArrayList<>();
        ArrayList<PointF> vt = new ArrayList<>();

        Map<String, List<Float> > objData = new HashMap<String, List<Float>>();
        ArrayList<Float> out_uvs = new ArrayList<>();
        ArrayList<Float> out_vertices = new ArrayList<>();
        ArrayList<Float> out_normals = new ArrayList<>();

        InputStream in = context.getResources().openRawResource(sourceId);
//        File file = new File(objPath);

        try
        {
            BufferedReader br = new BufferedReader( new InputStreamReader(in,"UTF-8") );
            String line;
            while ((line = br.readLine()) != null)
            {
                // process the line.
                StringTokenizer tok = new StringTokenizer(line);
                String cmd = tok.nextToken();


                if (cmd.equals("v"))
                {
                    v.add(read_point(tok));
                }
                else
                if (cmd.equals("vn"))
                {
                    vn.add(read_point(tok));
                }
                else
                if (cmd.equals("vt"))
                {
                    PointF t = new PointF();
                    t.x = Float.parseFloat(tok.nextToken());
                    t.y = - Float.parseFloat(tok.nextToken());

                    vt.add(t);
                }
                else
                if (cmd.equals("f"))
                {
                    if (tok.countTokens() != 3)
                    {
                        Log.e("load object", "parse face from obj file failed\n");
                        return null;
                    }

                    while (tok.hasMoreTokens()) {
                        StringTokenizer face_tok = new StringTokenizer(tok.nextToken(), "/");

                        int v_idx = -1;
                        int vt_idx = -1;
                        int vn_idx = -1;
                        v_idx = Integer.parseInt(face_tok.nextToken());
                        if (face_tok.hasMoreTokens())
                            vt_idx = Integer.parseInt(face_tok.nextToken());
                        if (face_tok.hasMoreTokens())
                            vn_idx = Integer.parseInt(face_tok.nextToken());

                        //Log.v("objmodel", "face: "+v_idx+"/"+vt_idx+"/"+vn_idx);

                        vertexIndices.add(v_idx);
                        uvIndices.add(vt_idx);
                        normalIndices.add(vn_idx);
                    }
                }
            }
            for( int i=0 ; i<vertexIndices.size(); i++)
            {
                int v_idx = vertexIndices.get(i);
                int vt_idx = uvIndices.get(i);
                int vn_idx = normalIndices.get(i);

                Point3F vertex = v.get(v_idx - 1 );
                PointF uv = vt.get(vt_idx - 1);
                Point3F normal = vn.get(vn_idx - 1);

                out_vertices.add(vertex.x);
                out_vertices.add(vertex.y);
                out_vertices.add(vertex.z);

                out_uvs.add(uv.x);
                out_uvs.add(uv.y);

                out_normals.add(normal.x);
                out_normals.add(normal.y);
                out_normals.add(normal.z);

            }

            objData.put("vertex",out_vertices);
            objData.put("uv",out_uvs);
            objData.put("normal",out_normals);

//            Log.e(TAG,"vertex size="+out_vertices.size());
//            Log.e(TAG,"uv size="+out_uvs.size());
//            Log.e(TAG,"normal size="+out_normals.size());
//
//            Log.e("load obj data finished:","ready to return them");


            return objData;
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }

        return null;
    }

    private static Point3F read_point(StringTokenizer tok)
    {
        Point3F ret = new Point3F();
        if (tok.hasMoreTokens())
        {
            ret.x = Float.parseFloat(tok.nextToken());
            if (tok.hasMoreTokens())
            {
                ret.y = Float.parseFloat(tok.nextToken());
                if (tok.hasMoreTokens()) {
                    ret.z = Float.parseFloat(tok.nextToken());
                }
            }
        }
        return ret;
    }

    public static String readTextFileFromRawResource(final Context context,
                                                     final int resourceId)
    {
        final InputStream inputStream = context.getResources().openRawResource(
                resourceId);
        final InputStreamReader inputStreamReader = new InputStreamReader(
                inputStream);
        final BufferedReader bufferedReader = new BufferedReader(
                inputStreamReader);

        String nextLine;
        final StringBuilder body = new StringBuilder();

        try
        {
            while ((nextLine = bufferedReader.readLine()) != null)
            {
                body.append(nextLine);
                body.append('\n');
            }
        }
        catch (IOException e)
        {
            return null;
        }

        return body.toString();
    }
}
