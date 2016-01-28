package widget.kaedea.com.swipeloadingview;

import android.animation.Animator;

/**
 * Created by kaede on 2016/1/28.
 */
abstract public class AnimatorEndListener implements Animator.AnimatorListener {
	@Override
	public void onAnimationStart(Animator animation) {

	}

	abstract public void onAnimationEnd(Animator animation);

	@Override
	public void onAnimationCancel(Animator animation) {

	}

	@Override
	public void onAnimationRepeat(Animator animation) {

	}
}
