package com.example.parkhere.provider;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
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
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AddParkingSpaceAvailabilityRepeatedFragment extends Fragment {
    private User user;
    private ParkingSpace parkingSpace;
    private static final String USER_KEY = "user";
    private static final String PARKING_SPACE_KEY = "parkingSpace";
    private View rootView;
    private FragmentActivity myContext;
    private EditText add_psar_start_date, add_psar_end_date, add_psar_price_per_hour;
    private Spinner add_psar_weekday, add_psar_start_time, add_psar_end_time;
    private Button add_psar_button;
    private ImageView add_psar_startDatePicker, add_psar_endDatePicker;
    private Availability availability;
    private TableLayout tl;
    private List<Interval> intervals = new ArrayList<>();
    private int START_REQUEST_CODE = 1;
    private int END_REQUEST_CODE = 2;
    private String weekday;

    @Override
    public void onAttach(Activity activity) {
        myContext=(FragmentActivity) activity;
        super.onAttach(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        user = (User) getArguments().getSerializable(USER_KEY);
        parkingSpace = (ParkingSpace) getArguments().getSerializable(PARKING_SPACE_KEY);

        rootView = inflater.inflate(R.layout.fragment_add_parking_space_availability_repeated, container, false);

        add_psar_start_date = (EditText) rootView.findViewById(R.id.add_psar_start_date);
        add_psar_end_date = (EditText) rootView.findViewById(R.id.add_psar_end_date);
        add_psar_price_per_hour = (EditText) rootView.findViewById(R.id.add_psar_price_per_hour);
        add_psar_weekday = (Spinner) rootView.findViewById(R.id.add_psar_weekday_spinner);
        add_psar_start_time = (Spinner) rootView.findViewById(R.id.add_psar_start_time_spinner);
        add_psar_end_time = (Spinner) rootView.findViewById(R.id.add_psar_end_time_spinner);
        add_psar_button = (Button) rootView.findViewById(R.id.add_psar_button);
        add_psar_startDatePicker = (ImageView) rootView.findViewById(R.id.add_psar_startDatePicker);
        add_psar_endDatePicker = (ImageView) rootView.findViewById(R.id.add_psar_endDatePicker);

        add_psar_startDatePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment picker = new StartDatePickerFragment();
                picker.setTargetFragment(AddParkingSpaceAvailabilityRepeatedFragment.this, START_REQUEST_CODE);
                picker.show(getFragmentManager(), "datePicker");
            }
        });

        add_psar_endDatePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment picker = new EndDatePickerFragment();
                picker.setTargetFragment(AddParkingSpaceAvailabilityRepeatedFragment.this, END_REQUEST_CODE);
                picker.show(getFragmentManager(), "datePicker");
            }
        });

        tl = (TableLayout) rootView.findViewById(R.id.add_psar_table);

        TableRow tr_head = new TableRow(myContext);
        tr_head.setBackgroundColor(Color.GRAY);
        tr_head.setLayoutParams(new TableRow.LayoutParams(
                TableRow.LayoutParams.FILL_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT));

        TextView start_time = new TextView(myContext);
        start_time.setText("START TIME");
        start_time.setTextColor(Color.WHITE);
        start_time.setPadding(5, 5, 5, 5);
        tr_head.addView(start_time);

        TextView end_time = new TextView(myContext);
        end_time.setText("END TIME");
        end_time.setTextColor(Color.WHITE);
        end_time.setPadding(5, 5, 5, 5);
        tr_head.addView(end_time);

        TextView start_date = new TextView(myContext);
        start_date.setText("START DATE");
        start_date.setTextColor(Color.WHITE);
        start_date.setPadding(5, 5, 5, 5);
        tr_head.addView(start_date);

        TextView end_date = new TextView(myContext);
        end_date.setText("END DATE");
        end_date.setTextColor(Color.WHITE);
        end_date.setPadding(5, 5, 5, 5);
        tr_head.addView(end_date);

        TextView repeat = new TextView(myContext);
        repeat.setText("REPEAT");
        repeat.setTextColor(Color.WHITE);
        repeat.setPadding(5, 5, 5, 5);
        tr_head.addView(repeat);

        tl.addView(tr_head, new TableLayout.LayoutParams(
                TableLayout.LayoutParams.FILL_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT));

        getCurrAvailability();

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
        add_psar_start_time.setAdapter(dataAdapter);
        add_psar_end_time.setAdapter(dataAdapter);

        categories = new ArrayList<String>();
        categories.add("Sunday");
        categories.add("Monday");
        categories.add("Tuesday");
        categories.add("Wednesday");
        categories.add("Thursday");
        categories.add("Friday");
        categories.add("Saturday");
        dataAdapter = new ArrayAdapter<String>(rootView.getContext(), android.R.layout.simple_spinner_item, categories);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        add_psar_weekday.setAdapter(dataAdapter);

        add_psar_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String startDate = add_psar_start_date.getText().toString();
                String endDate = add_psar_end_date.getText().toString();
                String pricePerHour = add_psar_price_per_hour.getText().toString();
                weekday = add_psar_weekday.getItemAtPosition(add_psar_weekday.getSelectedItemPosition()).toString();;

                if (!validate(startDate, endDate, pricePerHour)) {
                    onAddSpaceAvailabilityFailed();
                } else {
                    //Check that weekday of start date and end date is correct
                    SimpleDateFormat sdf = null;
                    Date startdate = null;
                    Date enddate = null;
                    try {
                        sdf = new SimpleDateFormat("yyyy-MM-dd");
                        startdate = sdf.parse(startDate);
                        enddate = sdf.parse(endDate);
                        Calendar c = Calendar.getInstance();
                        c.setTime(startdate);
                        int startdayOfWeek = c.get(Calendar.DAY_OF_WEEK);
                        c.setTime(enddate);
                        int enddayOfWeek = c.get(Calendar.DAY_OF_WEEK);
                        if (!getDayOfWeek(startdayOfWeek).equals(weekday) || !getDayOfWeek(enddayOfWeek).equals(weekday)) {
                            Snackbar.make(rootView, "Start date and end date weekday must be " + weekday, Snackbar.LENGTH_LONG).show();
                            return;
                        }
                    } catch (ParseException ex) {
                        System.out.println("ERROR" + ex.getLocalizedMessage());
                        //ex.printStackTrace();
                    }

                    String startTime = add_psar_start_time.getItemAtPosition(add_psar_start_time.getSelectedItemPosition()).toString();
                    String endTime = add_psar_end_time.getItemAtPosition(add_psar_end_time.getSelectedItemPosition()).toString();

                    try {
                        while (startdate.before(enddate) || startdate.equals(enddate)) {
                            /*System.out.println(startDate.toString());*///TESTING

                            availability = new Availability();
                            availability.setStartDate(startDate);
                            availability.setPricePerHour(pricePerHour);
                            availability.setWeekday(weekday);

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
                                SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd hh:mm a");
                                Date parsedDate1 = sdf2.parse(startDate + " " + startTime);
                                Date parsedDate2 = sdf2.parse(startDate + " " + endTime);
                                boolean overlap = false;
                                for (int i = 0; i < intervals.size(); i++) {
                                    /*System.out.println(intervals.get(i).start + "  " + intervals.get(i).end +
                                    parsedDate1.toString() + " " + parsedDate2.toString());*///TESTING
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

                            if (availability.getEndHour() <= availability.getStartHour()) {
                                Snackbar.make(rootView, "End hour must be after start hour", Snackbar.LENGTH_LONG).show();
                                return;
                            } else {
                                try {
                                    SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd hh:mm a");
                                    Date parsedDate1 = sdf2.parse(startDate + " " + startTime);
                                    Date parsedDate2 = sdf2.parse(startDate + " " + endTime);
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
                                    a.setWeekday(weekday);
                                    addSpaceAvailability(a);
                                }
                            }

                            Calendar c = Calendar.getInstance();
                            c.setTime(startdate);
                            c.add(Calendar.DATE, 7);  // number of days to add
                            startDate = sdf.format(c.getTime());
                            startdate = sdf.parse(startDate);
                        }
                        TableRow tr = new TableRow(myContext);
                        tr.setLayoutParams(new TableRow.LayoutParams(
                                TableRow.LayoutParams.FILL_PARENT,
                                TableRow.LayoutParams.WRAP_CONTENT));

                        TextView start_time = new TextView(myContext);
                        start_time.setText(startTime);
                        tr.addView(start_time);

                        TextView end_time = new TextView(myContext);
                        end_time.setText(endTime);
                        tr.addView(end_time);

                        TextView start_date = new TextView(myContext);
                        start_date.setText(add_psar_start_date.getText());
                        tr.addView(start_date);

                        TextView end_date = new TextView(myContext);
                        end_date.setText(add_psar_end_date.getText());
                        tr.addView(end_date);

                        TextView repeat = new TextView(myContext);
                        repeat.setText(weekday);
                        tr.addView(repeat);

                        tl.addView(tr, new TableLayout.LayoutParams(
                                TableLayout.LayoutParams.FILL_PARENT,
                                TableLayout.LayoutParams.WRAP_CONTENT));
                    } catch (ParseException ex) {
                        System.out.println("ERROR" + ex.getLocalizedMessage());
                        //ex.printStackTrace();
                    }
                }
            }
        });

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
            add_psar_start_date.setError("Enter a valid start date [YYYY-MM-DD]");
            valid = false;
        } else {
            if (startDate.charAt(4) != '-' || startDate.charAt(7) != '-') {
                add_psar_start_date.setError("Enter a valid start date [YYYY-MM-DD]");
                valid = false;
            } else {
                add_psar_start_date.setError(null);
            }
        }

        if (endDate.isEmpty() || endDate.length() != 10) {
            add_psar_end_date.setError("Enter a valid end date [YYYY-MM-DD]");
            valid = false;
        } else {
            if (endDate.charAt(4) != '-' || endDate.charAt(7) != '-') {
                add_psar_end_date.setError("Enter a valid end date [YYYY-MM-DD]");
                valid = false;
            } else {
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    Date date1 = sdf.parse(startDate);
                    Date date2 = sdf.parse(endDate);

                    long diff = date2.getTime() - date1.getTime();
                    String days = Long.toString(TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS));

                    if (Integer.parseInt(days) < 7) {
                        add_psar_end_date.setError("End date must at least 7 days after the start date");
                        valid = false;
                    } else {
                        add_psar_end_date.setError(null);
                    }
                } catch (ParseException ex) {
                    System.out.println("ERROR" + ex.getLocalizedMessage());
                    //ex.printStackTrace();
                }
            }
        }

        if (pricePerHour.isEmpty() || pricePerHour.length() < 5){
            add_psar_price_per_hour.setError("Enter a valid price per hour [$XX.XX]");
            valid = false;
        } else {
            String priceSubstring = pricePerHour.substring(pricePerHour.length() - 3, pricePerHour.length());
            if (priceSubstring.charAt(0) != '.') {
                add_psar_price_per_hour.setError("Enter a valid price per hour [$XX.XX]");
                valid = false;
            } else {
                add_psar_price_per_hour.setError(null);
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
        request.setOperation(Constants.ADD_PARKING_SPACE_REPEATED_AVAILABILITY_OPERATION);
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
            add_psar_start_date.setText(start);
        } else if (requestCode == EndDatePickerFragment.REQUEST_CODE) {
            String end = data.getStringExtra(EndDatePickerFragment.DATE_KEY);
            add_psar_end_date.setText(end);
        }
    }

    private String getDayOfWeek(int value) {
        String day = "";
        switch (value) {
            case 1:
                day = "Sunday";
                break;
            case 2:
                day = "Monday";
                break;
            case 3:
                day = "Tuesday";
                break;
            case 4:
                day = "Wednesday";
                break;
            case 5:
                day = "Thursday";
                break;
            case 6:
                day = "Friday";
                break;
            case 7:
                day = "Saturday";
                break;
        }
        return day;
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

