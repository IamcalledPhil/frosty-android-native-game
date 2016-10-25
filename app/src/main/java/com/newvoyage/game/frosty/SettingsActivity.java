package com.newvoyage.game.frosty;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

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

    public void setDiffButton(){
        if (difficulty==0){
            diffButton.setText("Difficulty: Easy");
        } else if (difficulty==1){
            diffButton.setText("Difficulty: Medium");
        }else if (difficulty==2){
            diffButton.setText("Difficulty: Hard");
        }
    }

}
