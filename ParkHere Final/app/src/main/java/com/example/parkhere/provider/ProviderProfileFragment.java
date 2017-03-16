package com.example.parkhere.provider;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.parkhere.R;
import com.example.parkhere.objects.ProviderPayment;
import com.example.parkhere.objects.Rating;
import com.example.parkhere.objects.User;
import com.example.parkhere.server.Constants;
import com.example.parkhere.server.RequestInterface;
import com.example.parkhere.server.ServerRequest;
import com.example.parkhere.server.ServerResponse;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.app.Activity.RESULT_OK;

public class ProviderProfileFragment extends Fragment {
    private User user;
    private static final String USER_KEY = "user";
    private static final String PROVIDER_PAYMENT_KEY = "providerPayment";
    private static final String PROVIDER_RATINGS_KEY = "rating";
    private View rootView;
    private CircleImageView pprof_profile;
    private TextView pprof_email, pprof_membersince;
    private FragmentActivity myContext;
    private int PICK_IMAGE_REQUEST = 1;
    private Bitmap bitmap;
    private boolean uploadedImage = false;
    private String oldProfilePicURL;
    private ProviderPayment payment;
    private Rating[] ratings;
    private FragmentTabHost mTabHost;
    private Bundle bundle;

    public static ProviderProfileFragment newInstance(User user) {
        ProviderProfileFragment fragment = new ProviderProfileFragment();
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
        getCurrInfo();

        rootView = inflater.inflate(R.layout.fragment_provider_profile, container, false);

        oldProfilePicURL = user.getProfilePicURL();
        pprof_profile = (CircleImageView) rootView.findViewById(R.id.pprof_profile);
        pprof_email = (TextView) rootView.findViewById(R.id.pprof_email);
        pprof_membersince = (TextView) rootView.findViewById(R.id.pprof_membersince);

        String imgURL = "http://10.0.2.2/parkhere/" + user.getProfilePicURL();
        new DownloadImageTask(pprof_profile).execute(imgURL);

        pprof_email.setText(user.getEmail());
        pprof_membersince.setText("Member Since: " + user.getCreatedAt());

        pprof_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFileChooser();
            }
        });

        mTabHost = (FragmentTabHost)rootView.findViewById(android.R.id.tabhost);
        mTabHost.setup(getActivity(), getChildFragmentManager(), R.id.realtabcontent);

        bundle = new Bundle();
        bundle.putSerializable(USER_KEY, user);
        mTabHost.addTab(mTabHost.newTabSpec("personal").setIndicator("PERSONAL"),
                ProviderProfilePersonalFragment.class, bundle);

        return rootView;
    }

    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(rootView.getContext().getContentResolver(), filePath);
                pprof_profile.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }

            uploadedImage = true;
            String image = getStringImage(bitmap);
            user.setProfilePicURL(image);

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(Constants.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            RequestInterface requestInterface = retrofit.create(RequestInterface.class);

            ServerRequest request = new ServerRequest();
            request.setOperation(Constants.UPDATE_PROFILE_PICTURE_OPERATION);
            request.setUser(user);
            Call<ServerResponse> response = requestInterface.operation(request);

            response.enqueue(new Callback<ServerResponse>() {
                @Override
                public void onResponse(Call<ServerResponse> call, retrofit2.Response<ServerResponse> response) {
                    ServerResponse resp = response.body();

                    Snackbar.make(rootView, resp.getMessage(), Snackbar.LENGTH_LONG).show();

                    if(resp.getResult().equals(Constants.SUCCESS)){
                        user.setProfilePicURL(oldProfilePicURL);
                    }
                }

                @Override
                public void onFailure(Call<ServerResponse> call, Throwable t) {
                    Snackbar.make(rootView, t.getLocalizedMessage(), Snackbar.LENGTH_LONG).show();
                }
            });
        }
    }

    public String getStringImage(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    public void getCurrInfo() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RequestInterface requestInterface = retrofit.create(RequestInterface.class);

        ServerRequest request = new ServerRequest();
        request.setOperation(Constants.GET_PROVIDER_PROFILE_OPERATION);
        request.setUser(user);
        Call<ServerResponse> response = requestInterface.operation(request);

        response.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, retrofit2.Response<ServerResponse> response) {
                ServerResponse resp = response.body();

                if(resp.getResult().equals(Constants.SUCCESS)){
                    payment = resp.getProviderPayment();
                    ratings = resp.getRatings();

                    bundle = new Bundle();
                    bundle.putSerializable(USER_KEY, user);
                    bundle.putSerializable(PROVIDER_PAYMENT_KEY, payment);
                    mTabHost.addTab(mTabHost.newTabSpec("payment").setIndicator("PAYMENT"),
                            ProviderProfilePaymentFragment.class, bundle);

                    bundle = new Bundle();
                    bundle.putSerializable(PROVIDER_RATINGS_KEY, ratings);
                    mTabHost.addTab(mTabHost.newTabSpec("ratings").setIndicator("RATINGS"),
                            ProviderProfileRatingsFragment.class, bundle);
                }
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
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
