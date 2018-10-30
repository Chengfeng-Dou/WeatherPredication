package app;

import android.app.Application;
import android.util.Log;

import java.util.ArrayList;

import util.CityDBUtil;
import model.CityEntity;

public class WeatherPredication extends Application {
    private static final String TAG = "db_application";
    private ArrayList<CityEntity> cityList;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate()");
        initCityMap();
    }

    private void initCityMap(){
        new Thread(){
            @Override
            public void run() {
                CityDBUtil db = CityDBUtil.openCityDB(WeatherPredication.this);
                cityList = db.getAllCity();
                for(CityEntity city: cityList){
                    Log.d(TAG, String.format("%s: %s", city.getCityName(), city.getCityCode()));
                }
            }
        }.start();
    }

    public ArrayList<CityEntity> getCityList() {
        return cityList;
    }
}
