package bapspatil.silverscreener.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Movie extends RealmObject implements Parcelable {

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel source) {
            return new Movie(source);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };
    @PrimaryKey
    @SerializedName("id")
    private int id;
    @SerializedName("poster_path")
    private String posterPath;
    @SerializedName("title")
    private String title;
    @SerializedName("overview")
    private String plot;
    @SerializedName("release_date")
    private String date;
    @SerializedName("vote_average")
    private String rating;
    @SerializedName("backdrop_path")
    private String backdropPath;
    private byte[] posterBytes;

    public Movie() {
    }

    public Movie(String posterPath, String title, String plot, String date, String rating, String backdropPath, int id, byte[] posterBytes) {

        this.posterPath = posterPath;
        this.title = title;
        this.plot = plot;
        this.date = date;
        this.rating = rating;
        this.backdropPath = backdropPath;
        this.id = id;
        this.posterBytes = posterBytes;
    }

    protected Movie(Parcel in) {
        this.posterPath = in.readString();
        this.title = in.readString();
        this.plot = in.readString();
        this.date = in.readString();
        this.rating = in.readString();
        this.backdropPath = in.readString();
        this.id = in.readInt();
        this.posterBytes = in.createByteArray();
    }

    public String getPosterPath() {

        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPlot() {
        return plot;
    }

    public void setPlot(String plot) {
        this.plot = plot;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getBackdropPath() {
        return backdropPath;
    }

    public void setBackdropPath(String backdropPath) {
        this.backdropPath = backdropPath;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public byte[] getPosterBytes() {
        return posterBytes;
    }

    public void setPosterBytes(byte[] posterBytes) {
        this.posterBytes = posterBytes;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.posterPath);
        dest.writeString(this.title);
        dest.writeString(this.plot);
        dest.writeString(this.date);
        dest.writeString(this.rating);
        dest.writeString(this.backdropPath);
        dest.writeInt(this.id);
        dest.writeByteArray(this.posterBytes);
    }
}
