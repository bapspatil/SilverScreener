package bapspatil.silverscreener.ui;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.miguelcatalan.materialsearchview.MaterialSearchView;

import org.aviran.cookiebar2.CookieBar;

import java.util.ArrayList;

import bapspatil.silverscreener.BuildConfig;
import bapspatil.silverscreener.R;
import bapspatil.silverscreener.adapters.MovieRecyclerViewAdapter;
import bapspatil.silverscreener.data.RealmDataSource;
import bapspatil.silverscreener.model.Movie;
import bapspatil.silverscreener.model.MovieRecyclerView;
import bapspatil.silverscreener.model.TMDBResponse;
import bapspatil.silverscreener.network.RetrofitAPI;
import butterknife.BindView;
import butterknife.ButterKnife;
import it.gmariotti.recyclerview.adapter.ScaleInAnimatorAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements MovieRecyclerViewAdapter.ItemClickListener {
    private static final int SEARCH_TASK = 0, POPULAR_TASK = 1, TOP_RATED_TASK = 2, UPCOMING_TASK = 3, NOW_PLAYING_TASK = 4;

    private MovieRecyclerViewAdapter mAdapter;
    private ArrayList<Movie> movieArray = new ArrayList<>();
    private Context mContext;

    @BindView(R.id.main_toolbar) Toolbar toolbar;
    @BindView(R.id.loading_indicator) ProgressBar mProgressBar;
    @BindView(R.id.rv_movies) MovieRecyclerView mRecyclerView;
    @BindView(R.id.bottom_navigation) BottomNavigationView bottomNavigationView;
    @BindView(R.id.search_view) MaterialSearchView searchView;

    private RealmDataSource dataSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mContext = getApplicationContext();
        toolbar.setLogo(R.mipmap.titlebar_logo);
        setSupportActionBar(toolbar);
        CookieBar.Build(MainActivity.this)
                .setLayoutGravity(Gravity.BOTTOM)
                .setBackgroundColor(R.color.colorPrimaryDark)
                .setTitle("App developed by Bapusaheb Patil")
                .show();

        dataSource = new RealmDataSource();
        dataSource.open();

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

        fetchMovies(POPULAR_TASK, null);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_popular:
                        fetchMovies(POPULAR_TASK, null);
                        break;
                    case R.id.action_rated:
                        fetchMovies(TOP_RATED_TASK, null);
                        break;
                    case R.id.action_upcoming:
                        fetchMovies(UPCOMING_TASK, null);
                        break;
                    case R.id.action_now:
                        fetchMovies(NOW_PLAYING_TASK, null);
                        break;
                    case R.id.action_favorites:
                        fetchFavs();
                        break;
                    default:
                        fetchMovies(POPULAR_TASK, null);
                }
                return true;
            }
        });

        searchView = findViewById(R.id.search_view);
        searchView.setCursorDrawable(R.drawable.cursor_search);
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                fetchMovies(SEARCH_TASK, query);
                searchView.closeSearch();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {
                bottomNavigationView.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onSearchViewClosed() {
                bottomNavigationView.setVisibility(View.VISIBLE);
            }
        });

    }

    private void fetchMovies(int taskId, String taskQuery) {
        mRecyclerView.setVisibility(View.INVISIBLE);
        mProgressBar.setVisibility(View.VISIBLE);
        RetrofitAPI retrofitAPI = RetrofitAPI.retrofit.create(RetrofitAPI.class);
        Call<TMDBResponse> call;
        switch (taskId) {
            case SEARCH_TASK:
                call = retrofitAPI.searchMovies(BuildConfig.TMDB_API_TOKEN, "en-US", 1, taskQuery);
                break;
            case POPULAR_TASK:
                call = retrofitAPI.getMovies("popular", BuildConfig.TMDB_API_TOKEN, "en-US", 1);
                break;
            case TOP_RATED_TASK:
                call = retrofitAPI.getMovies("top_rated", BuildConfig.TMDB_API_TOKEN, "en-US", 1);
                break;
            case UPCOMING_TASK:
                call = retrofitAPI.getMovies("upcoming", BuildConfig.TMDB_API_TOKEN, "en-US", 1);
                break;
            case NOW_PLAYING_TASK:
                call = retrofitAPI.getMovies("now_playing", BuildConfig.TMDB_API_TOKEN, "en-US", 1);
                break;
            default:
                call = retrofitAPI.getMovies("popular", BuildConfig.TMDB_API_TOKEN, "en-US", 1);
        }
        call.enqueue(new Callback<TMDBResponse>() {
            @Override
            public void onResponse(Call<TMDBResponse> call, Response<TMDBResponse> response) {
                TMDBResponse tmdbResponse = response.body();
                movieArray.clear();
                movieArray.addAll(tmdbResponse.getResults());
                mAdapter.notifyDataSetChanged();
                mRecyclerView.setVisibility(View.VISIBLE);
                mProgressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onFailure(Call<TMDBResponse> call, Throwable t) {
                Toast.makeText(mContext, "Error!", Toast.LENGTH_LONG).show();
                mRecyclerView.setVisibility(View.INVISIBLE);
                mProgressBar.setVisibility(View.GONE);
            }
        });
    }

    private void fetchFavs() {
        mRecyclerView.setVisibility(View.INVISIBLE);
        mProgressBar.setVisibility(View.VISIBLE);
        movieArray.clear();
        movieArray.addAll(dataSource.getAllFavMovies());
        mAdapter.notifyDataSetChanged();
        mRecyclerView.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.INVISIBLE);
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
    public void onItemClick(int position, CardView posterCardView) {
        Movie movie;
        movie = movieArray.get(position);
        Intent startDetailsActivity = new Intent(mContext, DetailsActivity.class);
        startDetailsActivity.putExtra("movie", movie);
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, posterCardView, "posterTransition");
        startActivity(startDetailsActivity, options.toBundle());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        MenuItem item = menu.findItem(R.id.action_search);
        searchView.setMenuItem(item);

        return true;
    }

    @Override
    public void onBackPressed() {
        if (searchView.isSearchOpen())
            searchView.closeSearch();
        else
            super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dataSource.close();
    }
}
