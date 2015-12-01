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

    public Point3F(Point3F ohter)
    {
        super(ohter.x,ohter.y);
        z = ohter.z;
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

    public double get_length()
    {
        return Math.sqrt( x*x + y*y + z*z  );
    }

    public double distance(Point3F p)
    {
        return Math.sqrt( Math.pow(x-p.x,2) + Math.pow(y-p.y,2)  + Math.pow(z-p.z, 2)  );
    }

    public String toString()
    {
        String str = "Point3F";
        str+="("+x+","+y+","+z+")";

        return str;
    }

    public Point3F normalize()
    {
        float len = (float) (this.get_length() );

        return new Point3F( x/len, y/len , z/len );
    }

    public Point3F subtract(Point3F other)
    {
        return new Point3F(x-other.x,  y- other.y , z-other.z);
    }

    public Point3F multiply(float d )
    {
        x *= d;
        y *= d;
        z *= d;

        return new Point3F(x,y,z);
    }

    public Point3F multiply( Point3F other)
    {
        x *= other.x;
        y *= other.y;
        z *= other.z;

        return new Point3F(x,y,z);
    }

    public Point3F reverse()
    {
        x = -x;
        y = -y;
        z = -z;

        return new Point3F(x,y,z);
    }

    public Point3F add(Point3F other)
    {
        x += other.x;
        y += other.y;
        z += other.z;

        return new Point3F(x,y,z);
    }
}
