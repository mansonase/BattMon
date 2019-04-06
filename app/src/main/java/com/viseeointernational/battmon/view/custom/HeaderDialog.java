package com.viseeointernational.battmon.view.custom;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatDialog;
import android.view.Gravity;
import android.view.View;

import com.viseeointernational.battmon.R;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class HeaderDialog extends AppCompatDialog {

    private Callback callback;

    public HeaderDialog(Context context, @NonNull Callback callback) {
        super(context, R.style.DialogBottom);
        this.callback = callback;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_header);
        ButterKnife.bind(this);

        getWindow().setGravity(Gravity.BOTTOM);

        setCanceledOnTouchOutside(true);
    }

    @OnClick({R.id.camera, R.id.select, R.id.delete, R.id.cancel})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.camera:
                callback.onCamera(this);
                dismiss();
                break;
            case R.id.select:
                callback.onSelect(this);
                dismiss();
                break;
            case R.id.delete:
                callback.onDelete(this);
                dismiss();
                break;
            case R.id.cancel:
                dismiss();
                break;
        }
    }

    public interface Callback {

        void onCamera(HeaderDialog dialog);

        void onSelect(HeaderDialog dialog);

        void onDelete(HeaderDialog dialog);
    }
}
