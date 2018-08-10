package com.liuyi.customprogressbar;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.Toast;

public class ProgressBarView extends View {

    private Paint mPaint;
    private RectF mRect;
    private float mCenterX;
    private float mCenterY;
    private float mWidth;
    private float mHeight;
    private float mCurTime;
    private Path mArrowPath;
    /**
     * 箭头上移的距离
     */
    private float upDisplacement;
    /**
     * 动画过程中箭头向左的横向位移
     */
    private float mArrowHorizontalLeftDisplacement;
    /**
     * 动画过程中箭头向右的横向位移
     */
    private float mArrowHorizontalRightDisplacement;
    /**
     * 动画过程中箭头往上跳动的形变量
     */
    private float shapeSize;
    /**
     * 是否在画向左横向移动的箭头
     */
    private boolean isDrawLeftHorizontalArrow;
    /**
     * 是否在画向右横向移动的箭头
     */
    private boolean isDrawRightHorizontalArrow = false;
    /**
     * 背景矩形的宽度变化范围
     */
    private int mBackgroundRectWidthChangeRange = 280;
    /**
     * 背景矩形的高度度变化范围
     */
    private float mBackgroundRectHeightChangeRange = mBackgroundRectWidthChangeRange * 0.23f;
    /**
     * 箭头边长
     */
    private float mArrowSideLength;
    private Matrix mArrowMatrix;
    private float mProgressToRightDisplacement;
    /**
     * 背景矩形的圆角度数
     */
    private int mBackgroundRectRadius = 20;
    private int mFirstPartAnimationDuration = 500;
    private ValueAnimator arrowMoveToRightAnimator;
    private AnimatorSet animatorSet;
    private ValueAnimator rectangleChangeAnimator;
    private ValueAnimator arrowHorizontalDisplacementAnimator;
    private ValueAnimator arrowUpDisplacementAnimator;


    public ProgressBarView(Context context) {
        this(context, null);
    }

    public ProgressBarView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ProgressBarView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);

    }

    private void init(AttributeSet attrs) {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mRect = new RectF();
        mArrowPath = new Path();
        mArrowMatrix = new Matrix();

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);


    }

    private int drawFlag = DRAW_BACKGROUND_RECT;
    public static final int DRAW_BACKGROUND_RECT = 0x001;
    //    public static final int DRAW_ARROW_MOVE_TO_LEFT = 0x002;
    public static final int DRAW_ARROW_MOVE_TO_RIGHT = 0x003;
    public static final int DRAW_PROGRESS = 0x004;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawBackgroundRect(canvas);
        drawArrowMoveToLeft(canvas);
        switch (drawFlag) {
            case DRAW_ARROW_MOVE_TO_RIGHT:
                break;
            case DRAW_PROGRESS:
                drawArrowMoveToRight(canvas);
                drawProgress(canvas);
                break;
            default:
        }
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
        mCenterX = w / 2;
        mCenterY = w / 2;
        mArrowSideLength = mCenterX / 8;

    }

    private int mCurentPercent;
    /**
     * 箭头向左移动时旋转的角度
     */
    private float mArrowToLeftRotateAngle;
    /**
     * 箭头向右移动时旋转的角度
     */
    private float mArrowToRightRotateAngle;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void start() {
        resetAnimation();

        /*矩形变化的动画*/
        rectangleChangeAnimator = ValueAnimator.ofFloat(0, mBackgroundRectWidthChangeRange);
        rectangleChangeAnimator.setDuration(mFirstPartAnimationDuration);
        rectangleChangeAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mCurWidth = (float) animation.getAnimatedValue();
                mCurHeight = mCurWidth * 0.23f;
                mCurTime = mCurWidth / 2.8f;
                invalidate();
            }
        });

        /*箭头横向位移的动画*/
        arrowHorizontalDisplacementAnimator = ValueAnimator.ofFloat(0, mBackgroundRectWidth / 2 + mBackgroundRectWidthChangeRange);
        arrowHorizontalDisplacementAnimator.setDuration(mFirstPartAnimationDuration);
        arrowHorizontalDisplacementAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mArrowHorizontalLeftDisplacement = (float) animation.getAnimatedValue();
                mArrowToLeftRotateAngle = 15.0f * mArrowHorizontalLeftDisplacement / (mBackgroundRectWidth / 2 + mBackgroundRectWidthChangeRange);
                isDrawLeftHorizontalArrow = true;
                invalidate();
            }
        });

        /*箭头往上跳动的动画*/
        arrowUpDisplacementAnimator = ValueAnimator.ofFloat(0, mArrowSideLength + 12);
        arrowUpDisplacementAnimator.setInterpolator(new OvershootInterpolator());
        arrowUpDisplacementAnimator.setDuration(mFirstPartAnimationDuration);
        arrowUpDisplacementAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                upDisplacement = (float) animation.getAnimatedValue();
            }
        });

        /*进度增加的动画*/
        arrowMoveToRightAnimator = ValueAnimator.ofFloat(0, mBackgroundRectWidth + 2 * mBackgroundRectWidthChangeRange);
        arrowMoveToRightAnimator.setStartDelay(mFirstPartAnimationDuration);
        arrowMoveToRightAnimator.setDuration(2000);
        arrowMoveToRightAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                drawFlag = DRAW_PROGRESS;
                mProgressToRightDisplacement = (float) animation.getAnimatedValue();
                mArrowHorizontalRightDisplacement = (float) animation.getAnimatedValue();
