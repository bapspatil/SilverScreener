package bapspatil.silverscreener.data;

import android.net.Uri;
import android.provider.BaseColumns;

public class FavsContract {

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://bapspatil.silverscreener");

    public static final class FavsEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath("favorites").build();

        public static final String TABLE_NAME = "favorites";
        public static final String COLUMN_ID = "id";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_PLOT = "plot";
        public static final String COLUMN_RATING = "rating";
        public static final String COLUMN_DATE = "date";

    }

}
