package com.liuyi.swipemenulayout.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.OvershootInterpolator;

import com.liuyi.swipemenulayout.R;

/**
 * @author liuyi
 * @date 2018/8/10
 */
public class SwipeMenuLayout extends ViewGroup {

    /**
     * 处理单击事件的冲突
     */
    private int mScaledTouchSlop;
    /**
     * 是否开启滑动
     */
    private boolean isSwipeEnable;
    /**
     * 是否左滑
     */
    private boolean isLeftSwipe;
    /**
     * ViewGroup中第一个View
     */
    private View mContentView;
    /**
     * 屏幕宽度
     */
    private int mScreenWidth;
    /**
     * 左滑时右侧菜单的总宽度
     */
    private int mRightMenuWidth;
    /**
     * 用于绘制该ViewGroup的height
     */
    private int mHeight;
    /**
     * 多指同时按下的时候的第一个触点的id
     */
    private int mPointerId;
    private VelocityTracker mVelocityTracker;
    private int mMaximumFlingVelocity;
    /**
     * 滑动临界值，当超过这个限制的时候可以打开或关闭menu
     */
    private int mSlideLimit;
    private ValueAnimator closeAnimator;
    private ValueAnimator expandAnimator;

    public SwipeMenuLayout(Context context) {
        this(context, null);
    }

    public SwipeMenuLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SwipeMenuLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        mLastPoint = new PointF();
        mFirstPoint = new PointF();
        mScreenWidth = getScreenWidth(context);
        mScaledTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        mMaximumFlingVelocity = ViewConfiguration.get(context).getScaledMaximumFlingVelocity();
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.SwipeMenuLayout);
        try {
            isSwipeEnable = ta.getBoolean(R.styleable.SwipeMenuLayout_swipeEnable, true);
            isLeftSwipe = ta.getBoolean(R.styleable.SwipeMenuLayout_leftSwipe, true);
        } finally {
            ta.recycle();
        }


    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setClickable(true);/*如果不进行此设置，则MotionEvent中获取不到MOVE和UP事件*/

