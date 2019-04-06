package com.viseeointernational.battmon.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.viseeointernational.battmon.R;
import com.viseeointernational.battmon.data.entity.Voltage;
import com.viseeointernational.battmon.util.TimeUtil;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AbnormalChargingAdapter extends BaseAdapter {

    private Context context;
    private List<Voltage> list;

    @Inject
    public AbnormalChargingAdapter(Context context) {
        this.context = context;
        this.list = new ArrayList<>();
    }

    public void setData(List<Voltage> list) {
        this.list.clear();
        this.list.addAll(list);
        notifyDataSetChanged();
    }

    public List<Voltage> getData() {
        return list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_abnormal_charging, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Voltage item = list.get(position);
        holder.date.setText(TimeUtil.getFormatTime(item.time, "yyyy/MM/dd HH:mm"));
        holder.value.setText(String.valueOf(item.value));

        return convertView;
    }

    static class ViewHolder {
        @BindView(R.id.date)
        TextView date;
        @BindView(R.id.value)
        TextView value;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
