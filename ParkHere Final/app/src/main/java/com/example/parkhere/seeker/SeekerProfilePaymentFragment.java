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
import com.example.parkhere.objects.BillingAddress;
import com.example.parkhere.objects.SeekerPayment;
import com.example.parkhere.objects.User;
import com.example.parkhere.server.Constants;
import com.example.parkhere.server.RequestInterface;
import com.example.parkhere.server.ServerRequest;
import com.example.parkhere.server.ServerResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SeekerProfilePaymentFragment extends Fragment {
    private User user;
    private SeekerPayment[] payments;
    private static final String USER_KEY = "user";
    private static final String SEEKER_PAYMENTS_KEY = "payments";
    private View rootView;
    private EditText sprof_ccfname, sprof_cclname, sprof_ccname, sprof_ccnumber, sprof_ccexpmonth, sprof_ccexpyear;
    private EditText sprof_address, sprof_city, sprof_state, sprof_zipcode, sprof_country;
    private TableLayout tl;
    private Button sprof_add_payment_button, sprof_edit_payment_button, sprof_delete_payment_button;
    private FragmentActivity myContext;
    private int tag = 1;
    private String selectedName = "";
    private String selectedNumber = "";
    private int selectedIndex = -1;

    @Override
    public void onAttach(Activity activity) {
        myContext=(FragmentActivity) activity;
        super.onAttach(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        user = (User) getArguments().getSerializable(USER_KEY);
        payments = (SeekerPayment[]) getArguments().getSerializable(SEEKER_PAYMENTS_KEY);

        getCurrInfo();

        rootView = inflater.inflate(R.layout.fragment_seeker_profile_payment, container, false);

        sprof_ccfname = (EditText) rootView.findViewById(R.id.sprof_ccfname);
        sprof_cclname =(EditText) rootView.findViewById(R.id.sprof_cclname);
        sprof_ccname =(EditText) rootView.findViewById(R.id.sprof_ccname);
        sprof_ccnumber = (EditText) rootView.findViewById(R.id.sprof_ccnumber);
        sprof_ccexpmonth = (EditText) rootView.findViewById(R.id.sprof_ccexpmonth);
        sprof_ccexpyear =(EditText) rootView.findViewById(R.id.sprof_ccexpyear);

        sprof_address = (EditText) rootView.findViewById(R.id.sprof_address);
        sprof_city = (EditText) rootView.findViewById(R.id.sprof_city);
        sprof_state = (EditText) rootView.findViewById(R.id.sprof_state);
        sprof_zipcode = (EditText) rootView.findViewById(R.id.sprof_zipcode);
        sprof_country = (EditText) rootView.findViewById(R.id.sprof_country);

        tl = (TableLayout) rootView.findViewById(R.id.sprof_payment_table);

        sprof_add_payment_button = (Button) rootView.findViewById(R.id.sprof_add_payment_button);
        sprof_edit_payment_button = (Button) rootView.findViewById(R.id.sprof_edit_payment_button);
        sprof_edit_payment_button.setVisibility(View.GONE);//REMOVE LATER
        sprof_delete_payment_button = (Button) rootView.findViewById(R.id.sprof_delete_payment_button);

        sprof_add_payment_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String ccfname = sprof_ccfname.getText().toString();
                String cclname = sprof_cclname.getText().toString();
                String ccname = sprof_ccname.getText().toString();
                String ccnumber = sprof_ccnumber.getText().toString();
                String ccexpmonth = sprof_ccexpmonth.getText().toString();
                String ccexpyear = sprof_ccexpyear.getText().toString();

                SeekerPayment seekerPayment = new SeekerPayment();
                seekerPayment.setFirstName(ccfname);
                seekerPayment.setLastName(cclname);
                seekerPayment.setName(ccname);
                seekerPayment.setExpMonth(ccexpmonth);
                seekerPayment.setExpYear(ccexpyear);

                String address = sprof_address.getText().toString();
                String city = sprof_city.getText().toString();
                String state = sprof_state.getText().toString();
                String zipCode = sprof_zipcode.getText().toString();
                String country = sprof_country.getText().toString();

                BillingAddress billingAddress = new BillingAddress();
                billingAddress.setAddress(address);
                billingAddress.setCity(city);
                billingAddress.setState(state);
                billingAddress.setZipCode(zipCode);
                billingAddress.setCountry(country);

                if (!validate(seekerPayment, billingAddress)) {
                    onAddUpdatePaymentFailed();
                    return;
                }

                int ccNum = Integer.parseInt(ccnumber.substring(ccnumber.length()-4, ccnumber.length()));
                seekerPayment.setNumber(Integer.toString(ccNum));
                seekerPayment.setBillingAddress(billingAddress);

                SeekerPayment p = getPayment(ccname, seekerPayment.getNumber());
                if (p.getName() == null) {
                    addPayment(seekerPayment);
                } else {
                    Snackbar.make(rootView, "Payment with this cc name and number already exists; cannot re-add", Snackbar.LENGTH_LONG).show();
                }
            }
        });

        sprof_delete_payment_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (selectedName.equals("")){
                    Snackbar.make(rootView, "Must select a payment", Snackbar.LENGTH_LONG).show();
                } else if (tag == 2) {
                    Snackbar.make(rootView, "Must add another payment option before deleting your only listed payment", Snackbar.LENGTH_LONG).show();
                } else {
                    LayoutInflater li = LayoutInflater.from(myContext);
                    View customView = li.inflate(R.layout.popup_delete_payment, null);

                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(myContext);
                    alertDialogBuilder.setView(customView);
                    final AlertDialog alertDialog = alertDialogBuilder.create();

                    TextView deleteText = (TextView) customView.findViewById(R.id.popup_delete_payment_text);
                    deleteText.setText("Are you sure you want to delete this payment: " + selectedName + " Ending In " +
                            selectedNumber + "?");

                    ImageButton closeButton = (ImageButton) customView.findViewById(R.id.popup_delete_payment_close);
                    closeButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            alertDialog.dismiss();
                        }
                    });

                    Button deleteButton = (Button) customView.findViewById(R.id.popup_delete_payment_button);
                    deleteButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            deletePayment();
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
                    payments = resp.getSeekerPayments();

                    TableRow tr_head = new TableRow(myContext);
                    tr_head.setBackgroundColor(Color.GRAY);
                    tr_head.setLayoutParams(new TableRow.LayoutParams(
                            TableRow.LayoutParams.FILL_PARENT,
                            TableRow.LayoutParams.WRAP_CONTENT));

                    TextView name = new TextView(myContext);
                    name.setText("CC NAME");
                    name.setTextColor(Color.WHITE);
                    name.setPadding(5, 5, 5, 5);
                    tr_head.addView(name);

                    TextView number = new TextView(myContext);
                    number.setText("CC NUM");
                    number.setTextColor(Color.WHITE);
                    number.setPadding(5, 5, 5, 5);
                    tr_head.addView(number);

                    TextView billing = new TextView(myContext);
                    billing.setText("BILLING");
                    billing.setTextColor(Color.WHITE);
                    billing.setPadding(5, 5, 5, 5);
                    tr_head.addView(billing);

                    tl.addView(tr_head, new TableLayout.LayoutParams(
                            TableLayout.LayoutParams.FILL_PARENT,
                            TableLayout.LayoutParams.WRAP_CONTENT));

                    for (int i = 0; i < payments.length; i++) {
                        if (payments[i].getDeletedWithHistory() == 1) {
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

                        TextView p_name = new TextView(myContext);
                        p_name.setText(payments[i].getName());
                        p_name.setLayoutParams(new TableRow.LayoutParams(300, 150));
                        tr.addView(p_name);

                        TextView p_number = new TextView(myContext);
                        p_number.setText("XXXX..." + payments[i].getNumber());
                        p_number.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
                        tr.addView(p_number);

                        TextView p_billing = new TextView(myContext);
                        p_billing.setText(payments[i].getBillingAddress().getAddress());
                        p_billing.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
                        tr.addView(p_billing);

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

    public void onAddUpdatePaymentFailed() {
        Snackbar.make(rootView, "Invalid add/update payment parameters", Snackbar.LENGTH_LONG).show();
    }

    public boolean validate(SeekerPayment seekerPayment, BillingAddress billingAddress) {
        boolean valid = true;

        //SEEKER PAYMENT CHECKS
        if (seekerPayment.getFirstName().isEmpty()) {
            sprof_ccfname.setError("Enter a valid cardholder first name");
            valid = false;
        } else {
            sprof_ccfname.setError(null);
        }

        if (seekerPayment.getLastName().isEmpty()) {
            sprof_cclname.setError("Enter a valid cardholder last name");
            valid = false;
        } else {
            sprof_cclname.setError(null);
        }

        if (seekerPayment.getName().isEmpty()) {
            sprof_ccname.setError("Enter a valid card company name");
            valid = false;
        } else {
            sprof_ccname.setError(null);
        }

        int length = sprof_ccnumber.getText().toString().length();
        if (sprof_ccnumber.getText().toString().isEmpty() || length < 10 || length > 19) {
            sprof_ccnumber.setError("Enter a valid card number");
            valid = false;
        } else {
            sprof_ccnumber.setError(null);
        }

        if (seekerPayment.getExpMonth().isEmpty()) {
            sprof_ccexpmonth.setError("Enter a valid card expiration month");
            valid = false;
        } else {
            sprof_ccexpmonth.setError(null);
        }

        if (seekerPayment.getExpYear().isEmpty() || seekerPayment.getExpYear().length() != 4) {
            sprof_ccexpyear.setError("Enter a valid card expiration year");
            valid = false;
        } else {
            sprof_ccexpyear.setError(null);
        }

        //BILLING ADDRESS CHECKS
        if (billingAddress.getAddress().isEmpty()) {
            sprof_address.setError("Enter a valid billing address");
            valid = false;
        } else {
            sprof_address.setError(null);
        }

        if (billingAddress.getCity().isEmpty()) {
            sprof_city.setError("Enter a valid billing city");
            valid = false;
        } else {
            sprof_city.setError(null);
        }

        if (billingAddress.getState().isEmpty() || billingAddress.getState().length() != 2) {
            sprof_state.setError("Enter a valid billing state (Ex: CA)");
            valid = false;
        } else {
            sprof_state.setError(null);
        }

        if (billingAddress.getZipCode().isEmpty() || billingAddress.getZipCode().length() != 5) {
            sprof_zipcode.setError("Enter a valid 5 digit billing zip code");
            valid = false;
        } else {
            sprof_zipcode.setError(null);
        }

        if (billingAddress.getCountry().isEmpty() || !billingAddress.getCountry().equals("USA")) {
            sprof_country.setError("Enter a valid billing country (USA)");
            valid = false;
        } else {
            sprof_country.setError(null);
        }

        return valid;
    }

    public void addPayment(final SeekerPayment payment) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RequestInterface requestInterface = retrofit.create(RequestInterface.class);

        ServerRequest request = new ServerRequest();
        request.setOperation(Constants.ADD_SEEKER_PAYMENT_OPERATION);
        request.setUser(user);
        request.setSeekerPayment(payment);
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

                    TextView p_name = new TextView(myContext);
                    p_name.setText(payment.getName());
                    p_name.setLayoutParams(new TableRow.LayoutParams(300, 150));
                    tr.addView(p_name);

                    TextView p_number = new TextView(myContext);
                    p_number.setText("XXXX..." + payment.getNumber());
                    p_number.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
                    tr.addView(p_number);

                    TextView p_billing = new TextView(myContext);
                    p_billing.setText(payment.getBillingAddress().getAddress());
                    p_billing.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
                    tr.addView(p_billing);

                    tl.addView(tr, new TableLayout.LayoutParams(
                            TableLayout.LayoutParams.FILL_PARENT,
                            TableLayout.LayoutParams.WRAP_CONTENT));

                    SeekerPayment[] arr = new SeekerPayment[payments.length + 1];
                    for (int i = 0; i < payments.length; i++) {
                        arr[i] = payments[i];
                    }
                    arr[payments.length] = payment;
                    payments = arr;
                }
                Snackbar.make(rootView, resp.getMessage(), Snackbar.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                Snackbar.make(rootView, t.getLocalizedMessage(), Snackbar.LENGTH_LONG).show();
            }
        });
    }

    public void deletePayment() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RequestInterface requestInterface = retrofit.create(RequestInterface.class);

        ServerRequest request = new ServerRequest();
        request.setOperation(Constants.DELETE_SEEKER_PAYMENT_OPERATION);
        request.setUser(user);
        SeekerPayment p = getPayment(selectedName, selectedNumber);
        request.setSeekerPayment(p);
        Call<ServerResponse> response = requestInterface.operation(request);

        response.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, retrofit2.Response<ServerResponse> response) {
                ServerResponse resp = response.body();

                if(resp.getResult().equals(Constants.SUCCESS)){
                    payments[selectedIndex-1] = new SeekerPayment();
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
            selectedName = tv.getText().toString();
            tv = (TextView) tr.getChildAt(1);
            selectedNumber = tv.getText().toString();
            selectedNumber = selectedNumber.substring(selectedNumber.length()-4, selectedNumber.length());
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

    public SeekerPayment getPayment(String name, String number) {
        for (int i = 0; i < payments.length; i++) {
            if (name.equals(payments[i].getName()) && number.equals(payments[i].getNumber()) && payments[i].getDeletedWithHistory() == 0) {
                return payments[i];
            }
        }
        return new SeekerPayment();
    }
}