//                mArrowToRightRotateAngle = 15 * mProgressToRightDisplacement / (mBackgroundRectWidth + 2 * mBackgroundRectWidthChangeRange);
                mCurentPercent = (int) (101 * mProgressToRightDisplacement / (mBackgroundRectWidth + 2 * mBackgroundRectWidthChangeRange));
                isDrawRightHorizontalArrow = true;
                invalidate();
            }
        });
        arrowMoveToRightAnimator.addPauseListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationPause(Animator animation) {
                /*动画暂停的时候让箭头执行复位操作*/
                Toast.makeText(getContext(), "暂停", Toast.LENGTH_SHORT).show();
            }

            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onAnimationEnd(Animator animation) {
                animatorSet.reverse();
            }
        });

        ValueAnimator arrowToRightRotateDegreeAnimator = ValueAnimator.ofFloat(0, 15);
        arrowToRightRotateDegreeAnimator.setStartDelay(mFirstPartAnimationDuration);
        arrowToRightRotateDegreeAnimator.setDuration(200);
        arrowToRightRotateDegreeAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                isDrawRightHorizontalArrow = true;
                mArrowToRightRotateAngle = (float) animation.getAnimatedValue();
                invalidate();
            }
        });

        animatorSet = new AnimatorSet();
        animatorSet.play(rectangleChangeAnimator)
                .with(arrowUpDisplacementAnimator)
                .before(arrowHorizontalDisplacementAnimator)
                .before(arrowMoveToRightAnimator)
