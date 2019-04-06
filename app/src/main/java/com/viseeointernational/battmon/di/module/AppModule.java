package com.viseeointernational.battmon.di.module;

import android.arch.persistence.room.Room;
import android.content.Context;

import com.viseeointernational.battmon.data.source.android.ble.BleService;
import com.viseeointernational.battmon.data.source.base.database.BMDatabase;
import com.viseeointernational.battmon.data.source.base.database.CrankingDao;
import com.viseeointernational.battmon.data.source.base.database.CrankingValueDao;
import com.viseeointernational.battmon.data.source.base.database.DeviceDao;
import com.viseeointernational.battmon.data.source.base.database.TripDao;
import com.viseeointernational.battmon.data.source.base.database.VoltageDao;
import com.viseeointernational.battmon.data.source.base.sharedpreferences.SharedPreferencesHelper;
import com.viseeointernational.battmon.data.source.device.DeviceRepository;
import com.viseeointernational.battmon.data.source.device.DeviceSource;
import com.viseeointernational.battmon.data.source.file.FileRepository;
import com.viseeointernational.battmon.data.source.file.FileSource;
import com.viseeointernational.battmon.di.component.ConnectActivityComponent;
import com.viseeointernational.battmon.di.component.MainActivityComponent;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(subcomponents = {ConnectActivityComponent.class,
        MainActivityComponent.class})
public class AppModule {

    private static final String DATABASE_NAME = "BM.db";
    private static final String SHARED_PREFERENCES_NAME = "Config";

    private Context context;
    private BleService bleService;

    public AppModule(Context context, BleService bleService) {
        this.context = context;
        this.bleService = bleService;
    }

    @Singleton
    @Provides
    Context context() {
        return context;
    }

    @Singleton
    @Provides
    BleService bleService() {
        return bleService;
    }

    @Singleton
    @Provides
    BMDatabase bmDatabase(Context context) {
        return Room.databaseBuilder(context, BMDatabase.class, DATABASE_NAME)
                .build();
    }

    @Singleton
    @Provides
    DeviceDao deviceDao(BMDatabase database) {
        return database.deviceDao();
    }

    @Singleton
    @Provides
    TripDao tripDao(BMDatabase database) {
        return database.tripDao();
    }

    @Singleton
    @Provides
    CrankingDao crankingDao(BMDatabase database) {
        return database.crankingDao();
    }

    @Singleton
    @Provides
    VoltageDao voltageDao(BMDatabase database) {
        return database.voltageDao();
    }

    @Singleton
    @Provides
    CrankingValueDao crankingValueDao(BMDatabase database) {
        return database.crankingValueDao();
    }

    @Singleton
    @Provides
    SharedPreferencesHelper sharedPreferencesHelper(Context context) {
        return new SharedPreferencesHelper(context, SHARED_PREFERENCES_NAME);
    }

    @Singleton
    @Provides
    DeviceSource deviceSource(DeviceRepository deviceRepository) {
        return deviceRepository;
    }

    @Singleton
    @Provides
    FileSource fileSource(FileRepository fileRepository) {
        return fileRepository;
    }

}
