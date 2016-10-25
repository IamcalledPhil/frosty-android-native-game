package com.newvoyage.game.frosty;

import android.content.Context;
import android.graphics.Bitmap;

/**
 * Created by Phil on 05-Jan-16.
 */
public class Background {
    private Bitmap bitmap;

    private float x;
    private float y;
    private  int BackgroundScreenY;


    public Background(int x, int y, int screenX, int screenY, Context context,Bitmap newbitmap) {
        this.x = x;
        this.y = y;
        BackgroundScreenY=screenY;

        // Initialize the bitmap
        bitmap=newbitmap;

    }

    public void update(int speed, long fps) {

        int densityUnit=BackgroundScreenY/100;
        y = y - (densityUnit*(speed/20)) / fps;

        // Check to see if the image has gone off the top
        if (this.y <= -1 * bitmap.getHeight()) {

            // If it has, line it back up so that its left edge is
            // lined up to the right side of the other background image
            this.y = this.y + bitmap.getHeight() * 2;
        }


    }

    public void setX(int x) {
        this.x = x;
    }
    public float getX() {
        return this.x;
    }
    public float getY() {
        return this.y;
    }
    public int getImageHeight() {
        return bitmap.getHeight();
    }
    public Bitmap getBitmap(){
        return bitmap;
    }



}