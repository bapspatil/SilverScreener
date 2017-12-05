package bapspatil.silverscreener.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.Request;
import com.bumptech.glide.request.target.SizeReadyCallback;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;

import org.aviran.cookiebar2.CookieBar;

import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import bapspatil.silverscreener.BuildConfig;
import bapspatil.silverscreener.R;
import bapspatil.silverscreener.adapters.ReviewRecyclerViewAdapter;
import bapspatil.silverscreener.adapters.TrailerRecyclerViewAdapter;
import bapspatil.silverscreener.data.RealmDataSource;
import bapspatil.silverscreener.model.Movie;
import bapspatil.silverscreener.model.MovieRecyclerView;
import bapspatil.silverscreener.model.Review;
import bapspatil.silverscreener.model.TMDBReviewResponse;
import bapspatil.silverscreener.model.TMDBTrailerResponse;
import bapspatil.silverscreener.model.Trailer;
import bapspatil.silverscreener.network.RetrofitAPI;
import bapspatil.silverscreener.utils.GlideApp;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailsActivity extends AppCompatActivity implements TrailerRecyclerViewAdapter.ItemClickListener {
    private static final int TRAILERS_DETAILS_TYPE = 0, REVIEWS_DETAILS_TYPE = 1;

    private TrailerRecyclerViewAdapter mTrailerAdapter;
    private ReviewRecyclerViewAdapter mReviewAdapter;
    private Context mContext;
    private ArrayList<String> mTrailerTitles = new ArrayList<>();
    private ArrayList<String> mTrailerPaths = new ArrayList<>();
    private ArrayList<String> mReviewAuthors = new ArrayList<>();
    private ArrayList<String> mReviewContents = new ArrayList<>();
    private byte[] imageBytes;
    Movie tempMovie, mMovie;

    @BindView(R.id.appbar) AppBarLayout appBarLayout;
    @BindView(R.id.collapsing_toolbar_layout) CollapsingToolbarLayout collapsingToolbarLayout;
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.trailer_label_tv) TextView mTrailersLabel0;
    @BindView(R.id.trailers_hint_tv) TextView mTrailersLabel1;
    @BindView(R.id.reviews_label_tv) TextView mReviewsLabel0;
    @BindView(R.id.rating_value_tv) TextView mRatingTextView;
    @BindView(R.id.date_value_tv) TextView mDateTextView;
    @BindView(R.id.title_tv) TextView mTitleTextView;
    @BindView(R.id.plot_tv) TextView mPlotTextView;
    @BindView(R.id.poster_image_view) ImageView mPosterImageView;
    @BindView(R.id.rv_trailers) MovieRecyclerView mTrailerRecyclerView;
    @BindView(R.id.rv_reviews) MovieRecyclerView mReviewRecyclerView;
    @BindView(R.id.fav_button) FloatingActionButton mFavoriteButton;
    @BindView(R.id.backdrop_iv) ImageView mBackdropImageView;

    private RealmDataSource dataSource;
    private Unbinder unbinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        unbinder = ButterKnife.bind(this);
        mContext = getApplicationContext();
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mMovie = getIntent().getParcelableExtra("movie");
        collapsingToolbarLayout.setTitle(mMovie.getTitle());
        collapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(android.R.color.transparent));
        collapsingToolbarLayout.setCollapsedTitleTextColor(getResources().getColor(android.R.color.white));
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (Math.abs(verticalOffset) - appBarLayout.getTotalScrollRange() == 0)
                    mPosterImageView.setVisibility(View.GONE);
                else
                    mPosterImageView.setVisibility(View.VISIBLE);
            }
        });

        dataSource = new RealmDataSource();
        dataSource.open();

        mRatingTextView.setText(mMovie.getRating());
        if (mMovie.getDate() != null && !mMovie.getDate().equals(""))
            mDateTextView.setText(prettifyDate(mMovie.getDate()));
        mTitleTextView.setText(mMovie.getTitle());
        mPlotTextView.setText(mMovie.getPlot());

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

        favButtonInit(mMovie.getId());

        mTrailerRecyclerView.setLayoutManager(new LinearLayoutManager(DetailsActivity.this, LinearLayoutManager.HORIZONTAL, false));
        mTrailerAdapter = new TrailerRecyclerViewAdapter(mContext, mTrailerTitles, mTrailerPaths, this);
        mTrailerRecyclerView.setAdapter(mTrailerAdapter);

        mReviewRecyclerView.setLayoutManager(new LinearLayoutManager(DetailsActivity.this, LinearLayoutManager.VERTICAL, false));
        mReviewAdapter = new ReviewRecyclerViewAdapter(mContext, mReviewAuthors, mReviewContents);
        mReviewRecyclerView.setAdapter(mReviewAdapter);

        fetchDetails(mMovie.getId(), TRAILERS_DETAILS_TYPE);
        fetchDetails(mMovie.getId(), REVIEWS_DETAILS_TYPE);
    }

    private void fetchDetails(int movieId, int detailsType) {
        RetrofitAPI retrofitAPI = RetrofitAPI.retrofit.create(RetrofitAPI.class);
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
                        if (tmdbTrailerResponse.getResults().size() != 0) {
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
                        if (tmdbReviewResponse.getResults().size() != 0) {
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
        mFavoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
                    tempMovie.setPosterBytes(imageBytes);
                    dataSource.addMovieToFavs(tempMovie);
                    mFavoriteButton.setImageResource(R.drawable.ic_favorite);
                    CookieBar.Build(DetailsActivity.this)
                            .setBackgroundColor(android.R.color.holo_blue_dark)
                            .setTitle("Movie added to favorites!")
                            .setMessage("You can now see the details, even when offline, in your Favorites.")
                            .show();
                } else {
                    CookieBar.Build(DetailsActivity.this)
                            .setBackgroundColor(android.R.color.holo_red_dark)
                            .setTitle("Movie removed from favorites!")
                            .setMessage("But did you really have to? :-(")
                            .show();
                    dataSource.deleteMovieFromFavs(transactedMovie);
                    mFavoriteButton.setImageResource(R.drawable.ic_favorite_border);
                }
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
        unbinder.unbind();
        dataSource.close();
    }

}
