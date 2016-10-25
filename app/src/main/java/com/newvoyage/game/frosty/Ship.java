package com.newvoyage.game.frosty;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;

/**
 * Created by Phil on 12/19/2015.
 */
import android.graphics.PointF;


public class Ship {

    PointF a;
    PointF b;
    PointF c;
    PointF d;
    PointF centre;
    PointF pathPoint;

    int screenRight;

    int difficulty;
    /*
    Which way is the ship facing
    Straight up to start with
    */
    int currentDistance=0;
    float facingAngle = 90;
    float previousFA = facingAngle;
    boolean setFacing = true;

    // How long will our spaceship be
    private float length;
    private float width;

    //different speed values
    int fastAcceleration;
    int medAcceleration;
    int fastIncrement;
    int medIncrement;
    int fastestSpeed;
    int slowestSpeed;
    int speedSetter;
    /*
    These next two variables control the actual movement rate per frame
    their values are set each frame based on speed and heading
    */

    private float horizontalVelocity;
    private float verticalVelocity;

    //the ship's horizontal speed in the game
    private float horizontalSpeed;

    private int densityUnit;

    private Bitmap snowmanPic;
    private Bitmap snowboardPicMaster;
    private Bitmap snowboardPicll;
    private Bitmap snowboardPicl;
    private Bitmap snowboardPic0;
    private Bitmap snowboardPicr;
    private Bitmap snowboardPicrr;
    private  Bitmap [] snowboardPics;

    private int snowboardPos;

    private float bitHeight;
    private float bitWidth;

    private int cropwidth;
    private int cropheight;

    //matrix for the bitmap rotation
    private Matrix mat;

    private float[] points;




    /*
    This the the constructor method
    When we create an object from this class we will pass
    in the screen width and height
    */

    public Ship(Context context, int screenX, int screenY, int speed, Bitmap snowmanBitmap, Bitmap snowboardBitmap, int difficulty){

        length = screenY / 8;
        width = screenY / 22;
        horizontalSpeed = speed/4;
        snowboardPos=0;
        slowestSpeed=50;

        this.difficulty=difficulty;
        if(difficulty==0){
            speedSetter=8;
            fastestSpeed=2000;
            fastAcceleration=10;
            medAcceleration=6;
            fastIncrement=3;
            medIncrement=2;
        }else if (difficulty==1){
            speedSetter=7;
            fastestSpeed=2500;
            fastAcceleration=14;
            medAcceleration=9;
            fastIncrement=6;
            medIncrement=4;
        }else if (difficulty==2){
            speedSetter=6;
            fastestSpeed=3000;
            fastAcceleration=35;
            medAcceleration=20;
            fastIncrement=7;
            medIncrement=5;
        }
        densityUnit=screenY/100;
        screenRight=screenX;
        mat = new Matrix();
        //set the height for the bitmap to be a bit bigger to compensate for the image size
        bitHeight = Math.round(length * 1.2);
        bitWidth = Math.round(width * 6);
        cropwidth=Math.round(bitWidth/5);
        cropheight=Math.round(bitHeight);
        snowmanPic = Bitmap.createScaledBitmap(snowmanBitmap, Math.round(cropwidth), Math.round(bitHeight), true);
        snowboardPicMaster = Bitmap.createScaledBitmap(snowboardBitmap, Math.round(bitWidth), Math.round(bitHeight), true);
        snowboardPicll=Bitmap.createBitmap(snowboardPicMaster, 0, 0, cropwidth, cropheight);
        snowboardPicl=Bitmap.createBitmap(snowboardPicMaster, cropwidth, 0, cropwidth, cropheight);
        snowboardPic0=Bitmap.createBitmap(snowboardPicMaster, cropwidth * 2, 0, cropwidth, cropheight);
        snowboardPicr=Bitmap.createBitmap(snowboardPicMaster, cropwidth * 3, 0, cropwidth, cropheight);
        if (cropwidth*5>snowboardPicMaster.getWidth()){
            int shittyPhoneCropWidth = snowboardPicMaster.getWidth()/5;
            snowboardPicrr=Bitmap.createBitmap(snowboardPicMaster, shittyPhoneCropWidth * 4, 0, shittyPhoneCropWidth-2, cropheight);
        } else {
            snowboardPicrr = Bitmap.createBitmap(snowboardPicMaster, cropwidth * 4, 0, cropwidth, cropheight);
        }
       snowboardPics=new Bitmap [5];
        snowboardPics[0]=snowboardPicll;
        snowboardPics[1]=snowboardPicl;
        snowboardPics[2]=snowboardPic0;
        snowboardPics[3]=snowboardPicr;
        snowboardPics[4]=snowboardPicrr;



        a = new PointF();
        b = new PointF();
        c = new PointF();
        d = new PointF();
        centre = new PointF();

        centre.x = screenX / 2;
        centre.y = screenY / 8;

        a.x = centre.x -width/2;
        a.y = centre.y + length/2;

        b.x = centre.x - width / 2;
        b.y = centre.y - length / 2;

        c.x = centre.x + width / 2;
        c.y = centre.y - length / 2;

        d.x = centre.x + width/2;
        d.y = centre.y + length/2;

        points=new float[10];

        points[0]=a.x;
        points[1]=a.y;
        points[2]=b.x;
        points[3]=b.y;
        points[4]=c.x;
        points[5]=c.y;
        points[6]=d.x;
        points[7]=d.y;
        points[8]=centre.x;
        points[9]=centre.y;

        mat.postTranslate(b.x, b.y);

    }

    public PointF getCentre(){
        return  centre;
    }

    public PointF getA(){
        return  a;
    }

    public PointF getB(){
        return  b;
    }

