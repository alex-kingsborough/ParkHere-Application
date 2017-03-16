package com.example.parkhere.main;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.support.design.widget.Snackbar;
import android.app.AlertDialog;

import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.parkhere.R;
import com.example.parkhere.objects.User;
import com.example.parkhere.provider.ProviderHomeActivity;
import com.example.parkhere.seeker.SeekerHomeActivity;
import com.example.parkhere.server.Constants;
import com.example.parkhere.server.RequestInterface;
import com.example.parkhere.server.ServerRequest;
import com.example.parkhere.server.ServerResponse;

import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HomeActivity extends AppCompatActivity {
    private ScrollView login_scroll_view;
    private EditText login_email, login_password;
    private Button login_button;
    private TextView login_forgot_pw_link, login_link_signup;
    private ImageView login_link_about;
    private static final String CHAR_LIST = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
    private static final int RANDOM_STRING_LENGTH = 9;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        login_scroll_view = (ScrollView) findViewById(R.id.login_scrollview);
        login_email = (EditText) findViewById(R.id.login_email);
        login_password = (EditText) findViewById(R.id.login_password);
        login_button = (Button) findViewById(R.id.login_button);
        login_forgot_pw_link = (TextView) findViewById(R.id.login_forgot_pw_link);
        login_forgot_pw_link.setPaintFlags(login_forgot_pw_link.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        login_link_signup = (TextView) findViewById(R.id.login_link_signup);
        login_link_signup.setPaintFlags(login_link_signup.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        login_link_about = (ImageView) findViewById(R.id.login_link_about);

        login_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                login();
            }
        });

        login_forgot_pw_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater li = LayoutInflater.from(HomeActivity.this);
                View customView = li.inflate(R.layout.popup_forgot_password, null);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(HomeActivity.this);
                alertDialogBuilder.setView(customView);
                final AlertDialog alertDialog = alertDialogBuilder.create();

                ImageButton closeButton = (ImageButton) customView.findViewById(R.id.popup_forgot_password_close);
                closeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog.dismiss();
                    }
                });

                Button resetButton = (Button) customView.findViewById(R.id.popup_forgot_password_button);
                final EditText popup_forgot_password_email = (EditText) customView.findViewById(R.id.popup_forgot_password_email);
                resetButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String email = popup_forgot_password_email.getText().toString();

                        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                            popup_forgot_password_email.setError("Enter a valid email address");
                            return;
                        } else {
                            Retrofit retrofit = new Retrofit.Builder()
                                    .baseUrl(Constants.BASE_URL)
                                    .addConverterFactory(GsonConverterFactory.create())
                                    .build();

                            RequestInterface requestInterface = retrofit.create(RequestInterface.class);

                            user = new User();
                            user.setEmail(email);
                            user.setPassword(generateRandomString());

                            ServerRequest request = new ServerRequest();
                            request.setOperation(Constants.FORGOT_PASSWORD_OPERATION);
                            request.setUser(user);
                            Call<ServerResponse> response = requestInterface.operation(request);

                            response.enqueue(new Callback<ServerResponse>() {
                                @Override
                                public void onResponse(Call<ServerResponse> call, retrofit2.Response<ServerResponse> response) {
                                    ServerResponse resp = response.body();

                                    if(resp.getResult().equals(Constants.SUCCESS)){
                                        try {
                                            new EmailTask().execute();
                                        } catch (Exception e) {
                                            Toast.makeText(HomeActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                        }
                                    }
                                    Snackbar.make(login_scroll_view, resp.getMessage(), Snackbar.LENGTH_LONG).show();
                                }

                                @Override
                                public void onFailure(Call<ServerResponse> call, Throwable t) {
                                    Snackbar.make(login_scroll_view, t.getLocalizedMessage(), Snackbar.LENGTH_LONG).show();
                                }
                            });
                        }
                    }
                });

                alertDialog.show();
            }
        });

        login_link_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater li = LayoutInflater.from(HomeActivity.this);
                View customView = li.inflate(R.layout.popup_terms_of_service, null);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(HomeActivity.this);
                alertDialogBuilder.setView(customView);
                final AlertDialog alertDialog = alertDialogBuilder.create();

                ImageButton closeButton = (ImageButton) customView.findViewById(R.id.popup_tos_close);
                closeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog.dismiss();
                    }
                });

                Button agreeButton = (Button) customView.findViewById(R.id.popup_tos_agree_button);
                agreeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                        startActivity(intent);
                    }
                });

                alertDialog.show();
            }
        });

        login_link_about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater li = LayoutInflater.from(HomeActivity.this);
                View customView = li.inflate(R.layout.popup_about, null);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(HomeActivity.this);
                alertDialogBuilder.setView(customView);
                final AlertDialog alertDialog = alertDialogBuilder.create();

                ImageButton closeButton = (ImageButton) customView.findViewById(R.id.popup_about_close);
                closeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog.dismiss();
                    }
                });

                alertDialog.show();
            }
        });
    }

    public void login() {
        String email = login_email.getText().toString();
        String password = login_password.getText().toString();

        if (!validate(email, password)) {
            onLoginFailed();
            return;
        }

        loginProcess(email, password);
    }

    public void onLoginFailed() {
        Snackbar.make(login_scroll_view, "Invalid login parameters", Snackbar.LENGTH_LONG).show();
    }

    public boolean validate(String email, String password) {
        boolean valid = true;

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            login_email.setError("Enter a valid email address");
            valid = false;
        } else {
            login_email.setError(null);
        }

        if (password.isEmpty() || password.length() < 10) {
            login_password.setError("Enter a valid password");
            valid = false;
        } else {
            login_password.setError(null);
        }

        return valid;
    }

    private void loginProcess(String email,String password){
        final ProgressDialog progressDialog = new ProgressDialog(HomeActivity.this, R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RequestInterface requestInterface = retrofit.create(RequestInterface.class);

        user = new User();
        user.setEmail(email);
        user.setPassword(password);
        ServerRequest request = new ServerRequest();
        request.setOperation(Constants.LOGIN_OPERATION);
        request.setUser(user);
        Call<ServerResponse> response = requestInterface.operation(request);

        response.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, retrofit2.Response<ServerResponse> response) {
                ServerResponse resp = response.body();

                if(resp.getResult().equals(Constants.SUCCESS)){
                    user.setId(resp.getUser().getId());
                    user.setFirstName(resp.getUser().getFirstName());
                    user.setLastName(resp.getUser().getLastName());
                    user.setPhoneNumber(resp.getUser().getPhoneNumber());
                    user.setIsProvider(resp.getUser().getIsProvider());
                    user.setIsSeeker(resp.getUser().getIsSeeker());
                    user.setDefaultRole(resp.getUser().getDefaultRole());
                    user.setProfilePicURL(resp.getUser().getProfilePicURL());
                    user.setValidationCode(resp.getUser().getValidationCode());
                    user.setIsValidated(resp.getUser().getIsValidated());
                    user.setCreatedAt(resp.getUser().getCreatedAt());

                    String hasCreatedProfile;
                    if (user.getFirstName() == null){
                        hasCreatedProfile = "false";
                    } else {
                        hasCreatedProfile = "true";
                    }

                    goToNextScreen(user.getIsValidated(), hasCreatedProfile, user.getDefaultRole());
                    onLoginPassed();
                } else {
                    Snackbar.make(login_scroll_view, resp.getMessage(), Snackbar.LENGTH_LONG).show();
                    onLoginInvalid();
                }
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                progressDialog.dismiss();
                Snackbar.make(login_scroll_view, t.getLocalizedMessage(), Snackbar.LENGTH_LONG).show();
            }
        });
    }

    public void onLoginPassed() {
        Snackbar.make(login_scroll_view, "Login successful", Snackbar.LENGTH_LONG).show();
    }

    public void onLoginInvalid() {
        Snackbar.make(login_scroll_view, "Invalid Login Credentials", Snackbar.LENGTH_LONG).show();
    }

    private void goToNextScreen(String isValidated, String hasCreatedProfile, String defaultRole){
        if (isValidated.equals("false")) {
            Intent intent = new Intent(getApplicationContext(), ValidationActivity.class);
            intent.putExtra("user", user);
            startActivity(intent);
        } else if (hasCreatedProfile.equals("false")) {
            if (defaultRole.equals("Provider")) {
                Intent intent = new Intent(getApplicationContext(), CreateProviderProfileActivity.class);
                intent.putExtra("user", user);
                startActivity(intent);
            } else {
                Intent intent = new Intent(getApplicationContext(), CreateSeekerProfileActivity.class);
                intent.putExtra("user", user);
                startActivity(intent);
            }
        } else {
            if (defaultRole.equals("Provider")) {
                Intent intent = new Intent(getApplicationContext(), ProviderHomeActivity.class);
                intent.putExtra("user", user);
                startActivity(intent);
            } else {
                Intent intent = new Intent(getApplicationContext(), SeekerHomeActivity.class);
                intent.putExtra("user", user);
                startActivity(intent);
            }
        }
    }

    //GENERATE TEMPORARY PASSWORD
    public String generateRandomString(){
        StringBuffer randStr = new StringBuffer();
        for(int i = 0; i < RANDOM_STRING_LENGTH; i++){
            int number = getRandomNumber();
            char ch = CHAR_LIST.charAt(number);
            randStr.append(ch);
        }
        randStr.append('!');
        return randStr.toString();
    }

    private int getRandomNumber() {
        int randomInt = 0;
        Random randomGenerator = new Random();
        randomInt = randomGenerator.nextInt(CHAR_LIST.length());
        if (randomInt - 1 == -1) {
            return randomInt;
        } else {
            return randomInt - 1;
        }
    }

    private class EmailTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params)
        {
            ForgotPasswordEmailSender mailer = new ForgotPasswordEmailSender();
            boolean emailResult = mailer.sendEmailToUser(user.getEmail(), user.getPassword());
            if (emailResult == false){
                return "Temporary password reset and email sending failure";
            } else {
                return "Password temporarily reset and email successfully sent to " + user.getEmail();
            }
        }

        @Override
        protected void onPostExecute(String result)
        {
            Toast.makeText(HomeActivity.this, result, Toast.LENGTH_LONG).show();
        }

        @Override
        protected void onPreExecute() {}

        @Override
        protected void onProgressUpdate(Void... values) {}
    }
}
