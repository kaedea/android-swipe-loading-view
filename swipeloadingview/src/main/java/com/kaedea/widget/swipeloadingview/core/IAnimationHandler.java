package com.kaedea.widget.swipeloadingview.core;

import android.view.View;
import android.view.animation.Interpolator;

/**
 * Created by kaede on 2016/3/23.
 */
public interface IAnimationHandler {
    void show(View targetView, String property, OnSwipeAnimationListener listener);

    void hide(View targetView, String property, float positionEnd, OnSwipeAnimationListener listener);

    void setDuration(int duration);

    void setInterpolator(Interpolator interpolator);
}
