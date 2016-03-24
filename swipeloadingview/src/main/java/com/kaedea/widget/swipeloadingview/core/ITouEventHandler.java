package com.kaedea.widget.swipeloadingview.core;

import android.view.MotionEvent;
import android.view.View;

/**
 * Created by kaede on 2016/3/23.
 */
public interface ITouEventHandler {
	void attach(ISwipeHandler iSwipeHandler);

	boolean onTouchEvent(View localView, MotionEvent event);
}
