package com.example.parkhere.provider;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.parkhere.R;
import com.example.parkhere.objects.ParkingSpace;
import com.example.parkhere.objects.Rating;
import com.example.parkhere.objects.User;
import com.example.parkhere.server.Constants;
import com.example.parkhere.server.RequestInterface;
import com.example.parkhere.server.ServerRequest;
import com.example.parkhere.server.ServerResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ParkingSpaceRatingsFragment extends Fragment {
    private User user;
    private static final String USER_KEY = "user";
    private ParkingSpace parkingSpace;
    private static final String PARKING_SPACE_KEY = "parkingSpace";
    private FragmentActivity myContext;
    private View rootView;
    private TextView ps_ratings_table_label;
    private TableLayout tl;

    public static ParkingSpaceRatingsFragment newInstance(User user, ParkingSpace parkingSpace) {
        ParkingSpaceRatingsFragment fragment = new ParkingSpaceRatingsFragment();
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

        rootView = inflater.inflate(R.layout.fragment_parking_space_ratings, container, false);

        ps_ratings_table_label = (TextView) rootView.findViewById(R.id.ps_ratings_table_label);
        tl = (TableLayout) rootView.findViewById(R.id.ps_ratings_table);

        ps_ratings_table_label.setText(parkingSpace.getAddress());
        getRatings();

        return rootView;
    }

    public void getRatings() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RequestInterface requestInterface = retrofit.create(RequestInterface.class);

        ServerRequest request = new ServerRequest();
        request.setOperation(Constants.GET_PARKING_SPACE_RATINGS_OPERATION);
        request.setParkingSpace(parkingSpace);
        Call<ServerResponse> response = requestInterface.operation(request);

        response.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, retrofit2.Response<ServerResponse> response) {
                ServerResponse resp = response.body();

                if(resp.getResult().equals(Constants.SUCCESS)){
                    Rating[] r = resp.getRatings();
                    TableRow tr_head = new TableRow(myContext);
                    tr_head.setBackgroundColor(Color.GRAY);
                    tr_head.setLayoutParams(new TableRow.LayoutParams(
                            TableRow.LayoutParams.FILL_PARENT,
                            TableRow.LayoutParams.WRAP_CONTENT));

                    TextView rating = new TextView(myContext);
                    rating.setText("RATING");
                    rating.setTextColor(Color.WHITE);
                    rating.setPadding(5, 5, 5, 5);
                    tr_head.addView(rating);

                    TextView comment = new TextView(myContext);
                    comment.setText("COMMENTS");
                    comment.setTextColor(Color.WHITE);
                    comment.setPadding(5, 5, 5, 5);
                    tr_head.addView(comment);

                    tl.addView(tr_head, new TableLayout.LayoutParams(
                            TableLayout.LayoutParams.FILL_PARENT,
                            TableLayout.LayoutParams.WRAP_CONTENT));

                    for (int i = 0; i < r.length; i++) {
                        TableRow tr = new TableRow(myContext);
                        tr.setLayoutParams(new TableRow.LayoutParams(
                                TableRow.LayoutParams.FILL_PARENT,
                                TableRow.LayoutParams.WRAP_CONTENT));

                        TextView r_rating = new TextView(myContext);
                        r_rating.setText(Integer.toString(r[i].getRating()));
                        r_rating.setLayoutParams(new TableRow.LayoutParams(300, 150));
                        tr.addView(r_rating);

                        TextView r_comment = new TextView(myContext);
                        r_comment.setText(r[i].getComment());
                        r_comment.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
                        tr.addView(r_comment);

                        tl.addView(tr, new TableLayout.LayoutParams(
                                TableLayout.LayoutParams.FILL_PARENT,
                                TableLayout.LayoutParams.WRAP_CONTENT));
                    }

                    String avg = Double.toString(parkingSpace.getAvgRating());
                    TextView avgrating = (TextView) rootView.findViewById(R.id.ps_ratings_avg_label);
                    avgrating.setText("Average Rating: " + avg.substring(0, 3));
                }
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                Snackbar.make(rootView, t.getLocalizedMessage(), Snackbar.LENGTH_LONG).show();
            }
        });
    }
}

