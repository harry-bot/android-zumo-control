package com.harrysoft.arduinocontrol;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.bluetooth.BluetoothDevice;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.harrysoft.androidbluetoothserial.BluetoothManager;

import java.util.List;

public class MainActivityViewModel extends AndroidViewModel {

    private BluetoothManager bluetoothManager;

    private MutableLiveData<List<BluetoothDevice>> pairedDeviceList = new MutableLiveData<>();

    private boolean viewModelSetup = false;

    public MainActivityViewModel(@NonNull Application application) {
        super(application);
    }

    public boolean setupViewModel() {
        if (!viewModelSetup) {
            viewModelSetup = true;

            bluetoothManager = BluetoothManager.getInstance();
            if (bluetoothManager == null) {
                Toast.makeText(getApplication(), R.string.no_bluetooth, Toast.LENGTH_LONG).show();
                return false;
            }
        }
        return true;
    }

    public void refreshPairedDevices() {
        pairedDeviceList.postValue(bluetoothManager.getPairedDevicesList());
    }

    @Override
    protected void onCleared() {
        bluetoothManager.close();
    }

    public LiveData<List<BluetoothDevice>> getPairedDeviceList() { return pairedDeviceList; }
}
