package pick_city;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import com.github.promeg.pinyinhelper.Pinyin;
import com.weather.douchengfeng.weatherpredication.R;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

import show_weather.WeatherPredicationApp;
import show_weather.bean.CityEntity;
import pick_city.adapter.CitiesAdapter;
import pick_city.bean.CitiesBean;
import pick_city.view.QuickIndexView;

/**
 * Created by kun on 2016/10/26.
 */
public class PickCityActivity extends Activity {

    private QuickIndexView quickIndexView;
    private RecyclerView recyclerView;
    private List<CityEntity> cityEntities;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pick_city);
        initViews();
        initEvents();

    }

    private void initViews() {
        initTitleBar();
        initSearchView();
        initIndexView();
        initRecycleView();
    }

    private void initTitleBar() {
        ImageView back = findViewById(R.id.back);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra("cityCode", "null");
                setResult(2, intent);
                finish();
            }
        });
    }

    private void initRecycleView() {
        recyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        cityEntities = ((WeatherPredicationApp) getApplication()).getCityList();
        String cityName = getIntent().getStringExtra("currentCity");
        ((TextView) findViewById(R.id.cur_city)).setText(String.format("当前城市: %s", cityName));

        setAdapter(cityEntities);
    }

    private void initIndexView() {
        quickIndexView = findViewById(R.id.quickIndexView);
    }

    private void initSearchView() {
        searchView = findViewById(R.id.search_city);

        int textViewId = getResources().getIdentifier("android:id/search_src_text", null, null);
        TextView textView = searchView.findViewById(textViewId);
        textView.setTextColor(Color.BLACK);//字体颜色
        textView.setTextSize(20);//字体、提示字体大小
        textView.setHintTextColor(Color.GRAY);//提示字体颜色**
        textView.setHint("请输入城市名称"); // 设置提示语
        searchView.setIconified(false);

        int closeBtnId = getResources().getIdentifier("android:id/search_close_btn", null, null);
        ImageView closeBtn = searchView.findViewById(closeBtnId);
        closeBtn.setVisibility(View.INVISIBLE); //使关闭按钮不可见
    }


    private void setAdapter(List<CityEntity> cityEntities) {
        List<CitiesBean.DataBean> data = entity2DataBean(cityEntities);
        CitiesAdapter adapter = new CitiesAdapter(this, data);
        recyclerView.setAdapter(adapter);
        quickIndexView.setIndexData(data);
    }

    private List<CitiesBean.DataBean> entity2DataBean(List<CityEntity> cityEntities) {
        List<CitiesBean.DataBean> dataBeans = new LinkedList<>();

        String preAlf = "";
        CitiesBean.DataBean curBean;
        List<CitiesBean.DataBean.AddressListBean> addressListBeans = new LinkedList<>();

        for (CityEntity cityEntity : cityEntities) {
            String curAlf = cityEntity.getPinyin();
            if (!curAlf.equals(preAlf)) {
                curBean = new CitiesBean.DataBean();
                curBean.setAlifName(curAlf);

                addressListBeans = new LinkedList<>();
                curBean.setAddressList(addressListBeans);
                dataBeans.add(curBean);
                preAlf = curAlf;
            }

            CitiesBean.DataBean.AddressListBean addressListBean = new CitiesBean.DataBean.AddressListBean();
            addressListBean.setId(Integer.parseInt(cityEntity.getCityCode()));
            addressListBean.setName(cityEntity.getCityName());

            addressListBeans.add(addressListBean);
        }

        return dataBeans;
    }

    private void initEvents() {
        quickIndexView.setOnIndexChangeListener(new QuickIndexView.OnIndexChangeListener() {
            @Override
            public void onIndexChange(String words) {
                List<CitiesBean.DataBean> data = quickIndexView.getIndex();
                if (data != null && data.size() > 0) {
                    int count = 0;
                    for (CitiesBean.DataBean dataBean : data) {
                        if (dataBean.getAlifName().equals(words)) {
                            LinearLayoutManager llm = (LinearLayoutManager) recyclerView
                                    .getLayoutManager();
                            llm.scrollToPositionWithOffset(count + 1, 0);
                            return;
                        }
                        count += dataBean.getAddressList().size() + 1;
                    }
                }
            }
        });


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            private Pattern inputFilter = Pattern.compile("([\\u4e00-\\u9fa5]|[a-z]|[A-Z])*");

            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                newText = newText.replace(" ", "");

                if (newText.length() == 0) {
                    setAdapter(cityEntities);
                    return true;
                }

                List<CityEntity> target = new LinkedList<>();

                if (!inputFilter.matcher(newText).matches()) { // 如果输入包含汉字和字母意外的字符，则显示空
                    setAdapter(target);
                    return true;
                }

                for (CityEntity entity : cityEntities) {
                    if (matchName(newText, entity.getCityName())) {
                        target.add(entity);
                    }
                }

                setAdapter(target);

                return true;
            }

            private boolean matchName(String text, String cityName) {
                text = text.toLowerCase();
                if (cityName.contains(text)) {
                    return true;
                }

                int textPtr = -1, namePtr = -1;
                while (textPtr < text.length() - 1 && namePtr < cityName.length() - 1) {
                    textPtr++;
                    namePtr++;

                    if (text.charAt(textPtr) == cityName.charAt(namePtr)) {   // 如果汉字相同
                        continue;
                    }

                    String pinyin = Pinyin.toPinyin(cityName.charAt(namePtr)).toLowerCase();

                    if (text.startsWith(pinyin)) { // 如果包含全拼
                        textPtr += pinyin.length() - 1;
                        continue;
                    }

                    if ('a' <= text.charAt(textPtr) && text.charAt(textPtr) <= 'z') { // 如果包含拼音首字母
                        if (text.charAt(textPtr) == pinyin.charAt(0)) {
                            continue;
                        }
                    }

                    return false;
                }

                return true;
            }
        });
    }
}
