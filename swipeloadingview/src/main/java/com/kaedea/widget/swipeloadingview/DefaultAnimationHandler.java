package com.kaedea.widget.swipeloadingview;

import android.support.v4.view.ViewCompat;
import android.view.View;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

import com.kaedea.widget.swipeloadingview.util.animation.Animator;
import com.kaedea.widget.swipeloadingview.util.animation.ObjectAnimator;
import com.kaedea.widget.swipeloadingview.core.IAnimationHandler;
import com.kaedea.widget.swipeloadingview.core.SwipeConstants;

/**
 * Created by Kaede on 2016/3/26.
 */
public class DefaultAnimationHandler implements IAnimationHandler {
    Interpolator interpolator;
    int duration;

    public DefaultAnimationHandler() {
        interpolator = new LinearInterpolator();
        duration = SwipeConstants.DEFAULT_DURATION;
    }

    @Override
    public void show(View targetView, String property, OnSwipeAnimationListener listener) {
        if (targetView == null) {
            return;
        }
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(targetView, property, ViewCompat.getTranslationY(targetView), 0f);
        objectAnimator.setInterpolator(interpolator);
        objectAnimator.setDuration(duration);
        if (listener != null) objectAnimator.addListener(new SwipeAnimatorListener(listener));
        objectAnimator.start();
    }

    @Override
    public void hide(View targetView, String property, float positionEnd, OnSwipeAnimationListener listener) {
        if (targetView == null) {
            return;
        }
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(targetView, property, ViewCompat.getTranslationY(targetView), positionEnd);
        objectAnimator.setInterpolator(interpolator);
        objectAnimator.setDuration(duration);
        if (listener != null) objectAnimator.addListener(new SwipeAnimatorListener(listener));
        objectAnimator.start();
    }

    @Override
    public void setDuration(int duration) {
        this.duration = duration;
    }

    @Override
    public void setInterpolator(Interpolator interpolator) {
        this.interpolator = interpolator;
    }

    public class SwipeAnimatorListener extends BaseSwipeAnimatorListener {

        OnSwipeAnimationListener listener;

        public SwipeAnimatorListener(OnSwipeAnimationListener listener) {
            this.listener = listener;
        }

        @Override
        public void onAnimationStart(Animator animation) {
            listener.onAnimationStart();
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            listener.onAnimationEnd();
        }
    }
}
