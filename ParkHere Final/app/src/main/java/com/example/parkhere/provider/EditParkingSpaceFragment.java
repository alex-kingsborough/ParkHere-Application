package com.example.parkhere.provider;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Base64;
import android.util.Log;
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
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class EditParkingSpaceFragment extends Fragment {
    private User user;
    private static final String USER_KEY = "user";
    private ParkingSpace parkingSpace;
    private static final String PARKING_SPACE_KEY = "parkingSpace";
    private EditText edit_ps_address, edit_ps_city, edit_ps_state, edit_ps_zip_code, edit_ps_country, edit_ps_additional_info;
    private Spinner edit_ps_type, edit_ps_permit, edit_ps_cancellation;
    private ImageView edit_ps_image, pspic1, pspic2, pspic3;;
    private Button edit_ps_button;
    private View rootView;
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
    private ArrayAdapter<String> typeDataAdapter, permitDataAdapter, cancellationDataAdapter;
    private CheckBox cbHandicap, cbCoveredParking;
    private int[] picsSet = {0,0,0};
    private long stime, etime, duration;


    public static EditParkingSpaceFragment newInstance(User user, ParkingSpace parkingSpace) {
        EditParkingSpaceFragment fragment = new EditParkingSpaceFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(USER_KEY, user);
        bundle.putSerializable(PARKING_SPACE_KEY, parkingSpace);
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
        stime = System.nanoTime();

        user = (User) getArguments().getSerializable(USER_KEY);
        parkingSpace = (ParkingSpace) getArguments().getSerializable(PARKING_SPACE_KEY);

        rootView = inflater.inflate(R.layout.fragment_edit_parking_space, container, false);

        edit_ps_address = (EditText) rootView.findViewById(R.id.edit_ps_address);
        edit_ps_city = (EditText) rootView.findViewById(R.id.edit_ps_city);
        edit_ps_state = (EditText) rootView.findViewById(R.id.edit_ps_state);
        edit_ps_zip_code = (EditText) rootView.findViewById(R.id.edit_ps_zipcode);
        edit_ps_country = (EditText) rootView.findViewById(R.id.edit_ps_country);
        edit_ps_additional_info = (EditText) rootView.findViewById(R.id.edit_ps_additionalinfo);
        edit_ps_type = (Spinner) rootView.findViewById(R.id.edit_ps_type_spinner);
        edit_ps_permit = (Spinner) rootView.findViewById(R.id.edit_ps_permit_spinner);
        edit_ps_cancellation = (Spinner) rootView.findViewById(R.id.edit_ps_cancellation_spinner);
        edit_ps_button = (Button) rootView.findViewById(R.id.edit_ps_button);
        edit_ps_image = (ImageView) rootView.findViewById(R.id.edit_ps_image);
        pspic1 = (ImageView) rootView.findViewById(R.id.pspic1);
        pspic2 = (ImageView) rootView.findViewById(R.id.pspic2);
        pspic3 = (ImageView) rootView.findViewById(R.id.pspic3);

        List<String> categories = new ArrayList<String>();
        categories.add("Compact");
        categories.add("SUV");
        categories.add("Truck");
        typeDataAdapter = new ArrayAdapter<String>(rootView.getContext(), android.R.layout.simple_spinner_item, categories);
        typeDataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        edit_ps_type.setAdapter(typeDataAdapter);

        categories = new ArrayList<String>();
        categories.add("No Permit Required");
        categories.add("Permit Required");
        permitDataAdapter = new ArrayAdapter<String>(rootView.getContext(), android.R.layout.simple_spinner_item, categories);
        permitDataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        edit_ps_permit.setAdapter(permitDataAdapter);

        categories = new ArrayList<String>();
        categories.add("No Cancellation Fee");
        categories.add("10% Fee Anytime After Reservation");
        categories.add("10% Fee Within 24 Hours of Reservation Start");
        cancellationDataAdapter = new ArrayAdapter<String>(rootView.getContext(), android.R.layout.simple_spinner_item, categories);
        cancellationDataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        edit_ps_cancellation.setAdapter(cancellationDataAdapter);

        edit_ps_address.setText(parkingSpace.getAddress());
        edit_ps_city.setText(parkingSpace.getCity());
        edit_ps_state.setText(parkingSpace.getState());
        edit_ps_zip_code.setText(parkingSpace.getZipCode());
        edit_ps_country.setText(parkingSpace.getCountry());
        edit_ps_additional_info.setText(parkingSpace.getAdditionalInfo());

        String imgURL = "http://10.0.2.2/parkhere/" + parkingSpace.getImage();
        new DownloadImageTask(edit_ps_image).execute(imgURL);

        int numAddPics = parkingSpace.getNumAddPics();
        if (numAddPics == 1) {
            imgURL = "http://10.0.2.2/parkhere/parking_spaces_pics/" + parkingSpace.getId() + "-1.png";
            new DownloadImageTask(pspic1).execute(imgURL);
            picsSet[0] = 1;
        } else if (numAddPics == 2) {
            imgURL = "http://10.0.2.2/parkhere/parking_spaces_pics/" + parkingSpace.getId() + "-1.png";
            new DownloadImageTask(pspic1).execute(imgURL);
            picsSet[0] = 1;
            imgURL = "http://10.0.2.2/parkhere/parking_spaces_pics/" + parkingSpace.getId() + "-2.png";
            new DownloadImageTask(pspic2).execute(imgURL);
            picsSet[1] = 1;
        } else if (numAddPics == 3) {
            imgURL = "http://10.0.2.2/parkhere/parking_spaces_pics/" + parkingSpace.getId() + "-1.png";
            new DownloadImageTask(pspic1).execute(imgURL);
            picsSet[0] = 1;
            imgURL = "http://10.0.2.2/parkhere/parking_spaces_pics/" + parkingSpace.getId() + "-2.png";
            new DownloadImageTask(pspic2).execute(imgURL);
            picsSet[1] = 1;
            imgURL = "http://10.0.2.2/parkhere/parking_spaces_pics/" + parkingSpace.getId() + "-3.png";
            new DownloadImageTask(pspic3).execute(imgURL);
            picsSet[2] = 1;
        }

        String[] typesArray = parkingSpace.getType().split(",");
        int spinnerPosition = typeDataAdapter.getPosition(typesArray[0]);
        edit_ps_type.setSelection(spinnerPosition);

        cbHandicap = (CheckBox) rootView.findViewById(R.id.editCbHandicap);
        if (typesArray.length >= 2 && typesArray[1].equals("Handicap")) {
            cbHandicap.setChecked(true);
        }
        cbCoveredParking = (CheckBox) rootView.findViewById(R.id.editCbCoveredParking);
        if (typesArray.length == 2 && typesArray[1].equals("CoveredParking") || typesArray.length == 3 && typesArray[2].equals("CoveredParking")) {
            cbCoveredParking.setChecked(true);
        }

        String permit = "";
        if (parkingSpace.getPermitRequirement() == 0) {
            permit = "No Permit Required";
        } else {
            permit = "Permit Required";
        }
        spinnerPosition = permitDataAdapter.getPosition(permit);
        edit_ps_permit.setSelection(spinnerPosition);

        spinnerPosition = cancellationDataAdapter.getPosition(parkingSpace.getCancellationPolicy());
        edit_ps_cancellation.setSelection(spinnerPosition);

        edit_ps_image.setOnClickListener(new View.OnClickListener() {
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

        edit_ps_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String address = edit_ps_address.getText().toString();
                String city = edit_ps_city.getText().toString();
                String state = edit_ps_state.getText().toString();
                String zipCode = edit_ps_zip_code.getText().toString();
                String country = edit_ps_country.getText().toString();
                String additionalInfo = edit_ps_additional_info.getText().toString();

                if (!validate(address, city, state, zipCode, country, additionalInfo)) {
                    onEditSpaceFailed();
                } else {
                    ParkingSpace updatedParkingSpace = new ParkingSpace();

                    String image;
                    if (uploadedImage == false) {
                        image = "No";
                    } else {
                        image = getStringImage(bitmap);
                    }
                    int numAddPics = 0;
                    if (pspic1Uploaded == true) {
                        updatedParkingSpace.setPspic1(getStringImage(pspic1_bitmap));
                        if (picsSet[0] == 0){
                            numAddPics++;
                        }
                    } else {
                        updatedParkingSpace.setPspic1("No");
                    }

                    if (pspic2Uploaded == true) {
                        updatedParkingSpace.setPspic2(getStringImage(pspic2_bitmap));
                        if (picsSet[1] == 0){
                            numAddPics++;
                        }
                    } else {
                        updatedParkingSpace.setPspic2("No");
                    }

                    if (pspic3Uploaded == true) {
                        updatedParkingSpace.setPspic3(getStringImage(pspic3_bitmap));
                        if (picsSet[2] == 0){
                            numAddPics++;
                        }
                    } else {
                        updatedParkingSpace.setPspic3("No");
                    }
                    updatedParkingSpace.setNumAddPics(numAddPics + parkingSpace.getNumAddPics());

                    String type = edit_ps_type.getItemAtPosition(edit_ps_type.getSelectedItemPosition()).toString();
                    if (cbHandicap.isChecked()) {
                        type += ",Handicap";
                    }
                    if (cbCoveredParking.isChecked()) {
                        type += ",CoveredParking";
                    }

                    String permitRequirement = edit_ps_permit.getItemAtPosition(edit_ps_permit.getSelectedItemPosition()).toString();
                    String cancellationPolicy = edit_ps_cancellation.getItemAtPosition(edit_ps_cancellation.getSelectedItemPosition()).toString();

                    updatedParkingSpace.setId(parkingSpace.getId());
                    updatedParkingSpace.setAddress(address);
                    updatedParkingSpace.setCity(city);
                    updatedParkingSpace.setState(state);
                    updatedParkingSpace.setZipCode(zipCode);
                    updatedParkingSpace.setCountry(country);
                    updatedParkingSpace.setAdditionalInfo(edit_ps_additional_info.getText().toString());
                    updatedParkingSpace.setType(type);
                    if (permitRequirement.equals("No Permit Required")) {
                        updatedParkingSpace.setPermitRequirement(0);
                    } else {
                        updatedParkingSpace.setPermitRequirement(1);
                    }
                    updatedParkingSpace.setCancellationPolicy(cancellationPolicy);
                    updatedParkingSpace.setImage(image);

                    String location = address + " " + city + " " + state + " " + zipCode;
                    List<Address> addressList = null;
                    Geocoder geocoder = new Geocoder(rootView.getContext());
                    try {
                        addressList = geocoder.getFromLocationName(location, 1);
                        Address geocoderAddress = addressList.get(0);
                        updatedParkingSpace.setLongitude(geocoderAddress.getLongitude());
                        updatedParkingSpace.setLatitude(geocoderAddress.getLatitude());

                        updateSpaceProcess(updatedParkingSpace);
                    } catch (IOException e) {
                        System.out.println("Address conversion error: " + e.getLocalizedMessage());
                    }
                }
            }
        });

        etime = System.nanoTime();
        duration = (etime - stime);
        System.out.println("edit parking space fragment took: " + duration);
        return rootView;
    }

    public void onEditSpaceFailed() {
        Snackbar.make(rootView, "Invalid edit space parameters", Snackbar.LENGTH_LONG).show();
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
                edit_ps_image.setImageBitmap(bitmap);
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
                pspic3.setImageBitmap(pspic3_bitmap);
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
            edit_ps_address.setError("Enter a valid address");
            valid = false;
        } else {
            edit_ps_address.setError(null);
        }

        if (city.isEmpty()) {
            edit_ps_city.setError("Enter a valid city");
            valid = false;
        } else {
            edit_ps_city.setError(null);
        }

        if (state.isEmpty() || state.length() != 2) {
            edit_ps_state.setError("Enter a valid state");
            valid = false;
        } else {
            edit_ps_state.setError(null);
        }

        if (zipCode.isEmpty() || zipCode.length() != 5) {
            edit_ps_zip_code.setError("Enter a valid zip code");
            valid = false;
        } else {
            edit_ps_zip_code.setError(null);
        }

        if (country.isEmpty() || !country.equals("USA")) {
            edit_ps_country.setError("Country must be USA");
            valid = false;
        } else {
            edit_ps_country.setError(null);
        }

        if (additionalInfo.isEmpty()) {
            edit_ps_additional_info.setText("None");
        } else {
            edit_ps_additional_info.setError(null);
        }

        return valid;
    }

    public void updateSpaceProcess(final ParkingSpace parkingSpace) {
        final ProgressDialog progressDialog = new ProgressDialog(rootView.getContext(), R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Updating Parking Space...");
        progressDialog.show();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RequestInterface requestInterface = retrofit.create(RequestInterface.class);

        ServerRequest request = new ServerRequest();
        request.setOperation(Constants.UPDATE_PARKING_SPACE_OPERATION);
        request.setUser(user);
        request.setParkingSpace(parkingSpace);
        Call<ServerResponse> response = requestInterface.operation(request);

        response.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, retrofit2.Response<ServerResponse> response) {
                ServerResponse resp = response.body();

                if(resp.getResult().equals(Constants.SUCCESS)){
                    Fragment fragment = EditParkingSpaceAvailabilityFragment.newInstance(user, parkingSpace);
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
            }
        });
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}