package com.khiancode.traveltrang.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.khiancode.traveltrang.BaseActivity;
import com.khiancode.traveltrang.DetailTravelActivity;
import com.khiancode.traveltrang.MainActivity;
import com.khiancode.traveltrang.PlaceActivity;
import com.khiancode.traveltrang.R;
import com.khiancode.traveltrang.ScrollingTripActivity;
import com.khiancode.traveltrang.adapter.AdapterCategory;
import com.khiancode.traveltrang.adapter.AdapterTravel;
import com.khiancode.traveltrang.adapter.AdapterTripAdmin;
import com.khiancode.traveltrang.model.CategoryModel;
import com.khiancode.traveltrang.model.CategoryTravelModel;
import com.khiancode.traveltrang.model.TravelModel;
import com.khiancode.traveltrang.model.TripAdminModel;
import com.khiancode.traveltrang.okhttp.ApiClient;
import com.khiancode.traveltrang.okhttp.CallServiceListener;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;


public class FavoritFragment extends Fragment {

    private RecyclerView recyclerView;
    private AdapterTravel adapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private TextView noData;
    private Button btnFavorit,btnPlan;

    public FavoritFragment() {
        // Required empty public constructor
    }

    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        return AnimationUtils.loadAnimation(getActivity(),
                enter ? android.R.anim.fade_in : android.R.anim.fade_out);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorit, container, false);

        recyclerView = view.findViewById(R.id.dummyfrag_scrollableview);
        noData = view.findViewById(R.id.noData);
        mSwipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getBaseContext()));
        recyclerView.setHasFixedSize(true);

        btnPlan = view.findViewById(R.id.btn_plan);
        btnFavorit = view.findViewById(R.id.btn_favorit);
        btnFavorit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFavorit();
                disBtn(btnPlan);
                enBtn(btnFavorit);
            }
        });
        btnPlan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getTrip();
                disBtn(btnFavorit);
                enBtn(btnPlan);
            }
        });
        getTrip();
        enBtn(btnPlan);
        disBtn(btnFavorit);
        return view;
    }

    private void disBtn(Button view) {
        view.setBackgroundColor(Color.parseColor("#03A9F4"));
        view.setTextColor(Color.parseColor("#ffffff"));
    }

    private void enBtn(Button view) {
        view.setBackgroundColor(getResources().getColor(R.color.white));
        view.setTextColor(Color.parseColor("#03A9F4"));
    }

    private void getFavorit() {
        ((MainActivity) getActivity()).showProgressDialog(BaseActivity.LOAD);
        SharedPreferences sp = getActivity().getSharedPreferences("Preferences_TravelTrang", Context.MODE_PRIVATE);
        ApiClient.GET post = new ApiClient.GET(getActivity());
        post.setURL(BaseActivity.BASE_URL + "user/follow-customer/" + sp.getInt("id", 0));
        post.execute();
        post.setListenerCallService(new CallServiceListener() {
            @Override
            public void ResultData(String data) {
                mSwipeRefreshLayout.setRefreshing(false);
                noData.setVisibility(View.INVISIBLE);
                recyclerView.setVisibility(View.VISIBLE);
                setViewFavorit(data);
            }

            @Override
            public void ResultError(String data) {
                mSwipeRefreshLayout.setRefreshing(false);
                ((MainActivity) getActivity()).hideProgressDialog();
                ((MainActivity) getActivity()).dialogResultError2();
            }

            @Override
            public void ResultNull(String data) {
                mSwipeRefreshLayout.setRefreshing(false);
                ((MainActivity) getActivity()).hideProgressDialog();
                noData.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void setViewFavorit(String json) {
        Gson gson = new Gson();
        Type collectionType = new TypeToken<Collection<CategoryTravelModel>>() {}.getType();
        Collection<CategoryTravelModel> enums = gson.fromJson(json, collectionType);
        final ArrayList<CategoryTravelModel> posts = new ArrayList<CategoryTravelModel>(enums);

        String[] name = new String[posts.size()];

        for (int i = 0; i < posts.size(); i++) {
            name[i] = posts.get(i).getName();
        }

        adapter = new AdapterTravel(getActivity(), posts);
        recyclerView.setAdapter(adapter);

        adapter.SetOnItemClickListener(new AdapterTravel.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                int ID =posts.get(position).getId();
                Intent intent = new Intent(getActivity(), DetailTravelActivity.class);
                intent.putExtra("id", ID);
                intent.putExtra("image", posts.get(position).getImage());
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_left);
            }
        });

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getFavorit();
            }
        });
        ((MainActivity) getActivity()).hideProgressDialog();
    }

    private void getTrip() {
        ((MainActivity) getActivity()).showProgressDialog(BaseActivity.LOAD);
        SharedPreferences sp = getActivity().getSharedPreferences("Preferences_TravelTrang", Context.MODE_PRIVATE);
        ApiClient.GET post = new ApiClient.GET(getActivity());
        post.setURL(BaseActivity.BASE_URL + "user/trip/follow-customer/" + sp.getInt("id", 0));
        post.execute();
        post.setListenerCallService(new CallServiceListener() {
            @Override
            public void ResultData(String data) {
                mSwipeRefreshLayout.setRefreshing(false);
                noData.setVisibility(View.INVISIBLE);
                recyclerView.setVisibility(View.VISIBLE);
                setViewTrip(data);
            }

            @Override
            public void ResultError(String data) {
                mSwipeRefreshLayout.setRefreshing(false);
                ((MainActivity) getActivity()).hideProgressDialog();
                ((MainActivity) getActivity()).dialogResultError2();
            }

            @Override
            public void ResultNull(String data) {
                mSwipeRefreshLayout.setRefreshing(false);
                ((MainActivity) getActivity()).hideProgressDialog();
                noData.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void setViewTrip(String json) {
        Gson gson = new Gson();
        Type collectionType = new TypeToken<Collection<TripAdminModel>>() {}.getType();
        Collection<TripAdminModel> enums = gson.fromJson(json, collectionType);
        final ArrayList<TripAdminModel> posts = new ArrayList<TripAdminModel>(enums);

        AdapterTripAdmin adapter = new AdapterTripAdmin(getActivity(), posts);
        recyclerView.setAdapter(adapter);

        adapter.SetOnItemClickListener(new AdapterTripAdmin.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                int ID =posts.get(position).getId();
                Intent intent = new Intent(getActivity(), ScrollingTripActivity.class);
                intent.putExtra("id", ID);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_left);
            }
        });

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getTrip();
            }
        });
        ((MainActivity) getActivity()).hideProgressDialog();
    }

}
