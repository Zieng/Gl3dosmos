package com.example.zieng.gl3dosmos;

import android.graphics.PointF;

/**
 * Created by zieng on 11/22/15.
 */
public class Point3F extends PointF
{
    public float z;

    public Point3F()
    {
        super(0,0);
        z = 0;
    }

    public Point3F(float xx, float yy, float zz)
    {
        super(xx,yy);
        z = zz;
    }

    public boolean equals(float xx ,float yy, float zz)
    {
        if(x == xx && y==yy && z==zz)
            return true;

        return false;
    }

    public float get_length()
    {
        return (float) Math.sqrt( x*x + y*y + z*z  );
    }

    public float distance(Point3F p)
    {
        return (float) Math.sqrt( Math.pow(x-p.x,2) + Math.pow(y-p.y,2)  + Math.pow(z-p.z, 2)  );
    }

}
