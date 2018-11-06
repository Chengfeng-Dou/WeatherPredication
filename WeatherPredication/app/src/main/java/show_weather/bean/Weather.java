package show_weather.bean;

/**
 * Created by douchengfeng on 2018/10/10.
 */

public class Weather {
    private String date;
    private String high;
    private String low;
    private WeatherDetail day;
    private WeatherDetail night;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getHigh() {
        return high;
    }

    public void setHigh(String high) {
        this.high = high.replace("高温", "");
    }

    public String getLow() {
        return low;
    }

    public void setLow(String low) {
        this.low = low.replace("低温", "");
    }

    public WeatherDetail getDay() {
        return day;
    }

    public void setDay(WeatherDetail day) {
        this.day = day;
    }

    public WeatherDetail getNight() {
        return night;
    }

    public void setNight(WeatherDetail night) {
        this.night = night;
    }

    @Override
    public String toString() {
        return "Weather{" +
                "date='" + date + '\'' +
                ", high='" + high + '\'' +
                ", low='" + low + '\'' +
                ", day=" + day +
                ", night=" + night +
                '}';
    }
}
