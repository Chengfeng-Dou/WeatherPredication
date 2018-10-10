package model;

import java.util.Arrays;
import java.util.Objects;

/**
 * Created by douchengfeng on 2018/10/10.
 *
 */

public class CityData {
    private String city;
    private String updatetime;
    private String wendu;
    private String fengli;
    private String shidu;
    private String fengxiang;
    private String pm25;
    private String quality;
    private Weather[] forecast;

    public Weather[] getForecast() {
        return forecast;
    }

    public void setForecast(Object[] forecast) {
        this.forecast = new Weather[forecast.length];
        for(int i = 0; i < forecast.length; i ++){
            this.forecast[i] = (Weather) forecast[i];
        }
    }

    public String getQuality() {
        return quality;
    }

    public void setQuality(String quality) {
        this.quality = quality;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getUpdatetime() {
        return updatetime;
    }

    public void setUpdatetime(String updatetime) {
        this.updatetime = updatetime;
    }

    public String getWendu() {
        return wendu;
    }

    public void setWendu(String wendu) {
        this.wendu = wendu;
    }

    public String getFengli() {
        return fengli;
    }

    public void setFengli(String fengli) {
        this.fengli = fengli;
    }

    public String getShidu() {
        return shidu;
    }

    public void setShidu(String shidu) {
        this.shidu = shidu;
    }

    public String getFengxiang() {
        return fengxiang;
    }

    public void setFengxiang(String fengxiang) {
        this.fengxiang = fengxiang;
    }

    public String getPm25() {
        return pm25;
    }

    public void setPm25(String pm25) {
        this.pm25 = pm25;
    }

    @Override
    public String toString() {
        return "CityData{" +
                "city='" + city + '\'' +
                ", updatetime='" + updatetime + '\'' +
                ", wendu='" + wendu + '\'' +
                ", fengli='" + fengli + '\'' +
                ", shidu='" + shidu + '\'' +
                ", fengxiang='" + fengxiang + '\'' +
                ", pm25='" + pm25 + '\'' +
                ", quality='" + quality + '\'' +
                ", forecast=" + Arrays.toString(forecast) +
                '}';
    }
}
