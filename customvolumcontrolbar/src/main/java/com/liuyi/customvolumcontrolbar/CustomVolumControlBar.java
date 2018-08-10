package com.liuyi.customvolumcontrolbar;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class CustomVolumControlBar extends View {
    /**
     * 第一圈的颜色
     */
    private int firstColor;
    /**
     * 第二圈的颜色
     */
    private int secondColor;
    /**
     * 圈的宽度
     */
    private int circleWidth;
    /**
     * 背景图片
     */
    private Bitmap bg;
    /**
     * 每个块之间的间隙有多少度
     */
    private int splitSize;
    /**
     * 块的个数
     */
    private int dotCount;
    private Context mContext;
    private Paint mPaint;
    /**
     * 圆的内切矩形
     */
    private Rect mRect;
    /**
     * 圆的外切矩形
     */
    private RectF mOuterRect;
    private int mWidth;
    private int mHeight;
    private long l;

    public CustomVolumControlBar(Context context) {
        this(context, null);
    }

    public CustomVolumControlBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomVolumControlBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        initView(attrs, defStyleAttr);

    }

    private void initView(AttributeSet attrs, int defStyleAttr) {
        TypedArray t = mContext.obtainStyledAttributes(attrs, R.styleable.CustomVolumControlBar);
        try {
            firstColor = t.getColor(R.styleable.CustomVolumControlBar_firstColor, Color.parseColor("#FEFEFE"));
            secondColor = t.getColor(R.styleable.CustomVolumControlBar_secondColor, Color.parseColor("#252420"));
            circleWidth = t.getDimensionPixelSize(R.styleable.CustomVolumControlBar_circleWidth, 25);
            bg = BitmapFactory.decodeResource(t.getResources(), t.getResourceId(R.styleable.CustomVolumControlBar_bg, R.mipmap.ic_launcher));
            splitSize = t.getInteger(R.styleable.CustomVolumControlBar_splitSize, 10);
            dotCount = t.getInteger(R.styleable.CustomVolumControlBar_dotCount, 20);
        } finally {
            t.recycle();
        }

        mPaint = new Paint();
        mRect = new Rect();
        mOuterRect = new RectF();

        // 消除锯齿
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(circleWidth);
//        设置空心
        mPaint.setStyle(Paint.Style.STROKE);
//        设置线段断点处为圆头
        mPaint.setStrokeCap(Paint.Cap.ROUND);

    }

    /**
     * 圆心x坐标
     */
    private int centure;
    private int radius;

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        mWidth = w;
        mHeight = h;
        centure = mWidth / 2;
        radius = centure - circleWidth / 2 - 300;

    }

    private int outRectDiffWidth = 90;
    private int outRectDiffHeight = 50;

    @Override
    protected void onDraw(Canvas canvas) {
        int relRadius = radius - circleWidth / 2;
        drawOuterRect(canvas, relRadius);
        drawCircle(canvas);
        /*圆的内切矩形*/
        mRect.left = (int) (centure - relRadius * Math.cos(45 * Math.PI / 180));
        mRect.top = (int) (centure - relRadius * Math.cos(45 * Math.PI / 180));
        mRect.right = (int) (centure + relRadius * Math.cos(45 * Math.PI / 180));
        mRect.bottom = (int) (centure + relRadius * Math.cos(45 * Math.PI / 180));

        if (bg.getWidth() < mRect.right - mRect.left) {
            /*如果图片较小，则置于中心位置*/
            mRect.left = centure - bg.getWidth() / 2;
            mRect.right = centure + bg.getWidth() / 2;
        }
        if (bg.getHeight() < mRect.bottom - mRect.top) {
            mRect.top = centure - bg.getHeight() / 2;
            mRect.bottom = centure + bg.getHeight() / 2;
        }
        canvas.drawBitmap(bg, null, mRect, mPaint);

    }

    /**
     * 绘制外切矩形背景
     */
    private void drawOuterRect(Canvas canvas, int relRadius) {
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(Color.parseColor("#D04A473E"));
        mOuterRect.left = centure - relRadius - circleWidth - outRectDiffWidth;
        mOuterRect.right = centure + relRadius + circleWidth + outRectDiffWidth;
        mOuterRect.top = centure - relRadius - circleWidth - outRectDiffHeight;
        mOuterRect.bottom = centure + relRadius + circleWidth + outRectDiffHeight;
        canvas.drawRoundRect(mOuterRect, 20, 20, mPaint);
    }

    private int mCurrentCount = 9;
    float rawY;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                l = SystemClock.currentThreadTimeMillis();
                rawY = event.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                float rawYMove = event.getRawY();
                /*丢弃15ms内的滑动事件*/
                if (SystemClock.currentThreadTimeMillis() - l < 15) {
                    break;
                }

                if (rawYMove > rawY) {
                    /*下滑*/
                    lowerDownVolume();
                } else if (rawYMove < rawY) {
                    /*上滑*/
                    increaseVolume();
                }
                rawY = event.getRawY();
                l = SystemClock.currentThreadTimeMillis();
                break;
            case MotionEvent.ACTION_UP:
                break;
            default:
        }
        return true;
    }

    private void lowerDownVolume() {
        if (mCurrentCount >= 0) {
            mCurrentCount--;
            invalidate();
        }
    }

    private void increaseVolume() {
        if (mCurrentCount <= dotCount) {
            mCurrentCount++;
            invalidate();
        }
    }

    /**
     * 画出圆环
     *
     * @param canvas
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void drawCircle(Canvas canvas) {
        // 消除锯齿
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(circleWidth);
//        设置空心
        mPaint.setStyle(Paint.Style.STROKE);
//        设置线段断点处为圆头
        mPaint.setStrokeCap(Paint.Cap.ROUND);

        float itemSize = 360 * 1.0f / dotCount - splitSize;
        mPaint.setColor(firstColor);
        for (int i = 0; i < dotCount; i++) {
            canvas.drawArc(centure - radius, centure - radius, centure + radius, centure + radius,
                    i * (itemSize + splitSize), itemSize, false, mPaint);
        }
        mPaint.setColor(secondColor);
        for (int i = 0; i < mCurrentCount; i++) {
            canvas.drawArc(centure - radius, centure - radius, centure + radius, centure + radius,
                    i * (itemSize + splitSize), itemSize, false, mPaint);
        }

    }


}
