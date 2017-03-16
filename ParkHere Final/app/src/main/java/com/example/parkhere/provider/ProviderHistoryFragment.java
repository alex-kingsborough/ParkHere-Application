package com.example.parkhere.provider;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.parkhere.R;
import com.example.parkhere.objects.ParkingSpace;
import com.example.parkhere.objects.User;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ProviderHistoryFragment extends Fragment {
    private User user;
    private static final String USER_KEY = "user";
    private TextView phistory_table_label;
    private TableLayout tl;
    private Button phistory_view_history_button, phistory_view_ratings_button;
    private FragmentActivity myContext;
    private String selectedAddress = "";
    private String selectedNumRes = "";
    private String selectedAvgRating = "";
    private int selectedIndex = -1;
    private View rootView;
    private long stime, etime, duration;


    public static ProviderHistoryFragment newInstance(User user) {
        ProviderHistoryFragment fragment = new ProviderHistoryFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(USER_KEY, user);
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
        stime = System.nanoTime();

        user = (User) getArguments().getSerializable(USER_KEY);

        rootView = inflater.inflate(R.layout.fragment_provider_history, container, false);

        phistory_table_label = (TextView) rootView.findViewById(R.id.phistory_table_label);
        tl = (TableLayout) rootView.findViewById(R.id.phistory_table);
        phistory_view_history_button = (Button) rootView.findViewById(R.id.phistory_view_history_button);
        phistory_view_ratings_button = (Button) rootView.findViewById(R.id.phistory_view_ratings_button);

        getSpaces();

        phistory_view_history_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (selectedNumRes.equals("0")) {
                    Snackbar.make(rootView, "No reservations to show for this space", Snackbar.LENGTH_LONG).show();
                } else {
                    ParkingSpace p = getParkingSpace(selectedAddress);
                    Fragment fragment = ParkingSpaceReservationsFragment.newInstance(user, p);
                    FragmentManager fragmentManager = myContext.getSupportFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
                }
            }
        });

        phistory_view_ratings_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (selectedAvgRating.equals("0.0")) {
                    Snackbar.make(rootView, "No ratings to show for this space", Snackbar.LENGTH_LONG).show();
                } else {
                    ParkingSpace p = getParkingSpace(selectedAddress);
                    Fragment fragment = ParkingSpaceRatingsFragment.newInstance(user, p);
                    FragmentManager fragmentManager = myContext.getSupportFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
                }
            }
        });
        etime = System.nanoTime();
        duration = (etime - stime);
        System.out.println("Provider history fragment took: " + duration);

        return rootView;
    }

    public void getSpaces() {
        if (user.getProviderParkingSpaces() != null) {
            TableRow tr_head = new TableRow(myContext);
            tr_head.setBackgroundColor(Color.GRAY);
            tr_head.setLayoutParams(new TableRow.LayoutParams(
                    TableRow.LayoutParams.FILL_PARENT,
                    TableRow.LayoutParams.WRAP_CONTENT));

            TextView address = new TextView(myContext);
            address.setText("ADDRESS");
            address.setTextColor(Color.WHITE);
            address.setPadding(5, 5, 5, 5);
            tr_head.addView(address);

            TextView numRes = new TextView(myContext);
            numRes.setText("# RESERVATIONS");
            numRes.setTextColor(Color.WHITE);
            numRes.setPadding(5, 5, 5, 5);
            tr_head.addView(numRes);

            TextView avgRating = new TextView(myContext);
            avgRating.setText("AVERAGE RATING");
            avgRating.setTextColor(Color.WHITE);
            avgRating.setPadding(5, 5, 5, 5);
            tr_head.addView(avgRating);

            tl.addView(tr_head, new TableLayout.LayoutParams(
                    TableLayout.LayoutParams.FILL_PARENT,
                    TableLayout.LayoutParams.WRAP_CONTENT));

            ParkingSpace[] p = user.getProviderParkingSpaces();

            for (int i = 0; i < p.length; i++) {
                TableRow tr = new TableRow(myContext);
                tr.setLayoutParams(new TableRow.LayoutParams(
                        TableRow.LayoutParams.FILL_PARENT,
                        TableRow.LayoutParams.WRAP_CONTENT));
                tr.setTag(i + 1);
                tr.setClickable(true);
                tr.setOnClickListener(clickListener);

                TextView p_address = new TextView(myContext);
                p_address.setText(p[i].getAddress());
                p_address.setLayoutParams(new TableRow.LayoutParams(300, 150));
                tr.addView(p_address);

                TextView p_numRes = new TextView(myContext);
                p_numRes.setText(Integer.toString(p[i].getNumReservations()));
                p_numRes.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
                tr.addView(p_numRes);

                TextView p_avgRating = new TextView(myContext);
                String avg = Double.toString(p[i].getAvgRating());
                p_avgRating.setText(avg.substring(0, 3));
                p_avgRating.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
                tr.addView(p_avgRating);

                tl.addView(tr, new TableLayout.LayoutParams(
                        TableLayout.LayoutParams.FILL_PARENT,
                        TableLayout.LayoutParams.WRAP_CONTENT));
            }
        } else {
            TextView phistory_table_label = (TextView) rootView.findViewById(R.id.phistory_table_label);
            phistory_table_label.setText("You haven't listed any parking spaces yet so you don't have any" +
                    "reservation history to view; be sure to add a parking space today!");
            phistory_view_history_button.setVisibility(View.GONE);
            phistory_view_ratings_button.setVisibility(View.GONE);
        }
    }

    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Integer index = (Integer) v.getTag();

            if (index != null) {
                selectRow(index);
            }
        }
    };

    private void selectRow(int index) {
        if (index != selectedIndex) {
            if (selectedIndex >= 0) {
                deselectRow(selectedIndex);
            }
            TableRow tr = (TableRow) tl.getChildAt(index);
            tr.setBackgroundColor(Color.LTGRAY);
            selectedIndex = index;
            TextView tv = (TextView) tr.getChildAt(0);
            selectedAddress = tv.getText().toString();
            tv = (TextView) tr.getChildAt(1);
            selectedNumRes = tv.getText().toString();
            tv = (TextView) tr.getChildAt(2);
            selectedAvgRating = tv.getText().toString();
        }
    }

    private void deselectRow(int index) {
        if (index >= 0) {
            TableRow tr = (TableRow) tl.getChildAt(index);
            tr.setBackgroundColor(Color.parseColor("#F2F7F2"));
        }
    }

    public ParkingSpace getParkingSpace(String address) {
        ParkingSpace[] p = user.getProviderParkingSpaces();
        for (int i = 0; i < p.length; i++) {
            if (address.equals(p[i].getAddress())) {
                return p[i];
            }
        }
        return new ParkingSpace();
    }
}

