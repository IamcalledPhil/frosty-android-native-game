package com.newvoyage.game.frosty;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;

public class WelcomeScreenActivity extends Activity  {

    private Button tutButton;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_screen);

        //get any stored settings
         prefs = getSharedPreferences("samoanewzealand", Context.MODE_PRIVATE);

        //set the buttons
        tutButton = (Button)findViewById(R.id.tutorial_button);




        tutButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), TutorialActivity.class);
                startActivity(intent);
            }
        });

        final ImageView backgroundOne = (ImageView) findViewById(R.id.background_one);
        final ImageView backgroundTwo = (ImageView) findViewById(R.id.background_two);

        final ValueAnimator animator = ValueAnimator.ofFloat(0.0f, -1.0f);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setInterpolator(new LinearInterpolator());
        animator.setDuration(10000L);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                final float progress = (float) animation.getAnimatedValue();
                final float height = backgroundOne.getHeight();
                final float translationY = height * progress;
                backgroundOne.setTranslationY(translationY);
                backgroundTwo.setTranslationY(translationY + height);
            }
        });
        animator.start();
    }

    /** Called when the user clicks the Send button */
    public void playGame(View view) {


        SharedPreferences.Editor edit = prefs.edit();

        if( prefs.contains("notFirstPlaythru")) {
            Intent intent = new Intent(this, DownhillGameActivity.class);
            startActivity(intent);
        }else{
            Intent intent = new Intent(this, TutorialActivity.class);
            startActivity(intent);
        }

    }

    public void playTutorial(View view) {
        Intent intent = new Intent(this, TutorialActivity.class);
        startActivity(intent);
    }

    public void viewHighscores(View view) {
        Intent intent = new Intent(this, HighscoreActivity.class);
        startActivity(intent);
    }

    public void viewSettings(View view) {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }




}
