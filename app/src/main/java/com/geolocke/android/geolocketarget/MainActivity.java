package com.geolocke.android.geolocketarget;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.geolocke.android.geolocketarget.services.ParseService;
import com.geolocke.android.geolocketarget.services.PositioningService;
import com.geolocke.android.geolocketarget.services.ScanService;
import com.geolocke.android.geolocketarget.services.TimerService;

public class MainActivity extends AppCompatActivity {

    public final int REQUEST_ENABLE_BT = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final BluetoothAdapter mBluetoothAdapter;

        int count = 0;

        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

/**
 * Checks if Bluetooth is enabled on device
 * Use this within and Activity
 */
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            /*Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBluetooth, REQUEST_ENABLE_BT);*/
            mBluetoothAdapter.enable();
        }

        startService(new Intent(getBaseContext(), ScanService.class));
        startService(new Intent(getBaseContext(), ParseService.class));
        startService(new Intent(getBaseContext(), TimerService.class));
        startService(new Intent(getBaseContext(), PositioningService.class));

        Intent i = new Intent(this,MapsActivity.class);
        startActivity(i);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(new Intent(getBaseContext(), ScanService.class));
        stopService(new Intent(getBaseContext(), ParseService.class));
        stopService(new Intent(getBaseContext(), TimerService.class));
        stopService(new Intent(getBaseContext(), PositioningService.class));
    }
}
