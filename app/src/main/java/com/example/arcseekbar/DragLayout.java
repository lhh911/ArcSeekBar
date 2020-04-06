package com.example.arcseekbar;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

import androidx.core.view.MotionEventCompat;
import androidx.customview.widget.ViewDragHelper;

/**
 * ViewDragHelper的用法
 * 1. ViewDragHelper.create()创建实例
 * 2. 初始化callback
 * 3. 将当前ViewGroup的onInterceptTouchEvent和onTouchEvent事件委托给ViewDragHelper处理
 * 4. 处理拖拽view的边界(在callback中的clampViewPositionHorizontal和clampViewPositionVertical方法中处理)
 */
public class DragLayout extends LinearLayout {

    private ViewDragHelper mViewDragHelper;

    public DragLayout(Context context) {
        super(context);
        initViewDragHelper();
    }

    public DragLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initViewDragHelper();
    }

    public DragLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initViewDragHelper();
    }



    private void initViewDragHelper() {
        //ViewDragHelper.Callback是一个抽象类, 里面有更多的处理, 将需要处理的方法重写就可以了
        mViewDragHelper = ViewDragHelper.create(this, 1.0f, new ViewDragHelper.Callback() {
            @Override
            public boolean tryCaptureView(View child, int pointerId) {
                Log.e("abc", "pointerId=" + pointerId);

                //第3个view不能截取事件 (也就是不能拖拽)
                if (getChildCount() >= 3) {
                    if (child == getChildAt(2)) {
                        return false;
                    }
                }

                return true; //true表示child可以拖拽, false标示child不能拖拽
            }

            @Override
            public int clampViewPositionHorizontal(View child, int left, int dx) {
                final int leftBound = getPaddingLeft();
                final int rightBound = getWidth() - child.getWidth() - getPaddingRight();

//                int newLeft = left;
//                if(left < leftBound) newLeft = leftBound;
//                if(left > rightBound) newLeft = rightBound;

                //词句等价于上面的三句
                int newLefth = Math.min(Math.max(left, leftBound), rightBound);
                return newLefth;
            }

            @Override
            public int clampViewPositionVertical(View child, int top, int dy) {
                final int topBound = getPaddingTop();
                final int bottomBound = getHeight() - child.getHeight() - getPaddingBottom();

                int newTop = Math.min(bottomBound, Math.max(topBound, top));
                return newTop;
            }
        });
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        final int action = MotionEventCompat.getActionMasked(ev);
        if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP) {
            mViewDragHelper.cancel();
            return false;
        }

        return mViewDragHelper.shouldInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mViewDragHelper.processTouchEvent(event);
        return true;
    }
}

