package bapspatil.silverscreener;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;

import bapspatil.silverscreener.data.Connection;
import bapspatil.silverscreener.data.FavsContract;
import butterknife.BindView;
import butterknife.ButterKnife;
import it.gmariotti.recyclerview.adapter.ScaleInAnimatorAdapter;

public class MainActivity extends AppCompatActivity implements MovieRecyclerViewAdapter.ItemClickListener {

    private MovieRecyclerViewAdapter mAdapter;
    private ArrayList<Movie> movieArray = new ArrayList<>();
    private String MOVIE_URL_POPULAR = "http://api.themoviedb.org/3/movie/popular";
    private String MOVIE_URL_RATED = "http://api.themoviedb.org/3/movie/top_rated";
    private String MOVIE_URL_UPCOMING = "http://api.themoviedb.org/3/movie/upcoming";
    private String MOVIE_URL_NOW = "http://api.themoviedb.org/3/movie/now_playing";
    private String MOVIE_POSTER_URL = "http://image.tmdb.org/t/p/w342";
    private String MOVIE_BACKDROP_URL = "http://image.tmdb.org/t/p/w300";
    private Context mContext;
    private GetTheMoviesTask getTheMoviesTask;
    private GetTheFavsTask getTheFavsTask;

    @BindView(R.id.main_toolbar) Toolbar toolbar;
    @BindView(R.id.loading_indicator) ProgressBar mProgressBar;
    @BindView(R.id.rv_movies) MovieRecyclerView mRecyclerView;
    @BindView(R.id.bottom_navigation) BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mContext = getApplicationContext();
        toolbar.setLogo(R.mipmap.titlebar_logo);
        setSupportActionBar(toolbar);
        Toast.makeText(mContext, "App developed by Bapusaheb Patil", Toast.LENGTH_SHORT).show();

        int columns = 2;
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
            columns = 4;
        GridLayoutManager gridLayoutManager = new GridLayoutManager(mContext, columns);
        mRecyclerView.setLayoutManager(gridLayoutManager);

        if (savedInstanceState != null) {
            movieArray.clear();
            for (int i = 0; i < savedInstanceState.getInt("noOfMovies"); i++) {
                Movie movie;
                movie = savedInstanceState.getParcelable("movieParcel" + i);
                movieArray.add(movie);
            }
        }

        mAdapter = new MovieRecyclerViewAdapter(mContext, movieArray, this);
        ScaleInAnimatorAdapter<MovieRecyclerViewAdapter.MovieViewHolder> animatorAdapter = new ScaleInAnimatorAdapter<>(mAdapter, mRecyclerView);
        mRecyclerView.setAdapter(animatorAdapter);

