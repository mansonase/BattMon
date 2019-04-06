package com.viseeointernational.battmon.view.page.connect;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.viseeointernational.battmon.App;
import com.viseeointernational.battmon.R;
import com.viseeointernational.battmon.data.entity.Device;
import com.viseeointernational.battmon.view.adapter.DeviceAdapter;
import com.viseeointernational.battmon.view.custom.IfDialog;
import com.viseeointernational.battmon.view.page.BaseActivity;
import com.viseeointernational.battmon.view.page.help.HelpActivity;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ConnectActivity extends BaseActivity implements ConnectActivityContract.View {

    public static final int REQUEST_BLUETOOTH = 1;

    public static final int RESULT_BACK = 1;

    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.list)
    ListView list;

    @Inject
    ConnectActivityContract.Presenter presenter;
    @Inject
    DeviceAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect);
        ButterKnife.bind(this);

        ((App) getApplication()).getAppComponent().connectActivityComponent().build().inject(this);

        title.setText(R.string.bluetooth);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                presenter.choose(adapter.getData(position));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.takeView(this);
    }

    @Override
    protected void onPause() {
        presenter.dropView();
        super.onPause();
    }

    @Override
    public void showEnableBluetooth() {
        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(intent, REQUEST_BLUETOOTH);
    }

    @Override
    public void showDevices(List<Device> list) {
        adapter.setData(list);
    }

    @Override
    public void alertIfReconnect() {
        new IfDialog(this, new IfDialog.Callback() {

            @Override
            public void onOk(IfDialog dialog) {
                presenter.doConnection();
            }

            @Override
            public void onCancel(IfDialog dialog) {
            }
        }).show(getText(R.string.dialog_add_reconnect));
    }

    @Override
    public void close() {
        finish();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        presenter.result(requestCode, resultCode, data);
    }

    @OnClick({R.id.help, R.id.rescan, R.id.cancel})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.help:
                startActivity(new Intent(this, HelpActivity.class));
                break;
            case R.id.rescan:
                presenter.search();
                break;
            case R.id.cancel:
                onBackPressed();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_BACK);
        super.onBackPressed();
    }
}
