package com.nanochap.customringchart_master;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * @author xiaoyi
 * @description
 * 自定义柱状图
 * @date 2021/2/4
 */
public class BaseChart extends View {
  
    protected List<Double> mDatas;
    protected List<String> mDescription;
    //柱状图画笔
    protected Paint mDataLinePaint;
    protected int defaultLineColor = Color.parseColor("#2B6DE6");
    protected int descriptionColor;
    protected int dataColor;
    private int mWidth;
    private int mHeight;
    private int mShowNumber;

    private float perBarW;
    private Double maxData = 0.0;

    private int mMaxScrollx;
    protected int defaultBorderColor = Color.argb(255, 217, 217, 217);
    protected Paint mBorderLinePaint;
    //x轴描述文字画笔
    protected Paint mTextPaint;
    protected int descriptionTextSize;
    protected int dataTextSize;

    private int mBottomPadding;
    private int mLeftPadding;
    private int mTopPadding;


    protected float scale = 0.5f;

    protected boolean canClickAnimation = false;

    protected ValueAnimator animator;

    // 柱状图控件点击监听  0.0
    private setOnRangeBarItemClickListener onRangeBarItemClickListener;
    /* 用户点击到了无效位置 */
    public static final int INVALID_POSITION = -1;
    /* 辅助计算柱宽，表示一个条目的宽度，包括柱子和空余部分 */
    private float mItemBarWidth = 0;
    private Context mContext;

    public BaseChart(Context context) {
        super(context);
        init(context, null);
    }


