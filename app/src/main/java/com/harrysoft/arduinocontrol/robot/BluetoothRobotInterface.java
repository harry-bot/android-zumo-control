package com.harrysoft.arduinocontrol.robot;

import androidx.annotation.Nullable;

import com.harrysoft.androidbluetoothserial.BluetoothManager;
import com.harrysoft.androidbluetoothserial.BluetoothSerialDevice;

import java.io.OutputStream;

import io.reactivex.Completable;
import io.reactivex.Single;

public class BluetoothRobotInterface implements RobotInterface {
    private final String mac;

    @Nullable
    private BluetoothManager bluetoothManager;
    @Nullable
    private BluetoothSerialDevice device;
    @Nullable
    private OutputStream deviceOutputStream;

    public BluetoothRobotInterface(String mac) {
        this.mac = mac;
    }

    @Override
    public Completable connect() {
        return Single.fromCallable(BluetoothManager::getInstance)
                .flatMap((bluetoothManager) -> {
                    this.bluetoothManager = bluetoothManager;
                    return bluetoothManager.openSerialDevice(mac);
                })
                .flatMapCompletable((device) -> Completable.fromAction(() -> {
                    this.device = device;
                    this.deviceOutputStream = device.getOutputStream();
                }));
    }

    @Override
    public void setMotorSpeeds(float leftMotorSpeed, float rightMotorSpeed) throws Exception {
        if (deviceOutputStream == null) throw new IllegalStateException();
        deviceOutputStream.write(new byte[]{(byte) leftMotorSpeed, (byte) rightMotorSpeed, ';'});
    }

    @Override
    public void disconnect() {
        if (bluetoothManager != null) {
            if (device != null) {
                bluetoothManager.closeDevice(device);
                device = null;
            }
            bluetoothManager.close();
        }
    }
}
