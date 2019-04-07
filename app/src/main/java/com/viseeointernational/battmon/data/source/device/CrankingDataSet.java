package com.viseeointernational.battmon.data.source.device;

import android.util.Log;

import com.viseeointernational.battmon.util.MathUtil;
import com.viseeointernational.battmon.util.ValueUtil;

import java.util.LinkedList;
import java.util.List;

public class CrankingDataSet {

    private static final String TAG = CrankingDataSet.class.getSimpleName();

    private byte calH;
    private byte calL;
    private byte pageIndex;
    private long startTime;


    private byte[] a2;
    private List<Float> receivedData = new LinkedList<>();

    public CrankingDataSet(long now, byte calH, byte calL) {
        this.calH = calH;
        this.calL = calL;
        startTime = now - 10240;
    }

    public void putValue(byte b1, byte b2) {
        float f = ValueUtil.getRealVoltage(b1, b2, calH, calL);
        f = MathUtil.formatFloat2(f);
        receivedData.add(f);
    }

    public boolean isEnd() {
        return pageIndex >= 8;
    }

    public byte[] createA2Cmd() {
        a2 = new byte[2];
        a2[0] = (byte) 0xa2;
        a2[1] = pageIndex;
        Log.d(TAG, "创建A2指令 pageIndex = " + (pageIndex & 0xff));
        pageIndex++;
        return a2;
    }

    public byte[] getA2() {
        return a2;
    }

    public List<Float> getReceivedData() {
        return receivedData;
    }

    public long getStartTime() {
        return startTime;
    }
}
