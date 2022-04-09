package edu.jsu.mcis.cs408.project2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class DatabaseHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "words.db";
    private static final String TABLE_WORDS = "words";
    public static final String COLUMN_KEY = "key";

    public DatabaseHandler(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_WORDS_TABLE = "CREATE TABLE words (key text)";
        db.execSQL(CREATE_WORDS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_WORDS);
        onCreate(db);
    }

    public void addKey(String key) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_KEY, key);
        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(TABLE_WORDS, null, values);
        db.close();

    }

    public ArrayList<String> getAllKeysAsList() {
        String query = "SELECT * FROM " + TABLE_WORDS;

        ArrayList<String> keys = new ArrayList<String>();

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            cursor.moveToFirst();
            do {
                keys.add(cursor.getString(0));
            }
            while (cursor.moveToNext());
        }
        cursor.close();

        db.close();
        return keys;

    }
}