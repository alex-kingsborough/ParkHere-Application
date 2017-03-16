package com.example.parkhere.seeker;

import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.nfc.Tag;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.example.parkhere.R;
import com.example.parkhere.objects.FilterRequest;
import com.example.parkhere.objects.ParkingRequest;
import com.example.parkhere.objects.ParkingSpace;
import com.example.parkhere.objects.User;
import com.example.parkhere.server.Constants;
import com.example.parkhere.server.RequestInterface;
import com.example.parkhere.server.ServerRequest;
import com.example.parkhere.server.ServerResponse;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Circle;

import org.json.JSONArray;

import java.sql.Array;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Vector;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;

import static com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_AZURE;
import static com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_BLUE;
import static com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_CYAN;
import static com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_YELLOW;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, OnMarkerClickListener {

    private GoogleMap mMap;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    private User user; // TO DO: GET USER FROM SEARCH SCREEN
    // search objects
    private boolean first = true;
    private int hourStart = -1;
    private int hourEnd = -1;
    private String[] types = null;
    private String[] dateRange = null;

    private double latitude = 0;
    private double longitude = 0;

    // filter objects
    private int price = -1;
    private double distance = -1;
    private int rating = -1;
    //private String size = null;
    private int permit = -1;
    private int p_rating = -1;
    private ParkingSpace[] originalSpaces = null;
    private ArrayList<MarkerOptions> currMarkers = new ArrayList<MarkerOptions>();
    private ArrayList<ParkingSpace> currParkingSpaces = new ArrayList<ParkingSpace>();
    RelativeLayout r;
    private boolean isRepeatSearch;
    private String weekDay;

    // FOR TESTING
//    MarkerOptions uscMarker = new MarkerOptions().position(new LatLng(34.0224, -118.2851))
    //          .title("University of Southern California").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        r = (RelativeLayout) findViewById(R.id.map);
        if(getIntent().getExtras() != null){
            user = (User)getIntent().getSerializableExtra("user");
            price = getIntent().getExtras().getInt("price");
            distance = getIntent().getExtras().getDouble("distance");
            rating = getIntent().getExtras().getInt("rating");
            //size = getIntent().getExtras().getString("size");
            permit = getIntent().getExtras().getInt("permit");
            p_rating = getIntent().getExtras().getInt("p_rating");
            //Log.d("myTag", "price = " + price + ", distance = " + distance + ", rating = " + rating
            //        + ", permit = " + permit + ", provider rating = " + p_rating);

            if(getIntent().getExtras().getDoubleArray("latlng") != null) {
                double[] latlngs = getIntent().getExtras().getDoubleArray("latlng");
                latitude = latlngs[0];
                longitude = latlngs[1];
                Log.d("mytag", "in the map now: lat = "+latitude+" and lng = "+longitude);
            }
            if(getIntent().getExtras().getStringArray("types") != null) {
                types = getIntent().getExtras().getStringArray("types");
//                for(int i = 0; i < types.length; i++) {
//                    Log.d("mytag", types[i]);
//                }
            }

            hourStart = getIntent().getExtras().getInt("hourStart");
            hourEnd = getIntent().getExtras().getInt("hourEnd");

            isRepeatSearch = getIntent().getExtras().getBoolean("isRepeatSearch");
            if (isRepeatSearch) {
                weekDay = getIntent().getExtras().getString("weekDay");
            } else {
                weekDay = "";
            }

            if(getIntent().getExtras().get("dateStart") != null) {
                if(getIntent().getExtras().get("dateEnd") != null) {
                    String s = getIntent().getExtras().getString("dateStart");
                    String e = getIntent().getExtras().getString("dateEnd");
                    //Log.d("mytag","START DATE = "+s);
                    //Log.d("mytag","END DATE = "+e);
                    dateRange = getDates(s,e);
                }
            }
        }
        // TO DO: GET USER FROM PREVIOUS SCREEN INSTEAD OF THIS HARD CODING
        /*user = new User();
        user.setEmail("zhengjen@usc.edu");
        user.setID("3");*/

        //latitude = 34.0224;
        //longitude = -118.2851;
        //hourStart = 17;
        //hourEnd = 9;
        //dateRange = getDates("10/31/2016", "11/2/2016");
        //System.out.println("!!!!!!!!!!!!!!!!!!!  "+dateRange.length);//+": 1st = "+dateRange[0]+", 2nd = "+dateRange[1]);
        //types = new String[]{"Compact", "Handicap"};
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
        //Log.d("mytag", "one");
        ParkingSpace p1 = new ParkingSpace();
        p1.setTotalPrice(12.00);
        ParkingSpace p2 = new ParkingSpace();
        //Log.d("mytag", "two");
        p2.setTotalPrice(6.00);
        ParkingSpace p3 = new ParkingSpace();
        p3.setTotalPrice(7.00);
        //Log.d("mytag", "threee");
        ParkingSpace[] testSort = {p1, p2, p3};
        Arrays.sort(testSort, new Comparator<ParkingSpace>(){
            public int compare(ParkingSpace p1, ParkingSpace p2){
                return (int)(p1.getTotalPrice() - p2.getTotalPrice());
            }
        });
        //Log.d("mytag", "four");
        for(int i = 0; i < 3; i++){
            //System.out.println("NOTICE ME" + testSort[i].getTotalPrice());
            //Log.d("mytag", testSort[i].getTotalPrice() + "");
        }


        /*Button button = new Button(this);
        button.setText("Click me");
        addContentView(button, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), filter_window.class);
                startActivity(intent);
            }
        });*/
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setOnMarkerClickListener(this);

        // Add a marker and move the camera
        // bitmap thing is the color/image the marker is
        // mMap.addMarker(uscMarker);

        //Circle circle = mMap.addCircle(new CircleOptions().center(usc).radius(3 * 1609.344).strokeColor(Color.RED)); // multiply to convert to m
        mMap.moveCamera(CameraUpdateFactory.zoomTo((float) 12.20)); // set the zoom level

        mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(latitude, longitude)));

