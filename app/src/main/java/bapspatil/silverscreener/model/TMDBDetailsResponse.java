package bapspatil.silverscreener.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TMDBDetailsResponse implements Parcelable {

    public static final Creator<TMDBDetailsResponse> CREATOR = new Creator<TMDBDetailsResponse>() {
        @Override
        public TMDBDetailsResponse createFromParcel(Parcel source) {
            return new TMDBDetailsResponse(source);
        }

        @Override
        public TMDBDetailsResponse[] newArray(int size) {
            return new TMDBDetailsResponse[size];
        }
    };
    @SerializedName("original_language")
    private String originalLanguage;
    @SerializedName("imdb_id")
    private String imdbId;
    @SerializedName("video")
    private boolean video;
    @SerializedName("title")
    private String title;
    @SerializedName("backdrop_path")
    private String backdropPath;
    @SerializedName("revenue")
    private int revenue;
    @SerializedName("genres")
    private List<GenresItem> genres;
    @SerializedName("popularity")
    private double popularity;
    @SerializedName("id")
    private int id;
    @SerializedName("vote_count")
    private int voteCount;
    @SerializedName("budget")
    private int budget;
    @SerializedName("overview")
    private String overview;
    @SerializedName("original_title")
    private String originalTitle;
    @SerializedName("runtime")
    private int runtime;
    @SerializedName("poster_path")
    private String posterPath;
    @SerializedName("release_date")
    private String releaseDate;
    @SerializedName("vote_average")
    private double voteAverage;
    @SerializedName("tagline")
    private String tagline;
    @SerializedName("adult")
    private boolean adult;
    @SerializedName("homepage")
    private String homepage;
    @SerializedName("status")
    private String status;

    public TMDBDetailsResponse() {
    }

    public TMDBDetailsResponse(String originalLanguage, String imdbId, boolean video, String title, String backdropPath, int revenue, List<GenresItem> genres, double popularity, int id, int voteCount, int budget, String overview, String originalTitle, int runtime, String posterPath, String releaseDate, double voteAverage, String tagline, boolean adult, String homepage, String status) {
        this.originalLanguage = originalLanguage;
        this.imdbId = imdbId;
        this.video = video;
        this.title = title;

        this.backdropPath = backdropPath;
        this.revenue = revenue;
        this.genres = genres;
        this.popularity = popularity;
        this.id = id;
        this.voteCount = voteCount;
        this.budget = budget;
        this.overview = overview;
        this.originalTitle = originalTitle;
        this.runtime = runtime;
        this.posterPath = posterPath;
        this.releaseDate = releaseDate;
        this.voteAverage = voteAverage;
        this.tagline = tagline;
        this.adult = adult;
        this.homepage = homepage;
        this.status = status;
    }

    protected TMDBDetailsResponse(Parcel in) {
        this.originalLanguage = in.readString();
        this.imdbId = in.readString();
        this.video = in.readByte() != 0;
        this.title = in.readString();
        this.backdropPath = in.readString();
        this.revenue = in.readInt();
        this.genres = in.createTypedArrayList(GenresItem.CREATOR);
        this.popularity = in.readDouble();
        this.id = in.readInt();
        this.voteCount = in.readInt();
        this.budget = in.readInt();
        this.overview = in.readString();
        this.originalTitle = in.readString();
        this.runtime = in.readInt();
        this.posterPath = in.readString();
        this.releaseDate = in.readString();
        this.voteAverage = in.readDouble();
        this.tagline = in.readString();
        this.adult = in.readByte() != 0;
        this.homepage = in.readString();
        this.status = in.readString();
    }

    public String getOriginalLanguage() {
        return originalLanguage;
    }

    public void setOriginalLanguage(String originalLanguage) {
        this.originalLanguage = originalLanguage;
    }

    public String getImdbId() {
        return imdbId;
    }

    public void setImdbId(String imdbId) {
        this.imdbId = imdbId;
    }

    public boolean isVideo() {
        return video;
    }

    public void setVideo(boolean video) {
        this.video = video;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBackdropPath() {
        return backdropPath;
    }

    public void setBackdropPath(String backdropPath) {
        this.backdropPath = backdropPath;
    }

    public int getRevenue() {
        return revenue;
    }

    public void setRevenue(int revenue) {
        this.revenue = revenue;
    }

    public List<GenresItem> getGenres() {
        return genres;
    }

    public void setGenres(List<GenresItem> genres) {
        this.genres = genres;
    }

    public double getPopularity() {
        return popularity;
    }

    public void setPopularity(double popularity) {
        this.popularity = popularity;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getVoteCount() {
        return voteCount;
    }

    public void setVoteCount(int voteCount) {
        this.voteCount = voteCount;
    }

    public int getBudget() {
        return budget;
    }

    public void setBudget(int budget) {
        this.budget = budget;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }

    public int getRuntime() {
        return runtime;
    }

    public void setRuntime(int runtime) {
        this.runtime = runtime;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public double getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(double voteAverage) {
        this.voteAverage = voteAverage;
    }

    public String getTagline() {
        return tagline;
    }

    public void setTagline(String tagline) {
        this.tagline = tagline;
    }

    public boolean isAdult() {
        return adult;
    }

    public void setAdult(boolean adult) {
        this.adult = adult;
    }

    public String getHomepage() {
        return homepage;
    }

    public void setHomepage(String homepage) {
        this.homepage = homepage;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.originalLanguage);
        dest.writeString(this.imdbId);
        dest.writeByte(this.video ? (byte) 1 : (byte) 0);
        dest.writeString(this.title);
        dest.writeString(this.backdropPath);
        dest.writeInt(this.revenue);
        dest.writeTypedList(this.genres);
        dest.writeDouble(this.popularity);
        dest.writeInt(this.id);
        dest.writeInt(this.voteCount);
        dest.writeInt(this.budget);
        dest.writeString(this.overview);
        dest.writeString(this.originalTitle);
        dest.writeInt(this.runtime);
        dest.writeString(this.posterPath);
        dest.writeString(this.releaseDate);
        dest.writeDouble(this.voteAverage);
        dest.writeString(this.tagline);
        dest.writeByte(this.adult ? (byte) 1 : (byte) 0);
        dest.writeString(this.homepage);
        dest.writeString(this.status);
    }
}