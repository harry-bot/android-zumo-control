package com.harrysoft.arduinocontrol;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.harrysoft.androidbluetoothserial.BluetoothManager;
import com.harrysoft.androidbluetoothserial.BluetoothSerialDevice;

import java.text.DecimalFormat;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class RobotControlViewModel extends AndroidViewModel {

    private final DecimalFormat decimalFormat = new DecimalFormat("#");
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    private BluetoothManager bluetoothManager;

    @Nullable
    private BluetoothSerialDevice serialDevice;

    private MutableLiveData<ConnectionStatus> connectionStatusData = new MutableLiveData<>();
    private MutableLiveData<String> deviceNameData = new MutableLiveData<>();

    private String mac;

    private boolean connectionAttemptedOrMade = false;
    private boolean viewModelSetup = false;

    public RobotControlViewModel(@NonNull Application application) {
        super(application);
    }

    public boolean setupViewModel(String deviceName, String mac) {
        if (!viewModelSetup) {
            viewModelSetup = true;

            bluetoothManager = BluetoothManager.getInstance();
            if (bluetoothManager == null) {
                toast(R.string.bluetooth_unavailable);
                return false;
            }

            this.mac = mac;

            deviceNameData.postValue(deviceName);
            connectionStatusData.postValue(ConnectionStatus.DISCONNECTED);
        }
        return true;
    }

    public void updateControls(float xValue, float yValue) {
        RobotUtils.MotorSpeedConfig speeds = RobotUtils.calculateMotorSpeeds(xValue, yValue);
        sendMessage("setl" + decimalFormat.format(speeds.leftSpeed) + "lr" + decimalFormat.format(speeds.rightSpeed) + "r");
    }

    public void connect() {
        if (!connectionAttemptedOrMade) {
            compositeDisposable.add(
                    bluetoothManager.openSerialDevice(mac)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(this::onConnected, t -> {
                                toast(R.string.connection_failed);
                                connectionAttemptedOrMade = false;
                                connectionStatusData.postValue(ConnectionStatus.DISCONNECTED);
                            }));
            connectionAttemptedOrMade = true;
            connectionStatusData.postValue(ConnectionStatus.CONNECTING);
        }
    }

    public void disconnect() {
        if (connectionAttemptedOrMade) {
            connectionAttemptedOrMade = false;
            if (serialDevice != null) {
                bluetoothManager.closeDevice(serialDevice);
            }
            serialDevice = null;
            connectionStatusData.postValue(ConnectionStatus.DISCONNECTED);
        }
    }

    private void onConnected(BluetoothSerialDevice device) {
        this.serialDevice = device;
        if (serialDevice != null) {
            connectionStatusData.postValue(ConnectionStatus.CONNECTED);
            compositeDisposable.add(
                    serialDevice.openMessageStream()
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(this::onMessageReceived, Throwable::printStackTrace));
            toast(R.string.connected);
        } else {
            toast(R.string.connection_failed);
            connectionStatusData.postValue(ConnectionStatus.DISCONNECTED);
        }
    }

    private void onMessageReceived(String message) {
        // todo handle messages
    }

    private void onMessageSent(String message) {
        // todo
    }

    public void sendMessage(String message) {
        if (serialDevice != null && !TextUtils.isEmpty(message)) {
            compositeDisposable.add(
                    serialDevice.send(message)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(() -> onMessageSent(message), t -> toast(R.string.message_send_error)));
        }
    }

    @Override
    protected void onCleared() {
        compositeDisposable.dispose();
        bluetoothManager.close();
    }

    private void toast(@StringRes int messageResource) { Toast.makeText(getApplication(), messageResource, Toast.LENGTH_LONG).show(); }

    public LiveData<ConnectionStatus> getConnectionStatus() { return connectionStatusData; }

    public LiveData<String> getDeviceName() { return deviceNameData; }

    enum ConnectionStatus { DISCONNECTED, CONNECTING, CONNECTED }
}
