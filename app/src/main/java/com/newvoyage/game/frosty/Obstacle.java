package com.newvoyage.game.frosty;

import android.graphics.Bitmap;
import android.graphics.RectF;

import java.util.Random;

/**
 * Created by Phil on 12/18/2015.
 */
public class Obstacle {
    private float x;
    private float y;

    private RectF rect;

    Random generator = new Random();

    private int width;
    private int height;
    private boolean clearedBottomY;
    private boolean checked;

    private boolean isActive;
    private int ID;
    private Bitmap obsPic;
    private float bitHeight;
    private float bitWidth;

    public Obstacle(int screenX, int screenY, int id, Bitmap bitmap) {

        height = screenY / 25;
        width = screenX/15;
        isActive = false;
        ID=id;
        rect = new RectF();
        //set the height for the bitmap to be a bit bigger to compensate for the image size
        bitHeight = Math.round(height*1.5);
        bitWidth = Math.round(width*1.5);
        obsPic = Bitmap.createScaledBitmap(bitmap,Math.round(bitWidth),Math.round(bitHeight),true);

    }

    public RectF getRect(){
        return  rect;
    }

    public Bitmap getBitmap(){return obsPic;}

    public boolean getStatus(){
        return isActive;
    }

    public void setInactive(){
        isActive = false;
    }

    //set if it has been checked if it's intersecting another obstacle
    public void setChecked(boolean isChecked){
        checked=isChecked;
    }

    public boolean getChecked(){
        return checked;
    }

    public void clearObs (){
        x = 0;
        y = 0;
        isActive = false;
    }


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



    public boolean createSparse (float startX, int screenY) {

        //randomly decide if will be created
        int yesCreate = generator.nextInt(2500);
        if (yesCreate == 0) {
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

    public boolean createWarning (float startX, int screenY) {
        int yesCreate = generator.nextInt(2000);
        if (yesCreate == 0) {
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

    public boolean createDense (float startX, int screenY) {
        int yesCreate = generator.nextInt(700);
        if (yesCreate == 0) {
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

    public boolean createTunnel (float startX, int screenY) {
            clearedBottomY=false;
            x  = startX;
            y = screenY;
            // Update rect
            rect.left = x;
            rect.right = x + width;
            rect.top = y;
            rect.bottom = y + height;

            isActive = true;
            return true;
    }

    public boolean create (float startX, int screenY) {
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
    }

    public boolean getClearedBottomY(){
        return clearedBottomY;
    }


    public void update(long fps, int speed, int screenY){
        int densityUnit=screenY/100;
            y = y - (densityUnit*(speed/20)) / fps;

        // Update rect
        rect.top = y;
        rect.bottom = y + height;
        /*   set this when the obstacle has been entirely generated. This means another obstacle may take the x coordinates, or if tunnel, then
        another pair of obstacles may be generated*/
        if (clearedBottomY==false) {
             if (rect.bottom<screenY){
                clearedBottomY = true;
            }
        }


    }
}
