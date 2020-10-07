package com.uzitech.uhome;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AllAppsActivity extends AppCompatActivity {

    String TAG = "AllApps";

    BroadcastReceiver input_receiver;

    List<ResolveInfo> allApps;

    ScrollView apps_scroll;
    LinearLayout apps_layout;
    TextView app_name;

    ArrayList<CardView> app_cards;

    int margin, app_index = 0;
    int[] app_count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_apps);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        apps_scroll = findViewById(R.id.apps_scroll);
        apps_layout = findViewById(R.id.apps_layout);
        app_name = findViewById(R.id.app_name);

        getAllApps();

        highlightApp();

        input_receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String input = intent.getStringExtra("Remote_Input");
                assert input != null;
                switch (input) {
                    case "BTN_BACK":
                        onBackPressed();
                        break;
                    case "D_DOWN":
                        app_index += 6;
                        if (app_index > allApps.size() - 1) {
                            if (app_index < app_count[0] * 6) {
                                app_index = allApps.size() - 1;
                            } else {
                                app_index = 0;
                            }
                        }
                        highlightApp();
                        break;
                    case "D_UP":
                        app_index -= 6;
                        if (app_index < 0) {
                            app_index = allApps.size() - 1;
                        }
                        highlightApp();
                        break;
                    case "D_RIGHT":
                        if (app_index != allApps.size() - 1) {
                            app_index += 1;
                        } else {
                            app_index = 0;
                        }
                        highlightApp();
                        break;
                    case "D_LEFT":
                        if (app_index != 0) {
                            app_index -= 1;
                        } else {
                            app_index = allApps.size() - 1;
                        }
                        highlightApp();
                        break;
                    case "D_ENTER":
                        app_cards.get(app_index).performClick();
                        break;
                }
            }
        };

        registerReceiver(input_receiver, new IntentFilter("utv.uzitech.remote_input"));
    }

    private void highlightApp() {
        for (CardView cards : app_cards) {
            cards.setScaleY(0.8f);
            cards.setScaleX(0.8f);
            cards.setCardBackgroundColor(getResources().getColor(android.R.color.white, null));
        }

        CardView cards = app_cards.get(app_index);
        cards.setScaleY(1);
        cards.setScaleX(1);
        LinearLayout layout = (LinearLayout) cards.getParent();
        cards.setCardBackgroundColor(getResources().getColor(R.color.colorAccent, null));
        app_name.setText(allApps.get(app_index).loadLabel(getPackageManager()));
        apps_scroll.smoothScrollTo(0, layout.getTop());
    }

    private void getAllApps() {
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        allApps = this.getPackageManager().queryIntentActivities(mainIntent, 0);
        Collections.sort(allApps, new ResolveInfo.DisplayNameComparator(getPackageManager()));

        DisplayMetrics metrics = getResources().getDisplayMetrics();
        float density = metrics.density;
        margin = (int) (density * 8);

        app_count = new int[2];
        app_cards = new ArrayList<>();
        Log.d(TAG, "List_Start");
        int i = 0;
        boolean end = false;
        while (!end) {
            LinearLayout temp = new LinearLayout(this);
            for (int j = 0; j < 6; j++) {
                if (i < allApps.size()) {
                    final CardView view = createCard(allApps.get(i), density);
                    temp.addView(view);
                    app_cards.add(view);
                    i++;
                } else {
                    end = true;
                    app_count[0] = apps_layout.getChildCount();
                    app_count[1] = temp.getChildCount();
                    break;
                }
            }
            apps_layout.addView(temp);
        }
        Log.d(TAG, "List_Done");
    }

    private CardView createCard(final ResolveInfo app_info, float density) {
        LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        @SuppressLint("InflateParams") CardView layout = (CardView) inflater.inflate(R.layout.app_tile, null);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams((int) (density * 100), (int) (density * 100));

        ImageView icon = layout.findViewById(R.id.tile_icon);
        icon.setImageDrawable(app_info.loadIcon(getPackageManager()));

        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent appIntent = getPackageManager().getLaunchIntentForPackage(app_info.activityInfo.packageName);
                startActivity(appIntent);
            }
        });

        params.setMargins(margin, margin, margin, margin);
        layout.setLayoutParams(params);

        return layout;
    }

    @Override
    protected void onPause() {
        unregisterReceiver(input_receiver);
        super.onPause();
    }
}