package com.kaedea.widget.swipeloadingviewdemo.demolist.presenter;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import com.kaedea.widget.swipeloadingviewdemo.EndlessSwipeActivity;
import com.kaedea.widget.swipeloadingviewdemo.SimpleSwipeActivity;
import com.kaedea.widget.swipeloadingviewdemo.VerticalSwipeActivity;
import com.kaedea.widget.swipeloadingviewdemo.demolist.util.ActivityHolder;
import com.kaedea.widget.swipeloadingviewdemo.demolist.view.IHomeView;


/**
 * Created by kaede on 2015/5/19.
 */
public class HomePresenterCompl implements IHomePresenter {
	public static ActivityHolder activityHolder;
	static {
		activityHolder = new ActivityHolder();
		activityHolder.addActivity("Simple Swipe",SimpleSwipeActivity.class);
		activityHolder.addActivity("Vertical Swipe", VerticalSwipeActivity.class);
		activityHolder.addActivity("Endless Swipe", EndlessSwipeActivity.class);

}
Context context;
IHomeView homeView;

	public HomePresenterCompl(Context context, IHomeView homeView) {
		this.context = context;
		this.homeView = homeView;
	}

	@Override
	public void loadDatas() {

		Handler handler = new Handler(Looper.getMainLooper());
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				homeView.onGetDataList(activityHolder.getNameList());
			}
		},2000);
	}

	@Override
	public void onItemClick(int position) {
		Class activity = activityHolder.getActivity(activityHolder.getNameList().get(position));
		if (activity!=null){
			context.startActivity(new Intent(context, activity));
		}
	}
}
