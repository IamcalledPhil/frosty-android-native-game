package com.newvoyage.game.frosty;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.RectF;

/**
 * Created by phil on 6/22/16.
 */
public class SpritesheetAnimation {

/*    for the snowboard, you must create a cropped bitmap using "createBitmap" (only when it
is updated when the angle changes enough, not every frame!) to pass to the canvas,
    as you can't use both the matrix and the rect in the same draw method. OR if that's too slow,
    just spilt the snowboard spritesheet into different images!!!*/

    private int frameWidth;
    private int frameHeight;
    private float x;
    private float y;
    private boolean stopAnimation;

    private Bitmap spritesheetBitmap;

    // A rectangle to define an area of the
    // sprite sheet that represents 1 frame
    private Rect frameToDraw;

    // How many frames are there on the sprite sheet?
    private int frameCount;

    // Start at the first frame - where else?
    private int currentFrame = 0;

    // What time was it when we last changed frames
    private long lastFrameChangeTime = 0;

    // How long should each frame last
    private int frameLengthInMilliseconds;

    // A rect that defines an area of the screen
// on which to draw
    RectF whereToDraw;

    public SpritesheetAnimation (Bitmap rawBitmap, int frameCount,
                                 int frameLengthMillis, int frameWidth,int frameHeight) {
        this.frameWidth=frameWidth;
        this.frameHeight=frameHeight;
        this.frameCount=frameCount;
        this.frameLengthInMilliseconds=frameLengthMillis;
        frameToDraw = new Rect(
                0,
                0,
                frameWidth,
                frameHeight);
        whereToDraw = new RectF(x,y,x+frameWidth, y+frameHeight);

        spritesheetBitmap = Bitmap.createScaledBitmap(rawBitmap,
                frameWidth * frameCount,
                frameHeight,
                false);
        stopAnimation=true;


    }

    public Bitmap getSpritesheetBitmap(){return  spritesheetBitmap;}

    public void setStopAnimation(boolean stopped){
        stopAnimation=stopped;
        if (stopAnimation){
            frameToDraw.left = 0;
            frameToDraw.right = frameWidth;
        }
    }

    public boolean getStopAnimation(){return stopAnimation;}

    public Rect getFrameToDraw(){return frameToDraw;}

    public RectF getWhereToDraw(){return whereToDraw;}

    public void setX(float x){this.x=x;}

    public void setY(float y){this.y=y;}

    public void update(){

        whereToDraw.set(x,y,x+frameWidth, y+frameHeight);

        long time  = System.currentTimeMillis();

        if ( time > lastFrameChangeTime + frameLengthInMilliseconds) {
            lastFrameChangeTime = time;
            currentFrame ++;
            if (currentFrame >= frameCount) {

                currentFrame = 0;
                stopAnimation=true;
            }
        }

        //update the left and right values of the source of
        //the next frame on the spritesheet
        frameToDraw.left = currentFrame * frameWidth;
        frameToDraw.right = frameToDraw.left + frameWidth;

    }



}
