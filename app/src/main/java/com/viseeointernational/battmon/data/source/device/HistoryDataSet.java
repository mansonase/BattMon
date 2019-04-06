package com.viseeointernational.battmon.data.source.device;

import android.util.Log;

import com.viseeointernational.battmon.data.constant.StateType;
import com.viseeointernational.battmon.data.entity.Voltage;
import com.viseeointernational.battmon.util.MathUtil;
import com.viseeointernational.battmon.util.ValueUtil;

import java.util.LinkedList;
import java.util.List;

public class HistoryDataSet {

    private static final String TAG = HistoryDataSet.class.getSimpleName();

    private byte indexH;
    private byte indexL;
    private long startTime;

    private byte pageIndexH;
    private byte pageIndexL;

    private String address;
    private float abnormalIdle;
    private float yellow;
    private float engineStop;
    private float overCharging;
    private byte calH;
    private byte calL;

    private long timeCursor;

    private byte[] a1;
    private List<Voltage> receivedData = new LinkedList<>();

    public HistoryDataSet(String address, float abnormalIdle, float yellow, float engineStop, float overCharging, byte calH, byte calL, byte indexH, byte indexL, long startTime, long endTime) {
        this.address = address;
        this.abnormalIdle = abnormalIdle;
        this.yellow = yellow;
        this.engineStop = engineStop;
        this.overCharging = overCharging;
        this.calH = calH;
        this.calL = calL;
        this.indexH = indexH;
        this.indexL = indexL;
        this.startTime = startTime;
        timeCursor = endTime - 1000 * 60;// 第一个数据是endTime前一分钟的
    }

    // 时间推前1分钟
    public void next() {
        timeCursor -= 1000 * 60;
    }

    public void putVoltage(byte b1, byte b2) {
        Voltage voltage = new Voltage(address);
        voltage.time = timeCursor;
        float f = ValueUtil.getRealVoltage(b1, b2, calH, calL);
        f = MathUtil.formatDouble2(f);
        voltage.value = f;

        if (voltage.value <= abnormalIdle) {
            voltage.state = StateType.VOLTAGE_DYING;
        } else if (abnormalIdle < voltage.value && voltage.value < yellow) {
            voltage.state = StateType.VOLTAGE_LOW;
        } else if (yellow < voltage.value && voltage.value < engineStop) {
            voltage.state = StateType.VOLTAGE_GOOD;
        } else if (engineStop <= voltage.value && voltage.value < overCharging) {
            voltage.state = StateType.CHARGING;
        } else {
            voltage.state = StateType.OVER_CHARGING;
        }
        receivedData.add(voltage);
    }

    public boolean isEnd() {
        return timeCursor < startTime;
    }

    public byte[] createA1Cmd() {
        a1 = new byte[5];
        a1[0] = (byte) 0xa1;
        a1[1] = pageIndexH;
        a1[2] = pageIndexL;
        a1[3] = indexH;
        a1[4] = indexL;
        Log.d(TAG, "创建A1指令 pageIndexH = " + (pageIndexH & 0xff) + " pageIndexL = " + (pageIndexL & 0xff)
                + " indexH = " + (indexH & 0xff) + " indexL = " + (indexL & 0xff));
        if (pageIndexL == (byte) 0xff) {
            pageIndexH++;
            pageIndexL = (byte) 0x00;
        } else {
            pageIndexL++;
        }
        return a1;
    }

    public List<Voltage> getReceivedData() {
        return receivedData;
    }

    public byte[] getA1() {
        return a1;
    }

}
