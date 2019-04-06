package com.viseeointernational.battmon.view.page.main.trip;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.github.mikephil.charting.data.Entry;
import com.viseeointernational.battmon.R;
import com.viseeointernational.battmon.data.entity.Trip;
import com.viseeointernational.battmon.view.adapter.TripAdapter;
import com.viseeointernational.battmon.view.custom.ChartDialog;
import com.viseeointernational.battmon.view.custom.DateDialog;
import com.viseeointernational.battmon.view.custom.TripDialog;
import com.viseeointernational.battmon.view.page.BaseFragment;
import com.viseeointernational.battmon.view.page.main.MainActivity;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class TripFragment extends BaseFragment implements TripFragmentContract.View {

    @Inject
    TripFragmentContract.Presenter presenter;
    @Inject
    TripAdapter adapter;

    @BindView(R.id.date)
    TextView date;
    @BindView(R.id.list)
    ListView list;
    Unbinder unbinder;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainActivity activity = (MainActivity) getActivity();
        if (activity != null) {
            activity.getMainActivityComponent().tripFragmentComponent().fragment(this).build().inject(this);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_trip, container, false);
        unbinder = ButterKnife.bind(this, root);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        list.setAdapter(adapter);
        adapter.setCallback(new TripAdapter.Callback() {
            @Override
            public void onGraphVoltageClick(TripAdapter adapter, Trip trip) {
                presenter.showVoltageGraph(trip);
            }

            @Override
            public void onGraphCrankingClick(TripAdapter adapter, Trip trip) {
                presenter.showCrankingGraph(trip);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.takeView(this);
    }

    @Override
    public void onPause() {
        presenter.dropView();
        super.onPause();
    }

    @Override
    public void showDate(String s) {
        date.setText(s);
    }

    @Override
    public void showTrips(List<Trip> list) {
        adapter.setData(list);
    }

    private DateDialog dateDialog;

    @Override
    public void showCalendar(int year, int month, List<Integer> markedDays) {
        if (dateDialog == null) {
            dateDialog = new DateDialog(getActivity(), new DateDialog.Callback() {
                @Override
                public void onSelect(DateDialog dialog, int year, int month, int day) {
                    presenter.selectDate(year, month, day);
                }

                @Override
                public void onChangeMonth(int year, int month) {
                    presenter.changeMonth(year, month);
                }
            });
        }
        dateDialog.show(year, month, markedDays);
    }

    @Override
    public void showVoltageGraph(String title, List<Entry> list, int position) {
        new ChartDialog(getActivity()).show(title, list, position);
    }

    @Override
    public void showCrankingGraph(String title, List<Entry> list, int position) {
        new ChartDialog(getActivity()).show(title, list, position);
    }

    @OnClick({R.id.teach, R.id.calendar})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.teach:
                new TripDialog(getActivity()).show();
                break;
            case R.id.calendar:
                presenter.showCalendar();
                break;
        }
    }
}
