package com.example.parkhere.provider;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.example.parkhere.R;
import com.example.parkhere.objects.ParkingSpace;
import com.example.parkhere.objects.User;
import com.example.parkhere.server.Constants;
import com.example.parkhere.server.RequestInterface;
import com.example.parkhere.server.ServerRequest;
import com.example.parkhere.server.ServerResponse;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AddParkingSpaceFragment extends Fragment {
    private User user;
    private static final String USER_KEY = "user";
    private EditText add_ps_address, add_ps_city, add_ps_state, add_ps_zip_code, add_ps_country, add_ps_additional_info;
    private Spinner add_ps_type, add_ps_permit, add_ps_cancellation;
    private ImageView add_ps_image, pspic1, pspic2, pspic3;
    private Button add_ps_button;
    private View rootView;
    private ParkingSpace parkingSpace;
    private int PICK_IMAGE_REQUEST = 1;
    private int PICK_PSPIC1_REQUEST = 2;
    private int PICK_PSPIC2_REQUEST = 3;
    private int PICK_PSPIC3_REQUEST = 4;
    private Bitmap bitmap, pspic1_bitmap, pspic2_bitmap, pspic3_bitmap;
    private boolean uploadedImage = false;
    private boolean pspic1Uploaded = false;
    private boolean pspic2Uploaded = false;
    private boolean pspic3Uploaded = false;
    private FragmentActivity myContext;

    public static AddParkingSpaceFragment newInstance(User user) {
        AddParkingSpaceFragment fragment = new AddParkingSpaceFragment();
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

        rootView = inflater.inflate(R.layout.fragment_add_parking_space, container, false);

        add_ps_address = (EditText) rootView.findViewById(R.id.add_ps_address);
        add_ps_address.requestFocus();
        add_ps_city = (EditText) rootView.findViewById(R.id.add_ps_city);
        add_ps_state = (EditText) rootView.findViewById(R.id.add_ps_state);
        add_ps_zip_code = (EditText) rootView.findViewById(R.id.add_ps_zipcode);
        add_ps_country = (EditText) rootView.findViewById(R.id.add_ps_country);
        add_ps_additional_info = (EditText) rootView.findViewById(R.id.add_ps_additionalinfo);
        add_ps_type = (Spinner) rootView.findViewById(R.id.add_ps_type_spinner);
        add_ps_permit = (Spinner) rootView.findViewById(R.id.add_ps_permit_spinner);
        add_ps_cancellation = (Spinner) rootView.findViewById(R.id.add_ps_cancellation_spinner);
        add_ps_button = (Button) rootView.findViewById(R.id.add_ps_button);
        add_ps_image = (ImageView) rootView.findViewById(R.id.add_ps_image);
        pspic1 = (ImageView) rootView.findViewById(R.id.pspic1);
        pspic2 = (ImageView) rootView.findViewById(R.id.pspic2);
        pspic3 = (ImageView) rootView.findViewById(R.id.pspic3);

        List<String> categories = new ArrayList<String>();
        categories.add("Compact");
        categories.add("SUV");
        categories.add("Truck");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(rootView.getContext(), android.R.layout.simple_spinner_item, categories);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        add_ps_type.setAdapter(dataAdapter);

        categories = new ArrayList<String>();
        categories.add("No Permit Required");
        categories.add("Permit Required");
        dataAdapter = new ArrayAdapter<String>(rootView.getContext(), android.R.layout.simple_spinner_item, categories);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        add_ps_permit.setAdapter(dataAdapter);

        categories = new ArrayList<String>();
        categories.add("No Cancellation Fee");
        categories.add("10% Fee Anytime After Reservation");
        categories.add("10% Fee Within 24 Hours of Reservation Start");
        dataAdapter = new ArrayAdapter<String>(rootView.getContext(), android.R.layout.simple_spinner_item, categories);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        add_ps_cancellation.setAdapter(dataAdapter);

        add_ps_image.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                showFileChooser();
            }
        });
        pspic1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                showFileChooser2();
            }
        });
        pspic2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                showFileChooser3();
            }
        });
        pspic3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                showFileChooser4();
            }
        });

        add_ps_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String address = add_ps_address.getText().toString();
                String city = add_ps_city.getText().toString();
                String state = add_ps_state.getText().toString();
                String zipCode = add_ps_zip_code.getText().toString();
                String country = add_ps_country.getText().toString();
                String additionalInfo = add_ps_additional_info.getText().toString();

                parkingSpace = new ParkingSpace();

                if (!validate(address, city, state, zipCode, country, additionalInfo)) {
                    onAddSpaceFailed();
                } else if (uploadedImage == false) {
                    Snackbar.make(rootView, "Please upload a picture of the parking space", Snackbar.LENGTH_LONG).show();
                } else {
                    String image = getStringImage(bitmap);

                    int numAddPics = 0;
                    if (pspic1Uploaded == true) {
                        parkingSpace.setPspic1(getStringImage(pspic1_bitmap));
                        numAddPics++;
                    } else {
                        parkingSpace.setPspic1("No");
                    }

                    if (pspic2Uploaded == true) {
                        parkingSpace.setPspic2(getStringImage(pspic2_bitmap));
                        numAddPics++;
                    } else {
                        parkingSpace.setPspic2("No");
                    }

                    if (pspic3Uploaded == true) {
                        parkingSpace.setPspic3(getStringImage(pspic3_bitmap));
                        numAddPics++;
                    } else {
                        parkingSpace.setPspic3("No");
                    }
                    parkingSpace.setNumAddPics(numAddPics);

                    String type = add_ps_type.getItemAtPosition(add_ps_type.getSelectedItemPosition()).toString();
                    CheckBox cbHandicap = (CheckBox) rootView.findViewById(R.id.cbHandicap);
                    if (cbHandicap.isChecked()) {
                        type += ",Handicap";
                    }
                    CheckBox cbCoveredParking = (CheckBox) rootView.findViewById(R.id.cbCoveredParking);
                    if (cbCoveredParking.isChecked()) {
                        type += ",CoveredParking";
                    }

                    String permitRequirement = add_ps_permit.getItemAtPosition(add_ps_permit.getSelectedItemPosition()).toString();
                    String cancellationPolicy = add_ps_cancellation.getItemAtPosition(add_ps_cancellation.getSelectedItemPosition()).toString();

                    parkingSpace.setAddress(address);
                    parkingSpace.setCity(city);
                    parkingSpace.setState(state);
                    parkingSpace.setZipCode(zipCode);
                    parkingSpace.setCountry(country);
                    parkingSpace.setAdditionalInfo(add_ps_additional_info.getText().toString());
                    parkingSpace.setType(type);
                    if (permitRequirement.equals("No Permit Required")) {
                        parkingSpace.setPermitRequirement(0);
                    } else {
                        parkingSpace.setPermitRequirement(1);
                    }
                    parkingSpace.setCancellationPolicy(cancellationPolicy);
                    parkingSpace.setImage(image);

                    String location = address + " " + city + " " + state + " " + zipCode;
                    List<Address> addressList = null;
                    Geocoder geocoder = new Geocoder(rootView.getContext());
                    try {
                        addressList = geocoder.getFromLocationName(location, 1);
                        Address geocoderAddress = addressList.get(0);
                        parkingSpace.setLongitude(geocoderAddress.getLongitude());
                        parkingSpace.setLatitude(geocoderAddress.getLatitude());

                        addSpaceProcess(parkingSpace);
                    } catch (IOException e) {
                        System.out.println("Address conversion error: " + e.getLocalizedMessage());
                    }
                }
            }
        });

        return rootView;
    }

    public void onAddSpaceFailed() {
        Snackbar.make(rootView, "Invalid add space parameters", Snackbar.LENGTH_LONG).show();
    }

    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    private void showFileChooser2() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_PSPIC1_REQUEST);
    }

    private void showFileChooser3() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_PSPIC2_REQUEST);
    }

    private void showFileChooser4() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_PSPIC3_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(rootView.getContext().getContentResolver(), filePath);
                add_ps_image.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }

            uploadedImage = true;
        } else if (requestCode == PICK_PSPIC1_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            try {
                pspic1_bitmap = MediaStore.Images.Media.getBitmap(rootView.getContext().getContentResolver(), filePath);
                pspic1.setImageBitmap(pspic1_bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }

            pspic1Uploaded = true;
        } else if (requestCode == PICK_PSPIC2_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            try {
                pspic2_bitmap = MediaStore.Images.Media.getBitmap(rootView.getContext().getContentResolver(), filePath);
                pspic2.setImageBitmap(pspic2_bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }

            pspic2Uploaded = true;
        } else if (requestCode == PICK_PSPIC3_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            try {
                pspic3_bitmap = MediaStore.Images.Media.getBitmap(rootView.getContext().getContentResolver(), filePath);
                pspic3.setImageBitmap(pspic1_bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }

            pspic3Uploaded = true;
        }
    }

    public String getStringImage(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    public boolean validate(String address, String city, String state, String zipCode, String country, String additionalInfo) {
        boolean valid = true;

        if (address.isEmpty()) {
            add_ps_address.setError("Enter a valid address");
            valid = false;
        } else {
            add_ps_address.setError(null);
        }

        if (city.isEmpty()) {
            add_ps_city.setError("Enter a valid city");
            valid = false;
        } else {
            add_ps_city.setError(null);
        }

        if (state.isEmpty() || state.length() != 2) {
            add_ps_state.setError("Enter a valid state (Ex: CA)");
            valid = false;
        } else {
            add_ps_state.setError(null);
        }

        if (zipCode.isEmpty() || zipCode.length() != 5) {
            add_ps_zip_code.setError("Enter a valid zip code");
            valid = false;
        } else {
            add_ps_zip_code.setError(null);
        }

        if (country.isEmpty() || !country.equals("USA")) {
            add_ps_country.setError("Country must be USA");
            valid = false;
        } else {
            add_ps_country.setError(null);
        }

        if (additionalInfo.isEmpty()) {
            add_ps_additional_info.setText("None");
        } else {
            add_ps_additional_info.setError(null);
        }

        return valid;
    }

    public void addSpaceProcess(final ParkingSpace parkingSpace) {
        final ProgressDialog progressDialog = new ProgressDialog(rootView.getContext(), R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Adding Parking Space...");
        progressDialog.show();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RequestInterface requestInterface = retrofit.create(RequestInterface.class);

        ServerRequest request = new ServerRequest();
        request.setOperation(Constants.ADD_PARKING_SPACE_OPERATION);
        request.setUser(user);
        request.setParkingSpace(parkingSpace);
        Call<ServerResponse> response = requestInterface.operation(request);

        response.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, retrofit2.Response<ServerResponse> response) {
                ServerResponse resp = response.body();

                if(resp.getResult().equals(Constants.SUCCESS)){
                    parkingSpace.setId(resp.getParkingSpace().getId());
                    Fragment fragment = AddParkingSpaceAvailabilityFragment.newInstance(user, parkingSpace);
                    FragmentManager fragmentManager = myContext.getSupportFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
                }

                progressDialog.dismiss();
                Snackbar.make(rootView, resp.getMessage(), Snackbar.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                progressDialog.dismiss();
                Snackbar.make(rootView, t.getLocalizedMessage(), Snackbar.LENGTH_LONG).show();
                System.out.println(t.getLocalizedMessage());
            }
        });
    }

}
