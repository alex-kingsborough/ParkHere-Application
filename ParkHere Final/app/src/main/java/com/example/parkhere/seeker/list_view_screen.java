package com.example.parkhere.seeker;

import android.content.Intent;
import android.location.Location;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.parkhere.R;
import com.example.parkhere.objects.ParkingRequest;
import com.example.parkhere.objects.ParkingSpace;
import com.example.parkhere.objects.User;
import com.example.parkhere.server.Constants;
import com.example.parkhere.server.RequestInterface;
import com.example.parkhere.server.ServerRequest;
import com.example.parkhere.server.ServerResponse;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.sql.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.graphics.Color.DKGRAY;
import static android.graphics.Color.GRAY;
import static android.graphics.Color.LTGRAY;
import static android.graphics.Color.TRANSPARENT;
import static android.graphics.Color.WHITE;

public class list_view_screen extends AppCompatActivity {

    //String [] testArray;
    private User user; // TO DO: GET USER FROM SEARCH SCREEN
    //private boolean first = true;
    private int hourStart = -1;
    private int hourEnd = -1;
    private String dateStart;
    private String dateEnd;
    private String[] types = null;
    private String[] dateRange = null;
    private double[] latlngs;
    private double latitude = 0;
    private double longitude = 0;
    private double dist = 3;
    private int rating = 0;
    private int permit = 0;
    private ParkingSpace[] spaces = null;
    LinearLayout ll = null;
    RelativeLayout r;
    //Spinner sortBy, ascendingVsDescending;
    private boolean isRepeatSearch;
    private String weekDay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view_screen);

        r = (RelativeLayout) findViewById(R.id.activity_list_view_screen);
        ll = (LinearLayout) findViewById(R.id.list_view_button_layout);
        //sortBy = (Spinner) findViewById(R.id.sort_by_spinner);
        //ascendingVsDescending = (Spinner) findViewById(R.id.ascending_descending_spinner);

        //testArray = (String [])getResources().getStringArray(R.array.list_view_array);

        if (getIntent().getExtras() != null) {
//            price = getIntent().getExtras().getInt("price");
//            distance = getIntent().getExtras().getDouble("distance");
//            rating = getIntent().getExtras().getInt("rating");
//            //size = getIntent().getExtras().getString("size");
//            permit = getIntent().getExtras().getInt("permit");
//            p_rating = getIntent().getExtras().getInt("p_rating");
//            Log.d("myTag", "price = " + price + ", distance = " + distance + ", rating = " + rating
//                    + ", permit = " + permit + ", provider rating = " + p_rating);
            user = (User) getIntent().getSerializableExtra("user");

            if (getIntent().getExtras().getDoubleArray("latlng") != null) {
                latlngs = getIntent().getExtras().getDoubleArray("latlng");
                latitude = latlngs[0];
                longitude = latlngs[1];
            }
            if (getIntent().getExtras().getStringArray("types") != null) {
                types = getIntent().getExtras().getStringArray("types");
                Log.d("mytag", "TYPES RECEIVED IN LISTVIEW:");
                for(int i = 0; i < types.length; i++) {
                    Log.d("mytag", types[i]);
                }
                Log.d("mytag", "FINISHED");
            }
            hourStart = getIntent().getExtras().getInt("hourStart");
            hourEnd = getIntent().getExtras().getInt("hourEnd");
            if(getIntent().getExtras().getDouble("distance")!=0.0) {
                dist = getIntent().getExtras().getDouble("distance");
            }
            if(getIntent().getExtras().getInt("rating")!=0) {
                rating = getIntent().getExtras().getInt("rating");
            }
            if(getIntent().getExtras().getInt("permit")!=0) {
                permit = getIntent().getExtras().getInt("permit");
            }
            isRepeatSearch = getIntent().getExtras().getBoolean("isRepeatSearch");
            if (isRepeatSearch) {
                weekDay = getIntent().getExtras().getString("weekDay");
            } else {
                weekDay = "";
            }
            if (getIntent().getExtras().get("dateStart") != null) {
                if (getIntent().getExtras().get("dateEnd") != null) {
                    dateStart = getIntent().getExtras().getString("dateStart");
                    dateEnd = getIntent().getExtras().getString("dateEnd");
                    //Log.d("mytag","START DATE = "+s);
                    //Log.d("mytag","END DATE = "+e);
                    dateRange = getDates(dateStart, dateEnd);
                }
            }
        }
        // TO DO: GET USER FROM PREVIOUS SCREEN INSTEAD OF THIS HARD CODING
