package com.example.parkhere.seeker;

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
import com.example.parkhere.objects.SeekerReservation;
import com.example.parkhere.objects.User;
import com.example.parkhere.server.Constants;
import com.example.parkhere.server.RequestInterface;
import com.example.parkhere.server.ServerRequest;
import com.example.parkhere.server.ServerResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SeekerHistoryFragment extends Fragment {
    private User user;
    private static final String USER_KEY = "user";
    private FragmentActivity myContext;
    private View rootView;
    private TextView shistory_table_label;
    private TableLayout tl;
    private Button shistory_view_resdet_button;
    private SeekerReservation[] sr;
    private SeekerReservation selectedSeekerReservation;
    private int selectedIndex = -1;

    public static SeekerHistoryFragment newInstance(User user) {
        SeekerHistoryFragment fragment = new SeekerHistoryFragment();
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
        user = (User) getArguments().getSerializable(USER_KEY);

        rootView = inflater.inflate(R.layout.fragment_seeker_history, container, false);

        shistory_table_label = (TextView) rootView.findViewById(R.id.shistory_table_label);
        tl = (TableLayout) rootView.findViewById(R.id.shistory_table);
        shistory_view_resdet_button = (Button) rootView.findViewById(R.id.shistory_view_resdet_button);

        getReservations();

        shistory_view_resdet_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (selectedIndex < 0) {
                    Snackbar.make(rootView, "Must select a reservation to view its details", Snackbar.LENGTH_LONG).show();
                } else {
                    Fragment fragment = SeekerReservationDetailsFragment.newInstance(user, selectedSeekerReservation);
                    FragmentManager fragmentManager = myContext.getSupportFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
                }
            }
        });

        return rootView;
    }

    public void getReservations() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RequestInterface requestInterface = retrofit.create(RequestInterface.class);

        ServerRequest request = new ServerRequest();
        request.setOperation(Constants.GET_SEEKER_RESERVATIONS_OPERATION);
        request.setUser(user);
        Call<ServerResponse> response = requestInterface.operation(request);

        response.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, retrofit2.Response<ServerResponse> response) {
                ServerResponse resp = response.body();

                if(resp.getResult().equals(Constants.SUCCESS)){
                    sr = resp.getSeekerReservations();

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

                    TextView start_date = new TextView(myContext);
                    start_date.setText("START DATE");
                    start_date.setTextColor(Color.WHITE);
                    start_date.setPadding(5, 5, 5, 5);
                    tr_head.addView(start_date);

                    TextView start_time = new TextView(myContext);
                    start_time.setText("START TIME");
                    start_time.setTextColor(Color.WHITE);
                    start_time.setPadding(5, 5, 5, 5);
                    tr_head.addView(start_time);

                    /*TextView end_date = new TextView(myContext);
                    end_date.setText("END DATE");
                    end_date.setTextColor(Color.WHITE);
                    end_date.setPadding(5, 5, 5, 5);
                    tr_head.addView(end_date);

                    TextView end_time = new TextView(myContext);
                    end_time.setText("END TIME");
                    end_time.setTextColor(Color.WHITE);
                    end_time.setPadding(5, 5, 5, 5);
                    tr_head.addView(end_time);*/

                    TextView price = new TextView(myContext);
                    price.setText("PRICE");
                    price.setTextColor(Color.WHITE);
                    price.setPadding(5, 5, 5, 5);
                    tr_head.addView(price);

                    tl.addView(tr_head, new TableLayout.LayoutParams(
                            TableLayout.LayoutParams.FILL_PARENT,
                            TableLayout.LayoutParams.WRAP_CONTENT));

                    for (int i = 0; i < sr.length; i++) {
                        TableRow tr = new TableRow(myContext);
                        tr.setLayoutParams(new TableRow.LayoutParams(
                                TableRow.LayoutParams.FILL_PARENT,
                                TableRow.LayoutParams.WRAP_CONTENT));
                        tr.setTag(i + 1);
                        tr.setClickable(true);
                        tr.setOnClickListener(clickListener);

                        TextView sr_address = new TextView(myContext);
                        sr_address.setText(sr[i].getParkingSpace().getAddress());
                        sr_address.setLayoutParams(new TableRow.LayoutParams(300, 150));
                        tr.addView(sr_address);

                        TextView sr_start_date = new TextView(myContext);
                        sr_start_date.setText(sr[i].getStartDate());
                        sr_start_date.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
                        tr.addView(sr_start_date);

                        TextView sr_start_time = new TextView(myContext);
                        sr_start_time.setText(getTime(sr[i].getStartHour()));
                        sr_start_time.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
                        tr.addView(sr_start_time);

                        /*TextView sr_end_date = new TextView(myContext);
                        sr_end_date.setText(sr[i].getEndDate());
                        sr_end_date.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
                        tr.addView(sr_end_date);

                        TextView sr_end_time = new TextView(myContext);
                        sr_end_time.setText(getTime(sr[i].getEndHour()));
                        sr_end_time.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
                        tr.addView(sr_end_time);*/

                        TextView sr_price = new TextView(myContext);
                        sr_price.setText(Double.toString(sr[i].getTotalPrice()));
                        sr_price.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
                        tr.addView(sr_price);

                        tl.addView(tr, new TableLayout.LayoutParams(
                                TableLayout.LayoutParams.FILL_PARENT,
                                TableLayout.LayoutParams.WRAP_CONTENT));
                    }

                    TextView numRes = (TextView) rootView.findViewById(R.id.shistory_reservations_num_label);
                    numRes.setText("Number of Reservations: " + sr.length);
                } else {
                    shistory_table_label.setText("No Reservations Yet!");
                }
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                Snackbar.make(rootView, t.getLocalizedMessage(), Snackbar.LENGTH_LONG).show();
            }
        });
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
            TextView tv = (TextView) tr.getChildAt(1);
            String startDate = tv.getText().toString();
            tv = (TextView) tr.getChildAt(2);
            String startHour = tv.getText().toString();
            for (int i = 0; i < sr.length; i++) {
                if (sr[i].getStartDate().equals(startDate) && getTime(sr[i].getStartHour()).equals(startHour)) {
                    selectedSeekerReservation = sr[i];
                }
            }
        }
    }

    private void deselectRow(int index) {
        if (index >= 0) {
            TableRow tr = (TableRow) tl.getChildAt(index);
            tr.setBackgroundColor(Color.parseColor("#F2F7F2"));
        }
    }

    public String getTime(int time) {
        if (time == 0 || time == 24) {
            return "12:00 AM";
        } else if (time >= 1 && time <= 9) {
            return "0" + time + ":00 AM";
        } else if (time == 10 || time == 11) {
            return time + ":00 AM";
        } else if (time == 12) {
            return time + ":00 PM";
        } else if (time >= 13 && time <= 21) {
            time -= 12;
            return "0" + time + ":00 PM";
        } else if (time == 22 || time == 23) {
            time -= 12;
            return time + ":00 PM";
        }
        return "Invalid time";
    }
}