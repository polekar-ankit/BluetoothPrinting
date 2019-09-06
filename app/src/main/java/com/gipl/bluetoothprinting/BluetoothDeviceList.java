package com.gipl.bluetoothprinting;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.Set;

public class BluetoothDeviceList extends AppCompatActivity implements DeviceListAdapter.BtDeviceClickListener {

    public static final String KEY_BT_DEVICE = "KEY_BT_DEVICE";
    private RecyclerView rvPaired, rvNew;

    private DeviceListAdapter pairedDeviceArrayAdapter;
    private DeviceListAdapter scanDeviceArrayAdapter;
    private BluetoothAdapter bluetoothAdapter;
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Discovery has found a device. Get the BluetoothDevice
                // object and its info from the Intent.
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address

                scanDeviceArrayAdapter.addItem(new BTDevice(deviceName, deviceHardwareAddress));
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_device_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        rvPaired = findViewById(R.id.rv_paired);
        rvNew = findViewById(R.id.rv_new);

        pairedDeviceArrayAdapter = new DeviceListAdapter(this);
        rvPaired.setLayoutManager(new LinearLayoutManager(this));
        rvPaired.setAdapter(pairedDeviceArrayAdapter);

        scanDeviceArrayAdapter = new DeviceListAdapter(this);
        rvNew.setLayoutManager(new LinearLayoutManager(this));
        rvNew.setAdapter(scanDeviceArrayAdapter);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> pairedDeviceSet = bluetoothAdapter.getBondedDevices();
        if (pairedDeviceSet.size() > 0) {
            for (BluetoothDevice bluetoothDevice :
                    pairedDeviceSet) {
                pairedDeviceArrayAdapter.addItem(new BTDevice(bluetoothDevice.getName(), bluetoothDevice.getAddress()));
            }
        }


        findViewById(R.id.btn_scan).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bluetoothAdapter.startDiscovery();
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(receiver, filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
        if (bluetoothAdapter != null) {
            bluetoothAdapter.cancelDiscovery();
        }
    }

    @Override
    public void onDeviceClick(BTDevice btDevice) {
        Intent intent = new Intent();
        intent.putExtra(KEY_BT_DEVICE, btDevice);
        setResult(RESULT_OK, intent);
        finish();
    }
}
