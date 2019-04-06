package com.viseeointernational.battmon.data.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity
public class CrankingValue {

    @PrimaryKey(autoGenerate = true)
    public long id;
    public String address;
    public long startTime;
    public float value;

    public CrankingValue(@NonNull String address){
        this.address = address;
    }
}
