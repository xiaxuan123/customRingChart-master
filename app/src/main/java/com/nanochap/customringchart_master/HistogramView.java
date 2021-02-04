package com.nanochap.customringchart_master;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * @author xiaoyi
 * @description
 * 自定义柱状图
 * @date 2021/2/4
 */
public class HistogramView extends FrameLayout {
    protected int defaultBorderColor = Color.argb(255, 217, 217, 217);
    protected int titleTextColor = Color.argb(255, 217, 217, 217);
    protected int labelTextColor;
    protected int mTitleTextSize = 42;
    protected int mLabelTextSize = 20;
    protected String mTitle;
    private int mWidth;
    private int mHeight;
    private int mLeftTextSpace;
    private int mBottomTextSpace;
    private int mTopTextSpace;
    protected Paint mBorderLinePaint;
    private Double maxData=0.0;

    private List<Double> mDatas;

    /**
     * 备注文本画笔
     */
    private Paint mTextPaint;
    /**
     * 标题文本画笔
     */
    private Paint mTitleTextPaint;

    private BaseChart baseChartView;

    public HistogramView(@NonNull Context context) {
        super(context);
        init(context, null);
    }

    public HistogramView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public HistogramView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public HistogramView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = getMeasuredWidth();
        mHeight = getMeasuredHeight();
    }
    
    @SuppressLint("CustomViewStyleable")
    private void init(Context context, AttributeSet attrs) {
        mDatas = new ArrayList<>();
        
        TypedArray t = context.obtainStyledAttributes(attrs, R.styleable.barCharts);
        defaultBorderColor = t.getColor(R.styleable.barCharts_borderColor, defaultBorderColor);
        titleTextColor = t.getColor(R.styleable.barCharts_titleTextColor, Color.GRAY);
        mTitleTextSize = (int) t.getDimension(R.styleable.barCharts_titleTextSize, mTitleTextSize);
        mLabelTextSize = (int) t.getDimension(R.styleable.barCharts_labelTextSize, mLabelTextSize);
        labelTextColor = t.getColor(R.styleable.barCharts_labelTextColor, Color.GRAY);

        mLeftTextSpace = (int) t.getDimension(R.styleable.barCharts_leftTextSpace, 30);
        mBottomTextSpace = (int) t.getDimension(R.styleable.barCharts_bottomTextSpace, 20);
        mTopTextSpace = (int) t.getDimension(R.styleable.barCharts_topTextSpace, 70); //间距
        mTitle = t.getString(R.styleable.barCharts_title);
        t.recycle();
        //注释
        mBorderLinePaint = generatePaint();
        mBorderLinePaint.setColor(defaultBorderColor);
        mBorderLinePaint.setStrokeWidth(dp2px(context, 1));

        mTextPaint = generatePaint();
        mTextPaint.setColor(labelTextColor);
        mTextPaint.setTextSize(mLabelTextSize);

        mTitleTextPaint = generatePaint();
        mTitleTextPaint.setColor(titleTextColor);
        mTitleTextPaint.setTextSize(mTitleTextSize);

        baseChartView = new BaseChart(context, attrs);
        LayoutParams parames = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        parames.setMargins(mLeftTextSpace, mTopTextSpace, mLeftTextSpace, 0);
        baseChartView.setLayoutParams(parames);
        baseChartView.setBottomDrawPadding(mBottomTextSpace);
        baseChartView.setLeftDrawPadding(mLeftTextSpace);
        baseChartView.setTopDrawPadding(mTopTextSpace);
        addView(baseChartView);
    }

    private Paint generatePaint() {
        Paint m = new Paint();
        m.setAntiAlias(true);
        m.setStyle(Paint.Style.STROKE);
        return m;
    }

    private void setMaxData() {
        if (mDatas.size()>0){
            this.maxData = Collections.max(mDatas);
        }
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);

        if (mTitle != null) {
            canvas.drawText(mTitle, mWidth / 2 - mTitleTextPaint.measureText(mTitle) / 2,
                    mTopTextSpace - mTitleTextSize, mTitleTextPaint);
        }

        canvas.translate(mLeftTextSpace, mHeight - mBottomTextSpace);
    }
    
    public void setDatas(List<Double> mDatas, List<String> mDescribe,boolean isAnimation) {
        this.mDatas = mDatas;
        setMaxData();
        baseChartView.setDatas(mDatas, mDescribe,isAnimation);
    }
    
    //柱状图item点击事件
    public void setOnItemClick(BaseChart.setOnRangeBarItemClickListener clickListener) {
        baseChartView.setOnRangeBarItemClickListener(clickListener);
    }

    
    public void addEndMoreData(List<Double> mDatas, List<String> mDesciption) {
        baseChartView.addEndMoreData(mDatas, mDesciption);
    }

    
    public void addStartMoreData(List<Double> mDatas, List<String> mDesciption) {
        baseChartView.addStartMoreData(mDatas,mDesciption);
    }
    
    
    public static int dp2px(Context context, int dpValue) {
        return (int) context.getResources().getDisplayMetrics().density * dpValue;
    }
}
