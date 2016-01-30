package widget.kaedea.com.swipeloadingview;

/**
 * Created by kaede on 2016/1/30.
 */
public interface OnSwipeListener {
	public void onSwiping(float swipeRatio, int direction);

	public void onSwipeFinished(int direction);

	public void onSwipeCanceled(int direction);
}
