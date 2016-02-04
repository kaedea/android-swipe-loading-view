package com.kaedea.widget.swipeloadingviewdemo;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.kaedea.widget.swipeloadingview.OnSwipeListener;
import com.kaedea.widget.swipeloadingview.SwipeConstants;
import com.kaedea.widget.swipeloadingview.SwipeDetectorView;

public class EndlessSwipeActivity extends AppCompatActivity {

	int mIndex = 0;
	private View pbMain;
	LoadingTask mLoadingTask;
	private Handler handler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_endless_swipe);

		pbMain = this.findViewById(R.id.pb_main);
		handler = new Handler();
		mLoadingTask = new LoadingTask();
		this.findViewById(R.id.layout_loadingcontent).setVisibility(View.INVISIBLE);

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

				// do loading job
				handler.removeCallbacks(mLoadingTask);
				pbMain.setVisibility(View.VISIBLE);
				handler.postDelayed(mLoadingTask, 2000);
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

		swipeDetectorLayout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(EndlessSwipeActivity.this, "onClick", Toast.LENGTH_SHORT).show();
			}
		});
	}

	public class LoadingTask implements Runnable{

		@Override
		public void run() {
			Toast.makeText(EndlessSwipeActivity.this, "Loading Success!", Toast.LENGTH_SHORT).show();
			pbMain.setVisibility(View.INVISIBLE);
		}
	}
}
