package com.example.parkhere.seeker;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;

import com.example.parkhere.R;
import com.example.parkhere.objects.ParkingReservation;
import com.example.parkhere.objects.ParkingSpace;
import com.example.parkhere.objects.Rating;
import com.example.parkhere.objects.User;
import com.example.parkhere.server.Constants;
import com.example.parkhere.server.RequestInterface;
import com.example.parkhere.server.ServerRequest;
import com.example.parkhere.server.ServerResponse;

import java.sql.Array;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ratings_screen extends AppCompatActivity {
    private User user;
    private ParkingSpace ps;
    private String[] dateRange;
    private int startTime;
    private int endTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ratings_screen);
        user = (User) getIntent().getSerializableExtra("user");
        ps = (ParkingSpace) getIntent().getSerializableExtra("parkingSpace");
        dateRange = (String[]) getIntent().getSerializableExtra("dateRange");
        Log.d("mytag", "start date = "+dateRange[0]);
        Log.d("mytag", "end date = "+dateRange[dateRange.length-1]);
        startTime = (int) getIntent().getSerializableExtra("hourStart");
        Log.d("mytag", "start time = "+startTime);
        endTime = (int) getIntent().getSerializableExtra("hourEnd");
        Log.d("mytag", "end time = "+endTime);

        Button sRate = (Button)findViewById(R.id.sub_rating);

        sRate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // save ratings in db
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(Constants.BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();

                RequestInterface requestInterface = retrofit.create(RequestInterface.class);

                ServerRequest request = new ServerRequest();
                request.setOperation(Constants.RATINGS);

                ParkingReservation pr = new ParkingReservation();
                pr.setDateRange(dateRange);
                pr.setHourStart(startTime);
                pr.setHourEnd(endTime);
                pr.setParkingSpace(ps);
                pr.setPrice(ps.getTotalPrice());
                //pr.setSeeker_id(Integer.parseInt(user.getID()));
                //System.out.println("seeker id = "+pr.getSeeker_id());
                request.setParkingReservation(pr);

                Rating psRating = new Rating();
                RatingBar psStars = (RatingBar)findViewById(R.id.ratingBar);
                psRating.setRating((int) psStars.getNumStars());
                Log.d("mytag", "ps rating = "+psRating.getRating());
                EditText psComment = (EditText)findViewById(R.id.comments_editText1);
                psRating.setComment(psComment.getText().toString());
                Log.d("mytag", "ps comments = "+psRating.getComment().toString());

                Rating providerRating = new Rating();
                RatingBar providerStars = (RatingBar)findViewById(R.id.ratingBar2);
                providerRating.setRating((int) providerStars.getRating());
                Log.d("mytag", "provider rating = "+providerRating.getRating());
                EditText providerComment = (EditText)findViewById(R.id.comments_editText2);
                providerRating.setComment(providerComment.getText().toString());
                Log.d("mytag", "provider comments = "+providerRating.getComment().toString());

                Rating[] ratings = {psRating, providerRating};
                Log.d("mytag", "ratings array size = "+ratings.length);
                request.setRatings(ratings);

                Call<ServerResponse> response = requestInterface.operation(request);

                response.enqueue(new Callback<ServerResponse>() {
                    @Override
                    public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                        ServerResponse resp = response.body();
                        //ParkingReservation[] reservations = resp.getParkingReservations();]
                        // TODO do we get anything back?
                        System.out.println(resp.getMessage());
                    }

                    @Override
                    public void onFailure(Call<ServerResponse> call, Throwable t) {
                        System.out.println("onFailure done");
                        System.out.println("RATINGS NO WORK");
                    }
                });

                Intent intent = new Intent(getApplicationContext(), SeekerHomeActivity.class);
                intent.putExtra("user", user);
                startActivity(intent);
            }
        });
    }
}