package com.harrysoft.arduinocontrol;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.widget.Button;
import android.widget.Toast;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;

import com.harrysoft.arduinocontrol.bluetooth.BluetoothNotAvailableException;

import java.io.IOException;
import java.util.UUID;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class RobotControl extends AppCompatActivity {

    CompositeDisposable compositeDisposable = new CompositeDisposable();

    Button upButton, downButton, leftButton, rightButton, stopButton, disconnectButton;
    String address = null;
    BluetoothSocket btSocket = null;
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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

        upButton.setOnClickListener(v -> sendUpCommand());
        downButton.setOnClickListener(v -> sendDownCommand());
        leftButton.setOnClickListener(v -> sendLeftCommand());
        rightButton.setOnClickListener(v -> sendRightCommand());
        stopButton.setOnClickListener(v -> sendStopCommand());
        disconnectButton.setOnClickListener(v -> Disconnect());

        compositeDisposable.add(
                connectBluetooth(address)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        socket -> {
                            btSocket = socket;
                            msg("Connected.");
                        },
                        t -> {
                            msg("Connection Failed. Is it a SPP Bluetooth? Try again.");
                            t.printStackTrace();
                            finish();
                        }));
    }

    private void msg(String s) {
        Toast.makeText(getApplicationContext(),s,Toast.LENGTH_LONG).show();
    }

    private void Disconnect() {
        if (btSocket != null) {
            try {
                btSocket.close();
            } catch (IOException e) {
                msg("Error");
            }
        }
        finish();
    }

    private void sendUpCommand() {
        if (btSocket!=null) {
            try {
                btSocket.getOutputStream().write("f".getBytes());
            } catch (IOException e) {
                msg("Error");
            }
        }
    }

    private void sendDownCommand() {
        if (btSocket!=null) {
            try {
                btSocket.getOutputStream().write("b".getBytes());
            } catch (IOException e) {
                msg("Error");
            }
        }
    }

    private void sendLeftCommand() {
        if (btSocket!=null) {
            try {
                btSocket.getOutputStream().write("l".getBytes());
            } catch (IOException e) {
                msg("Error");
            }
        }
    }

    private void sendRightCommand() {
        if (btSocket!=null) {
            try {
                btSocket.getOutputStream().write("r".getBytes());
            } catch (IOException e) {
                msg("Error");
            }
        }
    }

    private void sendStopCommand() {
        if (btSocket!=null) {
            try {
                btSocket.getOutputStream().write("s".getBytes());
            } catch (IOException e) {
                msg("Error");
            }
        }
    }

    private static Single<BluetoothSocket> connectBluetooth(String address) { // todo progress dialog
        return Single.fromCallable(() -> {
            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (bluetoothAdapter == null) {
                throw new BluetoothNotAvailableException();
            }
            BluetoothDevice device = bluetoothAdapter.getRemoteDevice(address);
            BluetoothSocket btSocket = device.createInsecureRfcommSocketToServiceRecord(myUUID);
            btSocket.connect();
            return btSocket;
        });
    }
}
