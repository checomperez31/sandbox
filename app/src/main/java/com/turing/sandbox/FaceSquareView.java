package com.turing.sandbox;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.SurfaceView;

/**
 * Created by usuario on 1/22/2018.
 */

public class FaceSquareView extends SurfaceView {

    public FaceSquareView(Context context) {
        super(context);
    }

    public FaceSquareView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public FaceSquareView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public FaceSquareView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    /**
     * Sets the aspect ratio for this view. The size of the view will be measured based on the ratio
     * calculated from the parameters. Note that the actual sizes of parameters don't matter, that
     * is, calling setAspectRatio(2, 3) and setAspectRatio(4, 6) make the same result.
     *
     * @param width  Relative horizontal size
     * @param height Relative vertical size
     */
    public void setAspectRatio(int width, int height) {
        if (width < 0 || height < 0) {
            throw new IllegalArgumentException("Size cannot be negative.");
        }
        requestLayout();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        if (width > height) {//landscap
            setMeasuredDimension(height, (height*4)/3);
        } else {
            setMeasuredDimension(width, (width * 4)/3);
        }
    }
}
