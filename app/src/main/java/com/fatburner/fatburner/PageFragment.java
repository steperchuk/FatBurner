package com.fatburner.fatburner;

/**
 * Created by sete on 6/15/2017.
 */

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextClock;
import android.widget.TextView;

public class PageFragment extends Fragment {

    static final String ARGUMENT_PAGE_NUMBER = "arg_page_number";
    static final String SAVE_PAGE_NUMBER = "save_page_number";

    int pageNumber;

    static PageFragment newInstance(int page) {
        PageFragment pageFragment = new PageFragment();
        Bundle arguments = new Bundle();
        arguments.putInt(ARGUMENT_PAGE_NUMBER, page);
        pageFragment.setArguments(arguments);
        return pageFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pageNumber = getArguments().getInt(ARGUMENT_PAGE_NUMBER);

        int savedPageNumber = -1;
        if (savedInstanceState != null) {
            savedPageNumber = savedInstanceState.getInt(SAVE_PAGE_NUMBER);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment, null);

        TextClock breakfestTime = (TextClock) view.findViewById(R.id.breakfestTime);
        TextClock secondBreakfestTime = (TextClock) view.findViewById(R.id.secondBreakfestTime);
        TextClock lunchTime = (TextClock) view.findViewById(R.id.lunchTime);
        TextClock secondLunchTime = (TextClock) view.findViewById(R.id.secondLunchTime);
        TextClock dinnerTime = (TextClock) view.findViewById(R.id.dinnerTime);

        TextView tvPage = (TextView) view.findViewById(R.id.tvPage);
        tvPage.setBackgroundColor(Color.WHITE);

        switch (pageNumber) {
            case 1:
                tvPage.setText("Mnd");
            break;
            case 2:
                tvPage.setText("Tue");
                break;
            case 3:
                tvPage.setText("Wen");
                break;
            case 4:
                tvPage.setText("Thu");
                break;
            case 5:
                tvPage.setText("Fri");
                break;
            case 6:
                tvPage.setText("Sat");
                break;
            case 7:
                tvPage.setText("Sun");
                break;
        }

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(SAVE_PAGE_NUMBER, pageNumber);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}
