package com.khiancode.traveltrang;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.github.chrisbanes.photoview.PhotoView;

public class ShowPictureTripActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_show_picture);
        String imageName = getIntent().getExtras().getString("image");

        PhotoView photoView = findViewById(R.id.photo_view);
        Glide.with(this)
                .load(BaseActivity.BASE_URL_PICTURE + "/images/trip/" + imageName)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .error(R.drawable.nopic)
                .into(photoView);
    }
}
