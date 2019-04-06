package com.viseeointernational.battmon.data.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity
public class Trip {

    @PrimaryKey(autoGenerate = true)
    public long id;
    public String address;// 物理地址
    public long startTime;// 开始时间
    public long endTime;// 结束时间

    public Trip(@NonNull String address) {
        this.address = address;
    }

    @Ignore
    public Voltage voltage;

    @Ignore
    public Cranking cranking;

    @Ignore
    public Voltage charging;
}
