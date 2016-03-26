package com.kaedea.widget.swipeloadingview.core;

import android.view.View;

/**
 * Created by kaede on 2016/3/23.
 */
public interface ISwipeHandler {
    void attach(ISwipeDetector iSwipeDetector);

    float getFinishRatio();

    int getThresholdMin();

    int getThresholdMax();

    void onPreTouch(int direction);

    void onTouchOffset(float offsetY, int direction);

    void onPostTouch(int direction);

    void hideLoadingView(boolean isShowAnimation, int direction, OnSwipeAnimationListener listener);

    void showLoadingView(boolean isShowAnimation, int direction, OnSwipeAnimationListener listener);

    void setLoadingView(View loadingView);

    void setOnSwipeListener(OnSwipeListener onSwipeListener);

    int getDirection();

    void setDirection(int direction);

    void setAnimationDuration(int duration);

    void setWorkingMode(int workingMode);

    int getWorkingMode();

    void setEnable(boolean isEnable);

    boolean isEnable();
}
