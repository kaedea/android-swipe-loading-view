package com.kaedea.widget.swipeloadingview;

import android.view.View;
import android.view.animation.Interpolator;

/**
 * Created by Kaede on 2016/3/26.
 */
public class AnimationHandler {
    Interpolator interpolator;
    int duration;

    public void show(View targetView,String property,float begin,float end,OnSwipeAnimationListener listener){
        if (targetView == null) {
            return;
        }

    }

    public interface OnSwipeAnimationListener{
        public void onAnimationStart();
        public void onAnimationEnd();
    }
}
