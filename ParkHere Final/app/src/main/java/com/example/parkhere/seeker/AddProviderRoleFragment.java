package com.example.parkhere.seeker;

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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;

import com.example.parkhere.R;
import com.example.parkhere.objects.ProviderPayment;
import com.example.parkhere.objects.User;
import com.example.parkhere.provider.ProviderHomeActivity;
import com.example.parkhere.server.Constants;
import com.example.parkhere.server.RequestInterface;
import com.example.parkhere.server.ServerRequest;
import com.example.parkhere.server.ServerResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AddProviderRoleFragment extends Fragment {
    private User user;
    private static final String USER_KEY = "user";
    private View rootView;
    private ScrollView create_pprof_scrollview;
    private EditText create_pprof_bank, create_pprof_accountnumber, create_pprof_routingnumber;
    private RadioGroup create_pprof_radio_group;
    private Button create_pprof_button;
    private FragmentActivity myContext;

    public static AddProviderRoleFragment newInstance(User user) {
        AddProviderRoleFragment fragment = new AddProviderRoleFragment();
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

        rootView = inflater.inflate(R.layout.fragment_add_provider_role, container, false);

        create_pprof_scrollview = (ScrollView) rootView.findViewById(R.id.add_pprof_scrollview);
        create_pprof_bank = (EditText) rootView.findViewById(R.id.add_pprof_bank);
        create_pprof_accountnumber = (EditText) rootView.findViewById(R.id.add_pprof_accountnumber);
        create_pprof_routingnumber = (EditText) rootView.findViewById(R.id.add_pprof_routingnumber);
        create_pprof_radio_group = (RadioGroup) rootView.findViewById(R.id.add_pprof_radio_group);
        create_pprof_button = (Button) rootView.findViewById(R.id.add_pprof_button);

        create_pprof_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                createProviderProfile();
            }
        });

        return rootView;
    }

    public void createProviderProfile() {
        String bank = create_pprof_bank.getText().toString();
        String accountNumber = create_pprof_accountnumber.getText().toString();
        String routingNumber = create_pprof_routingnumber.getText().toString();

        int selectedID = create_pprof_radio_group.getCheckedRadioButtonId();
        RadioButton radioButton = (RadioButton) rootView.findViewById(selectedID);
        String accountType = radioButton.getText().toString();

        if (!validate(bank, accountNumber, routingNumber)) {
            onAddProviderRoleFailed();
            return;
        } else {
            createProviderProfileProcess(bank, accountNumber.substring(accountNumber.length() - 4, accountNumber.length()),
                    routingNumber.substring(routingNumber.length() - 4, routingNumber.length()), accountType);
        }
    }

    public void onAddProviderRoleFailed() {
        Snackbar.make(create_pprof_scrollview, "Invalid provider profile creation parameters", Snackbar.LENGTH_LONG).show();
    }

    public boolean validate(String bank, String accountNumber, String routingNumber) {
        boolean valid = true;

        if (bank.isEmpty()) {
            create_pprof_bank.setError("Enter a valid bank name");
            valid = false;
        } else {
            create_pprof_bank.setError(null);
        }

        if (accountNumber.isEmpty() || accountNumber.length() < 10 || accountNumber.length() > 17) {
            create_pprof_accountnumber.setError("Enter a valid account number");
            valid = false;
        } else {
            create_pprof_accountnumber.setError(null);
        }

        if (routingNumber.isEmpty() || routingNumber.length() != 9) {
            create_pprof_routingnumber.setError("Enter a valid routing number");
            valid = false;
        } else {
            create_pprof_routingnumber.setError(null);
        }

        return valid;
    }

    public void createProviderProfileProcess(String bank,
                                             String accountNumber, String routingNumber, String accountType) {
        final ProgressDialog progressDialog = new ProgressDialog(rootView.getContext(), R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Adding Provider Role...");
        progressDialog.show();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RequestInterface requestInterface = retrofit.create(RequestInterface.class);

        ProviderPayment payment = new ProviderPayment();
        payment.setBank(bank);
        payment.setAccountNumber(accountNumber);
        payment.setRoutingNumber(routingNumber);
        payment.setAccountType(accountType);

        ServerRequest request = new ServerRequest();
        request.setOperation(Constants.ADD_PROVIDER_ROLE_OPERATION);
        request.setUser(user);
        request.setProviderPayment(payment);
        Call<ServerResponse> response = requestInterface.operation(request);

        response.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, retrofit2.Response<ServerResponse> response) {
                ServerResponse resp = response.body();

                if(resp.getResult().equals(Constants.SUCCESS)){
                    goToProviderUserhomeScreen();
                }

                progressDialog.dismiss();
                Snackbar.make(create_pprof_scrollview, resp.getMessage(), Snackbar.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                progressDialog.dismiss();
                Snackbar.make(create_pprof_scrollview, t.getLocalizedMessage(), Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void goToProviderUserhomeScreen() {
        Intent intent = new Intent(myContext, ProviderHomeActivity.class);
        intent.putExtra("user", user);
        startActivity(intent);
    }
}


