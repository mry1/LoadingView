package com.liuyi.adimageview;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;

public class AdImageView extends android.support.v7.widget.AppCompatImageView {

    private int screenHeight;
    private int intrinsicHeight;

    public AdImageView(Context context) {
        this(context, null);
    }

    public AdImageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AdImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {

    }

    private int mWidth;
    private int mHeight;

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;

        Resources resources = this.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        float density1 = dm.density;
        int width3 = dm.widthPixels;
        screenHeight = dm.heightPixels;
    }

    private int mDy;

    private int itemHeight;

    public void setDy(int dy, int itemHeight) {
        this.itemHeight = itemHeight;
//        if (dy > intrinsicHeight) {
//            dy = intrinsicHeight;
//        }

        this.mDy = -dy;
        System.out.println(dy + "==");
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Drawable drawable = getDrawable();

        intrinsicHeight = drawable.getIntrinsicHeight();
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), intrinsicHeight);
//        canvas.translate(0, mDy * intrinsicHeight / screenHeight);
        canvas.translate(0, mDy * screenHeight / (intrinsicHeight + 5 * itemHeight));
        super.onDraw(canvas);
    }
}
