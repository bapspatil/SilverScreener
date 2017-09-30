package bapspatil.silverscreener;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.takusemba.multisnaprecyclerview.MultiSnapRecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;

public class DetailsActivity extends AppCompatActivity implements TrailerRecyclerViewAdapter.ItemClickListener {


    private TextView mRatingTextView, mDateTextView, mTitleTextView, mPlotTextView;
    private ImageView mPosterImageView, mBackdropImageView;
    private MultiSnapRecyclerView mTrailerRecyclerView, mReviewRecyclerView;
    private TrailerRecyclerViewAdapter mTrailerAdapter;
    private ReviewRecyclerViewAdapter mReviewAdapter;
    private Button mFavoriteButton;
    private Context mContext;
    private ArrayList<String> mTrailerTitles = new ArrayList<String>();
    private ArrayList<String> mTrailerPaths = new ArrayList<String>();
    private ArrayList<String> mReviewAuthors = new ArrayList<String>();
    private ArrayList<String> mReviewContents = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        mContext = getApplicationContext();
        Toolbar toolbar = (Toolbar) findViewById(R.id.details_toolbar);
        toolbar.setLogo(R.mipmap.titlebar_logo);
        setSupportActionBar(toolbar);

        mRatingTextView = (TextView) findViewById(R.id.rating_value_tv);
        mDateTextView = (TextView) findViewById(R.id.date_value_tv);
        mTitleTextView = (TextView) findViewById(R.id.title_tv);
        mPlotTextView = (TextView) findViewById(R.id.plot_tv);
        mPosterImageView = (ImageView) findViewById(R.id.poster_image_view);
        mBackdropImageView = (ImageView) findViewById(R.id.backdrop_image_view);
        mFavoriteButton = (Button) findViewById(R.id.fav_button);
        mFavoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                return;
            }
        });

        mTrailerRecyclerView = (MultiSnapRecyclerView) findViewById(R.id.rv_trailers);
        mTrailerRecyclerView.setLayoutManager(new LinearLayoutManager(DetailsActivity.this, LinearLayoutManager.HORIZONTAL, false));
        mTrailerAdapter = new TrailerRecyclerViewAdapter(mContext, mTrailerTitles, mTrailerPaths, this);
        mTrailerRecyclerView.setAdapter(mTrailerAdapter);

        mReviewRecyclerView = (MultiSnapRecyclerView) findViewById(R.id.rv_reviews);
        mReviewRecyclerView.setLayoutManager(new LinearLayoutManager(DetailsActivity.this, LinearLayoutManager.HORIZONTAL, false));
        mReviewAdapter = new ReviewRecyclerViewAdapter(mContext, mReviewAuthors, mReviewContents);
        mReviewRecyclerView.setAdapter(mReviewAdapter);

        Movie movie;
        Intent receivedIntent = getIntent();
        if (receivedIntent.hasExtra("movie")) {
            movie = receivedIntent.getParcelableExtra("movie");
            mRatingTextView.setText(movie.getRating());
            mDateTextView.setText(movie.getDate());
            mTitleTextView.setText(movie.getTitle());
            mPlotTextView.setText(movie.getPlot());
            Picasso.with(getApplicationContext()).load(movie.getPosterPath()).into(mPosterImageView);
            Picasso.with(getApplicationContext()).load(movie.getBackdropPath()).into(mBackdropImageView);
            (new GetTheTrailersTask()).execute(movie.getId());
            (new GetTheReviewsTask()).execute(movie.getId());
        }
    }

    @Override
    public void onItemClick(String stringUrlTrailerClicked) {
        Uri youtubeUri = Uri.parse(stringUrlTrailerClicked);
        Intent openYoutube = new Intent(Intent.ACTION_VIEW, youtubeUri);
        startActivity(openYoutube);
    }

    private class GetTheTrailersTask extends AsyncTask<Integer, Void, String> {

        @Override
        protected void onPreExecute() {
            if (!Connection.hasNetwork(mContext)) {
                cancel(true);
                Toast.makeText(mContext, "No Internet Connection", Toast.LENGTH_SHORT).show();
            }
        }


        @Override
        protected String doInBackground(Integer... params) {
            Uri builtUriTrailers = Uri.parse("https://api.themoviedb.org/3/movie/" + params[0] + "/videos").buildUpon()
                    .appendQueryParameter("api_key", BuildConfig.TMDB_API_TOKEN)
                    .appendQueryParameter("language", "en-US")
                    .build();
            String jsonResponseTrailers;
            try {
                jsonResponseTrailers = Connection.getResponseFromHttpUrl(new URL(builtUriTrailers.toString()));
                return jsonResponseTrailers;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String jsonResponse) {
            mTrailerPaths.clear();
            mTrailerTitles.clear();
            try {
                JSONObject jsonTrailersObject = new JSONObject(jsonResponse);
                JSONArray jsonTrailersArray = jsonTrailersObject.getJSONArray("results");
                for (int i = 0; i < jsonTrailersArray.length(); i++) {
                    JSONObject jsonTrailer = jsonTrailersArray.getJSONObject(i);
                    mTrailerTitles.add(jsonTrailer.getString("name"));
                    mTrailerPaths.add("https://www.youtube.com/watch?v=" + jsonTrailer.getString("key"));
                    mTrailerAdapter.notifyDataSetChanged();
                }
            } catch (Exception e) {
                Toast.makeText(mContext, "Error in the trailer data fetched!", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
    }

    private class GetTheReviewsTask extends AsyncTask<Integer, Void, String> {

        @Override
        protected void onPreExecute() {
            if (!Connection.hasNetwork(mContext)) {
                cancel(true);
                Toast.makeText(mContext, "No Internet Connection", Toast.LENGTH_SHORT).show();
            }
        }


        @Override
        protected String doInBackground(Integer... params) {
            Uri builtUriReviews = Uri.parse("https://api.themoviedb.org/3/movie/" + params[0] + "/reviews").buildUpon()
                    .appendQueryParameter("api_key", BuildConfig.TMDB_API_TOKEN)
                    .appendQueryParameter("language", "en-US")
                    .build();
            String jsonResponseReviews;
            try {
                jsonResponseReviews = Connection.getResponseFromHttpUrl(new URL(builtUriReviews.toString()));
                return jsonResponseReviews;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String jsonResponse) {
            mReviewAuthors.clear();
            mReviewContents.clear();
            try {
                JSONObject jsonReviewsObject = new JSONObject(jsonResponse);
                JSONArray jsonReviewsArray = jsonReviewsObject.getJSONArray("results");
                for (int i = 0; i < jsonReviewsArray.length(); i++) {
                    JSONObject jsonReview = jsonReviewsArray.getJSONObject(i);
                    mReviewAuthors.add(jsonReview.getString("author"));
                    mReviewContents.add(jsonReview.getString("content"));
                    mReviewAdapter.notifyDataSetChanged();
                }
            } catch (Exception e) {
                Toast.makeText(mContext, "Error in the review data fetched!", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
    }

    void addMovieToFavorites() {

    }
}
