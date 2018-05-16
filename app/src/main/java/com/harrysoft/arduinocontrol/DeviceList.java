package com.harrysoft.arduinocontrol;

import android.bluetooth.BluetoothDevice;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import java.util.Set;
import java.util.ArrayList;
import android.widget.Toast;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import android.widget.TextView;
import android.content.Intent;
import android.bluetooth.BluetoothAdapter;

public class DeviceList extends AppCompatActivity {

    private ListView deviceList;
    private BluetoothAdapter myBluetooth = null;
    public static String EXTRA_ADDRESS = "device_address";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_list);

        Button showPairedDevicesButton = findViewById(R.id.showPairedDevicesButton);
        deviceList = findViewById(R.id.pairedDevicesListView);

        myBluetooth = BluetoothAdapter.getDefaultAdapter();

        if (myBluetooth != null) {
            if (!myBluetooth.isEnabled()) {
                Intent turnBTon = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(turnBTon,1);
            }
        } else {
            Toast.makeText(getApplicationContext(), "Bluetooth Not Available", Toast.LENGTH_LONG).show();
            finish();
        }

        showPairedDevicesButton.setOnClickListener(v -> getPairedDevicesList());
    }

    private void getPairedDevicesList()
    {
        Set<BluetoothDevice> pairedDevices = myBluetooth.getBondedDevices();
        ArrayList<String> list = new ArrayList<>();

        if (pairedDevices.size()>0) {
            for(BluetoothDevice bt : pairedDevices) {
                list.add(bt.getName() + "\n" + bt.getAddress()); //Get the device's name and the address
            }
        } else {
            Toast.makeText(getApplicationContext(), "No Paired Bluetooth Devices Found.", Toast.LENGTH_LONG).show();
        }

        final ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, list);
        deviceList.setAdapter(adapter);
        deviceList.setOnItemClickListener(myListClickListener);

    }

    private AdapterView.OnItemClickListener myListClickListener = (av, v, arg2, arg3) -> {
        // THIS IS HORRIBLE!! THERE MUST BE A BETTER WAY TO DO THIS...

        // Get the device MAC address, the last 17 chars in the View
        String info = ((TextView) v).getText().toString();
        String address = info.substring(info.length() - 17);
        Intent i = new Intent(DeviceList.this, RobotControl.class);
        i.putExtra(EXTRA_ADDRESS, address);
        startActivity(i);
    };
}
