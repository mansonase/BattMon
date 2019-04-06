package com.viseeointernational.battmon.data.source.base.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.support.annotation.NonNull;

import com.viseeointernational.battmon.data.entity.CrankingValue;

import java.util.List;

@Dao
public interface CrankingValueDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(CrankingValue... data);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(List<CrankingValue> data);

    @Query("SELECT * FROM CrankingValue WHERE address = :address AND startTime = :startTime ORDER BY id ASC")
    List<CrankingValue> getCrankingValues(@NonNull String address, long startTime);
}
