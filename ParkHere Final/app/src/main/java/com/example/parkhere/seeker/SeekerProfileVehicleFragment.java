package com.example.parkhere.seeker;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.parkhere.R;
import com.example.parkhere.objects.User;
import com.example.parkhere.objects.Vehicle;
import com.example.parkhere.server.Constants;
import com.example.parkhere.server.RequestInterface;
import com.example.parkhere.server.ServerRequest;
import com.example.parkhere.server.ServerResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SeekerProfileVehicleFragment extends Fragment {
    private User user;
    private Vehicle[] vehicles;
    private static final String USER_KEY = "user";
    private static final String SEEKER_VEHICLES_KEY = "vehicles";
    private View rootView;
    private EditText sprof_make, sprof_model, sprof_color, sprof_year, sprof_licenseplate;
    private TableLayout tl;
    private Button sprof_add_vehicle_button, sprof_edit_vehicle_button, sprof_delete_vehicle_button;
    private FragmentActivity myContext;
    private int tag = 1;
    private String selectedMake = "";
    private String selectedModel = "";
    private String selectedLicensePlate = "";
    private int selectedIndex = -1;

    @Override
    public void onAttach(Activity activity) {
        myContext=(FragmentActivity) activity;
        super.onAttach(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        user = (User) getArguments().getSerializable(USER_KEY);
        vehicles = (Vehicle[]) getArguments().getSerializable(SEEKER_VEHICLES_KEY);

        getCurrInfo();

        rootView = inflater.inflate(R.layout.fragment_seeker_profile_vehicle, container, false);

        sprof_make = (EditText) rootView.findViewById(R.id.sprof_make);
        sprof_model =(EditText) rootView.findViewById(R.id.sprof_model);
        sprof_color =(EditText) rootView.findViewById(R.id.sprof_color);
        sprof_year = (EditText) rootView.findViewById(R.id.sprof_year);
        sprof_licenseplate = (EditText) rootView.findViewById(R.id.sprof_licenseplate);

        tl = (TableLayout) rootView.findViewById(R.id.sprof_vehicle_table);

        sprof_add_vehicle_button = (Button) rootView.findViewById(R.id.sprof_add_vehicle_button);
        sprof_edit_vehicle_button = (Button) rootView.findViewById(R.id.sprof_edit_vehicle_button);
        sprof_delete_vehicle_button = (Button) rootView.findViewById(R.id.sprof_delete_vehicle_button);

        sprof_add_vehicle_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String make = sprof_make.getText().toString();
                String model = sprof_model.getText().toString();
                String color = sprof_color.getText().toString();
                String year = sprof_year.getText().toString();
                String licensePlate = sprof_licenseplate.getText().toString();

                Vehicle vehicle = new Vehicle();
                vehicle.setMake(make);
                vehicle.setModel(model);
                vehicle.setColor(color);
                vehicle.setYear(year);
                vehicle.setLicensePlate(licensePlate);

                if (!validate(vehicle)) {
                    onAddUpdateVehicleFailed();
                    return;
                }

                Vehicle v = getVehicle(licensePlate);
                if (v.getMake() == null) {
                    addVehicle(vehicle);
                } else {
                    Snackbar.make(rootView, "Vehicle with this license plate already exists; cannot re-add", Snackbar.LENGTH_LONG).show();
                }
            }
        });

        sprof_edit_vehicle_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (selectedMake.equals("")){
                    Snackbar.make(rootView, "Must select a vehicle", Snackbar.LENGTH_LONG).show();
                } else {
                    String make = sprof_make.getText().toString();
                    String model = sprof_model.getText().toString();
                    String color = sprof_color.getText().toString();
                    String year = sprof_year.getText().toString();
                    String licensePlate = sprof_licenseplate.getText().toString();

                    Vehicle vehicle = new Vehicle();
                    vehicle.setMake(make);
                    vehicle.setModel(model);
                    vehicle.setColor(color);
                    vehicle.setYear(year);
                    vehicle.setLicensePlate(licensePlate);

                    if (!validate(vehicle)) {
                        onAddUpdateVehicleFailed();
                        return;
                    } else if (!licensePlate.equals(selectedLicensePlate)) {
                        Snackbar.make(rootView, "Cannot update vehicle license plate; add a new vehicle with this license plate", Snackbar.LENGTH_LONG).show();
                    } else {
                        vehicles[selectedIndex-1] = vehicle;
                        TableRow tr = (TableRow) tl.findViewWithTag(selectedIndex);
                        tr.setBackgroundColor(Color.LTGRAY);
                        TextView tv = (TextView) tr.getChildAt(0);
                        tv.setText(vehicle.getMake());
                        tv = (TextView) tr.getChildAt(1);
                        tv.setText(vehicle.getModel());
                        editVehicle();
                    }
                }
            }
        });

        sprof_delete_vehicle_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (selectedMake.equals("")){
                    Snackbar.make(rootView, "Must select a vehicle", Snackbar.LENGTH_LONG).show();
                } else if (tag == 2) {
                    Snackbar.make(rootView, "Must add another vehicle before deleting your only listed vehicle", Snackbar.LENGTH_LONG).show();
                } else {
                    LayoutInflater li = LayoutInflater.from(myContext);
                    View customView = li.inflate(R.layout.popup_delete_vehicle, null);

                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(myContext);
                    alertDialogBuilder.setView(customView);
                    final AlertDialog alertDialog = alertDialogBuilder.create();

                    TextView deleteText = (TextView) customView.findViewById(R.id.popup_delete_vehicle_text);
                    deleteText.setText("Are you sure you want to delete this vehicle: " + selectedMake + " " +
                            selectedModel + " " + selectedLicensePlate + "?");

                    ImageButton closeButton = (ImageButton) customView.findViewById(R.id.popup_delete_vehicle_close);
                    closeButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            alertDialog.dismiss();
                        }
                    });

                    Button deleteButton = (Button) customView.findViewById(R.id.popup_delete_vehicle_button);
                    deleteButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            deleteVehicle();
                            alertDialog.dismiss();
                        }
                    });

                    alertDialog.show();
                }
            }
        });

        return rootView;
    }

    public void getCurrInfo() {
        tag = 1;

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RequestInterface requestInterface = retrofit.create(RequestInterface.class);

        ServerRequest request = new ServerRequest();
        request.setOperation(Constants.GET_SEEKER_PROFILE_OPERATION);
        request.setUser(user);
        Call<ServerResponse> response = requestInterface.operation(request);

        response.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, retrofit2.Response<ServerResponse> response) {
                ServerResponse resp = response.body();

                if(resp.getResult().equals(Constants.SUCCESS)){
                    vehicles = resp.getVehicles();

                    TableRow tr_head = new TableRow(myContext);
                    tr_head.setBackgroundColor(Color.GRAY);
                    tr_head.setLayoutParams(new TableRow.LayoutParams(
                            TableRow.LayoutParams.FILL_PARENT,
                            TableRow.LayoutParams.WRAP_CONTENT));

                    TextView make = new TextView(myContext);
                    make.setText("MAKE");
                    make.setTextColor(Color.WHITE);
                    make.setPadding(5, 5, 5, 5);
                    tr_head.addView(make);

                    TextView model = new TextView(myContext);
                    model.setText("MODEL");
                    model.setTextColor(Color.WHITE);
                    model.setPadding(5, 5, 5, 5);
                    tr_head.addView(model);

                    final TextView license = new TextView(myContext);
                    license.setText("LICENSE PLATE");
                    license.setTextColor(Color.WHITE);
                    license.setPadding(5, 5, 5, 5);
                    tr_head.addView(license);

                    tl.addView(tr_head, new TableLayout.LayoutParams(
                            TableLayout.LayoutParams.FILL_PARENT,
                            TableLayout.LayoutParams.WRAP_CONTENT));

                    for (int i = 0; i < vehicles.length; i++) {
                        if (vehicles[i].getDeletedWithHistory() == 1) {
                            continue;
                        }

                        TableRow tr = new TableRow(myContext);
                        tr.setLayoutParams(new TableRow.LayoutParams(
                                TableRow.LayoutParams.FILL_PARENT,
                                TableRow.LayoutParams.WRAP_CONTENT));
                        tr.setTag(tag);
                        tag++;
                        tr.setClickable(true);
                        tr.setOnClickListener(clickListener);

                        TextView v_make = new TextView(myContext);
                        v_make.setText(vehicles[i].getMake());
                        v_make.setLayoutParams(new TableRow.LayoutParams(300, 150));
                        tr.addView(v_make);

                        TextView v_model = new TextView(myContext);
                        v_model.setText(vehicles[i].getModel());
                        v_model.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
                        tr.addView(v_model);

                        TextView v_license = new TextView(myContext);
                        v_license.setText(vehicles[i].getLicensePlate());
                        v_license.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
                        tr.addView(v_license);

                        tl.addView(tr, new TableLayout.LayoutParams(
                                TableLayout.LayoutParams.FILL_PARENT,
                                TableLayout.LayoutParams.WRAP_CONTENT));
                    }
                }
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                Snackbar.make(rootView, t.getLocalizedMessage(), Snackbar.LENGTH_LONG).show();
            }
        });
    }

    public void onAddUpdateVehicleFailed() {
        Snackbar.make(rootView, "Invalid add/update vehicle parameters", Snackbar.LENGTH_LONG).show();
    }

    public boolean validate(Vehicle vehicle) {
        boolean valid = true;

        if (vehicle.getMake().isEmpty()) {
            sprof_make.setError("Enter a valid vehicle make");
            valid = false;
        } else {
            sprof_make.setError(null);
        }

        if (vehicle.getModel().isEmpty()) {
            sprof_model.setError("Enter a valid vehicle model");
            valid = false;
        } else {
            sprof_model.setError(null);
        }

        if (vehicle.getColor().isEmpty()) {
            sprof_color.setError("Enter a valid vehicle color");
            valid = false;
        } else {
            sprof_color.setError(null);
        }

        if (vehicle.getYear().isEmpty() || vehicle.getYear().length() < 4) {
            sprof_year.setError("Enter a valid vehicle year");
            valid = false;
        } else {
            sprof_year.setError(null);
        }

        if (vehicle.getLicensePlate().isEmpty()) {
            sprof_licenseplate.setError("Enter a valid vehicle license plate");
            valid = false;
        } else {
            sprof_licenseplate.setError(null);
        }

        return valid;
    }

    public void addVehicle(final Vehicle vehicle) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RequestInterface requestInterface = retrofit.create(RequestInterface.class);

        ServerRequest request = new ServerRequest();
        request.setOperation(Constants.ADD_VEHICLE_OPERATION);
        request.setUser(user);
        request.setVehicle(vehicle);
        Call<ServerResponse> response = requestInterface.operation(request);

        response.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, retrofit2.Response<ServerResponse> response) {
                ServerResponse resp = response.body();

                if(resp.getResult().equals(Constants.SUCCESS)){
                    TableRow tr = new TableRow(myContext);
                    tr.setLayoutParams(new TableRow.LayoutParams(
                            TableRow.LayoutParams.FILL_PARENT,
                            TableRow.LayoutParams.WRAP_CONTENT));
                    tr.setTag(tag);
                    tag++;
                    tr.setClickable(true);
                    tr.setOnClickListener(clickListener);

                    TextView v_make = new TextView(myContext);
                    v_make.setText(vehicle.getMake());
                    v_make.setLayoutParams(new TableRow.LayoutParams(300, 150));
                    tr.addView(v_make);

                    TextView v_model = new TextView(myContext);
                    v_model.setText(vehicle.getModel());
                    v_model.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
                    tr.addView(v_model);

                    TextView v_license = new TextView(myContext);
                    v_license.setText(vehicle.getLicensePlate());
                    v_license.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
                    tr.addView(v_license);

                    tl.addView(tr, new TableLayout.LayoutParams(
                            TableLayout.LayoutParams.FILL_PARENT,
                            TableLayout.LayoutParams.WRAP_CONTENT));

                    Vehicle[] arr = new Vehicle[vehicles.length + 1];
                    for (int i = 0; i < vehicles.length; i++) {
                        arr[i] = vehicles[i];
                    }
                    arr[vehicles.length] = vehicle;
                    vehicles = arr;
                }
                Snackbar.make(rootView, resp.getMessage(), Snackbar.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                Snackbar.make(rootView, t.getLocalizedMessage(), Snackbar.LENGTH_LONG).show();
            }
        });
    }

    public void editVehicle() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RequestInterface requestInterface = retrofit.create(RequestInterface.class);

        ServerRequest request = new ServerRequest();
        request.setOperation(Constants.UPDATE_VEHICLE_OPERATION);
        request.setUser(user);
        request.setVehicle(vehicles[selectedIndex-1]);
        Call<ServerResponse> response = requestInterface.operation(request);

        response.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, retrofit2.Response<ServerResponse> response) {
                ServerResponse resp = response.body();

                if(resp.getResult().equals(Constants.SUCCESS)){
                }
                Snackbar.make(rootView, resp.getMessage(), Snackbar.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                Snackbar.make(rootView, t.getLocalizedMessage(), Snackbar.LENGTH_LONG).show();
            }
        });
    }

    public void deleteVehicle() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RequestInterface requestInterface = retrofit.create(RequestInterface.class);

        ServerRequest request = new ServerRequest();
        request.setOperation(Constants.DELETE_VEHICLE_OPERATION);
        request.setUser(user);
        Vehicle v = getVehicle(selectedLicensePlate);
        request.setVehicle(v);
        Call<ServerResponse> response = requestInterface.operation(request);

        response.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, retrofit2.Response<ServerResponse> response) {
                ServerResponse resp = response.body();

                if(resp.getResult().equals(Constants.SUCCESS)){
                    vehicles[selectedIndex-1] = new Vehicle();
                    TableRow tr = (TableRow) tl.findViewWithTag(selectedIndex);
                    tl.removeView(tr);
                    selectedIndex = -1;
                    tag--;
                }
                Snackbar.make(rootView, resp.getMessage(), Snackbar.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                Snackbar.make(rootView, t.getLocalizedMessage(), Snackbar.LENGTH_LONG).show();
            }
        });
    }

    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Integer index = (Integer) v.getTag();

            if (index != null) {
                selectRow(index);
            }
        }
    };

    private void selectRow(int index) {
        if (index != selectedIndex) {
            if (selectedIndex >= 0) {
                deselectRow(selectedIndex);
            }
            TableRow tr = (TableRow) tl.findViewWithTag(index);
            tr.setBackgroundColor(Color.LTGRAY);
            selectedIndex = index;
            TextView tv = (TextView) tr.getChildAt(0);
            selectedMake = tv.getText().toString();
            tv = (TextView) tr.getChildAt(1);
            selectedModel = tv.getText().toString();
            tv = (TextView) tr.getChildAt(2);
            selectedLicensePlate = tv.getText().toString();

            Vehicle v = getVehicle(selectedLicensePlate);
            sprof_make.setText(v.getMake());
            sprof_model.setText(v.getModel());
            sprof_color.setText(v.getColor());
            sprof_year.setText(v.getYear());
            sprof_licenseplate.setText(v.getLicensePlate());
        }
    }

    private void deselectRow(int index) {
        if (index >= 0) {
            TableRow tr = (TableRow) tl.findViewWithTag(index);
            if (tr != null) {
                tr.setBackgroundColor(Color.parseColor("#F2F7F2"));
            }
        }
    }

    public Vehicle getVehicle(String licensePlate) {
        for (int i = 0; i < vehicles.length; i++) {
            if (licensePlate.equals(vehicles[i].getLicensePlate()) && vehicles[i].getDeletedWithHistory() == 0) {
                return vehicles[i];
            }
        }
        return new Vehicle();
    }
}

