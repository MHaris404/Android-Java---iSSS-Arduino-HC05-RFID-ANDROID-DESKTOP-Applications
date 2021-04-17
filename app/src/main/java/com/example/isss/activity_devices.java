package com.example.isss;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Set;

public class activity_devices extends AppCompatActivity {

    Button btn_pairedDevices;
    TextView txt_status, txt_tagId;
    ListView lst_pairedDevices;
    RelativeLayout loadingPanel;

    BluetoothAdapter bluetoothAdapter;
    BluetoothDevice[] btDevicesArray;

    int Request_enable_bluetooth = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_devices);

        btn_pairedDevices = findViewById(R.id.btn_pairedDevices);
        txt_status = findViewById(R.id.txt_status);
        txt_tagId = findViewById(R.id.txt_tagId);
        lst_pairedDevices = findViewById(R.id.lst_pairedDeives);
        loadingPanel = findViewById(R.id.loadingPanel);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        loadingPanel.setVisibility(View.GONE);

        listListener();
    }

    @Override
    protected void onStart() {
        super.onStart();

        txt_status.setText("Status: Not Connected");

        if (!bluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, Request_enable_bluetooth);
            Toast.makeText(this, "Bluetooth Switched On", Toast.LENGTH_LONG).show();
        }
    }

    private void listListener() {
        btn_pairedDevices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Set<BluetoothDevice> bt = bluetoothAdapter.getBondedDevices();
                String[] strings = new String[bt.size()];
                btDevicesArray = new BluetoothDevice[bt.size()];
                int index = 0;

                if (bt.size() > 0) {
                    for (BluetoothDevice device : bt) {
                        btDevicesArray[index] = device;
                        strings[index] = device.getName();
                        index++;

                    }
                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(activity_devices.this, android.R.layout.simple_list_item_1, strings) {
                        @NonNull
                        @Override
                        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                            View view = super.getView(position, convertView, parent);
                            TextView text = (TextView) view.findViewById(android.R.id.text1);
                            text.setTextColor(Color.LTGRAY);
                            return view;
                        }
                    };
                    arrayAdapter.notifyDataSetChanged();
                    lst_pairedDevices.setAdapter(arrayAdapter);
                } else {
                    Toast.makeText(activity_devices.this, "No Paired Devices", Toast.LENGTH_SHORT).show();
                }
            }
        });

        lst_pairedDevices.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

//                txt_status.setText("Status: Connecting");

                loadingPanel.setVisibility(View.VISIBLE);

                Intent intentService = new Intent(activity_devices.this, BluetoothServices.class);
                intentService.putExtra("btdevice", btDevicesArray[i]);
                startService(intentService);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadingPanel.setVisibility(View.GONE);
        txt_status.setText("Status: Not Connected");
    }

    @Override
    protected void onPause() {
        super.onPause();
        loadingPanel.setVisibility(View.GONE);
        txt_status.setText("Status: Connecting");
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        if (bluetoothAdapter.isEnabled()) {
            bluetoothAdapter.disable();
//            Toast.makeText(this, "Devices onRestart BT disabled", Toast.LENGTH_LONG).show();
            bluetoothAdapter.enable();
//            Toast.makeText(this, "Devices onRestart BT enabled", Toast.LENGTH_LONG).show();
            txt_status.setText("Status: Not Connected");
        }

    }
}