package com.viseeointernational.battmon.data.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.viseeointernational.battmon.data.source.device.CrankingDataSet;
import com.viseeointernational.battmon.data.source.device.HistoryDataSet;
import com.viseeointernational.battmon.data.source.device.RepeatTimer;

@Entity
public class Device {

    @PrimaryKey
    @NonNull
    public String address;// 物理地址
    public String pairId;// 长度8 使用时转成4字节
    public long pairedTime;// 配对时间
    public String name = "BM1";// 名字
    public String imagePath;// 头像
    public boolean enableAbnormalNotification = true;// 异常通知是否开启
    public boolean enableUsbPowerOff;// 关闭usb供电
    public long usbPowerOffDelayTime = -1;// 延时多久usb断电
    public long usbPowerOffStartTime;// 延时开始时间

    public byte calH = (byte) 0x45;// 电压校正高位 calibration
    public byte calL = (byte) 0x24;// 电压校正地位

    public byte chgOverH = (byte) 0x03;// 过充报警电压
    public byte chgOverL = (byte) 0x4f;

    public byte chgH = (byte) 0x03;// 充电电压
    public byte chgL = (byte) 0x16;

    public byte idleLowH = (byte) 0x02;// 平常最低报警电压
    public byte idleLowL = (byte) 0xb6;

    public byte triggerH = (byte) 0x02;// 启动触发电压
    public byte triggerL = (byte) 0x51;

    public byte crankLowH = (byte) 0x01;// 启动最低报警电压
    public byte crankLowL = (byte) 0xe0;

    public Device(@NonNull String address) {
        this.address = address;
    }

    @Ignore
    public float yellow = 13;

    @Ignore
    public int connectionState;// 连接状态

    @Ignore
    public int rssi;// 信号强度

    @Ignore
    public HistoryDataSet historyDataSet;// 历史数据参数

    @Ignore
    public RepeatTimer historyTimer;

    @Ignore
    public CrankingDataSet crankingDataSet;// cranking数据

    @Ignore
    public RepeatTimer crankingTimer;

    @Ignore
    public boolean isReady;

    @Ignore
    public Voltage voltage;

    @Ignore
    public Cranking cranking;

    @Ignore
    public Trip trip;
}
