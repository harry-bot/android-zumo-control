package com.harrysoft.arduinocontrol;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.harrysoft.arduinocontrol.robot.RobotInterface;

public class TcpipSelectActivity extends AppCompatActivity {
    private EditText address;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_tcpip);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        address = findViewById(R.id.tcpip_address);

        findViewById(R.id.start_control_tcpip)
                .setOnClickListener((e) -> openCommunicationsActivity());
    }

    private void openCommunicationsActivity() {
        String addressString = address.getText().toString();
        Intent intent = new Intent(this, RobotControlActivity.class);
        intent.putExtra("device_name", addressString);
        intent.putExtra("device_protocol", RobotInterface.Protocol.TCPIP);
        intent.putExtra("device_address", addressString);
        startActivity(intent);
    }
}
