package com.harrysoft.arduinocontrol;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.harrysoft.androidbluetoothserial.BluetoothManager;
import com.harrysoft.androidbluetoothserial.BluetoothSerialDevice;

import javax.annotation.Nullable;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class RobotControl extends AppCompatActivity {

    CompositeDisposable compositeDisposable = new CompositeDisposable();

    Button upButton, downButton, leftButton, rightButton, stopButton, disconnectButton;
    String address = null;

    private BluetoothManager bluetoothManager;
    @Nullable
    private BluetoothSerialDevice serialDevice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // todo progress dialog
        Intent newInt = getIntent();
        address = newInt.getStringExtra(DeviceList.EXTRA_ADDRESS);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_robot_control);

        upButton = findViewById(R.id.upButton);
        downButton = findViewById(R.id.downButton);
        leftButton = findViewById(R.id.leftButton);
        rightButton = findViewById(R.id.rightButton);
        stopButton = findViewById(R.id.stopButton);
        disconnectButton = findViewById(R.id.disconnectButton);

        upButton.setOnClickListener(v -> sendCommand("f"));
        downButton.setOnClickListener(v -> sendCommand("b"));
        leftButton.setOnClickListener(v -> sendCommand("l"));
        rightButton.setOnClickListener(v -> sendCommand("r"));
        stopButton.setOnClickListener(v -> sendCommand("s"));
        disconnectButton.setOnClickListener(v -> Disconnect());

        bluetoothManager = BluetoothManager.getInstance();

        if (bluetoothManager == null) {
            msg("Bluetooth not available");
            finish();
            return;
        }

        compositeDisposable.add(
                bluetoothManager.openSerialDevice(address)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(device -> {
                            this.serialDevice = device;
                            openMessageStream();
                            msg("Connected.");
                        }, t -> {
                            msg("Connection Failed. Is it a SPP Bluetooth? Try again.");
                            t.printStackTrace();
                            finish();
                        }));
    }

    private void openMessageStream() {
        if (serialDevice != null) {
            compositeDisposable.add(
                    serialDevice.openMessageStream()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(message -> Log.e("I GOT A MESSAGE!!!", message), Throwable::printStackTrace));
        }
    }

    private void msg(String s) {
        Toast.makeText(getApplicationContext(),s,Toast.LENGTH_LONG).show();
    }

    private void Disconnect() {
        try {
            if (serialDevice != null) {
                bluetoothManager.closeDevice(serialDevice);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        finish();
    }

    private void sendCommand(String command) {
        if (serialDevice != null) {
            compositeDisposable.add(
                    serialDevice.send(command)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(() -> {}, Throwable::printStackTrace)); // todo handle error
        }
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.dispose();
        super.onDestroy();
    }
}
