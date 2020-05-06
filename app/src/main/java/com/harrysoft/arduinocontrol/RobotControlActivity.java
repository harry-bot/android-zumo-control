package com.harrysoft.arduinocontrol;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import com.harrysoft.arduinocontrol.robot.RobotInterface;
import com.harrysoft.joystickview.JoystickView;

public class RobotControlActivity extends AppCompatActivity {

    private TextView connectionText;
    private Button connectButton;
    private JoystickView joystick;

    private RobotControlViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Setup Activity
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_robot_control);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Setup ViewModel
        viewModel = ViewModelProviders.of(this).get(RobotControlViewModel.class);

        if (!viewModel.setupViewModel(getIntent().getStringExtra("device_name"), (RobotInterface.Protocol) getIntent().getSerializableExtra("device_protocol"), getIntent().getStringExtra("device_address"))) {
            finish();
            return;
        }

        // Setup Views
        connectionText = findViewById(R.id.communicate_connection_text);
        connectButton = findViewById(R.id.communicate_connect);
        joystick = findViewById(R.id.robot_control_joystick);
        joystick.setJoystickListener((x, y, id) -> viewModel.updateControls(x, y));

        // Start observing
        viewModel.getConnectionStatus().observe(this, this::onConnectionStatus);
        viewModel.getDeviceName().observe(this, name -> setTitle(getString(R.string.device_name_format, name)));
    }

    private void onConnectionStatus(RobotControlViewModel.ConnectionStatus connectionStatus) {
        switch (connectionStatus) {
            case CONNECTED:
                connectionText.setText(R.string.status_connected);
                joystick.setEnabled(true);
                connectButton.setEnabled(true);
                connectButton.setText(R.string.disconnect);
                connectButton.setOnClickListener(v -> viewModel.disconnect());
                break;

            case CONNECTING:
                connectionText.setText(R.string.status_connecting);
                joystick.setEnabled(false);
                connectButton.setEnabled(false);
                connectButton.setText(R.string.connect);
                break;

            case DISCONNECTED:
                connectionText.setText(R.string.status_disconnected);
                joystick.setEnabled(false);
                connectButton.setEnabled(true);
                connectButton.setText(R.string.connect);
                connectButton.setOnClickListener(v -> viewModel.connect());
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
