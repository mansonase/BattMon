package com.viseeointernational.battmon.data.source.base.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.support.annotation.NonNull;

import com.viseeointernational.battmon.data.entity.Voltage;

import java.util.List;

@Dao
public interface VoltageDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Voltage... data);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(List<Voltage> data);

    @Delete()
    void delete(List<Voltage> data);

    @Query("SELECT * FROM Voltage WHERE address = :address AND (time BETWEEN :from AND :to) ORDER BY time ASC")
    List<Voltage> getVoltages(@NonNull String address, long from, long to);

    @Query("SELECT * FROM Voltage WHERE address = :address AND (time BETWEEN :from AND :to)  AND state IN (:state) ORDER BY time ASC")
    List<Voltage> getVoltagesByState(@NonNull String address, long from, long to, int... state);

    @Query("SELECT AVG(value) FROM Voltage WHERE address = :address AND (time BETWEEN :from AND :to) AND state IN (:state)")
    float getAvgVoltageByState(@NonNull String address, long from, long to, int... state);

    @Query("SELECT MAX(value) FROM Voltage WHERE address = :address AND (time BETWEEN :from AND :to) AND state IN (:state)")
    float getMaxVoltageByState(@NonNull String address, long from, long to, int... state);

    @Query("SELECT * FROM Voltage WHERE address = :address ORDER BY time DESC LIMIT 1")
    Voltage getLashVoltage(@NonNull String address);

    @Query("SELECT * FROM Voltage WHERE address = :address AND (time BETWEEN :from AND :to) AND state IN (:state) ORDER BY time DESC LIMIT 1")
    Voltage getLashVoltageByState(@NonNull String address, long from, long to, int... state);

    @Query("SELECT * FROM Voltage WHERE address = :address AND indexH = :indexH AND indexL = :indexL AND reportId = :reportId AND (time BETWEEN :from AND :to)")
    List<Voltage> getVoltagesByIndex(@NonNull String address, byte indexH, byte indexL, byte reportId, long from, long to);
}
