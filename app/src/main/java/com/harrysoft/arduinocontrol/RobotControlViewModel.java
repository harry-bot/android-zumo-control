package com.harrysoft.arduinocontrol;

import android.app.Application;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.harrysoft.arduinocontrol.robot.RobotInterface;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class RobotControlViewModel extends AndroidViewModel {
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Nullable
    private RobotInterface robot;

    private MutableLiveData<ConnectionStatus> connectionStatusData = new MutableLiveData<>();
    private MutableLiveData<String> deviceNameData = new MutableLiveData<>();

    private boolean connectionAttemptedOrMade = false;
    private boolean viewModelSetup = false;

    public RobotControlViewModel(@NonNull Application application) {
        super(application);
    }

    public boolean setupViewModel(String deviceName, RobotInterface.Protocol protocol, String address) {
        if (!viewModelSetup) {
            viewModelSetup = true;

            try {
                robot = RobotInterface.open(protocol, address);
            } catch (Exception e) {
                e.printStackTrace();
                toast(R.string.connection_failed);
                return false;
            }

            deviceNameData.postValue(deviceName);
            connectionStatusData.postValue(ConnectionStatus.DISCONNECTED);
        }
        return true;
    }

    public void updateControls(float xValue, float yValue) {
        if (robot != null) {
            RobotUtils.MotorSpeedConfig speeds = RobotUtils.calculateMotorSpeeds(xValue, yValue);
            try {
                robot.setMotorSpeeds(speeds.leftSpeed, speeds.rightSpeed);
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getApplication(), "Error sending to device", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void connect() {
        if (!connectionAttemptedOrMade && robot != null) {
            compositeDisposable.add(
                    robot.connect()
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(this::onConnected, t -> {
                                t.printStackTrace();
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
            if (robot != null) {
                try {
                    robot.disconnect();
                } catch (Exception e) {
                    // Ignored
                }
            }
            connectionStatusData.postValue(ConnectionStatus.DISCONNECTED);
        }
    }

    private void onConnected() {
        connectionStatusData.postValue(ConnectionStatus.CONNECTED);
        toast(R.string.connected);
    }

    @Override
    protected void onCleared() {
        compositeDisposable.dispose();
    }

    private void toast(@StringRes int messageResource) { Toast.makeText(getApplication(), messageResource, Toast.LENGTH_LONG).show(); }

    public LiveData<ConnectionStatus> getConnectionStatus() { return connectionStatusData; }

    public LiveData<String> getDeviceName() { return deviceNameData; }

    enum ConnectionStatus { DISCONNECTED, CONNECTING, CONNECTED }
}
