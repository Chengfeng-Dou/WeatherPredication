package weather;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;


import com.weather.douchengfeng.weatherpredication.R;

public class MainActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_weather);
    }
}
