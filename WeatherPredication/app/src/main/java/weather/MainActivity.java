package weather;

import android.app.Activity;
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
        refreshBtn = (ImageView) findViewById(R.id.refresh);
        cityName = (TextView) findViewById(R.id.city_name);
        dateTime = (TextView) findViewById(R.id.datetime);
        temperature = (TextView) findViewById(R.id.temperature);
        weather = (TextView) findViewById(R.id.weather);
        fengli = (TextView) findViewById(R.id.fengli);
        pm25 = (TextView) findViewById(R.id.pm25);
        quality = (TextView) findViewById(R.id.quality);
        city = (TextView) findViewById(R.id.city);
        publishTime = (TextView) findViewById(R.id.publish_time);
        wendu = (TextView) findViewById(R.id.wendu);
        shidu = (TextView) findViewById(R.id.shidu);
    }

    private void bindListener(){
        refreshBtn.setOnClickListener(new RefreshListener());
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
        cityName.setText(cityData.getCity() + " " + "天气");
        city.setText(cityData.getCity());
        publishTime.setText("更新时间" + " " + cityData.getUpdatetime());
        fengli.setText("风力" + "" + cityData.getFengli());
        pm25.setText(cityData.getPm25());
        quality.setText(cityData.getQuality());
        wendu.setText("温度" + " " + cityData.getWendu());
        shidu.setText("湿度" + " " + cityData.getShidu());

        Weather todayWeather = cityData.getForecast()[0];
        dateTime.setText(todayWeather.getDate());
        temperature.setText(todayWeather.getLow() + "-" + todayWeather.getHigh());
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
