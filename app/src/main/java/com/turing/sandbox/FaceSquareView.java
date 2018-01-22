package com.turing.sandbox;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

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
}
