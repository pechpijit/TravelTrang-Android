package com.khiancode.traveltrang;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.khiancode.traveltrang.adapter.AdapterTripAdminDetail;
import com.khiancode.traveltrang.model.TripAdminDetailModel;
import com.khiancode.traveltrang.okhttp.ApiClient;
import com.khiancode.traveltrang.okhttp.CallServiceListener;

import okhttp3.FormBody;
import okhttp3.RequestBody;

public class ScrollingTripActivity extends BaseActivity {
    private FloatingActionButton fab;
    MenuItem menuItemCount;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    AdapterTripAdminDetail adapter;
    int ID;
    ImageView imageHeader;
    Toolbar toolbar;
    boolean follow = false;
    CollapsingToolbarLayout toolbarLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling_trip);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbarLayout = findViewById(R.id.toolbar_layout);

        imageHeader = findViewById(R.id.htab_header);

        ID = getIntent().getExtras().getInt("id");

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                followPost();
            }
        });

        recyclerView = findViewById(R.id.dummyfrag_scrollableview);
        mSwipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);

        recyclerView.setLayoutManager(new LinearLayoutManager(getBaseContext()));
        recyclerView.setHasFixedSize(true);
        getData(ID);
        checkFollow();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_right);
        }

        return super.onOptionsItemSelected(item);
    }

    private void getData(int ID) {
        ApiClient.GET post = new ApiClient.GET(this);
        post.setURL(BaseActivity.BASE_URL + "user/trip-id/" + ID);
        post.execute();
        post.setListenerCallService(new CallServiceListener() {
            @Override
            public void ResultData(String data) {
                hideProgressDialog();
                mSwipeRefreshLayout.setRefreshing(false);
                setView(data);
            }

            @Override
            public void ResultError(String data) {
                mSwipeRefreshLayout.setRefreshing(false);
                hideProgressDialog();
                dialogResultError(data);
            }

            @Override
            public void ResultNull(String data) {
                mSwipeRefreshLayout.setRefreshing(false);
                hideProgressDialog();
                dialogResultNull();
            }
        });
    }

    private void setView(String json) {
        Gson gson = new Gson();
        final TripAdminDetailModel posts = gson.fromJson(json, TripAdminDetailModel.class);
        toolbarLayout.setTitle(posts.getTrip().getName());

        Glide.with(this)
                .load(BaseActivity.BASE_URL_PICTURE + "/images/trip/" + posts.getTrip().getImage())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .error(R.drawable.placeholder)
                .into(imageHeader);

        imageHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ScrollingTripActivity.this,ShowPictureActivity.class).putExtra("image",posts.getTrip().getImage()));
            }
        });

        adapter = new AdapterTripAdminDetail(this, posts.getListtravel());
        recyclerView.setAdapter(adapter);

        adapter.SetOnItemClickListener(new AdapterTripAdminDetail.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                int id =posts.getTrip().getId();
                int day =posts.getTrip().getDay();
                int page =posts.getListtravel().get(position).getDay();
                Intent intent = new Intent(ScrollingTripActivity.this, DetailTripDayActivity.class);
                intent.putExtra("id", id);
                intent.putExtra("day", day);
                intent.putExtra("page", page);
                intent.putExtra("image", posts.getListtravel().get(position).getImage());
//                Toast.makeText(ScrollingTripActivity.this, id+","+day+","+page, Toast.LENGTH_SHORT).show();
                startActivity(intent);
                overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_left);
            }
        });

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getData(ID);
            }
        });
    }

    private void followPost() {
        SharedPreferences  sp = getSharedPreferences("Preferences_TravelTrang", Context.MODE_PRIVATE);
        RequestBody requestBody = new FormBody.Builder()
                .add("customerId", String.valueOf(sp.getInt("id",0)))
                .add("tripId",String.valueOf(ID) )
                .build();

        ApiClient.POST post = new ApiClient.POST(this);
        if (follow) {
            post.setURL(BASE_URL + "user/trip/follow-unlike");
        } else {
            post.setURL(BASE_URL + "user/trip/follow-like");
        }
        post.setRequestBody(requestBody);
        post.execute();
        post.setListenerCallService(new CallServiceListener() {
            @Override
            public void ResultData(String data) {
                if (data.equals("unlikesuccess")) {
                    fab.setImageResource(R.drawable.ic_favorite_border_white_48dp);
                    follow = false;
                    Toast.makeText(ScrollingTripActivity.this, "UnLike", Toast.LENGTH_SHORT).show();
                }

                if (data.equals("likesuccess")) {
                    fab.setImageResource(R.drawable.ic_favorite_white_48dp);
                    follow = true;
                    Toast.makeText(ScrollingTripActivity.this, "Like", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void ResultError(String data) {
                Toast.makeText(ScrollingTripActivity.this, "ไม่สามารถทำรายการได้ กรุณาลองใหม่อีกครั้ง", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void ResultNull(String data) {
                Toast.makeText(ScrollingTripActivity.this, "ไม่สามารถทำรายการได้ กรุณาลองใหม่อีกครั้ง", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkFollow() {
        SharedPreferences  sp = getSharedPreferences("Preferences_TravelTrang", Context.MODE_PRIVATE);
        RequestBody requestBody = new FormBody.Builder()
                .add("customerId", String.valueOf(sp.getInt("id",0)))
                .add("tripId",String.valueOf(ID) )
                .build();

        ApiClient.POST post = new ApiClient.POST(this);
        post.setURL(BASE_URL+"user/trip/follow-check");
        post.setRequestBody(requestBody);
        post.execute();
        post.setListenerCallService(new CallServiceListener() {
            @Override
            public void ResultData(String data) {
                if (data.equals("0")) {
                    fab.setImageResource(R.drawable.ic_favorite_border_white_48dp);
                    follow = false;
                } else {
                    fab.setImageResource(R.drawable.ic_favorite_white_48dp);
                    follow = true;
                }
            }

            @Override
            public void ResultError(String data) {
            }

            @Override
            public void ResultNull(String data) {
            }
        });
    }

}