    public BaseChart(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    @SuppressLint("CustomViewStyleable")
    private void init(Context context, AttributeSet attrs) {
        mContext = context;
        initAnimation();
        TypedArray t = context.obtainStyledAttributes(attrs, R.styleable.barCharts);
        defaultBorderColor = t.getColor(R.styleable.barCharts_borderColor, defaultBorderColor);
        descriptionTextSize = (int) t.getDimension(R.styleable.barCharts_labelTextSize, 30);
        dataTextSize = (int) t.getDimension(R.styleable.barCharts_dataTextSize, 20);
        descriptionColor = t.getColor(R.styleable.barCharts_descriptionTextColor, Color.GRAY);
        dataColor = t.getColor(R.styleable.barCharts_dataTextColor, Color.GRAY);
        mShowNumber = t.getInteger(R.styleable.barCharts_barShowNumber, 4);
        canClickAnimation = t.getBoolean(R.styleable.barCharts_isClickAnimation, false);
        t.recycle();
        
        mDatas = new ArrayList<>();
        mDescription = new ArrayList<>();

        mDataLinePaint = new Paint();
        mDataLinePaint.setAntiAlias(true);
        mDataLinePaint.setColor(defaultLineColor);
        mDataLinePaint.setStyle(Paint.Style.STROKE);

        mBorderLinePaint = new Paint();
        mBorderLinePaint.setColor(defaultBorderColor);
        mBorderLinePaint.setStyle(Paint.Style.STROKE);
        mBorderLinePaint.setStrokeWidth(dp2px(5));
        mBorderLinePaint.setAntiAlias(true);

        mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setColor(Color.GRAY);
        mTextPaint.setStyle(Paint.Style.FILL);
        mTextPaint.setTextSize(dp2pxTo(context,14.0f));
    }

    public BaseChart(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = getMeasuredWidth();
        mHeight = getMeasuredHeight();
        setDataLineWidth();
    }

    private void setDataLineWidth() {
        if (mDatas.size() > 0) {
            //设置柱状图宽度
            mDataLinePaint.setStrokeWidth(15);
            mMaxScrollx = (mWidth / mShowNumber) * mDatas.size() - mWidth;

            //计算ITEM宽度
            mItemBarWidth = mWidth / mDatas.size();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        perBarW = mWidth / mShowNumber;
        canvas.translate(0, mHeight - mBottomPadding);
        setMaxData();

        canvas.drawLine(0, 0, mMaxScrollx + mWidth, 0, mBorderLinePaint);
        for (int i = 0; i < mDatas.size(); i++) {
            String perData = String.valueOf(Math.round(scale < 1 ? Math.round(mDatas.get(i) * scale) : mDatas.get(i)));

            float x = (i + 0.5f) * perBarW;
            float y = (float) ((mHeight - mTopPadding - mBottomPadding) / maxData * mDatas.get(i));
            canvas.drawLine(x, 0, x, -y * scale, mDataLinePaint);

            mTextPaint.setTextSize(dataTextSize);
            mTextPaint.setColor(dataColor);
            if (Integer.parseInt(perData) != 0) {
                canvas.drawText(perData + "次",
                        x - mTextPaint.measureText(perData) / 2,
                        -y * scale - dataTextSize,
                        mTextPaint);
            }
            mTextPaint.setTextSize(descriptionTextSize);
            mTextPaint.setColor(descriptionColor);

        }
        //绘制描文字
        for (int i = 0; i < mDescription.size(); i++) {
            float x = (i + 0.5f) * perBarW;
            canvas.drawText(mDescription.get(i),
                    x - mTextPaint.measureText(mDescription.get(i)) / 2,
                    descriptionTextSize * 2,
                    mTextPaint);
        }
    }

    public void startCliclkAnimation() {
     //   if (canClickAnimation) {
            animator.start();
      //  }
    }

    private void initAnimation() {
        animator = ValueAnimator.ofFloat(0.2f, 1);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.setDuration(600);
        animator.setRepeatCount(0);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                scale = (float) animation.getAnimatedValue();
                postInvalidate();
            }
        });
    }
    
    public void setBottomDrawPadding(int bottomy) {
        mBottomPadding = bottomy;
    }

    public void setLeftDrawPadding(int left) {
        mLeftPadding = left;
    }

    public void setTopDrawPadding(int left) {
        mTopPadding = left;
    }


    private void setMaxData() {
        if (mDatas.size() > 0) {
            this.maxData = Collections.max(mDatas);
        }
    }
    
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            int position = identifyWhickItemClick(event.getX(), event.getY());
            if (position != INVALID_POSITION && onRangeBarItemClickListener != null) {
                startCliclkAnimation();
                onRangeBarItemClickListener.onItemClick(position);
            }
        }
        return super.onTouchEvent(event);
    }
    
    /**
     * 根据点击的手势位置识别是第几个柱图被点击
     *
     * @param x
     * @param y
     * @return -1时表示点击的是无效位置
     */
    private int identifyWhickItemClick(float x, float y) {
        float leftx = 0;
        float rightx = 0;
        for (int i = 0; i < mDatas.size(); i++) {
            leftx = i * mItemBarWidth;
            rightx = (i + 1) * mItemBarWidth;
            if (x < leftx) {
                break;
            }
            if (leftx <= x && x <= rightx && mDatas.get(i) != 0.0) {
                return i;
            }
        }
        return INVALID_POSITION;
    }


    /**
     * 设置点击监听
     *
     * @param onClickListener 点击回调接口
     */
    public void setOnRangeBarItemClickListener(setOnRangeBarItemClickListener onClickListener) {
        this.onRangeBarItemClickListener = onClickListener;
    }

    /**
     * 点击回调接口
     */
    public interface setOnRangeBarItemClickListener {
        /**
         * 点击回调方法
         *
         * @param position 点击的下标
         */
        void onItemClick(int position);
    }


    public void setDatas(List<Double> mDatas, List<String> mDescribe, boolean isAnimation) {
        this.mDatas.clear();
        this.mDatas.addAll(mDatas);
        this.mDescription = mDescribe;
        setDataLineWidth();
        if (isAnimation) {
            animator.start();
        } else {
            scale = 1;
            postInvalidate();
        }
    }

    public void addEndMoreData(List<Double> mDatas, List<String> mDesciption) {
        this.mDatas.addAll(mDatas);
        this.mDescription.addAll(mDesciption);
        setDataLineWidth();

        scale = 1;
        postInvalidate();
    }

    private int startX = 0;

    public void addStartMoreData(List<Double> mDatas, List<String> mDesciption) {
        mDatas.addAll(this.mDatas);
        mDesciption.addAll(this.mDescription);
        this.mDatas.clear();
        this.mDatas.addAll(mDatas);
        this.mDescription.clear();
        this.mDescription.addAll(mDesciption);
        startX = (mWidth / mShowNumber) * mDatas.size();
        setDataLineWidth();
        postInvalidate();
    }

    protected int dp2px(int dpValue) {
        return (int) getContext().getResources().getDisplayMetrics().density * dpValue;
    }
    
    public static float dp2pxTo(Context context,float dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                context.getResources().getDisplayMetrics());
    }
}