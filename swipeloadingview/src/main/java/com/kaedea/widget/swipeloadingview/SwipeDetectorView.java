package com.kaedea.widget.swipeloadingview;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.kaedea.widget.swipeloadingview.animation.Animator;
import com.kaedea.widget.swipeloadingview.animation.ObjectAnimator;
import com.kaedea.widget.swipeloadingview.animation.proxy.ViewHelper;
import com.kaedea.widget.swipeloadingview.util.LogUtil;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created kaede on 1/26/16.
 */
public class SwipeDetectorView extends View {
    public static final String TAG = "SwipeDetectorLayout";
    public static final int INT_INVALID = -10086;

    ITouchEventProxy iTouchEventProxy;
    OnSwipeListener mOnSwipeListener;

    AtomicBoolean isCreated = new AtomicBoolean();
    View mLoadingView;
    float mSwipeRatio;
    private float mSwipeRatioThreshold;
    private int mDuration = SwipeConstants.DEFAULT_DURATION;
    private int mWorkingMode = SwipeConstants.DEFAULT_WORKING_MODE;
    private boolean mIsIntercept = false;
    private boolean mIsEnable = true;

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
        mSwipeRatioThreshold = 0.2f;
        iTouchEventProxy = new ITouchEventProxy() {
            int thresholdMin = 20;
            int thresholdMax = 150;

            @Override
            public int getThresholdMin() {
                LogUtil.i(TAG, "[getThreshold] thresholdMin =" + thresholdMin);
                return thresholdMin;
            }

            @Override
            public int getThresholdMax() {
                LogUtil.i(TAG, "[getThreshold] thresholdMax =" + thresholdMax);
                return thresholdMax;
            }

            @Override
            public void onPreTouch(int direction) {
                // start swipe
                LogUtil.i(TAG, "[onPreTouch] start swipe, direction = " + direction);
                if (direction == SwipeConstants.SWIPE_TO_UP) {
                    resetLoadingViewPosition(SwipeConstants.POSITION_BOTTOM);
                } else {
                    resetLoadingViewPosition(SwipeConstants.POSITION_ABOVE);
                }
                if (mOnSwipeListener != null)
                    mOnSwipeListener.onSwipeStart(direction);
            }

            @Override
            public void onTouchOffset(float offsetY, int direction) {
                float targetTranslationY = ViewHelper.getTranslationY(mLoadingView) + offsetY;
                if (direction == SwipeConstants.SWIPE_TO_UP) {
                    mSwipeRatio = 1f - ViewHelper.getTranslationY(mLoadingView) / getTotalHeight();
                    LogUtil.d(TAG, "[onTouchOffset] direction =" + direction);
                    LogUtil.d(TAG, "[onTouchOffset] mSwipeRatio =" + mSwipeRatio);
                    LogUtil.d(TAG, "[onTouchOffset] offsetY =" + offsetY + " mLoadingView.getTranslationY() = " + ViewHelper.getTranslationY(mLoadingView) + " targetTranslationY=" + targetTranslationY);
                    if (offsetY < 0f && targetTranslationY < 0f) {
                        // can not continue swipe up
                        LogUtil.i(TAG, "[onTouchOffset] can not continue swipe up!");
                        resetLoadingViewPosition(SwipeConstants.POSITION_CENTER);
                        return;
                    }
                } else {
                    // swipe up to down
                    mSwipeRatio = ViewHelper.getTranslationY(mLoadingView) / getTotalHeight() + 1;
                    LogUtil.d(TAG, "[onTouchOffset] direction =" + direction);
                    LogUtil.d(TAG, "[onTouchOffset] mSwipeRatio =" + mSwipeRatio);
                    LogUtil.d(TAG, "[onTouchOffset] offsetY =" + offsetY + " mLoadingView.getTranslationY() = " + ViewHelper.getTranslationY(mLoadingView) + " targetTranslationY=" + targetTranslationY);
                    if (offsetY > 0f && targetTranslationY > getTotalHeight()) {
                        // can not continue swipe down
                        LogUtil.i(TAG, "[onTouchOffset] can not continue swipe down!");
                        resetLoadingViewPosition(SwipeConstants.POSITION_CENTER);
                        return;
                    }
                }
                translateLoadingView(targetTranslationY);
                // Notify swipe event's progress.
                LogUtil.d(TAG, "[onTouchOffset] Notify swipe event's progress.");
                if (mOnSwipeListener != null)
                    mOnSwipeListener.onSwiping(mSwipeRatio, mDirection);
            }

            @Override
            public void onPostTouch(int direction) {
                if (ViewHelper.getTranslationY(mLoadingView) > getTotalHeight()) {
                    // below the bottom end
                    LogUtil.i(TAG, "[onPostTouch] below the bottom end");
                    resetLoadingViewPosition(SwipeConstants.POSITION_BOTTOM);
                    if (mOnSwipeListener != null)
                        mOnSwipeListener.onSwipeCancel(mDirection);
                } else if (ViewHelper.getTranslationY(mLoadingView) < -getTotalHeight()) {
                    // above the top end
                    LogUtil.i(TAG, "[onPostTouch] above the top end");
                    resetLoadingViewPosition(SwipeConstants.POSITION_ABOVE);
                    if (mOnSwipeListener != null)
                        mOnSwipeListener.onSwipeCancel(mDirection);
                } else {
                    if (mSwipeRatio <= mSwipeRatioThreshold) {
                        // Can not reach the "Swipe Threshold", therefore taking it as Cancel;
                        if (mOnSwipeListener != null)
                            mOnSwipeListener.onPostSwipeCancel(mDirection);
                        hideLoadingView(true, direction, new SwipeAnimatorListener() {

                            @Override
                            public void onAnimationEnd(Animator animation) {
                                LogUtil.i(TAG, "[onPostTouch] Swipe Cancel");
                                if (mOnSwipeListener != null)
                                    mOnSwipeListener.onSwipeCancel(mDirection);
                            }
                        });

                    } else {
                        // Reach the "Swipe Threshold", therefore taking it as Finish;
                        if (mOnSwipeListener != null)
                            mOnSwipeListener.onPostSwipeFinish(mDirection);
                        showLoadingView(true, direction, new SwipeAnimatorListener() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                LogUtil.i(TAG, "[onPostTouch] Swipe Finish");
                                if (mOnSwipeListener != null)
                                    mOnSwipeListener.onSwipeFinish(mDirection);
                            }
                        });

                    }
                }
            }
        };
    }

    private void translateLoadingView(float translate) {
        if (mLoadingView == null) {
            LogUtil.w(TAG, "[translateLoadingView] mLoadingView is null");
            return;
        }
        LogUtil.d(TAG, "[translateLoadingView] translate mLoadingView, translate= " + translate);
        ViewHelper.setTranslationY(mLoadingView, translate);
    }

    private void setInterceptTouchEvent(boolean isInterceptTouchEvent) {
        // setClickable(isInterceptTouchEvent); // Pause or resume detect swipe event.
        mIsIntercept = isInterceptTouchEvent;
    }

    private boolean isInterceptTouchEvent() {
        // return isClickable();
        return mIsIntercept;
    }

    public void hideLoadingView(boolean isShowAnimation, int direction, SwipeAnimatorListener listener) {
        if (mLoadingView == null) {
            LogUtil.w(TAG, "[hideLoadingView] mLoadingView is null");
            return;
        }

        LogUtil.d(TAG, "[hideLoadingView] isShowAnimation = " + isShowAnimation + " direction= " + direction);

        if (direction == SwipeConstants.SWIPE_UNKNOWN) {
            setInterceptTouchEvent(false);
            if (listener != null) listener.onAnimationStart(null);
            resetLoadingViewPosition(SwipeConstants.POSITION_BOTTOM);
            if (listener != null) listener.onAnimationEnd(null);
            setInterceptTouchEvent(true);
            return;
        }

        float targetTranslateY;
        if (direction == SwipeConstants.SWIPE_TO_UP) {
            if (!isShowAnimation) {
                setInterceptTouchEvent(false);
                if (listener != null) listener.onAnimationStart(null);
                resetLoadingViewPosition(SwipeConstants.POSITION_BOTTOM);
                if (listener != null) listener.onAnimationEnd(null);
                setInterceptTouchEvent(true);
                return;
            }
            targetTranslateY = getTotalHeight();
        } else {
            if (!isShowAnimation) {
                setInterceptTouchEvent(false);
                if (listener != null) listener.onAnimationStart(null);
                resetLoadingViewPosition(SwipeConstants.POSITION_ABOVE);
                if (listener != null) listener.onAnimationEnd(null);
                setInterceptTouchEvent(true);
                return;
            }
            targetTranslateY = -getTotalHeight();
        }
        // Execute animation job.
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(mLoadingView, "translationY", ViewHelper.getTranslationY(mLoadingView), targetTranslateY);
        objectAnimator.setDuration(mDuration);
        objectAnimator.setInterpolator(new LinearInterpolator());
        objectAnimator.addListener(new SwipeAnimatorListener() {

            @Override
            public void onAnimationStart(Animator animation) {
                setInterceptTouchEvent(false);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                setInterceptTouchEvent(true);
            }
        });
        if (listener != null) objectAnimator.addListener(listener);
        objectAnimator.start();
    }

    public void showLoadingView(boolean isShowAnimation, int direction, SwipeAnimatorListener listener) {
        if (mLoadingView == null) {
            LogUtil.w(TAG, "[showLoadingView] mLoadingView is null");
            return;
        }

        LogUtil.d(TAG, "[showLoadingView] isShowAnimation = " + isShowAnimation + " direction= " + direction);

        if (direction == SwipeConstants.SWIPE_UNKNOWN) {
            setInterceptTouchEvent(false);
            if (listener != null) listener.onAnimationStart(null);
            resetLoadingViewPosition(SwipeConstants.POSITION_CENTER);
            if (listener != null) listener.onAnimationEnd(null);
        }

        if (!isShowAnimation) {
            setInterceptTouchEvent(false);
            if (listener != null) listener.onAnimationStart(null);
            resetLoadingViewPosition(SwipeConstants.POSITION_CENTER);
            if (listener != null) listener.onAnimationEnd(null);
            return;
        }
        // Execute animation job.
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(mLoadingView, "translationY", ViewHelper.getTranslationY(mLoadingView), 0f);
        objectAnimator.setInterpolator(new LinearInterpolator());
        objectAnimator.setDuration(mDuration);
        objectAnimator.addListener(new SwipeAnimatorListener() {

            @Override
            public void onAnimationStart(Animator animation) {
                setInterceptTouchEvent(false);
            }
        });
        if (listener != null) objectAnimator.addListener(listener);
        objectAnimator.start();
    }


    private void resetLoadingViewPosition(int mode) {
        if (mLoadingView == null) {
            LogUtil.w(TAG, "[resetLoadingViewPosition] mLoadingView is null");
            return;
        }
        switch (mode) {
            case SwipeConstants.POSITION_ABOVE:
                ViewHelper.setTranslationY(mLoadingView, -getTotalHeight());
                break;
            case SwipeConstants.POSITION_CENTER:
                ViewHelper.setTranslationY(mLoadingView, 0f);
                break;
            case SwipeConstants.POSITION_BOTTOM:
                ViewHelper.setTranslationY(mLoadingView, getTotalHeight());
                break;
        }
    }

    private int getTotalHeight() {
        return this.getMeasuredHeight();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // Hide the loading view in the very beginning.
        if (!isCreated.get()) {
            hideLoadingView(false, SwipeConstants.SWIPE_UNKNOWN, null);
           isCreated.set(true);
        }

    }

    float y_pre = INT_INVALID;
    float y_down = INT_INVALID;
    int mDirection = SwipeConstants.SWIPE_UNKNOWN;
    boolean isBeginSwipe = false;
    boolean isMultiTouch = false;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (isMultiTouch || event.getPointerCount() > 1) {
            LogUtil.w(TAG, "[onTouchEvent] multitouch! event.getPointerCount()=" + event.getPointerCount());
            mIsEnable = true;
            return super.onTouchEvent(event); // Eliminate multi-touch.
        }
        if (!mIsEnable) return super.onTouchEvent(event); // Enable is false.
        if (iTouchEventProxy == null) return super.onTouchEvent(event);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                logEventInfo("ACTION_DOWN", event);
                y_down = event.getY(0);
                y_pre = event.getY(0);
                mDirection = SwipeConstants.SWIPE_UNKNOWN;
                isBeginSwipe = false;
                break;
            case MotionEvent.ACTION_MOVE:
                logEventInfo("ACTION_MOVE", event);
                if (isBeginSwipe) {
                    iTouchEventProxy.onTouchOffset(event.getY(0) - y_pre, mDirection);
                } else {
                    float y_abs = Math.abs(event.getY(0) - y_down);
                    LogUtil.i(TAG, "[onTouchEvent] y_abs = " + y_abs);
                    if (y_down != INT_INVALID && y_abs >= iTouchEventProxy.getThresholdMin() && y_abs <= iTouchEventProxy.getThresholdMax()) {

                        // Start swipe job.
                        if (mWorkingMode == SwipeConstants.MODE_VERTICAL) {
                            if (event.getY(0) <= y_down) {
                                // down to up
                                mDirection = SwipeConstants.SWIPE_TO_UP;
                            } else {
                                // up to down
                                mDirection = SwipeConstants.SWIPE_TO_DOWN;
                            }
                            iTouchEventProxy.onPreTouch(mDirection);
                            isBeginSwipe = true;
                            iTouchEventProxy.onTouchOffset(event.getY(0) - y_pre, mDirection);
                        } else if (mWorkingMode == SwipeConstants.MODE_BOTTOM) {
                            if (event.getY(0) <= y_down) {
                                mDirection = SwipeConstants.SWIPE_TO_UP;
                                iTouchEventProxy.onPreTouch(mDirection);
                                isBeginSwipe = true;
                                iTouchEventProxy.onTouchOffset(event.getY(0) - y_pre, mDirection);
                            }

                        } else {
                            if (event.getY(0) > y_down) {
                                mDirection = SwipeConstants.SWIPE_TO_DOWN;
                                iTouchEventProxy.onPreTouch(mDirection);
                                isBeginSwipe = true;
                                iTouchEventProxy.onTouchOffset(event.getY(0) - y_pre, mDirection);
                            }

                        }
                    }
                }
                y_pre = event.getY(0);
                break;
            default:
                LogUtil.i(TAG, "Action = " + event.getAction());
                logEventInfo("ACTION_OTHERS", event);
                y_down = INT_INVALID;
                y_pre = INT_INVALID;
                if (isBeginSwipe) {
                    isBeginSwipe = false;
                    iTouchEventProxy.onPostTouch(mDirection);
                    return true;
                } else {
                    // The OnClick event is based on the TouchEvent, since we have changed the handling ot the TouchEvent,
                    // we have to do the OnClick ourselves, or the OnClickListener of this view will not work.
                    performClick();
                }
                if (event.getPointerCount() <= 1) {
                    isMultiTouch = false;
                }
                break;
        }
        return isInterceptTouchEvent() || super.onTouchEvent(event);
    }

    private void logEventInfo(String type, MotionEvent event) {
        LogUtil.d(TAG, "[onTouchEvent][logEventInfo] " + type + " getY= " + event.getY(0) + "; getRawY=" + event.getRawY());
    }

    public void setLoadingView(View loadingView) {
        this.mLoadingView = loadingView;
    }

    public void setOnSwipeListener(OnSwipeListener onSwipeListener) {
        this.mOnSwipeListener = onSwipeListener;
    }

    public int getDirection() {
        return mDirection;
    }

    public void setAnimationDuration(int duration) {
        this.mDuration = duration;
    }

    public void setWorkingMode(int workingMode) {
        this.mWorkingMode = workingMode;
    }

    public void setEnable(boolean isEnable) {
        mIsEnable = isEnable;
    }

    public interface ITouchEventProxy {
        int getThresholdMin();

        int getThresholdMax();

        void onPreTouch(int direction);

        void onTouchOffset(float offsetY, int direction);

        void onPostTouch(int direction);
    }


}
