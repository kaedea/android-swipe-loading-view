package widget.kaedea.com.swipeloadingview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SwipeDetectorLayout swipeDetectorLayout = (SwipeDetectorLayout) this.findViewById(R.id.swipe_loading);
        View loadingView = this.findViewById(R.id.view_loading);
        swipeDetectorLayout.setViewLoading(loadingView);
//        swipeDetectorLayout.resetLoadingView(false);
    }
}
