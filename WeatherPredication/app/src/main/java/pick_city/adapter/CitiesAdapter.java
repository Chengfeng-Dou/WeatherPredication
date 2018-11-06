package pick_city.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.weather.douchengfeng.weatherpredication.R;

import java.util.ArrayList;
import java.util.List;

import pick_city.bean.CitiesBean;


/**
 * Created by kun on 2016/10/26.
 */
public class CitiesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private ArrayList<DataHolder> dataHolders;

    private final int HEAD = 0;
    private final int WORD = 1;
    private final int CITY = 2;

    public CitiesAdapter(Context context, List<CitiesBean.DataBean> cities) {
        this.context = context;
        initDataHolder(cities);
    }

    private void initDataHolder(List<CitiesBean.DataBean> cities) {
        this.dataHolders = new ArrayList<>(cities.size() + 26);
        DataHolder head = new DataHolder();
        head.type = HEAD;
        dataHolders.add(head);

        for (CitiesBean.DataBean datesBean : cities) {
            DataHolder word = new DataHolder();
            word.type = WORD;
            word.content = datesBean.getAlifName();
            dataHolders.add(word);

            List<CitiesBean.DataBean.AddressListBean> addressList = datesBean.getAddressList();
            for (CitiesBean.DataBean.AddressListBean addressListBean : addressList) {
                DataHolder city = new DataHolder();
                city.type = CITY;
                city.content = addressListBean;
                dataHolders.add(city);
            }
        }
    }

    @Override
    public int getItemCount() {
        return dataHolders.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (position < dataHolders.size()) {
            return dataHolders.get(position).type;
        }
        return super.getItemViewType(position);
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case HEAD:
                View head = LayoutInflater.from(context).inflate(R.layout.pick_city_layout_head, parent, false);
                return new HeadViewHolder(head);
            case WORD:
                View word = LayoutInflater.from(context).inflate(R.layout.pick_city_layout_word, parent, false);
                return new WordViewHolder(word);
            case CITY:
                View city = LayoutInflater.from(context).inflate(R.layout.pick_city_layout_city, parent, false);
                return new CityViewHolder(city);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        DataHolder dataHolder = dataHolders.get(position);
        switch (dataHolder.type) {
            case WORD:
                WordViewHolder wordViewHolder = (WordViewHolder) holder;
                wordViewHolder.textWord.setText((String) dataHolder.content);
                return;
            case CITY:
                CityViewHolder cityViewHolder = (CityViewHolder) holder;
                final CitiesBean.DataBean.AddressListBean addressListBean =
                        (CitiesBean.DataBean.AddressListBean) dataHolder.content;
                cityViewHolder.textCity.setText(addressListBean.getName());
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        intent.putExtra("cityCode", String.valueOf(addressListBean.getId()));
                        Activity activity = (Activity) context;
                        activity.setResult(2, intent);
                        activity.finish();
                    }
                });
        }
    }

    private static class DataHolder {
        int type;
        Object content;
    }


    public static class HeadViewHolder extends RecyclerView.ViewHolder {
        HeadViewHolder(View view) {
            super(view);
        }
    }

    public static class WordViewHolder extends RecyclerView.ViewHolder {
        TextView textWord;

        WordViewHolder(View view) {
            super(view);
            textWord = view.findViewById(R.id.textWord);
        }
    }

    public static class CityViewHolder extends RecyclerView.ViewHolder {

        TextView textCity;

        CityViewHolder(View view) {
            super(view);
            textCity = view.findViewById(R.id.textCity);
        }
    }
}
