package com.kaedea.widget.swipeloadingviewdemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;
import com.kaedea.widget.swipeloadingview.SwipeDetectorView;

public class VerticalSwipeActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_endless_swipe);

		final SwipeDetectorView swipeDetectorLayout = (SwipeDetectorView) this.findViewById(R.id.swipe_loading);
		View loadingView = this.findViewById(R.id.view_loading);
		swipeDetectorLayout.setLoadingView(loadingView);
		swipeDetectorLayout.setOnSwipeListener(new BaseOnSwipeListenerImpl() {


			@Override
			public void onSwipeFinish(int direction) {
				Toast.makeText(VerticalSwipeActivity.this, "onSwipeFinished", Toast.LENGTH_SHORT).show();
			}


			@Override
			public void onSwipeCancel(int direction) {
				Toast.makeText(VerticalSwipeActivity.this, "onSwipeCanceled", Toast.LENGTH_SHORT).show();
			}
		});
		loadingView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				swipeDetectorLayout.hideLoadingView(true, swipeDetectorLayout.getDirection(),null);
			}
		});
	}
}
