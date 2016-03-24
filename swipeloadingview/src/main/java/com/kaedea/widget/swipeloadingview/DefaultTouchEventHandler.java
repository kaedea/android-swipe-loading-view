package com.kaedea.widget.swipeloadingview;

import android.view.MotionEvent;
import android.view.View;
import com.kaedea.widget.swipeloadingview.core.ISwipeHandler;
import com.kaedea.widget.swipeloadingview.core.ITouEventHandler;
import com.kaedea.widget.swipeloadingview.core.SwipeConstants;
import com.kaedea.widget.swipeloadingview.util.LogUtil;

/**
 * Created by kaede on 2016/3/23.
 */
public class DefaultTouchEventHandler implements ITouEventHandler {
	public static final String TAG = "DefaultTouchEventHandler";
	public static final int INT_INVALID = -10086;

	ISwipeHandler iSwipeHandler;

	float y_pre = INT_INVALID;
	float y_down = INT_INVALID;
	boolean isBeginSwipe = false;
	// boolean isMultiTouch = false;

	@Override
	public void attach(ISwipeHandler iSwipeHandler) {
		this.iSwipeHandler = iSwipeHandler;
	}

	@Override
	public boolean onTouchEvent(View localView, MotionEvent event) {
		if (iSwipeHandler == null) {
			LogUtil.w(TAG, "[onTouchEvent] iTouchEventProxy is null!");
			return false;
		}
		/*if (isMultiTouch || event.getPointerCount() > 1) {
			LogUtil.w(TAG, "[onTouchEvent] multitouch! event.getPointerCount()=" + event.getPointerCount());
			isMultiTouch = true;
			return iSwipeHandler.isEnable(); // Eliminate multi-touch.
		}*/
		if (!iSwipeHandler.isEnable()) return false; // Enable is false.

		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				logEventInfo("ACTION_DOWN", event);
				y_down = event.getY(0);
				y_pre = event.getY(0);
				iSwipeHandler.setDirection(SwipeConstants.SWIPE_UNKNOWN);
				isBeginSwipe = false;
				break;
			case MotionEvent.ACTION_MOVE:
				logEventInfo("ACTION_MOVE", event);
				if (isBeginSwipe) {
					iSwipeHandler.onTouchOffset(event.getY(0) - y_pre, iSwipeHandler.getDirection());
				} else {
					float y_abs = Math.abs(event.getY(0) - y_down);
					LogUtil.i(TAG, "[onTouchEvent] y_abs = " + y_abs);
					if (y_down != INT_INVALID && y_abs >= iSwipeHandler.getThresholdMin() && y_abs <= iSwipeHandler.getThresholdMax()) {

						// Start swipe job.
						if (iSwipeHandler.getWorkingMode() == SwipeConstants.MODE_VERTICAL) {
							if (event.getY(0) <= y_down) {
								// down to up
								iSwipeHandler.setDirection(SwipeConstants.SWIPE_TO_UP);
							} else {
								// up to down
								iSwipeHandler.setDirection(SwipeConstants.SWIPE_TO_DOWN);
							}
							iSwipeHandler.onPreTouch(iSwipeHandler.getDirection());
							isBeginSwipe = true;
							iSwipeHandler.onTouchOffset(event.getY(0) - y_pre, iSwipeHandler.getDirection());
						} else if (iSwipeHandler.getWorkingMode() == SwipeConstants.MODE_BOTTOM) {
							if (event.getY(0) <= y_down) {
								iSwipeHandler.setDirection(SwipeConstants.SWIPE_TO_UP);
								iSwipeHandler.onPreTouch(iSwipeHandler.getDirection());
								isBeginSwipe = true;
								iSwipeHandler.onTouchOffset(event.getY(0) - y_pre, iSwipeHandler.getDirection());
							}

						} else {
							if (event.getY(0) > y_down) {
								iSwipeHandler.setDirection(SwipeConstants.SWIPE_TO_DOWN);
								iSwipeHandler.onPreTouch(iSwipeHandler.getDirection());
								isBeginSwipe = true;
								iSwipeHandler.onTouchOffset(event.getY(0) - y_pre, iSwipeHandler.getDirection());
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
					iSwipeHandler.onPostTouch(iSwipeHandler.getDirection());
					return true;
				} else {
					// The OnClick event is based on the TouchEvent, since we have changed the handling ot the TouchEvent,
					// we have to do the OnClick ourselves, or the OnClickListener of this view will not work.
					localView.performClick();
				}
/*				if (event.getPointerCount() <= 1) {
					isMultiTouch = false;
				}*/
				break;
		}
		return iSwipeHandler.isEnable();
	}

	private void logEventInfo(String type, MotionEvent event) {
		LogUtil.d(TAG, "[onTouchEvent][logEventInfo] " + type + " getY= " + event.getY(0) + "; getRawY=" + event.getRawY());
	}
}
