package com.newvoyage.game.frosty;

import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

/**
 * Created by phil on 4/20/16.
 */
public class TutorialActivity extends AppCompatActivity {

    TutorialGameView TutorialGameView;
    FrameLayout tutGame;// Sort of "holder" for everything we are placing
    View tutGameButtons;//Holder for the buttons
    View GameButtons;//Holder for the buttons
    Button shareButton;
    TextView gameOverText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get a Display object to access screen details
        Display display = getWindowManager().getDefaultDisplay();
        // Load the resolution into a Point object
        Point size = new Point();
        display.getSize(size);

        //enables the view class to interact with the UI elements of the activity class, eg for dialog boxes
        Handler handler = new Handler();

        // Initialize gameView and set it as the view

        tutGame = new FrameLayout(this);
        LayoutInflater layOutInflater = (LayoutInflater) getSystemService (LAYOUT_INFLATER_SERVICE);
        tutGameButtons = layOutInflater.inflate (R.layout.tut_buttons_holder, null);
        GameButtons = layOutInflater.inflate (R.layout.buttons_holder, null);
        shareButton = (Button)GameButtons.findViewById(R.id.share);

        TutorialGameView = new TutorialGameView(this, size.x, size.y, handler);
        tutGame.addView(TutorialGameView);
        tutGame.addView(tutGameButtons);
        tutGame.addView(GameButtons);
        setContentView(tutGame);

    }

    // This method executes when the player starts the game
    @Override
    protected void onResume() {
        super.onResume();

        // Tell the gameView resume method to execute
        TutorialGameView.resume();
    }

    // This method executes when the player quits the game
    @Override
    protected void onPause() {
        super.onPause();

        // Tell the gameView pause method to execute
        TutorialGameView.pause();
        Log.d("paused", "dga");
    }

    public void callFinishedTutorialScreen(){
        TutorialActivity.this.runOnUiThread(new Runnable() {
            public void run() {
                tutGameButtons.setVisibility(View.VISIBLE);
            }
        });
    }

    public void callGameOverScreen(){
        TutorialActivity.this.runOnUiThread(new Runnable() {
            public void run() {
                shareButton.setVisibility(View.GONE);
                GameButtons.setVisibility(View.VISIBLE);
            }
        });
    }

    public void startMainGame(View tutGameButtons){
        tutGameButtons.setVisibility(View.GONE);
        Intent intent = new Intent(this, DownhillGameActivity.class);
        this.startActivity(intent);
    }

    public void backToMain(View gameButtons){
        GameButtons.setVisibility(View.GONE);
        Intent intent = new Intent(this, WelcomeScreenActivity.class);
        this.startActivity(intent);
    }

    public void playAgain(View gameButtons){
        GameButtons.setVisibility(View.GONE);
        TutorialGameView.prepareLevel();
    }



}