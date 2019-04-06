package com.viseeointernational.battmon.data.source.file;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;

import java.io.File;

import javax.inject.Inject;

public class FileRepository implements FileSource {

    private Context context;

    @Inject
    public FileRepository(Context context) {
        this.context = context;
    }

    private File root() {
        return Environment.getExternalStorageDirectory();
    }

    private File headerDir() {
        File dir = new File(root(), "ViseeO/BattMon/Header");
        if (!dir.exists() || !dir.isDirectory()) {
            dir.mkdirs();
        }
        return dir;
    }

    @Override
    public boolean deleteHeaderByAddress(@NonNull String address) {
        String prefix = address.replaceAll(":", "-");
        File[] files = headerDir().listFiles();
        if (files != null) {
            for (int i = files.length - 1; i >= 0; i--) {
                String name = files[i].getName();
                if (name.startsWith(prefix) && files[i].exists() && files[i].isFile()) {
                    files[i].delete();
                }
            }
        }
        return true;
    }

    @Override
    public File getHeaderFileByAddress(@NonNull String address) {
        String prefix = address.replaceAll(":", "-");
        return new File(headerDir(), prefix + System.currentTimeMillis() + ".jpg");
    }

    @Override
    public Uri getTakePhotoUri() {
        File file = new File(headerDir(), "temp.jpg");
        return FileProvider.getUriForFile(context, "com.viseeointernational.battmon.fileprovider", file);
    }
}
