package com.kaedea.widget.swipeloadingview.core;

/**
 * Created by kaede on 2016/3/23.
 */
public interface ISwipeDetector {
	void attach(ITouEventHandler iTouEventHandler, ISwipeHandler iSwipeHandler);
	void setInterceptTouchEvent(boolean isEnable);
	int getTotalHeight();
}
