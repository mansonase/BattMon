package com.viseeointernational.battmon.view.custom;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatDialog;
import android.view.View;
import android.widget.TextView;

import com.viseeointernational.battmon.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class IfDialog extends AppCompatDialog {

    @BindView(R.id.title)
    TextView title;

    private Callback callback;

    public IfDialog(@NonNull Context context, @NonNull Callback callback) {
        super(context, R.style.DialogBase);
        this.callback = callback;
    }

    private IfDialog(Context context, int theme) {
        super(context, theme);
    }

    private IfDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_if);
        ButterKnife.bind(this);
        setCanceledOnTouchOutside(false);
    }

    public void show(@NonNull CharSequence title) {
        super.show();
        this.title.setText(title);
    }

    @OnClick({R.id.cancel, R.id.ok})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ok:
                callback.onOk(this);
                dismiss();
                break;
            case R.id.cancel:
                callback.onCancel(this);
                dismiss();
                break;
        }
    }

    public interface Callback {

        void onOk(IfDialog dialog);

        void onCancel(IfDialog dialog);
    }
}
