package com.uzitech.uhome.Adapters;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.json.JSONObject;

import java.util.ArrayList;

public class DatabaseAdapter extends SQLiteOpenHelper {

    private final String TAG = "Database";

    private String table = "favourite_tiles";

    public DatabaseAdapter(Context context) {
        super(context, "uTV_Home.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("create table " + table + "(ID NAME UNIQUE, ICON TEXT, PKG TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("drop table if exists " + table);
        onCreate(sqLiteDatabase);
    }

    public void addFav(String name, String pkg, String icon) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("ID", name);
        contentValues.put("PKG", pkg);
        contentValues.put("ICON", icon);
        database.insert(table, null, contentValues);
        Log.d(TAG, "App added");
    }

    public void removeFav(String pkg) {
        SQLiteDatabase database = this.getWritableDatabase();
        database.delete(table, "PKG = \"" + pkg + "\"", null);
        Log.d(TAG, "App removed");
    }

    public ArrayList<JSONObject> getAllFav() {
        ArrayList<JSONObject> temp = new ArrayList<>();
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor res = database.rawQuery("select * from " + table, null);

        while (res.moveToNext()) {
            try {
                JSONObject object = new JSONObject();
                object.put("name", res.getString(0));
                object.put("icon", res.getString(1));
                object.put("pkg_name", res.getString(2));
                temp.add(object);
            } catch (Exception e) {
                Log.d(TAG, e.toString());
            }
        }
        res.close();
        return temp;
    }
}
