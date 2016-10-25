package com.newvoyage.game.frosty;

import android.graphics.Bitmap;

/**
 * Created by phil on 3/14/16.
 */
public class WarningSign {

    private float x;
    private float y;
    private int screenY;


    private int width;
    private int height;
    private boolean isActive;

    private Bitmap warningPic;

    public WarningSign(int screenX, int screenY, Bitmap bitmap) {

        isActive=false;
        height = screenY / 6;
        width = screenX/3;
        this.screenY = screenY;
        warningPic = Bitmap.createScaledBitmap(bitmap,width,height,true);
    }

    public Bitmap getBitmap(){return warningPic;}


    public void setInactive(){
        isActive = false;
    }
    public boolean getStatus(){return isActive;}

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



    public boolean createWarningSign (float startX) {
        x  = startX;
        y = screenY;

        isActive = true;
        return true;
    }

    public void update(long fps, int speed){
        int densityUnit=screenY/100;
        y = y - (densityUnit*(speed/20)) / fps;

    }

}
