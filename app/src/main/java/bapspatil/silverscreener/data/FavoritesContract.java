package bapspatil.silverscreener.data;

import android.provider.BaseColumns;

public class FavoritesContract {

    public static final class FavoritesEntry implements BaseColumns {

        public static final String TABLE_NAME = "favorites";
        public static final String COLUMN_POSTER = "poster";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_PLOT = "plot";
        public static final String COLUMN_RATING = "rating";
        public static final String COLUMN_DATE = "date";

    }

}
