package com.viseeointernational.battmon.view.page.help;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.viseeointernational.battmon.R;
import com.viseeointernational.battmon.util.DisplaySizeUtil;
import com.viseeointernational.battmon.view.page.BaseActivity;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.loader.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class HelpActivity extends BaseActivity {

    private static final String TAG = HelpActivity.class.getSimpleName();

    public static final String KEY_TYPE = "type";

    public static final int TYPE_NOT_FIRST_START = 0;
    public static final int TYPE_FIRST_START = 1;

    public static final int RESULT_BACK = 1;
    public static final int RESULT_START = 2;

    @BindView(R.id.banner)
    Banner banner;
    @BindView(R.id.tabs)
    LinearLayout tabs;
    @BindView(R.id.start)
    TextView start;

    private List<Integer> images = new ArrayList<>();

    private int type;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        ButterKnife.bind(this);

        images.add(R.mipmap.ic_launcher);
        images.add(R.mipmap.ic_launcher);
        images.add(R.mipmap.ic_launcher);
        images.add(R.mipmap.ic_launcher);
        images.add(R.mipmap.ic_launcher);

        banner.setImageLoader(new ImageLoader() {
            @Override
            public void displayImage(Context context, Object path, ImageView imageView) {
                imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                Picasso.with(context).load((int) path).into(imageView);
            }
        });
        banner.setBannerStyle(BannerConfig.NOT_INDICATOR);
        banner.isAutoPlay(false);
        banner.setImages(images);
        banner.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
            }

            @Override
            public void onPageSelected(int i) {
                showTabs(i);
            }

            @Override
            public void onPageScrollStateChanged(int i) {
            }
        });
        banner.start();

        Intent intent = getIntent();
        if (intent != null) {
            type = intent.getIntExtra(KEY_TYPE, TYPE_NOT_FIRST_START);
        }
        if (type == TYPE_NOT_FIRST_START) {
            start.setText("Cancel");
            start.setBackground(null);
        }
    }

    private void showTabs(int position) {
        tabs.removeAllViews();
        for (int i = 0; i < images.size(); i++) {
            View dot = new View(this);
            if (i == position) {
                dot.setBackgroundResource(R.drawable.dot_white);
            } else {
                dot.setBackgroundResource(R.drawable.dot_default);
            }
            int dp8 = DisplaySizeUtil.dp2px(this, 8);
            int dp15 = DisplaySizeUtil.dp2px(this, 15);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(dp15, dp15);
            params.setMargins(dp8, 0, dp8, 0);
            tabs.addView(dot, params);
        }
    }

    @OnClick(R.id.start)
    public void onViewClicked() {
        if (type == TYPE_FIRST_START) {
            setResult(RESULT_START);
        }
        finish();
    }

    @Override
    public void onBackPressed() {
        if (type == TYPE_FIRST_START) {
            setResult(RESULT_BACK);
        }
        super.onBackPressed();
    }
}
