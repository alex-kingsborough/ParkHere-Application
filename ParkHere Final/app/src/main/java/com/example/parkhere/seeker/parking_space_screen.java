package com.example.parkhere.seeker;

import android.app.Application;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.parkhere.R;
import com.example.parkhere.objects.Car;
import com.example.parkhere.objects.ParkingReservation;
import com.example.parkhere.objects.ParkingSpace;
import com.example.parkhere.objects.Payment;
import com.example.parkhere.objects.Rating;
import com.example.parkhere.objects.SeekerPayment;
import com.example.parkhere.objects.User;
import com.example.parkhere.server.Constants;
import com.example.parkhere.server.RequestInterface;
import com.example.parkhere.server.ServerRequest;
import com.example.parkhere.server.ServerResponse;

import org.w3c.dom.Text;

import java.io.InputStream;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class parking_space_screen extends AppCompatActivity {

    private User user;
    private ParkingSpace parkingSpace;
    private double currLat;
    private double currLong;

    private String[] dateRange;
    private int startTime;
    private int endTime;

    private ImageView imageView;

    private Rating[] ratings;
    private Payment[] payments;
    private Car[] cars;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        parkingSpace = (ParkingSpace) getIntent().getSerializableExtra("parkingSpot");
        currLat = getIntent().getExtras().getDouble("currLat");
        currLong = getIntent().getExtras().getDouble("currLong");
        user = (User) getIntent().getExtras().getSerializable("user");
        dateRange = (String[]) getIntent().getExtras().getSerializable("dateRange");
        startTime = getIntent().getExtras().getInt("hourStart");
        endTime = getIntent().getExtras().getInt("hourEnd");

        setContentView(R.layout.parking_space_screen);
        TextView tv1 = (TextView)findViewById(R.id.space_address_editText);
        tv1.setText(parkingSpace.getAddress());
        TextView tv2 = (TextView)findViewById(R.id.space_editText_2);
        tv2.setText("$" + String.format("%.2f",parkingSpace.getTotalPrice()));
        TextView tv3 = (TextView)findViewById(R.id.space_distance_editText);

        final float[] temp = new float[1];
        Location.distanceBetween(parkingSpace.getLat(), parkingSpace.getLong(), currLat, currLong, temp);
        float m = temp[0];
        double miles = m / 1609.344; // convert meters to miles
        tv3.setText(String.format("%.2f",miles));

        TextView tv4 = (TextView)findViewById(R.id.space_rating_editText);
        tv4.setText(String.format("%.1f",parkingSpace.getRating())+"/5.0");
        TextView tv5 = (TextView)findViewById(R.id.space_Size_editText);
        tv5.setText(parkingSpace.getSize());
        TextView tv6 = (TextView)findViewById(R.id.space_Permit_editText);
        if(parkingSpace.getPermit() == 1){
            tv6.setText("Permit Required");}else{tv6.setText("No Permit Required");}
        TextView tv7 = (TextView)findViewById(R.id.space_Cancel_editText);
        tv7.setText(parkingSpace.getCancellationPolicy());

        imageView = (ImageView) findViewById(R.id.imageView);
        String imgURL = "http://10.0.2.2/parkhere/" + parkingSpace.getImage();
        new parking_space_screen.DownloadImageTask(imageView).execute(imgURL);

        TextView tv8 = (TextView)findViewById(R.id.space_ProviderRate_editText);
        tv8.setText(String.format("%.1f",parkingSpace.getpRating())+"/5.0");
        TextView tv9 = (TextView)findViewById(R.id.comments_editText);
        tv9.setText(parkingSpace.getAdditional());

        final TextView tv10 = (TextView)findViewById(R.id.Rate_editText);
        tv10.setMovementMethod(new ScrollingMovementMethod());

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RequestInterface requestInterface = retrofit.create(RequestInterface.class);
        ServerRequest request = new ServerRequest();
        request.setOperation(Constants.GET_PARKING_SPACE_RATINGS_OPERATION);
        System.out.println("user id = "+user.getId());
        request.setUser(user);
        request.setParkingSpace(parkingSpace);
        Call<ServerResponse> response = requestInterface.operation(request);

        response.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                ServerResponse resp = response.body();
                System.out.println("message = "+resp.getMessage());
                ratings = resp.getRatings();
                //System.out.println("ratings size = "+ratings.length);
                String reviews = "";
                if (ratings != null) {
                    for (int i = 0; i < ratings.length; i++) {
                        Rating curr = ratings[i];
                        reviews = reviews + "rating: " + curr.getRating() + ".0/5.0" + "\n" + "review: " + curr.getComment() + "\n\n";
                    }
                }
                //tv10.setText("RATINGS AND STUFF. WHERE DOES THIS GO AND HOW DOES IT DEAL WITH MUCH STRINGS SO MANY STRINGS AND IS THIS IN ADDITION TO THE NUMERICAL RATING? IT SHOULDN'T HAVE REPLACED IT RIGHT? K COOL. THIS SHOULD BE LONG ENOUGH. K BYE");
                tv10.setText(reviews);
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                System.out.println("onFailure in parking space screen");
                //Snackbar.make(register_scroll_view, t.getLocalizedMessage(), Snackbar.LENGTH_LONG).show();
            }
        });



        Button btn = (Button)findViewById(R.id.reserve_space_btn);

