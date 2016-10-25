package com.newvoyage.game.frosty;

import android.graphics.Bitmap;

/**
 * Created by phil on 7/2/16.
 */

public class Tile {

    private int tileX, speed, type;
    private float tileY;
    private Bitmap tileImage;
    private int densityUnit;
    private float height;
    private int rows;

    public Tile(int x, int y, int tileWidth, float tileHeight,int typeInt, int densityunit, int rows) {
        tileX = x * tileWidth;
        tileY = y * tileHeight;
        this.densityUnit=densityunit;
        height=tileHeight;
        type = typeInt;
        this.rows=rows;
    }

    public void update(int speed, long fps) {
        tileY = tileY - (densityUnit*(speed/20)) / fps;
        //if the tile goes past the top of the screen, put it back on bottom of array
        if (tileY<0){
            type=0;
            tileY=tileY+(height*rows);
        }
    }

    public int getTileX() {
        return tileX;
    }

    public void setTileX(int tileX) {
        this.tileX = tileX;
    }

    public float getTileY() {
        return tileY;
    }

    public void setTileY(int tileY) {
        this.tileY = tileY;
    }

    public Bitmap getTileImage() {
        return tileImage;
    }

    public void setTileImage(Bitmap tileImage) {
        this.tileImage = tileImage;
    }

    public void setType(int type){this.type=type;}

    public int getType(){return type;}
}