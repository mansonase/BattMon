package com.viseeointernational.battmon.data.source.base.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import com.viseeointernational.battmon.data.entity.Cranking;
import com.viseeointernational.battmon.data.entity.CrankingValue;
import com.viseeointernational.battmon.data.entity.Device;
import com.viseeointernational.battmon.data.entity.Trip;
import com.viseeointernational.battmon.data.entity.Voltage;

@Database(entities = {Device.class, Trip.class, Voltage.class, Cranking.class, CrankingValue.class}, version = 1, exportSchema = false)
public abstract class BMDatabase extends RoomDatabase {

    public abstract DeviceDao deviceDao();

    public abstract TripDao tripDao();

    public abstract VoltageDao voltageDao();

    public abstract CrankingDao crankingDao();

    public abstract CrankingValueDao crankingValueDao();
}
