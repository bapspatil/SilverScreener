package bapspatil.silverscreener.network;

import bapspatil.silverscreener.model.TMDBResponse;
import bapspatil.silverscreener.model.TMDBReviewResponse;
import bapspatil.silverscreener.model.TMDBTrailerResponse;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by bapspatil
 */

public interface RetrofitAPI {

    public static final String POSTER_BASE_URL = "http://image.tmdb.org/t/p/w342";
    public static final String BASE_URL = "https://api.themoviedb.org/3/";

    @GET("movie/{type}")
    Call<TMDBResponse> getMovies(@Path("type") String TYPE, @Query("api_key") String API_KEY, @Query("language") String LANGUAGE, @Query("page") int PAGE);

    @GET("search/movie")
    Call<TMDBResponse> searchMovies(@Query("api_key") String API_KEY, @Query("language") String LANGUAGE, @Query("page") int PAGE, @Query("query") String QUERY);

    @GET("movie/{movie_id}/videos")
    Call<TMDBTrailerResponse> getTrailers(@Path("movie_id") int MOVIE_ID, @Query("api_key") String API_KEY, @Query("language") String LANGUAGE);

    @GET("movie/{movie_id}/reviews")
    Call<TMDBReviewResponse> getReviews(@Path("movie_id") int MOVIE_ID, @Query("api_key") String API_KEY, @Query("language") String LANGUAGE);


    public static Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();

}