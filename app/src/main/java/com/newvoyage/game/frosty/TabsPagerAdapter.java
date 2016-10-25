package com.newvoyage.game.frosty;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by phil on 5/4/16.
 */
public class TabsPagerAdapter extends FragmentPagerAdapter {

    private SharedPreferences prefs;
    private String[] stringScoreListEasy = new String[10];
    private String[] stringScoreListMed = new String[10];
    private String[] stringScoreListHard = new String[10];

    public TabsPagerAdapter(FragmentManager fm, Context context) {

        super(fm);

        prefs = context.getSharedPreferences("Easy",
                Context.MODE_PRIVATE);
       stringScoreListEasy= fetchHighscore();
        prefs = context.getSharedPreferences("Medium",
                Context.MODE_PRIVATE);
       stringScoreListMed= fetchHighscore();
        prefs = context.getSharedPreferences("Hard",
                Context.MODE_PRIVATE);
        stringScoreListHard= fetchHighscore();

    }

    @Override
    public Fragment getItem(int position) {
        if (position==0) {
            return PageFragment.newInstance(position + 1, stringScoreListEasy);
        } else if (position==1) {
            return PageFragment.newInstance(position + 1, stringScoreListMed);
        } else if (position==2) {
            return PageFragment.newInstance(position + 1, stringScoreListHard);
        } else{//need to have a default, difficulty here is arbitrary
            return PageFragment.newInstance(position + 1, stringScoreListHard);

        }
    }

    @Override
    public int getCount() {
        //sets number of tabs
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (position==0){
            return "Easy";
        }else if (position==1){
            return "Medium";
        }else if (position==2){
            return "Hard";
        }else{
            return "";
        }
    }

    public String[] fetchHighscore() {
        String tempstring = "";
        String[]tempStringArray = new String[10];

        int[] scoreList = new int[10];
        int scorePos = 0;
        for (int i=0; i<scoreList.length; i++){//write the scores to a score list
            String scorePosString = "score"+Integer.toString(scorePos);
            int storedScore = prefs.getInt(scorePosString, 0); //0 is the default value
            scoreList[i]=storedScore;
            scorePos++;
        }

        for (int i=0;i<scoreList.length; i++) {
            tempstring = Integer.toString(scoreList[i]);
            tempStringArray[i]=tempstring;
        }
        return tempStringArray;

    }
}
