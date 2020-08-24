package com.uzitech.uhome.Adapters;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.uzitech.uhome.R;

import org.json.JSONObject;

import java.util.List;

public class tilesAdapter extends RecyclerView.Adapter<tilesAdapter.adapterViewHolder> {

    Activity activity;
    List<JSONObject> tile_object;
    PackageManager packageManager;

    public tilesAdapter(Activity activity, List<JSONObject> tile_object, PackageManager packageManager){
        this.activity = activity;
        this.tile_object = tile_object;
        this.packageManager = packageManager;
    }

    @NonNull
    @Override
    public adapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.app_tile, parent, false);
        return new adapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull adapterViewHolder holder, int position) {
        final JSONObject appObj = tile_object.get(position);

        try {
            holder.tile_icon.setImageDrawable(ContextCompat.getDrawable(activity, activity.getResources().getIdentifier(appObj.getString("icon"), "drawable", activity.getPackageName())));
            holder.tile_name.setText(appObj.getString("name"));
        }catch (Exception ignored){}

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        Intent appIntent;
                        if(!appObj.getString("pkg_name").equals("NONE")){
                            appIntent = packageManager.getLaunchIntentForPackage(appObj.getString("pkg_name"));
                            activity.startActivity(appIntent);
                        }
                        Toast.makeText(activity, appObj.getString("name"), Toast.LENGTH_SHORT).show();
                    }catch (Exception ignored){}
                }
            });
    }

    @Override
    public int getItemCount() {
        return tile_object.size();
    }

    static class adapterViewHolder extends RecyclerView.ViewHolder{

        ImageView tile_icon;
        TextView tile_name;

        public adapterViewHolder(@NonNull View itemView) {
            super(itemView);
            tile_icon = itemView.findViewById(R.id.tile_icon);
        }
    }

}
