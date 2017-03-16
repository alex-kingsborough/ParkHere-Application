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

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CreateSeekerProfileActivity extends AppCompatActivity {
    private View create_sprof_view;
    private EditText create_sprof_fname, create_sprof_lname, create_sprof_phonenumber;
    private EditText create_sprof_ccfname, create_sprof_cclname, create_sprof_ccname, create_sprof_ccnumber, create_sprof_ccexpmonth, create_sprof_ccexpyear;
    private EditText create_sprof_address, create_sprof_city, create_sprof_state, create_sprof_zipcode, create_sprof_country;
    private EditText create_sprof_make, create_sprof_model, create_sprof_color, create_sprof_year, create_sprof_licenseplate;
    private CircleImageView create_sprof_profile;
    private Button create_sprof_button;
    private User user;
    private int PICK_IMAGE_REQUEST = 1;
    private Bitmap bitmap;
    private boolean uploadedImage = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_seeker_profile);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        user = (User) getIntent().getSerializableExtra("user");
        create_sprof_view = (View) findViewById(R.id.create_sprof_view);
        create_sprof_fname = (EditText) findViewById(R.id.create_sprof_fname);
        create_sprof_lname = (EditText) findViewById(R.id.create_sprof_lname);
        create_sprof_phonenumber = (EditText) findViewById(R.id.create_sprof_phonenumber);

        create_sprof_ccfname = (EditText) findViewById(R.id.create_sprof_ccfname);
        create_sprof_cclname =(EditText) findViewById(R.id.create_sprof_cclname);
        create_sprof_ccname =(EditText) findViewById(R.id.create_sprof_ccname);
        create_sprof_ccnumber = (EditText) findViewById(R.id.create_sprof_ccnumber);
        create_sprof_ccexpmonth = (EditText) findViewById(R.id.create_sprof_ccexpmonth);
        create_sprof_ccexpyear =(EditText) findViewById(R.id.create_sprof_ccexpyear);

        create_sprof_address = (EditText) findViewById(R.id.create_sprof_address);
        create_sprof_city = (EditText) findViewById(R.id.create_sprof_city);
        create_sprof_state = (EditText) findViewById(R.id.create_sprof_state);
        create_sprof_zipcode = (EditText) findViewById(R.id.create_sprof_zipcode);
        create_sprof_country = (EditText) findViewById(R.id.create_sprof_country);

        create_sprof_make = (EditText) findViewById(R.id.create_sprof_make);
        create_sprof_model = (EditText) findViewById(R.id.create_sprof_model);
        create_sprof_color = (EditText) findViewById(R.id.create_sprof_color);
        create_sprof_year = (EditText) findViewById(R.id.create_sprof_year);
        create_sprof_licenseplate = (EditText) findViewById(R.id.create_sprof_licenseplate);
        create_sprof_profile = (CircleImageView) findViewById(R.id.civProfilePic);
        View photoHeader = (View) findViewById(R.id.create_sprof_profile);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            photoHeader.setTranslationZ(10);
            photoHeader.invalidate();
        }
        create_sprof_button = (Button) findViewById(R.id.create_sprof_button);

        create_sprof_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                createSeekerProfile();
            }
        });

        create_sprof_profile.setOnClickListener(new View.OnClickListener() {
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
                create_sprof_profile.setImageBitmap(bitmap);
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

    public void createSeekerProfile() {
        final ProgressDialog progressDialog = new ProgressDialog(CreateSeekerProfileActivity.this, R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creating Seeker Profile...");
        progressDialog.show();

        String fname = create_sprof_fname.getText().toString();
        String lname = create_sprof_lname.getText().toString();
        String phoneNumber = create_sprof_phonenumber.getText().toString();
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

        String address = create_sprof_address.getText().toString();
        String city = create_sprof_city.getText().toString();
        String state = create_sprof_state.getText().toString();
        String zipCode = create_sprof_zipcode.getText().toString();
        String country = create_sprof_country.getText().toString();

        BillingAddress billingAddress = new BillingAddress();
        billingAddress.setAddress(address);
        billingAddress.setCity(city);
        billingAddress.setState(state);
        billingAddress.setZipCode(zipCode);
        billingAddress.setCountry(country);

        String make = create_sprof_make.getText().toString();
        String model = create_sprof_model.getText().toString();
        String color = create_sprof_color.getText().toString();
        String year = create_sprof_year.getText().toString();
        String licensePlate = create_sprof_licenseplate.getText().toString();

        Vehicle vehicle = new Vehicle();
        vehicle.setMake(make);
        vehicle.setModel(model);
        vehicle.setColor(color);
        vehicle.setYear(year);
        vehicle.setLicensePlate(licensePlate);

        if (!validate(fname, lname, phoneNumber, seekerPayment, billingAddress, vehicle)) {
            onCreateSeekerProfileFailed();
            progressDialog.dismiss();
            return;
        } else if (!uploadedImage) {
            Snackbar.make(create_sprof_view, "Must upload profile picture", Snackbar.LENGTH_LONG).show();
            progressDialog.dismiss();
            return;
        }

        String image = getStringImage(bitmap);
        int ccNum = Integer.parseInt(ccnumber.substring(ccnumber.length()-4, ccnumber.length()));
        seekerPayment.setNumber(Integer.toString(ccNum));
        createSeekerProfileProcess(fname, lname, phoneNumber, image, seekerPayment, billingAddress, vehicle);
    }

    public void onCreateSeekerProfileFailed() {
        Snackbar.make(create_sprof_view, "Invalid seeker profile creation parameters", Snackbar.LENGTH_LONG).show();
    }

    public boolean validate(String fname, String lname, String phoneNumber, SeekerPayment seekerPayment, BillingAddress billingAddress,
                            Vehicle vehicle) {
        boolean valid = true;

        if (fname.isEmpty()) {
            create_sprof_fname.setError("Enter your first name");
            valid = false;
        } else {
            create_sprof_fname.setError(null);
        }

        if (lname.isEmpty()) {
            create_sprof_lname.setError("Enter your last name");
            valid = false;
        } else {
            create_sprof_lname.setError(null);
        }

        if (phoneNumber.isEmpty() || phoneNumber.length() < 10) {
            create_sprof_phonenumber.setError("Enter a valid phone number");
            valid = false;
        } else {
            create_sprof_phonenumber.setError(null);
        }

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
            create_sprof_address.setError("Enter a valid billing address");
            valid = false;
        } else {
            create_sprof_address.setError(null);
        }

        if (billingAddress.getCity().isEmpty()) {
            create_sprof_city.setError("Enter a valid billing city");
            valid = false;
        } else {
            create_sprof_city.setError(null);
        }

        if (billingAddress.getState().isEmpty() || billingAddress.getState().length() != 2) {
            create_sprof_state.setError("Enter a valid billing state (Ex: CA)");
            valid = false;
        } else {
            create_sprof_state.setError(null);
        }

        if (billingAddress.getZipCode().isEmpty() || billingAddress.getZipCode().length() != 5) {
            create_sprof_zipcode.setError("Enter a valid 5 digit billing zip code");
            valid = false;
        } else {
            create_sprof_zipcode.setError(null);
        }

        if (billingAddress.getCountry().isEmpty() || !billingAddress.getCountry().equals("USA")) {
            create_sprof_country.setError("Enter a valid billing country (USA)");
            valid = false;
        } else {
            create_sprof_country.setError(null);
        }

        //VEHICLE CHECKS
        if (vehicle.getMake().isEmpty()) {
            create_sprof_make.setError("Enter a valid vehicle make");
            valid = false;
        } else {
            create_sprof_make.setError(null);
        }

        if (vehicle.getModel().isEmpty()) {
            create_sprof_model.setError("Enter a valid vehicle model");
            valid = false;
        } else {
            create_sprof_model.setError(null);
        }

        if (vehicle.getColor().isEmpty()) {
            create_sprof_color.setError("Enter a valid vehicle color");
            valid = false;
        } else {
            create_sprof_color.setError(null);
        }

        if (vehicle.getYear().isEmpty() || vehicle.getYear().length() < 4) {
            create_sprof_year.setError("Enter a valid vehicle year");
            valid = false;
        } else {
            create_sprof_year.setError(null);
        }

        if (vehicle.getLicensePlate().isEmpty()) {
            create_sprof_licenseplate.setError("Enter a valid vehicle license plate");
            valid = false;
        } else {
            create_sprof_licenseplate.setError(null);
        }

        return valid;
    }

    public void createSeekerProfileProcess(String fname, String lname, String phoneNumber, String image, SeekerPayment seekerPayment,
                                           BillingAddress billingAddress, Vehicle vehicle) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RequestInterface requestInterface = retrofit.create(RequestInterface.class);

        user.setFirstName(fname);
        user.setLastName(lname);
        user.setPhoneNumber(phoneNumber);
        user.setProfilePicURL(image);

        ServerRequest request = new ServerRequest();
        request.setOperation(Constants.CREATE_SEEKER_PROFILE_OPERATION);
        request.setUser(user);
        request.setSeekerPayment(seekerPayment);
        request.setBillingAddress(billingAddress);
        request.setVehicle(vehicle);
        Call<ServerResponse> response = requestInterface.operation(request);

        response.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, retrofit2.Response<ServerResponse> response) {
                ServerResponse resp = response.body();

                if(resp.getResult().equals(Constants.SUCCESS)){
                    goToSeekerUserhomeScreen();
                }

                Snackbar.make(create_sprof_view, resp.getMessage(), Snackbar.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                Snackbar.make(create_sprof_view, t.getLocalizedMessage(), Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void goToSeekerUserhomeScreen() {
        Intent intent = new Intent(getApplicationContext(), SeekerHomeActivity.class);
        user.setProfilePicURL("profile_pics/" + user.getEmail() + ".png");
        intent.putExtra("user", user);
        startActivity(intent);
    }
}
