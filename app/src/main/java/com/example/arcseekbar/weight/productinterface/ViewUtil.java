package com.example.arcseekbar.weight.productinterface;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.view.View;

public class ViewUtil {

    /**
     * Returns true if a child view contains the specified point when transformed
     * into its coordinate space.
     */
    private boolean isTransformedTouchPointInView(float x, float y, View child,
                                                  PointF outLocalPoint) {
        // get x, y offset
//        float localX = x + getScrollX() - child.getLeft();
//        float localY = y + getScrollY() - child.getTop();
        float localX = x  - child.getLeft();
        float localY = y  - child.getTop();

        // restore location
        final float[] localXY = new float[2];
        localXY[0] = localX;
        localXY[1] = localY;
        final Matrix inverseMatrix = new Matrix();
        child.getMatrix().invert(inverseMatrix);
        inverseMatrix.mapPoints(localXY);
        localX = localXY[0];
        localY = localXY[1];

        // fill out data
        final boolean isInView = pointInView(child, localX, localY);
        if (isInView && outLocalPoint != null) {
            outLocalPoint.set(localX, localY);
        }
        return isInView;
    }

    /**
     * Determines whether the given point, in local coordinates is inside the view.
     */
    private static boolean pointInView(View view, float localX, float localY) {
        return localX >= 0 && localX < (view.getRight() - view.getLeft())
                && localY >= 0 && localY < (view.getBottom()- view.getTop());
    }



    public static Bitmap scaleBitmap(Bitmap origin, int newWidth, int newHeight) {
        if (origin == null) {
            return null;
        }
        int height = origin.getHeight();
        int width = origin.getWidth();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);// 使用后乘
        Bitmap newBM = Bitmap.createBitmap(origin, 0, 0, width, height, matrix, false);
        if (!origin.isRecycled()) {
            origin.recycle();
        }
        return newBM;
    }

    public static  int getTextHeight(String text, Paint paint) {
        Rect rect = new Rect();
        paint.getTextBounds(text, 0, text.length(), rect);
        return rect.height();
    }

}
