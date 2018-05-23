package com.khiancode.traveltrang.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.khiancode.traveltrang.BaseActivity;
import com.khiancode.traveltrang.MainActivity;
import com.khiancode.traveltrang.PlaceActivity;
import com.khiancode.traveltrang.R;
import com.khiancode.traveltrang.ScrollingDiaryActivity;
import com.khiancode.traveltrang.UpdateImageActivity;
import com.khiancode.traveltrang.adapter.AdapterCategory;
import com.khiancode.traveltrang.adapter.AdapterDiaryList;
import com.khiancode.traveltrang.model.CategoryModel;
import com.khiancode.traveltrang.model.DIaryModel;
import com.khiancode.traveltrang.okhttp.ApiClient;
import com.khiancode.traveltrang.okhttp.CallServiceListener;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;


public class DiaryFragment extends Fragment {

    private RecyclerView recyclerView;
    private AdapterDiaryList adapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    public DiaryFragment() {
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
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        ((MainActivity) getActivity()).showProgressDialog(BaseActivity.LOAD);
        recyclerView = view.findViewById(R.id.dummyfrag_scrollableview);
        mSwipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);

        recyclerView.setLayoutManager( new LinearLayoutManager(getActivity().getBaseContext()));
        recyclerView.setHasFixedSize(true);
        ((MainActivity)getActivity()).fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), UpdateImageActivity.class));
                getActivity().overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_left);
            }
        });
        getData();

        return view;
    }

    private void getData() {
        SharedPreferences sp = getActivity().getSharedPreferences("Preferences_TravelTrang", Context.MODE_PRIVATE);
        ApiClient.GET post = new ApiClient.GET(getActivity());
        post.setURL(BaseActivity.BASE_URL+"user/diary-user/"+sp.getInt("id",0));
        post.execute();
        post.setListenerCallService(new CallServiceListener() {
            @Override
            public void ResultData(String data) {
                mSwipeRefreshLayout.setRefreshing(false);
                setView(data);
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
                ((MainActivity) getActivity()).dialogResultNull();
            }
        });
    }

    private void setView(String json) {
        Gson gson = new Gson();
        Type collectionType = new TypeToken<Collection<DIaryModel>>() {}.getType();
        Collection<DIaryModel> enums = gson.fromJson(json, collectionType);
        final ArrayList<DIaryModel> posts = new ArrayList<DIaryModel>(enums);

        adapter = new AdapterDiaryList(getActivity(), posts);
        recyclerView.setAdapter(adapter);

        adapter.SetOnItemClickListener(new AdapterDiaryList.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                int ID =posts.get(position).getId();
                Intent intent = new Intent(getActivity(), ScrollingDiaryActivity.class);
                intent.putExtra("id", ID);
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
        ((MainActivity) getActivity()).hideProgressDialog();
    }

}
