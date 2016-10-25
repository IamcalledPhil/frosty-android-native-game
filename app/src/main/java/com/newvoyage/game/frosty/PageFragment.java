package com.newvoyage.game.frosty;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * Created by phil on 5/4/16.
 */
public class PageFragment extends Fragment {
    private static final String ARG_PAGE_NUMBER = "page_number";
    private static final String ARG_SCORE_LIST = "score_list";
    private String[] displayScoreList;

    public PageFragment() {
    }

    public static PageFragment newInstance(int page, String[] scoreList) {
        PageFragment fragment = new PageFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE_NUMBER, page);
        args.putStringArray(ARG_SCORE_LIST,scoreList);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_page_layout, container, false);
        displayScoreList = getArguments().getStringArray(ARG_SCORE_LIST);
        Log.d("scores",displayScoreList[0] );
           ArrayAdapter adapter = new ArrayAdapter<String>(getContext(),R.layout.activity_listview,displayScoreList);
        ListView listView = (ListView)rootView.findViewById(R.id.highscore_list);
        listView.setAdapter(adapter);

        return rootView;
    }


}
