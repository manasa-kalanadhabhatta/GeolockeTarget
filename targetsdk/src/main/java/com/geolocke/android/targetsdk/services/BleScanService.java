package com.geolocke.android.targetsdk.services;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.geolocke.android.targetsdk.beans.BleBeaconScan;
import com.geolocke.android.targetsdk.beans.IBeacon;
import com.geolocke.android.targetsdk.beans.IBeaconScan;

import java.util.ArrayList;
/**
 * Created by Manasa on 06-07-2016.
 */
public class BleScanService extends Service {

    BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    ArrayList<IBeacon> mIBeaconArrayList = new ArrayList<IBeacon>();
    ArrayList<Integer> mRssiArrayList = new ArrayList<Integer>();
    BleBeaconScan mBleBeaconScan;
    public static int sClear = 0;
    private IBinder mBinder = new BleScanServiceBinder();



    public class BleScanServiceBinder extends Binder {
        public BleScanService getService() {
            return BleScanService.this;
        }
    }

    @Override
    public void onRebind(Intent intent) {
        Log.v("", "in onRebind");
        super.onRebind(intent);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.v("", "in onUnbind");
        return true;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent pIntent) {
        Log.v("BLEBIND", "in onBind");
        return mBinder;
    }

    @Override
    public int onStartCommand(final Intent pIntent, int pFlags, int pStartId) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                final BluetoothAdapter.LeScanCallback leScanCallback = new BluetoothAdapter.LeScanCallback() {
                    @Override
                    public void onLeScan(final BluetoothDevice device, final int rssi, final byte[] scanRecord) {
                        int startByte = 2;
                        boolean patternFound = false;
                        while (startByte <= 5) {
                            if (    ((int) scanRecord[startByte + 2] & 0xff) == 0x02 && //Identifies an IBeacon
                                    ((int) scanRecord[startByte + 3] & 0xff) == 0x15) { //Identifies correct data length
                                patternFound = true;
                                break;
                            }
                            startByte++;
                        }

                        if (patternFound) {
                            //Convert to hex String
                            byte[] uuidBytes = new byte[16];
                            System.arraycopy(scanRecord, startByte+4, uuidBytes, 0, 16);
                            String hexString = bytesToHex(uuidBytes);

                            //Here is your UUID
                            String uuid =  hexString.substring(0,8) + "-" +
                                    hexString.substring(8,12) + "-" +
                                    hexString.substring(12,16) + "-" +
                                    hexString.substring(16,20) + "-" +
                                    hexString.substring(20,32);

                            //Here is your Major value
                            int major = (scanRecord[startByte+20] & 0xff) * 0x100 + (scanRecord[startByte+21] & 0xff);

                            //Here is your Minor value
                            int minor = (scanRecord[startByte+22] & 0xff) * 0x100 + (scanRecord[startByte+23] & 0xff);
                            byte txPower = scanRecord[startByte+24];


                            if(sClear ==1){
                                mIBeaconArrayList.clear();
                                mRssiArrayList.clear();
                                sClear =0;
                            }

                            IBeacon iBeacon = new IBeacon(device.getAddress(),uuid,major,minor,device.getName(),txPower);
                            mIBeaconArrayList.add(iBeacon);
                            mRssiArrayList.add(rssi);

                            mBleBeaconScan = new BleBeaconScan(mIBeaconArrayList,mRssiArrayList);

                            Intent intent = new Intent();
                            intent.putExtra("BLE_SCAN",mBleBeaconScan);
                            intent.setAction("BLE_SCAN_BROADCAST");
                            sendBroadcast(intent);
                        }
                    }
                };

                mBluetoothAdapter.startLeScan(leScanCallback);

            }
        }).start();

        return START_STICKY;
    }

    public static String bytesToHex(byte[] pBytes) {
        final char[] hexArray = "0123456789ABCDEF".toCharArray();
        char[] hexChars = new char[pBytes.length * 2];
        for ( int j = 0; j < pBytes.length; j++ ) {
            int v = pBytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopSelf();
    }
}
