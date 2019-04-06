package com.viseeointernational.battmon.data.source.file;

import android.net.Uri;
import android.support.annotation.NonNull;

import java.io.File;

public interface FileSource {

    boolean deleteHeaderByAddress(@NonNull String address);

    File getHeaderFileByAddress(@NonNull String address);

    Uri getTakePhotoUri();
}
