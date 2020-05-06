package com.harrysoft.arduinocontrol;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        findViewById(R.id.tcpip_control_button)
                .setOnClickListener((e) -> startActivity(new Intent(this, TcpipSelectActivity.class)));

        findViewById(R.id.bluetooth_control_button)
                .setOnClickListener((e) -> startActivity(new Intent(this, BluetoothSelectActivity.class)));
    }
}
