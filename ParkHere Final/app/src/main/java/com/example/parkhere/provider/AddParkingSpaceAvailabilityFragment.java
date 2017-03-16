package com.example.parkhere.provider;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.parkhere.R;
import com.example.parkhere.objects.ParkingSpace;
import com.example.parkhere.objects.User;

public class AddParkingSpaceAvailabilityFragment extends Fragment {
    private User user;
    private ParkingSpace parkingSpace;
    private static final String USER_KEY = "user";
    private static final String PARKING_SPACE_KEY = "parkingSpace";
    private View rootView;
    private FragmentActivity myContext;
    private FragmentTabHost mTabHost;
    private Bundle bundle;
    private Button add_psa_finish_button;

    public static AddParkingSpaceAvailabilityFragment newInstance(User user, ParkingSpace parkingSpace) {
        AddParkingSpaceAvailabilityFragment fragment = new AddParkingSpaceAvailabilityFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(USER_KEY, user);
        bundle.putSerializable(PARKING_SPACE_KEY, parkingSpace);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        myContext=(FragmentActivity) activity;
        super.onAttach(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        user = (User) getArguments().getSerializable(USER_KEY);
        parkingSpace = (ParkingSpace) getArguments().getSerializable(PARKING_SPACE_KEY);

        rootView = inflater.inflate(R.layout.fragment_add_parking_space_availability, container, false);

        add_psa_finish_button = (Button) rootView.findViewById(R.id.add_psa_finish_button);
        add_psa_finish_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Fragment fragment = ProviderHomeFragment.newInstance(user);
                FragmentManager fragmentManager = myContext.getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
            }
        });

        mTabHost = (FragmentTabHost) rootView.findViewById(android.R.id.tabhost);
        mTabHost.setup(getActivity(), getChildFragmentManager(), R.id.realtabcontent);

        bundle = new Bundle();
        bundle.putSerializable(USER_KEY, user);
        bundle.putSerializable(PARKING_SPACE_KEY, parkingSpace);
        mTabHost.addTab(mTabHost.newTabSpec("regular").setIndicator("REGULAR AVAILABILITY"),
                AddParkingSpaceAvailabilityRegularFragment.class, bundle);

        bundle = new Bundle();
        bundle.putSerializable(USER_KEY, user);
        bundle.putSerializable(PARKING_SPACE_KEY, parkingSpace);
        mTabHost.addTab(mTabHost.newTabSpec("repeated").setIndicator("REPEATED AVAILABILITY"),
                AddParkingSpaceAvailabilityRepeatedFragment.class, bundle);

        return rootView;
    }
}
