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

public class Dashboard2 extends AppCompatActivity {

    String TAG = "Dashboard_2";

    ArrayList<HorizontalScrollView> category;
    ArrayList<LinearLayout> app_list;
    ScrollView category_holder;
    LinearLayout category_layout;
    ArrayList<JSONObject> favTiles, nativeTiles;//check usage
    ArrayList<CardView> favCards, nativeCards;//check usage
    float density;
    BroadcastReceiver input_receiver;
    int margin, category_index = 0;
    int[] list_index;
    PackageManager packageManager;
    DatabaseAdapter database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard2);

        /*getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);*/

        packageManager = getPackageManager();
        database = new DatabaseAdapter(getApplicationContext());

        category_holder = findViewById(R.id.category_holder);
        category_layout = findViewById(R.id.category_holder_layout);

        density = getResources().getDisplayMetrics().density;
        margin = (int) density * 8;

        getTiles();

        setTileLists();

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

    private void setTileLists() {
        category = new ArrayList<>();
        app_list = new ArrayList<>();
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, margin, 0, margin);

        //fav list
        if (favTiles.size() > 0) {
            createCategory(favCards, params);
        }
        createCategory(nativeCards, params);

        for (HorizontalScrollView view : category) {
            category_layout.addView(view);
        }

        list_index = new int[category.size()];
    }

    private void createCategory(ArrayList<CardView> cards, LinearLayout.LayoutParams params) {
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
    }

    private void getTiles() {
        favTiles = database.getAllFav();
        for (JSONObject object : favTiles) {
            try {
                if (!packageInstalled(object.getString("pkg_name"))) {
                    favTiles.remove(object);
                    database.removeFav(object.getString("pkg_name"));
                }
            } catch (Exception e) {
                Log.d(TAG, e.toString());
            }
        }

        nativeTiles = new ArrayList<>();
        String[][] pkg_data = new String[][]{{"Videos", "movies", "utv.uzitech.umovies"},
                {"Music", "music", "utv.uzitech.umusic"}, {"Photos", "photos", "utv.uzitech.umovies"},
                {"Games", "games", "utv.uzitech.ugames"}, {"Store", "store", "utv.uzitech.ustore"}};
        for (String[] app : pkg_data) {
            if (packageInstalled(app[2])) {
                nativeTiles.add(JsonObj(app[0], app[1], app[2]));
            }
        }
        nativeTiles.add(JsonObj("All Apps", "all_apps", "NONE"));
        nativeTiles.add(JsonObj("Settings", "settings", "NONE"));

        favCards = addCards(favTiles);
        nativeCards = addCards(nativeTiles);
    }

    private ArrayList<CardView> addCards(ArrayList<JSONObject> tiles) {
        ArrayList<CardView> temp = new ArrayList<>();
        for (int i = 0; i < tiles.size(); i++) {
            try {
                JSONObject object = tiles.get(i);
                CardView cardView = createCard(object);
                temp.add(cardView);
            } catch (Exception e) {
                Log.d(TAG, e.toString());
            }
        }
        return temp;
    }

    private CardView createCard(final JSONObject object) throws Exception {
        LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        @SuppressLint("InflateParams") CardView layout = (CardView) inflater.inflate(R.layout.app_tile, null);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams((int) (density * 200), (int) (density * 100));

        ImageView icon = layout.findViewById(R.id.tile_icon);

        if (!object.getString("icon").isEmpty()) {
            icon.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),
                    getResources().getIdentifier(object.getString("icon"), "drawable", getPackageName())));
        }//else add app icon

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
            object.put("pkg_name", pkg_name);
        } catch (Exception e) {
            Log.d(TAG, e.toString());
        }
        return object;
    }

    private boolean packageInstalled(String pkg) {
        try {
            packageManager.getPackageInfo(pkg, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return true;
        }
    }

    /*private void setSelectedCard(int pos) {
        for (int i = 0; i < cards.size(); i++) {
            CardView cardView = cards.get(i);
            if (i == pos) {
                cardView.setScaleX(1);
                cardView.setScaleY(1);
                app_list_scroll.smoothScrollTo(cardView.getLeft() - (margin*10), 0);
            } else {
                cardView.setScaleX(0.85f);
                cardView.setScaleY(0.85f);
            }
        }
    }*/

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