    public PointF getC(){
        return  c;
    }

    public PointF getD(){return  d;}

    public int getSnowboardPos(){return snowboardPos;}

    public Bitmap getSnowmanPic(){return snowmanPic;}

    public Bitmap getSnowboardPic(){return snowboardPics[snowboardPos];}

    public Matrix getMatrix(){return mat;}

    public float getWidth(){return width;}

    public float getHeight(){return length;}

    public int getCurrentDistance(){return currentDistance;}

    public void setGradient(int gradient){
        if (gradient==10){
            slowestSpeed=10;
        } else if (gradient==20){
            if (difficulty==2) {
                fastestSpeed = fastestSpeed + 300;
            }
            slowestSpeed=35;
            fastAcceleration=fastAcceleration+fastIncrement;
            medAcceleration=medAcceleration+medIncrement;
        } else if (gradient==30){
            if (difficulty==2) {
                fastestSpeed = fastestSpeed + 300;
            }
            slowestSpeed=80;
            fastAcceleration=fastAcceleration+fastIncrement;
            medAcceleration=medAcceleration+medIncrement;
        } else if (gradient==40){
            fastestSpeed=fastestSpeed+300;
            slowestSpeed=110;
            fastAcceleration=fastAcceleration+fastIncrement;
            medAcceleration=medAcceleration+medIncrement;
        }
    }

    public void setFacingAngle(float angle){
        if (!setFacing){
            facingAngle=angle;
        }
        setFacing=true;
    }


    /*
    This update method will be called from update in HeadingAndRotationView
    It determines if the player ship needs to move and changes the coordinates
    and rotation when necessary.
    */

    public int update(long fps, int speed){


        horizontalVelocity = (float) (Math.cos(Math.toRadians(facingAngle)));
        verticalVelocity = (float)(Math.sin(Math.toRadians(facingAngle)));

        float newAx;
        float newBx;
        float newCx;
        float newDx;
        float newCentreX;

        // move the ship - 1 point at a time
        newCentreX = centre.x + horizontalVelocity * (densityUnit*(horizontalSpeed/20)) / fps;

        newAx = a.x + horizontalVelocity * (densityUnit*(horizontalSpeed/20)) / fps;

        newBx = b.x + horizontalVelocity * (densityUnit*(horizontalSpeed/20)) / fps;

        newCx = c.x + horizontalVelocity * (densityUnit*(horizontalSpeed/20)) / fps;

        newDx = d.x + horizontalVelocity * (densityUnit*(horizontalSpeed/20)) / fps;

        //don't let ship go past edges of screen
        if (newAx>0 && newDx<screenRight) {
            a.x = newAx;
           b.x = newBx;
            c.x =newCx;
            d.x = newDx;
           centre.x = newCentreX;
        }

        points[0]=a.x;
        points[1]=a.y;
        points[2]=b.x;
        points[3]=b.y;
        points[4] = c.x;
        points[5] =c.y;
        points[6]=d.x;
        points[7]=d.y;
        points[8]=centre.x;
        points[9]=centre.y;

        mat.reset();
        float angleDiff = facingAngle-previousFA;
        mat.postRotate(angleDiff, centre.x, centre.y);

        mat.mapPoints(points);
        a.x=points[0];
        a.y=points[1];
        b.x = points[2];
        b.y=points[3];
        c.x=points[4];
        c.y=points[5];
        d.x=points[6];
        d.y=points[7];
        centre.x = points[8];
        centre.y=points[9];

        //for the bitmap
        mat.reset();
        mat.postRotate(facingAngle-90, 0, 0);
        mat.postTranslate(b.x, b.y);


        //change global speed in response to the new facing angle
        int intAngle = Math.round(facingAngle);

        //see if the angle is getting further away from 90 degrees (starting angle), to see if need to increase or decrease speed
         currentDistance = 90-intAngle;
       // Log.d("angle", ""+currentDistance);

        //edit the pic of the snowboard that is showing, depending on the angle
        if (currentDistance<10 && currentDistance>-10){
            snowboardPos=2;
        } else if (currentDistance>=10 && currentDistance<25){
            snowboardPos=1;
        }else if (currentDistance>=25){
            snowboardPos=0;
        }else if (currentDistance<=-10 && currentDistance>-25){
            snowboardPos=3;
        }else if (currentDistance<=-25){
            snowboardPos=4;
        }

        currentDistance=Math.abs(currentDistance);

        //if pointing downhill, accelerate more
        if (currentDistance<5){
            speed = speed+fastAcceleration;
            horizontalSpeed = speed/2;
        }else if(currentDistance>=5 && currentDistance<15){
            speed = speed+medAcceleration;
            horizontalSpeed = speed/2;
        } else {
            //make ship keep on accelerating/decelerating until it gets to target speed for that angle
            int targetSpeed = (91 - currentDistance) * ((100 - currentDistance) / speedSetter);

            int speedDifference = targetSpeed - speed;
            //make the speed get ever closer to the target speed(altough it never actually approaches, but good enough
            speed = speed + (speedDifference / 30);

            //do the same for horizontal speed
            float targetHorizontalSpeed = (currentDistance*70);
            float horizontalSpeedDifference = targetHorizontalSpeed-horizontalSpeed;
            horizontalSpeed = horizontalSpeed + (horizontalSpeedDifference / 2);
        }

        //set caps on speed and slowness
        if (speed>fastestSpeed){
            speed=fastestSpeed;
        }
        if(speed<slowestSpeed){
            speed=slowestSpeed;
        }

        if (horizontalSpeed>2500){
            horizontalSpeed=2500;
        }

        setFacing=false;

        previousFA=facingAngle;

        return speed;


    }

}
