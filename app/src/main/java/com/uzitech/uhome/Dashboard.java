package com.uzitech.uhome;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import com.uzitech.uhome.Adapters.tilesAdapter;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Dashboard extends AppCompatActivity {

    static List<JSONObject> tiles;
    static ViewPager2 main_tiles;
    tilesAdapter adapter;
    TextView tile_title;

    static int pos_tile = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        float density = getResources().getDisplayMetrics().density;

        main_tiles = findViewById(R.id.main_app_list);
        tile_title = findViewById(R.id.tile_title);

        setUI(density);

        tiles = new ArrayList<>();

        addTiles();

        adapter = new tilesAdapter(this, tiles, getPackageManager());

        main_tiles.setAdapter(adapter);

        main_tiles.setClipToPadding(false);
        main_tiles.setClipChildren(false);
        main_tiles.setOffscreenPageLimit(3);
        CompositePageTransformer compositePageTransformer = new CompositePageTransformer();
        compositePageTransformer.addTransformer(new MarginPageTransformer((int) (density * 16)));
        main_tiles.setPageTransformer(compositePageTransformer);

        main_tiles.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                try {
                    tile_title.setText(tiles.get(position).getString("name"));
                } catch (Exception ignored) {
                }
            }
        });
    }

    private void setUI(float density) {
        float width = getResources().getDisplayMetrics().widthPixels;
        int hor_padd = (int) ((width - (200 * density)) / 2);

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
        try {
            object.put("name", name);
            object.put("icon", icon);
            object.put("pkg_name", pkg_name);
        } catch (Exception ignored) {
        }
        return object;
    }

    public static void performClickAction(Context context) {
        try {
            Intent appIntent;
            JSONObject appObj = tiles.get(pos_tile);
            if (!appObj.getString("pkg_name").equals("NONE")) {
                appIntent = context.getPackageManager().getLaunchIntentForPackage(appObj.getString("pkg_name"));
                context.startActivity(appIntent);
            }
        } catch (Exception ignored) {
        }
    }

    public static class BroadcastReceive extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String input = intent.getStringExtra("Remote_Input");
            assert input != null;
            switch (input) {
                case "D_LEFT":
                    if (pos_tile != 0) {
                        pos_tile -= 1;
                    }
                    break;
                case "D_RIGHT":
                    if (pos_tile != tiles.size() - 1) {
                        pos_tile += 1;
                    }
                    break;
                case "D_ENTER":
                    performClickAction(context);
                    break;
            }
            main_tiles.setCurrentItem(pos_tile);
        }
    }

    @Override
    protected void onPause() {
        PackageManager pm = getPackageManager();
        ComponentName componentName = new ComponentName(this, BroadcastReceive.class);
        pm.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
        super.onPause();
    }

    @Override
    protected void onResume() {
        main_tiles.setCurrentItem(pos_tile);
        PackageManager pm = getPackageManager();
        ComponentName componentName = new ComponentName(this, BroadcastReceive.class);
        pm.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
        super.onResume();
    }
}