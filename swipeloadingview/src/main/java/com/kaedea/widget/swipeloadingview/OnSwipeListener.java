package com.kaedea.widget.swipeloadingview;

/**
 * Created by kaede on 2016/1/30.
 */
public interface OnSwipeListener {
	public void onSwipeStart(int direction);

	public void onSwiping(float swipeRatio, int direction);

	public void onPostSwipeFinish(int direction);

	public void onSwipeFinish(int direction);

	public void onPostSwipeCancel(int direction);

	public void onSwipeCancel(int direction);
}
