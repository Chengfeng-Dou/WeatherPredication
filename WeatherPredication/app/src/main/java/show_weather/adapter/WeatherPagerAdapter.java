package show_weather.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.weather.douchengfeng.weatherpredication.R;

import java.util.ArrayList;

import show_weather.bean.Weather;
import show_weather.bean.WeatherDetail;
import show_weather.utils.DateUtil;
import show_weather.utils.DataAnalyzeUtil;

public class WeatherPagerAdapter extends PagerAdapter {
    private Context context;
    private ArrayList<View> views = new ArrayList<>();

    public WeatherPagerAdapter(Context context) {
        this.context = context;

    }

    public void setData(Weather[] forecast, int startPos) {
        for(int i = startPos; i < forecast.length; i ++){
            views.add(inflateViewByData(forecast[i]));
        }
    }

    @SuppressLint("InflateParams")
    private View inflateViewByData(Weather weather) {
        View view = LayoutInflater.from(context).inflate(R.layout.show_weather_forecast_item, null);
        TextView date = view.findViewById(R.id.forecast_date);
        ImageView pic = view.findViewById(R.id.forecast_pic);
        TextView wendu = view.findViewById(R.id.forecast_temperature);
        TextView fengli = view.findViewById(R.id.forecast_fengli);
        TextView fengxiang = view.findViewById(R.id.forecast_fengxiang);

        date.setText(weather.getDate());
        wendu.setText(String.format("%s-%s", weather.getLow(), weather.getHigh()));

        WeatherDetail detail = DateUtil.isDayTime()? weather.getDay(): weather.getNight();
        pic.setImageResource(DataAnalyzeUtil.getImageIdByWeather(detail.getType()));
        fengli.setText(detail.getFengli());
        fengxiang.setText(detail.getFengxiang());


        return view;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = views.get(position);
        container.addView(view);

        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }


    @Override
    public int getCount() {
        return views.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public float getPageWidth(int position) {
        return 0.34f;
    }
}
