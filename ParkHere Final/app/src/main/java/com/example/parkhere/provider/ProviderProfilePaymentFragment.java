package com.example.parkhere.provider;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.example.parkhere.R;
import com.example.parkhere.objects.ProviderPayment;
import com.example.parkhere.objects.User;
import com.example.parkhere.server.Constants;
import com.example.parkhere.server.RequestInterface;
import com.example.parkhere.server.ServerRequest;
import com.example.parkhere.server.ServerResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ProviderProfilePaymentFragment extends Fragment {
    private User user;
    private ProviderPayment payment;
    private static final String USER_KEY = "user";
    private static final String PROVIDER_PAYMENT_KEY = "providerPayment";
    private View rootView;
    private EditText pprof_bank, pprof_accountnumber, pprof_routingnumber;
    private RadioGroup pprof_radio_group;
    private RadioButton pprof_radio_checking, pprof_radio_savings;
    private Button pprof_payment_button;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        user = (User) getArguments().getSerializable(USER_KEY);
        payment = (ProviderPayment) getArguments().getSerializable(PROVIDER_PAYMENT_KEY);

        rootView = inflater.inflate(R.layout.fragment_provider_profile_payment, container, false);

        pprof_bank = (EditText) rootView.findViewById(R.id.pprof_bank);
        pprof_radio_group = (RadioGroup) rootView.findViewById(R.id.pprof_radio_group);
        pprof_radio_checking = (RadioButton) rootView.findViewById(R.id.pprof_radio_checking);
        pprof_radio_savings = (RadioButton) rootView.findViewById(R.id.pprof_radio_savings);
        pprof_accountnumber = (EditText) rootView.findViewById(R.id.pprof_accountnumber);
        pprof_routingnumber = (EditText) rootView.findViewById(R.id.pprof_routingnumber);
        pprof_payment_button = (Button) rootView.findViewById(R.id.pprof_payment_button);

        pprof_bank.setText(payment.getBank());
        if (payment.getAccountType().equals("Checking/Prepaid Card")) {
            pprof_radio_checking.setChecked(true);
        } else {
            pprof_radio_savings.setChecked(true);
        }
        pprof_accountnumber.setText("XXXX..." + payment.getAccountNumber());
        pprof_routingnumber.setText("XXXX..." + payment.getRoutingNumber());

        pprof_payment_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                updateProviderPayment();
            }
        });

        return rootView;
    }

    public void updateProviderPayment() {
        String bank = pprof_bank.getText().toString();
        String accountNumber = pprof_accountnumber.getText().toString();
        String routingNumber = pprof_routingnumber.getText().toString();

        int selectedID = pprof_radio_group.getCheckedRadioButtonId();
        RadioButton radioButton = (RadioButton) rootView.findViewById(selectedID);
        String accountType = radioButton.getText().toString();

        if (!validatePayment(bank, accountNumber, routingNumber)) {
            Snackbar.make(rootView, "Invalid update payment information parameters", Snackbar.LENGTH_LONG).show();
            return;
        }

        final ProviderPayment p = new ProviderPayment();
        p.setBank(bank);
        p.setAccountType(accountType);
        p.setAccountNumber(accountNumber.substring(accountNumber.length()-4, accountNumber.length()));
        p.setRoutingNumber(routingNumber.substring(routingNumber.length()-4, routingNumber.length()));

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RequestInterface requestInterface = retrofit.create(RequestInterface.class);

        ServerRequest request = new ServerRequest();
        request.setOperation(Constants.UPDATE_PROVIDER_PAYMENT_OPERATION);
        request.setUser(user);
        request.setProviderPayment(p);
        Call<ServerResponse> response = requestInterface.operation(request);

        response.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, retrofit2.Response<ServerResponse> response) {
                ServerResponse resp = response.body();

                Snackbar.make(rootView, resp.getMessage(), Snackbar.LENGTH_LONG).show();

                if(resp.getResult().equals(Constants.SUCCESS)){
                    pprof_accountnumber.setText("XXXX..." + p.getAccountNumber());
                    pprof_routingnumber.setText("XXXX..." + p.getRoutingNumber());
                }
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                Snackbar.make(rootView, t.getLocalizedMessage(), Snackbar.LENGTH_LONG).show();
            }
        });
    }

    public boolean validatePayment(String bank, String accountNumber, String routingNumber) {
        boolean valid = true;

        if (bank.isEmpty()) {
            pprof_bank.setError("Enter a valid bank name");
            valid = false;
        } else {
            pprof_bank.setError(null);
        }

        if (accountNumber.isEmpty() || accountNumber.length() < 10 || accountNumber.length() > 17) {
            pprof_accountnumber.setError("Enter a valid account number");
            valid = false;
        } else {
            pprof_accountnumber.setError(null);
        }

        if (routingNumber.isEmpty() || routingNumber.length() != 9) {
            pprof_routingnumber.setError("Enter a valid routing number");
            valid = false;
        } else {
            pprof_routingnumber.setError(null);
        }

        return valid;
    }
}

