package com.example.zieng.gl3dosmos;

/**
 * Created by yangyuming on 15/12/15.
 */
public class Score {
    private static int score;
    private static int top = 0;

    public static void setScore(int s){
        score = s;
        if (score > top)
            top = score;
    }

    public static int getScore(){
        return score;
    }
}
