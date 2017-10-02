package bapspatil.silverscreener;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.takusemba.multisnaprecyclerview.MultiSnapRecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import bapspatil.silverscreener.data.Connection;
import bapspatil.silverscreener.data.FavsContract;

public class DetailsActivity extends AppCompatActivity implements TrailerRecyclerViewAdapter.ItemClickListener {


    private TextView mRatingTextView, mDateTextView, mTitleTextView, mPlotTextView, mReviewsLabel0, mReviewsLabel1, mTrailersLabel0, mTrailersLabel1;
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
    private byte[] imageBytes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        mContext = getApplicationContext();
        Toolbar toolbar = (Toolbar) findViewById(R.id.details_toolbar);
        toolbar.setLogo(R.mipmap.titlebar_logo);
        setSupportActionBar(toolbar);
        mTrailersLabel0 = (TextView) findViewById(R.id.trailer_label_tv);
        mTrailersLabel1 = (TextView) findViewById(R.id.trailers_hint_tv);
        mReviewsLabel0 = (TextView) findViewById(R.id.reviews_label_tv);
        mReviewsLabel1 = (TextView) findViewById(R.id.reviews_swipe_hint_tv);
        Movie movie = getIntent().getParcelableExtra("movie");


        mRatingTextView = (TextView) findViewById(R.id.rating_value_tv);
        mDateTextView = (TextView) findViewById(R.id.date_value_tv);
        mTitleTextView = (TextView) findViewById(R.id.title_tv);
        mPlotTextView = (TextView) findViewById(R.id.plot_tv);
        mPosterImageView = (ImageView) findViewById(R.id.poster_image_view);
        mBackdropImageView = (ImageView) findViewById(R.id.backdrop_image_view);
        mFavoriteButton = (Button) findViewById(R.id.fav_button);

        String[] movieTitle = {movie.getTitle()};
        Cursor cursor = getContentResolver().query(FavsContract.FavsEntry.CONTENT_URI,
                null,
                FavsContract.FavsEntry.COLUMN_TITLE + " = ?",
                movieTitle,
                null);
        if (cursor.getCount() == 0)
            mFavoriteButton.setBackgroundResource(R.drawable.ic_favorite_border);
        else
            mFavoriteButton.setBackgroundResource(R.drawable.ic_favorite);
        cursor.close();

