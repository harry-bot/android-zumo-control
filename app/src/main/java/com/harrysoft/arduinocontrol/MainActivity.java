package com.harrysoft.arduinocontrol;

import android.arch.lifecycle.ViewModelProviders;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private MainActivityViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Setup activity
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Setup ViewModel
        viewModel = ViewModelProviders.of(this).get(MainActivityViewModel.class);

        if (!viewModel.setupViewModel()) {
            finish();
            return;
        }

        // Setup Views
        RecyclerView deviceList = findViewById(R.id.main_devices);
        SwipeRefreshLayout swipeRefreshLayout = findViewById(R.id.main_swiperefresh);

        // Setup RecyclerView
        deviceList.setLayoutManager(new LinearLayoutManager(this));
        DeviceAdapter adapter = new DeviceAdapter();
        deviceList.setAdapter(adapter);

        // Setup SwipeRefreshLayout
        swipeRefreshLayout.setOnRefreshListener(() -> {
            viewModel.refreshPairedDevices();
            swipeRefreshLayout.setRefreshing(false);
        });

        // Start observing
        viewModel.getPairedDeviceList().observe(MainActivity.this, adapter::updateList);

        // Immediately refresh the paired devices list
        viewModel.refreshPairedDevices();
    }

    public void openCommunicationsActivity(String deviceName, String macAddress) {
        Intent intent = new Intent(this, RobotControlActivity.class);
        intent.putExtra("device_name", deviceName);
        intent.putExtra("device_mac", macAddress);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private class DeviceViewHolder extends RecyclerView.ViewHolder {

        private final RelativeLayout layout;
        private final TextView text1;
        private final TextView text2;

        DeviceViewHolder(View view) {
            super(view);
            layout = view.findViewById(R.id.list_item);
            text1 = view.findViewById(R.id.list_item_text1);
            text2 = view.findViewById(R.id.list_item_text2);
        }

        void setupView(BluetoothDevice device) {
            text1.setText(device.getName());
            text2.setText(device.getAddress());
            layout.setOnClickListener(view -> openCommunicationsActivity(device.getName(), device.getAddress()));
        }
    }

    private class DeviceAdapter extends RecyclerView.Adapter<DeviceViewHolder> {

        private List<BluetoothDevice> deviceList = new ArrayList<>();

        @NonNull
        @Override
        public DeviceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new DeviceViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull DeviceViewHolder holder, int position) {
            holder.setupView(deviceList.get(position));
        }

        @Override
        public int getItemCount() {
            return deviceList.size();
        }

        void updateList(List<BluetoothDevice> deviceList) {
            this.deviceList = deviceList;
            notifyDataSetChanged();
        }
    }
}
