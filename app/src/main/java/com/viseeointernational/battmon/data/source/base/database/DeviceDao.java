package com.viseeointernational.battmon.data.source.base.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.viseeointernational.battmon.data.entity.Device;

import java.util.List;

@Dao
public interface DeviceDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Device... data);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(List<Device> data);

    @Query("DELETE FROM Device WHERE address = :address")
    int deleteByAddress(String address);

    @Query("SELECT * FROM Device WHERE address = :address LIMIT 1")
    Device getDevice(String address);

    @Query("SELECT * FROM Device WHERE pairId != '' ORDER BY pairedTime ASC")
    List<Device> getPairedDevices();
}
