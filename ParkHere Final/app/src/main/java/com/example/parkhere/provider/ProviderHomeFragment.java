package com.example.parkhere.provider;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.parkhere.R;
import com.example.parkhere.objects.ParkingSpace;
import com.example.parkhere.objects.User;
import com.example.parkhere.server.Constants;
import com.example.parkhere.server.RequestInterface;
import com.example.parkhere.server.ServerRequest;
import com.example.parkhere.server.ServerResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ProviderHomeFragment extends Fragment {
    private User user;
    private static final String USER_KEY = "user";
    private Button add_space_button, edit_button, delete_button;
    private FragmentActivity myContext;
    private TableLayout tl;
    private View rootView;
    private String selectedAddress = "";
    private int selectedIndex = -1;
    private int tag = 1;

    public static ProviderHomeFragment newInstance(User user) {
        ProviderHomeFragment fragment = new ProviderHomeFragment();
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

        rootView = inflater.inflate(R.layout.fragment_provider_home, container, false);

        add_space_button = (Button) rootView.findViewById(R.id.provider_home_add_button);
        edit_button = (Button) rootView.findViewById(R.id.provider_home_edit_button);
        delete_button = (Button) rootView.findViewById(R.id.provider_home_delete_button);

        tl = (TableLayout) rootView.findViewById(R.id.provider_home_table);

        getSpaces();

        add_space_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Fragment fragment = AddParkingSpaceFragment.newInstance(user);
                FragmentManager fragmentManager = myContext.getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
            }
        });

        edit_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (selectedAddress.equals("")){
                    Snackbar.make(rootView, "Must select a parking space", Snackbar.LENGTH_LONG).show();
                } else {
                    editSpace();
                }
            }
        });

        delete_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (selectedAddress.equals("")){
                    Snackbar.make(rootView, "Must select a parking space", Snackbar.LENGTH_LONG).show();
                } else {
                    LayoutInflater li = LayoutInflater.from(myContext);
                    View customView = li.inflate(R.layout.popup_delete_parking_space, null);

                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(myContext);
                    alertDialogBuilder.setView(customView);
                    final AlertDialog alertDialog = alertDialogBuilder.create();

                    TextView deleteText = (TextView) customView.findViewById(R.id.popup_delete_ps_text);
                    deleteText.setText("Are you sure you want to delete this parking space at " + selectedAddress + "?");

                    ImageButton closeButton = (ImageButton) customView.findViewById(R.id.popup_delete_ps_close);
                    closeButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            alertDialog.dismiss();
                        }
                    });

                    Button deleteButton = (Button) customView.findViewById(R.id.popup_delete_ps_button);
                    deleteButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            deleteSpace();
                            alertDialog.dismiss();
                        }
                    });

                    alertDialog.show();
                }
            }
        });

        return rootView;
    }

    public void editSpace() {
        ParkingSpace p = getParkingSpace(selectedAddress);
        Fragment fragment = EditParkingSpaceFragment.newInstance(user, p);
        FragmentManager fragmentManager = myContext.getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
    }

    public void deleteSpace() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RequestInterface requestInterface = retrofit.create(RequestInterface.class);

        ServerRequest request = new ServerRequest();
        request.setOperation(Constants.DELETE_SPACE_OPERATION);
        ParkingSpace p = getParkingSpace(selectedAddress);
        request.setParkingSpace(p);
        Call<ServerResponse> response = requestInterface.operation(request);

        response.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, retrofit2.Response<ServerResponse> response) {
                ServerResponse resp = response.body();

                if(resp.getResult().equals(Constants.SUCCESS)){
                    Fragment fragment = ProviderHomeFragment.newInstance(user);
                    FragmentManager fragmentManager = myContext.getSupportFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
                }
                Snackbar.make(rootView, resp.getMessage(), Snackbar.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                Snackbar.make(rootView, t.getLocalizedMessage(), Snackbar.LENGTH_LONG).show();
            }
        });
    }

    public void getSpaces() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RequestInterface requestInterface = retrofit.create(RequestInterface.class);

        ServerRequest request = new ServerRequest();
        request.setOperation(Constants.GET_SPACES_OPERATION);
        request.setUser(user);
        Call<ServerResponse> response = requestInterface.operation(request);

        response.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, retrofit2.Response<ServerResponse> response) {
                ServerResponse resp = response.body();

                if(resp.getResult().equals(Constants.SUCCESS)){
                    ParkingSpace[] p = resp.getParkingSpaces();
                    user.setProviderParkingSpaces(p);

                    boolean allDeleted = true;
                    for (int i = 0; i < p.length; i++) {
                        if (p[i].getDeletedWithHistory() == 0) {
                            allDeleted = false;
                            break;
                        }
                    }

                    if (allDeleted == true) {
                        TextView provider_home_table_label = (TextView) rootView.findViewById(R.id.provider_home_table_label);
                        provider_home_table_label.setText("You don't have any parking spaces currently posted; be sure to add one today!");
                        edit_button.setVisibility(View.GONE);
                        delete_button.setVisibility(View.GONE);
                        return;
                    }

                    TableRow tr_head = new TableRow(myContext);
                    tr_head.setBackgroundColor(Color.GRAY);
                    tr_head.setLayoutParams(new TableRow.LayoutParams(
                            TableRow.LayoutParams.FILL_PARENT,
                            TableRow.LayoutParams.WRAP_CONTENT));

                    TextView address = new TextView(myContext);
                    address.setText("ADDRESS");
                    address.setTextColor(Color.WHITE);
                    address.setPadding(5, 5, 5, 5);
                    tr_head.addView(address);

                    TextView numRes = new TextView(myContext);
                    numRes.setText("# RESERVATIONS");
                    numRes.setTextColor(Color.WHITE);
                    numRes.setPadding(5, 5, 5, 5);
                    tr_head.addView(numRes);

                    TextView avgRating = new TextView(myContext);
                    avgRating.setText("AVERAGE RATING");
                    avgRating.setTextColor(Color.WHITE);
                    avgRating.setPadding(5, 5, 5, 5);
                    tr_head.addView(avgRating);

                    tl.addView(tr_head, new TableLayout.LayoutParams(
                            TableLayout.LayoutParams.FILL_PARENT,
                            TableLayout.LayoutParams.WRAP_CONTENT));

                    for (int i = 0; i < p.length; i++) {
                        if (p[i].getDeletedWithHistory() == 1) {
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

                        TextView p_address = new TextView(myContext);
                        p_address.setText(p[i].getAddress());
                        p_address.setLayoutParams(new TableRow.LayoutParams(300, 150));
                        tr.addView(p_address);

                        TextView p_numRes = new TextView(myContext);
                        p_numRes.setText(Integer.toString(p[i].getNumReservations()));
                        p_numRes.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
                        tr.addView(p_numRes);

                        TextView p_avgRating = new TextView(myContext);
                        String avg = Double.toString(p[i].getAvgRating());
                        p_avgRating.setText(avg.substring(0, 3));
                        p_avgRating.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
                        tr.addView(p_avgRating);

                        tl.addView(tr, new TableLayout.LayoutParams(
                                TableLayout.LayoutParams.FILL_PARENT,
                                TableLayout.LayoutParams.WRAP_CONTENT));
                    }
                } else {
                    TextView provider_home_table_label = (TextView) rootView.findViewById(R.id.provider_home_table_label);
                    provider_home_table_label.setText("You haven't listed any parking spaces yet; be sure to add one today!");
                    edit_button.setVisibility(View.GONE);
                    delete_button.setVisibility(View.GONE);
                }
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
            TableRow tr = (TableRow) tl.getChildAt(index);
            tr.setBackgroundColor(Color.LTGRAY);
            selectedIndex = index;
            TextView tv = (TextView) tr.getChildAt(0);
            selectedAddress = tv.getText().toString();
        }
    }

    private void deselectRow(int index) {
        if (index >= 0) {
            TableRow tr = (TableRow) tl.getChildAt(index);
            tr.setBackgroundColor(Color.parseColor("#F2F7F2"));
        }
    }

    public ParkingSpace getParkingSpace(String address) {
        ParkingSpace[] p = user.getProviderParkingSpaces();
        for (int i = 0; i < p.length; i++) {
            if (address.equals(p[i].getAddress())) {
                return p[i];
            }
        }
        return new ParkingSpace();
    }
}
