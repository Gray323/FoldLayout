package com.rxd.matrixlearn;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;

/**
 * Created by Administrator on 2018/3/7.
 */

public class TouchFoldLayout extends FoldLayout{

    private int mTranslation = -1;
    private GestureDetector mScrollGestureDetector;
    public TouchFoldLayout(Context context) {
        this(context, null);
    }

    public TouchFoldLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mScrollGestureDetector = new GestureDetector(context, new ScrollGestureDetector());
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return mScrollGestureDetector.onTouchEvent(event);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        if (mTranslation == -1){
            mTranslation = getWidth();
        }
        super.dispatchDraw(canvas);
    }

    class ScrollGestureDetector extends GestureDetector.SimpleOnGestureListener
    {
        @Override
        public boolean onDown(MotionEvent e)
        {
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2,
                                float distanceX, float distanceY)
        {
            mTranslation -= distanceX;

            if (mTranslation < 0)
            {
                mTranslation = 0;
            }
            if (mTranslation > getWidth())
            {
                mTranslation = getWidth();
            }

            float factor = Math.abs(((float) mTranslation)
                    / ((float) getWidth()));

            setFactor(factor);

            return true;
        }
    }

}
