package widget.kaedea.com.swipeloadingview;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
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
public class SwipeDetectorLayout extends RelativeLayout{
	AtomicBoolean isIntercept = new AtomicBoolean();
	ITouchEventProxy iTouchEventProxy;

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

	private void init(){
		isIntercept.set(true); // 拦截TouchEvent
		iTouchEventProxy = new ITouchEventProxy() {
			@Override
			public int getThreshold() {
				return 50;
			}

			@Override
			public void onTouchOffset(float offsetY) {
				Log.e(TAG, "[onTouchOffset] offsetY =" + offsetY);
			}
		};
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
				if (isBeginSwipe){
					iTouchEventProxy.onTouchOffset(event.getY() - y_pre);
				}
				if (Math.abs(event.getY() - y_down) >= iTouchEventProxy.getThreshold()){
					iTouchEventProxy.onTouchOffset(event.getY() - y_pre);
					isBeginSwipe = true;
				}
				y_pre = event.getY();
				break;
			default:
				Log.e(TAG, "Action = " + event.getAction());
				logEventInfo("ACTION_OTHERS", event);
				y_down = 0;
				y_pre = 0;
				isBeginSwipe = false;
				break;
		}
		return isIntercept.get() || super.onTouchEvent(event);
	}

	private void logEventInfo(String type,MotionEvent event) {
		Log.d(TAG, "[onTouchEvent][logEventInfo] "+type+" getY= " + event.getY() + "; getRawY=" + event.getRawY());
	}

	public interface ITouchEventProxy{
		public int getThreshold();
		public void onTouchOffset(float offsetY);
	}
}
