package com.example.arcseekbar.weight.productinterface;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.example.arcseekbar.R;

public class GridView extends LinearLayout {
    public GridView(Context context) {
        this(context,null);
    }

    public GridView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setOrientation(LinearLayout.VERTICAL);
        setGravity(Gravity.CENTER);
        setBackgroundResource(R.drawable.gridview_bg);
    }
}
