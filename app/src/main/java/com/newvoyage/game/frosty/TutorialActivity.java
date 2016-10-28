package com.newvoyage.game.frosty;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
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
    View GameTextLayout;
    Button shareButton;
    TextView gameText;


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
        GameTextLayout = layOutInflater.inflate (R.layout.game_text, null);
        shareButton = (Button)GameButtons.findViewById(R.id.share);

        gameText = (TextView) GameTextLayout.findViewById(R.id.game_text_view);

        TutorialGameView = new TutorialGameView(this, size.x, size.y, handler);
        tutGame.addView(TutorialGameView);
        tutGame.addView(tutGameButtons);
        tutGame.addView(GameButtons);
        tutGame.addView(GameTextLayout);
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

    public void callGameText( final boolean begingame,final String TextToDisplay){
        TutorialActivity.this.runOnUiThread(new Runnable() {
            public void run() {
                gameText.setText( TextToDisplay);

                // get the center/final radius for the clipping circle
                int cx = gameText.getWidth() / 2;
                int cy = gameText.getHeight() / 2;
                float finalRadius = (float) Math.hypot(cx, cy);

                //runs an animator if the API is high enough and is not the first level, where it's paused
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    if (!begingame){
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
        TutorialActivity.this.runOnUiThread(new Runnable() {
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
                            TutorialGameView.setCanStartNextAnimation (true);
                        }
                    });
                    anim.start();
                } else{
                    GameTextLayout.setVisibility(View.GONE);
                    TutorialGameView.setCanStartNextAnimation (true);
                }
            }
        });
    }

    public void setGameTextFromGameView(final String newText){
        TutorialActivity.this.runOnUiThread(new Runnable() {
            public void run() {
        gameText.setText(newText);
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