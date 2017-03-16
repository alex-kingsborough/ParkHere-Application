package com.example.parkhere.main;

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
import com.example.parkhere.objects.Notification;
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

public class NotificationsFragment extends Fragment {
    private User user;
    private static final String USER_KEY = "user";
    private Button delete_button;
    private FragmentActivity myContext;
    private TableLayout tl;
    private View rootView;
    private String selectedNotification = "";
    private int selectedIndex = -1;
    private int tag = 1;

    public static NotificationsFragment newInstance(User user) {
        NotificationsFragment fragment = new NotificationsFragment();
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

        rootView = inflater.inflate(R.layout.fragment_notifications, container, false);

        delete_button = (Button) rootView.findViewById(R.id.notifications_delete_button);

        tl = (TableLayout) rootView.findViewById(R.id.notifications_table);

        getNotifications();

        delete_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (selectedNotification.equals("")){
                    Snackbar.make(rootView, "Must select a notification", Snackbar.LENGTH_LONG).show();
                } else {
                    LayoutInflater li = LayoutInflater.from(myContext);
                    View customView = li.inflate(R.layout.popup_delete_notification, null);

                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(myContext);
                    alertDialogBuilder.setView(customView);
                    final AlertDialog alertDialog = alertDialogBuilder.create();

                    TextView deleteText = (TextView) customView.findViewById(R.id.popup_delete_notification_text);
                    deleteText.setText("Are you sure you want to delete this notification: " + selectedNotification + "?");

                    ImageButton closeButton = (ImageButton) customView.findViewById(R.id.popup_delete_notification_close);
                    closeButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            alertDialog.dismiss();
                        }
                    });

                    Button deleteButton = (Button) customView.findViewById(R.id.popup_delete_notification_button);
                    deleteButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            deleteNotification();
                            alertDialog.dismiss();
                        }
                    });

                    alertDialog.show();
                }
            }
        });

        return rootView;
    }

    public void getNotifications() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RequestInterface requestInterface = retrofit.create(RequestInterface.class);

        ServerRequest request = new ServerRequest();
        request.setOperation(Constants.GET_NOTIFICATIONS_OPERATION);
        request.setUser(user);
        Call<ServerResponse> response = requestInterface.operation(request);

        response.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, retrofit2.Response<ServerResponse> response) {
                ServerResponse resp = response.body();

                if(resp.getResult().equals(Constants.SUCCESS)){
                    Notification[] n = resp.getNotifications();

                    TableRow tr_head = new TableRow(myContext);
                    tr_head.setBackgroundColor(Color.GRAY);
                    tr_head.setLayoutParams(new TableRow.LayoutParams(
                            TableRow.LayoutParams.FILL_PARENT,
                            TableRow.LayoutParams.WRAP_CONTENT));

                    TextView message = new TextView(myContext);
                    message.setText("MESSAGE");
                    message.setTextColor(Color.WHITE);
                    message.setPadding(5, 5, 5, 5);
                    tr_head.addView(message);

                    tl.addView(tr_head, new TableLayout.LayoutParams(
                            TableLayout.LayoutParams.FILL_PARENT,
                            TableLayout.LayoutParams.WRAP_CONTENT));

                    for (int i = 0; i < n.length; i++) {
                        TableRow tr = new TableRow(myContext);
                        tr.setLayoutParams(new TableRow.LayoutParams(
                                TableRow.LayoutParams.FILL_PARENT,
                                TableRow.LayoutParams.WRAP_CONTENT));
                        tr.setTag(tag);
                        tag++;
                        tr.setClickable(true);
                        tr.setOnClickListener(clickListener);

                        TextView n_message = new TextView(myContext);
                        n_message.setText(n[i].getMessage());
                        n_message.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
                        tr.addView(n_message);

                        tl.addView(tr, new TableLayout.LayoutParams(
                                TableLayout.LayoutParams.FILL_PARENT,
                                TableLayout.LayoutParams.WRAP_CONTENT));
                    }
                } else {
                    TextView notifications_table_label = (TextView) rootView.findViewById(R.id.notifications_table_label);
                    notifications_table_label.setText("No new notifications!");
                    delete_button.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                Snackbar.make(rootView, t.getLocalizedMessage(), Snackbar.LENGTH_LONG).show();
            }
        });
    }

    public void deleteNotification() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RequestInterface requestInterface = retrofit.create(RequestInterface.class);

        ServerRequest request = new ServerRequest();
        request.setOperation(Constants.DELETE_NOTIFICATION_OPERATION);
        Notification n = new Notification();
        n.setMessage(selectedNotification);
        request.setUser(user);
        request.setNotification(n);
        Call<ServerResponse> response = requestInterface.operation(request);

        response.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, retrofit2.Response<ServerResponse> response) {
                ServerResponse resp = response.body();

                if(resp.getResult().equals(Constants.SUCCESS)){
                    Fragment fragment = NotificationsFragment.newInstance(user);
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
            selectedNotification = tv.getText().toString();
        }
    }

    private void deselectRow(int index) {
        if (index >= 0) {
            TableRow tr = (TableRow) tl.getChildAt(index);
            tr.setBackgroundColor(Color.parseColor("#F2F7F2"));
        }
    }
}
