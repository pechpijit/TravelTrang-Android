package com.khiancode.traveltrang.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.khiancode.traveltrang.BaseActivity;
import com.khiancode.traveltrang.DetailTravelActivity;
import com.khiancode.traveltrang.MainActivity;
import com.khiancode.traveltrang.PlaceActivity;
import com.khiancode.traveltrang.R;
import com.khiancode.traveltrang.adapter.AdapterCategory;
import com.khiancode.traveltrang.model.CategoryModel;
import com.khiancode.traveltrang.model.TravelModel;
import com.khiancode.traveltrang.okhttp.ApiClient;
import com.khiancode.traveltrang.okhttp.CallServiceListener;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;

import at.blogc.android.views.ExpandableTextView;

import static com.khiancode.traveltrang.BaseActivity.LOAD;


public class DetailTravelFragment extends Fragment implements View.OnClickListener {

    private int ID;
    private ExpandableTextView ex_detail, ex_detailohter, ex_address,ex_time,ex_price,ex_phone,ex_web;
    private ImageView img_ex1, img_ex2, img_ex3,img_ex4,img_ex5,img_ex6,img_ex7;
    private LinearLayout view_detail, view_detailohter, view_address,view_time,view_price,view_phone,view_web;
    String TAG = "DetailTravelFragment";

    public DetailTravelFragment() {
        // Required empty public constructor
    }

    @SuppressLint("ValidFragment")
    public DetailTravelFragment(int id) {
        this.ID = id;
    }

    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        return AnimationUtils.loadAnimation(getActivity(),
                enter ? android.R.anim.fade_in : android.R.anim.fade_out);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail_travel, container, false);

        ex_detail = view.findViewById(R.id.ex_detail);
        ex_detailohter = view.findViewById(R.id.ex_detailohter);
        ex_address = view.findViewById(R.id.ex_address);
        ex_time = view.findViewById(R.id.ex_time);
        ex_price = view.findViewById(R.id.ex_price);
        ex_phone = view.findViewById(R.id.ex_phone);
        ex_web = view.findViewById(R.id.ex_web);

        view_detail = view.findViewById(R.id.view_detail);
        view_detailohter = view.findViewById(R.id.view_detailohter);
        view_address = view.findViewById(R.id.view_address);
        view_time = view.findViewById(R.id.view_time);
        view_price = view.findViewById(R.id.view_price);
        view_phone = view.findViewById(R.id.view_phone);
        view_web = view.findViewById(R.id.view_web);

        img_ex1 = view.findViewById(R.id.img_ex1);
        img_ex2 = view.findViewById(R.id.img_ex2);
        img_ex3 = view.findViewById(R.id.img_ex3);
        img_ex4 = view.findViewById(R.id.img_ex4);
        img_ex5 = view.findViewById(R.id.img_ex5);
        img_ex6 = view.findViewById(R.id.img_ex6);
        img_ex7 = view.findViewById(R.id.img_ex7);

        view_detail.setOnClickListener(this);
        view_detailohter.setOnClickListener(this);
        view_address.setOnClickListener(this);
        view_time.setOnClickListener(this);
        view_price.setOnClickListener(this);
        view_phone.setOnClickListener(this);
        view_web.setOnClickListener(this);

        setExp(ex_detail);
        setExp(ex_detailohter);
        setExp(ex_address);
        setExp(ex_time);
        setExp(ex_price);
        setExp(ex_phone);
        setExp(ex_web);

        getData();
        return view;
    }

    private void setExp(ExpandableTextView exp) {
        exp.setAnimationDuration(1000L);
        exp.setPadding(10,10,10,10);
        exp.setInterpolator(new OvershootInterpolator());
        exp.setExpandInterpolator(new OvershootInterpolator());
        exp.setCollapseInterpolator(new OvershootInterpolator());
        exp.addOnExpandListener(new ExpandableTextView.OnExpandListener()
        {
            @Override
            public void onExpand(@NonNull final ExpandableTextView view)
            {
                Log.d(TAG, "ExpandableTextView expanded");
            }

            @Override
            public void onCollapse(@NonNull final ExpandableTextView view)
            {
                Log.d(TAG, "ExpandableTextView collapsed");
            }
        });
    }


    private void getData() {
        ((DetailTravelActivity) getActivity()).showProgressDialog(LOAD);
        ApiClient.GET post = new ApiClient.GET(getActivity());
        post.setURL(BaseActivity.BASE_URL + "user/travel/" + ID);
        post.execute();
        post.setListenerCallService(new CallServiceListener() {
            @Override
            public void ResultData(String data) {
                ((DetailTravelActivity) getActivity()).hideProgressDialog();
                setView(data);
            }

            @Override
            public void ResultError(String data) {
                ((DetailTravelActivity) getActivity()).hideProgressDialog();
                ((DetailTravelActivity) getActivity()).dialogResultError(data);
            }

            @Override
            public void ResultNull(String data) {
                ((DetailTravelActivity) getActivity()).hideProgressDialog();
                ((DetailTravelActivity) getActivity()).dialogResultNull();
            }
        });
    }

    private void setView(String data) {
        Gson gson = new Gson();
        final TravelModel model = gson.fromJson(data, TravelModel.class);
        ((DetailTravelActivity)getActivity()).toolbar.setTitle(model.getName());
        ex_detail.setText(Html.fromHtml(model.getDetail().trim()));
        ex_detailohter.setText(model.getDetailOther());
        ex_address.setText(model.getAddress());
        ex_time.setText(model.getTimeOpen()+" - "+model.getTimeClose());
        ex_price.setText(model.getPriceMin()+" - "+model.getPriceMax());
        ex_phone.setText(model.getPhone());
        ex_web.setText(model.getUrlShare());

        ex_phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse("tel:"+Uri.encode(model.getPhone().trim())));
                callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(callIntent);
            }
        });

        ex_web.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent callIntent = new Intent(Intent.ACTION_VIEW);
                callIntent.setData(Uri.parse(model.getUrlShare()));
                callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(callIntent);
            }
        });

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.view_detail:
                setEx(ex_detail, img_ex1);
                break;
            case R.id.view_detailohter:
                setEx(ex_detailohter, img_ex2);
                break;
            case R.id.view_address:
                setEx(ex_address, img_ex3);
                break;
            case R.id.view_time:
                setEx(ex_time, img_ex4);
                break;
            case R.id.view_price:
                setEx(ex_price, img_ex5);
                break;
            case R.id.view_phone:
                setEx(ex_phone, img_ex6);
                break;
            case R.id.view_web:
                setEx(ex_web, img_ex7);
                break;
        }
    }

    private void setEx(ExpandableTextView ex, ImageView img) {
        ex.toggle();
        if (ex.isExpanded()) {
            img.animate().rotation(0).start();
            ex.collapse();
        } else {
            img.animate().rotation(180).start();
            ex.expand();
        }
    }
}
