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

    private String category_table = "category";
    private String category_apps = "favourite_tiles";

    public DatabaseAdapter(Context context) {
        super(context, "uTV_Home.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("create table " + category_table + "(ID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, NAME TEXT UNIQUE)");
        sqLiteDatabase.execSQL("insert into " + category_table + " values (0, \"Favourite\")");
        sqLiteDatabase.execSQL("create table " + category_apps + "(CATEGORY_ID INTEGER, NAME TEXT UNIQUE, ICON TEXT, PKG TEXT UNIQUE)");
        Log.d(TAG, "Tables created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("drop table if exists " + category_table);
        sqLiteDatabase.execSQL("drop table if exists " + category_apps);
        onCreate(sqLiteDatabase);
    }

    public void addApp(int id, String name, String icon, String pkg) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("ID", id);
        contentValues.put("NAME", name);
        contentValues.put("ICON", icon);
        contentValues.put("PKG", pkg);
        database.insert(category_apps, null, contentValues);
        Log.d(TAG, "App added");
    }

    public void removeApp(int id, String pkg) {
        SQLiteDatabase database = this.getWritableDatabase();
        database.delete(category_apps, "CATEGORY_ID = \"" + id + "\" AND PKG = \"" + pkg + "\"", null);
        Log.d(TAG, "App removed");
    }

    public void removeCategory(int id) {
        SQLiteDatabase database = this.getWritableDatabase();
        database.delete(category_table, "ID = \"" + id + "\"", null);
        Log.d(TAG, "Category removed");
    }

    public ArrayList<JSONObject> getAllApps() {
        ArrayList<JSONObject> temp = new ArrayList<>();
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor res = database.rawQuery("select * from " + category_apps, null);

        while (res.moveToNext()) {
            try {
                JSONObject object = new JSONObject();
                object.put("id", res.getInt(0));
                object.put("name", res.getString(1));
                object.put("icon", res.getString(2));
                object.put("pkg_name", res.getString(3));
                temp.add(object);
            } catch (Exception e) {
                Log.d(TAG, e.toString());
            }
        }
        res.close();
        return temp;
    }

    public ArrayList<JSONObject> getAllCategory() {
        ArrayList<JSONObject> temp = new ArrayList<>();
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor res = database.rawQuery("select * from " + category_table, null);

        while (res.moveToNext()) {
            try {
                JSONObject object = new JSONObject();
                object.put("id", res.getInt(0));
                object.put("name", res.getString(1));
                temp.add(object);
            } catch (Exception e) {
                Log.d(TAG, e.toString());
            }
        }
        res.close();
        return temp;
    }
}
