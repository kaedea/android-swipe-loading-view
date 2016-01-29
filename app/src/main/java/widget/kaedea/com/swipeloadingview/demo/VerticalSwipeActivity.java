package widget.kaedea.com.swipeloadingview.demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;
import widget.kaedea.com.swipeloadingview.EndlessSwipeDetectorView;
import widget.kaedea.com.swipeloadingview.R;

public class VerticalSwipeActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_endless_swipe);

		final EndlessSwipeDetectorView swipeDetectorLayout = (EndlessSwipeDetectorView) this.findViewById(R.id.swipe_loading);
		View loadingView = this.findViewById(R.id.view_loading);
		swipeDetectorLayout.setLoadingView(loadingView);
		swipeDetectorLayout.setOnSwipeListener(new EndlessSwipeDetectorView.OnSwipeListener() {
			@Override
			public void onSwiping(float swipeRatio, int direction) {
			}

			@Override
			public void onSwipeFinished(int direction) {
				Toast.makeText(VerticalSwipeActivity.this, "onSwipeFinished", Toast.LENGTH_LONG).show();
				swipeDetectorLayout.hideLoadingView(false, EndlessSwipeDetectorView.EndlessSwipeConstants.SWIPE_UNKNOW,null);
			}

			@Override
			public void onSwipeCanceled(int direction) {
				Toast.makeText(VerticalSwipeActivity.this, "onSwipeCanceled", Toast.LENGTH_LONG).show();
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
