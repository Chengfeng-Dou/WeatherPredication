package show_weather;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
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
import show_weather.adapter.WeatherPagerAdapter;
import show_weather.bean.CityData;
import show_weather.bean.CityEntity;
import show_weather.bean.Weather;
import show_weather.bean.WeatherDetail;
import show_weather.utils.DateUtil;
import show_weather.utils.LocationUtil;
import show_weather.utils.MsgFlag;
import show_weather.utils.NetUtil;
import show_weather.utils.DataAnalyzeUtil;

public class ShowWeatherActivity extends Activity {
    private ImageView refreshBtn;
    private ImageView cityManage;
    private TextView cityName;
    private TextView dateTime;
    private TextView temperature;
    private TextView fengxiang;
    private TextView fengli;
    private TextView pm25;
    private TextView quality;
    private TextView city;
    private TextView publishTime;
    private TextView wendu;
    private TextView shidu;
    private ImageView locationBtn;
    private ImageView weatherImg;


    private Handler dataHandler = new RefreshDateReceiver(this);
    private DataAnalyzeUtil parser = new DataAnalyzeUtil();
    private SharedPreferences sharedPreferences;

    private volatile boolean isRefreshing = false;
    private Animation rotate;
    private Timer refreshTimer;
    private ViewPager viewPager;

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
        fengxiang = findViewById(R.id.fengxiang);
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
        viewPager = findViewById(R.id.forecast);
        weatherImg = findViewById(R.id.today_weather);
    }

    private void initEvents() {
        refreshBtn.setOnClickListener(new RefreshListener());
        cityManage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent selectCity = new Intent(ShowWeatherActivity.this, PickCityActivity.class);
                selectCity.putExtra("currentCity", city.getText());
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
        if (isRefreshing) {
            return true;
        }
        isRefreshing = true;


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
        isRefreshing = false;

        // 将计时的 timer 关闭，由于timer取消后不能够再次使用，所以手动置为null，释放资源，防止MemoryLeak
        if (refreshTimer != null) {
            refreshTimer.cancel();
            refreshTimer = null;
        }

        // 停止旋转
        refreshBtn.clearAnimation();

    }


    private void sendCityCodeToNetUtil(String cityCode) {
        if (NetUtil.netWorkIsOk(ShowWeatherActivity.this)) {
            NetUtil.getDataFromUrl(String.format("http://wthrcdn.etouch.cn/WeatherApi?citykey=%s", cityCode), MsgFlag.REFRESH, dataHandler);
        }
    }

    private void refreshData(String data) {
        CityData cityData = parser.parseSimpleObjectFromXML(CityData.class, data);
        refreshTodayWeather(cityData);


        final WeatherPagerAdapter adapter = new WeatherPagerAdapter(this);
        adapter.setData(cityData.getForecast(), 1);
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            @Override
            public void onPageSelected(int position) {
                adapter.setDotIsSelected(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {}
        });


        setCanRefreshNow();
        Toast.makeText(this, "天气更新成功", Toast.LENGTH_SHORT).show();
    }

    private void refreshTodayWeather(CityData cityData){
        cityName.setText(String.format("%s 天气", cityData.getCity()));
        city.setText(cityData.getCity());
        publishTime.setText(String.format("更新时间 %s", cityData.getUpdatetime()));
        fengli.setText(String.format("风力%s", cityData.getFengli()));
        pm25.setText(cityData.getPm25());
        quality.setText(cityData.getQuality());
        wendu.setText(String.format("温度 %s", cityData.getWendu()));
        shidu.setText(String.format("湿度 %s", cityData.getShidu()));

        if(cityData.getForecast() == null){
            return;
        }
        Weather todayWeather = cityData.getForecast()[0];
        dateTime.setText(todayWeather.getDate());
        temperature.setText(String.format("%s-%s", todayWeather.getLow(), todayWeather.getHigh()));

        WeatherDetail detail = DateUtil.isDayTime() ? todayWeather.getDay() : todayWeather.getNight();
        fengxiang.setText(detail.getFengxiang());
        weatherImg.setImageResource(DataAnalyzeUtil.getImageIdByWeather(detail.getType()));
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
