package com.newvoyage.game.frosty;

import android.graphics.Bitmap;
import android.graphics.RectF;

import java.util.Random;

/**
 * Created by phil on 5/4/16.
 */
public class Gate  {
    private float x;
    private float y;
    private int screenY;


    private int width;
    private int height;
    private boolean isActive;
    private Random generator = new Random();

    private Bitmap gatePic;
    private RectF rect;

    public Gate(int screenX, int screenY, Bitmap bitmap) {

        isActive=false;
        height = screenY / 12;
        width = screenX/3;
        rect = new RectF();
        this.screenY = screenY;
        gatePic = Bitmap.createScaledBitmap(bitmap,width,height,true);
    }

    public Bitmap getBitmap(){return gatePic;}


    public void setInactive(){
        isActive = false;
    }
    public boolean getStatus(){return isActive;}
    public RectF getRect(){return rect;};

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



    public boolean createGate (float startX, boolean isTutorialLevel) {

        //randomly decide if will be created
        int yesCreate = generator.nextInt(1000);
        if (yesCreate == 0 || isTutorialLevel) {
            //the newly created object has not entirely come onto the screen yet
            x  = generator.nextInt((Math.round(startX))-width);
            y = screenY;
            // Update rects

            rect.top = y;
            rect.bottom = y + height;
            rect.left=x+(width/5);
            rect.right=x+(width-width/5);

            isActive = true;
            return true;
        }else{
            return false;
        }
    }

    public void update(long fps, int speed){
        int densityUnit=screenY/100;
        y = y - (densityUnit*(speed/20)) / fps;

        rect.top = y;
        rect.bottom = y + height;
        rect.left=x+(width/5);
        rect.right=x+(width-width/5);

    }

}
