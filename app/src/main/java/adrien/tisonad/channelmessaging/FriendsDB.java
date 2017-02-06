package adrien.tisonad.channelmessaging;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;

/**
 * Created by tisonad on 30/01/2017.
 */
public class FriendsDB  extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "MyDB.db";
    public static final String FRIEND_TABLE_NAME = "Friend";
    public static final String KEY_ID = "id";
    public static final String KEY_NAME = "name";
    public static final String KEY_IMAGE = "image";
    private static final String HOMME_TABLE_CREATE = "CREATE TABLE " + FRIEND_TABLE_NAME + "(" + KEY_ID + " INTEGER, " + KEY_NAME + " TEXT, " + KEY_IMAGE + " TEXT);";

    public FriendsDB(Context context) {
        super(context, Environment.getExternalStorageDirectory() + "/" + DATABASE_NAME, null,
                DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(HOMME_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + FRIEND_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + FRIEND_TABLE_NAME);
        onCreate(db);
    }
}
