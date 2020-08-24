package com.uzitech.uhome;

import android.os.Bundle;
import android.os.Parcel;
import android.util.Base64;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import com.uzitech.uhome.Adapters.tilesAdapter;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Dashboard extends AppCompatActivity {

    List<JSONObject> tiles;
    ViewPager2 main_tiles;
    tilesAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        main_tiles = findViewById(R.id.main_app_list);

        setUI();

        tiles = new ArrayList<>();

        addTiles();

        adapter = new tilesAdapter(this, tiles, getPackageManager());

        main_tiles.setAdapter(adapter);

        main_tiles.setClipToPadding(false);
        main_tiles.setClipChildren(false);
        main_tiles.setOffscreenPageLimit(3);
        CompositePageTransformer compositePageTransformer = new CompositePageTransformer();
        compositePageTransformer.addTransformer(new MarginPageTransformer(16));
        main_tiles.setPageTransformer(compositePageTransformer);

    }

    private void setUI() {
        float density = getResources().getDisplayMetrics().density;
        float width = getResources().getDisplayMetrics().widthPixels;
        int hor_padd = (int) ((width - (300*density)) / 2);

        main_tiles.setPadding(hor_padd, 0, hor_padd, 0);
    }

    private void addTiles() {
        tiles.add(JsonObj("Videos", "movies", "NONE"));
        tiles.add(JsonObj("Music", "music", "utv.uzitech.umusic"));
        tiles.add(JsonObj("Photos", "photos", "NONE"));
        tiles.add(JsonObj("Games", "games", "NONE"));
        tiles.add(JsonObj("Store", "store", "NONE"));
        tiles.add(JsonObj("All Apps", "all_apps", "NONE"));
        tiles.add(JsonObj("Settings", "settings", "NONE"));
    }

    private JSONObject JsonObj(String name, String icon, String pkg_name) {
        JSONObject object = new JSONObject();
        try{
            object.put("name", name);
            object.put("icon", icon);
            object.put("pkg_name", pkg_name);
        }catch (Exception ignored){}
        return object;
    }
}