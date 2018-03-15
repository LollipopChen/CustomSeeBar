package com.example.cqe.customseebar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.WindowManager;

/**
 * 进度条
 * Author: CQE
 * Date: 2018/2/28.
 */

public class HSeeBarLevelView extends View {

    private Drawable pointDrawable; // 积分图标
    private Paint mProgressPaint;   // 绘制进度条的画笔
    private Paint mCirclePaint;     // 绘制进度尾端圆的画笔
    private Paint mTextPaint;       // 绘制进度文字的画笔
    private Paint mTrianglePaint;  // 绘制三角形的画笔
    private Paint mCurrentRecFPaint; // 绘制三角形上面矩形的画笔
    private Paint mIconPaint;       // 绘制图标的画笔
    private Path mPath;    // 用于绘制三角形的箭头

    private Context mContext;

    //进度条的底色和完成进度的颜色
    private int mProgressBackColor;
    private int mProgressForeColor;

    //进度条上方现实的文字
    private String mProgressText;
    //进度文字的颜色和文字背景颜色
    private int mTextColor;
    private int mTextBackColor;
    //进度文字的字体大小
    private int mTextSize;

    private float currentProgress; // 进度条的当前值
    private float startProgress; // 进度条的起始值
    private float endProgress; //进度条的结束值

    //进度条的高度
    private int mProgressBarHeight;

    //view的上下内边距
    private int mPaddingTop;
    private int mPaddingBottom;

    //用于测量文字显示区域的宽度和高度
    private Paint.FontMetricsInt mTextFontMetrics;
    private Rect mTextBound;

    //进度条和进度文字显示框的间距
    private int mLine2TextDividerHeight;

    //三角形箭头的高度
    private int mTriangleHeight;

    //绘制进度条圆角矩形的圆角
    private int mRectCorn;

    //设置进度显示文字的所有边距
    private static final int TEXT_LEFT_RIGHT_PADDING = 15;
    //圆点的半径
    private int mRadius = 20;
    //图标与边框的边距
    private int mIconPaddingTop = 15;
    private int mIconPaddingBottom = 8;
    //图标的宽度
    private int mIconWidth = 20;
    //图标与文字的间距
    private int mIconTextPadding = 5;


    /**分段颜色*/
    private static final int[] SECTION_COLORS = {Color.parseColor("#FFFF33"),
            Color.parseColor("#FF9900"),
            Color.RED};


    public HSeeBarLevelView(Context context) {
        this(context,null);
    }

    public HSeeBarLevelView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public HSeeBarLevelView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;

