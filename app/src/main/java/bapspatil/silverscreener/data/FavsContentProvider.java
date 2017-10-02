package bapspatil.silverscreener.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class FavsContentProvider extends ContentProvider {

    public static final int FAVS = 200, FAVS_WITH_ID = 201;
    private FavsDbHelper mFavsDbHelper;

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    public static UriMatcher buildUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(FavsContract.AUTHORITY, FavsContract.PATH_FAVS, FAVS);
        uriMatcher.addURI(FavsContract.AUTHORITY, FavsContract.PATH_FAVS + "/#", FAVS_WITH_ID);
        return uriMatcher;
    }

    @Override
    public boolean onCreate() {
        mFavsDbHelper = new FavsDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        return null;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final SQLiteDatabase mDb = mFavsDbHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        Uri returnUri;
        switch (match) {
            case FAVS:
                long id = mDb.insert(FavsContract.FavsEntry.TABLE_NAME, null, values);
                if(id > 0) {
                    returnUri = ContentUris.withAppendedId(FavsContract.FavsEntry.CONTENT_URI, id);
                } else {
                    throw new SQLException("Failed to insert row into " + id);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown Uri: " + uri.toString());
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }
}
