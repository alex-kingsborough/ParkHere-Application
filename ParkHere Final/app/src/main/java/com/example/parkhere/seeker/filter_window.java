package com.example.parkhere.seeker;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.parkhere.R;
import com.example.parkhere.objects.User;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

public class filter_window extends AppCompatActivity {


    int price = Integer.MAX_VALUE;
    double distance = 3;
    int rating = 0;
    String size = "Any";
    int permit = -1;
    int p_rating = 0;

    private User user;
    private int hourStart = -1;
    private int hourEnd = -1;
    private String dateStart;
    private String dateEnd;
    private String[] types = null;
    private double[] latlngs;


    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter_window);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

        // to pass back to list view screen
        user = (User) getIntent().getSerializableExtra("user");
        latlngs = getIntent().getExtras().getDoubleArray("latlng");
        types = getIntent().getExtras().getStringArray("types");
        dateStart = getIntent().getExtras().getString("dateStart");
        dateEnd = getIntent().getExtras().getString("dateEnd");
        hourStart = getIntent().getExtras().getInt("hourStart");
        hourEnd = getIntent().getExtras().getInt("hourEnd");


        /*Button btn$10 = (Button) findViewById(R.id.$10_btn);
        btn$10.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                price = 10;
            }
        });

        Button btn$25 = (Button) findViewById(R.id.$25_btn);
        btn$25.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                price = 25;
            }
        });

        Button btn$5 = (Button) findViewById(R.id.$5_btn);
        btn$5.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                price = 5;
            }
        });

        Button btn$15 = (Button) findViewById(R.id.$15_btn);
        btn$15.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                price = 15;
            }
        });

        Button btn$20 = (Button) findViewById(R.id.$20_btn);
        btn$20.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                price = 20;
            }
        });

        Button btn$any = (Button) findViewById(R.id.any$_btn);
        btn$any.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                price = 0;
            }
        });*/

        Button btn0_5mi = (Button) findViewById(R.id.mile_0_5_btn);
        btn0_5mi.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                distance = 0.5;
            }
        });

        Button btn1_5mi = (Button) findViewById(R.id.mile_1_5_btn);
        btn1_5mi.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                distance = 1.5;
            }
        });

        Button btn1mi = (Button) findViewById(R.id.mile_1_0_btn);
        btn1mi.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                distance = 1.0;
            }
        });

        Button btn2mi = (Button) findViewById(R.id.mile_2_0_btn);
        btn2mi.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                distance = 2.0;
            }
        });

        Button btn2_5mi = (Button) findViewById(R.id.mile_2_5_btn);
        btn2_5mi.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                distance = 2.5;
            }
        });

        Button btn3mi = (Button) findViewById(R.id.mile_3_0_btn);
        btn3mi.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                distance = 3.0;
            }
        });


        Button btnrating2 = (Button) findViewById(R.id.rating_2_button);
        btnrating2.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                rating = 2;
            }
        });

        Button btnrating3 = (Button) findViewById(R.id.rating_3_button);
        btnrating3.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                rating = 3;
            }
        });

        Button btnrating4 = (Button) findViewById(R.id.rating_4_button);
        btnrating4.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                rating = 4;
            }
        });

        Button btnratingany = (Button) findViewById(R.id.rating_any_button);
        btnratingany.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                rating = 0;
            }
        });

        /*Button btnsizereg = (Button) findViewById(R.id.size_reg_btn);
        btnsizereg.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                size = "Regular";
            }
        });

        Button btnsizecomp = (Button) findViewById(R.id.size_comp_btn);
        btnsizecomp.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                size = "Compact";
            }
        });

        Button btnsizehandicap = (Button) findViewById(R.id.size_handicap_btn);
        btnsizehandicap.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                size = "Handicap";
            }
        });

        Button btnsizetruck = (Button) findViewById(R.id.size_truck_btn);
        btnsizetruck.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                size = "Truck";
            }
        });

        Button btnsizecovered = (Button) findViewById(R.id.size_covered_btn);
        btnsizecovered.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                size = "Covered";
            }
        });

        Button btnsizeany = (Button) findViewById(R.id.size_any_btn);
        btnsizeany.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                size = "Any";
            }
        });*/

        Button btnpermityes = (Button) findViewById(R.id.permit_required);
        btnpermityes.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                permit = 1;
            }
        });

        Button btnpermitna = (Button) findViewById(R.id.permit_na);
        btnpermitna.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                permit = -1;
            }
        });

        Button btnpermitany = (Button) findViewById(R.id.permit_any);
        btnpermitany.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                permit = 0;
            }
        });

        /*Button btnprating2 = (Button) findViewById(R.id.provider_rating_2);
        btnprating2.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                p_rating = 2;
            }
        });

        Button btnprating3 = (Button) findViewById(R.id.provider_rating_3);
        btnprating3.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                p_rating = 3;
            }
        });

        Button btnprating4 = (Button) findViewById(R.id.provider_rating_4);
        btnprating4.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                p_rating = 4;
            }
        });

        Button btnpratingany = (Button) findViewById(R.id.provider_rating_any);
        btnpratingany.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                p_rating = 0;
            }
        });*/


        Button apply_btn = (Button) findViewById(R.id.filters_apply);
        apply_btn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent intent = new Intent(getApplicationContext(), list_view_screen.class);
                //intent.putExtra("price", price);
                intent.putExtra("distance", distance);
                intent.putExtra("rating", rating);
                //intent.putExtra("size", size);
                intent.putExtra("permit", permit);
                //intent.putExtra("p_rating", p_rating);

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

    }

}