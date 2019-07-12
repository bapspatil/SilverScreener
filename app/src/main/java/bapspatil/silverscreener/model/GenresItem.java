package bapspatil.silverscreener.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class GenresItem implements Parcelable {

    public static final Creator<GenresItem> CREATOR = new Creator<GenresItem>() {
        @Override
        public GenresItem createFromParcel(Parcel source) {
            return new GenresItem(source);
        }

        @Override
        public GenresItem[] newArray(int size) {
            return new GenresItem[size];
        }
    };
    @SerializedName("name")
    private String name;
    @SerializedName("id")
    private int id;

    public GenresItem() {
    }

    public GenresItem(String name, int id) {

        this.name = name;
        this.id = id;
    }

    protected GenresItem(Parcel in) {
        this.name = in.readString();
        this.id = in.readInt();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeInt(this.id);
    }
}