        mFavoriteButton.setOnClickListener(new View.OnClickListener() {
            Movie movie = getIntent().getParcelableExtra("movie");

            @Override
            public void onClick(View v) {
                bounceAnimation();
                AddRemoveFavoritesTask addRemoveFavoritesTask = new AddRemoveFavoritesTask();
                if (addRemoveFavoritesTask.getStatus() == AsyncTask.Status.RUNNING)
                    return;
                else
                    addRemoveFavoritesTask.execute(movie);
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

        try {
            Glide.with(mContext)
                    .load(movie.getPosterPath())
                    .asBitmap()
                    .into(new SimpleTarget<Bitmap>(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL) {
                        @Override
                        public void onResourceReady(Bitmap bitmap, GlideAnimation<? super Bitmap> glideAnimation) {
                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                            imageBytes = stream.toByteArray();
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (movie.getPosterBytes() != null) {
            Glide.with(getApplicationContext())
                    .load(movie.getPosterBytes())
                    .centerCrop()
                    .error(R.drawable.no_internet_placeholder)
                    .fallback(R.drawable.no_internet_placeholder)
                    .into(mPosterImageView);
        } else {
            Glide.with(mContext)
                    .load(movie.getPosterPath())
                    .centerCrop()
                    .error(R.drawable.no_internet_placeholder)
                    .fallback(R.drawable.no_internet_placeholder)
                    .into(mPosterImageView);
        }
        if (Connection.hasNetwork(mContext)) {
            Glide.with(mContext)
                    .load(movie.getBackdropPath())
                    .centerCrop()
                    .error(R.drawable.no_internet_placeholder_landscape)
                    .fallback(R.drawable.no_internet_placeholder_landscape)
                    .into(mBackdropImageView);
        } else {
            Glide.with(mContext)
                    .load(R.drawable.no_internet_placeholder_landscape)
                    .centerCrop()
                    .into(mBackdropImageView);
        }
        (new GetTheTrailersTask()).execute(movie.getId());
        (new GetTheReviewsTask()).execute(movie.getId());
    }

    @Override
    public void onItemClick(String stringUrlTrailerClicked) {
        Uri youtubeUri = Uri.parse(stringUrlTrailerClicked);
        Intent openYoutube = new Intent(Intent.ACTION_VIEW, youtubeUri);
        startActivity(openYoutube);
    }

    private class AddRemoveFavoritesTask extends AsyncTask<Movie, Void, Cursor> {

        @Override
        protected void onPreExecute() {
        }


        @Override
        protected Cursor doInBackground(Movie... movie) {
            String title = movie[0].getTitle();
            String[] movieTitle = {title};
            Cursor cursor = getContentResolver().query(FavsContract.FavsEntry.CONTENT_URI,
                    null,
                    FavsContract.FavsEntry.COLUMN_TITLE + "=?",
                    movieTitle,
                    null);
            if (cursor != null) {
                if (cursor.getCount() == 0) {
                    try {
                        addMovieToFavorites(movie[0]);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    deleteMovieFromFavorites(movie[0]);
                }
                cursor.close();
            }
            return cursor;
        }

        @Override
        protected void onPostExecute(Cursor cursor) {
            if (cursor != null)
                if (cursor.getCount() == 0) {
                    Toast.makeText(mContext, "Movie added to Favorites! :-)", Toast.LENGTH_SHORT).show();
                    mFavoriteButton.setBackgroundResource(R.drawable.ic_favorite);
                } else {
                    Toast.makeText(mContext, "Movie removed from Favorites! :-(", Toast.LENGTH_SHORT).show();
                    mFavoriteButton.setBackgroundResource(R.drawable.ic_favorite_border);
                }
        }
    }

    private class GetTheTrailersTask extends AsyncTask<Integer, Void, String> {

        @Override
        protected void onPreExecute() {
            if (!Connection.hasNetwork(mContext)) {
                cancel(true);
                Toast.makeText(mContext, "No Internet Connection", Toast.LENGTH_SHORT).show();
                mTrailersLabel0.setVisibility(View.INVISIBLE);
                mTrailersLabel1.setVisibility(View.INVISIBLE);
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
                mReviewsLabel0.setVisibility(View.INVISIBLE);
                mReviewsLabel1.setVisibility(View.INVISIBLE);
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

    void addMovieToFavorites(Movie movie) throws ExecutionException, InterruptedException {
        ContentValues cv = new ContentValues();
        cv.put(FavsContract.FavsEntry._ID, String.valueOf(movie.getId()));
        cv.put(FavsContract.FavsEntry.COLUMN_TITLE, movie.getTitle());
        cv.put(FavsContract.FavsEntry.COLUMN_PLOT, movie.getPlot());
        cv.put(FavsContract.FavsEntry.COLUMN_RATING, movie.getRating());
        cv.put(FavsContract.FavsEntry.COLUMN_DATE, movie.getDate());
        cv.put(FavsContract.FavsEntry.COLUMN_POSTERPATH, movie.getPosterPath());
        cv.put(FavsContract.FavsEntry.COLUMN_BACKDROPPATH, movie.getBackdropPath());
        cv.put(FavsContract.FavsEntry.COLUMN_POSTER, imageBytes);
        Uri uri = getContentResolver().insert(FavsContract.FavsEntry.CONTENT_URI, cv);
        if (uri != null)
            Log.d("Add Fav", "Uri add: " + uri.toString());
    }

    void deleteMovieFromFavorites(Movie movie) {
        String movieId = String.valueOf(movie.getId());
        Uri uri = FavsContract.FavsEntry.CONTENT_URI;
        uri = uri.buildUpon().appendPath(movieId).build();
        getContentResolver().delete(uri, null, null);
        Log.d("Remove Fav", "Uri delete: " + uri.toString());
    }
}
