package com.kaedea.widget.swipeloadingviewdemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import com.kaedea.widget.swipeloadingview.OnSwipeListener;
import com.kaedea.widget.swipeloadingview.SwipeConstants;
import com.kaedea.widget.swipeloadingview.SwipeDetectorView;

public class EndlessSwipeActivity extends AppCompatActivity {

	int mIndex = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_endless_swipe);

		final SwipeDetectorView swipeDetectorLayout = (SwipeDetectorView) this.findViewById(R.id.swipe_loading);
		View loadingView = this.findViewById(R.id.view_loading);
		final TextView tvIndex = (TextView) this.findViewById(R.id.tv_index);
		tvIndex.setText(String.valueOf(mIndex));
		swipeDetectorLayout.setLoadingView(loadingView);
		swipeDetectorLayout.setOnSwipeListener(new OnSwipeListener() {
			@Override
			public void onSwiping(float swipeRatio, int direction) {
			}

			@Override
			public void onSwipeFinished(int direction) {
				// Toast.makeText(EndlessSwipeActivity.this, "onSwipeFinished", Toast.LENGTH_LONG).show();
				if (direction == SwipeConstants.SWIPE_TO_UP) mIndex++;
				else if (direction == SwipeConstants.SWIPE_TO_DOWN) mIndex--;
				tvIndex.setText(String.valueOf(mIndex));
				swipeDetectorLayout.hideLoadingView(false, SwipeConstants.SWIPE_UNKNOWN, null);
			}

			@Override
			public void onSwipeCanceled(int direction) {
				// Toast.makeText(EndlessSwipeActivity.this, "onSwipeCanceled", Toast.LENGTH_LONG).show();
			}
		});
		loadingView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				swipeDetectorLayout.hideLoadingView(true, swipeDetectorLayout.getDirection(), null);
			}
		});

		// swipeDetectorLayout.setAnimationDuration(10000);
	}
}
