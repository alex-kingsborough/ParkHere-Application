package com.example.parkhere.provider;

import android.annotation.SuppressLint;

import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidGridAdapter;

import java.util.ArrayList;
import java.util.List;

public class CaldroidSampleCustomFragment extends CaldroidFragment {
    private List<String> startDates;

    public CaldroidSampleCustomFragment() {

    }

    @SuppressLint("ValidFragment")
    public CaldroidSampleCustomFragment(List<String> startDates) {
        this.startDates = startDates;
    }

    @Override
    public CaldroidGridAdapter getNewDatesGridAdapter(int month, int year) {
        // TODO Auto-generated method stub
        return new CaldroidSampleCustomAdapter(getActivity(), month, year,
                getCaldroidData(), extraData, startDates);
    }

}


