package com.viseeointernational.battmon.data.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity
public class Voltage {

    @PrimaryKey(autoGenerate = true)
    public long id;
    public String address;
    public long time;// 发生时间
    public float value;
    public byte indexH;// 硬件每笔数据的索引 用来查历史记录 1分钟1个index
    public byte indexL;
    public byte reportId;// 判断收到a0 c0 c1是否为同一组数据
    public int state;

    public Voltage(@NonNull String address) {
        this.address = address;
    }
}
