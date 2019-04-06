package com.viseeointernational.battmon.view.custom;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialog;
import android.view.View;
import android.widget.TextView;

import com.viseeointernational.battmon.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.annotations.NonNull;

public class DeleteDialog extends AppCompatDialog {

    @BindView(R.id.title)
    TextView title;
    private Callback callback;

    public DeleteDialog(Context context, @NonNull Callback callback) {
        super(context, R.style.DialogBase);
        this.callback = callback;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_delete);
        ButterKnife.bind(this);

        setCancelable(false);
        setCanceledOnTouchOutside(false);
    }

    public void show(String title) {
        super.show();
        this.title.setText(title);
    }

    @OnClick({R.id.cancel, R.id.delete})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.cancel:
                dismiss();
                break;
            case R.id.delete:
                callback.onDelete(this);
                dismiss();
                break;
        }
    }

    public interface Callback {

        void onDelete(DeleteDialog dialog);
    }
}