//        System.out.println("button btn: " + btn.getText());
//        final RadioButton newCard = (RadioButton) findViewById(R.id.new_card_rb);
//        final EditText name = (EditText) findViewById(R.id.popup_name);
//        final EditText creditCard = (EditText) findViewById(R.id.popup_credit_card);
//        final EditText cvv = (EditText) findViewById(R.id.popup_CVV);
//        final EditText expiration = (EditText) findViewById(R.id.popup_expiration_date);
//        System.out.println("newCardis: " + newCard.getText());

        btn.setOnClickListener(
                new View.OnClickListener(){
                    @Override
                    public void onClick(View v){
                        LayoutInflater li = LayoutInflater.from(parking_space_screen.this);
                        final View customView = li.inflate(R.layout.reserve_parking_space, null);

                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(parking_space_screen.this);
                        alertDialogBuilder.setView(customView);
                        final AlertDialog alertDialog = alertDialogBuilder.create();

                        // get all the credit cards they have saved
                        Retrofit retrofit = new Retrofit.Builder()
                                .baseUrl(Constants.BASE_URL)
                                .addConverterFactory(GsonConverterFactory.create())
                                .build();

                        RequestInterface requestInterface = retrofit.create(RequestInterface.class);
                        ServerRequest request = new ServerRequest();
                        request.setOperation(Constants.CARDS);
                        System.out.println("user id = "+user.getId());
                        request.setUser(user);
                        Call<ServerResponse> response = requestInterface.operation(request);

                        response.enqueue(new Callback<ServerResponse>() {
                            @Override
                            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                                ServerResponse resp = response.body();
                                System.out.println("message = "+resp.getMessage());
                                // TODO test: should work now
                                payments = resp.getPaymentInformation();
                                //System.out.println("CARDS SIZE = "+payments.length);
                                int[] card = new int[payments.length];
                                Spinner spinner = (Spinner)customView.findViewById(R.id.card_spinner);
                                ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_spinner_item, android.R.id.text1);
                                spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                spinner.setAdapter(spinnerAdapter);
                                for(int i = 0; i < payments.length; i++) {
                                    card[i] = payments[i].getLast4digits();
                                    spinnerAdapter.add(card[i] + "");
                                    spinnerAdapter.notifyDataSetChanged();
                                }
                            }

                            @Override
                            public void onFailure(Call<ServerResponse> call, Throwable t) {
                                System.out.println("onFailure in parking space screen");
                                //Snackbar.make(register_scroll_view, t.getLocalizedMessage(), Snackbar.LENGTH_LONG).show();
                            }
                        });

                        Retrofit retrofit2 = new Retrofit.Builder()
                                .baseUrl(Constants.BASE_URL)
                                .addConverterFactory(GsonConverterFactory.create())
                                .build();

                        RequestInterface requestInterface2 = retrofit.create(RequestInterface.class);
                        ServerRequest request2 = new ServerRequest();
                        request2.setOperation(Constants.VEHICLE_OPTIONS);
                        System.out.println("user id = "+user.getId());
                        request2.setUser(user);
                        Call<ServerResponse> response2 = requestInterface2.operation(request2);

                        response2.enqueue(new Callback<ServerResponse>() {
                            @Override
                            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response2) {
                                ServerResponse resp = response2.body();
                                System.out.println("message = "+resp.getMessage());
                                cars = resp.getVehicleOptions();
                                String[] vehicle = new String[cars.length];
                                Spinner spinner2 = (Spinner)customView.findViewById(R.id.new_spinner);
                                ArrayAdapter<String> spinner2Adapter = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_spinner_item, android.R.id.text1);
                                spinner2Adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                spinner2.setAdapter(spinner2Adapter);
                                for(int i = 0; i < cars.length; i++) {
                                    vehicle[i] = cars[i].getLicensePlate();
                                    spinner2Adapter.add(vehicle[i]);
                                    spinner2Adapter.notifyDataSetChanged();
                                }
                            }

                            @Override
                            public void onFailure(Call<ServerResponse> call, Throwable t) {
                                System.out.println("onFailure in parking space screen: vehicle");
                                //Snackbar.make(register_scroll_view, t.getLocalizedMessage(), Snackbar.LENGTH_LONG).show();
                            }
                        });

