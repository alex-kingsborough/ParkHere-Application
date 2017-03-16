package com.example.parkhere.main;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.parkhere.R;
import com.example.parkhere.objects.User;
import com.example.parkhere.server.Constants;
import com.example.parkhere.server.RequestInterface;
import com.example.parkhere.server.ServerRequest;
import com.example.parkhere.server.ServerResponse;

import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RegisterActivity extends AppCompatActivity {
    private ScrollView register_scroll_view;
    private EditText register_email, register_password, register_reenter_password;
    private RadioGroup register_radio_group;
    private Button register_button;
    private TextView register_link_login;
    private static final String CHAR_LIST = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
    private static final int RANDOM_STRING_LENGTH = 10;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        register_scroll_view = (ScrollView) findViewById(R.id.register_scrollview);
        register_email = (EditText) findViewById(R.id.register_email);
        register_password = (EditText) findViewById(R.id.register_password);
        register_reenter_password = (EditText) findViewById(R.id.register_reenter_password);
        register_radio_group = (RadioGroup) findViewById(R.id.register_radio_group);
        register_button = (Button) findViewById(R.id.register_button);
        register_link_login = (TextView) findViewById(R.id.register_link_login);
        register_link_login.setPaintFlags(register_link_login.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        register_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                register();
            }
        });

        register_link_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                startActivity(intent);
            }
        });
    }

    public void register() {
        String email = register_email.getText().toString();
        String password = register_password.getText().toString();
        String reenter_password = register_reenter_password.getText().toString();

        int selectedID = register_radio_group.getCheckedRadioButtonId();
        RadioButton radioButton = (RadioButton) findViewById(selectedID);
        String primary_role = radioButton.getText().toString();

        if (!validate(email, password, reenter_password)) {
            onRegisterFailed();
            return;
        }

        registerProcess(email, password, primary_role);
    }

    public void onRegisterFailed() {
        Snackbar.make(register_scroll_view, "Invalid registration parameters", Snackbar.LENGTH_LONG).show();
    }

    public boolean validate(String email, String password, String reenter_password) {
        boolean valid = true;

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            register_email.setError("Enter a valid email address");
            valid = false;
        } else {
            register_email.setError(null);
        }

        Pattern p = Pattern.compile("[^A-Za-z0-9]");
        Matcher m = p.matcher(password);
        boolean b = m.find();
        if (password.isEmpty() || password.length() < 10) {
            register_password.setError("At least 10 characters in length");
            valid = false;
        } else if (!b) {
            register_password.setError("At least one special character");
            valid = false;
        } else {
            register_password.setError(null);
        }

        if (!(reenter_password.equals(password))) {
            register_reenter_password.setError("Passwords do not match");
            valid = false;
        } else {
            register_reenter_password.setError(null);
        }

        return valid;
    }

    private void registerProcess(String email, String password, String primaryRole){
        final ProgressDialog progressDialog = new ProgressDialog(RegisterActivity.this, R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creating User...");
        progressDialog.show();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RequestInterface requestInterface = retrofit.create(RequestInterface.class);

        user = new User();
        user.setEmail(email);
        user.setPassword(password);
        user.setDefaultRole(primaryRole);
        user.setValidationCode(generateRandomString());
        user.setIsValidated("false");

        if (primaryRole.equals("Seeker")){
            user.setIsProvider("false");
            user.setIsSeeker("true");
        } else {
            user.setIsProvider("true");
            user.setIsSeeker("false");
        }

        ServerRequest request = new ServerRequest();
        request.setOperation(Constants.REGISTER_OPERATION);
        request.setUser(user);
        Call<ServerResponse> response = requestInterface.operation(request);

        response.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, retrofit2.Response<ServerResponse> response) {
                ServerResponse resp = response.body();

                if(resp.getResult().equals(Constants.SUCCESS)){
                    user.setId(resp.getUser().getId());
                    user.setCreatedAt(resp.getUser().getCreatedAt());
                    try {
                        new EmailTask().execute();
                    } catch (Exception e) {
                        Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                    goToValidationScreen();
                }

                progressDialog.dismiss();
                Snackbar.make(register_scroll_view, resp.getMessage(), Snackbar.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                register_button.setEnabled(true);
                progressDialog.dismiss();
                Snackbar.make(register_scroll_view, t.getLocalizedMessage(), Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void goToValidationScreen() {
        Intent intent = new Intent(getApplicationContext(), ValidationActivity.class);
        intent.putExtra("user", user);
        startActivity(intent);
    }

    //GENERATE VALIDATION PASSPHRASE
    public String generateRandomString(){
        StringBuffer randStr = new StringBuffer();
        for(int i = 0; i < RANDOM_STRING_LENGTH; i++){
            int number = getRandomNumber();
            char ch = CHAR_LIST.charAt(number);
            randStr.append(ch);
        }
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

    private class EmailTask extends AsyncTask<Void, Void, String>
    {
        @Override
        protected String doInBackground(Void... params)
        {
            ValidationPassphraseEmailSender mailer = new ValidationPassphraseEmailSender();
            boolean emailResult = mailer.sendEmailToUser(user.getEmail(), user.getValidationCode());
            if (emailResult == false){
                return "Validation email sending failure";
            } else {
                return "Validation email successfully sent to " + user.getEmail();
            }
        }

        @Override
        protected void onPostExecute(String result)
        {
            Toast.makeText(RegisterActivity.this, result, Toast.LENGTH_LONG).show();
        }

        @Override
        protected void onPreExecute() {}

        @Override
        protected void onProgressUpdate(Void... values) {}
    }
}
