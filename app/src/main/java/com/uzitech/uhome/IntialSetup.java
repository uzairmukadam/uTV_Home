package com.uzitech.uhome;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class IntialSetup extends AppCompatActivity {

    List<String> packageNames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intial_setup);

        getAllApps();

        startDashboard();
    }

    private void getAllApps() {
        PackageManager packageManager = getPackageManager();
        List<ResolveInfo> appList;
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        appList = this.getPackageManager().queryIntentActivities(mainIntent, 0);
        Collections.sort(appList, new ResolveInfo.DisplayNameComparator(packageManager));
        packageNames = new ArrayList<>();

        for (int i = 0; i < appList.size(); i++) {
            ResolveInfo app = appList.get(i);
            String packName = app.activityInfo.packageName;
            if (packName.substring(0, 4).contains("utv")) {
                packageNames.add(packName);
            }
        }
    }

    private void startDashboard() {
        startActivity(new Intent(this, Dashboard.class));
        finish();
    }
}