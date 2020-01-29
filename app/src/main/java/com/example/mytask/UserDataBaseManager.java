package com.example.mytask;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class UserDataBaseManager extends SQLiteOpenHelper {

    private static final String DataBaseName = "UserDB";
    private static final String table = "MySavedData";
    private static final String col1 = "userName";
    private static final String col2 = "userSecretID";

    public UserDataBaseManager(@Nullable Context context) { super(context, DataBaseName, null, 1); }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + table + " ( " + col1 + " TEXT , " + col2 + " TEXT ) ");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+table);
        onCreate(db);
    }
    public String insertToMyData(String userName , String userSecretID) {
        try {
            ContentValues cv = new ContentValues();
            cv.put(col1, userName);
            cv.put(col2, userSecretID);
            SQLiteDatabase db = this.getWritableDatabase();
            db.insert(table, null, cv);
            return "Insert To DataBase Success";
        }catch (Exception e) {
            return "Exception "+e.getMessage();
        }
    }
    public String deleteByName(String name) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.execSQL("DELETE FROM "+table+" WHERE "+col1+" =\""+name+"\"; ");
            return "Delete Success";
        }
        catch (Exception e) {
            return "Exception "+e.getMessage();
        }
    }
    public Cursor allData() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM "+table,null);
        return c ;
    }
}
