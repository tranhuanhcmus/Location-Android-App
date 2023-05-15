package com.example.googlemaps.Fragments;



import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.example.googlemaps.R;
import com.example.googlemaps.utils.ImageLoader;
import com.github.chrisbanes.photoview.PhotoView;

public class PhotoViewActivity extends AppCompatActivity {

    PhotoView photoView;
    ImageButton btnClose;
    public static final String CHAT_IMAGE_URL = "CHAT_IMAGE_URL";
    public static final String CHAT_IMAGE_BITMAP = "CHAT_IMAGE_BITMAP";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_view);

        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
        changeStatusBarColor();

        photoView = (PhotoView) findViewById(R.id.photoView);
        btnClose = (ImageButton) findViewById(R.id.btnClose);

        btnClose.setOnClickListener(view -> onBackPressed());

        String url = getIntent().getStringExtra(CHAT_IMAGE_URL);
        if (url != null) {
            ImageLoader.loadPhotoView(photoView, url);
        } else {
            Bitmap bitmap = getIntent().getParcelableExtra(CHAT_IMAGE_BITMAP);
            photoView.setImageBitmap(bitmap);
        }
    }

    private void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }
}
