package com.example.parkhere.provider;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import java.util.Collections;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import com.roomorama.caldroid.CaldroidFragment;
import java.util.Calendar;

public class ParkingSpaceCalendarViewFragment extends Fragment {
    private User user;
    private ParkingSpace parkingSpace;
    private static final String USER_KEY = "user";
    private static final String PARKING_SPACE_KEY = "parkingSpace";
    private View rootView;
    private FragmentActivity myContext;
    List<String> startDates = new ArrayList<>();
    private CaldroidFragment caldroidFragment;

    @Override
    public void onAttach(Activity activity) {
        myContext=(FragmentActivity) activity;
        super.onAttach(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        user = (User) getArguments().getSerializable(USER_KEY);
        parkingSpace = (ParkingSpace) getArguments().getSerializable(PARKING_SPACE_KEY);

        getCurrAvailability();

        rootView = inflater.inflate(R.layout.fragment_parking_space_calendar_view, container, false);

        return rootView;
    }

    private void setCustomResourceForDates() {
        Calendar cal = Calendar.getInstance();

        // Min date is today
        cal.add(Calendar.DATE, 0);
        Date greenDate = cal.getTime();

        if (caldroidFragment != null) {
            ColorDrawable green = new ColorDrawable(myContext.getResources().getColor(R.color.green));
            caldroidFragment.setBackgroundDrawableForDate(green, greenDate);
            caldroidFragment.setTextColorForDate(R.color.white, greenDate);
            caldroidFragment.setMinDate(greenDate);
        }
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
                    startDates = new ArrayList<String>();

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
                            startDates.add(start.getStartDate());
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
                            startDates.add(start.getStartDate());
                        } catch (ParseException e) {
                            System.out.println("Parse Error" + e.getLocalizedMessage());
                        }
                    }

                    //TEST
                    final SimpleDateFormat formatter = new SimpleDateFormat("dd MMM yyyy");

                    // Setup caldroid fragment
                    caldroidFragment = new CaldroidSampleCustomFragment(startDates);

                    // Setup arguments
                    Bundle args = new Bundle();
                    Calendar cal = Calendar.getInstance();
                    args.putInt(CaldroidFragment.MONTH, cal.get(Calendar.MONTH) + 1);
                    args.putInt(CaldroidFragment.YEAR, cal.get(Calendar.YEAR));
                    args.putBoolean(CaldroidFragment.ENABLE_SWIPE, true);
                    args.putBoolean(CaldroidFragment.SIX_WEEKS_IN_CALENDAR, true);

                    caldroidFragment.setArguments(args);

                    setCustomResourceForDates();

                    // Attach to the activity
                    FragmentTransaction t = myContext.getSupportFragmentManager().beginTransaction();
                    t.replace(R.id.calendar1, caldroidFragment);
                    t.commit();
                }
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                Snackbar.make(rootView, t.getLocalizedMessage(), Snackbar.LENGTH_LONG).show();
            }
        });
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
