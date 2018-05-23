package com.khiancode.traveltrang;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.khiancode.traveltrang.adapter.AdapterTravel;
import com.khiancode.traveltrang.adapter.AdapterTravelSelect;
import com.khiancode.traveltrang.model.CategoryTravelModel;
import com.khiancode.traveltrang.okhttp.ApiClient;
import com.khiancode.traveltrang.okhttp.CallServiceListener;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import okhttp3.FormBody;
import okhttp3.RequestBody;

public class SelectTravelActivity extends BaseActivity {
    private RecyclerView recyclerView;
    private AdapterTravelSelect adapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    int tripId, day;
    public int[] activitie;
    String[] search;
    int sum = 0;
    int index = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_travel);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView = findViewById(R.id.dummyfrag_scrollableview);
        mSwipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);

        recyclerView.setLayoutManager(new LinearLayoutManager(getBaseContext()));
        recyclerView.setHasFixedSize(true);

        tripId = getIntent().getExtras().getInt("trip");
        day = getIntent().getExtras().getInt("day");
        getData();
    }

    private void getData() {
        ApiClient.GET post = new ApiClient.GET(this);
        post.setURL(BaseActivity.BASE_URL + "user/travel");
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
        Type collectionType = new TypeToken<Collection<CategoryTravelModel>>() {
        }.getType();
        Collection<CategoryTravelModel> enums = gson.fromJson(json, collectionType);
        final ArrayList<CategoryTravelModel> posts = new ArrayList<CategoryTravelModel>(enums);

        activitie = new int[posts.size()];
        for (int i = 0; i < activitie.length; i++) {
            activitie[i] = 0;
        }

        adapter = new AdapterTravelSelect(this, posts);
        recyclerView.setAdapter(adapter);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getData();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_select_travel, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_done) {
            for (int i = 0; i < activitie.length; i++) {
                if (activitie[i] != 0) {
                    sum++;
                }
            }
            search = new String[sum];
            for (int i = 0; i < activitie.length; i++) {
                if (activitie[i] != 0) {
                    search[index] = String.valueOf(activitie[i]);
                    index++;
                }
            }
//            Toast.makeText(SelectTravelActivity.this, ""+ Arrays.toString(search), Toast.LENGTH_SHORT).show();
            sum = 0;
            index = 0;
            addTravel(implode(",", search));
            return true;
        }

        if (id == android.R.id.home) {
            Intent returnIntent = new Intent();
            setResult(Activity.RESULT_CANCELED, returnIntent);
            finish();
            overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_right);
        }

        return super.onOptionsItemSelected(item);
    }

    @NonNull
    public static String implode(String separator, String... data) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < data.length - 1; i++) {
            //data.length - 1 => to not add separator at the end
            if (!data[i].matches(" *")) {//empty string are ""; " "; "  "; and so on
                sb.append(data[i]);
                sb.append(separator);
            }
        }
        sb.append(data[data.length - 1].trim());
        return sb.toString();
    }

    private void addTravel(String data) {
        String TAG = SelectTravelActivity.class.getSimpleName();
        Log.d(TAG, data);
        RequestBody requestBody = new FormBody.Builder()
                .add("data", data)
                .add("day", String.valueOf(day))
                .add("tripId", String.valueOf(tripId))
                .build();

        ApiClient.POST post = new ApiClient.POST(this);
        post.setURL(BaseActivity.BASE_URL + "user/user/trip-addtravel");
        post.setRequestBody(requestBody);
        post.execute();
        post.setListenerCallService(new CallServiceListener() {
            @Override
            public void ResultData(String data) {
                if (data.equals("success")) {
                    Intent returnIntent = new Intent();
                    setResult(Activity.RESULT_CANCELED, returnIntent);
                    finish();
                }
            }

            @Override
            public void ResultError(String data) {
                hideProgressDialog();
                dialogResultError2();
            }

            @Override
            public void ResultNull(String data) {
                hideProgressDialog();
                dialogResultNull();
            }
        });
    }

}
