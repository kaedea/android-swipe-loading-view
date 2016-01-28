package widget.kaedea.com.swipeloadingview;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.os.Build;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created kaede on 1/26/16.
 */
@SuppressLint("LongLogTag")
public class SwipeDetectorLayout extends RelativeLayout {
	AtomicBoolean isIntercept = new AtomicBoolean();
	ITouchEventProxy iTouchEventProxy;
	View viewLoading;
	float swipeRatio;

	public static final String TAG = "SwipeDetectorLayout";

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

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		return isIntercept.get() || super.onInterceptTouchEvent(ev);
	}

	private void init() {
		isIntercept.set(true); // 拦截TouchEvent
		/*Activity activity = (Activity) getContext();
		viewLoading = activity.findViewById(R.id.view_loading);*/
		iTouchEventProxy = new ITouchEventProxy() {
			int threshold = 50;

			@Override
			public int getThreshold() {
				Log.i(TAG, "[getThreshold] threshold =" + threshold);
				return threshold;
			}

			@Override
			public void onTouchOffset(float offsetY) {
				float targetTranslationY = viewLoading.getTranslationY() + offsetY;
				swipeRatio = 1f - viewLoading.getTranslationY() / getTotalHeight();
				Log.i(TAG, "[onTouchOffset] swipeRatio =" + swipeRatio);
				Log.i(TAG, "[onTouchOffset] offsetY =" + offsetY + " viewLoading.getTranslationY() = " + viewLoading.getTranslationY() + " targetTranslationY=" + targetTranslationY);
				if (offsetY<0f && targetTranslationY<0f) {
					ViewCompat.setTranslationY(viewLoading, 0f);
					return;
				}
				ViewCompat.setTranslationY(viewLoading, targetTranslationY);
			}

			@Override
			public void onTouchFinished() {
				if (viewLoading.getTranslationY()>SwipeDetectorLayout.this.getMeasuredHeight()){
					ViewCompat.setTranslationY(viewLoading, getTotalHeight());
				}
			}
		};
	}

	public void resetLoadingView(boolean isShowAnimation){
		if (!isShowAnimation){
			ViewCompat.setTranslationY(viewLoading, getTotalHeight());
		}
	}

	private int getTotalHeight(){
		return this.getMeasuredHeight();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		ViewCompat.setTranslationY(viewLoading, getTotalHeight());
	}

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
				Log.i(TAG, "Action = " + event.getAction());
				logEventInfo("ACTION_OTHERS", event);
				y_down = 0;
				y_pre = 0;
				isBeginSwipe = false;
				iTouchEventProxy.onTouchFinished();
				break;
		}
		return isIntercept.get() || super.onTouchEvent(event);
	}

	private void logEventInfo(String type, MotionEvent event) {
		Log.d(TAG, "[onTouchEvent][logEventInfo] " + type + " getY= " + event.getY() + "; getRawY=" + event.getRawY());
	}

	public void setViewLoading(View viewLoading) {
		this.viewLoading = viewLoading;
	}

	public interface ITouchEventProxy {
		public int getThreshold();

		public void onTouchOffset(float offsetY);

		public void onTouchFinished();
	}
}
