package com.example.parkhere.main;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.example.parkhere.R;
import com.example.parkhere.objects.ProviderPayment;
import com.example.parkhere.objects.User;
import com.example.parkhere.provider.ProviderHomeActivity;
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

public class CreateProviderProfileActivity extends AppCompatActivity {
    private View create_pprof_view;
    private EditText create_pprof_fname, create_pprof_lname, create_pprof_phonenumber, create_pprof_bank, create_pprof_accountnumber, create_pprof_routingnumber;
    private RadioGroup create_pprof_radio_group;
    private CircleImageView create_pprof_profile;
    private Button create_pprof_button;
    private User user;
    private int PICK_IMAGE_REQUEST = 1;
    private Bitmap bitmap;
    private boolean uploadedImage = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_provider_profile);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        user = (User) getIntent().getSerializableExtra("user");
        create_pprof_view = (View) findViewById(R.id.create_pprof_view);
        create_pprof_fname = (EditText) findViewById(R.id.create_pprof_fname);
        create_pprof_lname = (EditText) findViewById(R.id.create_pprof_lname);
        create_pprof_phonenumber = (EditText) findViewById(R.id.create_pprof_phonenumber);
        create_pprof_bank = (EditText) findViewById(R.id.create_pprof_bank);
        create_pprof_accountnumber = (EditText) findViewById(R.id.create_pprof_accountnumber);
        create_pprof_routingnumber = (EditText) findViewById(R.id.create_pprof_routingnumber);
        create_pprof_radio_group = (RadioGroup) findViewById(R.id.create_pprof_radio_group);
        create_pprof_profile = (CircleImageView) findViewById(R.id.civProfilePic);
        View photoHeader = (View) findViewById(R.id.create_pprof_profile);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            photoHeader.setTranslationZ(10);
            photoHeader.invalidate();
        }
        create_pprof_button = (Button) findViewById(R.id.create_pprof_button);

        create_pprof_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                createProviderProfile();
            }
        });

        create_pprof_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadProfPic();
            }
        });
    }

    public void uploadProfPic() {
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
                create_pprof_profile.setImageBitmap(bitmap);
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

    public void createProviderProfile() {
        String fname = create_pprof_fname.getText().toString();
        String lname = create_pprof_lname.getText().toString();
        String phoneNumber = create_pprof_phonenumber.getText().toString();
        String bank = create_pprof_bank.getText().toString();
        String accountNumber = create_pprof_accountnumber.getText().toString();
        String routingNumber = create_pprof_routingnumber.getText().toString();

        int selectedID = create_pprof_radio_group.getCheckedRadioButtonId();
        RadioButton radioButton = (RadioButton) findViewById(selectedID);
        String accountType = radioButton.getText().toString();

        if (!validate(fname, lname, phoneNumber, bank, accountNumber, routingNumber)) {
            onCreateProviderProfileFailed();
            return;
        } else if (!uploadedImage) {
            noProfPic();
            return;
        }

        String image = getStringImage(bitmap);
        createProviderProfileProcess(fname, lname, phoneNumber, bank, accountNumber.substring(accountNumber.length()-4, accountNumber.length()),
                routingNumber.substring(routingNumber.length()-4, routingNumber.length()), accountType, image);
    }

    public void onCreateProviderProfileFailed() {
        Snackbar.make(create_pprof_view, "Invalid provider profile creation parameters", Snackbar.LENGTH_LONG).show();
    }

    public void noProfPic() {
        Snackbar.make(create_pprof_view, "Must upload profile picture", Snackbar.LENGTH_LONG).show();
    }

    public boolean validate(String fname, String lname, String phoneNumber, String bank, String accountNumber, String routingNumber) {
        boolean valid = true;

        if (fname.isEmpty()) {
            create_pprof_fname.setError("Enter your first name");
            valid = false;
        } else {
            create_pprof_fname.setError(null);
        }

        if (lname.isEmpty()) {
            create_pprof_lname.setError("Enter your last name");
            valid = false;
        } else {
            create_pprof_lname.setError(null);
        }

        if (phoneNumber.isEmpty() || phoneNumber.length() < 10) {
            create_pprof_phonenumber.setError("Enter a valid phone number");
            valid = false;
        } else {
            create_pprof_phonenumber.setError(null);
        }

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

    public void createProviderProfileProcess(String fname, String lname, String phoneNumber, String bank,
                                             String accountNumber, String routingNumber, String accountType, String image) {
        final ProgressDialog progressDialog = new ProgressDialog(CreateProviderProfileActivity.this, R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creating Provider Profile...");
        progressDialog.show();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RequestInterface requestInterface = retrofit.create(RequestInterface.class);

        user.setFirstName(fname);
        user.setLastName(lname);
        user.setPhoneNumber(phoneNumber);
        user.setProfilePicURL(image);

        ProviderPayment payment = new ProviderPayment();
        payment.setBank(bank);
        payment.setAccountNumber(accountNumber);
        payment.setRoutingNumber(routingNumber);
        payment.setAccountType(accountType);

        ServerRequest request = new ServerRequest();
        request.setOperation(Constants.CREATE_PROVIDER_PROFILE_OPERATION);
        request.setUser(user);
        request.setProviderPayment(payment);
        Call<ServerResponse> response = requestInterface.operation(request);

        response.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, retrofit2.Response<ServerResponse> response) {
                ServerResponse resp = response.body();

                if(resp.getResult().equals(Constants.SUCCESS)){
                    user.setProfilePicURL(resp.getUser().getProfilePicURL());
                    goToProviderUserhomeScreen();
                }

                progressDialog.dismiss();
                Snackbar.make(create_pprof_view, resp.getMessage(), Snackbar.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                progressDialog.dismiss();
                Snackbar.make(create_pprof_view, t.getLocalizedMessage(), Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void goToProviderUserhomeScreen() {
        Intent intent = new Intent(getApplicationContext(), ProviderHomeActivity.class);
        user.setProfilePicURL("profile_pics/" + user.getEmail() + ".png");
        intent.putExtra("user", user);
        startActivity(intent);
    }
}
