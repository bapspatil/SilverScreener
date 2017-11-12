package bapspatil.silverscreener.ui;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.Request;
import com.bumptech.glide.request.target.SizeReadyCallback;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ExecutionException;

import bapspatil.silverscreener.BuildConfig;
import bapspatil.silverscreener.R;
import bapspatil.silverscreener.adapters.ReviewRecyclerViewAdapter;
import bapspatil.silverscreener.adapters.TrailerRecyclerViewAdapter;
import bapspatil.silverscreener.data.FavsContract;
import bapspatil.silverscreener.model.Movie;
import bapspatil.silverscreener.model.MovieRecyclerView;
import bapspatil.silverscreener.network.Connection;
import bapspatil.silverscreener.network.RetrofitAPI;
import bapspatil.silverscreener.utils.GlideApp;
import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailsActivity extends AppCompatActivity implements TrailerRecyclerViewAdapter.ItemClickListener {

    private TrailerRecyclerViewAdapter mTrailerAdapter;
    private ReviewRecyclerViewAdapter mReviewAdapter;
    private Context mContext;
    private ArrayList<String> mTrailerTitles = new ArrayList<>();
    private ArrayList<String> mTrailerPaths = new ArrayList<>();
    private ArrayList<String> mReviewAuthors = new ArrayList<>();
    private ArrayList<String> mReviewContents = new ArrayList<>();
    private byte[] imageBytes;

    @BindView(R.id.details_toolbar) Toolbar toolbar;
    @BindView(R.id.trailer_label_tv) TextView mTrailersLabel0;
    @BindView(R.id.trailers_hint_tv) TextView mTrailersLabel1;
    @BindView(R.id.reviews_label_tv) TextView mReviewsLabel0;
    @BindView(R.id.no_reviews_cv) CardView noReviewsCardView;
    @BindView(R.id.rating_value_tv) TextView mRatingTextView;
    @BindView(R.id.date_value_tv) TextView mDateTextView;
    @BindView(R.id.title_tv) TextView mTitleTextView;
    @BindView(R.id.plot_tv) TextView mPlotTextView;
    @BindView(R.id.poster_image_view) ImageView mPosterImageView;
    @BindView(R.id.rv_trailers) MovieRecyclerView mTrailerRecyclerView;
    @BindView(R.id.rv_reviews) MovieRecyclerView mReviewRecyclerView;
    @BindView(R.id.fav_button) FloatingActionButton mFavoriteButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        ButterKnife.bind(this);
        mContext = getApplicationContext();
        toolbar.setLogo(R.mipmap.titlebar_logo);
        setSupportActionBar(toolbar);
        Movie movie = getIntent().getParcelableExtra("movie");

        mRatingTextView.setText(movie.getRating());
        mDateTextView.setText(prettifyDate(movie.getDate()));
        mTitleTextView.setText(movie.getTitle());
        mPlotTextView.setText(movie.getPlot());

        if (Connection.hasNetwork(mContext)) {
            GlideApp.with(mContext)
                    .load(movie.getPosterPath())
                    .centerCrop()
                    .into(new Target<Drawable>() {
                        @Override
                        public void onLoadStarted(@Nullable Drawable placeholder) {

                        }

                        @Override
                        public void onLoadFailed(@Nullable Drawable errorDrawable) {

                        }

                        @Override
                        public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {
                            Bitmap bitmap = ((BitmapDrawable) resource).getBitmap();
                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                            imageBytes = stream.toByteArray();
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {

                        }

                        @Override
                        public void getSize(SizeReadyCallback cb) {

                        }

                        @Override
                        public void removeCallback(SizeReadyCallback cb) {

                        }

                        @Override
                        public void setRequest(@Nullable Request request) {

                        }

                        @Nullable
                        @Override
                        public Request getRequest() {
                            return null;
                        }

                        @Override
                        public void onStart() {

                        }

                        @Override
                        public void onStop() {

                        }

                        @Override
                        public void onDestroy() {

                        }
                    });
        }

        if (movie.getPosterBytes() != null) {
            GlideApp.with(getApplicationContext())
                    .load(movie.getPosterBytes())
                    .error(R.drawable.no_internet_placeholder)
                    .fallback(R.drawable.no_internet_placeholder)
                    .centerCrop()
                    .transition(new DrawableTransitionOptions().crossFade())
                    .into(mPosterImageView);
        } else {
            GlideApp.with(mContext)
                    .load(RetrofitAPI.POSTER_BASE_URL + movie.getPosterPath())
                    .error(R.drawable.no_internet_placeholder)
                    .fallback(R.drawable.no_internet_placeholder)
                    .centerCrop()
                    .transition(new DrawableTransitionOptions().crossFade())
                    .into(mPosterImageView);
        }

        (new CheckIfFavoritedTask()).execute(movie);

        mFavoriteButton.setOnClickListener(new View.OnClickListener() {
            Movie movie = getIntent().getParcelableExtra("movie");

            @Override
            public void onClick(View v) {
                AddRemoveFavoritesTask addRemoveFavoritesTask = new AddRemoveFavoritesTask();
                if (addRemoveFavoritesTask.getStatus() == AsyncTask.Status.RUNNING)
                    return;
                else
                    addRemoveFavoritesTask.execute(movie);
            }
        });

        mTrailerRecyclerView.setLayoutManager(new LinearLayoutManager(DetailsActivity.this, LinearLayoutManager.HORIZONTAL, false));
        mTrailerAdapter = new TrailerRecyclerViewAdapter(mContext, mTrailerTitles, mTrailerPaths, this);
        mTrailerRecyclerView.setAdapter(mTrailerAdapter);

        mReviewRecyclerView.setLayoutManager(new LinearLayoutManager(DetailsActivity.this, LinearLayoutManager.VERTICAL, false));
        mReviewAdapter = new ReviewRecyclerViewAdapter(mContext, mReviewAuthors, mReviewContents);
        mReviewRecyclerView.setAdapter(mReviewAdapter);

        (new GetTheTrailersTask()).execute(movie.getId());
        (new GetTheReviewsTask()).execute(movie.getId());
    }

    @Override
    public void onItemClick(String stringUrlTrailerClicked) {
        Uri youtubeUri = Uri.parse("https://www.youtube.com/watch?v=" + stringUrlTrailerClicked);
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
                    mFavoriteButton.setImageResource(R.drawable.ic_favorite);
                } else {
                    Toast.makeText(mContext, "Movie removed from Favorites! :-(", Toast.LENGTH_SHORT).show();
                    mFavoriteButton.setImageResource(R.drawable.ic_favorite_border);
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
                    mTrailerPaths.add(jsonTrailer.getString("key"));
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
                if (jsonReviewsArray.length() != 0) {
                    for (int i = 0; i < jsonReviewsArray.length(); i++) {
                        JSONObject jsonReview = jsonReviewsArray.getJSONObject(i);
                        mReviewAuthors.add(jsonReview.getString("author"));
                        mReviewContents.add(jsonReview.getString("content"));
                        mReviewAdapter.notifyDataSetChanged();
                    }
                    noReviewsCardView.setVisibility(View.INVISIBLE);
                } else {
                    noReviewsCardView.setVisibility(View.VISIBLE);
                    mReviewRecyclerView.setVisibility(View.INVISIBLE);
                }
            } catch (Exception e) {
                Toast.makeText(mContext, "Error in the review data fetched!", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
    }

    private class CheckIfFavoritedTask extends AsyncTask<Movie, Void, Cursor> {
        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Cursor doInBackground(Movie... movie) {
            String title = movie[0].getTitle();
            String[] movieTitle = {title};
            return getContentResolver().query(FavsContract.FavsEntry.CONTENT_URI,
                    null,
                    FavsContract.FavsEntry.COLUMN_TITLE + "=?",
                    movieTitle,
                    null);
        }

        @Override
        protected void onPostExecute(Cursor cursor) {
            if (cursor.getCount() == 0)
                mFavoriteButton.setImageResource(R.drawable.ic_favorite_border);
            else
                mFavoriteButton.setImageResource(R.drawable.ic_favorite);
        }
    }

    void addMovieToFavorites(Movie movie) throws ExecutionException, InterruptedException {
        ContentValues cv = new ContentValues();
        cv.put(FavsContract.FavsEntry._ID, String.valueOf(movie.getId()));
        cv.put(FavsContract.FavsEntry.COLUMN_TITLE, movie.getTitle());
        cv.put(FavsContract.FavsEntry.COLUMN_PLOT, movie.getPlot());
        cv.put(FavsContract.FavsEntry.COLUMN_RATING, movie.getRating());
        cv.put(FavsContract.FavsEntry.COLUMN_DATE, movie.getDate());
        cv.put(FavsContract.FavsEntry.COLUMN_POSTERPATH, movie.getPosterPath());
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

    private String prettifyDate(String jsonDate) {
        DateFormat sourceDateFormat = new SimpleDateFormat("YYYY-MM-dd");
        Date date = null;
        try {
            date = sourceDateFormat.parse(jsonDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        DateFormat destDateFormat = new SimpleDateFormat("MMM dd\nYYYY");
        String dateStr = destDateFormat.format(date);
        return dateStr;
    }


}
