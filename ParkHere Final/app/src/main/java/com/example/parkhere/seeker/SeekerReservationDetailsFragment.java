package com.example.parkhere.seeker;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.parkhere.R;
import com.example.parkhere.objects.SeekerReservation;
import com.example.parkhere.objects.User;

public class SeekerReservationDetailsFragment extends Fragment {
    private User user;
    private static final String USER_KEY = "user";
    private SeekerReservation seekerReservation;
    private static final String SEEKER_RESERVATION_KEY = "seekerReservation";
    private FragmentActivity myContext;
    private View rootView;
    private TextView sr_resdet_resid, sr_resdet_address, sr_resdet_dateres, sr_resdet_time, sr_resdet_price, sr_resdet_provider, sr_resdet_ratings;

    public static SeekerReservationDetailsFragment newInstance(User user, SeekerReservation seekerReservation) {
        SeekerReservationDetailsFragment fragment = new SeekerReservationDetailsFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(USER_KEY, user);
        bundle.putSerializable(SEEKER_RESERVATION_KEY, seekerReservation);
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
        seekerReservation = (SeekerReservation) getArguments().getSerializable(SEEKER_RESERVATION_KEY);

        rootView = inflater.inflate(R.layout.fragment_seeker_reservation_details, container, false);

        sr_resdet_resid = (TextView) rootView.findViewById(R.id.sr_resdet_resid);
        sr_resdet_address = (TextView) rootView.findViewById(R.id.sr_resdet_address);
        sr_resdet_dateres = (TextView) rootView.findViewById(R.id.sr_resdet_dateres);
        sr_resdet_time = (TextView) rootView.findViewById(R.id.sr_resdet_time);
        sr_resdet_price = (TextView) rootView.findViewById(R.id.sr_resdet_price);
        sr_resdet_provider = (TextView) rootView.findViewById(R.id.sr_resdet_provider);
        sr_resdet_ratings = (TextView) rootView.findViewById(R.id.sr_resdet_ratings);

        sr_resdet_resid.setText(Integer.toString(seekerReservation.getId()));
        String preq = "";
        if (seekerReservation.getParkingSpace().getPermitRequirement() == 1) {
            preq = "Yes";
        } else {
            preq = "No";
        }
        sr_resdet_address.setText("Address: \n" + seekerReservation.getParkingSpace().getAddress() + " " + seekerReservation.getParkingSpace().getCity() + ", " +
                seekerReservation.getParkingSpace().getState() + " " + seekerReservation.getParkingSpace().getZipCode() + " " +
                seekerReservation.getParkingSpace().getCountry() +
                "\n" + "Type: " + seekerReservation.getParkingSpace().getType() +
                "\n" + "Permit Requirement: " + preq +
                "\n" + "Cancellation Policy: " + seekerReservation.getParkingSpace().getCancellationPolicy() +
                "\n" + "Additional Information: " + seekerReservation.getParkingSpace().getAdditionalInfo());
        sr_resdet_dateres.setText(seekerReservation.getDateReserved());
        sr_resdet_time.setText("Start: " + seekerReservation.getStartDate() + " " + getTime(seekerReservation.getStartHour())
                + "\n" + "End: " + seekerReservation.getEndDate() + " " + getTime(seekerReservation.getEndHour()));
        sr_resdet_price.setText("Total Price: $" + Double.toString(seekerReservation.getTotalPrice()) +
                "\n" + "Charge Information: " + "\n" + seekerReservation.getSeekerPayment().getName() +
                " Ending in " + seekerReservation.getSeekerPayment().getNumber() +
                "\n" + "Cardholder: " + seekerReservation.getSeekerPayment().getFirstName() + " " +
                seekerReservation.getSeekerPayment().getLastName() +
                "\n" + "Expiring: " + seekerReservation.getSeekerPayment().getExpMonth() + ", " +
                seekerReservation.getSeekerPayment().getExpYear());

        sr_resdet_provider.setText(seekerReservation.getProvider().getFirstName() + "\n" +
                seekerReservation.getProvider().getEmail() + "\n" +
                seekerReservation.getProvider().getPhoneNumber());

        sr_resdet_ratings.setText("Parking Space Rating: " +
                seekerReservation.getSeekerParkingSpaceRating().getRating() + "/5 Stars, Comments: " +
                seekerReservation.getSeekerParkingSpaceRating().getComment() + "\n" +
                "Provider Rating: " +
                seekerReservation.getSeekerProviderRating().getRating() + "/5 Stars, Comments: " +
                seekerReservation.getSeekerProviderRating().getComment());

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
