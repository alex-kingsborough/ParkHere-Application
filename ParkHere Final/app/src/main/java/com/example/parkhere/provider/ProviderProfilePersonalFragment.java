package com.example.parkhere.provider;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.parkhere.R;
import com.example.parkhere.objects.User;
import com.example.parkhere.server.Constants;
import com.example.parkhere.server.RequestInterface;
import com.example.parkhere.server.ServerRequest;
import com.example.parkhere.server.ServerResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ProviderProfilePersonalFragment extends Fragment {
    private User user;
    private static final String USER_KEY = "user";
    private View rootView;
    private EditText pprof_fname, pprof_lname, pprof_phonenumber;
    private Button pprof_personal_button;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        user = (User) getArguments().getSerializable(USER_KEY);

        rootView = inflater.inflate(R.layout.fragment_provider_profile_personal, container, false);

        pprof_fname = (EditText) rootView.findViewById(R.id.pprof_fname);
        pprof_lname = (EditText) rootView.findViewById(R.id.pprof_lname);
        pprof_phonenumber = (EditText) rootView.findViewById(R.id.pprof_phonenumber);
        pprof_personal_button = (Button) rootView.findViewById(R.id.pprof_personal_button);

        pprof_fname.setText(user.getFirstName());
        pprof_lname.setText(user.getLastName());
        pprof_phonenumber.setText(user.getPhoneNumber());

        pprof_personal_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                updateProviderPersonal();
            }
        });

        return rootView;
    }

    public void updateProviderPersonal() {
        String fname = pprof_fname.getText().toString();
        String lname = pprof_lname.getText().toString();
        String phoneNumber = pprof_phonenumber.getText().toString();

        if (!validatePersonal(fname, lname, phoneNumber)) {
            Snackbar.make(rootView, "Invalid update personal information parameters", Snackbar.LENGTH_LONG).show();
            return;
        }

        user.setFirstName(fname);
        user.setLastName(lname);
        user.setPhoneNumber(phoneNumber);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RequestInterface requestInterface = retrofit.create(RequestInterface.class);

        ServerRequest request = new ServerRequest();
        request.setOperation(Constants.UPDATE_PROFILE_PERSONAL_OPERATION);
        request.setUser(user);
        Call<ServerResponse> response = requestInterface.operation(request);

        response.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, retrofit2.Response<ServerResponse> response) {
                ServerResponse resp = response.body();
                Snackbar.make(rootView, resp.getMessage(), Snackbar.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                Snackbar.make(rootView, t.getLocalizedMessage(), Snackbar.LENGTH_LONG).show();
            }
        });
    }

    public boolean validatePersonal(String fname, String lname, String phoneNumber) {
        boolean valid = true;

        if (fname.isEmpty()) {
            pprof_fname.setError("Enter your first name");
            valid = false;
        } else {
            pprof_fname.setError(null);
        }

        if (lname.isEmpty()) {
            pprof_lname.setError("Enter your last name");
            valid = false;
        } else {
            pprof_lname.setError(null);
        }

        if (phoneNumber.isEmpty() || phoneNumber.length() < 10) {
            pprof_phonenumber.setError("Enter a valid phone number");
            valid = false;
        } else {
            pprof_phonenumber.setError(null);
        }

        return valid;
    }
}

