package com.newvoyage.game.frosty;

import android.content.Intent;
import android.graphics.Point;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

public class DownhillGameActivity extends AppCompatActivity {

        DownhillGameView DownhillGameView;
        FrameLayout game;// Sort of "holder" for everything we are placing
     View GameButtons;//Holder for the buttons
    TextView gameOverText;
    boolean beatHighScore;
    int score;


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


            //get the intent that created this activity, and the corresponding difficulty settings
            Intent myIntent = getIntent();

            // Initialize gameView and set it as the view
            game = new FrameLayout(this);
            LayoutInflater layOutInflater = (LayoutInflater) getSystemService (LAYOUT_INFLATER_SERVICE);
            GameButtons = layOutInflater.inflate (R.layout.buttons_holder, null);

            gameOverText = (TextView) GameButtons.findViewById(R.id.game_over);

            DownhillGameView = new DownhillGameView(this, size.x, size.y, handler);

            game.addView(DownhillGameView);
            game.addView(GameButtons);
            setContentView(game);


        }

        // This method executes when the player starts the game
        @Override
        protected void onResume() {
            super.onResume();

            // Tell the gameView resume method to execute
            DownhillGameView.resume();
        }

        // This method executes when the player quits the game
        @Override
        protected void onPause() {
            super.onPause();

            // Tell the gameView pause method to execute
            DownhillGameView.pause();
           // Log.d("paused", "dga");
        }

    public void callGameOverScreen(final int gameScore, final boolean beatHighGameScore){
        DownhillGameActivity.this.runOnUiThread(new Runnable() {
            public void run() {
                GameButtons.setVisibility(View.VISIBLE);
                score=gameScore;
                beatHighScore=beatHighGameScore;
                if (beatHighScore==true) {
                    gameOverText.setText("New High Score! \nYour score is " + score + "\nContinue?");
                }else{
                    gameOverText.setText("Your score is " + score);
                }
            }
        });
    }

    public void backToMain(View gameButtons){
        GameButtons.setVisibility(View.GONE);
        Intent intent = new Intent(this, WelcomeScreenActivity.class);
        this.startActivity(intent);
    }

    public void playAgain(View gameButtons){
        GameButtons.setVisibility(View.GONE);
        DownhillGameView.prepareLevel();
    }

    public void shareIt(View gameButtons){
        DownhillGameView.shareIt();
    }

    }