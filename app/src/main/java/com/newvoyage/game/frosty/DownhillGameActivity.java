package com.newvoyage.game.frosty;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.FrameLayout;
import android.widget.TextView;

public class DownhillGameActivity extends AppCompatActivity {

        DownhillGameView DownhillGameView;
        FrameLayout game;// Sort of "holder" for everything we are placing
     View GameButtons;//Holder for the buttons
    View GameTextLayout;
    TextView gameOverText;
    TextView gameText;
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
            GameTextLayout = layOutInflater.inflate (R.layout.game_text, null);

            gameOverText = (TextView) GameButtons.findViewById(R.id.game_over);
            gameText = (TextView) GameTextLayout.findViewById(R.id.game_text_view);

            DownhillGameView = new DownhillGameView(this, size.x, size.y, handler);

            game.addView(DownhillGameView);
            game.addView(GameButtons);
            game.addView(GameTextLayout);
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

    public void callGameText(final int levelNumber, final String TextToDisplay){
        DownhillGameActivity.this.runOnUiThread(new Runnable() {
            public void run() {
                gameText.setText("Level " + levelNumber + "\n" + TextToDisplay);

                // get the center/final radius for the clipping circle
                int cx = gameText.getWidth() / 2;
                int cy = gameText.getHeight() / 2;
                float finalRadius = (float) Math.hypot(cx, cy);

                //runs an animator if the API is high enough and is not the first level, where it's paused
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    if (levelNumber!=1){
                        Animator anim = ViewAnimationUtils.createCircularReveal(gameText, cx, cy, 0, finalRadius);
                        GameTextLayout.setVisibility(View.VISIBLE);
                        anim.start();
                    }else {GameTextLayout.setVisibility(View.VISIBLE);}
                } else{
                    GameTextLayout.setVisibility(View.VISIBLE);
                }


            }
        });
    }

    public void turnOffGameText(){
        DownhillGameActivity.this.runOnUiThread(new Runnable() {
            public void run() {
                // get the center/initial radius for the clipping circle
                int cx = gameText.getWidth() / 2;
                int cy = gameText.getHeight() / 2;
                float initialRadius = (float) Math.hypot(cx, cy);
                //runs an animator if the API is high enough and is not the first level, where it's paused
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    Animator anim = ViewAnimationUtils.createCircularReveal(gameText, cx, cy, initialRadius, 0);
                    // make the view invisible when the animation is done
                    anim.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            GameTextLayout.setVisibility(View.GONE);
                        }
                    });
                    anim.start();
                } else{
                    GameTextLayout.setVisibility(View.GONE);
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