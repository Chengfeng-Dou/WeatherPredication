package app;

import android.app.Application;
import android.util.Log;

import java.util.List;

import db.CityDB;
import model.City;

public class WeatherPredication extends Application {
    private static final String TAG = "db_application";
    private List<City> cityList;

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
                CityDB db = CityDB.openCityDB(WeatherPredication.this);
                cityList = db.getAllCity();
                for(City city: cityList){
                    Log.d(TAG, String.format("%s: %s", city.getCityName(), city.getCityCode()));
                }
            }
        }.start();
    }

}
