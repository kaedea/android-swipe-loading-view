package com.kaedea.widget.swipeloadingviewdemo;

import com.kaedea.widget.swipeloadingview.OnSwipeListener;
import com.kaedea.widget.swipeloadingview.util.LogUtil;

/**
 * Created by kaede on 2016/3/17.
 */
public class BaseOnSwipeListenerImpl implements OnSwipeListener{

	public static final String TAG = "BaseOnSwipeListenerImpl";

	@Override
	public void onSwipeStart(int direction) {
		LogUtil.d(TAG,"[onSwipeStart] direction= "+direction);
	}

	@Override
	public void onSwiping(float swipeRatio, int direction) {
		LogUtil.d(TAG,"[onSwiping] swipeRatio= "+swipeRatio+" direction = "+direction);
	}

	@Override
	public void onPostSwipeFinish(int direction) {
		LogUtil.d(TAG,"[onPostSwipeFinish] direction= "+direction);
	}

	@Override
	public void onSwipeFinish(int direction) {
		LogUtil.d(TAG,"[onSwipeFinish] direction= "+direction);
	}

	@Override
	public void onPostSwipeCancel(int direction) {
		LogUtil.d(TAG,"[onPostSwipeCancel] direction= "+direction);
	}

	@Override
	public void onSwipeCancel(int direction) {
		LogUtil.d(TAG,"[onSwipeCancel] direction= "+direction);
	}
}
