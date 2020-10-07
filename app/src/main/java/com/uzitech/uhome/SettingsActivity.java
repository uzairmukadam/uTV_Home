package com.uzitech.uhome;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.WindowManager;

public class SettingsActivity extends AppCompatActivity {

    BroadcastReceiver input_receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        input_receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String input = intent.getStringExtra("Remote_Input");
                assert input != null;
                if(input.equals("BTN_BACK")){
                    onBackPressed();
                }
            }
        };

        registerReceiver(input_receiver, new IntentFilter("utv.uzitech.remote_input"));
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(input_receiver);
        super.onDestroy();
    }
}