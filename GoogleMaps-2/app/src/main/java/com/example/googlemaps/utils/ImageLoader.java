package com.example.googlemaps.utils;


import android.view.View;
import android.webkit.URLUtil;
import android.widget.ImageView;

import com.example.googlemaps.R;
import com.squareup.picasso.Picasso;


public class ImageLoader {
    public static final String PLACEHOLDER_IMAGE_URL = "http://abc.com/xyz.jpg";


    /**
     * Picasso cannot load a null or empty url
     * this is a middleware function for load image safety
     * Use invalid url for a null or empty url
     */
    static String safetyImageURL(String url) {
        if (URLUtil.isValidUrl(url) && url != null
                && !url.contains("default.jpg")) return url;
        return PLACEHOLDER_IMAGE_URL;
    }

    public static void loadImageChatMessage(View view, String url) {
        Picasso.get().load(safetyImageURL(url))
                .placeholder(R.drawable.placeholder_image_chat)
                .error(R.drawable.placeholder_image_chat)
                .fit()
                .centerCrop()
                .into((ImageView) view);
    }

    public static void loadPhotoView(View view, String url) {
        Picasso.get().load(safetyImageURL(url))
                .placeholder(R.drawable.placeholder_image_chat)
                .error(R.drawable.placeholder_image_chat)
                .into((ImageView) view);
    }
}