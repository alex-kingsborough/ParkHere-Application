package com.example.parkhere.provider;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.parkhere.R;
import com.example.parkhere.objects.ParkingSpace;
import com.example.parkhere.objects.ProviderReservation;
import com.example.parkhere.objects.User;
import com.example.parkhere.server.Constants;
import com.example.parkhere.server.RequestInterface;
import com.example.parkhere.server.ServerRequest;
import com.example.parkhere.server.ServerResponse;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ParkingSpaceReservationDetailsFragment extends Fragment {
    private User user;
    private static final String USER_KEY = "user";
    private ParkingSpace parkingSpace;
    private static final String PARKING_SPACE_KEY = "parkingSpace";
    private ProviderReservation providerReservation;
    private static final String PROVIDER_RESERVATION_KEY = "providerReservation";
    private FragmentActivity myContext;
    private View rootView;
    private TextView ps_resdet_resid, ps_resdet_address, ps_resdet_dateres, ps_resdet_time, ps_resdet_price, ps_resdet_seeker;
    private boolean upcoming = false;

    public static ParkingSpaceReservationDetailsFragment newInstance(User user, ParkingSpace parkingSpace, ProviderReservation providerReservation) {
        ParkingSpaceReservationDetailsFragment fragment = new ParkingSpaceReservationDetailsFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(USER_KEY, user);
        bundle.putSerializable(PARKING_SPACE_KEY, parkingSpace);
        bundle.putSerializable(PROVIDER_RESERVATION_KEY, providerReservation);
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
        providerReservation = (ProviderReservation) getArguments().getSerializable(PROVIDER_RESERVATION_KEY);

        rootView = inflater.inflate(R.layout.fragment_parking_space_reservation_details, container, false);

        ps_resdet_resid = (TextView) rootView.findViewById(R.id.ps_resdet_resid);
        ps_resdet_address = (TextView) rootView.findViewById(R.id.ps_resdet_address);
        ps_resdet_dateres = (TextView) rootView.findViewById(R.id.ps_resdet_dateres);
        ps_resdet_time = (TextView) rootView.findViewById(R.id.ps_resdet_time);
        ps_resdet_price = (TextView) rootView.findViewById(R.id.ps_resdet_price);
        ps_resdet_seeker = (TextView) rootView.findViewById(R.id.ps_resdet_seeker);

        ps_resdet_resid.setText(Integer.toString(providerReservation.getId()));
        ps_resdet_address.setText(parkingSpace.getAddress() + " " + parkingSpace.getCity() + ", " +
                parkingSpace.getState() + " " + parkingSpace.getZipCode() + " " + parkingSpace.getCountry());
        ps_resdet_dateres.setText(providerReservation.getDateReserved());
        ps_resdet_time.setText("Start: " + providerReservation.getStartDate() + " " + getTime(providerReservation.getStartHour())
                + "\n" + "End: " + providerReservation.getEndDate() + " " + getTime(providerReservation.getEndHour()));
        ps_resdet_price.setText("Total Price: $" + Double.toString(providerReservation.getTotalPrice()) +
                "\n" + "Total Paid to You: $" + providerReservation.getTotalPrice() * 0.90 +
                "\n" + "Direct Deposit Information: " + "\n" + providerReservation.getProviderPayment().getBank() + "\n"
                + providerReservation.getProviderPayment().getAccountType() + "\n" +
                "Account Number Ending in " + providerReservation.getProviderPayment().getAccountNumber() +
                "\n" + "Routing Number Ending in " + providerReservation.getProviderPayment().getRoutingNumber());

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RequestInterface requestInterface = retrofit.create(RequestInterface.class);

        ServerRequest request = new ServerRequest();
        request.setOperation(Constants.GET_CURRENT_TIME_OPERATION);
        Call<ServerResponse> response = requestInterface.operation(request);

        response.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, retrofit2.Response<ServerResponse> response) {
                ServerResponse resp = response.body();
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm a");
                    Date currentDate = sdf.parse(resp.getMessage());
                    Date endDate = sdf.parse(providerReservation.getEndDate() + " " + getTime(providerReservation.getEndHour()));
                    upcoming = endDate.after(currentDate);

                    if (upcoming == true) {
                        ps_resdet_seeker.setText(providerReservation.getSeeker().getFirstName() + "\n" +
                                providerReservation.getSeeker().getEmail() + "\n" +
                                providerReservation.getSeeker().getPhoneNumber() + "\n" + "\n" +
                                "Seeker Vehicle" + "\n" +
                                providerReservation.getSeekerVehicle().getMake() + " " +
                                providerReservation.getSeekerVehicle().getModel() + " " +
                                providerReservation.getSeekerVehicle().getYear() + "\n" +
                                providerReservation.getSeekerVehicle().getColor() + "\n" +
                                providerReservation.getSeekerVehicle().getLicensePlate());
                    } else {
                        ps_resdet_seeker.setText(providerReservation.getSeeker().getFirstName() + "\n" +
                                providerReservation.getSeeker().getEmail() + "\n" +
                                providerReservation.getSeeker().getPhoneNumber());
                    }
                } catch (ParseException e) {
                    System.out.println("Parse Exception " + e.getLocalizedMessage());
                }
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                Snackbar.make(rootView, t.getLocalizedMessage(), Snackbar.LENGTH_LONG).show();
            }
        });

        return rootView;
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

