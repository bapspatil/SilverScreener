package bapspatil.silverscreener;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class Movie implements Parcelable {

    private String posterPath, title, plot, date, rating, backdropPath;
    private ArrayList<String> trailerTitles, trailerPaths, reviewAuthors, reviewContents;
    private int id;

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        @Override
        public Movie createFromParcel(Parcel source) {
            return new Movie(source);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    Movie(Parcel in) {
        this.posterPath = in.readString();
        this.title = in.readString();
        this.plot = in.readString();
        this.date = in.readString();
        this.rating = in.readString();
        this.id = in.readInt();
        this.backdropPath = in.readString();
        this.trailerTitles = in.createStringArrayList();
        this.trailerPaths = in.createStringArrayList();
        this.reviewAuthors = in.createStringArrayList();
        this.reviewContents = in.createStringArrayList();
    }

    Movie() {

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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public void addTrailerInfo(String title, String path) {
        trailerTitles.add(title);
        trailerPaths.add("https://www.youtube.com/watch?v=" + path);
    }

    public void addReviewInfo(String author, String content) {
        reviewAuthors.add(author);
        reviewContents.add(content);
    }

    public ArrayList<String> getTrailerTitles() {
        return trailerTitles;
    }

    public ArrayList<String> getTrailerPaths() {
        return trailerPaths;
    }

    public ArrayList<String> getReviewAuthors() {
        return reviewAuthors;
    }

    public ArrayList<String> getReviewContents() {
        return reviewContents;
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
        dest.writeInt(this.id);
        dest.writeString(this.backdropPath);
        dest.writeStringList(this.trailerTitles);
        dest.writeStringList(this.trailerPaths);
        dest.writeStringList(this.reviewAuthors);
        dest.writeStringList(this.reviewContents);
    }
}
