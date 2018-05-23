package com.khiancode.traveltrang;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.khiancode.traveltrang.fragment.CommentFragment;
import com.khiancode.traveltrang.fragment.DetailTravelFragment;
import com.khiancode.traveltrang.fragment.TravelFragment;
import com.khiancode.traveltrang.okhttp.ApiClient;
import com.khiancode.traveltrang.okhttp.CallServiceListener;

import java.util.ArrayList;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.RequestBody;

public class DetailTripDayActivity extends BaseActivity {

    int id;
    String image;
    ImageView imageHeader;
    public FloatingActionButton fab;
    public Toolbar toolbar;
    MenuItem menuItemCount;
    boolean follow = false;
    int day = 0;
    int page = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_trip_day);
        id = getIntent().getExtras().getInt("id");
        day = getIntent().getExtras().getInt("day");
        image = getIntent().getExtras().getString("image");
        page = getIntent().getExtras().getInt("page");

        imageHeader = findViewById(R.id.htab_header);
        fab = findViewById(R.id.fab);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        final ViewPager viewPager = findViewById(R.id.htab_viewpager);
        setupViewPager(viewPager);

        TabLayout tabLayout = findViewById(R.id.htab_tabs);
        tabLayout.setupWithViewPager(viewPager);


        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

       viewPager.setCurrentItem(page-1);
    }

    private void setupViewPager(ViewPager viewPager) {
        DetailTravelActivity.ViewPagerAdapter adapter = new DetailTravelActivity.ViewPagerAdapter(getSupportFragmentManager());
        for (int i = 1; i <= day; i++) {
            adapter.addFrag(new TravelFragment(id,i), "วันที่ "+i);
        }
        viewPager.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_right);
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


}
