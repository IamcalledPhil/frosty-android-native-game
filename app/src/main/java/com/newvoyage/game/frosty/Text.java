package com.newvoyage.game.frosty;

/**
 * Created by phil on 3/27/16.
 */


public class Text {

    //the values for the text animations
    private float textX;
    private float textY;
    private int textAlpha;
    private float textSize;
    private float screenX;
    private float screenY;
    private boolean isActive;
    private int animationType;

    public Text (int ScreenX, int ScreenY, int animationType){
        this.screenX = ScreenX;
        this.screenY = ScreenY;
        this.animationType = animationType;
        isActive = false;
    }

    public boolean startAnimation() {

        if (animationType == 1) {
            textX = screenX / 3;
            textSize = screenX/30;
            textY = screenY / 3;
        } else if (animationType==2){
            textX = screenX / 3;
            textY = screenY-(screenY/4);
            textSize = screenX/30;
        }  else if (animationType==3){ //for the tutorial
            textX = screenX / 4;
            textY = screenY-(screenY/4);
            textSize = screenX/35;
        }
        else if (animationType==4){ //for the power-ups text
            textX = screenX / 3;
            textY = screenY-(screenY/5);
            textSize = screenX/30;
        }
        isActive = true;
        textAlpha = 255;
        return true;
    }

    public void setInactive(){
        isActive = false;
    }

    public boolean getStatus(){
        return isActive;
    }

    public float getX(){
        return textX;
    }

    public float getY(){
        return textY;
    }

    public float getSize(){
        return textSize;
    }

    public int getAlpha(){
        return textAlpha;
    }


    public void update(long fps){
        float densityUnit=screenY/100;

        //animation for size increase. This must be set to inactive in the game view
        if(animationType ==1){

            //set it to be static once the text reaches a certain point
            if (textSize<screenX/12){
                textSize=textSize+(screenX/400);
                textX = textX - (densityUnit*25) / fps;
            }
        }
        //animation for size and y axis increase.This is set inactive here.
        else if (animationType ==2){
            textY = textY - (densityUnit*60) / fps;
            if (textSize<screenX/10){
                textSize = textSize+(screenX/300);
                textX = textX - (densityUnit*30) / fps;
            }

            //slowly fade the text away
            textAlpha = textAlpha - 5;
            if (textAlpha<0){
                textAlpha=0;
            }
            if (textY<screenY/4){
                setInactive();
                textSize = screenX/30;
            }
        } else if(animationType ==3){

            //set it to be static once the text reaches a certain point
            if (textSize<screenX/20){
                textSize=textSize+(screenX/500);
                textX = textX - (densityUnit*25) / fps;
            }
        } else if (animationType==4){
            textY = textY - (densityUnit*60) / fps;
            if (textSize<screenX/15){
                textSize = textSize+(screenX/300);
                textX = textX - (densityUnit*30) / fps;
            }
            //slowly fade the text away
            textAlpha = textAlpha - 3;
            if (textAlpha<0){
                textAlpha=0;
            }
        }


    }
}
