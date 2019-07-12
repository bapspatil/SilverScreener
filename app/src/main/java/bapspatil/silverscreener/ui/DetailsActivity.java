package bapspatil.silverscreener.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.transition.Slide;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NavUtils;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.Request;
import com.bumptech.glide.request.target.SizeReadyCallback;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.aviran.cookiebar2.CookieBar;

import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import bapspatil.silverscreener.BuildConfig;
import bapspatil.silverscreener.R;
import bapspatil.silverscreener.adapters.CastRecyclerViewAdapter;
import bapspatil.silverscreener.adapters.GenresRecyclerViewAdapter;
import bapspatil.silverscreener.adapters.MovieRecyclerViewAdapter;
import bapspatil.silverscreener.adapters.ReviewRecyclerViewAdapter;
import bapspatil.silverscreener.adapters.TrailerRecyclerViewAdapter;
import bapspatil.silverscreener.data.RealmDataSource;
import bapspatil.silverscreener.model.Cast;
import bapspatil.silverscreener.model.Crew;
import bapspatil.silverscreener.model.GenresItem;
import bapspatil.silverscreener.model.Movie;
import bapspatil.silverscreener.model.MovieRecyclerView;
import bapspatil.silverscreener.model.Review;
import bapspatil.silverscreener.model.TMDBCreditsResponse;
import bapspatil.silverscreener.model.TMDBDetailsResponse;
import bapspatil.silverscreener.model.TMDBResponse;
import bapspatil.silverscreener.model.TMDBReviewResponse;
import bapspatil.silverscreener.model.TMDBTrailerResponse;
import bapspatil.silverscreener.model.Trailer;
import bapspatil.silverscreener.network.RetrofitAPI;
import bapspatil.silverscreener.utils.GlideApp;
import bapspatil.silverscreener.utils.NetworkUtils;
import butterknife.BindView;
import butterknife.ButterKnife;
import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailsActivity extends AppCompatActivity implements TrailerRecyclerViewAdapter.ItemClickListener {
    private static final int TRAILERS_DETAILS_TYPE = 0, REVIEWS_DETAILS_TYPE = 1;
    Movie tempMovie, mMovie;
    @BindView(R.id.appbar)
    AppBarLayout appBarLayout;
    @BindView(R.id.collapsing_toolbar_layout)
    CollapsingToolbarLayout collapsingToolbarLayout;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.trailer_label_tv)
    TextView mTrailersLabel0;
    @BindView(R.id.trailers_hint_tv)
    TextView mTrailersLabel1;
    @BindView(R.id.reviews_label_tv)
    TextView mReviewsLabel0;
    @BindView(R.id.rating_value_tv)
    TextView mRatingTextView;
    @BindView(R.id.date_value_tv)
    TextView mDateTextView;
    @BindView(R.id.title_tv)
    TextView mTitleTextView;
    @BindView(R.id.plot_tv)
    TextView mPlotTextView;
    @BindView(R.id.poster_image_view)
    ImageView mPosterImageView;
    @BindView(R.id.rv_trailers)
    MovieRecyclerView mTrailerRecyclerView;
    @BindView(R.id.rv_reviews)
    MovieRecyclerView mReviewRecyclerView;
    @BindView(R.id.fav_button)
    FloatingActionButton mFavoriteButton;
    @BindView(R.id.backdrop_iv)
    ImageView mBackdropImageView;
    @BindView(R.id.director_value_tv)
    TextView mDirectorTextView;
    @BindView(R.id.cast_rv)
    RecyclerView mCastRecyclerView;
    @BindView(R.id.tagline_tv)
    TextView mTaglineTextView;
    @BindView(R.id.votes_value_tv)
    TextView mVotesTextView;
    @BindView(R.id.minutes_value_tv)
    TextView mMinutesTextView;
    @BindView(R.id.imdb_value_tv)
    ImageButton mImdbButton;
    @BindView(R.id.genres_rv)
    RecyclerView mGenresRecyclerView;
    @BindView(R.id.similar_movies_rv)
    RecyclerView mSimilarMoviesRecyclerView;
    private TrailerRecyclerViewAdapter mTrailerAdapter;
    private ReviewRecyclerViewAdapter mReviewAdapter;
    private GenresRecyclerViewAdapter mGenreAdapter;
    private MovieRecyclerViewAdapter mSimilarMoviesAdapter;
    private Context mContext;
    private ArrayList<String> mTrailerTitles = new ArrayList<>();
    private ArrayList<String> mTrailerPaths = new ArrayList<>();
    private ArrayList<String> mReviewAuthors = new ArrayList<>();
    private ArrayList<String> mReviewContents = new ArrayList<>();
    private ArrayList<GenresItem> mGenres = new ArrayList<>();
    private ArrayList<Movie> mSimilarMovies = new ArrayList<>();
    private byte[] imageBytes;
    private RealmDataSource dataSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        ButterKnife.bind(this);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimary));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        }
        mContext = getApplicationContext();
        if (Build.VERSION.SDK_INT >= 21) {
            Slide slide = new Slide(Gravity.BOTTOM);
            getWindow().setEnterTransition(slide);
            postponeEnterTransition();
        }
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mMovie = getIntent().getParcelableExtra("movie");
        collapsingToolbarLayout.setTitle(mMovie.getTitle());
        collapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(android.R.color.transparent));
        collapsingToolbarLayout.setCollapsedTitleTextColor(getResources().getColor(android.R.color.white));
        appBarLayout.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> {
            if (Math.abs(verticalOffset) - appBarLayout.getTotalScrollRange() == 0)
                mPosterImageView.setVisibility(View.GONE);
            else
                mPosterImageView.setVisibility(View.VISIBLE);
        });

        dataSource = new RealmDataSource();
        dataSource.open();

        mRatingTextView.setText(mMovie.getRating());
        if (mMovie.getDate() != null && !mMovie.getDate().equals(""))
            mDateTextView.setText(prettifyDate(mMovie.getDate()));
        mTitleTextView.setText(mMovie.getTitle());
        mPlotTextView.setText(mMovie.getPlot());
        favButtonInit(mMovie.getId());
        GlideApp.with(getApplicationContext())
                .load(RetrofitAPI.BACKDROP_BASE_URL + mMovie.getBackdropPath())
                .centerCrop()
                .placeholder(R.drawable.tmdb_placeholder_land)
                .error(R.drawable.tmdb_placeholder_land)
                .fallback(R.drawable.tmdb_placeholder_land)
                .transition(new DrawableTransitionOptions().crossFade())
                .into(mBackdropImageView);
        if (mMovie.getPosterBytes() != null) {
            GlideApp.with(getApplicationContext())
                    .load(mMovie.getPosterBytes())
                    .centerCrop()
                    .error(R.drawable.tmdb_placeholder)
                    .fallback(R.drawable.tmdb_placeholder)
                    .transition(new DrawableTransitionOptions().crossFade())
                    .into(mPosterImageView);
        } else {
            GlideApp.with(mContext)
                    .load(RetrofitAPI.POSTER_BASE_URL + mMovie.getPosterPath())
                    .error(R.drawable.tmdb_placeholder)
                    .fallback(R.drawable.tmdb_placeholder)
                    .centerCrop()
                    .transition(new DrawableTransitionOptions().crossFade())
                    .into(mPosterImageView);
        }

        if (!NetworkUtils.hasNetwork(mContext)) {
            (findViewById(R.id.tagline_tv)).setVisibility(View.GONE);
            (findViewById(R.id.similar_label_tv)).setVisibility(View.GONE);
            (findViewById(R.id.cast_label_tv)).setVisibility(View.GONE);
            (findViewById(R.id.votes_label_tv)).setVisibility(View.GONE);
            (findViewById(R.id.votes_value_tv)).setVisibility(View.GONE);
            (findViewById(R.id.minutes_label_tv)).setVisibility(View.GONE);
            (findViewById(R.id.minutes_value_tv)).setVisibility(View.GONE);
            (findViewById(R.id.imdb_label_tv)).setVisibility(View.GONE);
            (findViewById(R.id.imdb_value_tv)).setVisibility(View.GONE);
            (findViewById(R.id.director_label_tv)).setVisibility(View.GONE);
            (findViewById(R.id.director_value_tv)).setVisibility(View.GONE);
            (findViewById(R.id.genres_label_tv)).setVisibility(View.GONE);
            (findViewById(R.id.trailers_hint_tv)).setVisibility(View.GONE);
            (findViewById(R.id.trailer_label_tv)).setVisibility(View.GONE);
            (findViewById(R.id.reviews_label_tv)).setVisibility(View.GONE);
        } else {
            fetchCredits();
            fetchMoreDetails();

            mTrailerRecyclerView.setLayoutManager(new LinearLayoutManager(DetailsActivity.this, RecyclerView.HORIZONTAL, false));
            mTrailerAdapter = new TrailerRecyclerViewAdapter(mContext, mTrailerTitles, mTrailerPaths, this);
            mTrailerRecyclerView.setAdapter(new ScaleInAnimationAdapter(mTrailerAdapter));

            mReviewRecyclerView.setLayoutManager(new LinearLayoutManager(DetailsActivity.this, RecyclerView.VERTICAL, false));
            mReviewAdapter = new ReviewRecyclerViewAdapter(mContext, mReviewAuthors, mReviewContents);
            mReviewRecyclerView.setAdapter(mReviewAdapter);

            mGenresRecyclerView.setLayoutManager(new LinearLayoutManager(DetailsActivity.this, RecyclerView.HORIZONTAL, false));
            mGenreAdapter = new GenresRecyclerViewAdapter(mContext, mGenres);
            mGenresRecyclerView.setAdapter(new ScaleInAnimationAdapter(mGenreAdapter));

            mSimilarMoviesRecyclerView.setLayoutManager(new LinearLayoutManager(DetailsActivity.this, RecyclerView.HORIZONTAL, false));
            mSimilarMoviesAdapter = new MovieRecyclerViewAdapter(mContext, mSimilarMovies, (position, posterImageView) -> CookieBar.build(DetailsActivity.this)
                    .setBackgroundColor(android.R.color.holo_green_dark)
                    .setTitle(mSimilarMovies.get(position).getTitle())
                    .setMessage("Rating: " + mSimilarMovies.get(position).getRating() + " \nRelease: " + mSimilarMovies.get(position).getDate())
                    .show());
            mSimilarMoviesRecyclerView.setAdapter(new ScaleInAnimationAdapter(mSimilarMoviesAdapter));

            fetchDetails(mMovie.getId(), TRAILERS_DETAILS_TYPE);
            fetchDetails(mMovie.getId(), REVIEWS_DETAILS_TYPE);
            fetchSimilarMovies(mMovie.getId());
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            startPostponedEnterTransition();
        }
    }

    private void fetchSimilarMovies(int id) {
        RetrofitAPI retrofitAPI = NetworkUtils.getCacheEnabledRetrofit(getApplicationContext()).create(RetrofitAPI.class);
        Call<TMDBResponse> similarMoviesCall = retrofitAPI.getSimilarMovies(id, BuildConfig.TMDB_API_TOKEN, "en-US");
        similarMoviesCall.enqueue(new Callback<TMDBResponse>() {
            @Override
            public void onResponse(Call<TMDBResponse> call, Response<TMDBResponse> response) {
                if (response.body() != null && response.body().getResults() != null && response.body().getResults().size() != 0) {
                    mSimilarMovies.addAll(response.body().getResults());
                    mSimilarMoviesAdapter.notifyDataSetChanged();
                } else {
                    (findViewById(R.id.similar_label_tv)).setVisibility(View.GONE);
                    mSimilarMoviesRecyclerView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<TMDBResponse> call, Throwable t) {
                // Do I really have to do this?
            }
        });
    }

    private void fetchDetails(int movieId, int detailsType) {
        RetrofitAPI retrofitAPI = NetworkUtils.getCacheEnabledRetrofit(getApplicationContext()).create(RetrofitAPI.class);
        switch (detailsType) {
            case TRAILERS_DETAILS_TYPE:
                mTrailerRecyclerView.setVisibility(View.GONE);
                mTrailersLabel0.setVisibility(View.GONE);
                mTrailersLabel1.setVisibility(View.GONE);
                Call<TMDBTrailerResponse> trailerResponseCall = retrofitAPI.getTrailers(movieId, BuildConfig.TMDB_API_TOKEN, "en-US");
                trailerResponseCall.enqueue(new Callback<TMDBTrailerResponse>() {
                    @Override
                    public void onResponse(Call<TMDBTrailerResponse> call, Response<TMDBTrailerResponse> response) {
                        TMDBTrailerResponse tmdbTrailerResponse = response.body();
                        if (tmdbTrailerResponse != null && tmdbTrailerResponse.getResults().size() != 0) {
                            mTrailerTitles.clear();
                            mTrailerPaths.clear();
                            for (Trailer trailer : tmdbTrailerResponse.getResults()) {
                                mTrailerTitles.add(trailer.getName());
                                mTrailerPaths.add(trailer.getKey());
                            }
                            mTrailerAdapter.notifyDataSetChanged();
                            mTrailerRecyclerView.setVisibility(View.VISIBLE);
                            mTrailersLabel0.setVisibility(View.VISIBLE);
                            mTrailersLabel1.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onFailure(Call<TMDBTrailerResponse> call, Throwable t) {

                    }
                });
                break;
            case REVIEWS_DETAILS_TYPE:
                mReviewsLabel0.setVisibility(View.GONE);
                mReviewRecyclerView.setVisibility(View.GONE);
                Call<TMDBReviewResponse> reviewResponseCall = retrofitAPI.getReviews(movieId, BuildConfig.TMDB_API_TOKEN, "en-US");
                reviewResponseCall.enqueue(new Callback<TMDBReviewResponse>() {
                    @Override
                    public void onResponse(Call<TMDBReviewResponse> call, Response<TMDBReviewResponse> response) {
                        TMDBReviewResponse tmdbReviewResponse = response.body();
                        if (tmdbReviewResponse != null && tmdbReviewResponse.getResults().size() != 0) {
                            mReviewAuthors.clear();
                            mReviewContents.clear();
                            for (Review review : tmdbReviewResponse.getResults()) {
                                mReviewAuthors.add(review.getAuthor());
                                mReviewContents.add(review.getContent());
                            }
                            mReviewAdapter.notifyDataSetChanged();
                            mReviewRecyclerView.setVisibility(View.VISIBLE);
                            mReviewsLabel0.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onFailure(Call<TMDBReviewResponse> call, Throwable t) {

                    }
                });
                break;
        }
    }

    private void fetchCredits() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false);
        mCastRecyclerView.setLayoutManager(layoutManager);

        final ArrayList<Cast> castList = new ArrayList<>();
        final CastRecyclerViewAdapter mCastAdapter = new CastRecyclerViewAdapter(this, castList, actorName -> {
            try {
                Uri uri = Uri.parse("https://www.google.com/search?q=" + actorName + " movies");
                Intent actorMoviesIntent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(actorMoviesIntent);
            } catch (Exception e) {
                // Who doesn't have Google? Or a browser?
                e.printStackTrace();
            }
        });
        mCastRecyclerView.setAdapter(new ScaleInAnimationAdapter(mCastAdapter));

        RetrofitAPI retrofitAPI = NetworkUtils.getCacheEnabledRetrofit(getApplicationContext()).create(RetrofitAPI.class);
        final Call<TMDBCreditsResponse> creditsCall = retrofitAPI.getCredits(mMovie.getId(), BuildConfig.TMDB_API_TOKEN);
        creditsCall.enqueue(new Callback<TMDBCreditsResponse>() {
            @Override
            public void onResponse(Call<TMDBCreditsResponse> call, Response<TMDBCreditsResponse> response) {
                TMDBCreditsResponse creditsResponse = response.body();

                // Get cast info
                castList.clear();
                if (creditsResponse != null && creditsResponse.getCast().size() != 0) {
                    castList.addAll(creditsResponse.getCast());
                    mCastAdapter.notifyDataSetChanged();
                } else {
                    (findViewById(R.id.cast_label_tv)).setVisibility(View.GONE);
                    mCastRecyclerView.setVisibility(View.GONE);
                }

                // Get director info
                if (creditsResponse != null) {
                    for (Crew crew : creditsResponse.getCrew()) {
                        if (crew.getJob().equals("Director")) {
                            mDirectorTextView.setText(crew.getName());
                            break;
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<TMDBCreditsResponse> call, Throwable t) {
                // Why bother doing anything here?
            }
        });
    }

    private void fetchMoreDetails() {
        RetrofitAPI retrofitAPI = NetworkUtils.getCacheEnabledRetrofit(getApplicationContext()).create(RetrofitAPI.class);
        Call<TMDBDetailsResponse> detailsResponseCall = retrofitAPI.getDetails(mMovie.getId(), BuildConfig.TMDB_API_TOKEN, "en-US");
        detailsResponseCall.enqueue(new Callback<TMDBDetailsResponse>() {
            @Override
            public void onResponse(Call<TMDBDetailsResponse> call, Response<TMDBDetailsResponse> response) {
                final TMDBDetailsResponse tmdbDetailsResponse = response.body();
                String tagline = null;
                if (tmdbDetailsResponse != null) {
                    tagline = tmdbDetailsResponse.getTagline();
                }
                if (tagline != null && !tagline.equals("")) {
                    mTaglineTextView.setText(tagline);
                } else {
                    mTaglineTextView.setVisibility(View.GONE);
                }
                mVotesTextView.setText(String.valueOf(tmdbDetailsResponse.getVoteCount()));
                mMinutesTextView.setText(String.valueOf(tmdbDetailsResponse.getRuntime()));
                mImdbButton.setOnClickListener(view -> {
                    String imdbId = tmdbDetailsResponse.getImdbId();
                    try {
                        Uri uri;
                        if (imdbId != null && !imdbId.equals(""))
                            uri = Uri.parse("http://www.imdb.com/title/" + imdbId + "/");
                        else {
                            Toast.makeText(mContext, "Movie isn't there on IMDB. Here is a Google search for it instead!", Toast.LENGTH_LONG).show();
                            uri = Uri.parse("https://www.google.com/search?q=" + tmdbDetailsResponse.getTitle());
                        }
                        Intent imdbIntent = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(imdbIntent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
                if (tmdbDetailsResponse.getGenres() != null && tmdbDetailsResponse.getGenres().size() != 0) {
                    mGenres.clear();
                    mGenres.addAll(tmdbDetailsResponse.getGenres());
                    mGenreAdapter.notifyDataSetChanged();
                } else {
                    (findViewById(R.id.genres_label_tv)).setVisibility(View.GONE);
                    mGenresRecyclerView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<TMDBDetailsResponse> call, Throwable t) {
                // Why bother doing anything here
            }
        });
    }

    @Override
    public void onItemClick(String stringUrlTrailerClicked) {
        Uri youtubeUri = Uri.parse("https://www.youtube.com/watch?v=" + stringUrlTrailerClicked);
        Intent openYoutube = new Intent(Intent.ACTION_VIEW, youtubeUri);
        startActivity(openYoutube);
    }

    private String prettifyDate(String jsonDate) {
        DateFormat sourceDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = sourceDateFormat.parse(jsonDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        DateFormat destDateFormat = new SimpleDateFormat("MMM dd\nyyyy");
        String dateStr = destDateFormat.format(date);
        return dateStr;
    }

    private void favButtonInit(final int id) {
        Movie checkedMovie = dataSource.findMovieWithId(id);
        if (checkedMovie == null)
            mFavoriteButton.setImageResource(R.drawable.ic_favorite_border);
        else
            mFavoriteButton.setImageResource(R.drawable.ic_favorite);
        mFavoriteButton.setOnClickListener(view -> {
            Movie transactedMovie = dataSource.findMovieWithId(id);
            if (transactedMovie == null) {
                tempMovie = mMovie;
                GlideApp.with(mContext)
                        .load(tempMovie.getPosterPath())
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

                            @Nullable
                            @Override
                            public Request getRequest() {
                                return null;
                            }

                            @Override
                            public void setRequest(@Nullable Request request) {

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
                tempMovie.setPosterBytes(imageBytes);
                dataSource.addMovieToFavs(tempMovie);
                mFavoriteButton.setImageResource(R.drawable.ic_favorite);
                CookieBar.build(DetailsActivity.this)
                        .setBackgroundColor(android.R.color.holo_blue_dark)
                        .setTitle("Movie added to favorites!")
                        .setMessage("You can now see the details, even when offline, in your Favorites.")
                        .show();
            } else {
                CookieBar.build(DetailsActivity.this)
                        .setBackgroundColor(android.R.color.holo_red_dark)
                        .setTitle("Movie removed from favorites!")
                        .setMessage("But did you really have to? :-(")
                        .show();
                dataSource.deleteMovieFromFavs(transactedMovie);
                mFavoriteButton.setImageResource(R.drawable.ic_favorite_border);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dataSource.close();
    }

}
