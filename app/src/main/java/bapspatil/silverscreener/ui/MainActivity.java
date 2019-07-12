package bapspatil.silverscreener.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.transition.Slide;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;
import com.google.android.material.bottomnavigation.BottomNavigationView;

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
import bapspatil.silverscreener.utils.NetworkUtils;
import butterknife.BindView;
import butterknife.ButterKnife;
import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements MovieRecyclerViewAdapter.ItemClickListener {
    private static final int SEARCH_TASK = 0, POPULAR_TASK = 1, TOP_RATED_TASK = 2, UPCOMING_TASK = 3, NOW_PLAYING_TASK = 4;
    private final int VOICE_RECOGNITION_REQUEST_CODE = 13;
    @BindView(R.id.loading_indicator)
    ProgressBar mProgressBar;
    @BindView(R.id.rv_movies)
    MovieRecyclerView mRecyclerView;
    @BindView(R.id.bottom_navigation)
    BottomNavigationView bottomNavigationView;
    @BindView(R.id.search_view)
    FloatingSearchView searchView;
    private MovieRecyclerViewAdapter mAdapter;
    private ArrayList<Movie> movieArray = new ArrayList<>();
    private Context mContext;
    private RealmDataSource dataSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimary));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.colorPrimary));
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Slide slide = new Slide(Gravity.LEFT);
            getWindow().setExitTransition(slide);
        }
        mContext = getApplicationContext();
        CookieBar.build(MainActivity.this)
                .setLayoutGravity(Gravity.BOTTOM)
                .setBackgroundColor(R.color.colorAccent)
                .setTitleColor(R.color.colorPrimary)
                .setTitle("App developed by Bapusaheb Patil")
                .show();

        dataSource = new RealmDataSource();
        dataSource.open();

        int columns = 2;
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
            columns = 4;
        GridLayoutManager gridLayoutManager = new GridLayoutManager(mContext, columns);
        mRecyclerView.setLayoutManager(gridLayoutManager);

        mAdapter = new MovieRecyclerViewAdapter(mContext, movieArray, this);
        mRecyclerView.setAdapter(new ScaleInAnimationAdapter(mAdapter));

        fetchMovies(POPULAR_TASK, null);

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.action_popular:
                    mRecyclerView.smoothScrollToPosition(0);
                    fetchMovies(POPULAR_TASK, null);
                    break;
                case R.id.action_rated:
                    mRecyclerView.smoothScrollToPosition(0);
                    fetchMovies(TOP_RATED_TASK, null);
                    break;
                case R.id.action_upcoming:
                    mRecyclerView.smoothScrollToPosition(0);
                    fetchMovies(UPCOMING_TASK, null);
                    break;
                case R.id.action_now:
                    mRecyclerView.smoothScrollToPosition(0);
                    fetchMovies(NOW_PLAYING_TASK, null);
                    break;
                case R.id.action_favorites:
                    mRecyclerView.smoothScrollToPosition(0);
                    fetchFavs();
                    break;
                default:
                    fetchMovies(POPULAR_TASK, null);
            }
            return true;
        });
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) { // Scrolled up
                    bottomNavigationView.setVisibility(View.GONE);
                } else {
                    bottomNavigationView.setVisibility(View.VISIBLE);
                }
            }
        });

        searchView = findViewById(R.id.search_view);
        searchView.setOnSearchListener(new FloatingSearchView.OnSearchListener() {
            @Override
            public void onSuggestionClicked(SearchSuggestion searchSuggestion) {

            }

            @Override
            public void onSearchAction(String currentQuery) {
                mRecyclerView.smoothScrollToPosition(0);
                fetchMovies(SEARCH_TASK, currentQuery);
                searchView.clearQuery();
            }
        });
        searchView.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.action_about_me:
                    Intent intentToAboutMe = new Intent(this, AboutMeActivity.class);
                    ActivityOptionsCompat options = ActivityOptionsCompat.makeCustomAnimation(this, android.R.anim.fade_in, android.R.anim.fade_out);
                    startActivity(intentToAboutMe, options.toBundle());
                    break;
                case R.id.action_voice_search:
                    startVoiceRecognition();
            }
        });
    }

    private void fetchMovies(int taskId, String taskQuery) {
        mRecyclerView.setVisibility(View.INVISIBLE);
        mProgressBar.setVisibility(View.VISIBLE);
        RetrofitAPI retrofitAPI = NetworkUtils.getCacheEnabledRetrofit(getApplicationContext()).create(RetrofitAPI.class);
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
                if (tmdbResponse != null) {
                    movieArray.addAll(tmdbResponse.getResults());
                    mAdapter.notifyDataSetChanged();
                }
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
    public void onItemClick(int position, ImageView posterImageView) {
        Movie movie;
        movie = movieArray.get(position);
        Intent startDetailsActivity = new Intent(mContext, DetailsActivity.class);
        startDetailsActivity.putExtra("movie", movie);
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, posterImageView, "posterTransition");
        startActivity(startDetailsActivity, options.toBundle());
    }

    private void startVoiceRecognition() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Voice searching...");
        startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == VOICE_RECOGNITION_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (matches != null) {
                if (!matches.isEmpty()) {
                    String query = matches.get(0);
                    fetchMovies(SEARCH_TASK, query);
                    Toast.makeText(this, "Searching for " + query + "...", Toast.LENGTH_LONG).show();
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        if (searchView.isSearchBarFocused())
            searchView.clearQuery();
        else
            super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dataSource.close();
    }
}
