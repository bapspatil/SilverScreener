package bapspatil.silverscreener.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by bapspatil
 */

public class TMDBReviewResponse implements Parcelable {
    public static final Creator<TMDBReviewResponse> CREATOR = new Creator<TMDBReviewResponse>() {
        @Override
        public TMDBReviewResponse createFromParcel(Parcel source) {
            return new TMDBReviewResponse(source);
        }

        @Override
        public TMDBReviewResponse[] newArray(int size) {
            return new TMDBReviewResponse[size];
        }
    };
    @SerializedName("results")
    private ArrayList<Review> results;

    public TMDBReviewResponse() {
    }

    protected TMDBReviewResponse(Parcel in) {
        this.results = in.createTypedArrayList(Review.CREATOR);
    }

    public ArrayList<Review> getResults() {
        return results;
    }

    public void setResults(ArrayList<Review> results) {
        this.results = results;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(this.results);
    }
}
