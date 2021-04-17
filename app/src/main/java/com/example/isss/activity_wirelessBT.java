package com.example.isss;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class activity_wirelessBT extends AppCompatActivity {
    Button enableBluetooth;
    BluetoothAdapter adapter;
    Intent intentOpenBluetoothSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wireless_bt);

        adapter = BluetoothAdapter.getDefaultAdapter();

        enableBluetooth = findViewById(R.id.btn_enableBluetooth);
        if (adapter == null) {
            // Device does not support Bluetooth
            Toast.makeText(getApplicationContext(), "This device does not support Bluetooth", Toast.LENGTH_SHORT).show();
        } else if (!adapter.isEnabled()) {
            enableBluetooth.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    intentOpenBluetoothSettings = new Intent(getApplicationContext(), activity_devices.class);
                    intentOpenBluetoothSettings.setAction(android.provider.Settings.ACTION_BLUETOOTH_SETTINGS);
                    intentOpenBluetoothSettings.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                    if (!adapter.isEnabled()) {
                        adapter.enable();
                    }
                    startActivity(intentOpenBluetoothSettings);
                }
            });
        } else {
            // Bluetooth is enabled
            intentOpenBluetoothSettings = new Intent(getApplicationContext(), activity_devices.class);
            startActivity(intentOpenBluetoothSettings);
        }
    }
}