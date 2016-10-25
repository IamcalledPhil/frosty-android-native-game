package com.newvoyage.game.frosty;

/**
 * Created by Phil on 21-Oct-16.
 */
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import org.junit.Test;

import static junit.framework.Assert.assertNotSame;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static junit.framework.Assert.assertEquals;
import com.newvoyage.game.frosty.Ship;

@RunWith(MockitoJUnitRunner.class)
public class ShipTest {

    @Mock
    Context mMockContext;


    @Test

    public void testSetGradient() {
         Bitmap snowmanPic = BitmapFactory.decodeResource(mMockContext.getResources(),R.drawable.snowmanbody);
         Bitmap snowboardPicMaster = BitmapFactory.decodeResource(mMockContext.getResources(), R.drawable.snowboardspritesheet);
       Ship testShip = new Ship(mMockContext,500,100,0,snowmanPic,snowboardPicMaster,0);
        testShip.setGradient(20);
        int speed =0;
        speed = testShip.update(10,0);
        assertNotSame(0,speed);
    }
}