//        Vector<LatLng> markers = new Vector<LatLng>();
//        markers.add(new LatLng(34.027699,-118.289941));
//        markers.add(new LatLng(34.016768, -118.28242));
//        markers.add(new LatLng(34.028122, -118.288985));

        //searchProcess();

        //System.out.println("!!!!!!!!!!! ON MAP READY");

        // PROCESSES
        if(first) {
            //System.out.println("!!!!!!!!!!!  inside first");
            if(dateRange != null) {
                initialSearchProcess();
            } else {
                Snackbar.make(r, "Invalid Dates", Snackbar.LENGTH_LONG).show();
            }
            first = false;
        } else { // filtering
            filterProcess();
        }
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

    public ParkingSpace[] preFilter() {
        ArrayList<ParkingSpace> result = new ArrayList<ParkingSpace>();
        for(int i = 0; i < originalSpaces.length; i++) {
            ParkingSpace curr = originalSpaces[i];

            // distance
            float[] temp = new float[1];
            Location.distanceBetween(latitude, longitude, curr.getLat(), curr.getLong(), temp);
            float m = temp[0];
            double miles = m / 1609.344; // convert meterms to miles
            if (miles > distance) continue;

            // size
            //if(!size.equals("Any")) {
            //if(!size.equals(curr.getSize())) continue;
            //}

            // permit
            if(permit != -1) {
                if(curr.getPermit() == 1) {
                    if(permit == 0) continue;
                } else {
                    if(permit == 1) continue;
                }
            }

            // TODO filter by other stuff

            result.add(curr);
        }

        return result.toArray(new ParkingSpace[result.size()]);
    }

    public void createMarkers(ParkingSpace[] spaces) {
        if(currParkingSpaces != null){
            currParkingSpaces.clear();
            currMarkers.clear();}
        //System.out.println("Should be creating marker!");
        for (int i = 0; i < spaces.length; i++) {
            ParkingSpace p = spaces[i];

            float[] result = new float[1];
            Location.distanceBetween(latitude, longitude, p.getLat(), p.getLong(), result);
            float m = result[0];
            double miles = m / 1609.344;

            // only create markers if they're within 3 miles
            if (miles <= 3) {
                //System.out.println("within 3 miles!");

                // 30 & up blue
                // 11-30 azure
                // 1-10 cyan
                // 0 yellow & partly opaque
                int count = p.getCount();
                float color;
                float alpha = 1;
                if(count == 0) {
                    color = HUE_YELLOW;
                    alpha = 0.5f;
                } else if(count <= 1) {
                    color = HUE_CYAN;
                } else if(count <= 2) {
                    color = HUE_AZURE;
                } else {
                    color = HUE_BLUE;
                }

                MarkerOptions mMarker = new MarkerOptions().position(new LatLng(p.getLat(), p.getLong()))
                        .title("$"+String.format("%.2f",p.getTotalPrice())+", "+String.format("%.1f",spaces[i].getRating())+"/5.0")
                        .icon(BitmapDescriptorFactory.defaultMarker(color))
                        .alpha(alpha);
                mMap.addMarker(mMarker).showInfoWindow();
                currMarkers.add(mMarker);
                currParkingSpaces.add(p);
            }

        }
    }

    @Override
    public boolean onMarkerClick(final Marker marker){
        //Log.d("mytag", "in the onMarkerClick outside the for loop");

        for(int i = 0; i < currMarkers.size(); i++) {
            if(marker.getTitle().equals(currMarkers.get(i).getTitle())) {
                //Log.d("mytag", "in the onMarkerClick");
                Intent intent = new Intent(this, parking_space_screen.class);
                intent.putExtra("parkingSpot", currParkingSpaces.get(i));
                intent.putExtra("currLat", latitude);
                intent.putExtra("currLong", longitude);
                intent.putExtra("user", user);
                intent.putExtra("dateRange", dateRange);
                intent.putExtra("hourStart", hourStart);
                intent.putExtra("hourEnd", hourEnd);
                startActivity(intent);
                return true;
            }
        }
        return false;
    };

    public void initialSearchProcess() {
        //System.out.println("!!!!!!!!!!! inside initial search process");
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
        Call<ServerResponse> response = requestInterface.operation(request);

        response.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                ServerResponse resp = response.body();
                ParkingSpace[] allSpaces = resp.getParkingSpaces();
                //System.out.println("SIZE : " + allSpaces.length);
                originalSpaces = allSpaces;
                createMarkers(allSpaces);
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                //System.out.println("onFailure done");
                //Snackbar.make(register_scroll_view, t.getLocalizedMessage(), Snackbar.LENGTH_LONG).show();
            }
        });
    }


    /*
    public void sortPrice(ParkingSpace[] spaces){
        Arrays.sort(spaces, new Comparator<ParkingSpace>() {
            public int compare(ParkingSpace p1, ParkingSpace p2){
                return (int)(p1.getTotalPrice() - p2.getTotalPrice());
            }
        });

        //return spaces;
    } */


    public void filterProcess() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RequestInterface requestInterface = retrofit.create(RequestInterface.class);

        ServerRequest request = new ServerRequest();
        request.setOperation(Constants.FILTER_SEARCH_OPERATION);
        FilterRequest filterRequest = new FilterRequest();
        filterRequest.setDateRange(dateRange);
        filterRequest.setHourStart(hourStart);
        filterRequest.setHourEnd(hourEnd);
        filterRequest.setRating(rating);
        filterRequest.setpRating(p_rating);
        filterRequest.setPrice(price);
        filterRequest.setParkingSpaces(preFilter());
        request.setFilterRequest(filterRequest);
        Call<ServerResponse> response = requestInterface.operation(request);

        response.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                ServerResponse resp = response.body();
                ParkingSpace[] allSpaces = resp.getParkingSpaces();
                mMap.clear();
                for(int i = 0; i < allSpaces.length; i++) {
                    ParkingSpace p = allSpaces[i];
                    mMap.addMarker(new MarkerOptions().position(new LatLng(p.getLat(), p.getLong())).title("m" + i));
                }
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                //System.out.println("onFailure done");
                //Snackbar.make(register_scroll_view, t.getLocalizedMessage(), Snackbar.LENGTH_LONG).show();
            }
        });
    }


    public void searchProcess() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RequestInterface requestInterface = retrofit.create(RequestInterface.class);

        ServerRequest request = new ServerRequest();
        request.setOperation(Constants.SEARCH_OPERATION);
        Call<ServerResponse> response = requestInterface.operation(request);

        response.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                ServerResponse resp = response.body();

                ParkingSpace[] allSpaces = resp.getParkingSpaces();
                createMarkers(allSpaces);
//                for(int i = 0; i < allSpaces.length; i++) {
//                    System.out.println("parking space #"+i+": ");
//                    ParkingSpace p = allSpaces[i];
//                    System.out.print(p.getId()+", ");
//                    System.out.print(p.getProvider()+", ");
//                    System.out.print(p.getAddress()+", ");
//                    System.out.print(p.getSize()+", ");
//                    System.out.print(p.getPermit()+", ");
//                    System.out.print(p.getCancellationPolicy()+", ");
//                    System.out.println(p.getAdditional());
//                }
                //Snackbar.make(register_scroll_view, resp.getMessage(), Snackbar.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                //System.out.println("onFailure done");
                //Snackbar.make(register_scroll_view, t.getLocalizedMessage(), Snackbar.LENGTH_LONG).show();
            }
        });

    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Maps Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }
}