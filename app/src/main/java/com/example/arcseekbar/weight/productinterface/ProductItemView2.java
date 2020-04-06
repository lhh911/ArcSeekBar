package com.example.arcseekbar.weight.productinterface;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.arcseekbar.R;

public class ProductItemView2 extends RelativeLayout {
    private Context mContext;
    private TextView addCountTv;
    private ImageView productIv;
    private TextView levelTv;

    private int mWidth,mHeight;
    private Product product;
    private Handler mHander;
    private AnimRunning mRunning;

    private ScaleAnimation scaleAnimation;

    private TranslateAnimation translateAnimation;
    private AlphaAnimation alphaAnimation;
    private AnimationSet animationSet;


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

    private void init(){

        //添加秒产量
        addCountTv = new TextView(mContext);
        addCountTv.setTextSize(12);
        addCountTv.setVisibility(INVISIBLE);
        RelativeLayout.LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_HORIZONTAL);
        addView(addCountTv,params);

        //产品图片
        productIv = new ImageView(mContext);
        LayoutParams params2 = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params2.addRule(RelativeLayout.CENTER_IN_PARENT);
        addView(productIv,params2);

        //添加产品等级
        levelTv = new TextView(mContext);
        levelTv.setTextSize(12);
        levelTv.setBackgroundResource(R.drawable.level_bg);
        LayoutParams params3 = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params3.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        params3.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        addView(levelTv,params3);
    }




    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        mWidth = getMeasuredWidth();
        mHeight = getMeasuredHeight();
    }


    public void setProduct(Product product){
        this.product = product;
        addCountTv.setText(product.getProfit());
        levelTv.setText(product.getLevel()+"");
        productIv.setImageResource(product.getBitmapResId());

    }

    public void startAnim() {
        if(mHander == null)
            mHander = new Handler();

        if(mRunning == null)
            mRunning = new AnimRunning();

        initAnim();
        mHander.postDelayed(mRunning,4000);
    }

    public void clearAnim(){
        if(mHander != null) {
            mHander.removeCallbacks(mRunning);
            mHander = null;
        }
        if(scaleAnimation != null){
            scaleAnimation.cancel();
        }
        if(animationSet != null)
            animationSet.cancel();
    }


    private void executeAnim() {

        if(scaleAnimation != null){
            productIv.startAnimation(scaleAnimation);
        }
        if(animationSet != null)
            addCountTv.startAnimation(animationSet);
    }



    private void initAnim(){
        scaleAnimation = new ScaleAnimation(1.0f,1.5f,1f,1.5f,
                ScaleAnimation.RELATIVE_TO_SELF,0.5f,ScaleAnimation.RELATIVE_TO_SELF,0.5f);
        scaleAnimation.setDuration(200);
        scaleAnimation.setFillAfter(false);

        translateAnimation = new TranslateAnimation(TranslateAnimation.RELATIVE_TO_PARENT,0f,TranslateAnimation.RELATIVE_TO_PARENT,0f,
                TranslateAnimation.RELATIVE_TO_PARENT,0.5f,TranslateAnimation.RELATIVE_TO_PARENT,0f);

        alphaAnimation = new AlphaAnimation(0f,1f);

        animationSet  = new AnimationSet(true);
        animationSet.setDuration(700);
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


    public class AnimRunning implements Runnable{
        @Override
        public void run() {

            executeAnim();
            mHander.postDelayed(mRunning,4000);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if(mHander != null)
            mHander.removeCallbacks(mRunning);

        Log.w("info","onDetachedFromWindow");
    }
}