        getTheMoviesTask = new GetTheMoviesTask();
        getTheMoviesTask.execute(MOVIE_URL_POPULAR);
        getTheFavsTask = new GetTheFavsTask();

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                String stringURL;
                getTheMoviesTask.cancel(true);
                getTheMoviesTask = new GetTheMoviesTask();
                getTheFavsTask.cancel(true);
                getTheFavsTask = new GetTheFavsTask();
                switch (item.getItemId()) {
                    case R.id.action_popular:
                        stringURL = MOVIE_URL_POPULAR;
                        getTheMoviesTask.execute(stringURL);
                        break;
                    case R.id.action_rated:
                        stringURL = MOVIE_URL_RATED;
                        getTheMoviesTask.execute(stringURL);
                        break;
                    case R.id.action_upcoming:
                        stringURL = MOVIE_URL_UPCOMING;
                        getTheMoviesTask.execute(stringURL);
                        break;
                    case R.id.action_now:
                        stringURL = MOVIE_URL_NOW;
                        getTheMoviesTask.execute(stringURL);
                        break;
                    case R.id.action_favorites:
                        getTheFavsTask.execute();
                        break;
                    default:
                        stringURL = MOVIE_URL_POPULAR;
                        getTheMoviesTask.execute(stringURL);
                }
                return true;
            }
        });

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        int numberOfMovies = movieArray.size();
        for (int i = 0; i < numberOfMovies; i++) {
            Movie movie = new Movie();
            outState.putParcelable("movieParcel" + i, movie);
        }
        outState.putInt("noOfMovies", numberOfMovies);
        super.onSaveInstanceState(outState);
    }


    @Override
    public void onItemClick(int position, ImageView posterImageView) {
        Movie movie;
        movie = movieArray.get(position);
        Intent startDetailsActivity = new Intent(mContext, DetailsActivity.class);
        startDetailsActivity.putExtra("movie", movie);
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, posterImageView, "posterTransition");
        startActivity(startDetailsActivity, options.toBundle());
    }

    private class GetTheMoviesTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            mProgressBar.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.INVISIBLE);
            if (!Connection.hasNetwork(mContext)) {
                cancel(true);
                mProgressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(mContext, "No Internet Connection", Toast.LENGTH_SHORT).show();

            }
        }


        @Override
        protected String doInBackground(String... params) {
            Uri builtUri = Uri.parse(params[0]).buildUpon()
                    .appendQueryParameter("api_key", BuildConfig.TMDB_API_TOKEN)
                    .appendQueryParameter("language", "en-US")
                    .build();
            String jsonResponse;
            try {
                jsonResponse = Connection.getResponseFromHttpUrl(new URL(builtUri.toString()));
                return jsonResponse;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String jsonResponse) {
            movieArray.clear();
            try {
                JSONObject jsonMoviesObject = new JSONObject(jsonResponse);
                JSONArray jsonMoviesArray = jsonMoviesObject.getJSONArray("results");
                for (int i = 0; i < jsonMoviesArray.length(); i++) {
                    JSONObject jsonMovie = jsonMoviesArray.getJSONObject(i);
                    Movie movie = new Movie();
                    movie.setPosterPath(MOVIE_POSTER_URL + jsonMovie.getString("poster_path"));
                    movie.setTitle(jsonMovie.getString("title"));
                    movie.setPlot(jsonMovie.getString("overview"));
                    movie.setDate(jsonMovie.getString("release_date"));
                    movie.setId(jsonMovie.getInt("id"));
                    movie.setRating(jsonMovie.getString("vote_average"));
                    movieArray.add(movie);
                    mAdapter.notifyDataSetChanged();
                }

            } catch (Exception e) {
                Toast.makeText(mContext, "Error in the movie data fetched!", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
            mProgressBar.setVisibility(View.INVISIBLE);
            mRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    private class GetTheFavsTask extends AsyncTask<Void, Void, Cursor> {

        @Override
        protected void onPreExecute() {
            mProgressBar.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.INVISIBLE);
        }

        @Override
        protected Cursor doInBackground(Void... params) {
            Cursor cursor;
            try {
                cursor = getContentResolver().query(FavsContract.FavsEntry.CONTENT_URI,
                        null,
                        null,
                        null,
                        null);
                return cursor;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(Cursor cursor) {
            movieArray.clear();
            if (cursor.getCount() != 0) {
                cursor.moveToFirst();
                for (int i = 0; i < cursor.getCount(); i++) {
                    Movie movie = new Movie();
                    movie.setId(cursor.getInt(cursor.getColumnIndex(FavsContract.FavsEntry._ID)));
                    movie.setTitle(cursor.getString(cursor.getColumnIndex(FavsContract.FavsEntry.COLUMN_TITLE)));
                    movie.setDate(cursor.getString(cursor.getColumnIndex(FavsContract.FavsEntry.COLUMN_DATE)));
                    movie.setPlot(cursor.getString(cursor.getColumnIndex(FavsContract.FavsEntry.COLUMN_PLOT)));
                    movie.setRating(cursor.getString(cursor.getColumnIndex(FavsContract.FavsEntry.COLUMN_RATING)));
                    movie.setPosterBytes(cursor.getBlob(cursor.getColumnIndex(FavsContract.FavsEntry.COLUMN_POSTER)));
                    movie.setPosterPath(cursor.getString(cursor.getColumnIndex(FavsContract.FavsEntry.COLUMN_POSTERPATH)));
                    movieArray.add(movie);
                    mAdapter.notifyDataSetChanged();
                    cursor.moveToNext();
                }
            } else {
                mAdapter.notifyDataSetChanged();
                Toast.makeText(mContext, "No favorites!", Toast.LENGTH_SHORT).show();
            }
            mProgressBar.setVisibility(View.INVISIBLE);
            mRecyclerView.setVisibility(View.VISIBLE);
        }
    }

}
