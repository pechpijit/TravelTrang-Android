package com.khiancode.traveltrang.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.khiancode.traveltrang.BaseActivity;
import com.khiancode.traveltrang.MainActivity;
import com.khiancode.traveltrang.MainActivity;
import com.khiancode.traveltrang.R;
import com.khiancode.traveltrang.ScrollingTripActivity;
import com.khiancode.traveltrang.ScrollingUserTripActivity;
import com.khiancode.traveltrang.adapter.AdapterTravel;
import com.khiancode.traveltrang.adapter.AdapterTrip;
import com.khiancode.traveltrang.model.ListTripModel;
import com.khiancode.traveltrang.model.TravelModel;
import com.khiancode.traveltrang.okhttp.ApiClient;
import com.khiancode.traveltrang.okhttp.CallServiceListener;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;

import okhttp3.FormBody;
import okhttp3.RequestBody;


public class TipCreateFragment extends Fragment {

    private RecyclerView recyclerView;
    private AdapterTrip adapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private TextView noData;
    private Button btnFavorit,btnPlan;

    public TipCreateFragment() {
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
        View view = inflater.inflate(R.layout.fragment_listtrip, container, false);

        recyclerView = view.findViewById(R.id.dummyfrag_scrollableview);
        noData = view.findViewById(R.id.noData);
        mSwipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getBaseContext()));
        recyclerView.setHasFixedSize(true);

        ((MainActivity)getActivity()).fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog();
            }
        });

        getTrip();
        return view;
    }

    private void getTrip() {
        ((MainActivity) getActivity()).showProgressDialog(BaseActivity.LOAD);
        SharedPreferences sp = getActivity().getSharedPreferences("Preferences_TravelTrang", Context.MODE_PRIVATE);
        ApiClient.GET post = new ApiClient.GET(getActivity());
        post.setURL(BaseActivity.BASE_URL + "user/tip-customer/" + sp.getInt("id", 0));
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

    private void setView(String json) {
        Gson gson = new Gson();
        Type collectionType = new TypeToken<Collection<ListTripModel>>() {}.getType();
        Collection<ListTripModel> enums = gson.fromJson(json, collectionType);
        final ArrayList<ListTripModel> posts = new ArrayList<ListTripModel>(enums);

        adapter = new AdapterTrip(getActivity(), posts);
        recyclerView.setAdapter(adapter);

        adapter.SetOnItemClickListener(new AdapterTrip.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                int ID =posts.get(position).getTrip().getId();
                Intent intent = new Intent(getActivity(), ScrollingUserTripActivity.class);
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

    private void dialog() {
        AlertDialog.Builder builder =
                new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getLayoutInflater();

        View view = inflater.inflate(R.layout.custom_dialog_create_plan,null);
        builder.setView(view);

        final EditText name = view.findViewById(R.id.input_name);
        final EditText day = view.findViewById(R.id.input_day);
        final EditText night = view.findViewById(R.id.input_night);

        builder.setTitle("เพิ่มแผนท่องเที่ยว");
        builder.setPositiveButton("ตกลง", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ((MainActivity)getActivity()).showProgressDialog(BaseActivity.LOAD);
                postComment(name.getText().toString().trim(),day.getText().toString(),night.getText().toString());
            }
        });
        builder.setNegativeButton("ยกเลิก", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.show();
    }

    private void postComment(String name, String day, String night) {
        SharedPreferences sp = getActivity().getSharedPreferences("Preferences_TravelTrang", Context.MODE_PRIVATE);

        RequestBody requestBody = new FormBody.Builder()
                .add("name", name)
                .add("day", day)
                .add("night", night)
                .add("customerId", String.valueOf(sp.getInt("id",0)))
                .build();

        ApiClient.POST post = new ApiClient.POST(getActivity());
        post.setURL(BaseActivity.BASE_URL+"user/tip-create");
        post.setRequestBody(requestBody);
        post.execute();
        post.setListenerCallService(new CallServiceListener() {
            @Override
            public void ResultData(String data) {
                getTrip();
            }

            @Override
            public void ResultError(String data) {
                ((MainActivity)getActivity()).hideProgressDialog();
                ((MainActivity)getActivity()).dialogResultError2();
            }

            @Override
            public void ResultNull(String data) {
                ((MainActivity)getActivity()).hideProgressDialog();
                ((MainActivity)getActivity()).dialogResultNull();
            }
        });
    }

}
