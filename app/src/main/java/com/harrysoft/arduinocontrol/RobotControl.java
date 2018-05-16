package com.harrysoft.arduinocontrol;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.AsyncTask;
import java.io.IOException;
import java.util.UUID;

public class RobotControl extends AppCompatActivity {

    Button upButton, downButton, leftButton, rightButton, stopButton, disconnectButton;
    String address = null;
    private ProgressDialog progress;
    BluetoothAdapter myBluetooth = null;
    BluetoothSocket btSocket = null;
    private boolean isBtConnected = false;
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    ConnectBT mConnectBT = new ConnectBT();

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

        mConnectBT.execute();
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


    private class ConnectBT extends AsyncTask<Void, Void, Void> {
        private boolean ConnectSuccess = true;

        @Override
        protected void onPreExecute() {
            progress = ProgressDialog.show(RobotControl.this, "Connecting...", "Please wait!!!");
        }

        @Override
        protected Void doInBackground(Void... devices) {
            try {
                if (btSocket == null || !isBtConnected) {
                    myBluetooth = BluetoothAdapter.getDefaultAdapter();
                    Log.e("gay", "address: " + address);
                    BluetoothDevice dispositivo = myBluetooth.getRemoteDevice(address);
                    btSocket = dispositivo.createInsecureRfcommSocketToServiceRecord(myUUID);
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    btSocket.connect();
                }
            } catch (IOException e) {
                ConnectSuccess = false;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (!ConnectSuccess) {
                msg("Connection Failed. Is it a SPP Bluetooth? Try again.");
                finish();
            }
            else {
                msg("Connected.");
                isBtConnected = true;
            }
            progress.dismiss();
        }
    }
}
