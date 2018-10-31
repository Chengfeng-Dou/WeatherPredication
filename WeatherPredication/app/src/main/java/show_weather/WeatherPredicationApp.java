package show_weather;

import android.app.Application;

import java.util.ArrayList;

import show_weather.utils.CityDBUtil;
import show_weather.bean.CityEntity;

public class WeatherPredicationApp extends Application {
    private ArrayList<CityEntity> cityList;

    @Override
    public void onCreate() {
        super.onCreate();
        initCityMap();
    }

    private void initCityMap() {
        new Thread() {
            @Override
            public void run() {
                CityDBUtil db = CityDBUtil.openCityDB(WeatherPredicationApp.this);
                cityList = db.getAllCity();
            }
        }.start();
    }

    public ArrayList<CityEntity> getCityList() {
        return cityList;
    }
}
