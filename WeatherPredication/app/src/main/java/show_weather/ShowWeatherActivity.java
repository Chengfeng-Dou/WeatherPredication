package show_weather;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.weather.douchengfeng.weatherpredication.R;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import pick_city.PickCityActivity;
import show_weather.bean.CityData;
import show_weather.bean.CityEntity;
import show_weather.bean.Weather;
import show_weather.utils.DateUtil;
import show_weather.utils.LocationUtil;
import show_weather.utils.MsgFlag;
import show_weather.utils.NetUtil;
import show_weather.utils.XmlUtil;

public class ShowWeatherActivity extends Activity {
    private ImageView refreshBtn;
    private ImageView cityManage;
    private TextView cityName;
    private TextView dateTime;
    private TextView temperature;
    private TextView weather;
    private TextView fengli;
    private TextView pm25;
    private TextView quality;
    private TextView city;
    private TextView publishTime;
    private TextView wendu;
    private TextView shidu;
    private ImageView locationBtn;


    private Handler dataHandler = new RefreshDateReceiver(this);
    private XmlUtil parser = new XmlUtil();
    private SharedPreferences sharedPreferences;
    private CityData cityData;

    private volatile boolean isRefreshing = false;
    private Animation rotate;
    private Timer refreshTimer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_weather);
        NetUtil.toastNetworkState(this);
        sharedPreferences = getSharedPreferences("config", MODE_PRIVATE);

        initViews();
        initEvents();
        refreshWeatherOfCurrentCity();
    }


    private void initViews() {
        refreshBtn = findViewById(R.id.refresh);
        cityName = findViewById(R.id.city_name);
        dateTime = findViewById(R.id.datetime);
        temperature = findViewById(R.id.temperature);
        weather = findViewById(R.id.weather);
        fengli = findViewById(R.id.fengli);
        pm25 = findViewById(R.id.pm25);
        quality = findViewById(R.id.quality);
        city = findViewById(R.id.city);
        publishTime = findViewById(R.id.publish_time);
        wendu = findViewById(R.id.wendu);
        shidu = findViewById(R.id.shidu);
        cityManage = findViewById(R.id.title_city_manager);
        rotate = AnimationUtils.loadAnimation(this, R.anim.rotate_anim);
        locationBtn = findViewById(R.id.m_cur_location);
    }

    private void initEvents() {
        refreshBtn.setOnClickListener(new RefreshListener());
        cityManage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent selectCity = new Intent(ShowWeatherActivity.this, PickCityActivity.class);
                selectCity.putExtra("currentCity", cityData.getCity());
                startActivityForResult(selectCity, 2);
            }
        });

        locationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //  在获取信息的时候先要上锁，避免重复获取
                if (failedToSetCannotRefreshNow()) return;

                LocationUtil locationUtil = LocationUtil.getInstance(ShowWeatherActivity.this);
                String city = locationUtil.getCurrentLocation();
                if (city == null) {
                    return;
                }

                WeatherPredicationApp app = (WeatherPredicationApp) getApplication();
                List<CityEntity> cityEntities = app.getCityList();

                //  通过遍历城市列表，找到当前城市的cityCode，并刷新
                for (CityEntity cityEntity : cityEntities) {
                    if (cityEntity.getCityName().equals(city)) {
                        String cityCode = cityEntity.getCityCode();
                        setCurrentCity(cityCode);
                        refreshWeatherByCityCode(cityCode);
                    }
                }
            }
        });
    }

    private void setCurrentCity(String cityCode) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("main_city_code", cityCode);
        editor.apply();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        String cityCode = data.getStringExtra("cityCode");
        if (cityCode.equals("null")) {
            return;
        }

        if (!failedToSetCannotRefreshNow()) {
            refreshWeatherByCityCode(cityCode);
        }

        setCurrentCity(cityCode);
    }


    private class RefreshListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            refreshWeatherOfCurrentCity();
        }
    }

    private void refreshWeatherOfCurrentCity() {
        if (failedToSetCannotRefreshNow()) return;
        String cityCode = sharedPreferences.getString("main_city_code", "101010100");
        refreshWeatherByCityCode(cityCode);
    }

    private void refreshWeatherByCityCode(String cityCode) {
        sendCityCodeToNetUtil(cityCode);
    }

    private boolean failedToSetCannotRefreshNow() {
        // 使用synchronized关键字处理线程并发
        // isRefreshing 定义如下： private volatile boolean isRefreshing = false;
        synchronized (ShowWeatherActivity.class) {
            if (isRefreshing) {
                return true;
            }
            isRefreshing = true;
        }

        // 如果上锁成功，那么设置一个timer，防止获取信息的时候卡死，锁无法解除的情况
        refreshTimer = new Timer();
        refreshTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                // 停止调度
                Message stop = new Message();
                stop.what = MsgFlag.STOP_REFRESH;
                dataHandler.sendMessage(stop);

                Message toast = new Message();
                toast.what = MsgFlag.TOAST;
                toast.obj = "天气获取失败";
                dataHandler.sendMessage(toast);
            }
        }, 5000);

        // 让更新图标旋转
        refreshBtn.startAnimation(rotate);
        return false;
    }


    private void setCanRefreshNow() {
        synchronized (ShowWeatherActivity.class) {
            isRefreshing = false;

            // 将计时的 timer 关闭，由于timer取消后不能够再次使用，所以手动置为null，释放资源，防止MemoryLeak
            if (refreshTimer != null) {
                refreshTimer.cancel();
                refreshTimer = null;
            }

            // 停止旋转
            refreshBtn.clearAnimation();
        }
    }


    private void sendCityCodeToNetUtil(String cityCode) {
        if (NetUtil.netWorkIsOk(ShowWeatherActivity.this)) {
            NetUtil.getDataFromUrl(String.format("http://wthrcdn.etouch.cn/WeatherApi?citykey=%s", cityCode), MsgFlag.REFRESH, dataHandler);
        }
    }

    private void refreshData(String data) {
        cityData = parser.parseSimpleObjectFromXML(CityData.class, data);
        cityName.setText(String.format("%s 天气", cityData.getCity()));
        city.setText(cityData.getCity());
        publishTime.setText(String.format("更新时间 %s", cityData.getUpdatetime()));
        fengli.setText(String.format("风力%s", cityData.getFengli()));
        pm25.setText(cityData.getPm25());
        quality.setText(cityData.getQuality());
        wendu.setText(String.format("温度 %s", cityData.getWendu()));
        shidu.setText(String.format("湿度 %s", cityData.getShidu()));

        Weather todayWeather = cityData.getForecast()[0];
        dateTime.setText(todayWeather.getDate());
        temperature.setText(String.format("%s-%s", todayWeather.getLow(), todayWeather.getHigh()));
        if (DateUtil.isDayTime()) {
            weather.setText(todayWeather.getDay().getFengxiang());
        } else {
            weather.setText(todayWeather.getNight().getFengxiang());
        }

        setCanRefreshNow();
        Toast.makeText(this, "天气更新成功", Toast.LENGTH_SHORT).show();
    }


    private static class RefreshDateReceiver extends Handler {
        private ShowWeatherActivity mainActivity;

        RefreshDateReceiver(ShowWeatherActivity mainActivity) {
            this.mainActivity = mainActivity;
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MsgFlag.REFRESH:

                    String response = (String) msg.obj;
                    mainActivity.refreshData(response);
                    break;

                case MsgFlag.STOP_REFRESH:

                    mainActivity.setCanRefreshNow();
                    break;

                case MsgFlag.TOAST:

                    String info = (String) msg.obj;
                    Toast.makeText(mainActivity, info, Toast.LENGTH_SHORT).show();
                    break;

                default:
                    break;
            }
        }
    }

}
