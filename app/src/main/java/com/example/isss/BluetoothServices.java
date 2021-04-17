package com.example.isss;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

public class BluetoothServices extends Service {
    static final int State_Listening = 1;
    static final int State_Connecting = 2;
    static final int State_Connected = 3;
    static final int State_Connection_failed = 4;
    static final int State_Message_Received = 5;
    private static final UUID device_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private final BroadcastReceiver mBroadcastReceiver1 = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                switch (state) {
                    case BluetoothAdapter.STATE_OFF:
                        Toast.makeText(context, "Bluetooth off", Toast.LENGTH_LONG);
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        Toast.makeText(context, "Turning Bluetooth off", Toast.LENGTH_LONG);
                        break;
                    case BluetoothAdapter.STATE_ON:
                        Toast.makeText(context, "Bluetooth on", Toast.LENGTH_LONG);
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        Toast.makeText(context, "Turning Bluetooth on", Toast.LENGTH_LONG);
                        break;
                }

            }
        }
    };
    private final BroadcastReceiver mBroadcastReceiver2 = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED)) {
                int mode = intent.getIntExtra(BluetoothAdapter.EXTRA_SCAN_MODE, BluetoothAdapter.ERROR);
                switch (mode) {
                    case BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE:
                        Toast.makeText(context, "Conn Disc", Toast.LENGTH_SHORT).show();
                        break;
                    case BluetoothAdapter.SCAN_MODE_CONNECTABLE:
                    case BluetoothAdapter.SCAN_MODE_NONE:
                        intent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                        //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        Toast.makeText(context, "None", Toast.LENGTH_SHORT).show();
                        startActivity(intent);
                        break;
                }
            }
        }
    };
    BluetoothDevice bluetoothDevice;
    Receiver receiver;
    vibrate_sound vibrateSound;
    private final BroadcastReceiver mBroadcastReceiver3 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            switch (action) {
                case BluetoothDevice.ACTION_ACL_CONNECTED:
                    bluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    Toast.makeText(context, "Connected to : " + bluetoothDevice.getName(), Toast.LENGTH_SHORT).show();
                    Log.d("BroadcastActions", "Connected to " + bluetoothDevice.getName());

                    vibrateSound = new vibrate_sound();
                    vibrateSound.vibrate(getApplicationContext());
                    vibrateSound.sound(getApplicationContext(), 1);

                    break;

                case BluetoothDevice.ACTION_ACL_DISCONNECTED:
                    bluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                    vibrateSound = new vibrate_sound();
                    vibrateSound.vibrate(getApplicationContext());
                    vibrateSound.sound(getApplicationContext(), 2);

                    Toast.makeText(context, "Disconnected from : " + bluetoothDevice.getName(), Toast.LENGTH_SHORT).show();

                    Intent intentActivity = new Intent(BluetoothServices.this, activity_devices.class);
                    intentActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intentActivity);

                    break;
            }
        }
    };
    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message message) {
            switch (message.what) {
//                case State_Listening:
//                    txt_status.setText("Status: Listening");
//                    break;
//                case State_Connecting:
//                    txt_status.setText("Status: Connecting");
//                    break;
//                case State_Connected:
//                    txt_status.setText("Status: Connected");
//                    break;
//                case State_Connection_failed:
//                    txt_status.setText("Status: Connection Failed");
//                    break;
                case State_Message_Received:
                    byte[] readBuffer = (byte[]) message.obj;
                    String tempMsg = new String(readBuffer, 0, message.arg1);
                    tempMsg = tempMsg.toLowerCase().replace(" ", "");

//                    System.out.println(tempMsg);

                    Intent intent = new Intent("Action1");
                    Bundle bundle = new Bundle();
                    bundle.putString("tagID", tempMsg);
                    intent.putExtras(bundle);
                    LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);

                    break;
            }
            return true;
        }
    });

    @Nullable
    @Override
    public IBinder onBind(Intent intentT) {
        return null;
    }

    @Override
    public void onCreate() {
//        Toast.makeText(this, "Service Created", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onStart(Intent intent, int startId) {
//        Toast.makeText(this, "Service Started", Toast.LENGTH_LONG).show();

        IntentFilter filter1 = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(mBroadcastReceiver1, filter1);

        IntentFilter filter2 = new IntentFilter();
        filter2.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter2.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        filter2.addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
        registerReceiver(mBroadcastReceiver2, filter2);

        IntentFilter filter3 = new IntentFilter();
        filter3.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        filter3.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        registerReceiver(mBroadcastReceiver3, filter3);

        BluetoothDevice bluetoothDevice = intent.getExtras().getParcelable("btdevice");
        Client client = new Client(bluetoothDevice);
        client.start();
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(mBroadcastReceiver1);
        unregisterReceiver(mBroadcastReceiver2);
        unregisterReceiver(mBroadcastReceiver3);
//        Toast.makeText(this, "Service Destroyed", Toast.LENGTH_LONG).show();
    }

    private class Client extends Thread {
        private BluetoothDevice device;
        private BluetoothSocket socket;

        private Client(BluetoothDevice device1) {
            device = device1;
            try {
                socket = device.createInsecureRfcommSocketToServiceRecord(device_UUID);
            } catch (IOException e) {
                Log.i("data", "error :" + e);
                e.printStackTrace();
            }
        }

        public void run() {
            try {
                socket.connect();
                Message msg = Message.obtain();
                msg.what = State_Connected;
                handler.sendMessage(msg);

                Intent intentActivity = new Intent(BluetoothServices.this, activity_scannedProduct.class);
                intentActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intentActivity);

                receiver = new Receiver(socket);
                receiver.start();

            } catch (IOException e) {
                Message msg = Message.obtain();
                msg.what = State_Connection_failed;
                handler.sendMessage(msg);
                Log.i("data", "error :" + e);
                e.printStackTrace();

            }
        }

    }

    private class Receiver extends Thread {
        private final BluetoothSocket bluetoothSocket;
        private final InputStream inputStream;

        private Receiver(BluetoothSocket Socket) {
            bluetoothSocket = Socket;
            InputStream tempIn = null;

            try {
                tempIn = bluetoothSocket.getInputStream();

            } catch (IOException e) {
                Log.i("data", "error :" + e);
                e.printStackTrace();
            }

            inputStream = tempIn;
        }

        public void run() {
            byte[] buffer = new byte[1024];
            int bytes;

            while (true) {
                try {
                    bytes = inputStream.read(buffer);
                    handler.obtainMessage(State_Message_Received, bytes, 0, buffer).sendToTarget();

                } catch (IOException e) {
                    Log.i("data", "error :" + e);
                    e.printStackTrace();
                }
            }
        }
    }
}