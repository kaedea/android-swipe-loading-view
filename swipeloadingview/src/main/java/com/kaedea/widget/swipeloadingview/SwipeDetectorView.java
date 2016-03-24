package com.kaedea.widget.swipeloadingview;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import com.kaedea.widget.swipeloadingview.core.ISwipeDetector;
import com.kaedea.widget.swipeloadingview.core.ITouEventHandler;
import com.kaedea.widget.swipeloadingview.core.ISwipeHandler;
import com.kaedea.widget.swipeloadingview.core.SwipeConstants;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created kaede on 1/26/16.
 */
public class SwipeDetectorView extends View implements ISwipeDetector {
    public static final String TAG = "SwipeDetectorLayout";

    ISwipeHandler iSwipeHandler;
    ITouEventHandler iTouEventHandler;

    AtomicBoolean isCreated = new AtomicBoolean();
    private boolean mIsIntercept = false;

    public SwipeDetectorView(Context context) {
        super(context);
        init();
    }

    public SwipeDetectorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SwipeDetectorView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public SwipeDetectorView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        isCreated.set(false);
        setInterceptTouchEvent(true);
    }


    @Override
    public void attach(ITouEventHandler iTouEventHandler, ISwipeHandler iSwipeHandler){
        this.iSwipeHandler = iSwipeHandler;
        this.iTouEventHandler = iTouEventHandler;

    }



    public void setInterceptTouchEvent(boolean isInterceptTouchEvent) {
        // setClickable(isInterceptTouchEvent); // Pause or resume detect swipe event.
        mIsIntercept = isInterceptTouchEvent;
    }

    public boolean isInterceptTouchEvent() {
        // return isClickable();
        return mIsIntercept;
    }



    public int getTotalHeight() {
        return this.getMeasuredHeight();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // Hide the loading view in the very beginning.
        if (!isCreated.get()) {
            if (iSwipeHandler !=null){
                iSwipeHandler.hideLoadingView(false, SwipeConstants.SWIPE_UNKNOWN, null);
            }
           isCreated.set(true);
        }

    }
    int mDirection = SwipeConstants.SWIPE_UNKNOWN;


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (iTouEventHandler != null) {
            return iTouEventHandler.onTouchEvent(this, event) ? true : super.onTouchEvent(event);
        }
        return isInterceptTouchEvent() || super.onTouchEvent(event);
    }





}