//        user = new User();
//        user.setEmail("zhengjen@usc.edu");
//        user.setID("3");

        //EditText addressField = (EditText) findViewById(R.id.space_address_editText);

        // do actual search
        if (dateRange == null) {
            Snackbar.make(r, "Invalid Dates", Snackbar.LENGTH_LONG).show();
        } else if (types.length == 0) {
            Snackbar.make(r, "No type selected", Snackbar.LENGTH_LONG).show();
        }else {
            initialSearchProcess();
        }

        Button filtBtn = (Button) findViewById(R.id.button2);

        filtBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), filter_window.class);
                intent.putExtra("latlng", latlngs);
                intent.putExtra("dateStart", dateStart);
                intent.putExtra("dateEnd", dateEnd);
                intent.putExtra("types", types);
                intent.putExtra("user", user);
                intent.putExtra("hourStart", hourStart);
                intent.putExtra("hourEnd", hourEnd);
                startActivity(intent);
            }
        });

        Button sBtn = (Button)findViewById(R.id.sort_button);
        final Spinner s_by_spin = (Spinner)findViewById(R.id.sort_by_spinner);
        final Spinner as_spin = (Spinner)findViewById(R.id.ascending_descending_spinner);
        sBtn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(s_by_spin.getSelectedItem().equals("Distance")){
                            if(as_spin.getSelectedItem().equals("Ascending")){
                                sortDistance(0);
                            }else{sortDistance(1);}
                        }
                        if(s_by_spin.getSelectedItem().equals("Price")){
                            if(as_spin.getSelectedItem().equals("Ascending")){
                                sortPrice(0);
                            }else{sortPrice(1);}
                        }
                        if(s_by_spin.getSelectedItem().equals("Rating")){
                            if(as_spin.getSelectedItem().equals("Ascending")){
                                sortRating(0);
                            }else{sortRating(1);}
                        }
                        if(s_by_spin.getSelectedItem().equals("Provider Rating")){
                            if(as_spin.getSelectedItem().equals("Ascending")){
                                sortProRating(0);
                            }else{sortProRating(1);}
                        }
                        ll.removeAllViews();
                        showSpots();

                    }
                }
        );

    }

    public String[] getDates(String startDate, String endDate) {
        if (isRepeatSearch == false) {
            //Log.d("mytag", "in getDates(): START DATE = "+startDate+" & END DATE = "+endDate);
            ArrayList<String> dates = new ArrayList<String>();
            String[] pieces = startDate.split("/");
            int[] start = {Integer.parseInt(pieces[0]), Integer.parseInt(pieces[1]), Integer.parseInt(pieces[2])};

            String[] pieces2 = endDate.split("/");
            int[] end = {Integer.parseInt(pieces2[0]), Integer.parseInt(pieces2[1]), Integer.parseInt(pieces2[2])};

            int cur_mon = start[0];
            int cur_day = start[1];
            int cur_year = start[2];

            if ((cur_year > end[2]) || (cur_year == end[2] && cur_mon > end[0])
                    || (cur_year == end[2] && cur_mon == end[0] && cur_day > end[1])) {
                return null;
            }

            if (cur_mon == end[0] && cur_day == end[1] && cur_year == end[2]) {
                if (cur_day < 10) dates.add(cur_year + "-" + cur_mon + "-0" + cur_day);
                else dates.add(cur_year + "-" + cur_mon + "-" + cur_day);
            }
            while (!(cur_mon == end[0] && cur_day == end[1] && cur_year == end[2])) {
                String date;
                if (cur_day < 10) date = cur_year + "-" + cur_mon + "-0" + cur_day;
                else date = cur_year + "-" + cur_mon + "-" + cur_day;
                dates.add(date);
                if ((cur_day + 1) > Constants.DAYS_IN_MONTH[cur_mon - 1]) {
                    if ((cur_mon + 1) > 12) {
                        cur_year++;
                    }
                    cur_day = (cur_day + 1) % Constants.DAYS_IN_MONTH[cur_mon - 1];
                    if (cur_mon == 12) cur_mon = 1;
                    else cur_mon++;
                    //cur_mon = (cur_mon+1) % 12;
                } else {
                    cur_day++;
                }
            }
            if (end[1] < 10) dates.add(end[2] + "-" + end[0] + "-0" + end[1]);
            else dates.add(end[2] + "-" + end[0] + "-" + end[1]);

            String[] temp = dates.toArray(new String[dates.size()]);
        /*Log.d("mytag", "Date Range returning contains:");
        for(int i = 0; i < temp.length; i++) {
            Log.d("mytag", i+". "+temp[i]);
        }*/

            return temp;
        } else {
            ArrayList<String> dates = new ArrayList<String>();
            try {
                SimpleDateFormat sdf = null;
                Date startdate = null;
                Date enddate = null;

                try {
                    String[] pieces = startDate.split("/");
                    String s = pieces[2] + "-" + pieces[0] + "-" +pieces[1];
                    sdf = new SimpleDateFormat("yyyy-MM-dd");
                    startdate = sdf.parse(s);
                    //System.out.println(startdate);

                    pieces = endDate.split("/");
                    s = pieces[2] + "-" + pieces[0] + "-" +pieces[1];
                    sdf = new SimpleDateFormat("yyyy-MM-dd");
                    enddate = sdf.parse(s);
                    //System.out.println(enddate);

                } catch (ParseException ex) {
                    System.out.println("ERROR" + ex.getLocalizedMessage());
                    //ex.printStackTrace();
                }

                while (startdate.before(enddate) || startdate.equals(enddate)) {
                    Calendar c = Calendar.getInstance();
                    c.setTime(startdate);
                    startDate = sdf.format(c.getTime());
                    dates.add(startDate);

                    c.add(Calendar.DATE, 7);  // number of days to add
                    startDate = sdf.format(c.getTime());
                    startdate = sdf.parse(startDate);
                    //System.out.println("start date is now " + startDate);
                    //System.out.println(startdate.before(enddate));
                    //System.out.println(startdate.equals(enddate));
                }
            } catch (ParseException ex) {
                //System.out.println("ERROR" + ex.getLocalizedMessage());
                //ex.printStackTrace();
            }

            for (int i = 0; i < dates.size(); i++) {
                System.out.println(dates.get(i));
            }

            String[] temp = dates.toArray(new String[dates.size()]);
            return temp;
        }
    }

    public void filter(ParkingSpace[] allSpaces) {
        //Log.d("mytag", "parking spaces = "+allSpaces.length);
        ArrayList<ParkingSpace> filteringList = new ArrayList<ParkingSpace>();
        for(int i = 0; i < allSpaces.length; i++) {
            ParkingSpace p = allSpaces[i];
            float[] result = new float[1];
            Location.distanceBetween(latitude, longitude, p.getLat(), p.getLong(), result);
            float m = result[0];
            double miles = m / 1609.344;
            Log.d("mytag", "MILES = "+miles);
            if (miles <= dist) {
                p.setDistance(miles);
                if(p.getRating() >= rating) {
                    if(p.getPermit() == 1) { // if permit is required for the spot
                        System.out.println("permit req" + p.getPermit());
                        if(permit==1 || permit==0) { // if permit required or either add it
                            filteringList.add(p);
                        }
                    } else { // if permit is not required for the spot
                        System.out.println("permit not req" + p.getPermit());
                        if(permit==-1 || permit==0) { // if they don't want permits or they're fine with either
                            filteringList.add(p);
                        }
                    }
                }
            }
        }
        spaces = filteringList.toArray(new ParkingSpace[filteringList.size()]);
        //Log.d("mytag", "spaces length = "+spaces.length);
        sortDistance(0);
        showSpots();
    }

    public void showSpots() {
        for(int i = 0; i < spaces.length; i++){
            System.out.println("THERE'S A SPOT");
            Button btn = new Button(this);
            btn.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            btn.setId(i);
            btn.setText((i+1)+". "+spaces[i].getAddress()+" - "+String.format("%.2f",spaces[i].getDistance())+" mi, $"
                    +String.format("%.2f",spaces[i].getTotalPrice())+", "+String.format("%.1f",spaces[i].getRating())+"/5.0");
            if(spaces[i].getCount()==0) btn.setBackgroundColor(WHITE); // 0 white
            else if(spaces[i].getCount()<=1) btn.setBackgroundColor(LTGRAY); // 1 light gray
            else if(spaces[i].getCount()<=2) btn.setBackgroundColor(GRAY); // 2 gray
            else btn.setBackgroundColor(DKGRAY); // 3 & up dark gray
            ll.addView(btn);
            final int currID = i;
            btn.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(getApplicationContext(), parking_space_screen.class);
                            intent.putExtra("parkingSpot", spaces[currID]);
                            intent.putExtra("currLat", latitude);
                            intent.putExtra("currLong", longitude);
                            intent.putExtra("user", user);
                            intent.putExtra("dateRange", dateRange);
                            intent.putExtra("hourStart", hourStart);
                            intent.putExtra("hourEnd", hourEnd);
                            startActivity(intent);

                        }
                    }
            );
        }


        if(spaces.length == 0){
            TextView tv = new TextView(this);
            tv.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            tv.setText("There are currently no available parking spots, try adjusting your filters to find more spaces");
            tv.setTextSize(20);
            ll.addView(tv);
        }
    }


    public void initialSearchProcess() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RequestInterface requestInterface = retrofit.create(RequestInterface.class);

        ServerRequest request = new ServerRequest();
        ParkingRequest parkingRequest = new ParkingRequest();
        if (isRepeatSearch == false) {
            request.setOperation(Constants.INITIAL_SEARCH_OPERATION);
        } else {
            request.setOperation(Constants.SEARCH_REPEATED_OPERATION);
            parkingRequest.setWeekDay(weekDay);
            System.out.println("I'm setting weekday to " + weekDay);
        }
        parkingRequest.setDateRange(dateRange);
        parkingRequest.setHourStart(hourStart);
        parkingRequest.setHourEnd(hourEnd);
        parkingRequest.setType(types);
        request.setParkingRequest(parkingRequest);

        //TEST
        System.out.println("OUR TEST");
        System.out.println(isRepeatSearch);
        System.out.println(parkingRequest.getWeekDay());
        for (int i = 0; i < dateRange.length; i++) {
            System.out.println(dateRange[i]);
        }
        System.out.println(parkingRequest.getHourStart());
        System.out.println(parkingRequest.getHourEnd());
        for (int i = 0; i < types.length; i++) {
            System.out.println(types[i]);
        }
        //END TEsT

        Call<ServerResponse> response = requestInterface.operation(request);

        response.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                ServerResponse resp = response.body();
                ParkingSpace[] allSpaces = resp.getParkingSpaces();
                System.out.println("SIZE : " + allSpaces.length);
                filter(allSpaces);
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                System.out.println("onFailure done");
                System.out.println("SEARCH NO WORK");
                //Snackbar.make(register_scroll_view, t.getLocalizedMessage(), Snackbar.LENGTH_LONG).show();
            }
        });
    }


    public void sortPrice(int ad) {
        final int a_d = ad;
        Arrays.sort(spaces, new Comparator<ParkingSpace>() {
            public int compare(ParkingSpace p1, ParkingSpace p2) {
                if(a_d == 0){
                    if(p1.getTotalPrice() < p2.getTotalPrice()) return -1;
                    else return 1;
                } else {
                    if(p2.getTotalPrice() < p1.getTotalPrice()) return -1;
                    else return 1;
                }
            }
        });
    }

    public void sortDistance(int ad) {
        final int a_d = ad;
        Arrays.sort(spaces, new Comparator<ParkingSpace>() {
            public int compare(ParkingSpace p1, ParkingSpace p2) {
                if(a_d == 0){
                    if(p1.getDistance() < p2.getDistance()) return -1;
                    else return 1;
                } else {
                    if(p2.getDistance() < p1.getDistance()) return -1;
                    else return 1;
                }
            }
        });
    }

    public void sortRating(int ad) {
        final int a_d = ad;
        Arrays.sort(spaces, new Comparator<ParkingSpace>() {
            public int compare(ParkingSpace p1, ParkingSpace p2) {
                if(a_d == 0){
                    if(p1.getRating() < p2.getRating()) return -1;
                    else return 1;
                } else {
                    if(p2.getRating() < p1.getRating()) return -1;
                    else return 1;
                }
            }
        });
    }

    public void sortProRating(int ad) {
        final int a_d = ad;
        Arrays.sort(spaces, new Comparator<ParkingSpace>() {
            public int compare(ParkingSpace p1, ParkingSpace p2) {
                if(a_d == 0){
                    if(p1.getpRating() < p2.getpRating()) return -1;
                    else return 1;
                } else {
                    if(p2.getpRating() < p1.getpRating()) return -1;
                    else return 1;
                }
            }
        });
    }
}