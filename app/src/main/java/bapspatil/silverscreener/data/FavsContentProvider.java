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
        Cursor cursor;
        final SQLiteDatabase db = mFavsDbHelper.getReadableDatabase();
        int match = sUriMatcher.match(uri);
        switch (match) {
            case FAVS:
                cursor = db.query(FavsContract.FavsEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown Uri: " + uri.toString());
        }
        if (getContext() != null)
            cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final SQLiteDatabase db = mFavsDbHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        Uri returnUri;
        switch (match) {
            case FAVS:
                long id = db.insert(FavsContract.FavsEntry.TABLE_NAME, null, values);
                if (id > 0) {
                    returnUri = ContentUris.withAppendedId(FavsContract.FavsEntry.CONTENT_URI, id);
                } else {
                    throw new SQLException("Failed to insert row into " + id);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown Uri: " + uri.toString());
        }
        if (getContext() != null)
            getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        final SQLiteDatabase db = mFavsDbHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        int moviesDeleted;
        switch (match) {
            case FAVS_WITH_ID:
                String id = uri.getPathSegments().get(1);
                moviesDeleted = db.delete(FavsContract.FavsEntry.TABLE_NAME, "_id=?", new String[]{id});
                break;
            default:
                throw new UnsupportedOperationException("Unknown Uri: " + uri.toString());
        }
        if (moviesDeleted != 0)
            if (getContext() != null)
                getContext().getContentResolver().notifyChange(uri, null);
        return moviesDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }
}
