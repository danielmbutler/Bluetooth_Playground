package com.dbtechprojects.bluetoothplayground;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;


import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int REQUEST_ENABLE_BT = 1;
    private static final int MULTIPLE_PERMISSIONS = 2 ;
    private BluetoothAdapter bluetoothAdapter;
    private static final String TAG = "MainActivity";
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private ListAdapter adapter;

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        if (!checkPermission()){
            Toast.makeText(this, "Permissions check failed", Toast.LENGTH_SHORT).show();
        } else {
            findIds();
            setupBluetoothAdapter();
            registerBluetoothReceiver();
        }


    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    private void findIds() {
        Button scanButton = findViewById(R.id.scanButton);
        scanButton.setOnClickListener(this);
        progressBar = findViewById(R.id.progressBar);
        recyclerView = findViewById(R.id.recyclerview);
        adapter = new ListAdapter();
        recyclerView.setAdapter(adapter);
    }

    private void setupBluetoothAdapter() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            // Device doesn't support Bluetooth
            Log.d(TAG, "device doesnt support bluetooth");
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.Q)
    private boolean checkPermission() {
        String[] permissions = new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION // multiple permission check
        };

        int result;
        List<String> listPermissionsNeeded = new ArrayList<>();

        for (String p : permissions) {
            result = ContextCompat.checkSelfPermission(getApplicationContext(), p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p);
            }
        }

        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }

    private void registerBluetoothReceiver() {
        // Register for broadcasts when a device is discovered.
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(receiver, filter);
    }

    private void findPairedDevices() {
        // query paired devices (bluetooth devices that client is already aware of)
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();

        if (pairedDevices.size() > 0) {
            // There are paired devices. Get the name and address of each paired device.
            for (BluetoothDevice device : pairedDevices) {
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address
                Log.d(TAG, "paired devices found :" + device.getName());
            }
        } else {
            Log.d(TAG, "findPairedDevices: no devices found");
        }
    }

    // Create a BroadcastReceiver for ACTION_FOUND.
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @RequiresApi(api = Build.VERSION_CODES.R)
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Discovery has found a device. Get the BluetoothDevice
                // object and its info from the Intent.
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address
                Log.d(TAG, "onReceive: Device found" + device.getName());

                // populate recyclerview;
                Device device2 = new Device(deviceName, deviceHardwareAddress);
                EventBus.getDefault().post(new DeviceEventHandler(device2));


            }
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NotNull String permissions[], int @NotNull [] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MULTIPLE_PERMISSIONS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {  // permissions granted.
                // setup scanning Activity
                findIds();
                setupBluetoothAdapter();
                registerBluetoothReceiver();
            } else {
                String perStr = "";
                for (String per : permissions) {
                    perStr += "\n" + per;
                }   // permissions list of don't granted permission
            }
        }
    }

        @Override
        public void onClick (View v){
            if (v.getId() == R.id.scanButton) {
                findPairedDevices();
                bluetoothAdapter.startDiscovery();
                if (!bluetoothAdapter.startDiscovery()) {
                    Log.d(TAG, "onClick: BluetoothDiscovery Failed");
                    Toast.makeText(this, "Scan Error", Toast.LENGTH_SHORT).show();
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                }
            }
        }

        @Override
        protected void onDestroy () {
            super.onDestroy();
            // Don't forget to unregister the ACTION_FOUND receiver.
            unregisterReceiver(receiver);
        }

    @Override
    protected void onStart() {
        super.onStart();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    public static class DeviceEventHandler {

        public final Device device;

        public DeviceEventHandler(Device device) {
            this.device = device;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    @RequiresApi(api = Build.VERSION_CODES.R)
    public void onDeviceFindEvent(DeviceEventHandler deviceEventHandler) {
        adapter.ListAdapterAddDevice(deviceEventHandler.device);
    }
    }



