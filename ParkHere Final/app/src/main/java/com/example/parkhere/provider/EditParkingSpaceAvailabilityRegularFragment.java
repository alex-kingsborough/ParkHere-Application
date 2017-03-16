package com.example.parkhere.provider;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.parkhere.R;
import com.example.parkhere.objects.Availability;
import com.example.parkhere.objects.Interval;
import com.example.parkhere.objects.ParkingSpace;
import com.example.parkhere.objects.User;
import com.example.parkhere.server.Constants;
import com.example.parkhere.server.RequestInterface;
import com.example.parkhere.server.ServerRequest;
import com.example.parkhere.server.ServerResponse;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class EditParkingSpaceAvailabilityRegularFragment extends Fragment {
    private User user;
    private ParkingSpace parkingSpace;
    private static final String USER_KEY = "user";
    private static final String PARKING_SPACE_KEY = "parkingSpace";
    private View rootView;
    private FragmentActivity myContext;
    private EditText edit_psa_start_date, edit_psa_end_date, edit_psa_price_per_hour;
    private Spinner edit_psa_start_time, edit_psa_end_time;
    private Button edit_psa_add_button, edit_psa_edit_button, edit_psa_delete_button;
    private ImageView edit_psa_startDatePicker, edit_psa_endDatePicker;
    private Availability availability;
    private TableLayout tl;
    List<Interval> intervals = new ArrayList<>();
    private int START_REQUEST_CODE = 1;
    private int END_REQUEST_CODE = 2;
    private int selectedIndex = -1;
    private String selectedStartDate;
    private String selectedStartHour;
    private String selectedEndDate;
    private String selectedEndHour;
    private String selectedPricePerHour;
    private int tag = 1;
    private long stime, etime, duration;

    @Override
    public void onAttach(Activity activity) {
        myContext=(FragmentActivity) activity;
        super.onAttach(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        stime = System.nanoTime();
        user = (User) getArguments().getSerializable(USER_KEY);
        parkingSpace = (ParkingSpace) getArguments().getSerializable(PARKING_SPACE_KEY);

        rootView = inflater.inflate(R.layout.fragment_edit_parking_space_availability_regular, container, false);

        edit_psa_start_date = (EditText) rootView.findViewById(R.id.edit_psa_start_date);
        edit_psa_end_date = (EditText) rootView.findViewById(R.id.edit_psa_end_date);
        edit_psa_price_per_hour = (EditText) rootView.findViewById(R.id.edit_psa_price_per_hour);
        edit_psa_start_time = (Spinner) rootView.findViewById(R.id.edit_psa_start_time_spinner);
        edit_psa_end_time = (Spinner) rootView.findViewById(R.id.edit_psa_end_time_spinner);
        edit_psa_add_button = (Button) rootView.findViewById(R.id.edit_add_psa_button);
        edit_psa_startDatePicker = (ImageView) rootView.findViewById(R.id.edit_psa_startDatePicker);
        edit_psa_endDatePicker = (ImageView) rootView.findViewById(R.id.edit_psa_endDatePicker);
        edit_psa_edit_button = (Button) rootView.findViewById(R.id.edit_psa_edit_button);
        edit_psa_edit_button.setVisibility(View.GONE);//REMOVE LATER
        edit_psa_delete_button = (Button) rootView.findViewById(R.id.edit_psa_delete_button);

        edit_psa_startDatePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment picker = new StartDatePickerFragment();
                picker.setTargetFragment(EditParkingSpaceAvailabilityRegularFragment.this, START_REQUEST_CODE);
                picker.show(getFragmentManager(), "datePicker");
            }
        });

        edit_psa_endDatePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment picker = new EndDatePickerFragment();
                picker.setTargetFragment(EditParkingSpaceAvailabilityRegularFragment.this, END_REQUEST_CODE);
                picker.show(getFragmentManager(), "datePicker");
            }
        });

        tl = (TableLayout) rootView.findViewById(R.id.edit_psa_table);

        TableRow tr_head = new TableRow(myContext);
        tr_head.setBackgroundColor(Color.GRAY);
        tr_head.setLayoutParams(new TableRow.LayoutParams(
                TableRow.LayoutParams.FILL_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT));

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

        TextView end_date = new TextView(myContext);
        end_date.setText("END DATE");
        end_date.setTextColor(Color.WHITE);
        end_date.setPadding(5, 5, 5, 5);
        tr_head.addView(end_date);

        TextView end_time = new TextView(myContext);
        end_time.setText("END TIME");
        end_time.setTextColor(Color.WHITE);
        end_time.setPadding(5, 5, 5, 5);
        tr_head.addView(end_time);

        TextView price = new TextView(myContext);
        price.setText("PRICE/HR");
        price.setTextColor(Color.WHITE);
        price.setPadding(5, 5, 5, 5);
        tr_head.addView(price);

        tl.addView(tr_head, new TableLayout.LayoutParams(
                TableLayout.LayoutParams.FILL_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT));

        List<String> categories = new ArrayList<String>();
        categories.add("12:00 AM");
        categories.add("01:00 AM");
        categories.add("02:00 AM");
        categories.add("03:00 AM");
        categories.add("04:00 AM");
        categories.add("05:00 AM");
        categories.add("06:00 AM");
        categories.add("07:00 AM");
        categories.add("08:00 AM");
        categories.add("09:00 AM");
        categories.add("10:00 AM");
        categories.add("11:00 AM");
        categories.add("12:00 PM");
        categories.add("01:00 PM");
        categories.add("02:00 PM");
        categories.add("03:00 PM");
        categories.add("04:00 PM");
        categories.add("05:00 PM");
        categories.add("06:00 PM");
        categories.add("07:00 PM");
        categories.add("08:00 PM");
        categories.add("09:00 PM");
        categories.add("10:00 PM");
        categories.add("11:00 PM");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(rootView.getContext(), android.R.layout.simple_spinner_item, categories);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        edit_psa_start_time.setAdapter(dataAdapter);
        edit_psa_end_time.setAdapter(dataAdapter);

        getCurrAvailability();

        edit_psa_add_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String startDate = edit_psa_start_date.getText().toString();
                String endDate = edit_psa_end_date.getText().toString();
                String pricePerHour = edit_psa_price_per_hour.getText().toString();

                if (!validate(startDate, endDate, pricePerHour)) {
                    onAddSpaceAvailabilityFailed();
                } else {
                    String startTime = edit_psa_start_time.getItemAtPosition(edit_psa_start_time.getSelectedItemPosition()).toString();
                    String endTime = edit_psa_end_time.getItemAtPosition(edit_psa_end_time.getSelectedItemPosition()).toString();

                    availability = new Availability();
                    availability.setStartDate(startDate);
                    availability.setPricePerHour(pricePerHour);

                    String startTimeSubstring = startTime.substring(startTime.length()-2, startTime.length());
                    if (startTimeSubstring.charAt(0) == 'P') {
                        startTimeSubstring = startTime.substring(0, 2);
                        int time = Integer.parseInt(startTimeSubstring) + 12;
                        availability.setStartHour(time);
                    } else {
                        startTimeSubstring = startTime.substring(0, 2);
                        int time;
                        if (startTimeSubstring.equals("12")) {
                            time = 0;
                        } else {
                            time = Integer.parseInt(startTimeSubstring);
                        }

                        availability.setStartHour(time);
                    }

                    String endTimeSubstring = endTime.substring(endTime.length()-2, endTime.length());
                    if (endTimeSubstring.charAt(0) == 'P') {
                        endTimeSubstring = endTime.substring(0, 2);
                        int time = Integer.parseInt(endTimeSubstring) + 12;
                        availability.setEndHour(time);
                    } else {
                        endTimeSubstring = endTime.substring(0, 2);
                        int time;
                        if (endTimeSubstring.equals("12")) {
                            time = 0;
                        } else {
                            time = Integer.parseInt(endTimeSubstring);
                        }

                        availability.setEndHour(time);
                    }

                    try {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm a");
                        Date parsedDate1 = sdf.parse(startDate + " " + startTime);
                        Date parsedDate2 = sdf.parse(endDate + " " + endTime);
                        boolean overlap = false;
                        for (int i = 0; i < intervals.size(); i++) {
                            if (isOverlapping(intervals.get(i).start, intervals.get(i).end, parsedDate1, parsedDate2)){
                                overlap = true;
                                break;
                            }
                        }
                        if (overlap) {
                            Snackbar.make(rootView, "Space availability overlaps with already added availability", Snackbar.LENGTH_LONG).show();
                            return;
                        }
                    } catch (ParseException e) {
                        System.out.println("Parse Exception " + e.getLocalizedMessage());
                    }

                    if (startDate.equals(endDate)) {
                        if (availability.getEndHour() <= availability.getStartHour()) {
                            Snackbar.make(rootView, "End hour must be after start hour", Snackbar.LENGTH_LONG).show();
                            return;
                        } else {
                            try {
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm a");
                                Date parsedDate1 = sdf.parse(startDate + " " + startTime);
                                Date parsedDate2 = sdf.parse(endDate + " " + endTime);
                                intervals.add(new Interval(parsedDate1, parsedDate2));
                            } catch (ParseException e) {
                                System.out.println("Parse Exception " + e.getLocalizedMessage());
                            }

                            for (int i = availability.getStartHour(); i < availability.getEndHour(); i++) {
                                Availability a = new Availability();
                                a.setStartDate(startDate);
                                a.setStartHour(i);
                                a.setEndHour(i+1);
                                a.setPricePerHour(pricePerHour);
                                addSpaceAvailability(a);
                            }
                        }
                    } else {
                        try {
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm a");
                            Date parsedDate1 = sdf.parse(startDate + " " + startTime);
                            Date parsedDate2 = sdf.parse(endDate + " " + endTime);
                            intervals.add(new Interval(parsedDate1, parsedDate2));
                        } catch (ParseException e) {
                            System.out.println("Parse Exception " + e.getLocalizedMessage());
                        }

                        boolean firstIteration = true;
                        while (!startDate.equals(endDate)) {
                            try{
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                                Calendar c = Calendar.getInstance();
                                c.setTime(sdf.parse(startDate));
                                c.add(Calendar.DATE, 1);

                                if (firstIteration) {
                                    for (int i = availability.getStartHour(); i < 24; i++) {
                                        Availability a = new Availability();
                                        a.setStartDate(startDate);
                                        a.setStartHour(i);
                                        a.setEndHour(i+1);
                                        a.setPricePerHour(pricePerHour);
                                        addSpaceAvailability(a);
                                    }
                                    firstIteration = false;
                                } else {
                                    for (int i = 0; i < 24; i++) {
                                        Availability a = new Availability();
                                        a.setStartDate(startDate);
                                        a.setStartHour(i);
                                        a.setEndHour(i+1);
                                        a.setPricePerHour(pricePerHour);
                                        addSpaceAvailability(a);
                                    }
                                }
                                startDate = sdf.format(c.getTime());
                                if (startDate.equals(endDate)) {
                                    for (int i = 0; i < availability.getEndHour(); i++) {
                                        Availability a = new Availability();
                                        a.setStartDate(startDate);
                                        a.setStartHour(i);
                                        a.setEndHour(i+1);
                                        a.setPricePerHour(pricePerHour);
                                        addSpaceAvailability(a);
                                    }
                                }
                            }catch(ParseException ex){
                                ex.printStackTrace();
                            }
                        }
                    }

                    TableRow tr = new TableRow(myContext);
                    tr.setLayoutParams(new TableRow.LayoutParams(
                            TableRow.LayoutParams.FILL_PARENT,
                            TableRow.LayoutParams.WRAP_CONTENT));
                    tr.setTag(tag);
                    tag++;
                    tr.setClickable(true);
                    tr.setOnClickListener(clickListener);

                    TextView startDATE = new TextView(myContext);
                    startDATE.setText(edit_psa_start_date.getText());
                    tr.addView(startDATE);

                    TextView startTIME = new TextView(myContext);
                    startTIME.setText(startTime);
                    tr.addView(startTIME);

                    TextView endDATE = new TextView(myContext);
                    endDATE.setText(edit_psa_end_date.getText());
                    tr.addView(endDATE);

                    TextView endTIME = new TextView(myContext);
                    endTIME.setText(endTime);
                    tr.addView(endTIME);

                    TextView price = new TextView(myContext);
                    price.setText(pricePerHour);
                    tr.addView(price);

                    tl.addView(tr, new TableLayout.LayoutParams(
                            TableLayout.LayoutParams.FILL_PARENT,
                            TableLayout.LayoutParams.WRAP_CONTENT));
                }
            }
        });

        edit_psa_edit_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (selectedIndex < 0) {
                    Snackbar.make(rootView, "Must add a space availability to edit", Snackbar.LENGTH_LONG).show();
                } else {
                    //TODO EDIT FUNCTION
                }
            }
        });

        edit_psa_delete_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (selectedIndex < 0) {
                    Snackbar.make(rootView, "Must add a space availability to delete", Snackbar.LENGTH_LONG).show();
                } else {
                    String startDate = selectedStartDate;
                    String endDate = selectedEndDate;
                    String pricePerHour = selectedPricePerHour;
                    String startTime = selectedStartHour;
                    String endTime = selectedEndHour;

                    availability = new Availability();
                    availability.setStartDate(startDate);
                    availability.setPricePerHour(pricePerHour);

                    String startTimeSubstring = startTime.substring(startTime.length()-2, startTime.length());
                    if (startTimeSubstring.charAt(0) == 'P') {
                        startTimeSubstring = startTime.substring(0, 2);
                        int time = Integer.parseInt(startTimeSubstring) + 12;
                        availability.setStartHour(time);
                    } else {
                        startTimeSubstring = startTime.substring(0, 2);
                        int time;
                        if (startTimeSubstring.equals("12")) {
                            time = 0;
                        } else {
                            time = Integer.parseInt(startTimeSubstring);
                        }

                        availability.setStartHour(time);
                    }

                    String endTimeSubstring = endTime.substring(endTime.length()-2, endTime.length());
                    if (endTimeSubstring.charAt(0) == 'P') {
                        endTimeSubstring = endTime.substring(0, 2);
                        int time = Integer.parseInt(endTimeSubstring) + 12;
                        availability.setEndHour(time);
                    } else {
                        endTimeSubstring = endTime.substring(0, 2);
                        int time;
                        if (endTimeSubstring.equals("12")) {
                            time = 0;
                        } else {
                            time = Integer.parseInt(endTimeSubstring);
                        }

                        availability.setEndHour(time);
                    }

                    if (startDate.equals(endDate)) {
                        for (int i = availability.getStartHour(); i < availability.getEndHour(); i++) {
                            Availability a = new Availability();
                            a.setStartDate(startDate);
                            a.setStartHour(i);
                            a.setEndHour(i+1);
                            a.setPricePerHour(pricePerHour);
                            deleteSpaceAvailability(a);
                        }
                    } else {
                        boolean firstIteration = true;
                        while (!startDate.equals(endDate)) {
                            try{
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                                Calendar c = Calendar.getInstance();
                                c.setTime(sdf.parse(startDate));
                                c.add(Calendar.DATE, 1);

                                if (firstIteration) {
                                    for (int i = availability.getStartHour(); i < 24; i++) {
                                        Availability a = new Availability();
                                        a.setStartDate(startDate);
                                        a.setStartHour(i);
                                        a.setEndHour(i+1);
                                        a.setPricePerHour(pricePerHour);
                                        deleteSpaceAvailability(a);
                                    }
                                    firstIteration = false;
                                } else {
                                    for (int i = 0; i < 24; i++) {
                                        Availability a = new Availability();
                                        a.setStartDate(startDate);
                                        a.setStartHour(i);
                                        a.setEndHour(i+1);
                                        a.setPricePerHour(pricePerHour);
                                        deleteSpaceAvailability(a);
                                    }
                                }
                                startDate = sdf.format(c.getTime());
                                if (startDate.equals(endDate)) {
                                    for (int i = 0; i < availability.getEndHour(); i++) {
                                        Availability a = new Availability();
                                        a.setStartDate(startDate);
                                        a.setStartHour(i);
                                        a.setEndHour(i+1);
                                        a.setPricePerHour(pricePerHour);
                                        deleteSpaceAvailability(a);
                                    }
                                }
                            }catch(ParseException ex){
                                ex.printStackTrace();
                            }
                        }
                    }

                    TableRow tr = (TableRow) tl.findViewWithTag(selectedIndex);
                    tl.removeView(tr);
                    selectedIndex = -1;
                    try {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm a");
                        Date parsedDate1 = sdf.parse(selectedStartDate + " " + selectedStartHour);
                        Date parsedDate2 = sdf.parse(selectedEndDate + " " + selectedEndHour);
                        for (int i = 0; i < intervals.size(); i++) {
                            if (intervals.get(i).start.equals(parsedDate1) && intervals.get(i).end.equals(parsedDate2)) {
                                intervals.remove(i);
                                break;
                            }
                        }
                    } catch (ParseException e) {
                        System.out.println("Parse Exception " + e.getLocalizedMessage());
                    }
                }
            }
        });
        etime = System.nanoTime();
        duration = (etime - stime);
        System.out.println("edit parking space avialability fragment took: " + duration);
        return rootView;
    }

    public void getCurrAvailability() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RequestInterface requestInterface = retrofit.create(RequestInterface.class);

        ServerRequest request = new ServerRequest();
        request.setOperation(Constants.EDIT_PARKING_SPACE_AVAILABILITY_OPERATION);
        request.setUser(user);
        request.setParkingSpace(parkingSpace);
        Call<ServerResponse> response = requestInterface.operation(request);

        response.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, retrofit2.Response<ServerResponse> response) {
                ServerResponse resp = response.body();

                if(resp.getResult().equals(Constants.SUCCESS)){
                    Availability[] arr = resp.getAvailabilities();

                    //No availability
                    if (arr.length == 0) {
                        return;
                    }

                    Availability start = arr[0];
                    Availability end = new Availability();
                    end.setStartDate(start.getStartDate());
                    end.setStartHour(start.getStartHour());
                    end.setEndHour(start.getEndHour());
                    end.setPricePerHour(start.getPricePerHour());

                    //Only one availability
                    if (arr.length == 1) {
                        try {
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm a");
                            Date parsedDate1 = sdf.parse(start.getStartDate() + " " + getTime(start.getStartHour()));
                            Date parsedDate2;
                            if (end.getEndHour() == 24) {
                                SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
                                Calendar c = Calendar.getInstance();
                                c.setTime(sdf2.parse(end.getStartDate()));
                                c.add(Calendar.DATE, 1);
                                end.setStartDate(sdf2.format(c.getTime()));
                                parsedDate2 = sdf.parse(end.getStartDate() + " " + getTime(end.getEndHour()));
                            } else {
                                parsedDate2 = sdf.parse(end.getStartDate() + " " + getTime(end.getEndHour()));
                            }
                            intervals.add(new Interval(parsedDate1, parsedDate2));

                            //Add row to table
                            TableRow tr = new TableRow(myContext);
                            tr.setLayoutParams(new TableRow.LayoutParams(
                                    TableRow.LayoutParams.FILL_PARENT,
                                    TableRow.LayoutParams.WRAP_CONTENT));
                            tr.setTag(tag);
                            tag++;
                            tr.setClickable(true);
                            tr.setOnClickListener(clickListener);

                            TextView startDATE = new TextView(myContext);
                            startDATE.setText(start.getStartDate());
                            tr.addView(startDATE);

                            TextView startTIME = new TextView(myContext);
                            startTIME.setText(getTime(start.getStartHour()));
                            tr.addView(startTIME);

                            TextView endDATE = new TextView(myContext);
                            endDATE.setText(end.getStartDate());
                            tr.addView(endDATE);

                            TextView endTIME = new TextView(myContext);
                            endTIME.setText(getTime(end.getEndHour()));
                            tr.addView(endTIME);

                            TextView price = new TextView(myContext);
                            price.setText(start.getPricePerHour());
                            tr.addView(price);

                            tl.addView(tr, new TableLayout.LayoutParams(
                                    TableLayout.LayoutParams.FILL_PARENT,
                                    TableLayout.LayoutParams.WRAP_CONTENT));
                        } catch (ParseException e) {
                            System.out.println("Parse Error" + e.getLocalizedMessage());
                        }
                        return;
                    }

                    //more than one availability
                    int row = 1;
                    /*for (int i = 0; i < arr.length; i++) {
                        System.out.println(arr[i].getStartDate() + " " + arr[i].getStartHour() + " "
                        + arr[i].getEndHour() + " " + arr[i].getPricePerHour());
                    }*/
                    for (int i = 0; i < arr.length; i++) {
                        start = arr[i];
                        end = arr[i];

                        //not same day
                        if (i+1 < arr.length &&
                                !(arr[i+1].getStartDate().equals(end.getStartDate()))) {
                            //if end hour is 24 and price still matches and next hour is 0
                            if (end.getEndHour() == 24 && arr[i+1].getPricePerHour().equals(end.getPricePerHour())
                                    && arr[i+1].getStartHour() == 0) {
                                //increment by one day
                                try {
                                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                                    Calendar c = Calendar.getInstance();
                                    c.setTime(sdf.parse(end.getStartDate()));
                                    c.add(Calendar.DATE, 1);
                                    if (arr[i+1].getStartDate().equals(sdf.format(c.getTime()))) {
                                        end = arr[i+1];
                                        i++;
                                    }
                                } catch (ParseException e) {
                                    System.out.println("Parse Error" + e.getLocalizedMessage());
                                }
                            }
                        }

                        //same day, consecutive hours, same price
                        while (i+1 < arr.length &&
                                arr[i+1].getStartDate().equals(end.getStartDate()) &&
                                arr[i+1].getStartHour() == end.getEndHour() &&
                                arr[i+1].getPricePerHour().equals(end.getPricePerHour())) {

                            end = arr[i+1];
                            i++;

                            //not same day
                            if (i+1 < arr.length &&
                                    !(arr[i+1].getStartDate().equals(end.getStartDate()))) {
                                //if end hour is 24 and price still matches and next hour is 0
                                if (end.getEndHour() == 24 && arr[i+1].getPricePerHour().equals(end.getPricePerHour())
                                        && arr[i+1].getStartHour() == 0) {
                                    //increment by one day
                                    try {
                                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                                        Calendar c = Calendar.getInstance();
                                        c.setTime(sdf.parse(end.getStartDate()));
                                        c.add(Calendar.DATE, 1);
                                        if (arr[i+1].getStartDate().equals(sdf.format(c.getTime()))) {
                                            end = arr[i+1];
                                            i++;
                                        }
                                    } catch (ParseException e) {
                                        System.out.println("Parse Error" + e.getLocalizedMessage());
                                    }
                                }
                            }
                        }

                        //add interval to list
                        try {
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm a");
                            Date parsedDate1 = sdf.parse(start.getStartDate() + " " + getTime(start.getStartHour()));
                            Date parsedDate2;
                            if (end.getEndHour() == 24) {
                                int oldEndHour = end.getEndHour();
                                SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
                                Calendar c = Calendar.getInstance();
                                c.setTime(sdf2.parse(end.getStartDate()));
                                c.add(Calendar.DATE, 1);
                                end = new Availability();
                                end.setStartDate(sdf2.format(c.getTime()));
                                end.setEndHour(oldEndHour);
                                parsedDate2 = sdf.parse(end.getStartDate() + " " + getTime(end.getEndHour()));
                            } else {
                                parsedDate2 = sdf.parse(end.getStartDate() + " " + getTime(end.getEndHour()));
                            }
                            intervals.add(new Interval(parsedDate1, parsedDate2));
                            //Add row to table
                            TableRow tr = new TableRow(myContext);
                            tr.setLayoutParams(new TableRow.LayoutParams(
                                    TableRow.LayoutParams.FILL_PARENT,
                                    TableRow.LayoutParams.WRAP_CONTENT));
                            tr.setTag(tag);
                            tag++;
                            tr.setClickable(true);
                            tr.setOnClickListener(clickListener);

                            TextView startDATE = new TextView(myContext);
                            startDATE.setText(start.getStartDate());
                            tr.addView(startDATE);

                            TextView startTIME = new TextView(myContext);
                            startTIME.setText(getTime(start.getStartHour()));
                            tr.addView(startTIME);

                            TextView endDATE = new TextView(myContext);
                            endDATE.setText(end.getStartDate());
                            tr.addView(endDATE);

                            TextView endTIME = new TextView(myContext);
                            endTIME.setText(getTime(end.getEndHour()));
                            tr.addView(endTIME);

                            TextView price = new TextView(myContext);
                            price.setText(start.getPricePerHour());
                            tr.addView(price);

                            tl.addView(tr, new TableLayout.LayoutParams(
                                    TableLayout.LayoutParams.FILL_PARENT,
                                    TableLayout.LayoutParams.WRAP_CONTENT));
                        } catch (ParseException e) {
                            System.out.println("Parse Error" + e.getLocalizedMessage());
                        }
                    }
                    /*System.out.println("INTERVALS SIZE  " + intervals.size());
                    for (int i = 0; i < intervals.size(); i++) {
                        System.out.println(intervals.get(i).start.toString() + " " + intervals.get(i).end.toString());
                    }*/
                }
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                Snackbar.make(rootView, t.getLocalizedMessage(), Snackbar.LENGTH_LONG).show();
            }
        });
    }

    public void onAddSpaceAvailabilityFailed() {
        Snackbar.make(rootView, "Invalid add space availability parameters", Snackbar.LENGTH_LONG).show();
    }

    public boolean validate(String startDate, String endDate, String pricePerHour) {
        boolean valid = true;

        if (startDate.isEmpty() || startDate.length() != 10) {
            edit_psa_start_date.setError("Enter a valid start date [YYYY-MM-DD]");
            valid = false;
        } else {
            if (startDate.charAt(4) != '-' || startDate.charAt(7) != '-') {
                edit_psa_start_date.setError("Enter a valid start date [YYYY-MM-DD]");
                valid = false;
            } else {
                edit_psa_start_date.setError(null);
            }
        }

        if (endDate.isEmpty() || endDate.length() != 10) {
            edit_psa_end_date.setError("Enter a valid end date [YYYY-MM-DD]");
            valid = false;
        } else {
            if (endDate.charAt(4) != '-' || endDate.charAt(7) != '-') {
                edit_psa_end_date.setError("Enter a valid end date [YYYY-MM-DD]");
                valid = false;
            } else {
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    Date date1 = sdf.parse(startDate);
                    Date date2 = sdf.parse(endDate);

                    if (!date2.after(date1) && !date1.equals(date2)) {
                        edit_psa_end_date.setError("End date must be the same as or after the start date");
                        valid = false;
                    } else {
                        edit_psa_end_date.setError(null);
                    }
                } catch (ParseException ex) {
                    System.out.println("ERROR" + ex.getLocalizedMessage());
                    //ex.printStackTrace();
                }
            }
        }

        if (pricePerHour.isEmpty() || pricePerHour.length() < 5){
            edit_psa_price_per_hour.setError("Enter a valid price per hour [$XX.XX]");
            valid = false;
        } else {
            String priceSubstring = pricePerHour.substring(pricePerHour.length() - 3, pricePerHour.length());
            if (priceSubstring.charAt(0) != '.') {
                edit_psa_price_per_hour.setError("Enter a valid price per hour [$XX.XX]");
                valid = false;
            } else {
                edit_psa_price_per_hour.setError(null);
            }
        }

        return valid;
    }

    public void addSpaceAvailability(Availability av) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RequestInterface requestInterface = retrofit.create(RequestInterface.class);

        ServerRequest request = new ServerRequest();
        request.setOperation(Constants.ADD_PARKING_SPACE_AVAILABILITY_OPERATION);
        request.setParkingSpace(parkingSpace);
        request.setAvailability(av);
        Call<ServerResponse> response = requestInterface.operation(request);

        response.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, retrofit2.Response<ServerResponse> response) {
                //ServerResponse resp = response.body();
                //System.out.println(resp.getResult() + " " + resp.getMessage());
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                Snackbar.make(rootView, t.getLocalizedMessage(), Snackbar.LENGTH_LONG).show();
            }
        });
    }

    public void deleteSpaceAvailability(Availability av) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RequestInterface requestInterface = retrofit.create(RequestInterface.class);

        ServerRequest request = new ServerRequest();
        request.setOperation(Constants.DELETE_PARKING_SPACE_AVAILABILITY_OPERATION);
        request.setParkingSpace(parkingSpace);
        request.setAvailability(av);
        Call<ServerResponse> response = requestInterface.operation(request);

        response.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, retrofit2.Response<ServerResponse> response) {
                //ServerResponse resp = response.body();
                //System.out.println(resp.getResult() + " " + resp.getMessage());
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                Snackbar.make(rootView, t.getLocalizedMessage(), Snackbar.LENGTH_LONG).show();
            }
        });
    }

    public static boolean isOverlapping(Date start1, Date end1, Date start2, Date end2) {
        return start1.before(end2) && start2.before(end1);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == StartDatePickerFragment.REQUEST_CODE) {
            String start = data.getStringExtra(StartDatePickerFragment.DATE_KEY);
            edit_psa_start_date.setText(start);
        } else if (requestCode == EndDatePickerFragment.REQUEST_CODE) {
            String end = data.getStringExtra(EndDatePickerFragment.DATE_KEY);
            edit_psa_end_date.setText(end);
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
            TableRow tr = (TableRow) tl.findViewWithTag(index);
            tr.setBackgroundColor(Color.LTGRAY);
            selectedIndex = index;

            TextView tv = (TextView) tr.getChildAt(0);
            selectedStartDate = tv.getText().toString();
            tv = (TextView) tr.getChildAt(1);
            selectedStartHour = tv.getText().toString();
            tv = (TextView) tr.getChildAt(2);
            selectedEndDate = tv.getText().toString();
            tv = (TextView) tr.getChildAt(3);
            selectedEndHour = tv.getText().toString();
            tv = (TextView) tr.getChildAt(4);
            selectedPricePerHour = tv.getText().toString();
        }
    }

    private void deselectRow(int index) {
        if (index >= 0) {
            TableRow tr = (TableRow) tl.findViewWithTag(index);
            if (tr != null) {
                tr.setBackgroundColor(Color.parseColor("#F2F7F2"));
            }
        }
    }
}
