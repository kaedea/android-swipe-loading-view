package com.kaedea.widget.swipeloadingview;

import android.support.v4.view.ViewCompat;
import android.view.View;

import com.kaedea.widget.swipeloadingview.core.IAnimationHandler;
import com.kaedea.widget.swipeloadingview.core.ISwipeDetector;
import com.kaedea.widget.swipeloadingview.core.ISwipeHandler;
import com.kaedea.widget.swipeloadingview.core.OnSwipeAnimationListener;
import com.kaedea.widget.swipeloadingview.core.OnSwipeListener;
import com.kaedea.widget.swipeloadingview.core.SwipeConstants;
import com.kaedea.widget.swipeloadingview.util.LogUtil;

/**
 * Created by kaede on 2016/3/23.
 */
public class DefaultSwipeHandler implements ISwipeHandler {
	public static final String TAG = "DefaultSwipeHandler";

	View mLoadingView;
	ISwipeDetector mISwipeDetector;
	IAnimationHandler iAnimationHandler;
	int mDirection = SwipeConstants.SWIPE_UNKNOWN;
	OnSwipeListener mOnSwipeListener;
	float mSwipeRatio;
	private int mWorkingMode = SwipeConstants.DEFAULT_WORKING_MODE;
	private boolean mIsEnable = true;

	private float mSwipeRatioThreshold = 0.2f;
	int thresholdMin = 20;
	int thresholdMax = 150;

	public DefaultSwipeHandler() {
		iAnimationHandler = new DefaultAnimationHandler();
	}

	@Override
	public void attach(ISwipeDetector iSwipeDetector){
		mISwipeDetector = iSwipeDetector;
	}

