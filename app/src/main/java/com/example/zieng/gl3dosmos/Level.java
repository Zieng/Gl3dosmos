package com.example.zieng.gl3dosmos;

/**
 * Created by yangyuming on 15/12/14.
 */
public class Level {

    private static int curLevel;

    static {
        curLevel = 1;
    }


    public static int getLevel(){
        return curLevel;
    }


    public static boolean setLevel(int level){
        curLevel = level;
        return true;
    }

}
