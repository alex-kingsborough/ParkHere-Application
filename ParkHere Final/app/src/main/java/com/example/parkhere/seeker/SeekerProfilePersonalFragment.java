package com.example.parkhere.seeker;

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

public class SeekerProfilePersonalFragment extends Fragment {
    private User user;
    private static final String USER_KEY = "user";
    private View rootView;
    private EditText sprof_fname, sprof_lname, sprof_phonenumber;
    private Button sprof_personal_button;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        user = (User) getArguments().getSerializable(USER_KEY);

        rootView = inflater.inflate(R.layout.fragment_seeker_profile_personal, container, false);

        sprof_fname = (EditText) rootView.findViewById(R.id.sprof_fname);
        sprof_lname = (EditText) rootView.findViewById(R.id.sprof_lname);
        sprof_phonenumber = (EditText) rootView.findViewById(R.id.sprof_phonenumber);
        sprof_personal_button = (Button) rootView.findViewById(R.id.sprof_personal_button);

        sprof_fname.setText(user.getFirstName());
        sprof_lname.setText(user.getLastName());
        sprof_phonenumber.setText(user.getPhoneNumber());

        sprof_personal_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                updateSeekerPersonal();
            }
        });

        return rootView;
    }

    public void updateSeekerPersonal() {
        String fname = sprof_fname.getText().toString();
        String lname = sprof_lname.getText().toString();
        String phoneNumber = sprof_phonenumber.getText().toString();

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
            sprof_fname.setError("Enter your first name");
            valid = false;
        } else {
            sprof_fname.setError(null);
        }

        if (lname.isEmpty()) {
            sprof_lname.setError("Enter your last name");
            valid = false;
        } else {
            sprof_lname.setError(null);
        }

        if (phoneNumber.isEmpty() || phoneNumber.length() < 10) {
            sprof_phonenumber.setError("Enter a valid phone number");
            valid = false;
        } else {
            sprof_phonenumber.setError(null);
        }

        return valid;
    }
}

