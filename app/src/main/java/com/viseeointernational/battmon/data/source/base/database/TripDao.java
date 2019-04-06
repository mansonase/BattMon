package com.viseeointernational.battmon.data.source.base.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.support.annotation.NonNull;

import com.viseeointernational.battmon.data.entity.Trip;

import java.util.List;

@Dao
public interface TripDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Trip... data);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(List<Trip> data);

    @Query("SELECT * FROM Trip WHERE address = :address AND ((startTime BETWEEN :from AND :to) OR (endTime BETWEEN :from AND :to))")
    List<Trip> getTrips(@NonNull String address, long from, long to);
}
