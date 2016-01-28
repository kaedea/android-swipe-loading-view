package widget.kaedea.com.swipeloadingview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final SwipeDetectorLayout swipeDetectorLayout = (SwipeDetectorLayout) this.findViewById(R.id.swipe_loading);
        View loadingView = this.findViewById(R.id.view_loading);
        swipeDetectorLayout.setLoadingView(loadingView);
        swipeDetectorLayout.setOnSwipeListener(new SwipeDetectorLayout.OnSwipeListener() {
            @Override
            public void onSwipping(float swipeRatio) {
            }

            @Override
            public void onSwipeFinished() {
                Toast.makeText(MainActivity.this,"onSwipeFinished",Toast.LENGTH_LONG).show();
            }

            @Override
            public void onSwipeCanceled() {
                Toast.makeText(MainActivity.this,"onSwipeCanceled",Toast.LENGTH_LONG).show();
            }
        });
        loadingView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                swipeDetectorLayout.hideLoadingView(true);
            }
        });
    }
}
