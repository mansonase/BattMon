package com.viseeointernational.battmon.view.page.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.viseeointernational.battmon.App;
import com.viseeointernational.battmon.R;
import com.viseeointernational.battmon.di.component.MainActivityComponent;
import com.viseeointernational.battmon.view.custom.RoundCornerImageView;
import com.viseeointernational.battmon.view.page.BaseActivity;
import com.viseeointernational.battmon.view.page.help.HelpActivity;
import com.viseeointernational.battmon.view.page.main.cranking.CrankingFragment;
import com.viseeointernational.battmon.view.page.main.list.ListFragment;
import com.viseeointernational.battmon.view.page.main.setup.SetupFragment;
import com.viseeointernational.battmon.view.page.main.trip.TripFragment;
import com.viseeointernational.battmon.view.page.main.voltage.VoltageFragment;

import java.io.File;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends BaseActivity implements MainActivityContract.View {

    public static final String KEY_ADDRESS = "address";

    @BindView(R.id.offline)
    ImageView offline;
    @BindView(R.id.device_name)
    TextView deviceName;
    @BindView(R.id.device_header)
    RoundCornerImageView deviceHeader;
    @BindView(R.id.voltage)
    RadioButton voltage;
    @BindView(R.id.cranking)
    RadioButton cranking;
    @BindView(R.id.trip)
    RadioButton trip;
    @BindView(R.id.setup)
    RadioButton setup;
    @BindView(R.id.list)
    RadioButton list;
    @BindView(R.id.list_title)
    ImageView listTitle;
    @BindView(R.id.device_title)
    LinearLayout deviceTitle;

    private MainActivityComponent mainActivityComponent;

    public MainActivityComponent getMainActivityComponent() {
        return mainActivityComponent;
    }

    @Inject
    ListFragment listFragment;
    @Inject
    CrankingFragment crankingFragment;
    @Inject
    SetupFragment setupFragment;
    @Inject
    TripFragment tripFragment;
    @Inject
    VoltageFragment voltageFragment;
    @Inject
    MainActivityContract.Presenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mainActivityComponent = ((App) getApplication()).getAppComponent().mainActivityComponent().build();
        mainActivityComponent.inject(this);

        showPage(voltageFragment);

        voltage.setOnCheckedChangeListener(onCheckedChangeListener);
        list.setOnCheckedChangeListener(onCheckedChangeListener);
        cranking.setOnCheckedChangeListener(onCheckedChangeListener);
        trip.setOnCheckedChangeListener(onCheckedChangeListener);
        setup.setOnCheckedChangeListener(onCheckedChangeListener);
    }

    @Override
    protected void onDestroy() {
        presenter.onAppExit();
        super.onDestroy();
    }

    private CompoundButton.OnCheckedChangeListener onCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {
                switch (buttonView.getId()) {
                    case R.id.list:
                        showListTitle();
                        showPage(listFragment);
                        break;
                    case R.id.voltage:
                        showDeviceTitle();
                        showPage(voltageFragment);
                        break;
                    case R.id.cranking:
                        showDeviceTitle();
                        showPage(crankingFragment);
                        break;
                    case R.id.trip:
                        showDeviceTitle();
                        showPage(tripFragment);
                        break;
                    case R.id.setup:
                        showDeviceTitle();
                        showPage(setupFragment);
                        break;
                }
            }
        }
    };

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
    public void showDeviceName(String name) {
        deviceName.setText(name);
    }

    @Override
    public void showDeviceHeader(String s) {
        try {
            Picasso.with(this).load(new File(s)).placeholder(R.mipmap.ic_launcher).into(deviceHeader);
        } catch (Exception e) {
            e.printStackTrace();
            Picasso.with(this).load(R.mipmap.ic_launcher).into(deviceHeader);
        }
    }

    @Override
    public void showConnection(boolean isConnected) {
        if (isConnected) {
            offline.setVisibility(View.GONE);
        } else {
            offline.setVisibility(View.VISIBLE);
        }
    }

    public void setAddress(String address) {
        presenter.setAddress(address);
        voltage.setChecked(true);
    }

    public void changeDeviceInfo(String address){
        presenter.changeDeviceInfo(address);
    }

    private void showPage(Fragment fragment) {
        Bundle bundle = new Bundle();
        bundle.putString(KEY_ADDRESS, presenter.getAddress());
        fragment.setArguments(bundle);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.content, fragment).commit();
    }

    private void showListTitle() {
        listTitle.setVisibility(View.VISIBLE);
        deviceTitle.setVisibility(View.GONE);
    }

    private void showDeviceTitle() {
        listTitle.setVisibility(View.GONE);
        deviceTitle.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.help)
    public void onViewClicked() {
        Intent intent = new Intent(this, HelpActivity.class);
        intent.putExtra(HelpActivity.KEY_TYPE, HelpActivity.TYPE_NOT_FIRST_START);
        startActivity(intent);
    }

    private long exitTime;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                showMessage(R.string.msg_exit);
                exitTime = System.currentTimeMillis();
            } else {
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