//        由于RecyclerView的复用机制，这里需要手动恢复初始值
        mRightMenuWidth = 0;

        int childCount = getChildCount();
        boolean isParentHeightMatchParent = MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.EXACTLY;
        boolean isNeedMeasureChildHeight = false;
        for (int i = 0; i < childCount; i++) {
            View childView = getChildAt(i);
            if (childView != null && childView.getVisibility() != GONE) {
                MarginLayoutParams lp = (MarginLayoutParams) childView.getLayoutParams();
//            measureChildWithMargins(childView, widthMeasureSpec, 0, heightMeasureSpec, 0);
                measureChild(childView, widthMeasureSpec, heightMeasureSpec);
                mHeight = Math.max(mHeight, childView.getMeasuredHeight());
                if (!isParentHeightMatchParent && lp.height == MarginLayoutParams.MATCH_PARENT) {
                    isNeedMeasureChildHeight = true;
                }
                if (i > 0) {
                    mRightMenuWidth += childView.getMeasuredWidth();
                } else {
                    mContentView = childView;
                    /*将第一个View的宽度设置为屏幕宽度*/
//                lp.width = mScreenWidth - lp.leftMargin - lp.rightMargin;
//                mContentView.setLayoutParams(lp);
                }
            }
        }
        mSlideLimit = mRightMenuWidth / 2;
        setMeasuredDimension(getPaddingLeft() + getPaddingRight() + mContentView.getMeasuredWidth(),
                getPaddingTop() + mContentView.getMeasuredHeight() + getPaddingBottom());
        if (isNeedMeasureChildHeight) {
            forceUniformHeight(childCount, widthMeasureSpec);
        }
    }

    /**
     * 当父View不为MatchParent时给MatchParent的子View设置高度
     *
     * @param childCount
     * @param widthMeasureSpec
     * @see android.widget.LinearLayout# forceUniformHeight
     */
    private void forceUniformHeight(int childCount, int widthMeasureSpec) {
        // Pretend that the linear layout has an exact size. This is the measured height of
        // ourselves. The measured height should be the max height of the children, changed
        // to accommodate the heightMeasureSpec from the parent
        int uniformMeasureSpec = MeasureSpec.makeMeasureSpec(getMeasuredHeight(),
                MeasureSpec.EXACTLY);
        for (int i = 0; i < childCount; ++i) {
            final View child = getChildAt(i);
            if (child != null && child.getVisibility() != GONE) {
                MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();
                if (lp.height == LayoutParams.MATCH_PARENT) {
                    // Temporarily force children to reuse their old measured width
                    // FIXME: this may not be right for something like wrapping text?
                    int oldWidth = lp.width;
                    lp.width = child.getMeasuredWidth();

                    // Remeasure with new dimensions
                    measureChildWithMargins(child, widthMeasureSpec, 0, uniformMeasureSpec, 0);
                    lp.width = oldWidth;
                }
            }
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int mMenuViewLeft = 0 + getPaddingLeft();
        int mMenuViewRight = 0 + getPaddingRight();
        for (int i = 0; i < getChildCount(); i++) {
            View childView = getChildAt(i);
            if (childView != null && childView.getVisibility() != GONE) {
                MarginLayoutParams lp = (MarginLayoutParams) childView.getLayoutParams();
//            左滑菜单的left位置
                if (i == 0) {
//                mMenuViewLeft += lp.leftMargin;
//                第一个View，设置为屏幕宽度
                    childView.layout(mMenuViewLeft, /*lp.topMargin +*/ getPaddingTop(), mMenuViewLeft /*+ lp.leftMargin*/ + childView.getMeasuredWidth(),
                            getPaddingTop() + childView.getMeasuredHeight());
                    mMenuViewLeft = mMenuViewLeft + childView.getMeasuredWidth();
                } else {
                    childView.layout(mMenuViewLeft, getPaddingTop(), mMenuViewLeft + childView.getMeasuredWidth(), getPaddingTop() + childView.getMeasuredHeight());
                    mMenuViewLeft = mMenuViewLeft + childView.getMeasuredWidth();
                }
            }
        }
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }

    /**
     * 是否有手指在滑动，防止多指操作
     */
    private static boolean isTouching;

    /**
     * 当已经有别的item处于打开状态时，点击item应该先将别的item关闭，此变量用于存储处于打开状态的item
     */
    private static SwipeMenuLayout mViewCache;

    /**
     * 存储手指按下去时的位置
     */
    private PointF mLastPoint;
    /**
     * 存储手指第一次按下去时的位置
     */
    private PointF mFirstPoint;

    /**
     * menu是否正在滑动，滑动过程中不允许父View上下滑动
     */
    private boolean isSwiping;

    /**
     * 是否是阻塞式滑动
     */
    private boolean isBlockMode = true;
    private static boolean isBlock;

    /**
     * 设置是否阻塞式滑动
     *
     * @param isBlockMode
     */
    public void setBlockMode(boolean isBlockMode) {
        this.isBlockMode = isBlockMode;
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (isSwipeEnable) {
            acquireVelocityTracker(ev);
            switch (ev.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (isTouching) {/*防止多指操作*/
                        return false;
                    } else {
                        isTouching = true;
                    }
                    isMenuOpened = true;
                    if (mViewCache != null) {// 说明有别的item已经处于打开状态
                        isBlock = isBlockMode;
                        if (mViewCache != this) {
                            mViewCache.smoothClose();
                        }
//                        只要有一个item处于打开状态，则不允许父布局上下滑动了
                        getParent().requestDisallowInterceptTouchEvent(true);
                    }
                    mLastPoint.set(ev.getRawX(), ev.getRawY());
                    mFirstPoint.set(ev.getRawX(), ev.getRawY());
//                    多指按下时取第一个手指的id
                    mPointerId = ev.getPointerId(0);
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (mViewCache != this && isBlock) {
                        break;
                    }
                    /*在水平滑动中禁止父布局上下滑动*/
                    float currentTouchSlop = mLastPoint.x - ev.getRawX();
                    if (isSwiping || Math.abs(currentTouchSlop) > mScaledTouchSlop) {
                        getParent().requestDisallowInterceptTouchEvent(true);
                    } else {
                        getParent().requestDisallowInterceptTouchEvent(false);
                    }
                    if (Math.abs(currentTouchSlop) > mScaledTouchSlop) {
                        isMenuOpened = false;
                    }
                    isSwiping = true;

                    scrollBy((int) currentTouchSlop, 0);

                    if (getScrollX() < 0) {
                        scrollTo(0, 0);
                    }
                    if (getScrollX() >= mRightMenuWidth) {
                        scrollTo(mRightMenuWidth, 0);
                    }
                    mLastPoint.set(ev.getRawX(), ev.getRawY());
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    isSwiping = false;
                    if (mViewCache == this || !isBlock) {
                        mVelocityTracker.computeCurrentVelocity(1000, mMaximumFlingVelocity);
                        float xVelocity = mVelocityTracker.getXVelocity(mPointerId);
                        if (Math.abs(xVelocity) > 1000) {/*首先根据滑动速度判断是否打开或关闭menu，速度未达到标准则判断滑动距离*/
                            if (xVelocity < -1000) {
                                /*向左滑动，打开menu*/
                                smoothExpand();
                            } else {
                                /*关闭menu*/
                                smoothClose();
                            }
                        } else {/*判断滑动距离*/
                            if (Math.abs(getScrollX()) > mSlideLimit) {
                                if (getScrollX() > 0) {
                                    smoothExpand();
                                } else {
                                    smoothClose();
                                }
                            } else {
                                smoothClose();
                            }

                        }
                    }
                    releaseVelocityTracker();
                    isTouching = false;
                    break;
                default:
            }
        }

        return super.dispatchTouchEvent(ev);
    }

//    @Override
//    public boolean performClick() {
//        /*滑动时，禁止点击事件*/
//        if (Math.abs(getScrollX()) > mScaledTouchSlop) {
//            return false;
//        }
//        return super.performClick();
//    }

    /**
     * 菜单区域是否打开，打开状态下点击内容区会关闭菜单
     */
    private boolean isMenuOpened;

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (isSwipeEnable) {
            switch (ev.getAction()) {
                case MotionEvent.ACTION_MOVE:

                    break;
                case MotionEvent.ACTION_UP:
                    // 侧滑菜单时，屏蔽点击事件
                    if (getScrollX() > mScaledTouchSlop) {
//                        只屏蔽内容区域的点击事件，不能屏蔽掉右侧menu区域的点击事件
                        if (ev.getX() < getWidth() - getScrollX()) {
                            // 侧滑菜单展开时，点击内容区域，关闭侧滑菜单。
                            if (isMenuOpened) {
                                smoothClose();
                            }
                            return true;
                        }

                    }
                    if (isClosing) {
                        return true;
                    } else {
                        return super.onInterceptTouchEvent(ev);
                    }
                default:
            }

        }

        return super.onInterceptTouchEvent(ev);
    }

    /**
     * 释放VelocityTracker
     */
    private void releaseVelocityTracker() {
        if (mVelocityTracker != null) {
            mVelocityTracker.clear();
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
    }

    /**
     * 初始化VelocityTracker
     *
     * @param ev
     */
    private void acquireVelocityTracker(MotionEvent ev) {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(ev);
    }


    /**
     * 是否处于动画过程中
     */
//    private static boolean isAnimating;

    /**
     * 平滑打开
     */
    public void smoothExpand() {
//        展开的时候将当前ViewGroup设置为正在展开的View
//        isMenuOpened = true;
        mViewCache = this;
        cancelAnim();
        expandAnimator = ValueAnimator.ofInt(getScrollX(), mRightMenuWidth);
        expandAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                scrollTo((Integer) animation.getAnimatedValue(), 0);
            }
        });
        expandAnimator.setInterpolator(new OvershootInterpolator());
        expandAnimator.setDuration(300);
        expandAnimator.start();

    }

    public void cancelAnim() {
        if (expandAnimator != null && expandAnimator.isRunning()) {
            expandAnimator.cancel();
        }
        if (closeAnimator != null && closeAnimator.isRunning()) {
            closeAnimator.cancel();
        }
    }

    /**
     * 正处在关闭动画中，使用此变量的作用：当前item处于打开状态，点击别的item需要屏蔽点击事件
     */
    private static boolean isClosing;

    /**
     * 平滑关闭
     */
    public void smoothClose() {
//        isMenuOpened = false;
        mViewCache = null;
        cancelAnim();
        closeAnimator = ValueAnimator.ofInt(getScrollX(), 0);
        closeAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                scrollTo((Integer) animation.getAnimatedValue(), 0);
            }
        });
        closeAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationCancel(Animator animation) {
                super.onAnimationCancel(animation);
                isBlock = false;
                isClosing = false;
            }

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                if (getScrollX() > mScaledTouchSlop) {
                    isClosing = true;
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                isBlock = false;
                isClosing = false;
            }

        });
        closeAnimator.setInterpolator(new AccelerateInterpolator());
        closeAnimator.setDuration(300);
        closeAnimator.start();

    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mViewCache == this) {
            mViewCache.smoothClose();
            mViewCache = null;
        }
    }

    public static int getScreenWidth(Context context) {
        Resources resources = context.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
//        分辨率
        float density = dm.density;
//        宽度
        int width = dm.widthPixels;
//        高度
        int height = dm.heightPixels;
        return width;
    }

}
