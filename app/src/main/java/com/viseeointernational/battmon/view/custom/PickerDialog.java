package com.viseeointernational.battmon.view.custom;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatDialog;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.viseeointernational.battmon.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.forward.androids.views.StringScrollPicker;

public class PickerDialog extends AppCompatDialog {

    @BindView(R.id.picker)
    StringScrollPicker picker;

    private List<String> list = new ArrayList<>();
    private Callback callback;

    public PickerDialog(Context context, @NonNull Callback callback) {
        super(context, R.style.DialogBottom);
        this.callback = callback;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_picker);
        ButterKnife.bind(this);

        list.clear();
        list.add("Always On");
        list.add("30 mins");
        list.add("1 hour");
        list.add("2 hours");
        list.add("3 hours");
        list.add("4 hours");

        Window window = getWindow();
        window.setGravity(Gravity.BOTTOM);
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        window.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        layoutParams.width = displayMetrics.widthPixels;
        window.setAttributes(layoutParams);

        setCanceledOnTouchOutside(true);
    }

    @Override
    public void show() {
        super.show();
        picker.setData(list);
    }

    @OnClick({R.id.cancel, R.id.done})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.cancel:
                dismiss();
                break;
            case R.id.done:
                int position = picker.getSelectedPosition();
                switch (position) {
                    case 0:
                    default:
                        callback.onSelect(PickerDialog.this, -1);
                        break;
                    case 1:
                        callback.onSelect(PickerDialog.this, 60000L * 30);
                        break;
                    case 2:
                        callback.onSelect(PickerDialog.this, 60000L * 60);
                        break;
                    case 3:
                        callback.onSelect(PickerDialog.this, 60000L * 60 * 2);
                        break;
                    case 4:
                        callback.onSelect(PickerDialog.this, 60000L * 60 * 3);
                        break;
                    case 5:
                        callback.onSelect(PickerDialog.this, 60000L * 60 * 4);
                        break;
                }
                dismiss();
                break;
        }
    }

    public interface Callback {

        void onSelect(PickerDialog dialog, long time);
    }
}
