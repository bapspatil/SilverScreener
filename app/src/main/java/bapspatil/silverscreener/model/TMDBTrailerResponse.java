package bapspatil.silverscreener.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by bapspatil
 */

public class TMDBTrailerResponse implements Parcelable {
    public static final Creator<TMDBTrailerResponse> CREATOR = new Creator<TMDBTrailerResponse>() {
        @Override
        public TMDBTrailerResponse createFromParcel(Parcel source) {
            return new TMDBTrailerResponse(source);
        }

        @Override
        public TMDBTrailerResponse[] newArray(int size) {
            return new TMDBTrailerResponse[size];
        }
    };
    @SerializedName("results")
    private ArrayList<Trailer> results;

    public TMDBTrailerResponse() {
    }

    protected TMDBTrailerResponse(Parcel in) {
        this.results = in.createTypedArrayList(Trailer.CREATOR);
    }

    public ArrayList<Trailer> getResults() {
        return results;
    }

    public void setResults(ArrayList<Trailer> results) {
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
