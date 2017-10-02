package bapspatil.silverscreener;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;

import bapspatil.silverscreener.data.Connection;
import bapspatil.silverscreener.data.FavsContract;
import it.gmariotti.recyclerview.adapter.SlideInBottomAnimatorAdapter;

public class MainActivity extends AppCompatActivity implements MovieRecyclerViewAdapter.ItemClickListener {

    private MovieRecyclerViewAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private ProgressBar mProgressBar;
    private ArrayList<Movie> movieArray = new ArrayList<>();
    private String MOVIE_URL_POPULAR = "http://api.themoviedb.org/3/movie/popular";
    private String MOVIE_URL_RATED = "http://api.themoviedb.org/3/movie/top_rated";
    private String MOVIE_POSTER_URL = "http://image.tmdb.org/t/p/w500";
    private String MOVIE_BACKDROP_URL = "http://image.tmdb.org/t/p/w780";
    private Context mContext;
    public GetTheMoviesTask getTheMoviesTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = getApplicationContext();
        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        toolbar.setLogo(R.mipmap.titlebar_logo);
        setSupportActionBar(toolbar);
        Toast.makeText(mContext, "App developed by Bapusaheb Patil", Toast.LENGTH_SHORT).show();

        mProgressBar = (ProgressBar) findViewById(R.id.loading_indicator);
        final Spinner mSpinner = (Spinner) findViewById(R.id.sort_spinner);
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_movies);
        int columns = 2;
        mRecyclerView.setLayoutManager(new GridLayoutManager(mContext, columns));

        mAdapter = new MovieRecyclerViewAdapter(mContext, movieArray, this);
        SlideInBottomAnimatorAdapter animatorAdapter = new SlideInBottomAnimatorAdapter(mAdapter, mRecyclerView);
        mRecyclerView.setAdapter(animatorAdapter);

        getTheMoviesTask = new GetTheMoviesTask();
        getTheMoviesTask.execute(MOVIE_URL_POPULAR);

        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected = parent.getItemAtPosition(position).toString();
                getTheMoviesTask.cancel(true);
                getTheMoviesTask = new GetTheMoviesTask();
                if (selected.equals("Most Popular")) {
                    getTheMoviesTask.execute(MOVIE_URL_POPULAR);
                } else if (selected.equals("Highest Rated")) {
                    getTheMoviesTask.execute(MOVIE_URL_RATED);
                } else {

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                String selected = mSpinner.getSelectedItem().toString();
                getTheMoviesTask.cancel(true);
                getTheMoviesTask = new GetTheMoviesTask();
                if (selected.equals("Most Popular")) {
                    getTheMoviesTask.execute(MOVIE_URL_POPULAR);
                } else if (selected.equals("Highest Rated")) {
                    getTheMoviesTask.execute(MOVIE_URL_RATED);
                } else {

                }
                swipeRefreshLayout.setRefreshing(false);
            }
        });

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
            mProgressBar.setVisibility(View.INVISIBLE);
            try {
                JSONObject jsonMoviesObject = new JSONObject(jsonResponse);
                JSONArray jsonMoviesArray = jsonMoviesObject.getJSONArray("results");
                for (int i = 0; i < jsonMoviesArray.length(); i++) {
                    JSONObject jsonMovie = jsonMoviesArray.getJSONObject(i);
                    Movie movie = new Movie();
                    movie.setPosterPath(MOVIE_POSTER_URL + jsonMovie.getString("poster_path"));
                    movie.setBackdropPath(MOVIE_BACKDROP_URL + jsonMovie.getString("backdrop_path"));
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
        }
    }

    private class GetTheFavsTask extends AsyncTask<Void, Void, Cursor> {

        @Override
        protected void onPreExecute() {
            mProgressBar.setVisibility(View.VISIBLE);
            Toast.makeText(mContext, "Showing your favorites...", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected Cursor doInBackground(Void... params) {
            // TODO Add query for favs from MainActivity here
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
            mProgressBar.setVisibility(View.INVISIBLE);
            /*try {
                JSONObject jsonMoviesObject = new JSONObject(jsonResponse);
                JSONArray jsonMoviesArray = jsonMoviesObject.getJSONArray("results");
                for (int i = 0; i < jsonMoviesArray.length(); i++) {
                    JSONObject jsonMovie = jsonMoviesArray.getJSONObject(i);
                    Movie movie = new Movie();
                    movie.setPosterPath(MOVIE_POSTER_URL + jsonMovie.getString("poster_path"));
                    movie.setBackdropPath(MOVIE_BACKDROP_URL + jsonMovie.getString("backdrop_path"));
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
            }*/
        }
    }

}
