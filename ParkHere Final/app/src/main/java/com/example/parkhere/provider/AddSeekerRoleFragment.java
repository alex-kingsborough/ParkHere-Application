package com.example.parkhere.provider;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;

import com.example.parkhere.R;
import com.example.parkhere.objects.BillingAddress;
import com.example.parkhere.objects.SeekerPayment;
import com.example.parkhere.objects.User;
import com.example.parkhere.objects.Vehicle;
import com.example.parkhere.seeker.SeekerHomeActivity;
import com.example.parkhere.server.Constants;
import com.example.parkhere.server.RequestInterface;
import com.example.parkhere.server.ServerRequest;
import com.example.parkhere.server.ServerResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AddSeekerRoleFragment extends Fragment {
    private User user;
    private static final String USER_KEY = "user";
    private ScrollView create_sprof_scrollview;
    private EditText create_sprof_ccfname, create_sprof_cclname, create_sprof_ccname, create_sprof_ccnumber, create_sprof_ccexpmonth, create_sprof_ccexpyear;
    private EditText create_spof_address, create_spof_city, create_spof_state, create_spof_zipcode, create_spof_country;
    private EditText create_spof_make, create_spof_model, create_spof_color, create_spof_year, create_spof_licenseplate;
    private Button create_sprof_button;
    private View rootView;
    private FragmentActivity myContext;

    public static AddSeekerRoleFragment newInstance(User user) {
        AddSeekerRoleFragment fragment = new AddSeekerRoleFragment();
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
        rootView = inflater.inflate(R.layout.fragment_add_seeker_role, container, false);

        create_sprof_scrollview = (ScrollView) rootView.findViewById(R.id.add_sprof_scrollview);
        create_sprof_ccfname = (EditText) rootView.findViewById(R.id.add_sprof_ccfname);
        create_sprof_cclname =(EditText) rootView.findViewById(R.id.add_sprof_cclname);
        create_sprof_ccname =(EditText) rootView.findViewById(R.id.add_sprof_ccname);
        create_sprof_ccnumber = (EditText) rootView.findViewById(R.id.add_sprof_ccnumber);
        create_sprof_ccexpmonth = (EditText) rootView.findViewById(R.id.add_sprof_ccexpmonth);
        create_sprof_ccexpyear =(EditText) rootView.findViewById(R.id.add_sprof_ccexpyear);

        create_spof_address = (EditText) rootView.findViewById(R.id.add_sprof_address);
        create_spof_city = (EditText) rootView.findViewById(R.id.add_sprof_city);
        create_spof_state = (EditText) rootView.findViewById(R.id.add_sprof_state);
        create_spof_zipcode = (EditText) rootView.findViewById(R.id.add_sprof_zipcode);
        create_spof_country = (EditText) rootView.findViewById(R.id.add_sprof_country);

        create_spof_make = (EditText) rootView.findViewById(R.id.add_sprof_make);
        create_spof_model = (EditText) rootView.findViewById(R.id.add_sprof_model);
        create_spof_color = (EditText) rootView.findViewById(R.id.add_sprof_color);
        create_spof_year = (EditText) rootView.findViewById(R.id.add_sprof_year);
        create_spof_licenseplate = (EditText) rootView.findViewById(R.id.add_sprof_licenseplate);

        create_sprof_button = (Button) rootView.findViewById(R.id.add_sprof_button);

        create_sprof_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                createSeekerProfile();
            }
        });

        return rootView;
    }

    public void createSeekerProfile() {
        String ccfname = create_sprof_ccfname.getText().toString();
        String cclname = create_sprof_cclname.getText().toString();
        String ccname = create_sprof_ccname.getText().toString();
        String ccnumber = create_sprof_ccnumber.getText().toString();
        String ccexpmonth = create_sprof_ccexpmonth.getText().toString();
        String ccexpyear = create_sprof_ccexpyear.getText().toString();

        SeekerPayment seekerPayment = new SeekerPayment();
        seekerPayment.setFirstName(ccfname);
        seekerPayment.setLastName(cclname);
        seekerPayment.setName(ccname);
        seekerPayment.setExpMonth(ccexpmonth);
        seekerPayment.setExpYear(ccexpyear);

        String address = create_spof_address.getText().toString();
        String city = create_spof_city.getText().toString();
        String state = create_spof_state.getText().toString();
        String zipCode = create_spof_zipcode.getText().toString();
        String country = create_spof_country.getText().toString();

        BillingAddress billingAddress = new BillingAddress();
        billingAddress.setAddress(address);
        billingAddress.setCity(city);
        billingAddress.setState(state);
        billingAddress.setZipCode(zipCode);
        billingAddress.setCountry(country);

        String make = create_spof_make.getText().toString();
        String model = create_spof_model.getText().toString();
        String color = create_spof_color.getText().toString();
        String year = create_spof_year.getText().toString();
        String licensePlate = create_spof_licenseplate.getText().toString();

        Vehicle vehicle = new Vehicle();
        vehicle.setMake(make);
        vehicle.setModel(model);
        vehicle.setColor(color);
        vehicle.setYear(year);
        vehicle.setLicensePlate(licensePlate);

        if (!validate(seekerPayment, billingAddress, vehicle)) {
            onAddSeekerRoleFailed();
            return;
        } else {
            int ccNum = Integer.parseInt(ccnumber.substring(ccnumber.length()-4, ccnumber.length()));
            seekerPayment.setNumber(Integer.toString(ccNum));
            createSeekerProfileProcess(seekerPayment, billingAddress, vehicle);
        }
    }

    public void onAddSeekerRoleFailed() {
        Snackbar.make(create_sprof_scrollview, "Invalid add seeker role parameters", Snackbar.LENGTH_LONG).show();
    }

    public boolean validate(SeekerPayment seekerPayment, BillingAddress billingAddress,
                            Vehicle vehicle) {
        boolean valid = true;

        //SEEKER PAYMENT CHECKS
        if (seekerPayment.getFirstName().isEmpty()) {
            create_sprof_ccfname.setError("Enter a valid cardholder first name");
            valid = false;
        } else {
            create_sprof_ccfname.setError(null);
        }

        if (seekerPayment.getLastName().isEmpty()) {
            create_sprof_cclname.setError("Enter a valid cardholder last name");
            valid = false;
        } else {
            create_sprof_cclname.setError(null);
        }

        if (seekerPayment.getName().isEmpty()) {
            create_sprof_ccname.setError("Enter a valid card company name");
            valid = false;
        } else {
            create_sprof_ccname.setError(null);
        }

        int length = create_sprof_ccnumber.getText().toString().length();
        if (create_sprof_ccnumber.getText().toString().isEmpty() || length < 10 || length > 19) {
            create_sprof_ccnumber.setError("Enter a valid card number");
            valid = false;
        } else {
            create_sprof_ccnumber.setError(null);
        }

        if (seekerPayment.getExpMonth().isEmpty()) {
            create_sprof_ccexpmonth.setError("Enter a valid card expiration month");
            valid = false;
        } else {
            create_sprof_ccexpmonth.setError(null);
        }

        if (seekerPayment.getExpYear().isEmpty() || seekerPayment.getExpYear().length() != 4) {
            create_sprof_ccexpyear.setError("Enter a valid card expiration year");
            valid = false;
        } else {
            create_sprof_ccexpyear.setError(null);
        }

        //BILLING ADDRESS CHECKS
        if (billingAddress.getAddress().isEmpty()) {
            create_spof_address.setError("Enter a valid billing address");
            valid = false;
        } else {
            create_spof_address.setError(null);
        }

        if (billingAddress.getCity().isEmpty()) {
            create_spof_city.setError("Enter a valid billing city");
            valid = false;
        } else {
            create_spof_city.setError(null);
        }

        if (billingAddress.getState().isEmpty() || billingAddress.getState().length() != 2) {
            create_spof_state.setError("Enter a valid billing state (Ex: CA)");
            valid = false;
        } else {
            create_spof_state.setError(null);
        }

        if (billingAddress.getZipCode().isEmpty() || billingAddress.getZipCode().length() != 5) {
            create_spof_zipcode.setError("Enter a valid 5 digit billing zip code");
            valid = false;
        } else {
            create_spof_zipcode.setError(null);
        }

        if (billingAddress.getCountry().isEmpty() || !billingAddress.getCountry().equals("USA")) {
            create_spof_country.setError("Enter a valid billing country (USA)");
            valid = false;
        } else {
            create_spof_country.setError(null);
        }

        //VEHICLE CHECKS
        if (vehicle.getMake().isEmpty()) {
            create_spof_make.setError("Enter a valid vehicle make");
            valid = false;
        } else {
            create_spof_make.setError(null);
        }

        if (vehicle.getModel().isEmpty()) {
            create_spof_model.setError("Enter a valid vehicle model");
            valid = false;
        } else {
            create_spof_model.setError(null);
        }

        if (vehicle.getColor().isEmpty()) {
            create_spof_color.setError("Enter a valid vehicle color");
            valid = false;
        } else {
            create_spof_color.setError(null);
        }

        if (vehicle.getYear().isEmpty() || vehicle.getYear().length() < 4) {
            create_spof_year.setError("Enter a valid vehicle year");
            valid = false;
        } else {
            create_spof_year.setError(null);
        }

        if (vehicle.getLicensePlate().isEmpty()) {
            create_spof_licenseplate.setError("Enter a valid vehicle license plate");
            valid = false;
        } else {
            create_spof_licenseplate.setError(null);
        }

        return valid;
    }

    public void createSeekerProfileProcess(SeekerPayment seekerPayment,
                                           BillingAddress billingAddress, Vehicle vehicle) {
        final ProgressDialog progressDialog = new ProgressDialog(rootView.getContext(), R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Adding Seeker Role...");
        progressDialog.show();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RequestInterface requestInterface = retrofit.create(RequestInterface.class);

        ServerRequest request = new ServerRequest();
        request.setOperation(Constants.ADD_SEEKER_ROLE_OPERATION);
        request.setUser(user);
        request.setSeekerPayment(seekerPayment);
        request.setBillingAddress(billingAddress);
        request.setVehicle(vehicle);
        Call<ServerResponse> response = requestInterface.operation(request);

        response.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, retrofit2.Response<ServerResponse> response) {
                ServerResponse resp = response.body();

                if (resp.getResult().equals(Constants.SUCCESS)) {
                    goToSeekerUserhomeScreen();
                }

                progressDialog.dismiss();
                Snackbar.make(create_sprof_scrollview, resp.getMessage(), Snackbar.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                progressDialog.dismiss();
                Snackbar.make(create_sprof_scrollview, t.getLocalizedMessage(), Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void goToSeekerUserhomeScreen() {
        user.setIsSeeker("true");
        user.setDefaultRole("Seeker");
        Intent intent = new Intent(myContext, SeekerHomeActivity.class);
        intent.putExtra("user", user);
        startActivity(intent);
    }
}

