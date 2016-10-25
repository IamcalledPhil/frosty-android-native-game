package com.newvoyage.game.frosty;

import android.graphics.Bitmap;
import android.graphics.RectF;

/**
 * Created by Phil on 1/3/2016.
 */
public class Bullet {

    private float x;
    private float y;

    private RectF rect;

    private int difficulty;

    // Which way is it shooting
    public final int UP = 0;
    public final int DOWN = 1;

    // Going nowhere
    int heading = -1;

    private int width;
    private int height;
    private Bitmap bitmap;

    private boolean isActive;

    public Bullet(int screenY, int screenX, Bitmap snowball_bitmap, int difficulty) {

        height = screenY / 32;
        width = screenY/32;
        isActive = false;
        bitmap = Bitmap.createScaledBitmap(snowball_bitmap,width,height,true);
        this.difficulty=difficulty;

        rect = new RectF();
    }

    public RectF getRect(){
        return  rect;
    }

    public Bitmap getBitmap(){return bitmap;}

    public float getX(){return x;}

    public float getY(){return y;}

    public boolean getStatus(){
        return isActive;
    }

    public void setInactive(){
        isActive = false;
    }

    public float getImpactPointY(){
        if (heading == DOWN){
            return y + height;
        }else{
            return  y;
        }

    }

    public boolean shoot(float startX, float startY, int direction) {
        if (!isActive) {
            x = startX;
            y = startY;
            heading = direction;
            isActive = true;
            return true;
        }

        // Bullet already active
        return false;
    }

    public void update(long fps, int screenY){
        int densityUnit=screenY/100;

        // Just move up or down
        if(heading == UP){
            if (difficulty ==0) {
                y = y - (densityUnit * 65) / fps;
            }else if (difficulty ==1){
                y = y - (densityUnit * 90) / fps;
            }else if (difficulty == 2) {
                y = y - (densityUnit * 120) / fps;
            }
        }else{
            y = y + (densityUnit*200) / fps;
        }

        // Update rect
        rect.left = x;
        rect.right = x + width;
        rect.top = y;
        rect.bottom = y + height;

    }
}

