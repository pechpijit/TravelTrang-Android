package com.khiancode.traveltrang.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.model.Step;
import com.akexorcist.googledirection.util.DirectionConverter;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.khiancode.traveltrang.BaseActivity;
import com.khiancode.traveltrang.DetailTravelActivity;
import com.khiancode.traveltrang.InfoWindowRefresher;
import com.khiancode.traveltrang.MainActivity;
import com.khiancode.traveltrang.PlaceActivity;
import com.khiancode.traveltrang.R;
import com.khiancode.traveltrang.model.MapModel;
import com.khiancode.traveltrang.okhttp.ApiClient;
import com.khiancode.traveltrang.okhttp.CallServiceListener;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public class MapFragment extends Fragment implements OnMapReadyCallback,GoogleMap.OnMarkerClickListener{
    GoogleMap mMap;
    Marker mPerth;
    MapView gMapView;

    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        return AnimationUtils.loadAnimation(getActivity(),
                enter ? android.R.anim.fade_in : android.R.anim.fade_out);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //FragmentManager fragment = getActivity().getSupportFragmentManager();
        // Fragment fragment=(Fragment) getChildFragmentManager().findFragmentById(R.id.mapView);
        final SupportMapFragment myMAPF = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        myMAPF.getMapAsync(this);

    }

    private void getData() {
        ApiClient.GET post = new ApiClient.GET(getActivity());
        post.setURL(BaseActivity.BASE_URL + "user/map-travel");
        post.execute();
        post.setListenerCallService(new CallServiceListener() {
            @Override
            public void ResultData(String data) {
                setView(data);
            }

            @Override
            public void ResultError(String data) {
                ((MainActivity) getActivity()).hideProgressDialog();
                ((MainActivity) getActivity()).dialogResultError2();
            }

            @Override
            public void ResultNull(String data) {
                ((MainActivity) getActivity()).hideProgressDialog();
                ((MainActivity) getActivity()).dialogResultNull();
            }
        });
    }

    private void setView(String json) {
        Gson gson = new Gson();
        Type collectionType = new TypeToken<Collection<MapModel>>() {
        }.getType();
        Collection<MapModel> enums = gson.fromJson(json, collectionType);
        final ArrayList<MapModel> post = new ArrayList<MapModel>(enums);

        mMap.setBuildingsEnabled(true);
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(7.5680741, 99.6037002)).zoom(9).build();
        mMap.animateCamera(CameraUpdateFactory
                .newCameraPosition(cameraPosition));
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.setOnMarkerClickListener(this);
        LatLng posotion = null;
        for (final MapModel model : post) {
            if (model.getLatitude() != null && model.getLongitude() != null) {
                MarkerOptions markerOpt = new MarkerOptions();
                posotion = new LatLng(Double.parseDouble(model.getLatitude()), Double.parseDouble(model.getLongitude()));
                markerOpt.position(posotion).title(model.getName());

                final LatLng finalPosotion = posotion;
                Glide.with(getActivity())
                        .load(BaseActivity.BASE_URL_PICTURE + "/images_resize/travel/" + model.getImage())
                        .asBitmap()
                        .into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(Bitmap bitmap, GlideAnimation<? super Bitmap> glideAnimation) {
                                mPerth = mMap.addMarker(new MarkerOptions()
                                        .position(finalPosotion)
                                        .icon((BitmapDescriptorFactory.fromBitmap(getMarkerBitmapFromView(bitmap))))
                                        .title(model.getName()));
                                mPerth.setTag(model);
                            }
                        });
            }
        }

    }

    private Bitmap getMarkerBitmapFromView(Bitmap bitmap) {
        View customMarkerView = ((LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.view_marker, null);
        ImageView markerImageView = (ImageView) customMarkerView.findViewById(R.id.avatar_marker_image_view);
        markerImageView.setImageBitmap(bitmap);
        customMarkerView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        customMarkerView.layout(0, 0, customMarkerView.getMeasuredWidth(), customMarkerView.getMeasuredHeight());
        customMarkerView.buildDrawingCache();
        Bitmap returnedBitmap = Bitmap.createBitmap(customMarkerView.getMeasuredWidth(), customMarkerView.getMeasuredHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        canvas.drawColor(Color.WHITE, PorterDuff.Mode.SRC_IN);
        Drawable drawable = customMarkerView.getBackground();
        if (drawable != null)
            drawable.draw(canvas);
        customMarkerView.draw(canvas);
        return returnedBitmap;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        MapsInitializer.initialize(getActivity());
        getData();
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        MapModel model = (MapModel) marker.getTag();
        dialogTM(model);
//        Toast.makeText(getActivity(), model.getName(), Toast.LENGTH_SHORT).show();
        return false;
    }

    private void dialogTM(final MapModel model) {
        new AlertDialog.Builder(getActivity(), R.style.AppTheme_Dark_Dialog)
                .setTitle(model.getName())
                .setPositiveButton("เพิ่มเติม", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        Intent intent = new Intent(getActivity(), DetailTravelActivity.class);
                        intent.putExtra("id", model.getId());
                        intent.putExtra("image", model.getImage());
                        startActivity(intent);
                        getActivity().overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_left);
                    }
                })
                .setNegativeButton("ยกเลิก",null)
                .setCancelable(false)
                .show();
    }
}
