package com.khiancode.traveltrang;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.khiancode.traveltrang.model.TravelModel;
import com.squareup.picasso.Callback;

public class InfoWindowRefresher implements GoogleMap.InfoWindowAdapter {

    private Context context;
    private ImageView image,btnInfo;
    private TextView title, distance;

    public InfoWindowRefresher(Context ctx) {
        context = ctx;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        View view = ((Activity) context).getLayoutInflater()
                .inflate(R.layout.map_custom_infowindow, null);

        image = view.findViewById(R.id.image);
        btnInfo = view.findViewById(R.id.btnInfo);
        title = view.findViewById(R.id.title);
        distance = view.findViewById(R.id.distance);

        final TravelModel model = (TravelModel) marker.getTag();

        title.setText(model.getName());
        Glide.with(context)
                .load(BaseActivity.BASE_URL_PICTURE + "/images/travel/" + model.getImage())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .error(R.drawable.nopic)
                .into(image);

        btnInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int ID =model.getId();
                Intent intent = new Intent(context, DetailTravelActivity.class);
                intent.putExtra("id", ID);
                intent.putExtra("image", model.getImage());
                context.startActivity(intent);
                ((Activity) context).overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_left);
            }
        });

        return view;
    }
}
