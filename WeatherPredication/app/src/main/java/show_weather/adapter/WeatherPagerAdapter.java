package show_weather.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.weather.douchengfeng.weatherpredication.R;

import java.util.ArrayList;

import show_weather.bean.Weather;
import show_weather.bean.WeatherDetail;
import show_weather.utils.DataAnalyzeUtil;
import show_weather.utils.DateUtil;

public class WeatherPagerAdapter extends PagerAdapter {
    private Context context;
    private ArrayList<View> views = new ArrayList<>();
    private LinearLayout dotView;
    private int selectPos = 0;

    public WeatherPagerAdapter(Context context) {
        this.context = context;
        Activity activity = (Activity) context;
        dotView = activity.findViewById(R.id.dot_view);
        dotView.removeAllViews();
    }

    public void setData(Weather[] forecast, int startPos) {
        if(forecast == null){
            return;
        }
        for (int i = startPos; i < forecast.length; i++) {
            views.add(inflateViewByData(forecast[i]));
        }

        for (int i = 0; i < (((forecast.length - startPos) / 3) + 1); i++) {
            addDot(i == 0);
        }
    }

    public void setDotIsSelected(int pos) {
        int temp = pos / 3;
        if(pos % 3 >= 1) temp++; //当下一页能够显示两个的时候就算翻到下一页了
        pos = temp;


        ImageView select = (ImageView) dotView.getChildAt(pos);
        select.setImageResource(R.drawable.d_select);

        if(selectPos == pos){
            return;
        }

        ImageView unSelect = (ImageView) dotView.getChildAt(selectPos);
        unSelect.setImageResource(R.drawable.d_unselect);

        selectPos = pos;
    }

    private void addDot(boolean isFirst) {
        ImageView dot = new ImageView(context);
        dot.setMaxWidth(10);
        dot.setMaxHeight(10);
        if (isFirst) {
            dot.setImageResource(R.drawable.d_select);
        } else {
            dot.setImageResource(R.drawable.d_unselect);
        }

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(30, 30);
        params.leftMargin = 3;
        params.rightMargin = 3;
        dotView.addView(dot, params);
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

        WeatherDetail detail = DateUtil.isDayTime() ? weather.getDay() : weather.getNight();
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
