package com.viseeointernational.battmon.view.page.main.list;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.viseeointernational.battmon.R;
import com.viseeointernational.battmon.data.entity.Device;
import com.viseeointernational.battmon.view.adapter.ListAdapter;
import com.viseeointernational.battmon.view.custom.DeleteDialog;
import com.viseeointernational.battmon.view.custom.HeaderDialog;
import com.viseeointernational.battmon.view.page.BaseFragment;
import com.viseeointernational.battmon.view.page.connect.ConnectActivity;
import com.viseeointernational.battmon.view.page.main.MainActivity;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class ListFragment extends BaseFragment implements ListFragmentContract.View {

    private static final String TAG = ListFragment.class.getSimpleName();

    public static final int REQUEST_SELECT = 1;
    public static final int REQUEST_CAMERA = 2;
    public static final int REQUEST_CROP = 3;

    @Inject
    ListFragmentContract.Presenter presenter;
    @Inject
    ListAdapter adapter;

    @BindView(R.id.list)
    RecyclerView list;
    @BindView(R.id.input)
    EditText input;
    Unbinder unbinder;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainActivity activity = (MainActivity) getActivity();
        if (activity != null) {
            activity.getMainActivityComponent().chargingFragmentComponent().build().inject(this);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_list, container, false);
        unbinder = ButterKnife.bind(this, root);

        init();
        presenter.takeView(this);
        return root;
    }

    @Override
    public void onDestroyView() {
        presenter.dropView();
        super.onDestroyView();
        unbinder.unbind();
    }

    private void init() {
        list.setLayoutManager(new LinearLayoutManager(getContext()));
        list.setAdapter(adapter);
        adapter.setCallback(new ListAdapter.Callback() {

            @Override
            public void onEdit(ListAdapter adapter, Device device) {
                presenter.editDevice(device);
            }

            @Override
            public void onDone(ListAdapter adapter, Device device, String name) {
                Log.d(TAG, "name = " + name);
                presenter.setName(name);
            }

            @Override
            public void onDelete(ListAdapter adapter, final Device device) {
                new DeleteDialog(getContext(), new DeleteDialog.Callback() {
                    @Override
                    public void onDelete(DeleteDialog dialog) {
                        presenter.delete(device);
                    }
                }).show("Delete " + device.name);
            }

            @Override
            public void onChangeHeader(ListAdapter adapter, Device device) {
                new HeaderDialog(getContext(), new HeaderDialog.Callback() {
                    @Override
                    public void onCamera(HeaderDialog dialog) {
                        presenter.takePhoto();
                    }

                    @Override
                    public void onSelect(HeaderDialog dialog) {
                        selectPicture();
                    }

                    @Override
                    public void onDelete(HeaderDialog dialog) {
                        presenter.deleteHeader();
                    }
                }).show();
            }

            @Override
            public void onSelect(ListAdapter adapter, Device device) {
                MainActivity activity = (MainActivity) getActivity();
                if (activity != null)
                    activity.setAddress(device.address);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        presenter.result(requestCode, resultCode, data);
    }

    @Override
    public void showDevices(List<Device> devices) {
        adapter.setData(devices);
    }

    @Override
    public void cropHeader(Uri origin, Uri output) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(origin, "image/*");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 256);
        intent.putExtra("outputY", 256);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, output);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        startActivityForResult(intent, REQUEST_CROP);
    }

    @Override
    public void takePhoto(Uri output) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, output);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    private void selectPicture() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(intent, REQUEST_SELECT);
    }

    @Override
    public void showAdd() {
        startActivity(new Intent(getActivity(), ConnectActivity.class));
    }

    @Override
    public void clearEdit() {
        adapter.clearEdit();
    }

    @Override
    public void changDeviceInfo(String address) {
        MainActivity activity = (MainActivity) getActivity();
        if (activity != null) {
            activity.changeDeviceInfo(address);
        }
    }

    @OnClick({R.id.empty, R.id.search, R.id.add})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.empty:
                input.setText("");
                presenter.searchDevice("");
                break;
            case R.id.search:
                presenter.searchDevice(input.getText().toString());
                break;
            case R.id.add:
                presenter.add();
                break;
        }
    }
}
