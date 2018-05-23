package com.khiancode.traveltrang;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.khiancode.traveltrang.fragment.CategoryFragment;
import com.khiancode.traveltrang.fragment.DiaryFragment;
import com.khiancode.traveltrang.fragment.FavoritFragment;
import com.khiancode.traveltrang.fragment.MainFragment;
import com.khiancode.traveltrang.fragment.MapFragment;
import com.khiancode.traveltrang.fragment.TipCreateFragment;

public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private SharedPreferences sp;
    public FloatingActionButton fab;
    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        sp = getSharedPreferences("Preferences_TravelTrang", Context.MODE_PRIVATE);
        fab = findViewById(R.id.fab);
        toolbar.setTitle("หน้าหลัก");
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);
        TextView name = headerView.findViewById(R.id.name);
        TextView email = headerView.findViewById(R.id.email);
        name.setText(sp.getString("name",""));
        email.setText(sp.getString("email",""));
        fab.hide();
        setFram(new MainFragment());
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.main, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_favorite) {
//
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        fab.hide();
        if (id == R.id.nav_home) {
//            Crashlytics.getInstance().crash();
            setFram(new MainFragment());
            toolbar.setTitle("หน้าหลัก");
        } else if (id == R.id.nav_place) {
            toolbar.setTitle("สถานที่");
            setFram(new CategoryFragment());
        } else if (id == R.id.nav_create) {
            toolbar.setTitle("สร้างทริป");
            setFram(new TipCreateFragment());
            fab.show();
        }
        else if (id == R.id.nav_map) {
            toolbar.setTitle("แผนที่");
            setFram(new MapFragment());
        }
        else if (id == R.id.nav_follow) {
            toolbar.setTitle("รายการที่ชื่อชอบ");
            setFram(new FavoritFragment());
        } else if (id == R.id.nav_diary) {
            toolbar.setTitle("ไดอารี่");
            setFram(new DiaryFragment());
            fab.show();
        }
        else if (id == R.id.nav_logout) {
            super.LogoutApp();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void setFram(Fragment fram) {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction ft = manager.beginTransaction();
        ft.replace(R.id.content, fram);
        ft.commit();
    }
}
