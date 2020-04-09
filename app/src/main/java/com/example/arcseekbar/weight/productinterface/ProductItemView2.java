package com.example.arcseekbar.weight.productinterface;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.arcseekbar.R;

import java.lang.ref.WeakReference;
import java.util.concurrent.atomic.AtomicLong;

public class ProductItemView2 extends RelativeLayout {
    private Context mContext;
    private TextView addCountTv;
    private ImageView productIv;
    private TextView levelTv;

    private int mWidth,mHeight;
    private Product product;
    private AnimHandler mHander;
    private AtomicLong atomicLong;

    private ScaleAnimation scaleAnimation;
    private AnimationSet animationSet;

    private IncreaseListeren increaseListeren;
    private boolean viewVisibility;

    public ProductItemView2(Context context) {
        this(context,null);
    }


    public ProductItemView2(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    public ProductItemView2(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setIncreaseListeren(IncreaseListeren increaseListeren) {
        this.increaseListeren = increaseListeren;
    }

    public int getLevel() {
        if (product != null)
            return product.getLevel();
        else
            return 0;
    }

    private void init(){
        mHander = new AnimHandler(mContext);
        atomicLong = new AtomicLong(System.currentTimeMillis());

        int screenWidth = getScreenWidth(mContext);
        //产品图片
        productIv = new ImageView(mContext);
        RelativeLayout.LayoutParams params2 = new LayoutParams(screenWidth/8, screenWidth/8);
        params2.addRule(RelativeLayout.CENTER_IN_PARENT);
        addView(productIv,params2);

        //添加产品等级
        levelTv = new TextView(mContext);
        levelTv.setTextSize(10);
        levelTv.setIncludeFontPadding(false);
        levelTv.setBackgroundResource(R.drawable.level_bg);
        RelativeLayout.LayoutParams params3 = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params3.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        params3.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        addView(levelTv,params3);

        //添加秒产量
        addCountTv = new TextView(mContext);
        addCountTv.setTextSize(12);
        addCountTv.setIncludeFontPadding(false);
        addCountTv.setVisibility(INVISIBLE);
        RelativeLayout.LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_HORIZONTAL);
        addView(addCountTv,params);
    }



    public void setProduct(Product product){
        this.product = product;
        addCountTv.setText(product.getProfit());
        levelTv.setText(product.getLevel()+"");
        productIv.setImageResource(product.getBitmapResId());
        initAnim();
    }

    public long getCurUpdateTime(){
        if(atomicLong != null)
            return atomicLong.get();
        return 0;
    }



    private void initAnim(){
        scaleAnimation = new ScaleAnimation(1.0f,1.2f,1f,1.2f,
                ScaleAnimation.RELATIVE_TO_SELF,0.5f,ScaleAnimation.RELATIVE_TO_SELF,0.5f);
        scaleAnimation.setDuration(200);
        scaleAnimation.setFillAfter(false);

        TranslateAnimation translateAnimation = new TranslateAnimation(TranslateAnimation.RELATIVE_TO_PARENT,0f,TranslateAnimation.RELATIVE_TO_PARENT,0f,
                TranslateAnimation.RELATIVE_TO_PARENT,0.5f,TranslateAnimation.RELATIVE_TO_PARENT,0f);

        AlphaAnimation alphaAnimation = new AlphaAnimation(0f,1f);

        animationSet  = new AnimationSet(true);
        animationSet.setDuration(600);
        animationSet.setFillAfter(false);
        animationSet.setInterpolator(new DecelerateInterpolator());
        animationSet.addAnimation(translateAnimation);
        animationSet.addAnimation(alphaAnimation);
        animationSet.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                addCountTv.setVisibility(VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                addCountTv.setVisibility(INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    public void startAnim(){
        atomicLong.set(System.currentTimeMillis());
        if(mHander != null && viewVisibility){
            mHander.sendEmptyMessage(1);
        }

        if (increaseListeren != null) {
            increaseListeren.increase(Integer.parseInt(product.getProfit()) * 5);
        }
    }

    public void executeAnim() {
        Log.d("info","executeAnim = " );
        if(scaleAnimation != null){
            productIv.startAnimation(scaleAnimation);
        }
        if(animationSet != null)
            addCountTv.startAnimation(animationSet);
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        if(visibility == 0){
            viewVisibility = true;
        }else {
            viewVisibility = false;
        }

        Log.d("info","visibility = "+ visibility );
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if(productIv != null){
            productIv.clearAnimation();
        }
        if(addCountTv != null)
            addCountTv.clearAnimation();
        if(mHander != null) {
            mHander.removeMessages(1);
            mHander = null;
        }
    }


    public class AnimHandler extends Handler{
        WeakReference<Context> weakReference ;

        public AnimHandler(Context mContext) {
            weakReference = new WeakReference<>(mContext);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            if(weakReference.get() != null){
                executeAnim();
            }
        }
    }


    // 获取屏幕的宽度
    public int getScreenWidth(Context context) {
        WindowManager manager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.widthPixels;
    }

}