        if (attrs != null) {
            TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.HSeeBarLevelView, defStyleAttr, 0);
            int n = typedArray.getIndexCount();
            for (int i = 0; i < n; i++) {
                int attr = typedArray.getIndex(i);
                switch (attr) {
                    case R.styleable.HSeeBarLevelView_backColor:
                        mProgressBackColor = typedArray.getColor(attr, Color.TRANSPARENT);
                        break;
                    case R.styleable.HSeeBarLevelView_foreColor:
                        mProgressForeColor = typedArray.getColor(attr, Color.RED);
                        break;
                    case R.styleable.HSeeBarLevelView_textColor:
                        mTextColor = typedArray.getColor(attr, Color.GRAY);
                        break;
                    case R.styleable.HSeeBarLevelView_textBackColor:
                        mTextBackColor = typedArray.getColor(attr, Color.GRAY);
                        break;
                    case R.styleable.HSeeBarLevelView_startProgress:
                        startProgress = typedArray.getFloat(attr, 0);
                        break;
                    case R.styleable.HSeeBarLevelView_endProgress:
                        endProgress = typedArray.getFloat(attr, 0);
                        break;
                    case R.styleable.HSeeBarLevelView_scurrProgress:
                        currentProgress = typedArray.getFloat(attr, 0);
                        mProgressText = String.valueOf((int) currentProgress);
                        break;
                    case R.styleable.HSeeBarLevelView_progressTextSize:
                        mTextSize = typedArray.getDimensionPixelSize(attr, 26);
                        break;
                    case R.styleable.HSeeBarLevelView_rectCorn:
                        mRectCorn = typedArray.getDimensionPixelSize(attr, 4);
                        break;
                    case R.styleable.HSeeBarLevelView_pointIcon:
                        pointDrawable = typedArray.getDrawable(R.styleable.HSeeBarLevelView_pointIcon);
                    break;
                }
            }
            typedArray.recycle();
        }

        init();
    }

    /**
     * 初始化
     */
    private void init() {
        mTriangleHeight = 15;
        mProgressBarHeight = 20;
        mLine2TextDividerHeight = 5;
        mRectCorn = mProgressBarHeight / 2;

        mTextBound = new Rect();

        mProgressPaint = new Paint();
        mProgressPaint.setStyle(Paint.Style.FILL);
        mProgressPaint.setStrokeWidth(mProgressBarHeight);
        mProgressPaint.setAntiAlias(true);

        mCirclePaint = new Paint();
        mCirclePaint.setStyle(Paint.Style.FILL);
        mCirclePaint.setAntiAlias(true);

        mCurrentRecFPaint = new Paint();
        mCurrentRecFPaint.setStyle(Paint.Style.FILL);
        mCurrentRecFPaint.setAntiAlias(true);

        mTrianglePaint = new Paint();
        mTrianglePaint.setStyle(Paint.Style.FILL);
        mTrianglePaint.setStrokeWidth(mProgressBarHeight);
        mTrianglePaint.setAntiAlias(true);

        mIconPaint = new Paint();
        mIconPaint.setAntiAlias(true);
//        Bitmap iconBitmap = drawableToBitmap(pointDrawable);
//        mIconPaint.setShader(new BitmapShader(iconBitmap, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP));

        mTextPaint = new Paint();
        mTextPaint.setColor(mTextColor);
        mTextPaint.setAntiAlias(true);
        reCalculateTextSize();

        mPaddingTop = getPaddingTop();
        mPaddingBottom = getPaddingBottom();

        mPath = new Path();
    }

    /**
     * 设置字体的大小
     */
    private void reCalculateTextSize() {
        mTextPaint.setTextSize(mTextSize);
        mTextFontMetrics = mTextPaint.getFontMetricsInt();
        mTextPaint.getTextBounds(mProgressText, 0, mProgressText.length(), mTextBound);
    }

    @Override
    protected void onDraw(Canvas canvas) {
//        super.onDraw(canvas);
        //绘制前清理上次绘制的痕迹
        mPath.reset();
        mProgressPaint.setColor(mProgressBackColor);

        //计算开始绘制进度条的y坐标
        int startLineLocationY = mPaddingTop - mTextFontMetrics.top + mTextFontMetrics.bottom + mTriangleHeight + mLine2TextDividerHeight + mProgressBarHeight / 2;

        /* 绘制进度条底部背景 */
        canvas.drawRoundRect(0, startLineLocationY, getScreenWidth(), startLineLocationY + mProgressBarHeight, mRectCorn, mRectCorn, mProgressPaint);

        /* 绘制已经完成了的进度条 */
        mProgressPaint.setColor(mProgressForeColor);
        double progress = (currentProgress - startProgress) / (endProgress - startProgress);
        //绘图的起点
        int currProgress = (int) (getScreenWidth() * progress) + mRadius;
        //==================渐变色==================
        LinearGradient linearGradient = new LinearGradient(0, 0, getMeasuredWidth(), 0,SECTION_COLORS, null, LinearGradient.TileMode.CLAMP);
        mProgressPaint.setShader(linearGradient);
        //==================渐变色==================
        canvas.drawRoundRect(0, startLineLocationY, currProgress, startLineLocationY + mProgressBarHeight, mRectCorn, mRectCorn, mProgressPaint);

        /* 绘制结束的圆点 */
        //设置圆点的颜色
        float percent = currentProgress / endProgress ;
        if ( percent > 1.0f/3.0f){
            mCirclePaint.setColor(SECTION_COLORS[1]);
        }else if ( percent > 2.0f/3.0f){
            mCirclePaint.setColor(SECTION_COLORS[2]);
        }else if (percent > 0){
            mCirclePaint.setColor(SECTION_COLORS[0]);
        }else {
            mCirclePaint.setColor(mProgressForeColor);
        }
        canvas.drawCircle(currProgress,startLineLocationY + (mProgressBarHeight / 2 ), mRadius,mCirclePaint );

        /* 绘制显示文字三角形框 */
        //计算文字显示区域的宽度和高度
        int textWidth = mTextBound.right - mTextBound.left;
        int textHeight = mTextFontMetrics.bottom - mTextFontMetrics.top;

        //计算三角形定点开始时的y坐标
        int startTriangleY = startLineLocationY - mProgressBarHeight / 2 - mLine2TextDividerHeight;
        //绘制三角形
        mPath.moveTo(currProgress, startTriangleY);
        mPath.lineTo(currProgress + 10, startTriangleY - mTriangleHeight);
        mPath.lineTo(currProgress + textWidth / 2 + TEXT_LEFT_RIGHT_PADDING, startTriangleY - mTriangleHeight);
        mPath.lineTo(currProgress + textWidth / 2 + TEXT_LEFT_RIGHT_PADDING, startTriangleY - mTriangleHeight);// - textHeight);
        mPath.lineTo(currProgress - textWidth / 2 - TEXT_LEFT_RIGHT_PADDING , startTriangleY - mTriangleHeight);// - textHeight);
        mPath.lineTo(currProgress - textWidth / 2 - TEXT_LEFT_RIGHT_PADDING, startTriangleY - mTriangleHeight);
        mPath.lineTo(currProgress - 10, startTriangleY - mTriangleHeight);
        mPath.close();
        mTrianglePaint.setColor(mTextBackColor);
        canvas.drawPath(mPath, mTrianglePaint);
//        /* 绘制三角形上面的圆角矩形 */
        mCurrentRecFPaint.setColor(ContextCompat.getColor(getContext(),R.color.colorBlackWhite));
//       居中的
//        canvas.drawRoundRect(currProgress + textWidth / 2 + TEXT_LEFT_RIGHT_PADDING,
//                startTriangleY - mTriangleHeight - textHeight ,
//                currProgress - textWidth / 2 - TEXT_LEFT_RIGHT_PADDING,
//                startTriangleY - mTriangleHeight,
//                mRectCorn,
//                mRectCorn,
//                mCurrentRecFPaint);
        //偏右的
        canvas.drawRoundRect(currProgress - TEXT_LEFT_RIGHT_PADDING - 4,
                startTriangleY - mTriangleHeight - textHeight,
                currProgress + textWidth + TEXT_LEFT_RIGHT_PADDING + 4 + mIconWidth,
                startTriangleY - mTriangleHeight,
                mRectCorn,
                mRectCorn,
                mCurrentRecFPaint);

        //绘制文字
//        canvas.drawText(mProgressText, (float) (currProgress - textWidth / 2), mPaddingTop - mTextFontMetrics.top, mTextPaint);
        canvas.drawText(mProgressText, currProgress + mIconWidth + mIconTextPadding, mPaddingTop - mTextFontMetrics.top, mTextPaint);

        //绘制图标
        pointDrawable.setBounds(currProgress-10,
                startTriangleY - mTriangleHeight - textHeight + mIconPaddingTop,
                currProgress + mIconWidth ,
                startTriangleY - mTriangleHeight - mIconPaddingBottom);
        pointDrawable.draw(canvas);

//        mIconPaint.setColor(Color.TRANSPARENT);
//        canvas.translate((float) (currProgress), mPaddingTop - mTextFontMetrics.top);
//        canvas.drawRect(0,0,20 ,20,mIconPaint);

    }

    /**
     * 设置进度条的值
     * @param start 开始值
     * @param end 结束值
     * @param current 当前值
     */
    public void resetLevelProgress(float start, float end, float current) {
        this.startProgress = start;
        this.endProgress = end;
        this.currentProgress = current;
        this.mProgressText = String.valueOf((int) currentProgress);
        reCalculateTextSize();
        invalidate();
    }

    /**
     * 获取宽度
     * @return 宽度
     */
    public int getScreenWidth() {
        WindowManager wm = (WindowManager) mContext
                .getSystemService(Context.WINDOW_SERVICE);
        int width = 0;
        if (wm != null) {
            width = wm.getDefaultDisplay().getWidth() * 2 / 3;
        }
        return width;
    }

    public int dip2px(float dpValue) {
        final float scale = mContext.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
