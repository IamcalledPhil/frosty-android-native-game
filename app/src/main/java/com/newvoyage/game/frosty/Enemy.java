package com.newvoyage.game.frosty;

import android.graphics.Bitmap;
import android.graphics.RectF;

import java.util.Random;

/**
 * Created by Phil on 1/3/2016.
 */
public class Enemy {
    private float x;
    private float y;
    private  float screenXenemy;

    private RectF rect;
    Random generator = new Random();

    private int width;
    private int height;
    private boolean clearedBottomY;
    private boolean moveLeft;

    private boolean isActive;
    private int ID;
    private Bitmap bitmap;

    public Enemy(int screenX, int screenY, int id, Bitmap enemyPic) {

        height = screenY / 30;
        width = screenX/8;
        screenXenemy=screenX;
        isActive = false;
        ID=id;
        rect = new RectF();
        bitmap = Bitmap.createScaledBitmap(enemyPic,width,height,true);

    }

    public RectF getRect(){
        return  rect;
    }

    public boolean getStatus(){
        return isActive;
    }

    public void setInactive(){
        isActive = false;
        clearedBottomY=true;
    }

    public int getWidth(){
        return width;
    }

    public Bitmap getBitmap(){return bitmap;}


    public int getID(){
        return ID;
    }

    //for when the bottom of it dissapears off the top of the screen
    public float getImpactPointY(){
        return y + height;
    }

    public float getY(){
        return y;
    }
    public float getX(){
        return x;
    }


    public boolean create (float startX, int screenY,boolean startEnemyLevel) {
        int yesCreate = generator.nextInt(1000);
        if (yesCreate == 0 || startEnemyLevel==true) {
            //the newly created object has not entirely come onto the screen yet
            clearedBottomY=false;
            x  = generator.nextInt((Math.round(startX))-width);
            y = screenY;
            // Update rect
            rect.left = x;
            rect.right = x + width;
            rect.top = y;
            rect.bottom = y + height;

            isActive = true;
            return true;
        }else{
            return false;
        }
    }


    public boolean getClearedBottomY(){
        return clearedBottomY;
    }


    public void update(long fps, int speed, int screenY){
        int densityUnit=screenY/100;
        //go upscreen until reach a specific height, at which point it "stops" and moves back and forth
        if (y>(screenY*0.8)) {
            y = y - (densityUnit * (speed / 20)) / fps;

            // Update rect
            rect.top = y;
            rect.bottom = y + height;
        }else {
            if (moveLeft){
                x=x-((screenXenemy/2)/fps);
                if (x<0){
                    moveLeft=false;
                }
            }else{
                x=x+((screenXenemy/2)/fps);
                if (rect.right>screenXenemy){
                    moveLeft=true;
                }
            }

            rect.left = x;
            rect.right = x + width;
        }


        /*   set this when the obstacle has been entirely generated. This means another obstacle may take the x coordinates, or if tunnel, then
        another pair of obstacles may be generated*/
        if (clearedBottomY==false) {
            if (rect.bottom<screenY){
                clearedBottomY = true;
            }
        }


    }
}
