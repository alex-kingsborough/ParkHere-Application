package com.example.parkhere.provider;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.parkhere.R;
import com.example.parkhere.objects.Rating;

public class ProviderProfileRatingsFragment extends Fragment {
    private Rating[] ratings;
    private static final String PROVIDER_RATINGS_KEY = "rating";
    private View rootView;
    private FragmentActivity myContext;

    @Override
    public void onAttach(Activity activity) {
        myContext=(FragmentActivity) activity;
        super.onAttach(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ratings = (Rating[]) getArguments().getSerializable(PROVIDER_RATINGS_KEY);

        rootView = inflater.inflate(R.layout.fragment_provider_profile_ratings, container, false);

        TextView pprof_table_label = (TextView) rootView.findViewById(R.id.pprof_table_label);
        TableLayout tl = (TableLayout) rootView.findViewById(R.id.pprof_table);

        if (ratings.length == 0) {
            pprof_table_label.setText("No Ratings Yet!");
        } else {
            TableRow tr_head = new TableRow(myContext);
            tr_head.setBackgroundColor(Color.GRAY);
            tr_head.setLayoutParams(new TableRow.LayoutParams(
                    TableRow.LayoutParams.FILL_PARENT,
                    TableRow.LayoutParams.WRAP_CONTENT));

            TextView rating = new TextView(myContext);
            rating.setText("RATING");
            rating.setTextColor(Color.WHITE);
            rating.setPadding(5, 5, 5, 5);
            tr_head.addView(rating);

            TextView comment = new TextView(myContext);
            comment.setText("COMMENTS");
            comment.setTextColor(Color.WHITE);
            comment.setPadding(5, 5, 5, 5);
            tr_head.addView(comment);

            tl.addView(tr_head, new TableLayout.LayoutParams(
                    TableLayout.LayoutParams.FILL_PARENT,
                    TableLayout.LayoutParams.WRAP_CONTENT));

            int sum = 0;
            double average;

            for (int i = 0; i < ratings.length; i++) {
                sum += ratings[i].getRating();

                TableRow tr = new TableRow(myContext);
                tr.setLayoutParams(new TableRow.LayoutParams(
                        TableRow.LayoutParams.FILL_PARENT,
                        TableRow.LayoutParams.WRAP_CONTENT));

                TextView r_rating = new TextView(myContext);
                r_rating.setText(Integer.toString(ratings[i].getRating()));
                r_rating.setLayoutParams(new TableRow.LayoutParams(300, 150));
                tr.addView(r_rating);

                TextView r_comment = new TextView(myContext);
                r_comment.setText(ratings[i].getComment());
                r_comment.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
                tr.addView(r_comment);

                tl.addView(tr, new TableLayout.LayoutParams(
                        TableLayout.LayoutParams.FILL_PARENT,
                        TableLayout.LayoutParams.WRAP_CONTENT));
            }

            average = sum/(double) ratings.length;
            String avg = Double.toString(average);
            TextView avgrating = (TextView) rootView.findViewById(R.id.pprof_avgrating_label);
            avgrating.setText("Average Rating: " + avg.substring(0, 3));
        }

        return rootView;
    }
}

