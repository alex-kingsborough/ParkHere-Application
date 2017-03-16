package com.example.parkhere.seeker;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.parkhere.R;
import com.example.parkhere.main.ChangePasswordFragment;
import com.example.parkhere.main.HomeActivity;
import com.example.parkhere.main.NotificationsFragment;
import com.example.parkhere.objects.User;
import com.example.parkhere.provider.ProviderHomeActivity;
import com.example.parkhere.server.Constants;
import com.example.parkhere.server.RequestInterface;
import com.example.parkhere.server.ServerRequest;
import com.example.parkhere.server.ServerResponse;

import java.io.InputStream;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SeekerHomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private User user;
    private DrawerLayout drawer;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seeker_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        user = (User) getIntent().getSerializableExtra("user");

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View nav_header = navigationView.getHeaderView(0);
        CircleImageView imageView = (CircleImageView) nav_header.findViewById(R.id.imageView);
        String imgURL = "http://10.0.2.2/parkhere/" + user.getProfilePicURL();
        new DownloadImageTask(imageView).execute(imgURL);
        TextView navName = (TextView) nav_header.findViewById(R.id.navName);
        navName.setText(user.getFirstName() + " " + user.getLastName());
        TextView navEmail = (TextView) nav_header.findViewById(R.id.navEmail);
        navEmail.setText(user.getEmail());
        navigationView.getMenu().getItem(0).setChecked(true);
        onNavigationItemSelected(navigationView.getMenu().getItem(0));

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                View nav_header = navigationView.getHeaderView(0);
                CircleImageView imageView = (CircleImageView) nav_header.findViewById(R.id.imageView);
                String imgURL = "http://10.0.2.2/parkhere/" + user.getProfilePicURL();
                new DownloadImageTask(imageView).execute(imgURL);
                TextView navName = (TextView) nav_header.findViewById(R.id.navName);
                navName.setText(user.getFirstName() + " " + user.getLastName());
            }
        };
        drawer.setDrawerListener(toggle);
        toggle.syncState();
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        Fragment fragment = null;

        if (id == R.id.nav_home) {
            fragment = SeekerHomeFragment.newInstance(user);
        } else if (id == R.id.nav_profile) {
            fragment = SeekerProfileFragment.newInstance(user);
        } else if (id == R.id.nav_history) {
            fragment = SeekerHistoryFragment.newInstance(user);
        } else if (id == R.id.nav_notifications) {
            fragment = NotificationsFragment.newInstance(user);
        } else if (id == R.id.nav_changepassword) {
            fragment = ChangePasswordFragment.newInstance(user);
        } else if (id == R.id.nav_addchangerole) {
            if (user.getIsProvider().equals("false")){
                fragment = AddProviderRoleFragment.newInstance(user);
            } else {
                user.setDefaultRole("Provider");
                changeRoleToProvider();
            }
        } else if (id == R.id.nav_logout) {
            Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
            startActivity(intent);
        }

        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
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

    public void changeRoleToProvider() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RequestInterface requestInterface = retrofit.create(RequestInterface.class);

        ServerRequest request = new ServerRequest();
        request.setOperation(Constants.CHANGE_ROLE_OPERATION);
        request.setUser(user);
        Call<ServerResponse> response = requestInterface.operation(request);

        response.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, retrofit2.Response<ServerResponse> response) {
                ServerResponse resp = response.body();

                if(resp.getResult().equals(Constants.SUCCESS)){
                    Intent intent = new Intent(getApplicationContext(), ProviderHomeActivity.class);
                    intent.putExtra("user", user);
                    startActivity(intent);
                }
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                System.out.println("Change Role Failure: " + t.getLocalizedMessage());
            }
        });
    }
}
