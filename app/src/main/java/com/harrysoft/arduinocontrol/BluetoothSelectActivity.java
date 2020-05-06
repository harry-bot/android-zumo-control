package com.harrysoft.arduinocontrol;

import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.harrysoft.arduinocontrol.robot.RobotInterface;

import java.util.ArrayList;
import java.util.List;

public class BluetoothSelectActivity extends AppCompatActivity {

    private BluetoothSelectActivityViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Setup activity
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_bluetooth);

        // Setup ViewModel
        viewModel = ViewModelProviders.of(this).get(BluetoothSelectActivityViewModel.class);

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
        viewModel.getPairedDeviceList().observe(BluetoothSelectActivity.this, adapter::updateList);

        // Immediately refresh the paired devices list
        viewModel.refreshPairedDevices();
    }

    public void openCommunicationsActivity(String deviceName, String macAddress) {
        Intent intent = new Intent(this, RobotControlActivity.class);
        intent.putExtra("device_name", deviceName);
        intent.putExtra("device_protocol", RobotInterface.Protocol.BLUETOOTH);
        intent.putExtra("device_address", macAddress);
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
