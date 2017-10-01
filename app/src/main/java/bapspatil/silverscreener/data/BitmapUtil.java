package bapspatil.silverscreener.data;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;

public class BitmapUtil {

    // Convert from Bitmap to byte array
    public static byte[] getBytes(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
        return stream.toByteArray();
    }

    // Convert from byte array to Bitmap
    public static Bitmap getImage(byte[] image) {
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }

    // Add image to database
    /*public void addImageToDatabase(SQLiteDatabase database, byte[] image) {
        ContentValues cv = new ContentValues();
        cv.put(FavsContract.FavsEntry.COLUMN_POSTER, image);
        database.insert(FavsContract.FavsEntry.TABLE_NAME, null, cv);
    }*/

}
