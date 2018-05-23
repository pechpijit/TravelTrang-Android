package com.khiancode.traveltrang;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
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

public class ScrollingUserTripActivity extends BaseActivity {
    private FloatingActionButton fab,fabcreate;
    MenuItem menuItemCount;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    AdapterTripAdminDetail adapter;
    int ID;
    ImageView imageHeader;
    Toolbar toolbar;
    boolean follow = false;
    CollapsingToolbarLayout toolbarLayout;
    int createId = 0;
    int code = 8989;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling_trip_user);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbarLayout = findViewById(R.id.toolbar_layout);

        imageHeader = findViewById(R.id.htab_header);

        ID = getIntent().getExtras().getInt("id");

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fabcreate = (FloatingActionButton) findViewById(R.id.fabcreate);
        fabcreate.hide();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogTM();
            }
        });

        fabcreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ScrollingUserTripActivity.this, SelectTravelActivity.class);
                intent.putExtra("trip", ID);
                intent.putExtra("day", createId);
                startActivityForResult(intent,code);
            }
        });

        recyclerView = findViewById(R.id.dummyfrag_scrollableview);
        mSwipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);

        recyclerView.setLayoutManager(new LinearLayoutManager(getBaseContext()));
        recyclerView.setHasFixedSize(true);
        getData(ID);
        checkAdd(ID);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == code) {
            if (resultCode == Activity.RESULT_CANCELED) {
                getData(ID);
                checkAdd(ID);
            }
        }
    }//onActivityResult

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
        post.setURL(BaseActivity.BASE_URL + "user/user/trip-id/" + ID);
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

        if (posts.getListtravel().isEmpty()) {
            Glide.with(this)
                    .load(R.drawable.benner3)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .centerCrop()
                    .error(R.drawable.placeholder)
                    .into(imageHeader);
        } else {
            Glide.with(this)
                    .load(BaseActivity.BASE_URL_PICTURE + "/images/travel/" + posts.getListtravel().get(0).getImage())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .centerCrop()
                    .error(R.drawable.nopic)
                    .into(imageHeader);
        }

        imageHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ScrollingUserTripActivity.this,ShowPictureActivity.class).putExtra("image",posts.getTrip().getImage()));
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
                Intent intent = new Intent(ScrollingUserTripActivity.this, DetailCustomerTripDayActivity.class);
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

    private void checkAdd(int ID) {
        ApiClient.GET post = new ApiClient.GET(this);
        post.setURL(BaseActivity.BASE_URL + "user/user/trip-checkadd/" + ID);
        post.execute();
        post.setListenerCallService(new CallServiceListener() {
            @Override
            public void ResultData(String data) {
                hideProgressDialog();
                mSwipeRefreshLayout.setRefreshing(false);
                checkAdd(data);
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

    private void checkAdd(String data) {
        if (data.equals("success")) {
            fabcreate.hide();
        } else {
            fabcreate.show();
            createId = Integer.parseInt(data);
            Toast.makeText(this, data, Toast.LENGTH_SHORT).show();
        }
    }

    public void dialogTM() {
        new AlertDialog.Builder(this, R.style.AppTheme_Dark_Dialog)
                .setTitle("ยืนยันการลบ")
                .setPositiveButton("ตกลง", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        deleteTrip(ID);
                    }
                })
                .setNegativeButton("ยกเลิก", null)
                .setCancelable(false)
                .show();
    }

    private void deleteTrip(int ID) {
        ApiClient.GET post = new ApiClient.GET(this);
        post.setURL(BaseActivity.BASE_URL + "user/trip-delete/" + ID);
        post.execute();
        post.setListenerCallService(new CallServiceListener() {
            @Override
            public void ResultData(String data) {
                hideProgressDialog();
                mSwipeRefreshLayout.setRefreshing(false);
                if (data.equals("success")) {
                    finish();
                    overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_right);
                }
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

}
