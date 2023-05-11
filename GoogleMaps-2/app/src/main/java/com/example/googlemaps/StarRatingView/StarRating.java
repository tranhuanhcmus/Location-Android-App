package com.example.googlemaps.StarRatingView;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

import com.example.googlemaps.R;

public class StarRating extends View {
    private float rating; // số sao

    public StarRating(Context context, AttributeSet attrs) {
        super(context, attrs);
        // Thực hiện các thao tác cần thiết để tạo view
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Vẽ các sao
        for (int i = 0; i < 5; i++) {
            Drawable star = getResources().getDrawable(i < rating ? R.drawable.ic_star_filled : R.drawable.ic_star_half_filled);
            star.setBounds(getStarLeft(i), 0, getStarRight(i), getHeight());
            star.draw(canvas);
        }
    }

    // Lấy tọa độ bên trái của sao thứ i
    private int getStarLeft(int i) {
        return i * getWidth() / 5;
    }

    // Lấy tọa độ bên phải của sao thứ i
    private int getStarRight(int i) {
        return (i + 1) * getWidth() / 5;
    }

    // Set số sao
    public void setRating(float rating) {
        this.rating = rating;
        invalidate(); // Vẽ lại view
    }
}