//                .with(arrowToRightRotateDegreeAnimator)
        ;
        animatorSet.start();
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onAnimationEnd(Animator animation) {
                if (!animatorSet.isStarted()) {
                    resetAnimation();
                }
//                rectangleChangeAnimator.reverse();
//                arrowUpDisplacementAnimator.reverse();
//                arrowHorizontalDisplacementAnimator.reverse();
//                arrowMoveToRightAnimator.reverse();
            }
        });
    }

    private void resetAnimation() {
        if (rectangleChangeAnimator != null) {
            rectangleChangeAnimator.end();
            arrowUpDisplacementAnimator.end();
            arrowHorizontalDisplacementAnimator.end();
            arrowMoveToRightAnimator.end();
        }

        mCurWidth = 0;
        mCurHeight = 0;
        mCurTime = 0;
        mArrowHorizontalLeftDisplacement = 0;
        mArrowToLeftRotateAngle = 0;
        upDisplacement = 0;
        mProgressToRightDisplacement = 0;
        mArrowHorizontalRightDisplacement = 0;
        mArrowToRightRotateAngle = 0;
        isDrawLeftHorizontalArrow = false;
        isDrawRightHorizontalArrow = false;
        mRect = new RectF();
        mPaint = new Paint();
        mArrowPath = new Path();
        mArrowMatrix = new Matrix();
        drawFlag = DRAW_BACKGROUND_RECT;
        postInvalidate();
    }

    public boolean isProgressPause() {
        return isProgressPause;
    }

    private boolean isProgressPause;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void setProgressPause(boolean setProgressPause) {
        isProgressPause = setProgressPause;
        if (setProgressPause) {
            arrowMoveToRightAnimator.pause();
        } else {
            arrowMoveToRightAnimator.resume();
        }
    }

    private void drawProgress(Canvas canvas) {
        mPaint.setColor(Color.WHITE);
        mRect.right = mCenterX - mBackgroundRectWidthChangeRange - mBackgroundRectWidth / 2 + mProgressToRightDisplacement;
        canvas.drawRoundRect(mRect, mBackgroundRectRadius, mBackgroundRectRadius, mPaint);
    }

    private void drawArrowMoveToRight(Canvas canvas) {
//        mArrowHorizontalLeftDisplacement
//        mArrowPath.reset();
//        mArrowPath.moveTo();

    }


    private void drawArrowMoveToLeft(Canvas canvas) {
        mPaint.setColor(Color.WHITE);
        mPaint.setStyle(Paint.Style.FILL);
        if (mCurTime > 0) {
            /*开始变形后箭头为圆角*/
            mPaint.setPathEffect(new CornerPathEffect(8));
        }
        shapeSize = mArrowSideLength * mCurTime / 200;
        if (isDrawLeftHorizontalArrow) {
            mArrowMatrix.reset();
            mArrowMatrix.preTranslate(0, shapeSize);
            mArrowMatrix.preRotate(15 - mArrowToLeftRotateAngle, mCenterX - mArrowHorizontalLeftDisplacement, mCenterY);
        } else {
            upDisplacement = upDisplacement - shapeSize;
        }
        if (isDrawRightHorizontalArrow) {
//            mArrowMatrix.setRotate(mArrowToRightRotateAngle, mArrowHorizontalRightDisplacement, mCenterY);
        }

        /*画箭头*/
        mArrowPath.reset();
        mArrowPath.moveTo(mCenterX - mArrowSideLength - mArrowHorizontalLeftDisplacement + mArrowHorizontalRightDisplacement, mCenterY - upDisplacement);
        mArrowPath.lineTo(mCenterX - mArrowSideLength / 2 - shapeSize - mArrowHorizontalLeftDisplacement + mArrowHorizontalRightDisplacement, mCenterY - upDisplacement);
        mArrowPath.lineTo(mCenterX - mArrowSideLength / 2 - shapeSize - mArrowHorizontalLeftDisplacement + mArrowHorizontalRightDisplacement, mCenterY - mArrowSideLength - upDisplacement);
        mArrowPath.lineTo(mCenterX + mArrowSideLength / 2 + shapeSize - mArrowHorizontalLeftDisplacement + mArrowHorizontalRightDisplacement, mCenterY - mArrowSideLength - upDisplacement);
        mArrowPath.lineTo(mCenterX + mArrowSideLength / 2 + shapeSize - mArrowHorizontalLeftDisplacement + mArrowHorizontalRightDisplacement, mCenterY - upDisplacement);
        mArrowPath.lineTo(mCenterX + mArrowSideLength - shapeSize - mArrowHorizontalLeftDisplacement + mArrowHorizontalRightDisplacement, mCenterY - upDisplacement);
        mArrowPath.lineTo(mCenterX - mArrowHorizontalLeftDisplacement + mArrowHorizontalRightDisplacement, mCenterY + mArrowSideLength - upDisplacement - shapeSize);
        mArrowPath.lineTo(mCenterX - mArrowSideLength + shapeSize - mArrowHorizontalLeftDisplacement + mArrowHorizontalRightDisplacement, mCenterY - upDisplacement);
        mArrowPath.close();
        mArrowPath.transform(mArrowMatrix);
        canvas.drawPath(mArrowPath, mPaint);

        if (isDrawRightHorizontalArrow) {
            /*画箭头上的文字*/
            mPaint.setColor(Color.BLACK);
            mPaint.setTextSize(42);
            canvas.drawTextOnPath(mCurentPercent + "%", mArrowPath, mArrowSideLength * 3 / 2, 2 * shapeSize - 15, mPaint);
        }

    }

    /**
     * 背景矩形的高度变化值
     */
    private float mCurHeight;
    /**
     * 背景矩形的宽度变化值
     */
    private float mCurWidth;
    /**
     * 大矩形的宽度
     */
    private int mBackgroundRectWidth = 300;
    /**
     * 大矩形的高度
     */
    private int mBackgroundRectHeight = 150;

    private void drawBackgroundRect(Canvas canvas) {
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(Color.parseColor("#2294E0"));
        /*蓝色背景*/
        canvas.drawRect(0, mCenterX - 300, mWidth, mCenterX + 300, mPaint);

        mPaint.setColor(Color.parseColor("#FF525253"));
        mRect.left = mCenterX - mBackgroundRectWidth / 2 - mCurWidth;
        mRect.right = mCenterX + mBackgroundRectWidth / 2 + mCurWidth;
        mRect.top = mCenterY - mBackgroundRectHeight / 2 + mCurHeight;
        mRect.bottom = mCenterY + mBackgroundRectHeight / 2 - mCurHeight;
        /*灰色矩形*/
        canvas.drawRoundRect(mRect, mBackgroundRectRadius, mBackgroundRectRadius, mPaint);
    }
}
