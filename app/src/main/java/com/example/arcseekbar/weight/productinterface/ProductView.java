package com.example.arcseekbar.weight.productinterface;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class ProductView extends View {
    private Context mContext;
    private Product product;
    private int mWidth, mHeight;
    private RectF levelRect;//, dstRect;
    private Matrix matrix;//图片缩放比例
    private Paint mPaint;

    private String profit;
    private Bitmap bitmap;
    private String level;
    private int textWidth;
    private int textHeight;
    private int levelWidth, levelHeight;
    private int margin = 20;
    private int textY, baseDistance;//

    private int refrushCount;//刷新次数
    private float scaleX = 1f;
    private boolean scaleUp = true;
    private int bitmapWidth;

    private int delayMillis = 4000;
    private Handler mHander;
    private TimerRunnable timerRunnable;//定时任务
    private ThreadRunnable threadRunnable;//动画操作线程

    private boolean isRunning = true;

    private IncreaseListeren increaseListeren;

    public ProductView(Context context) {
        this(context, null);
    }

    public ProductView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    public ProductView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setIncreaseListeren(IncreaseListeren increaseListeren) {
        this.increaseListeren = increaseListeren;
    }

    public void setProduct(Product product) {
        this.product = product;

        level = String.valueOf(product.getLevel());
        profit = product.getProfit();

        mPaint = new Paint();
        mPaint.setColor(Color.BLACK);
        mPaint.setTextSize(42);
//        mPaint.setAntiAlias(true); //消除锯齿
//        mPaint.setStyle(Paint.Style.STROKE);

        matrix = new Matrix();
        textWidth = (int) mPaint.measureText(profit);//测量文字宽度
        textHeight = ViewUtil.getTextHeight(profit, mPaint);

        levelWidth = (int) mPaint.measureText(level);
        levelHeight = ViewUtil.getTextHeight(level, mPaint);


    }


    public int getLevel() {
        if (product != null)
            return product.getLevel();
        else
            return 0;
    }


    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
//        Log.d("visibility","onWindowFocusChanged ");
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        Log.d("visibility","onWindowVisibilityChanged = " + visibility);

        if(visibility == 0){
            startTimer();
        }
    }



    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        //回调比较慢，重新addview后才调用
        Log.d("visibility","onDetachedFromWindow ");
        stopTimer();

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        setMeasuredDimension(getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec),
                getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec));

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        mWidth = w;
        mHeight = h;

        baseDistance = textY = mHeight / 2 - textHeight;

        bitmapWidth = mWidth / 2;//图片宽，高

        if (product != null) {
            bitmap = BitmapFactory.decodeResource(mContext.getResources(), product.getBitmapResId());
            bitmap = ViewUtil.scaleBitmap(bitmap, bitmapWidth, bitmapWidth);
        }
        int left = mWidth - margin * 2;
        int top = mHeight - margin * 2;

        levelRect = new RectF(left, top, left + levelWidth * 2, top + levelHeight * 2);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //每秒增加 text
        if (product != null) {

            if (scaleX == 1f) {
                canvas.drawBitmap(bitmap, (mWidth - bitmapWidth) / 2, (mWidth - bitmapWidth) / 2, null);
            } else {
                canvas.save();
                //图片
                canvas.translate((mWidth - bitmapWidth) / 2, (mWidth - bitmapWidth) / 2);
                matrix.setScale(scaleX, scaleX, bitmap.getWidth() / 2, bitmap.getHeight() / 2);
//            matrix.setTranslate((mWidth-bitmapWidth)/2 , (mWidth-bitmapWidth)/2);
                canvas.drawBitmap(bitmap, matrix, null);

                canvas.restore();
            }

            if (textY < baseDistance && textY > 1) {

                canvas.drawText(profit, mWidth / 2 - textWidth / 2, textY + textHeight, mPaint);
            }

            //levle
            mPaint.setColor(Color.RED);
//            mPaint.setTextSize(dp2px(10));
            canvas.drawText(level,mWidth- margin ,mHeight - margin,mPaint);
////            mPaint.setStrokeWidth(3);
//            canvas.drawArc(levelRect,0,360,false, mPaint);
        }
    }


    public class ThreadRunnable implements Runnable {
        @Override
        public void run() {
            while (refrushCount < 10 && isRunning) {
                refrushCount += 1;
                updateData();
                postInvalidate();//重新绘制,会调用onDraw

                try {
                    Thread.sleep(30);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void updateData() {
        if (refrushCount <= 5)
            textY -= (baseDistance / 10) * 2 - 1;
        else {
            textY -= 1;
        }

        if (scaleX < 1.40f) {
            scaleX += 0.05f;
        } else {
            scaleX = 1.0f;
        }


        Log.w("info", "scaleUp = " + scaleUp + ", scaleX = " + scaleX);
        Log.w("info", "textY = " + textY + " refrushCount = " + refrushCount);
    }


    public class TimerRunnable implements Runnable {
        @Override
        public void run() {
            refrushCount = 0;
            textY = baseDistance;
            scaleX = 1.0f;
            new Thread(threadRunnable).start();

            mHander.postDelayed(timerRunnable, delayMillis);
            if (increaseListeren != null) {
                increaseListeren.increase(Integer.parseInt(profit) * delayMillis / 1000);
            }
        }
    }


    public void startTimer(){
        isRunning = true;
        if (mHander == null)
            mHander = new Handler();

        if (timerRunnable == null)
            timerRunnable = new TimerRunnable();

        if(threadRunnable == null)
            threadRunnable = new ThreadRunnable();

        mHander.removeCallbacks(timerRunnable);
        mHander.postDelayed(timerRunnable, delayMillis);
    }

    public void stopTimer(){
        isRunning = false;
        if (mHander != null && timerRunnable != null) {
            mHander.removeCallbacks(timerRunnable);
            mHander = null;
            timerRunnable = null;
            threadRunnable = null;
        }
    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getContext().getResources().getDisplayMetrics());
    }
}
