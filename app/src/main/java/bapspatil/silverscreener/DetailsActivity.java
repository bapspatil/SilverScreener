package bapspatil.silverscreener;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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

import bapspatil.silverscreener.data.Connection;
import bapspatil.silverscreener.data.FavsContract;
import bapspatil.silverscreener.data.FavsDbHelper;

public class DetailsActivity extends AppCompatActivity implements TrailerRecyclerViewAdapter.ItemClickListener {


    private TextView mRatingTextView, mDateTextView, mTitleTextView, mPlotTextView;
    private ImageView mPosterImageView, mBackdropImageView;
    private MultiSnapRecyclerView mTrailerRecyclerView, mReviewRecyclerView;
    private TrailerRecyclerViewAdapter mTrailerAdapter;
    private ReviewRecyclerViewAdapter mReviewAdapter;
    private Button mFavoriteButton;
    private Context mContext;
    private ArrayList<String> mTrailerTitles = new ArrayList<>();
    private ArrayList<String> mTrailerPaths = new ArrayList<>();
    private ArrayList<String> mReviewAuthors = new ArrayList<>();
    private ArrayList<String> mReviewContents = new ArrayList<>();
    private SQLiteDatabase mDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        mContext = getApplicationContext();
        Toolbar toolbar = (Toolbar) findViewById(R.id.details_toolbar);
        toolbar.setLogo(R.mipmap.titlebar_logo);
        setSupportActionBar(toolbar);
        FavsDbHelper mDbHelper = new FavsDbHelper(this);
        mDb = mDbHelper.getWritableDatabase();
        Movie movie = getIntent().getParcelableExtra("movie");

        mRatingTextView = (TextView) findViewById(R.id.rating_value_tv);
        mDateTextView = (TextView) findViewById(R.id.date_value_tv);
        mTitleTextView = (TextView) findViewById(R.id.title_tv);
        mPlotTextView = (TextView) findViewById(R.id.plot_tv);
        mPosterImageView = (ImageView) findViewById(R.id.poster_image_view);
        mBackdropImageView = (ImageView) findViewById(R.id.backdrop_image_view);
        mFavoriteButton = (Button) findViewById(R.id.fav_button);

        String[] movieTitle = {movie.getTitle()};
        Cursor cursor = mDb.rawQuery("SELECT * FROM " + FavsContract.FavsEntry.TABLE_NAME + " WHERE " + FavsContract.FavsEntry.COLUMN_TITLE + " = ?", movieTitle);
        if (cursor.getCount() == 0)
            mFavoriteButton.setBackgroundResource(R.drawable.ic_favorite_border);
        else
            mFavoriteButton.setBackgroundResource(R.drawable.ic_favorite);
        cursor.close();

        mFavoriteButton.setOnClickListener(new View.OnClickListener() {
            Movie movie = getIntent().getParcelableExtra("movie");
            String[] movieTitle = {movie.getTitle()};

            @Override
            public void onClick(View v) {
                bounceAnimation();
                Cursor cursor = mDb.rawQuery("SELECT * FROM " + FavsContract.FavsEntry.TABLE_NAME + " WHERE " + FavsContract.FavsEntry.COLUMN_TITLE + " = ?", movieTitle);
                if (cursor.getCount() == 0) {
                    addMovieToFavorites(movie);
                    mFavoriteButton.setBackgroundResource(R.drawable.ic_favorite);
                } else {
                    deleteMovieFromFavorites(movie);
                    mFavoriteButton.setBackgroundResource(R.drawable.ic_favorite_border);
                }
                cursor.close();

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

        mRatingTextView.setText(movie.getRating());
        mDateTextView.setText(movie.getDate());
        mTitleTextView.setText(movie.getTitle());
        mPlotTextView.setText(movie.getPlot());
        Picasso.with(getApplicationContext()).load(movie.getPosterPath()).into(mPosterImageView);
        Picasso.with(getApplicationContext()).load(movie.getBackdropPath()).into(mBackdropImageView);
        (new GetTheTrailersTask()).execute(movie.getId());
        (new GetTheReviewsTask()).execute(movie.getId());

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

    void bounceAnimation() {
        final Animation myAnim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bounce);
        BounceAnimationInterpolator interpolator = new BounceAnimationInterpolator(0.2, 20);
        myAnim.setInterpolator(interpolator);
        mFavoriteButton.startAnimation(myAnim);
    }

    void addMovieToFavorites(Movie movie) {
        Toast.makeText(mContext, "Movie added to Favorites! :-)", Toast.LENGTH_SHORT).show();
        ContentValues cv = new ContentValues();
        cv.put(FavsContract.FavsEntry.COLUMN_ID, movie.getId());
        cv.put(FavsContract.FavsEntry.COLUMN_TITLE, movie.getTitle());
        cv.put(FavsContract.FavsEntry.COLUMN_PLOT, movie.getPlot());
        cv.put(FavsContract.FavsEntry.COLUMN_RATING, movie.getRating());
        cv.put(FavsContract.FavsEntry.COLUMN_DATE, movie.getDate());
        mDb.insert(FavsContract.FavsEntry.TABLE_NAME, null, cv);
    }

    void deleteMovieFromFavorites(Movie movie) {
        Toast.makeText(mContext, "Movie removed from Favorites! :-(", Toast.LENGTH_SHORT).show();
        String[] movieTitle = {movie.getTitle()};
        mDb.delete(FavsContract.FavsEntry.TABLE_NAME, FavsContract.FavsEntry.COLUMN_TITLE + " = ?", movieTitle);
    }
}
