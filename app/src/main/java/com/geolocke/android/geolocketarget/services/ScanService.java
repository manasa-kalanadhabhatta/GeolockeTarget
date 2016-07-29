package com.geolocke.android.geolocketarget.services;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Manasa on 06-07-2016.
 */
public class ScanService extends Service {

    BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    JSONArray mDeviceList = new JSONArray();
    public static int sClear = 0;

    @Nullable
    @Override
    public IBinder onBind(Intent pIntent) {
        return null;
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

                            //Structure
                            try {
                                if(sClear ==1){
                                    mDeviceList = new JSONArray();
                                    sClear =0;
                                }
                                JSONObject deviceDetails = new JSONObject();
                                deviceDetails.put("FRIENDLY_NAME",device.getName());
                                deviceDetails.put("MAC_ADDRESS",device.getAddress());
                                deviceDetails.put("UUID",uuid);
                                deviceDetails.put("MAJOR",major);
                                deviceDetails.put("MINOR",minor);
                                deviceDetails.put("RSSI",rssi);
                                deviceDetails.put("TX_POWER",txPower);
                                mDeviceList.put(deviceDetails);

                                Intent intent = new Intent();
                                intent.putExtra("SCAN_LIST", mDeviceList.toString());
                                intent.setAction("SCANLIST_BROADCAST");
                                sendBroadcast(intent);

                                //Toast.makeText(getApplicationContext(), "First Broadcast Sent.", Toast.LENGTH_SHORT).show();

                            }catch (JSONException e){
                                e.printStackTrace();
                            }
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
