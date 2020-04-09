package com.example.arcseekbar.weight;


import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.Toast;

import com.example.arcseekbar.weight.productinterface.DogProduct;
import com.example.arcseekbar.weight.productinterface.DogProductFactory;
import com.example.arcseekbar.weight.productinterface.GridView;
import com.example.arcseekbar.weight.productinterface.IncreaseListeren;
import com.example.arcseekbar.weight.productinterface.ProductItemView2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 另外一种方式实现动画可拖动item的GridView
 *
 * @author way
 */
public class DragGridView2 extends ViewGroup implements View.OnTouchListener,
        View.OnClickListener, View.OnLongClickListener, IncreaseListeren {
    // layout vars
    public static float childRatio = .9f;
    private int colCount = 4, row = 3;
    protected int childSize, padding, dpi, scroll = 0;
    protected float lastDelta = 0;
    // dragging vars
    protected int dragged = -1, lastX = -1, lastY = -1, lastTarget = -1;
    protected boolean enabled = true, touching = false;
    // anim vars
    public static int animT = 150;
    protected ArrayList<Integer> newPositions = new ArrayList<Integer>();
    // listeners

    protected OnClickListener secondaryOnClickListener;
    private AdapterView.OnItemClickListener onItemClickListener;

    private Context mContext;
    private int gridSize = 12;//网格数量
    private List<GridView> gridList = new ArrayList<>();//网格view
    private Map<Integer, View> productMap = new HashMap<>();//产品view

    ItemAnimTaskRunnable itemTask;//轮询检查线程，符合时间的productitem执行动画
    boolean taskEnable = true;
    private Thread taskThread;

    @Override
    public void increase(int increase) {
        //每秒首页回调
        Log.d("info", "每秒收益 = " + increase);
    }


    // CONSTRUCTOR AND HELPERS
    public DragGridView2(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        setListeners();

        setChildrenDrawingOrderEnabled(true);

        DisplayMetrics metrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay()
                .getMetrics(metrics);
        dpi = metrics.densityDpi;

    }


    protected void setListeners() {
        setOnTouchListener(this);
        super.setOnClickListener(this);
        setOnLongClickListener(this);
    }

    @Override
    public void setOnClickListener(OnClickListener l) {
        secondaryOnClickListener = l;
    }


    public void addProductView(View child) {
        if (isFull()) {
            Toast.makeText(mContext, "最多12个", Toast.LENGTH_SHORT).show();
            return;
        }

        if (child instanceof ProductItemView2) {
            ((ProductItemView2) child).setIncreaseListeren(this);
        }

        for (Map.Entry<Integer, View> entry : productMap.entrySet()) {
            Integer key = entry.getKey();
            View value = entry.getValue();
            if (value == null) {
                productMap.put(key, child);
                break;
            }
        }

        LayoutParams params = new LayoutParams(childSize, childSize);
        child.setLayoutParams(params);
        addView(child);
    }

    private boolean isFull() {
        for (Map.Entry<Integer, View> entry : productMap.entrySet()) {
            View value = entry.getValue();
            if (value == null)
                return false;
        }
        return true;
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            measureChild(getChildAt(i),widthMeasureSpec,heightMeasureSpec);
        }

        setMeasuredDimension(getDefaultSize(MeasureSpec.getSize(widthMeasureSpec), widthMeasureSpec),
                getDefaultSize(MeasureSpec.getSize(heightMeasureSpec), heightMeasureSpec));
    }

    // LAYOUT
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        // determine childSize and padding, in px
        childSize = (r - l) / colCount;
        childSize = Math.round(childSize * childRatio);
        padding = ((r - l) - (childSize * colCount)) / (colCount + 1);


        //布局网格，固定的4行3列
        for (int i = 0; i < gridSize; i++) {
            if (gridList.size() < 12)
                break;

            Point xy = getCoorFromIndex(i);
            gridList.get(i).layout(xy.x, xy.y, xy.x + childSize,
                    xy.y + childSize);

        }

        //布局产品，有可能没有,和网格位置对应
        for (Map.Entry<Integer, View> entry : productMap.entrySet()) {
            Integer key = entry.getKey();
            View value = entry.getValue();
            if (value != null) {
                Point xy = getCoorFromIndex(key);
                value.layout(xy.x, xy.y, xy.x + childSize,
                        xy.y + childSize);
            }
        }
    }

    //初始化网格
    public void initGrid() {
//        LayoutParams params = new LayoutParams(childSize,childSize);
        for (int i = 0; i < gridSize; i++) {
            GridView gridView = new GridView(mContext);
            gridList.add(gridView);
            this.addView(gridView);

            //
            productMap.put(i, null);
        }
    }


    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        initGrid();

        startTask();
    }

    @Override
    protected int getChildDrawingOrder(int childCount, int i) {
        if (dragged == -1)
            return i;
        else if (i == childCount - 1)
            return dragged;
        else if (i >= dragged)
            return i + 1;
        return i;
    }

    public int getIndexFromCoor(int x, int y) {
        int col = getColOrRowFromCoor(x), row = getColOrRowFromCoor(y + scroll);
        if (col == -1 || row == -1) // touch is between columns or rows
            return -1;
        int index = row * colCount + col;
        if (index >= gridSize)
            return -1;
        return index;
    }

    protected int getColOrRowFromCoor(int coor) {
        coor -= padding;
        for (int i = 0; coor > 0; i++) {
            if (coor < childSize)
                return i;
            coor -= (childSize + padding);
        }
        return -1;
    }

    protected int getTargetFromCoor(int x, int y) {
        if (getColOrRowFromCoor(y + scroll) == -1) // touch is between rows
            return -1;
        // if (getIndexFromCoor(x, y) != -1) //touch on top of another visual
        // return -1;

        int leftPos = getIndexFromCoor(x - (childSize / 4), y);
        int rightPos = getIndexFromCoor(x + (childSize / 4), y);
        if (leftPos == -1 && rightPos == -1) // touch is in the middle of
            // nowhere
            return -1;
        if (leftPos == rightPos) // touch is in the middle of a visual
            return -1;

        int target = -1;
        if (rightPos > -1)
            target = rightPos;
        else if (leftPos > -1)
            target = leftPos + 1;
        if (dragged < target)
            return target - 1;

        // Toast.makeText(getContext(), "Target: " + target + ".",
        // Toast.LENGTH_SHORT).show();
        return target;
    }

    protected Point getCoorFromIndex(int index) {
        int col = index % colCount;
        int row = index / colCount;
        return new Point(padding + (childSize + padding) * col, padding
                + (childSize + padding) * row - scroll);
    }

    public int getIndexOf(View child) {
        for (int i = 0; i < getChildCount(); i++)
            if (getChildAt(i) == child)
                return i;
        return -1;
    }

    // EVENT HANDLERS
    public void onClick(View view) {
        if (enabled) {
            if (secondaryOnClickListener != null)
                secondaryOnClickListener.onClick(view);

            int lastIndex = getLastIndex();
            if (onItemClickListener != null && lastIndex != -1) {
                View product = productMap.get(lastIndex);
                productMap.put(lastIndex ,null);
                removeView(product);

//                requestAddView();

                onItemClickListener.onItemClick(null,
                        getChildAt(getLastIndex()), getLastIndex(),
                        getLastIndex() / colCount);
            }
        }
    }

    public boolean onLongClick(View view) {
        if (!enabled)
            return false;
        int index = getLastIndex();
        if (index != -1) {
            boolean empty = isEmpty(index);
            if (!empty) {//位置上有产品，可拖动
                dragged = index;

            }
//            animateDragged();
            return true;
        }
        return false;
    }

    //该位置上是否有产品
    private boolean isEmpty(int index) {
        return productMap.get(index) == null;
    }

    public boolean onTouch(View view, MotionEvent event) {
        int action = event.getAction();
        switch (action & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                enabled = true;
                lastX = (int) event.getX();
                lastY = (int) event.getY();
                int index = getLastIndex();
                if (index != -1) {
                    boolean empty = isEmpty(index);
                    if (!empty) {//位置上有产品，可拖动
                        dragged = index;
                    }
                }

                touching = true;
                break;
            case MotionEvent.ACTION_MOVE:
                int delta = lastY - (int) event.getY();
                if (dragged != -1) {
                    // change draw location of dragged visual
                    int x = (int) event.getX(), y = (int) event.getY();
                    int l = x - (3 * childSize / 4), t = y - (3 * childSize / 4);

                    productMap.get(dragged).layout(l, t, l + childSize,
                            t + childSize);


                }
                lastX = (int) event.getX();
                lastY = (int) event.getY();
                lastDelta = delta;
                break;
            case MotionEvent.ACTION_UP:
                if (dragged != -1) {
                    int x = (int) event.getX(), y = (int) event.getY();
                    lastTarget = getIndexFromCoor(x, y);
                    View v = productMap.get(dragged);
                    if (lastTarget != -1 && dragged != lastTarget) {//，在格子内，移动后的位置不一样，位置互换
                        replease();
                    } else {
                        Point xy = getCoorFromIndex(dragged);
                        v.layout(xy.x, xy.y, xy.x + childSize, xy.y + childSize);
                    }

                    lastTarget = -1;
                    dragged = -1;
                }
                touching = false;
                break;
        }
        if (dragged != -1)
            return true;
        return false;
    }

    //互换位置,如果等级相同，则升一级
    private void replease() {
//        ProductView view1 = (ProductView) productMap.get(dragged);
//        ProductView view2 = (ProductView) productMap.get(lastTarget);

        ProductItemView2 view1 = (ProductItemView2) productMap.get(dragged);
        ProductItemView2 view2 = (ProductItemView2) productMap.get(lastTarget);

        if (view2 != null) {
            final int level1 = view1.getLevel();
            int level2 = view2.getLevel();
            if (level1 == level2) {//相同等级
                productMap.put(dragged, null);
                productMap.put(lastTarget, null);

                removeView(view1);
                removeView(view2);
                View product = createProduct(level1 + 1);
                productMap.put(lastTarget, product);
                LayoutParams params = new LayoutParams(childSize, childSize);
                product.setLayoutParams(params);
                addView(product);
//                addProductView(product);

            } else {
                productMap.put(dragged, view2);
                productMap.put(lastTarget, view1);
                requestLayout();
            }
        }else{
            productMap.put(dragged, view2);
            productMap.put(lastTarget, view1);
            requestLayout();
        }

    }

    private void requestAddView(){
        removeAllViews();
        for (View child : gridList) {
            addView(child);
        }
        for (Map.Entry<Integer, View> entry : productMap.entrySet()) {
            View value = entry.getValue();
            if (value != null) {
                addView(value);

            }
        }
        requestLayout();
    }



    // EVENT HELPERS
    protected void animateDragged() {
        View v = getChildAt(dragged);
        int x = getCoorFromIndex(dragged).x + childSize / 2, y = getCoorFromIndex(dragged).y
                + childSize / 2;
        int l = x - (3 * childSize / 4), t = y - (3 * childSize / 4);
        v.layout(l, t, l + (childSize * 3 / 2), t + (childSize * 3 / 2));
        AnimationSet animSet = new AnimationSet(true);
        ScaleAnimation scale = new ScaleAnimation(.667f, 1, .667f, 1,
                childSize * 3 / 4, childSize * 3 / 4);
        scale.setDuration(animT);
        AlphaAnimation alpha = new AlphaAnimation(1, .5f);
        alpha.setDuration(animT);

        animSet.addAnimation(scale);
        animSet.addAnimation(alpha);
        animSet.setFillEnabled(true);
        animSet.setFillAfter(true);

        v.clearAnimation();
        v.startAnimation(animSet);
    }

    protected void animateGap(int target) {
        for (int i = 0; i < getChildCount(); i++) {
            View v = getChildAt(i);
            if (i == dragged)
                continue;
            int newPos = i;
            if (dragged < target && i >= dragged + 1 && i <= target)
                newPos--;
            else if (target < dragged && i >= target && i < dragged)
                newPos++;

            // animate
            int oldPos = i;
            if (newPositions.get(i) != -1)
                oldPos = newPositions.get(i);
            if (oldPos == newPos)
                continue;

            Point oldXY = getCoorFromIndex(oldPos);
            Point newXY = getCoorFromIndex(newPos);
            Point oldOffset = new Point(oldXY.x - v.getLeft(), oldXY.y
                    - v.getTop());
            Point newOffset = new Point(newXY.x - v.getLeft(), newXY.y
                    - v.getTop());

            TranslateAnimation translate = new TranslateAnimation(
                    Animation.ABSOLUTE, oldOffset.x, Animation.ABSOLUTE,
                    newOffset.x, Animation.ABSOLUTE, oldOffset.y,
                    Animation.ABSOLUTE, newOffset.y);
            translate.setDuration(animT);
            translate.setFillEnabled(true);
            translate.setFillAfter(true);
            v.clearAnimation();
            v.startAnimation(translate);

            newPositions.set(i, newPos);
        }
    }

    protected void reorderChildren() {
        // FIGURE OUT HOW TO REORDER CHILDREN WITHOUT REMOVING THEM ALL AND
        // RECONSTRUCTING THE LIST!!!
//        if (onRearrangeListener != null)
//            onRearrangeListener.onRearrange(dragged, lastTarget);
        ArrayList<View> children = new ArrayList<View>();
        for (int i = 0; i < getChildCount(); i++) {
            getChildAt(i).clearAnimation();
            children.add(getChildAt(i));
        }
        removeAllViews();
        while (dragged != lastTarget)
            if (lastTarget == children.size()) // dragged and dropped to the
            // right of the last element
            {
                children.add(children.remove(dragged));
                dragged = lastTarget;
            } else if (dragged < lastTarget) // shift to the right
            {
                Collections.swap(children, dragged, dragged + 1);
                dragged++;
            } else if (dragged > lastTarget) // shift to the left
            {
                Collections.swap(children, dragged, dragged - 1);
                dragged--;
            }
        for (int i = 0; i < children.size(); i++) {
//            newPositions.set(i, -1);
            addView(children.get(i));
        }
        requestLayout();
//        onLayout(true, getLeft(), getTop(), getRight(), getBottom());
    }

    public void scrollToTop() {
        scroll = 0;
    }

    public void scrollToBottom() {
        scroll = Integer.MAX_VALUE;
        clampScroll();
    }

    protected void clampScroll() {
        int stretch = 3, overreach = getHeight() / 2;
        int max = getMaxScroll();
        max = Math.max(max, 0);

        if (scroll < -overreach) {
            scroll = -overreach;
            lastDelta = 0;
        } else if (scroll > max + overreach) {
            scroll = max + overreach;
            lastDelta = 0;
        } else if (scroll < 0) {
            if (scroll >= -stretch)
                scroll = 0;
            else if (!touching)
                scroll -= scroll / stretch;
        } else if (scroll > max) {
            if (scroll <= max + stretch)
                scroll = max;
            else if (!touching)
                scroll += (max - scroll) / stretch;
        }
    }

    protected int getMaxScroll() {
        int rowCount = (int) Math.ceil((double) getChildCount() / colCount), max = rowCount
                * childSize + (rowCount + 1) * padding - getHeight();
        return max;
    }

    public int getLastIndex() {
        return getIndexFromCoor(lastX, lastY);
    }



    public void setOnItemClickListener(AdapterView.OnItemClickListener l) {
        this.onItemClickListener = l;
    }


    public View createProduct(int level) {
//        ProductView productView = new ProductView(mContext);
        ProductItemView2 productView = new ProductItemView2(mContext);
        DogProductFactory factory = new DogProductFactory();
        DogProduct product = factory.create(level);
        productView.setProduct(product);
        return productView;
    }


//    @Override
//    protected void onWindowVisibilityChanged(int visibility) {
//        super.onWindowVisibilityChanged(visibility);
//        if(visibility == 0){
//            taskEnable = true;
//            startTask();
//        }else {
//            taskEnable = false;
//        }
//
//        Log.d("info","visibility = "+ visibility );
//    }


    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        taskEnable = false;

    }

    public void startTask(){

        if(itemTask == null){
            itemTask = new ItemAnimTaskRunnable();
        }

        new Thread(itemTask).start();
    }

    public class ItemAnimTaskRunnable implements Runnable {
        @Override
        public void run() {
            while (taskEnable){
                long curTime = System.currentTimeMillis();
                for (Map.Entry<Integer, View> entry : productMap.entrySet()) {
                    View value = entry.getValue();
                    if (value != null && value instanceof ProductItemView2) {
                        ProductItemView2 productItemView2 = (ProductItemView2) value;
                        long curUpdateTime = productItemView2.getCurUpdateTime();
                        if(curTime - curUpdateTime >= 5000){
                            productItemView2.startAnim();
                        }
                    }
                }

                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                Log.d("info","ItemAnimTaskRunnable is running == threadName = " +Thread.currentThread() );
            }
        }
    }
}
