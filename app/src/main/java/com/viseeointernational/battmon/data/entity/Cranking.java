package com.viseeointernational.battmon.data.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.util.List;

@Entity
public class Cranking {

    @PrimaryKey(autoGenerate = true)
    public long id;
    public String address;
    public long startTime;// 发生时间
    public float minValue = Float.MAX_VALUE;// value中的最小值
    public int state;

    public Cranking(@NonNull String address) {
        this.address = address;
    }

    @Ignore
    public List<CrankingValue> value;
}
