package widget.kaedea.com.swipeloadingview;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

/**
 * Created kaede on 1/26/16.
 */
public class SwipeDetectorLayout extends RelativeLayout{

	public static final String TAG = "SwipeDetectorLayout";

	public SwipeDetectorLayout(Context context) {
		super(context);
	}

	public SwipeDetectorLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public SwipeDetectorLayout(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	public SwipeDetectorLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch(event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				logEventInfo("ACTION_DOWN",event);
				break;
			case MotionEvent.ACTION_MOVE:
				logEventInfo("ACTION_MOVE",event);
				break;
		default:
			Log.e(TAG,"Action = "+event.getAction());
			logEventInfo("ACTION_OTHERS", event);
		    break;
		}
		return super.onTouchEvent(event);
	}

	private void logEventInfo(String type,MotionEvent event) {
		Log.d(TAG, "[onTouchEvent] "+type+" getX= " + event.getX() + ";getRawX=" + event.getRawX() + ";getXPre=" + event.getXPrecision());
	}
}
