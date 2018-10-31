package app;

import android.app.Application;

import java.util.ArrayList;

import util.CityDBUtil;
import model.CityEntity;

public class WeatherPredication extends Application {
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
                CityDBUtil db = CityDBUtil.openCityDB(WeatherPredication.this);
                cityList = db.getAllCity();
            }
        }.start();
    }

    public ArrayList<CityEntity> getCityList() {
        return cityList;
    }
}