	@Override
	public float getFinishRatio() {
		return mSwipeRatioThreshold;
	}

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
		float targetTranslationY = ViewCompat.getTranslationY(mLoadingView) + offsetY;
		if (direction == SwipeConstants.SWIPE_TO_UP) {
			mSwipeRatio = 1f - ViewCompat.getTranslationY(mLoadingView) / mISwipeDetector.getTotalHeight();
			LogUtil.d(TAG, "[onTouchOffset] direction =" + direction);
			LogUtil.d(TAG, "[onTouchOffset] mSwipeRatio =" + mSwipeRatio);
			LogUtil.d(TAG, "[onTouchOffset] offsetY =" + offsetY + " mLoadingView.getTranslationY() = " + ViewCompat.getTranslationY(mLoadingView) + " targetTranslationY=" + targetTranslationY);
			if (offsetY < 0f && targetTranslationY < 0f) {
				// can not continue swipe up
				LogUtil.i(TAG, "[onTouchOffset] can not continue swipe up!");
				resetLoadingViewPosition(SwipeConstants.POSITION_CENTER);
				return;
			}
		} else {
			// swipe up to down
			mSwipeRatio = ViewCompat.getTranslationY(mLoadingView) / mISwipeDetector.getTotalHeight() + 1;
			LogUtil.d(TAG, "[onTouchOffset] direction =" + direction);
			LogUtil.d(TAG, "[onTouchOffset] mSwipeRatio =" + mSwipeRatio);
			LogUtil.d(TAG, "[onTouchOffset] offsetY =" + offsetY + " mLoadingView.getTranslationY() = " + ViewCompat.getTranslationY(mLoadingView) + " targetTranslationY=" + targetTranslationY);
			if (offsetY > 0f && targetTranslationY > mISwipeDetector.getTotalHeight()) {
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
		if (ViewCompat.getTranslationY(mLoadingView) > mISwipeDetector.getTotalHeight()) {
			// below the bottom end
			LogUtil.i(TAG, "[onPostTouch] below the bottom end");
			resetLoadingViewPosition(SwipeConstants.POSITION_BOTTOM);
			if (mOnSwipeListener != null)
				mOnSwipeListener.onSwipeCancel(mDirection);
		} else if (ViewCompat.getTranslationY(mLoadingView) < -mISwipeDetector.getTotalHeight()) {
			// above the top end
			LogUtil.i(TAG, "[onPostTouch] above the top end");
			resetLoadingViewPosition(SwipeConstants.POSITION_ABOVE);
			if (mOnSwipeListener != null)
				mOnSwipeListener.onSwipeCancel(mDirection);
		} else {
			if (mSwipeRatio <= getFinishRatio()) {
				// Can not reach the "Swipe Threshold", therefore taking it as Cancel;
				if (mOnSwipeListener != null)
					mOnSwipeListener.onPostSwipeCancel(mDirection);
				hideLoadingView(true, direction, new OnSwipeAnimationListener() {

					@Override
					public void onAnimationStart() {

					}

					@Override
					public void onAnimationEnd() {
						LogUtil.i(TAG, "[onPostTouch] Swipe Cancel");
						if (mOnSwipeListener != null)
							mOnSwipeListener.onSwipeCancel(mDirection);
					}
				});

			} else {
				// Reach the "Swipe Threshold", therefore taking it as Finish;
				if (mOnSwipeListener != null)
					mOnSwipeListener.onPostSwipeFinish(mDirection);
				showLoadingView(true, direction, new OnSwipeAnimationListener() {
					@Override
					public void onAnimationStart() {

					}

					@Override
					public void onAnimationEnd() {
						LogUtil.i(TAG, "[onPostTouch] Swipe Finish");
						if (mOnSwipeListener != null)
							mOnSwipeListener.onSwipeFinish(mDirection);
					}
				});

			}
		}
	}

	private void translateLoadingView(float translate) {
		if (mLoadingView == null) {
			LogUtil.w(TAG, "[translateLoadingView] mLoadingView is null");
			return;
		}
		LogUtil.d(TAG, "[translateLoadingView] translate mLoadingView, translate= " + translate);
		ViewCompat.setTranslationY(mLoadingView, translate);
	}


	private void resetLoadingViewPosition(int mode) {
		if (mLoadingView == null) {
			LogUtil.w(TAG, "[resetLoadingViewPosition] mLoadingView is null");
			return;
		}
		switch (mode) {
			case SwipeConstants.POSITION_ABOVE:
				ViewCompat.setTranslationY(mLoadingView, -mISwipeDetector.getTotalHeight());
				break;
			case SwipeConstants.POSITION_CENTER:
				ViewCompat.setTranslationY(mLoadingView, 0f);
				break;
			case SwipeConstants.POSITION_BOTTOM:
				ViewCompat.setTranslationY(mLoadingView, mISwipeDetector.getTotalHeight());
				break;
		}
	}


	@Override
	public void hideLoadingView(boolean isShowAnimation, int direction, final OnSwipeAnimationListener listener) {
		if (mLoadingView == null) {
			LogUtil.w(TAG, "[hideLoadingView] mLoadingView is null");
			return;
		}

		LogUtil.d(TAG, "[hideLoadingView] isShowAnimation = " + isShowAnimation + " direction= " + direction);

		if (direction == SwipeConstants.SWIPE_UNKNOWN) {
			mISwipeDetector.setInterceptTouchEvent(false);
			if (listener != null) listener.onAnimationStart();
			resetLoadingViewPosition(SwipeConstants.POSITION_BOTTOM);
			if (listener != null) listener.onAnimationEnd();
			mISwipeDetector.setInterceptTouchEvent(true);
			return;
		}

		float targetTranslateY;
		if (direction == SwipeConstants.SWIPE_TO_UP) {
			if (!isShowAnimation) {
				mISwipeDetector.setInterceptTouchEvent(false);
				if (listener != null) listener.onAnimationStart();
				resetLoadingViewPosition(SwipeConstants.POSITION_BOTTOM);
				if (listener != null) listener.onAnimationEnd();
				mISwipeDetector.setInterceptTouchEvent(true);
				return;
			}
			targetTranslateY = mISwipeDetector.getTotalHeight();
		} else {
			if (!isShowAnimation) {
				mISwipeDetector.setInterceptTouchEvent(false);
				if (listener != null) listener.onAnimationStart();
				resetLoadingViewPosition(SwipeConstants.POSITION_ABOVE);
				if (listener != null) listener.onAnimationEnd();
				mISwipeDetector.setInterceptTouchEvent(true);
				return;
			}
			targetTranslateY = -mISwipeDetector.getTotalHeight();
		}
		// Execute animation job.
		iAnimationHandler.hide(mLoadingView, "translationY", targetTranslateY, new OnSwipeAnimationListener() {
			@Override
			public void onAnimationStart() {
				mISwipeDetector.setInterceptTouchEvent(false);
				if (listener != null)
					listener.onAnimationStart();
			}

			@Override
			public void onAnimationEnd() {
				mISwipeDetector.setInterceptTouchEvent(true);
				if (listener != null)
					listener.onAnimationEnd();
			}
		});
	}

	@Override
	public void showLoadingView(boolean isShowAnimation, int direction, final OnSwipeAnimationListener listener) {
		if (mLoadingView == null) {
			LogUtil.w(TAG, "[showLoadingView] mLoadingView is null");
			return;
		}

		LogUtil.d(TAG, "[showLoadingView] isShowAnimation = " + isShowAnimation + " direction= " + direction);

		if (direction == SwipeConstants.SWIPE_UNKNOWN) {
			mISwipeDetector.setInterceptTouchEvent(false);
			if (listener != null) listener.onAnimationStart();
			resetLoadingViewPosition(SwipeConstants.POSITION_CENTER);
			if (listener != null) listener.onAnimationEnd();
		}

		if (!isShowAnimation) {
			mISwipeDetector.setInterceptTouchEvent(false);
			if (listener != null) listener.onAnimationStart();
			resetLoadingViewPosition(SwipeConstants.POSITION_CENTER);
			if (listener != null) listener.onAnimationEnd();
			return;
		}
		// Execute animation job.
		iAnimationHandler.show(mLoadingView, "translationY", new OnSwipeAnimationListener() {
			@Override
			public void onAnimationStart() {
				mISwipeDetector.setInterceptTouchEvent(false);
				if (listener != null)
					listener.onAnimationStart();
			}

			@Override
			public void onAnimationEnd() {
				if (listener != null)
					listener.onAnimationEnd();
			}
		});
	}

	@Override
	public void setLoadingView(View loadingView) {
		this.mLoadingView = loadingView;
	}

	@Override
	public void setOnSwipeListener(OnSwipeListener onSwipeListener) {
		this.mOnSwipeListener = onSwipeListener;
	}

	@Override
	public int getDirection() {
		return mDirection;
	}

	@Override
	public void setDirection(int direction){
		this.mDirection = direction;
	}

	@Override
	public void setAnimationDuration(int duration) {
		this.iAnimationHandler.setDuration(duration);
	}

	@Override
	public void setWorkingMode(int workingMode) {
		this.mWorkingMode = workingMode;
	}

	@Override
	public int getWorkingMode(){
		return this.mWorkingMode;
	}

	@Override
	public void setEnable(boolean isEnable) {
		mIsEnable = isEnable;
	}

	@Override
	public boolean isEnable(){
		return mIsEnable;
	}

}
