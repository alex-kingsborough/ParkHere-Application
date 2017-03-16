package com.example.parkhere.main;

import android.app.ProgressDialog;
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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ChangePasswordFragment extends Fragment {
    private User user;
    private static final String USER_KEY = "user";
    private EditText change_pw_curr_password, change_pw_new_password, change_pw_reenter_password;
    private Button change_pw_button;
    View rootView;

    public static ChangePasswordFragment newInstance(User user) {
        ChangePasswordFragment fragment = new ChangePasswordFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(USER_KEY, user);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        user = (User) getArguments().getSerializable(USER_KEY);

        rootView = inflater.inflate(R.layout.fragment_change_password, container, false);

        change_pw_curr_password = (EditText) rootView.findViewById(R.id.change_pw_curr_password);
        change_pw_new_password = (EditText) rootView.findViewById(R.id.change_pw_new_password);
        change_pw_reenter_password = (EditText) rootView.findViewById(R.id.change_pw_reenter_password);
        change_pw_button = (Button) rootView.findViewById(R.id.change_pw_button);

        change_pw_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String curr_password = change_pw_curr_password.getText().toString();
                String password = change_pw_new_password.getText().toString();
                String reenter_password = change_pw_reenter_password.getText().toString();

                if (!validate(curr_password, password, reenter_password)) {
                    onChangePasswordFailed();
                } else {
                    user.setPassword(password);
                    changePasswordProcess(user.getEmail(), password);
                }
            }
        });

        return rootView;
    }

    public void onChangePasswordFailed() {
        Snackbar.make(rootView, "Invalid change password parameters", Snackbar.LENGTH_LONG).show();
    }

    public void changeSuccessful() {
        Snackbar.make(rootView, "Password successfully reset", Snackbar.LENGTH_LONG).show();
    }

    public boolean validate(String curr_password, String password, String reenter_password) {
        boolean valid = true;

        if (curr_password.isEmpty() || !(curr_password.equals(user.getPassword()))) {
            change_pw_curr_password.setError("Enter your valid current password");
            valid = false;
        } else {
            change_pw_curr_password.setError(null);
        }

        Pattern p = Pattern.compile("[^A-Za-z0-9]");
        Matcher m = p.matcher(password);
        boolean b = m.find();
        if (password.equals(user.getPassword())) {
            change_pw_new_password.setError("New password cannot be the same as your current password");
            valid = false;
        } else if (password.isEmpty() || password.length() < 10) {
            change_pw_new_password.setError("At least 10 characters in length");
            valid = false;
        } else if (!b) {
            change_pw_new_password.setError("At least one special character");
            valid = false;
        } else {
            change_pw_new_password.setError(null);
        }

        if (!(reenter_password.equals(password))) {
            change_pw_reenter_password.setError("Passwords do not match");
            valid = false;
        } else {
            change_pw_reenter_password.setError(null);
        }

        return valid;
    }

    private void changePasswordProcess(String email, String password){
        final ProgressDialog progressDialog = new ProgressDialog(rootView.getContext(), R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Changing Password...");
        progressDialog.show();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RequestInterface requestInterface = retrofit.create(RequestInterface.class);

        ServerRequest request = new ServerRequest();
        request.setOperation(Constants.CHANGE_PASSWORD_OPERATION);
        request.setUser(user);
        Call<ServerResponse> response = requestInterface.operation(request);

        response.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, retrofit2.Response<ServerResponse> response) {
                ServerResponse resp = response.body();

                if(resp.getResult().equals(Constants.SUCCESS)){
                    changeSuccessful();
                }
                else {
                    Snackbar.make(rootView, resp.getMessage(), Snackbar.LENGTH_LONG).show();
                }

                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                progressDialog.dismiss();
                Snackbar.make(rootView, t.getLocalizedMessage(), Snackbar.LENGTH_LONG).show();
            }
        });
    }
}