/*
                        final EditText editName, editCard, editCVV, editExpir;
                        editName = (EditText)customView.findViewById(R.id.popup_name);
                        editCard = (EditText)customView.findViewById(R.id.popup_credit_card);
                        editCVV = (EditText)customView.findViewById(R.id.popup_CVV);
                        editExpir = (EditText)customView.findViewById(R.id.popup_expiration_date);
*/
                        final RadioButton currCard = (RadioButton)customView.findViewById(R.id.curr_card_rb);
                        final RadioButton newCard = (RadioButton)customView.findViewById(R.id.new_card_rb);
                        currCard.setChecked(true);
/*

                        editName.setFocusable(false);
                        editCard.setFocusable(false);
                        editCVV.setFocusable(false);
                        editExpir.setFocusable(false);
*/

                        currCard.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                  /*              EditText editName, editCard, editCVV, editExpir;
                                editName = (EditText)customView.findViewById(R.id.popup_name);
                                editCard = (EditText)customView.findViewById(R.id.popup_credit_card);
                                editCVV = (EditText)customView.findViewById(R.id.popup_CVV);
                                editExpir = (EditText)customView.findViewById(R.id.popup_expiration_date);
                                RadioButton currCard = (RadioButton) customView.findViewById(R.id.curr_card_rb);
                                RadioButton newCard = (RadioButton) customView.findViewById(R.id.new_card_rb);
*/
                                currCard.toggle();
                                newCard.setChecked(false);
                             /*       editName.setFocusable(false);
                                    editCard.setFocusable(false);
                                    editCVV.setFocusable(false);
                                    editExpir.setFocusable(false);
*/

                            }
                        });

                        newCard.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
  /*                              EditText editName, editCard, editCVV, editExpir;
                                editName = (EditText)findViewById(R.id.popup_name);
                                editCard = (EditText)findViewById(R.id.popup_credit_card);
                                editCVV = (EditText)findViewById(R.id.popup_CVV);
                                editExpir = (EditText)findViewById(R.id.popup_expiration_date);
                                RadioButton currCard = (RadioButton) findViewById(R.id.curr_card_rb);
                                RadioButton newCard = (RadioButton) findViewById(R.id.new_card_rb);
*/


                                newCard.setChecked(true);
                                currCard.setChecked(false);
                                    /*editName.setFocusable(true);
                                    editCard.setFocusable(true);
                                    editCVV.setFocusable(true);
                                    editExpir.setFocusable(true);
                               */
                            }
                        });



                        Button submitButton = (Button) customView.findViewById(R.id.popup_submit_reservation);
                        submitButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if(newCard.isChecked()) { //(RadioButton)customView.findViewById(R.id.new_card_rb
                                    final SeekerPayment seekerPayment = new SeekerPayment();
                                    EditText editName, editCard, editCVV, editExpir;

                                    editName = (EditText)customView.findViewById(R.id.popup_name);
                                    String name = editName.getText().toString();
                                    seekerPayment.setName(name);
                                    String[] split = name.split(" ");
                                    String firstName = split[0];
                                    seekerPayment.setFirstName(firstName);
                                    String lastName = split[1];
                                    seekerPayment.setLastName(lastName);

                                    editCard = (EditText)customView.findViewById(R.id.popup_credit_card);
                                    String temp = editCard.getText().toString();
                                    seekerPayment.setNumber(Integer.parseInt(temp.substring(temp.length() - 4)));
                                    editCVV = (EditText)findViewById(R.id.popup_CVV);

                                    editExpir = (EditText)customView.findViewById(R.id.popup_expiration_date);
                                    String[] split2 = editExpir.getText().toString().split(" ");
                                    String month = split2[0];
                                    seekerPayment.setExpMonth(month);
                                    String year = split2[1];
                                    seekerPayment.setExpYear(year);

                                    Spinner spinner = (Spinner)customView.findViewById(R.id.new_spinner);
                                    String selection = spinner.getSelectedItem().toString();
                                    for(int i = 0; i < cars.length; i++) {
                                        String curr = "" + cars[i].getLicensePlate();
                                        if (curr.equals(selection)) {
                                            final Car car = new Car();
                                            car.setLicensePlate(cars[i].getLicensePlate());
                                            car.setVehicleId(cars[i].getVehicleId());

                                            alertDialog.dismiss();

                                            LayoutInflater li = LayoutInflater.from(parking_space_screen.this);
                                            final View customView = li.inflate(R.layout.popup_confirmation, null);

                                            android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(parking_space_screen.this);
                                            alertDialogBuilder.setView(customView);
                                            final android.app.AlertDialog alertDialog1 = alertDialogBuilder.create();

                                            TextView tv = (TextView)customView.findViewById(R.id.confirm_text);

                                            // TODO: move some of this stuff out so don't have to have so much repeated code?
                                            final String start = getTime(startTime);
                                            final String end = getTime(endTime);
                                            String text;
                                            text = "Are you sure you want to reserve a parking space at " + parkingSpace.getAddress()
                                                    + "\nFrom: " + start + " on " + dateRange[0] + "\nTo: " + end + " on " + dateRange[1]
                                                    + "\nFor: $" + parkingSpace.getTotalPrice();
                                            tv.setText(text);

                                            Button yes_btn = (Button)customView.findViewById(R.id.confirm_yes_btn);
                                            Button no_btn = (Button)customView.findViewById(R.id.confirm_no_btn);
                                            alertDialog1.show();
                                            no_btn.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view){
                                                    alertDialog1.dismiss();
                                                }
                                            });

                                            yes_btn.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view){
                                                    Retrofit retrofit = new Retrofit.Builder()
                                                            .baseUrl(Constants.BASE_URL)
                                                            .addConverterFactory(GsonConverterFactory.create())
                                                            .build();

                                                    RequestInterface requestInterface = retrofit.create(RequestInterface.class);
                                                    System.out.println("this is in the yes button click for new card");
                                                    ServerRequest request = new ServerRequest();
                                                    request.setOperation(Constants.NEW_PAYMENT);
                                                    ParkingReservation parkingReservation = new ParkingReservation();
                                                    parkingReservation.setDateRange(dateRange);
                                                    parkingReservation.setHourStart(startTime);
                                                    parkingReservation.setHourEnd(endTime);
                                                    parkingReservation.setParkingSpace(parkingSpace);
                                                    parkingReservation.setPrice(parkingSpace.getTotalPrice());
                                                    parkingReservation.setSeeker_id(Integer.parseInt(user.getID()));
                                                    request.setParkingReservation(parkingReservation);
                                                    request.setSeekerPayment(seekerPayment);
                                                    request.setCar(car);

                                                    Call<ServerResponse> response = requestInterface.operation(request);

                                                    response.enqueue(new Callback<ServerResponse>() {
                                                        @Override
                                                        public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                                                            ServerResponse resp = response.body();
                                                            Intent intent = new Intent(getApplicationContext(), invoice_screen.class);
                                                            intent.putExtra("parkingSpot", parkingSpace);
                                                            intent.putExtra("user", user);
                                                            intent.putExtra("dateRange", dateRange);
                                                            intent.putExtra("hourStart", startTime);
                                                            intent.putExtra("hourEnd", endTime);
                                                            intent.putExtra("payment", seekerPayment.getNumber());
                                                            System.out.println("this is in the onResponse button click");
                                                            startActivity(intent);
                                                        }

                                                        @Override
                                                        public void onFailure(Call<ServerResponse> call, Throwable t) {
                                                            System.out.println("onFailure in creating reservation and saving new card");
                                                            //Snackbar.make(register_scroll_view, t.getLocalizedMessage(), Snackbar.LENGTH_LONG).show();
                                                        }
                                                    });

                                                    alertDialog1.dismiss();
                                                }
                                            });
                                        }
                                    }
                                } else {
                                    // loop through payments and match "last4digits"
                                    Spinner spinner = (Spinner)customView.findViewById(R.id.card_spinner);
                                    String selection = spinner.getSelectedItem().toString();
                                    for(int i = 0; i < payments.length; i++) {
                                        String curr = ""+payments[i].getLast4digits();
                                        if(curr.equals(selection)) {
                                            final Payment payment = new Payment();
                                            payment.setPaymentId(payments[i].getPaymentId());
                                            payment.setLast4digits(payments[i].getLast4digits());

                                            Spinner spinner2 = (Spinner)customView.findViewById(R.id.new_spinner);
                                            String selection2 = spinner2.getSelectedItem().toString();
                                            for(int j = 0; j < cars.length; j++) {
                                                String curr2 = "" + cars[j].getLicensePlate();
                                                if (curr2.equals(selection2)) {
                                                    final Car car = new Car();
                                                    car.setLicensePlate(cars[j].getLicensePlate());
                                                    car.setVehicleId(cars[j].getVehicleId());

                                                    alertDialog.dismiss();

                                                    LayoutInflater li = LayoutInflater.from(parking_space_screen.this);
                                                    final View customView = li.inflate(R.layout.popup_confirmation, null);

                                                    android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(parking_space_screen.this);
                                                    alertDialogBuilder.setView(customView);
                                                    final android.app.AlertDialog alertDialog1 = alertDialogBuilder.create();

                                                    TextView tv = (TextView) customView.findViewById(R.id.confirm_text);

                                                    final String start = getTime(startTime);
                                                    final String end = getTime(endTime);
                                                    String text;
                                                    text = "Are you sure you want to reserve a parking space at " + parkingSpace.getAddress()
                                                            + "\nFrom: " + start + " on " + dateRange[0] + "\nTo: " + end + " on " + dateRange[1]
                                                            + "\nFor: $" + parkingSpace.getTotalPrice();
                                                    tv.setText(text);

                                                    Button yes_btn = (Button) customView.findViewById(R.id.confirm_yes_btn);
                                                    Button no_btn = (Button) customView.findViewById(R.id.confirm_no_btn);
                                                    alertDialog1.show();
                                                    no_btn.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View view) {
                                                            alertDialog1.dismiss();
                                                        }
                                                    });

                                                    yes_btn.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View view) {
                                                            Retrofit retrofit = new Retrofit.Builder()
                                                                    .baseUrl(Constants.BASE_URL)
                                                                    .addConverterFactory(GsonConverterFactory.create())
                                                                    .build();

                                                            RequestInterface requestInterface = retrofit.create(RequestInterface.class);
                                                            System.out.println("this is in the yes button click for current card");
                                                            ServerRequest request = new ServerRequest();
                                                            request.setOperation(Constants.PARKING_RESERVATION);
                                                            ParkingReservation parkingReservation = new ParkingReservation();
                                                            parkingReservation.setDateRange(dateRange);
                                                            parkingReservation.setHourStart(startTime);
                                                            parkingReservation.setHourEnd(endTime);
                                                            parkingReservation.setParkingSpace(parkingSpace);
                                                            parkingReservation.setPrice(parkingSpace.getTotalPrice());
                                                            parkingReservation.setSeeker_id(Integer.parseInt(user.getID()));
                                                            request.setParkingReservation(parkingReservation);
                                                            request.setPayment(payment);
                                                            request.setCar(car);
                                                            Call<ServerResponse> response = requestInterface.operation(request);

                                                            response.enqueue(new Callback<ServerResponse>() {
                                                                @Override
                                                                public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                                                                    ServerResponse resp = response.body();
                                                                    Intent intent = new Intent(getApplicationContext(), invoice_screen.class);
                                                                    intent.putExtra("parkingSpot", parkingSpace);
                                                                    intent.putExtra("user", user);
                                                                    intent.putExtra("dateRange", dateRange);
                                                                    intent.putExtra("hourStart", startTime);
                                                                    intent.putExtra("hourEnd", endTime);
                                                                    intent.putExtra("payment", payment.getLast4digits());
                                                                    System.out.println("this is in the onResponse button click");
                                                                    startActivity(intent);
                                                                }

                                                                @Override
                                                                public void onFailure(Call<ServerResponse> call, Throwable t) {
                                                                    System.out.println("onFailure in creating reservation");
                                                                    //Snackbar.make(register_scroll_view, t.getLocalizedMessage(), Snackbar.LENGTH_LONG).show();
                                                                }
                                                            });

                                                            alertDialog1.dismiss();
                                                        }
                                                    });
                                                }
                                            }
                                        }
                                    }
                                }



                                //RadioButton currCard = (RadioButton) findViewById(R.id.curr_card_rb);
                        /*        RadioButton newCard = (RadioButton) findViewById(R.id.new_card_rb);
                                EditText name = (EditText) findViewById(R.id.popup_name);
                                EditText creditCard = (EditText) findViewById(R.id.popup_credit_card);
                                EditText cvv = (EditText) findViewById(R.id.popup_CVV);
                                EditText expiration = (EditText) findViewById(R.id.popup_expiration_date);
                        */        //System.out.println("name = "+ name);

//                                if(newCard.isSelected()) {
//                                    if (name==null || creditCard==null || cvv==null || expiration==null) {
//                                        alertDialog.dismiss();
//                                        Snackbar.make(imageView, "Invalid payment", Snackbar.LENGTH_LONG).show();
//                                    } else if (creditCard.length() != 16 || cvv.length() != 3) {
//                                        alertDialog.dismiss();
//                                        Snackbar.make(imageView, "Invalid payment", Snackbar.LENGTH_LONG).show();
//                                    }
//                                }  else {

                                // }
                            }
                        });

                        alertDialog.show();
                    }
                }
        );

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

    public String getTime(int time) {
        if (time == 0 || time == 24) {
            return "12:00 AM";
        } else if (time >= 1 && time <= 9) {
            return "0" + time + ":00 AM";
        } else if (time == 10 || time == 11) {
            return time + ":00 AM";
        } else if (time == 12) {
            return time + ":00 PM";
        } else if (time >= 13 && time <= 21) {
            time -= 12;
            return "0" + time + ":00 PM";
        } else if (time == 22 || time == 23) {
            time -= 12;
            return time + ":00 PM";
        }
        return "Invalid time";
    }
}