package bapspatil.silverscreener.data;

import android.provider.BaseColumns;

public class FavsContract {

    public static final class FavsEntry implements BaseColumns {

        public static final String TABLE_NAME = "favorites";
        public static final String COLUMN_ID = "id";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_PLOT = "plot";
        public static final String COLUMN_RATING = "rating";
        public static final String COLUMN_DATE = "date";

    }

}
