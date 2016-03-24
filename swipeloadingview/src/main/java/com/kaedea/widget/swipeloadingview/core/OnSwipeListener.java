package com.kaedea.widget.swipeloadingview.core;

/**
 * Created by kaede on 2016/1/30.
 */
public interface OnSwipeListener {
    void onSwipeStart(int direction);

    void onSwiping(float swipeRatio, int direction);

    void onPostSwipeFinish(int direction);

    void onSwipeFinish(int direction);

    void onPostSwipeCancel(int direction);

    void onSwipeCancel(int direction);
}
