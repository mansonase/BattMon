package com.viseeointernational.battmon.data.source.base.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.support.annotation.NonNull;

import com.viseeointernational.battmon.data.entity.Cranking;

import java.util.List;

@Dao
public interface CrankingDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Cranking... data);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(List<Cranking> data);

    @Query("SELECT * FROM Cranking WHERE address = :address ORDER BY startTime DESC LIMIT 1")
    Cranking getLashCranking(@NonNull String address);

    @Query("SELECT * FROM Cranking WHERE address = :address AND (startTime BETWEEN :from AND :to) ORDER BY startTime DESC LIMIT 1")
    Cranking getLashCrankingByTime(@NonNull String address, long from, long to);

    @Query("SELECT * FROM Cranking WHERE address = :address AND (startTime BETWEEN :from AND :to)")
    List<Cranking> getCrankings(@NonNull String address, long from, long to);
}
