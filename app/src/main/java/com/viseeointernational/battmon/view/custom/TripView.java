package com.viseeointernational.battmon.view.custom;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.viseeointernational.battmon.R;
import com.viseeointernational.battmon.data.constant.StateType;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TripView extends LinearLayout {

    @BindView(R.id.start)
    TextView start;
    @BindView(R.id.end)
    TextView end;
    @BindView(R.id.duration)
    TextView duration;
    @BindView(R.id.arrow_right)
    ImageView arrowRight;
    @BindView(R.id.arrow_down)
    ImageView arrowDown;
    @BindView(R.id.flash_voltage)
    FlashView flashVoltage;
    @BindView(R.id.state_voltage)
    TextView stateVoltage;
    @BindView(R.id.value_voltage)
    TextView valueVoltage;
    @BindView(R.id.flash_cranking)
    FlashView flashCranking;
    @BindView(R.id.state_cranking)
    TextView stateCranking;
    @BindView(R.id.value_cranking)
    TextView valueCranking;
    @BindView(R.id.flash_charging)
    FlashView flashCharging;
    @BindView(R.id.state_charging)
    TextView stateCharging;
    @BindView(R.id.detail)
    LinearLayout detail;

    public TripView(Context context) {
        super(context);
        init(context);
    }

    public TripView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public TripView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public TripView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.trip, this);
        ButterKnife.bind(this);
    }

    private boolean isShowing;
    private Listener listener;

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public void showDetail(boolean show) {
        if (show) {
            arrowDown.setVisibility(VISIBLE);
            arrowRight.setVisibility(GONE);
            detail.setVisibility(VISIBLE);
        } else {
            arrowDown.setVisibility(GONE);
            arrowRight.setVisibility(VISIBLE);
            detail.setVisibility(GONE);
        }
        isShowing = show;
    }

    public void setStart(String s) {
        start.setText(s);
    }

    public void setEnd(String s) {
        end.setText(s);
    }

    public void setDuration(String s) {
        duration.setText(s);
    }

    public void setVoltageValue(String s){
        valueVoltage.setText(s);
    }

    public void setCrankingValue(String s){
        valueCranking.setText(s);
    }

    public void setStateVoltage(int type) {
        switch (type) {
            default:
                stateVoltage.setText("--");
                stateVoltage.setTextColor(Color.TRANSPARENT);
                flashVoltage.setColor(Color.TRANSPARENT);
                break;
            case StateType.VOLTAGE_DYING:
                stateVoltage.setText("Battery is Dying");
                stateVoltage.setTextColor(getResources().getColor(R.color.stateRed));
                flashVoltage.setColor(getResources().getColor(R.color.stateRed));
                valueVoltage.setTextColor(getResources().getColor(R.color.stateRed));
                break;
            case StateType.VOLTAGE_LOW:
                stateVoltage.setText("Battery is Low");
                stateVoltage.setTextColor(getResources().getColor(R.color.stateYellow));
                flashVoltage.setColor(getResources().getColor(R.color.stateYellow));
                valueVoltage.setTextColor(getResources().getColor(R.color.stateYellow));
                break;
            case StateType.VOLTAGE_GOOD:
                stateVoltage.setText("Battery is Good");
                stateVoltage.setTextColor(getResources().getColor(R.color.theme));
                flashVoltage.setColor(getResources().getColor(R.color.theme));
                valueVoltage.setTextColor(Color.WHITE);
                break;
        }
    }

    public void setStateCranking(int type) {
        switch (type) {
            default:
                stateCranking.setText("--");
                stateCranking.setTextColor(Color.TRANSPARENT);
                flashCranking.setColor(Color.TRANSPARENT);
                break;
            case StateType.CRANKING_BAD:
                stateCranking.setText("Cranking is Bad");
                stateCranking.setTextColor(getResources().getColor(R.color.stateRed));
                flashCranking.setColor(getResources().getColor(R.color.stateRed));
                valueCranking.setTextColor(getResources().getColor(R.color.stateRed));
                break;
            case StateType.CRANKING_LOW:
                stateCranking.setText("Cranking is Low");
                stateCranking.setTextColor(getResources().getColor(R.color.stateYellow));
                flashCranking.setColor(getResources().getColor(R.color.stateYellow));
                valueCranking.setTextColor(getResources().getColor(R.color.stateYellow));
                break;
            case StateType.CRANKING_GOOD:
                stateCranking.setText("Cranking is Good");
                stateCranking.setTextColor(getResources().getColor(R.color.theme));
                flashCranking.setColor(getResources().getColor(R.color.theme));
                valueCranking.setTextColor(Color.WHITE);
                break;
        }
    }

    public void setStateCharging(int type) {
        switch (type) {
            default:
                stateCharging.setText("--");
                stateCharging.setTextColor(Color.TRANSPARENT);
                flashCharging.setColor(Color.TRANSPARENT);
                flashCharging.enableBreathe(false);
                break;
            case StateType.CHARGING:
                stateCharging.setText("Charging is Normal");
                stateCharging.setTextColor(getResources().getColor(R.color.theme));
                flashCharging.setColor(getResources().getColor(R.color.theme));
                flashCharging.enableBreathe(false);
                break;
            case StateType.OVER_CHARGING:
                stateCharging.setText("Over Charging");
                stateCharging.setTextColor(getResources().getColor(R.color.stateRed));
                flashCharging.setColor(getResources().getColor(R.color.stateRed));
                flashCharging.enableBreathe(true);
                break;
        }
    }

    @OnClick({R.id.show_detail, R.id.graph_voltage, R.id.graph_cranking})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.show_detail:
                if(!isShowing && listener != null){
                    listener.onOpenDetail(this);
                }
                showDetail(!isShowing);
                break;
            case R.id.graph_voltage:
                if (listener != null) {
                    listener.onGraphVoltageClick(this);
                }
                break;
            case R.id.graph_cranking:
                if (listener != null) {
                    listener.onGraphCrankingClick(this);
                }
                break;
        }
    }

    public interface Listener {

        void onGraphVoltageClick(TripView view);

        void onGraphCrankingClick(TripView view);

        void onOpenDetail(TripView view);
    }
}
