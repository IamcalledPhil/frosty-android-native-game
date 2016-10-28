package com.newvoyage.game.frosty;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import mehdi.sakout.aboutpage.AboutPage;

public class SettingsActivity extends AppCompatActivity {

    private SeekBar sensitivity = null;
    private SharedPreferences prefs;
    private int progressChanged = 0;
    private int difficulty ;
    private Button diffButton;
    private TextView sensitivityText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        prefs = getSharedPreferences("samoanewzealand", Context.MODE_PRIVATE);
        sensitivity = (SeekBar) findViewById(R.id.sensitivity_slider);

        diffButton = (Button)findViewById(R.id.difficulty_button);
        sensitivityText = (TextView)findViewById(R.id.sensitivity_text);
        if( prefs.contains("sensitivity")) {
            progressChanged  = prefs.getInt("sensitivity", 0);
            sensitivityText.setText("Sensitivity: " + progressChanged);
            sensitivity.setProgress(progressChanged);
        }else {
            sensitivityText.setText("Sensitivity: 5");
            progressChanged=5;
        }

        if( prefs.contains("difficulty")) {
            difficulty = prefs.getInt("difficulty", 0);
            setDiffButton();
        }else {
            difficulty = 1;
            setDiffButton();
        }
        diffButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (difficulty != 2) {
                    difficulty++;
                } else {
                    difficulty = 0;
                }
                setDiffButton();
            }
        });

        sensitivity.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChanged = progress;
                sensitivityText.setText("Sensitivity: " + progress);

            }

            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        final ImageView backgroundOne = (ImageView) findViewById(R.id.background_one_settings);
        final ImageView backgroundTwo = (ImageView) findViewById(R.id.background_two_settings);

        animateBackground(backgroundOne, backgroundTwo);


    }

    public void onBackPressed() {
        SharedPreferences.Editor edit = prefs.edit();

        edit.putInt("sensitivity", progressChanged);
        edit.commit();

        edit.putInt("difficulty", difficulty);
        edit.commit();
        SettingsActivity.super.onBackPressed();

    }

    public void changeDifficulty(View view) {
        if (difficulty!=2) {
            difficulty++;
        }else {
            difficulty=0;
        }
        setDiffButton();

    }

    public void goToAboutPage(View view) {
        View aboutPage = new AboutPage(this)
                .isRTL(false)
                .setImage(R.drawable.frosty)
                .setDescription("Frosty was developed by me, Phil, an app developer from the UK. If you like this app, please rate it five stars on Google Play! " +
                        "Or don't, you have your own free will and doing something just because an Android game tells you to probably isn't a great way to live your life. " +" \n\n Oh, and also all rights reserved, don't pirate this app, " +
                        "blah blah blah, legal stuff, whatever it's a free game, no-one's going to pirate it (he says before the pirated version becomes the top-downloaded app on Google Play).")
                .addGroup("Connect with us")
                .addEmail("newvoyagegames@gmail.com")
                .addTwitter("FrostyApp")
                .addPlayStore("com.newvoyage.game.frosty")
                .create();
        setContentView(aboutPage);
    }

    public void setDiffButton(){
        if (difficulty==0){
            diffButton.setText("Difficulty: Easy");
        } else if (difficulty==1){
            diffButton.setText("Difficulty: Medium");
        }else if (difficulty==2){
            diffButton.setText("Difficulty: Hard");
        }
    }

    public void animateBackground(final ImageView back1, final ImageView back2){
        final ValueAnimator animator = ValueAnimator.ofFloat(0.0f, -1.0f);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setInterpolator(new LinearInterpolator());
        animator.setDuration(10000L);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                final float progress = (float) animation.getAnimatedValue();
                final float height = back1.getHeight();
                final float translationY = height * progress;
                back1.setTranslationY(translationY);
                back2.setTranslationY(translationY + height);
            }
        });
        animator.start();
    }

}
