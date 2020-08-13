package com.uzitech.uhome;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;

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

        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE);
    }

    private void addTiles() {
        tiles.add(JsonObj("Videos", "movies"));
        tiles.add(JsonObj("Music", "music"));
        tiles.add(JsonObj("Photos", "photos"));
        tiles.add(JsonObj("Games", "games"));
        tiles.add(JsonObj("Store", "store"));
        tiles.add(JsonObj("All Apps", "all_apps"));
        tiles.add(JsonObj("Settings", "settings"));
    }

    private JSONObject JsonObj(String name, String icon) {
        JSONObject object = new JSONObject();
        try{
            object.put("name", name);
            object.put("icon", icon);
        }catch (Exception ignored){}
        return object;
    }
}