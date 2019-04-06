package com.viseeointernational.battmon.view.page.main.list;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.viseeointernational.battmon.data.entity.Device;
import com.viseeointernational.battmon.data.source.device.DeviceSource;
import com.viseeointernational.battmon.data.source.file.FileSource;

import java.io.File;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class ListFragmentPresenter implements ListFragmentContract.Presenter {

    private static final String TAG = ListFragmentPresenter.class.getSimpleName();

    private ListFragmentContract.View view;

    private DeviceSource deviceSource;
    private FileSource fileSource;

    private String input;
    private Device device;

    @Inject
    public ListFragmentPresenter(FileSource fileSource, DeviceSource deviceSource) {
        this.fileSource = fileSource;
        this.deviceSource = deviceSource;
    }

    @Override
    public void takeView(ListFragmentContract.View view) {
        this.view = view;
        getDevices(null);
    }

    @Override
    public void dropView() {
        view = null;
    }

    private void getDevices(final String search) {
        List<Device> devices = deviceSource.getDevices(search);
        if (view != null) {
            view.showDevices(devices);
        }
    }

    @Override
    public void add() {
        if (deviceSource.getDevicesCount() >= 6) {
            if (view != null) {
                view.showMessage("Can not add more devices!");
            }
            return;
        }
        if (view != null) {
            view.showAdd();
        }
    }

    @Override
    public void editDevice(@NonNull Device device) {
        this.device = device;
    }

    @Override
    public void deleteHeader() {
        Observable.just(1)
                .map(new Function<Integer, Boolean>() {
                    @Override
                    public Boolean apply(Integer integer) throws Exception {
                        return fileSource.deleteHeaderByAddress(device.address);
                    }
                })
                .doOnNext(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        if (aBoolean) {
                            deviceSource.saveHeader(device.address, null);
                        }
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        if (view != null) {
                            view.showLoading();
                        }
                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        if (view != null) {
                            view.cancelLoading();
                            getDevices(input);
                            view.changDeviceInfo(device.address);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (view != null) {
                            view.cancelLoading();
                            view.showMessage("Delete failed!");
                        }
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }

    private Uri takePhoto;
    private File headerFile;

    @Override
    public void takePhoto() {
        takePhoto = fileSource.getTakePhotoUri();
        if (view != null) {
            view.takePhoto(takePhoto);
        }
    }

    private void showCrop(Uri data) {
        headerFile = fileSource.getHeaderFileByAddress(device.address);
        if (view != null) {
            view.cropHeader(data, Uri.fromFile(headerFile));
        }
    }

    @Override
    public void result(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case ListFragment.REQUEST_SELECT:
                if (resultCode == Activity.RESULT_OK && data != null) {
                    showCrop(data.getData());
                }
                break;
            case ListFragment.REQUEST_CAMERA:
                if (resultCode == Activity.RESULT_OK) {
                    showCrop(takePhoto);
                }
                break;
            case ListFragment.REQUEST_CROP:
                if (resultCode == Activity.RESULT_OK && data != null) {
                    String headerPath = headerFile.getAbsolutePath();
                    deviceSource.saveHeader(device.address, headerPath);
                    getDevices(input);
                    if(view != null){
                        view.changDeviceInfo(device.address);
                    }
                }
                break;
        }
    }

    @Override
    public void setName(String s) {
        if (TextUtils.isEmpty(s)) {
            if (view != null) {
                view.showMessage("Please enter your name!");
            }
            return;
        }
        if (view != null) {
            view.clearEdit();
        }
        deviceSource.saveName(device.address, s);
        getDevices(input);
        if(view != null){
            view.changDeviceInfo(device.address);
        }
    }

    @Override
    public void delete() {
        Observable.just(1)
                .doOnNext(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        deviceSource.delete(device.address);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        if (view != null) {
                            view.showLoading();
                        }
                    }

                    @Override
                    public void onNext(Integer integer) {
                        if (view != null) {
                            view.cancelLoading();
                            view.clearEdit();
                            getDevices(input);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (view != null) {
                            view.cancelLoading();
                            view.clearEdit();
                        }
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }

    @Override
    public void searchDevice(@NonNull String input) {
        this.input = input;
        getDevices(input);
    }
}