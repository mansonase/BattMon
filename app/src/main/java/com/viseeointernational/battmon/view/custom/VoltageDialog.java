package com.viseeointernational.battmon.view.custom;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialog;

import com.viseeointernational.battmon.R;

public class VoltageDialog extends AppCompatDialog {

    public VoltageDialog(Context context) {
        super(context, R.style.DialogTeach);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_voltage);

        setCanceledOnTouchOutside(true);
    }
}
