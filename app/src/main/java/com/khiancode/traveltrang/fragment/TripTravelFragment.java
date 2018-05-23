package com.khiancode.traveltrang.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
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
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.khiancode.traveltrang.BaseActivity;
import com.khiancode.traveltrang.DetailCustomerTripDayActivity;
import com.khiancode.traveltrang.DetailTravelActivity;
import com.khiancode.traveltrang.DetailTripDayActivity;
import com.khiancode.traveltrang.R;
import com.khiancode.traveltrang.adapter.AdapterTravel;
import com.khiancode.traveltrang.model.CategoryTravelModel;
import com.khiancode.traveltrang.okhttp.ApiClient;
import com.khiancode.traveltrang.okhttp.CallServiceListener;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;


public class TripTravelFragment extends Fragment {

    private RecyclerView recyclerView;
    private AdapterTravel adapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private int ID;
    private int DAY;

    TextView noData;

    public TripTravelFragment() {
        // Required empty public constructor
    }

    @SuppressLint("ValidFragment")
    public TripTravelFragment(int id, int day) {
        this.ID = id;
        this.DAY = day;
    }

    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        return AnimationUtils.loadAnimation(getActivity(),
                enter ? android.R.anim.fade_in : android.R.anim.fade_out);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        ((DetailCustomerTripDayActivity) getActivity()).showProgressDialog(BaseActivity.LOAD);
        recyclerView = view.findViewById(R.id.dummyfrag_scrollableview);
        noData = view.findViewById(R.id.noData);
        mSwipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);

        recyclerView.setLayoutManager( new LinearLayoutManager(getActivity().getBaseContext()));
        recyclerView.setHasFixedSize(true);

        getData();

        return view;
    }

    private void getData() {
        ApiClient.GET post = new ApiClient.GET(getActivity());
        post.setURL(BaseActivity.BASE_URL + "user/user/trip/" + ID+"/day/"+DAY);
        post.execute();
        post.setListenerCallService(new CallServiceListener() {
            @Override
            public void ResultData(String data) {
                mSwipeRefreshLayout.setRefreshing(false);
                noData.setVisibility(View.INVISIBLE);
                recyclerView.setVisibility(View.VISIBLE);
                setView(data);
            }

            @Override
            public void ResultError(String data) {
                mSwipeRefreshLayout.setRefreshing(false);
                ((DetailCustomerTripDayActivity) getActivity()).hideProgressDialog();
                ((DetailCustomerTripDayActivity) getActivity()).dialogResultError2();
            }

            @Override
            public void ResultNull(String data) {
                mSwipeRefreshLayout.setRefreshing(false);
                ((DetailCustomerTripDayActivity) getActivity()).hideProgressDialog();
                noData.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void setView(String json) {
        Gson gson = new Gson();
        Type collectionType = new TypeToken<Collection<CategoryTravelModel>>() {}.getType();
        Collection<CategoryTravelModel> enums = gson.fromJson(json, collectionType);
        final ArrayList<CategoryTravelModel> posts = new ArrayList<CategoryTravelModel>(enums);

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
                getData();
            }
        });
        ((DetailCustomerTripDayActivity) getActivity()).hideProgressDialog();
    }

}
