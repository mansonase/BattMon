package com.viseeointernational.battmon.view.custom;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialog;
import android.widget.ListView;

import com.viseeointernational.battmon.R;
import com.viseeointernational.battmon.data.entity.Voltage;
import com.viseeointernational.battmon.view.adapter.AbnormalChargingAdapter;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AbnormalChargingDialog extends AppCompatDialog {

    @BindView(R.id.list)
    ListView list;

    private AbnormalChargingAdapter adapter;

    public AbnormalChargingDialog(Context context) {
        super(context, R.style.DialogTeach);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_abnormal_charging);
        ButterKnife.bind(this);

        setCanceledOnTouchOutside(true);
        adapter = new AbnormalChargingAdapter(getContext());
        list.setAdapter(adapter);
    }

    public void show(List<Voltage> list) {
        super.show();
        adapter.setData(list);
    }

}
