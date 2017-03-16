package com.example.parkhere.seeker;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import com.example.parkhere.R;
import com.example.parkhere.objects.User;
import com.example.parkhere.provider.EndDatePickerFragment;
import com.example.parkhere.provider.StartDatePickerFragment;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class SeekerHomeFragment extends Fragment {
    private User user;
    private static final String USER_KEY = "user";
    private FragmentActivity myContext;
    private View rootView;
    private EditText seeker_home_address, seeker_home_start_date, seeker_home_end_date;
    private Spinner seeker_home_start_time, seeker_home_end_time;
    private ImageView seeker_home_startDatePicker, seeker_home_endDatePicker;
    private CheckBox compact, suv, truck;
    private RadioGroup seeker_home_handicap, seeker_home_covered;
    private RadioButton notHandicap, handicap, eitherHandicap;
    private RadioButton notCovered, covered, eitherCovered;
    private Button seeker_home_list_button, seeker_home_map_button, seeker_home_repeat_button;
    private int START_REQUEST_CODE = 1;
    private int END_REQUEST_CODE = 2;
    private EditText search_psar_start_date, search_psar_end_date, search_psar_address;
    private Spinner search_psar_weekday, search_psar_start_time, search_psar_end_time;
    private Button search_psar_list_button, search_psar_map_button;
    private ImageView search_psar_startDatePicker, search_psar_endDatePicker;
    private CheckBox search_compact, search_suv, search_truck;
    private RadioGroup search_rg_handicap, search_rg_covered;
    private RadioButton search_notHandicap, search_handicap, search_eitherHandicap;
    private RadioButton search_notCovered, search_covered, search_eitherCovered;
    private String weekday;
    private GoogleApiClient client;
    @RequiresApi(api = Build.VERSION_CODES.N)

    public static SeekerHomeFragment newInstance(User user) {
        SeekerHomeFragment fragment = new SeekerHomeFragment();
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

        rootView = inflater.inflate(R.layout.fragment_seeker_home, container, false);

        seeker_home_address = (EditText) rootView.findViewById(R.id.seeker_home_address);
        seeker_home_start_date = (EditText) rootView.findViewById(R.id.seeker_home_start_date);
        seeker_home_end_date = (EditText) rootView.findViewById(R.id.seeker_home_end_date);

        seeker_home_start_time = (Spinner) rootView.findViewById(R.id.seeker_home_start_time_spinner);
        seeker_home_end_time = (Spinner) rootView.findViewById(R.id.seeker_home_end_time_spinner);

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
        seeker_home_start_time.setAdapter(dataAdapter);
        seeker_home_end_time.setAdapter(dataAdapter);

        seeker_home_startDatePicker = (ImageView) rootView.findViewById(R.id.seeker_home_startDatePicker);
        seeker_home_endDatePicker = (ImageView) rootView.findViewById(R.id.seeker_home_endDatePicker);

        seeker_home_startDatePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment picker = new StartDatePickerFragment();
                picker.setTargetFragment(SeekerHomeFragment.this, START_REQUEST_CODE);
                picker.show(getFragmentManager(), "datePicker");
            }
        });

        seeker_home_endDatePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment picker = new EndDatePickerFragment();
                picker.setTargetFragment(SeekerHomeFragment.this, END_REQUEST_CODE);
                picker.show(getFragmentManager(), "datePicker");
            }
        });

        compact = (CheckBox) rootView.findViewById(R.id.compact);
        suv = (CheckBox) rootView.findViewById(R.id.suv);
        truck = (CheckBox) rootView.findViewById(R.id.truck);

        seeker_home_handicap = (RadioGroup) rootView.findViewById(R.id.seeker_home_handicap);
        seeker_home_covered = (RadioGroup) rootView.findViewById(R.id.seeker_home_covered);

        notHandicap = (RadioButton) rootView.findViewById(R.id.notHandicap);
        handicap = (RadioButton) rootView.findViewById(R.id.handicap);
        eitherHandicap = (RadioButton) rootView.findViewById(R.id.eitherHandicap);

        notCovered = (RadioButton) rootView.findViewById(R.id.notCovered);
        covered = (RadioButton) rootView.findViewById(R.id.covered);
        eitherCovered = (RadioButton) rootView.findViewById(R.id.eitherCovered);

        seeker_home_list_button = (Button) rootView.findViewById(R.id.seeker_home_list_button);
        seeker_home_map_button = (Button) rootView.findViewById(R.id.seeker_home_map_button);
        seeker_home_repeat_button = (Button) rootView.findViewById(R.id.seeker_home_repeat_button);

        seeker_home_list_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String address = seeker_home_address.getText().toString();
                String startDate = seeker_home_start_date.getText().toString();
                String endDate = seeker_home_end_date.getText().toString();
                String startTime = seeker_home_start_time.getItemAtPosition(seeker_home_start_time.getSelectedItemPosition()).toString();
                String endTime = seeker_home_end_time.getItemAtPosition(seeker_home_end_time.getSelectedItemPosition()).toString();

                if (!validate(address, startDate, endDate)) {
                    Snackbar.make(rootView, "Invalid search parameters", Snackbar.LENGTH_LONG).show();
                    return;
                }
                int startHour = 0;
                int endHour = 0;
                String startTimeSubstring = startTime.substring(startTime.length()-2, startTime.length());
                if (startTimeSubstring.charAt(0) == 'P') {
                    startTimeSubstring = startTime.substring(0, 2);
                    int time = Integer.parseInt(startTimeSubstring) + 12;
                    startHour = time;
                } else {
                    startTimeSubstring = startTime.substring(0, 2);
                    int time;
                    if (startTimeSubstring.equals("12")) {
                        time = 0;
                    } else {
                        time = Integer.parseInt(startTimeSubstring);
                    }

                    startHour = time;
                }

                String endTimeSubstring = endTime.substring(endTime.length()-2, endTime.length());
                if (endTimeSubstring.charAt(0) == 'P') {
                    endTimeSubstring = endTime.substring(0, 2);
                    int time = Integer.parseInt(endTimeSubstring) + 12;
                    endHour = time;
                } else {
                    endTimeSubstring = endTime.substring(0, 2);
                    int time;
                    if (endTimeSubstring.equals("12")) {
                        time = 0;
                    } else {
                        time = Integer.parseInt(endTimeSubstring);
                    }

                    endHour = time;
                }

                if (endHour <= startHour) {
                    Snackbar.make(rootView, "End hour must be after start hour", Snackbar.LENGTH_LONG).show();
                    return;
                }

                if (!compact.isChecked() && !suv.isChecked() && !truck.isChecked()) {
                    Snackbar.make(rootView, "Must select at least one parking space type", Snackbar.LENGTH_LONG).show();
                    return;
                }

                //Longitude, Latitude
                Geocoder geocoder = new Geocoder(myContext);
                List<Address> addressList = null;
                try {
                    addressList = geocoder.getFromLocationName(address, 1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Address location = addressList.get(0);
                double[] latLngs = {location.getLatitude(), location.getLongitude()};

                //Types
                int selectedID = seeker_home_handicap.getCheckedRadioButtonId();
                RadioButton radioButton = (RadioButton) rootView.findViewById(selectedID);
                String handicapType = radioButton.getText().toString();

                selectedID = seeker_home_covered.getCheckedRadioButtonId();
                radioButton = (RadioButton) rootView.findViewById(selectedID);
                String coveredType = radioButton.getText().toString();

                ArrayList<String> types = new ArrayList<String>();
                String car_types = null;
                if (compact.isChecked()) {
                    if (car_types == null)
                        car_types = "Compact";
                    else
                        car_types += ",Compact";
                }
                if (suv.isChecked()) {
                    if (car_types == null)
                        car_types = "SUV";
                    else
                        car_types += ",SUV";
                }
                if (truck.isChecked()) {
                    if (car_types == null)
                        car_types = "Truck";
                    else
                        car_types += ",Truck";
                }
                types.add(car_types);

                String handicap = "";
                if (handicapType.equals("Not Handicap")) {
                    handicap = "Not";
                } else if (handicapType.equals("Handicap")) {
                    handicap = "Handicap";
                } else {
                    handicap = "Both";
                }
                types.add(handicap);

                String covered = "";
                if (coveredType.equals("Not Covered")) {
                    covered = "Not";
                } else if (coveredType.equals("Covered")) {
                    covered = "Covered";
                } else {
                    covered = "Both";
                }
                types.add(covered);

                Intent intent = new Intent(myContext, list_view_screen.class);
                intent.putExtra("latlng", latLngs);

                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    Date date1 = sdf.parse(startDate);
                    SimpleDateFormat sdf2 = new SimpleDateFormat("MM/dd/yyyy");
                    intent.putExtra("dateStart", sdf2.format(date1));
                } catch (ParseException ex) {
                    System.out.println("ERROR" + ex.getLocalizedMessage());
                    //ex.printStackTrace();
                }

                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    Date date1 = sdf.parse(endDate);
                    SimpleDateFormat sdf2 = new SimpleDateFormat("MM/dd/yyyy");
                    intent.putExtra("dateEnd", sdf2.format(date1));
                } catch (ParseException ex) {
                    System.out.println("ERROR" + ex.getLocalizedMessage());
                    //ex.printStackTrace();
                }

                intent.putExtra("types", types.toArray(new String[types.size()]));
                intent.putExtra("user", user);
                intent.putExtra("hourStart", startHour);
                intent.putExtra("hourEnd", endHour);
                intent.putExtra("isRepeatSearch", false);
                startActivity(intent);
            }
        });

        seeker_home_map_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String address = seeker_home_address.getText().toString();
                String startDate = seeker_home_start_date.getText().toString();
                String endDate = seeker_home_end_date.getText().toString();
                String startTime = seeker_home_start_time.getItemAtPosition(seeker_home_start_time.getSelectedItemPosition()).toString();
                String endTime = seeker_home_end_time.getItemAtPosition(seeker_home_end_time.getSelectedItemPosition()).toString();

                if (!validate(address, startDate, endDate)) {
                    Snackbar.make(rootView, "Invalid search parameters", Snackbar.LENGTH_LONG).show();
                    return;
                }
                int startHour = 0;
                int endHour = 0;
                String startTimeSubstring = startTime.substring(startTime.length()-2, startTime.length());
                if (startTimeSubstring.charAt(0) == 'P') {
                    startTimeSubstring = startTime.substring(0, 2);
                    int time = Integer.parseInt(startTimeSubstring) + 12;
                    startHour = time;
                } else {
                    startTimeSubstring = startTime.substring(0, 2);
                    int time;
                    if (startTimeSubstring.equals("12")) {
                        time = 0;
                    } else {
                        time = Integer.parseInt(startTimeSubstring);
                    }

                    startHour = time;
                }

                String endTimeSubstring = endTime.substring(endTime.length()-2, endTime.length());
                if (endTimeSubstring.charAt(0) == 'P') {
                    endTimeSubstring = endTime.substring(0, 2);
                    int time = Integer.parseInt(endTimeSubstring) + 12;
                    endHour = time;
                } else {
                    endTimeSubstring = endTime.substring(0, 2);
                    int time;
                    if (endTimeSubstring.equals("12")) {
                        time = 0;
                    } else {
                        time = Integer.parseInt(endTimeSubstring);
                    }

                    endHour = time;
                }

                if (endHour <= startHour) {
                    Snackbar.make(rootView, "End hour must be after start hour", Snackbar.LENGTH_LONG).show();
                    return;
                }

                if (!compact.isChecked() && !suv.isChecked() && !truck.isChecked()) {
                    Snackbar.make(rootView, "Must select at least one parking space type", Snackbar.LENGTH_LONG).show();
                    return;
                }

                //Longitude, Latitude
                Geocoder geocoder = new Geocoder(myContext);
                List<Address> addressList = null;
                try {
                    addressList = geocoder.getFromLocationName(address, 1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Address location = addressList.get(0);
                double[] latLngs = {location.getLatitude(), location.getLongitude()};

                //Types
                int selectedID = seeker_home_handicap.getCheckedRadioButtonId();
                RadioButton radioButton = (RadioButton) rootView.findViewById(selectedID);
                String handicapType = radioButton.getText().toString();

                selectedID = seeker_home_covered.getCheckedRadioButtonId();
                radioButton = (RadioButton) rootView.findViewById(selectedID);
                String coveredType = radioButton.getText().toString();

                ArrayList<String> types = new ArrayList<String>();
                String car_types = null;
                if (compact.isChecked()) {
                    if (car_types == null)
                        car_types = "Compact";
                    else
                        car_types += ",Compact";
                }
                if (suv.isChecked()) {
                    if (car_types == null)
                        car_types = "SUV";
                    else
                        car_types += ",SUV";
                }
                if (truck.isChecked()) {
                    if (car_types == null)
                        car_types = "Truck";
                    else
                        car_types += ",Truck";
                }
                types.add(car_types);

                String handicap = "";
                if (handicapType.equals("Not Handicap")) {
                    handicap = "Not";
                } else if (handicapType.equals("Handicap")) {
                    handicap = "Handicap";
                } else {
                    handicap = "Both";
                }
                types.add(handicap);

                String covered = "";
                if (coveredType.equals("Not Covered")) {
                    covered = "Not";
                } else if (coveredType.equals("Covered")) {
                    covered = "Covered";
                } else {
                    covered = "Both";
                }
                types.add(covered);

                Intent intent = new Intent(myContext, MapsActivity.class);
                intent.putExtra("latlng", latLngs);

                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    Date date1 = sdf.parse(startDate);
                    SimpleDateFormat sdf2 = new SimpleDateFormat("MM/dd/yyyy");
                    intent.putExtra("dateStart", sdf2.format(date1));
                } catch (ParseException ex) {
                    System.out.println("ERROR" + ex.getLocalizedMessage());
                    //ex.printStackTrace();
                }

                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    Date date1 = sdf.parse(endDate);
                    SimpleDateFormat sdf2 = new SimpleDateFormat("MM/dd/yyyy");
                    intent.putExtra("dateEnd", sdf2.format(date1));
                } catch (ParseException ex) {
                    System.out.println("ERROR" + ex.getLocalizedMessage());
                    //ex.printStackTrace();
                }

                intent.putExtra("types", types.toArray(new String[types.size()]));
                intent.putExtra("user", user);
                intent.putExtra("hourStart", startHour);
                intent.putExtra("hourEnd", endHour);
                intent.putExtra("isRepeatSearch", false);
                startActivity(intent);
            }
        });

        //REPEAT
        seeker_home_repeat_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                LayoutInflater li = LayoutInflater.from(myContext);
                final View customView = li.inflate(R.layout.popup_repeated_search, null);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(myContext);
                alertDialogBuilder.setView(customView);
                final AlertDialog alertDialog = alertDialogBuilder.create();

                ImageButton closeButton = (ImageButton) customView.findViewById(R.id.search_psar_close);
                closeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog.dismiss();
                    }
                });

                search_psar_start_date = (EditText) customView.findViewById(R.id.search_psar_start_date);
                search_psar_end_date = (EditText) customView.findViewById(R.id.search_psar_end_date);
                search_psar_address = (EditText) customView.findViewById(R.id.search_psar_address);
                search_psar_weekday = (Spinner) customView.findViewById(R.id.search_psar_weekday_spinner);
                search_psar_start_time = (Spinner) customView.findViewById(R.id.search_psar_start_time_spinner);
                search_psar_end_time = (Spinner) customView.findViewById(R.id.search_psar_end_time_spinner);
                search_psar_list_button = (Button) customView.findViewById(R.id.search_psar_list_button);
                search_psar_map_button = (Button) customView.findViewById(R.id.search_psar_map_button);
                search_psar_startDatePicker = (ImageView) customView.findViewById(R.id.search_psar_startDatePicker);
                search_psar_endDatePicker = (ImageView) customView.findViewById(R.id.search_psar_endDatePicker);

                search_psar_startDatePicker.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        DialogFragment picker = new StartDatePickerFragment();
                        picker.setTargetFragment(SeekerHomeFragment.this, START_REQUEST_CODE);
                        picker.show(getFragmentManager(), "datePicker");
                    }
                });

                search_psar_endDatePicker.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        DialogFragment picker = new EndDatePickerFragment();
                        picker.setTargetFragment(SeekerHomeFragment.this, END_REQUEST_CODE);
                        picker.show(getFragmentManager(), "datePicker");
                    }
                });

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
                ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(customView.getContext(), android.R.layout.simple_spinner_item, categories);
                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                search_psar_start_time.setAdapter(dataAdapter);
                search_psar_end_time.setAdapter(dataAdapter);

                categories = new ArrayList<String>();
                categories.add("Sunday");
                categories.add("Monday");
                categories.add("Tuesday");
                categories.add("Wednesday");
                categories.add("Thursday");
                categories.add("Friday");
                categories.add("Saturday");
                dataAdapter = new ArrayAdapter<String>(customView.getContext(), android.R.layout.simple_spinner_item, categories);
                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                search_psar_weekday.setAdapter(dataAdapter);

                search_compact = (CheckBox) customView.findViewById(R.id.search_compact);
                search_suv = (CheckBox) customView.findViewById(R.id.search_suv);
                search_truck = (CheckBox) customView.findViewById(R.id.search_truck);

                search_rg_handicap = (RadioGroup) customView.findViewById(R.id.search_rg_handicap);
                search_rg_covered = (RadioGroup) customView.findViewById(R.id.search_rg_covered);

                search_notHandicap = (RadioButton) customView.findViewById(R.id.search_notHandicap);
                search_handicap = (RadioButton) customView.findViewById(R.id.search_handicap);
                search_eitherHandicap = (RadioButton) customView.findViewById(R.id.search_eitherHandicap);

                search_notCovered = (RadioButton) customView.findViewById(R.id.search_notCovered);
                search_covered = (RadioButton) customView.findViewById(R.id.search_covered);
                search_eitherCovered = (RadioButton) customView.findViewById(R.id.search_eitherCovered);

                search_psar_list_button.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view) {
                        String startDate = search_psar_start_date.getText().toString();
                        String endDate = search_psar_end_date.getText().toString();
                        String address = search_psar_address.getText().toString();
                        weekday = search_psar_weekday.getItemAtPosition(search_psar_weekday.getSelectedItemPosition()).toString();;

                        if (!validateRepeated(startDate, endDate, address)) {
                            Snackbar.make(customView, "Invalid search repeated availability parameters", Snackbar.LENGTH_LONG).show();
                        } else {
                            System.out.println("I'm clicked");//TESTING

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

                            String startTime = search_psar_start_time.getItemAtPosition(search_psar_start_time.getSelectedItemPosition()).toString();
                            String endTime = search_psar_end_time.getItemAtPosition(search_psar_end_time.getSelectedItemPosition()).toString();

                            int startHour = 0;
                            int endHour = 0;
                            String startTimeSubstring = startTime.substring(startTime.length()-2, startTime.length());
                            if (startTimeSubstring.charAt(0) == 'P') {
                                startTimeSubstring = startTime.substring(0, 2);
                                int time = Integer.parseInt(startTimeSubstring) + 12;
                                startHour = time;
                            } else {
                                startTimeSubstring = startTime.substring(0, 2);
                                int time;
                                if (startTimeSubstring.equals("12")) {
                                    time = 0;
                                } else {
                                    time = Integer.parseInt(startTimeSubstring);
                                }

                                startHour = time;
                            }

                            String endTimeSubstring = endTime.substring(endTime.length()-2, endTime.length());
                            if (endTimeSubstring.charAt(0) == 'P') {
                                endTimeSubstring = endTime.substring(0, 2);
                                int time = Integer.parseInt(endTimeSubstring) + 12;
                                endHour = time;
                            } else {
                                endTimeSubstring = endTime.substring(0, 2);
                                int time;
                                if (endTimeSubstring.equals("12")) {
                                    time = 0;
                                } else {
                                    time = Integer.parseInt(endTimeSubstring);
                                }

                                endHour = time;
                            }

                            if (endHour <= startHour) {
                                Snackbar.make(customView, "End hour must be after start hour", Snackbar.LENGTH_LONG).show();
                                return;
                            }

                            if (!search_compact.isChecked() && !search_suv.isChecked() && !search_truck.isChecked()) {
                                Snackbar.make(customView, "Must select at least one parking space type", Snackbar.LENGTH_LONG).show();
                                return;
                            }

                            //Longitude, Latitude
                            Geocoder geocoder = new Geocoder(myContext);
                            List<Address> addressList = null;
                            try {
                                addressList = geocoder.getFromLocationName(address, 1);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            Address location = addressList.get(0);
                            double[] latLngs = {location.getLatitude(), location.getLongitude()};

                            //Types
                            int selectedID = search_rg_handicap.getCheckedRadioButtonId();
                            RadioButton radioButton = (RadioButton) customView.findViewById(selectedID);
                            String handicapType = radioButton.getText().toString();

                            selectedID = search_rg_covered.getCheckedRadioButtonId();
                            radioButton = (RadioButton) customView.findViewById(selectedID);
                            String coveredType = radioButton.getText().toString();

                            ArrayList<String> types = new ArrayList<String>();
                            String car_types = null;
                            if (search_compact.isChecked()) {
                                if (car_types == null)
                                    car_types = "Compact";
                                else
                                    car_types += ",Compact";
                            }
                            if (search_suv.isChecked()) {
                                if (car_types == null)
                                    car_types = "SUV";
                                else
                                    car_types += ",SUV";
                            }
                            if (search_truck.isChecked()) {
                                if (car_types == null)
                                    car_types = "Truck";
                                else
                                    car_types += ",Truck";
                            }
                            types.add(car_types);

                            String handicap = "";
                            if (handicapType.equals("Not Handicap")) {
                                handicap = "Not";
                            } else if (handicapType.equals("Handicap")) {
                                handicap = "Handicap";
                            } else {
                                handicap = "Both";
                            }
                            types.add(handicap);

                            String covered = "";
                            if (coveredType.equals("Not Covered")) {
                                covered = "Not";
                            } else if (coveredType.equals("Covered")) {
                                covered = "Covered";
                            } else {
                                covered = "Both";
                            }
                            types.add(covered);

                            Intent intent = new Intent(myContext, list_view_screen.class);
                            intent.putExtra("latlng", latLngs);

                            try {
                                sdf = new SimpleDateFormat("yyyy-MM-dd");
                                Date date1 = sdf.parse(startDate);
                                SimpleDateFormat sdf2 = new SimpleDateFormat("MM/dd/yyyy");
                                intent.putExtra("dateStart", sdf2.format(date1));
                            } catch (ParseException ex) {
                                System.out.println("ERROR" + ex.getLocalizedMessage());
                                //ex.printStackTrace();
                            }

                            try {
                                sdf = new SimpleDateFormat("yyyy-MM-dd");
                                Date date1 = sdf.parse(endDate);
                                SimpleDateFormat sdf2 = new SimpleDateFormat("MM/dd/yyyy");
                                intent.putExtra("dateEnd", sdf2.format(date1));
                            } catch (ParseException ex) {
                                System.out.println("ERROR" + ex.getLocalizedMessage());
                                //ex.printStackTrace();
                            }

                            intent.putExtra("types", types.toArray(new String[types.size()]));
                            intent.putExtra("user", user);
                            intent.putExtra("hourStart", startHour);
                            intent.putExtra("hourEnd", endHour);
                            intent.putExtra("isRepeatSearch", true);
                            intent.putExtra("weekDay", weekday);
                            startActivity(intent);
                        }
                    }
                });

                search_psar_map_button.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view) {
                        String startDate = search_psar_start_date.getText().toString();
                        String endDate = search_psar_end_date.getText().toString();
                        String address = search_psar_address.getText().toString();
                        weekday = search_psar_weekday.getItemAtPosition(search_psar_weekday.getSelectedItemPosition()).toString();;

                        if (!validateRepeated(startDate, endDate, address)) {
                            Snackbar.make(customView, "Invalid search repeated availability parameters", Snackbar.LENGTH_LONG).show();
                        } else {
                            System.out.println("I'm clicked");//TESTING

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

                            String startTime = search_psar_start_time.getItemAtPosition(search_psar_start_time.getSelectedItemPosition()).toString();
                            String endTime = search_psar_end_time.getItemAtPosition(search_psar_end_time.getSelectedItemPosition()).toString();

                            int startHour = 0;
                            int endHour = 0;
                            String startTimeSubstring = startTime.substring(startTime.length()-2, startTime.length());
                            if (startTimeSubstring.charAt(0) == 'P') {
                                startTimeSubstring = startTime.substring(0, 2);
                                int time = Integer.parseInt(startTimeSubstring) + 12;
                                startHour = time;
                            } else {
                                startTimeSubstring = startTime.substring(0, 2);
                                int time;
                                if (startTimeSubstring.equals("12")) {
                                    time = 0;
                                } else {
                                    time = Integer.parseInt(startTimeSubstring);
                                }

                                startHour = time;
                            }

                            String endTimeSubstring = endTime.substring(endTime.length()-2, endTime.length());
                            if (endTimeSubstring.charAt(0) == 'P') {
                                endTimeSubstring = endTime.substring(0, 2);
                                int time = Integer.parseInt(endTimeSubstring) + 12;
                                endHour = time;
                            } else {
                                endTimeSubstring = endTime.substring(0, 2);
                                int time;
                                if (endTimeSubstring.equals("12")) {
                                    time = 0;
                                } else {
                                    time = Integer.parseInt(endTimeSubstring);
                                }

                                endHour = time;
                            }

                            if (endHour <= startHour) {
                                Snackbar.make(customView, "End hour must be after start hour", Snackbar.LENGTH_LONG).show();
                                return;
                            }

                            if (!search_compact.isChecked() && !search_suv.isChecked() && !search_truck.isChecked()) {
                                Snackbar.make(customView, "Must select at least one parking space type", Snackbar.LENGTH_LONG).show();
                                return;
                            }

                            //Longitude, Latitude
                            Geocoder geocoder = new Geocoder(myContext);
                            List<Address> addressList = null;
                            try {
                                addressList = geocoder.getFromLocationName(address, 1);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            Address location = addressList.get(0);
                            double[] latLngs = {location.getLatitude(), location.getLongitude()};

                            //Types
                            int selectedID = search_rg_handicap.getCheckedRadioButtonId();
                            RadioButton radioButton = (RadioButton) customView.findViewById(selectedID);
                            String handicapType = radioButton.getText().toString();

                            selectedID = search_rg_covered.getCheckedRadioButtonId();
                            radioButton = (RadioButton) customView.findViewById(selectedID);
                            String coveredType = radioButton.getText().toString();

                            ArrayList<String> types = new ArrayList<String>();
                            String car_types = null;
                            if (search_compact.isChecked()) {
                                if (car_types == null)
                                    car_types = "Compact";
                                else
                                    car_types += ",Compact";
                            }
                            if (search_suv.isChecked()) {
                                if (car_types == null)
                                    car_types = "SUV";
                                else
                                    car_types += ",SUV";
                            }
                            if (search_truck.isChecked()) {
                                if (car_types == null)
                                    car_types = "Truck";
                                else
                                    car_types += ",Truck";
                            }
                            types.add(car_types);

                            String handicap = "";
                            if (handicapType.equals("Not Handicap")) {
                                handicap = "Not";
                            } else if (handicapType.equals("Handicap")) {
                                handicap = "Handicap";
                            } else {
                                handicap = "Both";
                            }
                            types.add(handicap);

                            String covered = "";
                            if (coveredType.equals("Not Covered")) {
                                covered = "Not";
                            } else if (coveredType.equals("Covered")) {
                                covered = "Covered";
                            } else {
                                covered = "Both";
                            }
                            types.add(covered);

                            Intent intent = new Intent(myContext, MapsActivity.class);
                            intent.putExtra("latlng", latLngs);

                            try {
                                sdf = new SimpleDateFormat("yyyy-MM-dd");
                                Date date1 = sdf.parse(startDate);
                                SimpleDateFormat sdf2 = new SimpleDateFormat("MM/dd/yyyy");
                                intent.putExtra("dateStart", sdf2.format(date1));
                            } catch (ParseException ex) {
                                System.out.println("ERROR" + ex.getLocalizedMessage());
                                //ex.printStackTrace();
                            }

                            try {
                                sdf = new SimpleDateFormat("yyyy-MM-dd");
                                Date date1 = sdf.parse(endDate);
                                SimpleDateFormat sdf2 = new SimpleDateFormat("MM/dd/yyyy");
                                intent.putExtra("dateEnd", sdf2.format(date1));
                            } catch (ParseException ex) {
                                System.out.println("ERROR" + ex.getLocalizedMessage());
                                //ex.printStackTrace();
                            }

                            intent.putExtra("types", types.toArray(new String[types.size()]));
                            intent.putExtra("user", user);
                            intent.putExtra("hourStart", startHour);
                            intent.putExtra("hourEnd", endHour);
                            intent.putExtra("isRepeatSearch", true);
                            intent.putExtra("weekDay", weekday);
                            startActivity(intent);
                        }
                    }
                });

                alertDialog.show();
            }
        });

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(myContext).addApi(AppIndex.API).build();

        return rootView;
    }

    public boolean validateRepeated(String startDate, String endDate, String address) {
        boolean valid = true;

        if (startDate.isEmpty() || startDate.length() != 10) {
            search_psar_start_date.setError("Enter a valid start date [YYYY-MM-DD]");
            valid = false;
        } else {
            if (startDate.charAt(4) != '-' || startDate.charAt(7) != '-') {
                search_psar_start_date.setError("Enter a valid start date [YYYY-MM-DD]");
                valid = false;
            } else {
                search_psar_start_date.setError(null);
            }
        }

        if (endDate.isEmpty() || endDate.length() != 10) {
            search_psar_end_date.setError("Enter a valid end date [YYYY-MM-DD]");
            valid = false;
        } else {
            if (endDate.charAt(4) != '-' || endDate.charAt(7) != '-') {
                search_psar_end_date.setError("Enter a valid end date [YYYY-MM-DD]");
                valid = false;
            } else {
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    Date date1 = sdf.parse(startDate);
                    Date date2 = sdf.parse(endDate);

                    long diff = date2.getTime() - date1.getTime();
                    String days = Long.toString(TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS));

                    if (Integer.parseInt(days) < 7) {
                        search_psar_end_date.setError("End date must at least 7 days after the start date");
                        valid = false;
                    } else {
                        search_psar_end_date.setError(null);
                    }
                } catch (ParseException ex) {
                    System.out.println("ERROR" + ex.getLocalizedMessage());
                    //ex.printStackTrace();
                }
            }
        }

        if (address.isEmpty()){
            search_psar_address.setError("Enter a valid address");
            valid = false;
        } else {
            search_psar_address.setError(null);
        }

        return valid;
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

    public boolean validate(String address, String startDate, String endDate) {
        boolean valid = true;

        if (address.isEmpty()){
            seeker_home_address.setError("Enter a valid address");
            valid = false;
        } else {
            seeker_home_address.setError(null);
        }

        if (startDate.isEmpty() || startDate.length() != 10) {
            seeker_home_start_date.setError("Enter a valid start date [YYYY-MM-DD]");
            valid = false;
        } else {
            if (startDate.charAt(4) != '-' || startDate.charAt(7) != '-') {
                seeker_home_start_date.setError("Enter a valid start date [YYYY-MM-DD]");
                valid = false;
            } else {
                seeker_home_start_date.setError(null);
            }
        }

        if (endDate.isEmpty() || endDate.length() != 10) {
            seeker_home_end_date.setError("Enter a valid end date [YYYY-MM-DD]");
            valid = false;
        } else {
            if (endDate.charAt(4) != '-' || endDate.charAt(7) != '-') {
                seeker_home_end_date.setError("Enter a valid end date [YYYY-MM-DD]");
                valid = false;
            } else {
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    Date date1 = sdf.parse(startDate);
                    Date date2 = sdf.parse(endDate);

                    if (!date2.after(date1) && !date1.equals(date2)) {
                        seeker_home_end_date.setError("End date must be the same as or after the start date");
                        valid = false;
                    } else {
                        seeker_home_end_date.setError(null);
                    }
                } catch (ParseException ex) {
                    System.out.println("ERROR" + ex.getLocalizedMessage());
                    //ex.printStackTrace();
                }
            }
        }

        return valid;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == StartDatePickerFragment.REQUEST_CODE) {
            String start = data.getStringExtra(StartDatePickerFragment.DATE_KEY);
            seeker_home_start_date.setText(start);
            if (search_psar_start_date != null) {
                search_psar_start_date.setText(start);
            }
        } else if (requestCode == EndDatePickerFragment.REQUEST_CODE) {
            String end = data.getStringExtra(EndDatePickerFragment.DATE_KEY);
            seeker_home_end_date.setText(end);
            if (search_psar_end_date != null) {
                search_psar_end_date.setText(end);
            }
        }
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("search_home_screen Page") // TODO: Define a title for the content shown.
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
