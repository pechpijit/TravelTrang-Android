package com.khiancode.traveltrang;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.crashlytics.android.Crashlytics;
import com.khiancode.traveltrang.fragment.CategoryFragment;
import com.khiancode.traveltrang.fragment.CommentFragment;
import com.khiancode.traveltrang.fragment.DetailTravelFragment;
import com.khiancode.traveltrang.okhttp.ApiClient;
import com.khiancode.traveltrang.okhttp.CallServiceListener;

import java.util.ArrayList;
import java.util.List;

import io.fabric.sdk.android.Fabric;
import okhttp3.FormBody;
import okhttp3.RequestBody;

public class DetailTravelActivity extends BaseActivity {

    int id;
    String image;
    ImageView imageHeader;
    public FloatingActionButton fab;
    public Toolbar toolbar;
    MenuItem menuItemCount;
    boolean follow = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_tabs_header);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            Window w = getWindow(); // in Activity's onCreate() for instance
//            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
//        }
        id = getIntent().getExtras().getInt("id");
        image = getIntent().getExtras().getString("image");

        imageHeader = findViewById(R.id.htab_header);
        fab = findViewById(R.id.fab);
       toolbar = findViewById(R.id.htab_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final ViewPager viewPager = findViewById(R.id.htab_viewpager);
        setupViewPager(viewPager);

        TabLayout tabLayout = findViewById(R.id.htab_tabs);
        tabLayout.setupWithViewPager(viewPager);

        final CollapsingToolbarLayout collapsingToolbarLayout = findViewById(R.id.htab_collapse_toolbar);
        collapsingToolbarLayout.setTitleEnabled(false);

        fab.setVisibility(View.INVISIBLE);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(DetailTravelActivity.this, "Comment", Toast.LENGTH_SHORT).show();
            }
        });

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                if (tab.getPosition() == 1) {
                    fab.show();
                } else {
                    fab.hide();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        Glide.with(this)
                .load(BaseActivity.BASE_URL_PICTURE + "/images/travel/" + image)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .error(R.drawable.placeholder)
                .into(imageHeader);

        imageHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(DetailTravelActivity.this,ShowPictureActivity.class).putExtra("image",image));
            }
        });

        checkFollow();
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new DetailTravelFragment(id), "รายละเอียด");
        adapter.addFrag(new CommentFragment(id), "คอมเมนต์");
        viewPager.setAdapter(adapter);
    }

    static class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_right);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        menuItemCount = menu.findItem(R.id.action_favorite);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_favorite) {
            followPost();
            return true;
        }

        if (id == android.R.id.home) {
            finish();
            overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_right);
        }

        return super.onOptionsItemSelected(item);
    }

    private void followPost() {
        SharedPreferences  sp = getSharedPreferences("Preferences_TravelTrang", Context.MODE_PRIVATE);
        RequestBody requestBody = new FormBody.Builder()
                .add("customerId", String.valueOf(sp.getInt("id",0)))
                .add("travelId",String.valueOf(id) )
                .build();

        ApiClient.POST post = new ApiClient.POST(this);
        if (follow) {
            post.setURL(BASE_URL + "user/follow-unlike");
        } else {
            post.setURL(BASE_URL + "user/follow-like");
        }
        post.setRequestBody(requestBody);
        post.execute();
        post.setListenerCallService(new CallServiceListener() {
            @Override
            public void ResultData(String data) {
                if (data.equals("unlikesuccess")) {
                    menuItemCount.setIcon(R.drawable.ic_favorite_border_white_48dp);
                    follow = false;
                    Toast.makeText(DetailTravelActivity.this, "UnLike", Toast.LENGTH_SHORT).show();
                }

                if (data.equals("likesuccess")) {
                    menuItemCount.setIcon(R.drawable.ic_favorite_white_48dp);
                    follow = true;
                    Toast.makeText(DetailTravelActivity.this, "Like", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void ResultError(String data) {
                Toast.makeText(DetailTravelActivity.this, "ไม่สามารถทำรายการได้ กรุณาลองใหม่อีกครั้ง", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void ResultNull(String data) {
                Toast.makeText(DetailTravelActivity.this, "ไม่สามารถทำรายการได้ กรุณาลองใหม่อีกครั้ง", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkFollow() {
        SharedPreferences  sp = getSharedPreferences("Preferences_TravelTrang", Context.MODE_PRIVATE);
        RequestBody requestBody = new FormBody.Builder()
                .add("customerId", String.valueOf(sp.getInt("id",0)))
                .add("travelId",String.valueOf(id) )
                .build();

        ApiClient.POST post = new ApiClient.POST(this);
        post.setURL(BASE_URL+"user/follow-check");
        post.setRequestBody(requestBody);
        post.execute();
        post.setListenerCallService(new CallServiceListener() {
            @Override
            public void ResultData(String data) {
                if (data.equals("0")) {
                    menuItemCount.setIcon(R.drawable.ic_favorite_border_white_48dp);
                    follow = false;
                } else {
                    menuItemCount.setIcon(R.drawable.ic_favorite_white_48dp);
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
