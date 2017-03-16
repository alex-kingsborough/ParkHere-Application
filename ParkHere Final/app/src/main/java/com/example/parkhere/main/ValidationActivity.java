package com.example.parkhere.main;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.parkhere.R;
import com.example.parkhere.objects.User;
import com.example.parkhere.server.Constants;
import com.example.parkhere.server.RequestInterface;
import com.example.parkhere.server.ServerRequest;
import com.example.parkhere.server.ServerResponse;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ValidationActivity extends AppCompatActivity {
    private ScrollView validation_scroll_view;
    private EditText validation_code;
    private TextView validation_code_textview, validation_resend_code_link;
    private CircleImageView validation_gov_id_image;
    private Button validation_button;
    private User user;
    private int PICK_IMAGE_REQUEST = 1;
    private Bitmap bitmap;
    private boolean uploadedImage = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_validation);
        user = (User) getIntent().getSerializableExtra("user");

        validation_scroll_view = (ScrollView) findViewById(R.id.validation_scrollview);
        validation_code_textview = (TextView) findViewById(R.id.validation_code_textview);
        validation_code_textview.setText("An email was sent to " + user.getEmail() + " with the validation passphrase necessary to complete your account creation.");
        validation_code = (EditText) findViewById(R.id.validation_code);
        validation_resend_code_link = (TextView) findViewById(R.id.validation_resend_code_link);
        validation_resend_code_link.setPaintFlags(validation_resend_code_link.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        validation_gov_id_image = (CircleImageView) findViewById(R.id.validation_gov_id_image);
        validation_button = (Button) findViewById(R.id.validation_button);

        validation_resend_code_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(validation_scroll_view, "Resending validation code...", Snackbar.LENGTH_LONG).show();
                try {
                    new EmailTask().execute();
                } catch (Exception e) {
                    Toast.makeText(ValidationActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });

        validation_gov_id_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadGovID();
            }
        });

        validation_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                validationProcess();
            }
        });
    }

    public void validationProcess() {
        final ProgressDialog progressDialog = new ProgressDialog(ValidationActivity.this, R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Validating User...");
        progressDialog.show();

        String validationCode = validation_code.getText().toString();

        if (!validate(validationCode)) {
            Snackbar.make(validation_scroll_view, "Invalid validation parameters", Snackbar.LENGTH_LONG).show();
            progressDialog.dismiss();
            return;
        } else if (!uploadedImage) {
            Snackbar.make(validation_scroll_view, "Must upload government ID", Snackbar.LENGTH_LONG).show();
            progressDialog.dismiss();
            return;
        } else {
            uploadImage();
            Snackbar.make(validation_scroll_view, "Account successfully validated", Snackbar.LENGTH_LONG).show();
            progressDialog.dismiss();
            return;
        }
    }

    public void uploadGovID() {
        showFileChooser();
    }

    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                validation_gov_id_image.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }

            uploadedImage = true;
        }
    }

    public String getStringImage(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    private void uploadImage(){
        String image = getStringImage(bitmap);
        user.setGovIdPicURL(image);

        final ProgressDialog progressDialog = new ProgressDialog(ValidationActivity.this, R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Validating User...");
        progressDialog.show();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RequestInterface requestInterface = retrofit.create(RequestInterface.class);
        ServerRequest request = new ServerRequest();
        request.setOperation(Constants.UPLOAD_GOV_ID_OPERATION);
        request.setUser(user);
        Call<ServerResponse> response = requestInterface.operation(request);

        response.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, retrofit2.Response<ServerResponse> response) {
                ServerResponse resp = response.body();

                if(resp.getResult().equals(Constants.SUCCESS)){
                    user.setIsValidated("true");
                    goToProfileScreen();
                }

                progressDialog.dismiss();
                Snackbar.make(validation_scroll_view, resp.getMessage(), Snackbar.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                progressDialog.dismiss();
                Snackbar.make(validation_scroll_view, t.getLocalizedMessage(), Snackbar.LENGTH_LONG).show();
            }
        });
    }

    public boolean validate(String validationCode) {
        boolean valid = true;

        if (validationCode.isEmpty() || validationCode.length() < 10) {
            validation_code.setError("Enter a valid 10 character code");
            valid = false;
        } else if (!(validationCode.equals(user.getValidationCode()))) {
            validation_code.setError("Invalid validation code");
            valid = false;
        } else {
            validation_code.setError(null);
        }

        return valid;
    }

    public void goToProfileScreen() {
        if (user.getDefaultRole().equals("Provider")) {
            Intent intent = new Intent(getApplicationContext(), CreateProviderProfileActivity.class);
            intent.putExtra("user", user);
            startActivity(intent);
        } else {
            Intent intent = new Intent(getApplicationContext(), CreateSeekerProfileActivity.class);
            intent.putExtra("user", user);
            startActivity(intent);
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
                return "Validation email resending failure";
            } else {
                return "Validation email resent to " + user.getEmail();
            }
        }

        @Override
        protected void onPostExecute(String result)
        {
            Toast.makeText(ValidationActivity.this, result, Toast.LENGTH_LONG).show();
        }

        @Override
        protected void onPreExecute() {}

        @Override
        protected void onProgressUpdate(Void... values) {}
    }
}
