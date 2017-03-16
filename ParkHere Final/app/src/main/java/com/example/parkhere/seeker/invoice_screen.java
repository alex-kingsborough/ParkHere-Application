package com.example.parkhere.seeker;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.parkhere.R;
import com.example.parkhere.objects.ParkingSpace;
import com.example.parkhere.objects.User;

public class invoice_screen extends AppCompatActivity {

    private User user;
    private ParkingSpace parkingSpace;
    private double currLat;
    private double currLong;

    private String[] dateRange;
    private int startTime;
    private int endTime;
    private int payment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        System.out.println("in the on create");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoice_screen);


        parkingSpace = (ParkingSpace) getIntent().getSerializableExtra("parkingSpot");
        user = (User) getIntent().getExtras().getSerializable("user");
        dateRange = (String[]) getIntent().getExtras().getSerializable("dateRange");
        startTime = getIntent().getExtras().getInt("hourStart");
        endTime = getIntent().getExtras().getInt("hourEnd");
        payment = getIntent().getExtras().getInt("payment");

        TextView tv = (TextView)findViewById(R.id.invoice_address);
        tv.setText("Address: " + parkingSpace.getAddress());
        TextView tv2 = (TextView)findViewById(R.id.invoice_title_text);
        TextView tv3 = (TextView)findViewById(R.id.invoice_title_text2);
        TextView tv4 = (TextView)findViewById(R.id.invoice_total_fair);
        TextView tv5 = (TextView)findViewById(R.id.invoice_charged_text);
        TextView tv6 = (TextView)findViewById(R.id.invoice_card);

        Button btn = (Button)findViewById(R.id.invoice_close_btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ratings_screen.class);
                intent.putExtra("user", user);
                intent.putExtra("parkingSpace", parkingSpace);
                intent.putExtra("dateRange", dateRange);
                intent.putExtra("hourStart", startTime);
                intent.putExtra("hourEnd", endTime);
                startActivity(intent);
            }
        });
//        String start, end;
//        if(startTime - 12 > 0){
//            startTime = startTime - 12;
//            start = startTime + "pm";
//        }else{
//            start = startTime + "am";
//        }
//        if(endTime - 12 > 0){
//            endTime = endTime - 12;
//            end = endTime + "pm";
//        }else{
//            end = endTime + "am";
//        }

        tv2.setText("Start: " + dateRange[0] + " " + getTime(startTime));
        tv3.setText("End: " + dateRange[1] + " " + getTime(endTime));
        tv4.setText("Total Fair: $" + parkingSpace.getTotalPrice());
        tv5.setText("Charged: $" + parkingSpace.getTotalPrice());
        tv6.setText("Payment: " + payment);


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