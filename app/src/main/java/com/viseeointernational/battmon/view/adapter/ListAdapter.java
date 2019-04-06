package com.viseeointernational.battmon.view.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.viseeointernational.battmon.R;
import com.viseeointernational.battmon.data.constant.StateType;
import com.viseeointernational.battmon.data.entity.Device;
import com.viseeointernational.battmon.view.custom.FlashView;
import com.viseeointernational.battmon.view.custom.RoundCornerImageView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_SHOW = 1;
    private static final int TYPE_EDIT = 2;
    private static final int TYPE_EMPTY = 3;

    private int editPosition = -1;
    private String editName = "";

    private Context context;
    private List<Device> list;

    private Callback callback;

    @Inject
    public ListAdapter(Context context) {
        this.context = context;
        this.list = new ArrayList<>();
    }

    public void setData(List<Device> list) {
        this.list.clear();
        this.list.addAll(list);
        this.list.add(new Device(""));
        notifyDataSetChanged();
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public void clearEdit() {
        editPosition = -1;
        editName = "";
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == editPosition) {
            return TYPE_EDIT;
        } else if (position == list.size() - 1) {
            return TYPE_EMPTY;
        }
        return TYPE_SHOW;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        switch (i) {
            case TYPE_SHOW:
            default:
                View view = LayoutInflater.from(context).inflate(R.layout.item_list_show, null);
                return new ViewHolderShow(view);

            case TYPE_EDIT:
                view = LayoutInflater.from(context).inflate(R.layout.item_list_edit, null);
                return new ViewHolderEdit(view);

            case TYPE_EMPTY:
                view = LayoutInflater.from(context).inflate(R.layout.item_list_empty, null);
                return new ViewHolderEmpty(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        if (viewHolder instanceof ViewHolderShow) {
            ViewHolderShow holder = (ViewHolderShow) viewHolder;
            setData(holder, i);
        } else if (viewHolder instanceof ViewHolderEdit) {
            ViewHolderEdit holder = (ViewHolderEdit) viewHolder;
            setData(holder, i);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ViewHolderShow extends RecyclerView.ViewHolder {
        @BindView(R.id.device_header)
        RoundCornerImageView deviceHeader;
        @BindView(R.id.device_name)
        TextView deviceName;
        @BindView(R.id.flash)
        FlashView flash;
        @BindView(R.id.state)
        TextView state;
        @BindView(R.id.voltage)
        TextView voltage;
        @BindView(R.id.edit)
        ImageView edit;
        @BindView(R.id.content)
        RelativeLayout content;

        ViewHolderShow(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    private void setData(ViewHolderShow holder, int position) {
        Device device = list.get(position);
        holder.deviceName.setText(device.name);
        try {
            Picasso.with(context).load(new File(device.imagePath)).placeholder(R.mipmap.ic_launcher).into(holder.deviceHeader);
        } catch (Exception e) {
            e.printStackTrace();
            Picasso.with(context).load(R.mipmap.ic_launcher).into(holder.deviceHeader);
        }
        holder.content.setTag(position);
        holder.content.setOnClickListener(onClickListener);
        holder.edit.setTag(position);
        holder.edit.setOnClickListener(onClickListener);
        holder.voltage.setText(device.voltage + "v");
        switch (device.voltage.state){
            case StateType.VOLTAGE_DYING:
                holder.state.setText("Battery is Dying");
                holder.state.setTextColor(context.getResources().getColor(R.color.stateRed));
                holder.flash.setColor(context.getResources().getColor(R.color.stateRed));
                holder.flash.enableBreathe(false);
                break;
            case StateType.VOLTAGE_LOW:
                holder.state.setText("Battery is Low");
                holder.state.setTextColor(context.getResources().getColor(R.color.stateYellow));
                holder.flash.setColor(context.getResources().getColor(R.color.stateYellow));
                holder.flash.enableBreathe(false);
                break;
            case StateType.VOLTAGE_GOOD:
                holder.state.setText("Battery is Good");
                holder.state.setTextColor(context.getResources().getColor(R.color.theme));
                holder.flash.setColor(context.getResources().getColor(R.color.theme));
                holder.flash.enableBreathe(false);
                break;
            case StateType.CHARGING:
                holder.state.setText("Charging");
                holder.state.setTextColor(context.getResources().getColor(R.color.theme));
                holder.flash.setColor(context.getResources().getColor(R.color.theme));
                holder.flash.enableBreathe(true);
                break;
            case StateType.OVER_CHARGING:
                holder.state.setText("Over Charging");
                holder.state.setTextColor(context.getResources().getColor(R.color.stateRed));
                holder.flash.setColor(context.getResources().getColor(R.color.stateRed));
                holder.flash.enableBreathe(true);
                break;
        }
    }

    static class ViewHolderEdit extends RecyclerView.ViewHolder {
        @BindView(R.id.device_header)
        RoundCornerImageView deviceHeader;
        @BindView(R.id.device_name)
        EditText deviceName;
        @BindView(R.id.empty)
        ImageView empty;
        @BindView(R.id.delete)
        ImageView delete;
        @BindView(R.id.cancel)
        TextView cancel;
        @BindView(R.id.done)
        ImageView done;

        ViewHolderEdit(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    private void setData(ViewHolderEdit holder, int position) {
        Device device = list.get(position);
        holder.deviceName.removeTextChangedListener(textWatcher);
        if (!TextUtils.isEmpty(editName)) {
            holder.deviceName.setText(editName);
        } else {
            holder.deviceName.setText(device.name);
        }
        holder.deviceName.addTextChangedListener(textWatcher);
        try {
            Picasso.with(context).load(new File(device.imagePath)).placeholder(R.mipmap.ic_launcher).into(holder.deviceHeader);
        } catch (Exception e) {
            e.printStackTrace();
            Picasso.with(context).load(R.mipmap.ic_launcher).into(holder.deviceHeader);
        }
        holder.cancel.setTag(position);
        holder.cancel.setOnClickListener(onClickListener);
        holder.deviceHeader.setTag(position);
        holder.deviceHeader.setOnClickListener(onClickListener);
        holder.done.setTag(position);
        holder.done.setOnClickListener(onClickListener);
        holder.delete.setTag(position);
        holder.delete.setOnClickListener(onClickListener);
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Object tag = v.getTag();
            if (tag instanceof Integer) {
                int position = (int) tag;
                switch (v.getId()) {
                    case R.id.edit:
                        editPosition = position;
                        notifyDataSetChanged();
                        if (callback != null) {
                            callback.onEdit(ListAdapter.this, list.get(position));
                        }
                        break;
                    case R.id.done:
                        if (callback != null) {
                            callback.onDone(ListAdapter.this, list.get(position), editName);
                        }
                        break;
                    case R.id.delete:
                        if (callback != null) {
                            callback.onDelete(ListAdapter.this, list.get(position));
                        }
                        break;
                    case R.id.empty:
                        list.get(position).name = "";
                        notifyDataSetChanged();
                        break;
                    case R.id.device_header:
                        if (callback != null) {
                            callback.onChangeHeader(ListAdapter.this, list.get(position));
                        }
                        break;
                    case R.id.cancel:
                        clearEdit();
                        break;
                    case R.id.content:
                        if (callback != null) {
                            callback.onSelect(ListAdapter.this, list.get(position));
                        }
                        break;
                }
            }
        }
    };

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            editName = s.toString();
        }
    };

    static class ViewHolderEmpty extends RecyclerView.ViewHolder {

        ViewHolderEmpty(View view) {
            super(view);
        }
    }

    public interface Callback {

        void onEdit(ListAdapter adapter, Device device);

        void onDone(ListAdapter adapter, Device device, String name);

        void onDelete(ListAdapter adapter, Device device);

        void onChangeHeader(ListAdapter adapter, Device device);

        void onSelect(ListAdapter adapter, Device device);
    }
}
