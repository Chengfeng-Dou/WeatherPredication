package activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.weather.douchengfeng.weatherpredication.R;


import model.CityData;
import model.Weather;
import util.DateUtil;
import util.MsgFlag;
import util.NetUtil;
import util.XmlParser;

public class MainActivity extends Activity {
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


    private Handler dataHandler = new RefreshDateReceiver(this);
    private XmlParser parser = new XmlParser();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_weather);
        NetUtil.toastNetworkState(this);

        bindViews();
        bindListener();
    }


    private void bindViews(){
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
    }

    private void bindListener(){
        refreshBtn.setOnClickListener(new RefreshListener());
        cityManage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent selectCity = new Intent(MainActivity.this, SelectCityActivity.class);
                startActivity(selectCity);
            }
        });
    }


    private class RefreshListener implements View.OnClickListener{
        private SharedPreferences sharedPreferences = getSharedPreferences("config", MODE_PRIVATE);

        @Override
        public void onClick(View v) {
            String cityCode = sharedPreferences.getString("main_city_code", "101010100");
            Log.d("myWeather", cityCode);

            if(NetUtil.netWorkIsOk(MainActivity.this)){
                NetUtil.getDataFromUrl("http://wthrcdn.etouch.cn/WeatherApi?citykey=" + cityCode, MsgFlag.REFRESH, dataHandler);
            }
        }


    }

    private void refreshData(String data){
        CityData cityData = parser.parseSimpleObjectFromXML(CityData.class, data);
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
        if(DateUtil.isDayTime()){
            weather.setText(todayWeather.getDay().getFengxiang());
        }else{
            weather.setText(todayWeather.getNight().getFengxiang());
        }



        Log.d("myWeather", cityData.toString());
    }



    private static class RefreshDateReceiver extends Handler{
        private MainActivity mainActivity;

        RefreshDateReceiver(MainActivity mainActivity) {
            this.mainActivity = mainActivity;
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case MsgFlag.REFRESH:

                    String response = (String) msg.obj;
                    mainActivity.refreshData(response);

                    break;
                default:
                    break;
            }
        }
    }

}
