package com.viseeointernational.battmon.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.viseeointernational.battmon.R;
import com.viseeointernational.battmon.data.constant.ConnectionType;
import com.viseeointernational.battmon.data.entity.Device;
import com.viseeointernational.battmon.view.custom.RSSIView;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DeviceAdapter extends BaseAdapter {

    private Context context;
    private List<Device> list;

    @Inject
    public DeviceAdapter(Context context) {
        this.context = context;
        this.list = new ArrayList<>();
    }

    public void setData(List<Device> list) {
        this.list.clear();
        this.list.addAll(list);
        notifyDataSetChanged();
    }

    public List<Device> getData() {
        return list;
    }

    public Device getData(int position) {
        if (position <= list.size() - 1) {
            return list.get(position);
        }
        return null;
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
            convertView = LayoutInflater.from(context).inflate(R.layout.item_device, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Device device = list.get(position);
        holder.name.setText(device.name);
        holder.rssi.setRssi(device.rssi);
        switch (device.connectionState) {
            case ConnectionType.CONNECTED:
                holder.state.setTextColor(context.getResources().getColor(R.color.theme));
                holder.state.setText(R.string.connected);
                break;
            case ConnectionType.CONNECTING:
                holder.state.setTextColor(context.getResources().getColor(R.color.theme));
                holder.state.setText(R.string.connecting);
                break;
            case ConnectionType.DISCONNECTED:
            default:
                holder.state.setTextColor(context.getResources().getColor(R.color.textDark));
                holder.state.setText(R.string.disconnected);
                break;
        }
        return convertView;
    }

    static class ViewHolder {
        @BindView(R.id.name)
        TextView name;
        @BindView(R.id.rssi)
        RSSIView rssi;
        @BindView(R.id.state)
        TextView state;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
