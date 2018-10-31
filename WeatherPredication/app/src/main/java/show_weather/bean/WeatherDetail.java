package show_weather.bean;

/**
 * Created by douchengfeng on 2018/10/10.
 *
 */

public class WeatherDetail {
    String type;
    String fengxiang;
    String fengli;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFengxiang() {
        return fengxiang;
    }

    public void setFengxiang(String fengxiang) {
        this.fengxiang = fengxiang;
    }

    public String getFengli() {
        return fengli;
    }

    public void setFengli(String fengli) {
        this.fengli = fengli;
    }

    @Override
    public String toString() {
        return "WeatherDetail{" +
                "type='" + type + '\'' +
                ", fengxiang='" + fengxiang + '\'' +
                ", fengli='" + fengli + '\'' +
                '}';
    }
}
