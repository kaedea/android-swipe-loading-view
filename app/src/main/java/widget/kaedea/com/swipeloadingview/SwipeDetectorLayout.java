package widget.kaedea.com.swipeloadingview;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.os.Build;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import widget.kaedea.com.swipeloadingview.util.LogUtil;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 *
 * Created kaede on 1/26/16.
 */
@SuppressLint("LongLogTag")
public class SwipeDetectorLayout extends View {
	public static final String TAG = "SwipeDetectorLayout";

	ITouchEventProxy iTouchEventProxy;
	OnSwipeListener mOnSwipeListener;

	AtomicBoolean isCreated = new AtomicBoolean();
	View mLoadingView;
	float mSwipeRatio;
	private float mSwipeRatioThreshold;

	public SwipeDetectorLayout(Context context) {
		super(context);
		init();
	}

	public SwipeDetectorLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public SwipeDetectorLayout(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}

	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	public SwipeDetectorLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
		init();
	}

	private void init() {
		isCreated.set(false);
		setInterceptTouchEvent(true);
		iTouchEventProxy = new ITouchEventProxy() {
			int threshold = 100;

			@Override
			public int getThreshold() {
				LogUtil.i(TAG, "[getThreshold] threshold =" + threshold);
				return threshold;
			}

			@Override
			public void onTouchOffset(float offsetY) {
				float targetTranslationY = mLoadingView.getTranslationY() + offsetY;
				mSwipeRatio = 1f - mLoadingView.getTranslationY() / getTotalHeight();
				LogUtil.i(TAG, "[onTouchOffset] mSwipeRatio =" + mSwipeRatio);
				LogUtil.i(TAG, "[onTouchOffset] offsetY =" + offsetY + " mLoadingView.getTranslationY() = " + mLoadingView.getTranslationY() + " targetTranslationY=" + targetTranslationY);
				if (offsetY < 0f && targetTranslationY < 0f) {
					ViewCompat.setTranslationY(mLoadingView, 0f);
					return;
				}
				ViewCompat.setTranslationY(mLoadingView, targetTranslationY);
				// Notify swipe event's progress.
				LogUtil.d(TAG, "[onTouchOffset] Notify swipe event's progress.");
				if (mOnSwipeListener != null)
					mOnSwipeListener.onSwipping(mSwipeRatio);
			}

			@Override
			public void onTouchFinished() {
				if (mLoadingView.getTranslationY() > SwipeDetectorLayout.this.getMeasuredHeight()) {
					ViewCompat.setTranslationY(mLoadingView, getTotalHeight());
					if (mOnSwipeListener != null)
						mOnSwipeListener.onSwipeCanceled();
				} else {
					mSwipeRatioThreshold = 0.5f;
					if (mSwipeRatio <= mSwipeRatioThreshold) {
						// Can not reach the "Swipe Threshold", therefore taking it as Cancel;
						hideLoadingView(true);
						LogUtil.d(TAG, "[onTouchFinished] Swipe Cancel");
						if (mOnSwipeListener != null)
							mOnSwipeListener.onSwipeCanceled();
					} else {
						// Reach the "Swipe Threshold", therefore taking it as Finish;
						showLoadingView(true);
						LogUtil.d(TAG, "[onTouchFinished] Swipe Finish");
						if (mOnSwipeListener != null)
							mOnSwipeListener.onSwipeFinished();
					}
				}
			}
		};
	}

	private void setInterceptTouchEvent(boolean isConsume) {
		setClickable(isConsume);
	}

	private boolean isConsumeTouchEvent(){
		return isClickable();
	}

	public void hideLoadingView(boolean isShowAnimation) {
		if (mLoadingView == null) {
			LogUtil.w(TAG, "[hideLoadingView] mLoadingView is null");
			return;
		}
		LogUtil.d(TAG, "[hideLoadingView] isShowAnimation = " + isShowAnimation);
		if (!isShowAnimation) {
			ViewCompat.setTranslationY(mLoadingView, getTotalHeight());
			setInterceptTouchEvent(true);
			return;
		}
		ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(mLoadingView, "translationY", mLoadingView.getTranslationY(), getTotalHeight());
		objectAnimator.setDuration(500);
		objectAnimator.addListener(new SwipeAnimatorListener() {
			@Override
			public void onAnimationEnd(Animator animation) {
				setInterceptTouchEvent(true); // Intercept TouchEvent, or we can not get the Action_Move event.
			}
		});
		objectAnimator.start();

	}

	public void showLoadingView(boolean isShowAnimation) {
		if (mLoadingView == null) {
			LogUtil.w(TAG, "[showLoadingView] mLoadingView is null");
			return;
		}
		LogUtil.d(TAG, "[showLoadingView] isShowAnimation = " + isShowAnimation);
		if (!isShowAnimation) {
			ViewCompat.setTranslationY(mLoadingView, 0f);
			setInterceptTouchEvent(false);
			return;
		}
		ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(mLoadingView, "translationY", mLoadingView.getTranslationY(), 0f);
		objectAnimator.setDuration(500);
		objectAnimator.addListener(new SwipeAnimatorListener() {
			@Override
			public void onAnimationEnd(Animator animation) {
				setInterceptTouchEvent(false);
			}
		});
		objectAnimator.start();
	}

	private int getTotalHeight() {
		return this.getMeasuredHeight();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		// Hide the loading view in the very beginning.
		if (!isCreated.get()){
			hideLoadingView(false);
			isCreated.set(true);
		}

	}

	/*@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		return isIntercept.get() || super.onInterceptTouchEvent(ev);
	}*/

	float y_pre = 0;
	float y_down = 0;
	boolean isBeginSwipe = false;

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (iTouchEventProxy == null) return super.onTouchEvent(event);

		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				logEventInfo("ACTION_DOWN", event);
				y_down = event.getY();
				y_pre = event.getY();
				isBeginSwipe = false;
				break;
			case MotionEvent.ACTION_MOVE:
				logEventInfo("ACTION_MOVE", event);
				if (isBeginSwipe) {
					iTouchEventProxy.onTouchOffset(event.getY() - y_pre);
				}
				if (Math.abs(event.getY() - y_down) >= iTouchEventProxy.getThreshold()) {
					iTouchEventProxy.onTouchOffset(event.getY() - y_pre);
					isBeginSwipe = true;
				}
				y_pre = event.getY();
				break;
			default:
				LogUtil.i(TAG, "Action = " + event.getAction());
				logEventInfo("ACTION_OTHERS", event);
				y_down = 0;
				y_pre = 0;
				if (isBeginSwipe) {
					iTouchEventProxy.onTouchFinished();
					isBeginSwipe = false;
				}
				break;
		}
		return isConsumeTouchEvent() || super.onTouchEvent(event);
	}

	private void logEventInfo(String type, MotionEvent event) {
		LogUtil.d(TAG, "[onTouchEvent][logEventInfo] " + type + " getY= " + event.getY() + "; getRawY=" + event.getRawY());
	}

	public void setLoadingView(View loadingView) {
		this.mLoadingView = loadingView;
	}

	public void setOnSwipeListener(OnSwipeListener onSwipeListener) {
		this.mOnSwipeListener = onSwipeListener;
	}


	public interface ITouchEventProxy {
		public int getThreshold();

		public void onTouchOffset(float offsetY);

		public void onTouchFinished();
	}


	public interface OnSwipeListener {
		public void onSwipping(float swipeRatio);

		public void onSwipeFinished();

		public void onSwipeCanceled();
	}
}
