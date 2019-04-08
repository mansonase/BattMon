package com.viseeointernational.battmon.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.viseeointernational.battmon.R;
import com.viseeointernational.battmon.data.entity.Trip;
import com.viseeointernational.battmon.util.TimeUtil;
import com.viseeointernational.battmon.view.custom.TripView;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TripAdapter extends BaseAdapter {

    private Context context;
    private List<Trip> list;

    private int openPosition = -1;
    private Callback callback;

    @Inject
    public TripAdapter(Context context) {
        this.context = context;
        this.list = new ArrayList<>();
    }

    public void setData(List<Trip> list) {
        openPosition = -1;
        this.list.clear();
        this.list.addAll(list);
        notifyDataSetChanged();
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
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
            convertView = LayoutInflater.from(context).inflate(R.layout.item_trip, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.trip.showDetail(openPosition == position);
        Trip trip = list.get(position);
        holder.trip.setStart(TimeUtil.getFormatTime(trip.startTime, "HH:mm"));
        holder.trip.setEnd(TimeUtil.getFormatTime(trip.endTime, "HH:mm"));

        long d = trip.endTime - trip.startTime;
        int hours = (int) (d / (60000L * 60));
        int minutes = (int) (d - hours * 60000L * 60) / 60000;
        int seconds = (int) (d - hours * 60000L * 60 - minutes * 60000L) / 1000;
        String duration = "";
        if (hours > 0) {
            duration = hours + "hour";
        }
        if (minutes > 0) {
            duration += minutes + "min";
        }
        if (seconds > 0) {
            duration += seconds + "s";
        }
        holder.trip.setDuration(duration);
        if (trip.voltage != null) {
            holder.trip.setStateVoltage(trip.voltage.state);
            holder.trip.setVoltageValue(trip.voltage.value + "v");
        }
        if (trip.cranking != null) {
            holder.trip.setStateCranking(trip.cranking.state);
            holder.trip.setCrankingValue(trip.cranking.minValue + "v");
        }
        if (trip.charging != null) {
            holder.trip.setStateCharging(trip.charging.state);
        }
        holder.trip.setTag(position);
        holder.trip.setListener(listener);
        return convertView;
    }

    private TripView.Listener listener = new TripView.Listener() {
        @Override
        public void onGraphVoltageClick(TripView view) {
            Object tag = view.getTag();
            if (tag instanceof Integer) {
                int position = (int) tag;
                if (callback != null) {
                    callback.onGraphVoltageClick(TripAdapter.this, list.get(position));
                }
            }
        }

        @Override
        public void onGraphCrankingClick(TripView view) {
            Object tag = view.getTag();
            if (tag instanceof Integer) {
                int position = (int) tag;
                if (callback != null) {
                    callback.onGraphCrankingClick(TripAdapter.this, list.get(position));
                }
            }
        }

        @Override
        public void onOpenDetail(TripView view) {
            Object tag = view.getTag();
            if (tag instanceof Integer) {
                openPosition = (int) tag;
                notifyDataSetChanged();
            }
        }
    };

    static class ViewHolder {
        @BindView(R.id.trip)
        TripView trip;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    public interface Callback {

        void onGraphVoltageClick(TripAdapter adapter, Trip trip);

        void onGraphCrankingClick(TripAdapter adapter, Trip trip);
    }
}
