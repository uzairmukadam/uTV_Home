package com.uzitech.uhome;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.uzitech.uhome.Adapters.DatabaseAdapter;

import org.json.JSONObject;

import java.util.ArrayList;

public class Dashboard extends AppCompatActivity {

    private String TAG = "Dashboard";

    ArrayList<ArrayList<CardView>> allApps;
    ArrayList<HorizontalScrollView> category;
    ArrayList<LinearLayout> app_list;
    ScrollView category_holder;
    float density;
    BroadcastReceiver input_receiver;
    int margin, category_index = 0;
    int[] list_index;
    DatabaseAdapter database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard2);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        database = new DatabaseAdapter(getApplicationContext());

        category_holder = findViewById(R.id.category_holder);

        density = getResources().getDisplayMetrics().density;
        margin = (int) density * 8;

        setApps();

        setCategory();
        selectCard();

        input_receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String input = intent.getStringExtra("Remote_Input");
                assert input != null;
                switch (input) {
                    case "D_LEFT":
                        if (list_index[category_index] != 0) {
                            list_index[category_index] -= 1;
                            selectCard();
                        }
                        break;
                    case "D_RIGHT":
                        if (list_index[category_index] != app_list.get(category_index).getChildCount() - 1) {
                            list_index[category_index] += 1;
                            selectCard();
                        }
                        break;
                    case "D_UP":
                        if (category_index != 0) {
                            category_index -= 1;
                            setCategory();
                        }
                        break;
                    case "D_DOWN":
                        if (category_index != category.size() - 1) {
                            category_index += 1;
                            setCategory();
                        }
                        break;
                    case "D_ENTER":
                        app_list.get(category_index).getChildAt(list_index[category_index]).performClick();
                        break;
                }
            }
        };

        registerReceiver(input_receiver, new IntentFilter("utv.uzitech.remote_input"));
    }

    private void selectCard() {
        for (int i = 0; i < app_list.get(category_index).getChildCount(); i++) {
            CardView temp = (CardView) app_list.get(category_index).getChildAt(i);
            temp.setScaleX(0.9f);
            temp.setScaleY(0.9f);
        }

        CardView temp = (CardView) app_list.get(category_index).getChildAt(list_index[category_index]);
        temp.setScaleX(1);
        temp.setScaleY(1);
        category.get(category_index).smoothScrollTo(temp.getLeft() - margin, 0);
    }

    private void setCategory() {
        category_holder.smoothScrollTo(0, category.get(category_index).getTop() - margin);
    }

    private void setApps() {

        ArrayList<CardView> nativeApps = new ArrayList<>();
        String[][] pkg_data = new String[][]{{"Videos", "movies", "utv.uzitech.umovies"},
                {"Music", "music", "utv.uzitech.umusic"}, {"Photos", "photos", "utv.uzitech.umovies"},
                {"Games", "games", "utv.uzitech.ugames"}, {"Store", "store", "utv.uzitech.ustore"}};
        for (String[] app : pkg_data) {
            if (packageInstalled(app[2])) {
                nativeApps.add(createCard(JsonObj(app[0], app[1], app[2])));
            }
        }
        nativeApps.add(createCard(JsonObj("All Apps", "all_apps", "NONE_allApps")));
        nativeApps.add(createCard(JsonObj("Settings", "settings", "NONE_settings")));

        ArrayList<JSONObject> category_list = database.getAllCategory();
        ArrayList<JSONObject> category_apps = database.getAllApps();

        ArrayList<ArrayList<CardView>> separated_apps = new ArrayList<>();

        for (JSONObject category : category_list) {
            ArrayList<CardView> temp = new ArrayList<>();
            try {
                int id = category.getInt("id");
                for (JSONObject app : category_apps) {
                    if (app.getInt("id") == id) {
                        temp.add(createCard(app));
                        category_apps.remove(app);
                    }
                }
                separated_apps.add(temp);
            } catch (Exception e) {
                Log.d(TAG, e.toString());
            }
        }

        category = new ArrayList<>();
        app_list = new ArrayList<>();
        allApps = new ArrayList<>();
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, margin, 0, margin);

        if (separated_apps.size() > 0) {
            if (separated_apps.get(0).size() > 0) {
                //fav row
                createCategory(separated_apps.get(0), params);
                separated_apps.remove(0);
            }
        }
        //native row
        createCategory(nativeApps, params);

        for (ArrayList<CardView> category : separated_apps) {
            if (category.size() > 0) {
                createCategory(category, params);
            }
        }

        LinearLayout category_layout = findViewById(R.id.category_holder_layout);
        for (HorizontalScrollView view : category) {
            category_layout.addView(view);
        }

        list_index = new int[category.size()];
    }

    private void createCategory(ArrayList<CardView> cards, LinearLayout.LayoutParams params) {
        try {
            HorizontalScrollView scrollView = new HorizontalScrollView(getApplicationContext());
            LinearLayout layout = new LinearLayout(getApplicationContext());
            layout.setOrientation(LinearLayout.HORIZONTAL);
            for (CardView cardView : cards) {
                layout.addView(cardView);
            }
            scrollView.addView(layout);
            scrollView.setHorizontalScrollBarEnabled(false);
            scrollView.setOverScrollMode(View.OVER_SCROLL_NEVER);
            scrollView.setLayoutParams(params);
            category.add(scrollView);
            app_list.add(layout);
            allApps.add(cards);
        } catch (Exception e) {
            Log.d(TAG, e.toString());
        }
    }

    private CardView createCard(final JSONObject object) {
        LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        @SuppressLint("InflateParams") CardView layout = (CardView) inflater.inflate(R.layout.app_tile, null);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams((int) (density * 200), (int) (density * 100));

        try {
            ImageView icon = layout.findViewById(R.id.tile_icon);

            if (!object.getString("icon").isEmpty()) {
                icon.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),
                        getResources().getIdentifier(object.getString("icon"), "drawable", getPackageName())));
            }//else add app icon
        } catch (Exception e) {
            Log.d(TAG, e.toString());
        }

        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent appIntent;
                    if (!object.getString("pkg_name").equals("NONE")) {
                        appIntent = getPackageManager().getLaunchIntentForPackage(object.getString("pkg_name"));
                        startActivity(appIntent);
                    }
                } catch (Exception e) {
                    Log.d(TAG, e.toString());
                }
            }
        });

        params.setMargins(margin, 0, margin, 0);
        layout.setLayoutParams(params);

        return layout;
    }

    private JSONObject JsonObj(String name, String icon, String pkg_name) {
        JSONObject object = new JSONObject();
        try {
            object.put("name", name);
            object.put("icon", icon);
            if (pkg_name.startsWith("NONE_")) {
                object.put("class_name", pkg_name.substring(4));
            } else {
                object.put("pkg_name", pkg_name);
            }
        } catch (Exception e) {
            Log.d(TAG, e.toString());
        }
        return object;
    }

    private boolean packageInstalled(String pkg) {
        try {
            getPackageManager().getPackageInfo(pkg, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return true;
        }
    }

    @Override
    protected void onPause() {
        unregisterReceiver(input_receiver);
        super.onPause();
    }

    @Override
    protected void onResume() {
        registerReceiver(input_receiver, new IntentFilter("utv.uzitech.remote_input"));
        super.onResume();
    }
}