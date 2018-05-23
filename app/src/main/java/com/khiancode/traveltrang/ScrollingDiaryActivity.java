package com.khiancode.traveltrang;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.khiancode.traveltrang.adapter.AdapterTripAdminDetail;
import com.khiancode.traveltrang.model.DIaryModel;
import com.khiancode.traveltrang.model.TripAdminDetailModel;
import com.khiancode.traveltrang.okhttp.ApiClient;
import com.khiancode.traveltrang.okhttp.CallServiceListener;

public class ScrollingDiaryActivity extends BaseActivity {

    int ID;
    ImageView imageHeader;
    Toolbar toolbar;
    CollapsingToolbarLayout toolbarLayout;
    TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling_diary);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbarLayout = findViewById(R.id.toolbar_layout);

        imageHeader = findViewById(R.id.htab_header);
        textView = findViewById(R.id.detail);

        ID = getIntent().getExtras().getInt("id");

        getData(ID);
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
        post.setURL(BaseActivity.BASE_URL + "user/diary-id/" + ID);
        post.execute();
        post.setListenerCallService(new CallServiceListener() {
            @Override
            public void ResultData(String data) {
                hideProgressDialog();

                setView(data);
            }

            @Override
            public void ResultError(String data) {
                hideProgressDialog();
                dialogResultError(data);
            }

            @Override
            public void ResultNull(String data) {
                hideProgressDialog();
                dialogResultNull();
            }
        });
    }

    private void setView(String json) {
        Gson gson = new Gson();
        final DIaryModel posts = gson.fromJson(json, DIaryModel.class);
        toolbarLayout.setTitle(posts.getDetail());

        Glide.with(this)
                .load(BaseActivity.BASE_URL_PICTURE + "/images_resize/" + posts.getImage())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .error(R.drawable.nopic)
                .into(imageHeader);

        textView.setText(posts.getDetail());
    }

}
