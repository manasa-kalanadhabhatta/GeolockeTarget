package com.geolocke.android.geolocketarget.services;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.geolocke.android.geolocketarget.beans.Location;
import com.geolocke.android.geolocketarget.beans.Position;

import java.util.ArrayList;

/**
 * Created by Manasa on 31-07-2016.
 */
public class LocalizingService extends Service {
    public static final Double RADIANS = 57.2957795;

    PositionReceiver mPositionReceiver;
    private static int sCount;
    ArrayList<Position> mPositionList, mPositionSet;
    Location mLocation;

    @Nullable
    @Override
    public IBinder onBind(Intent pIntent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent pIntent, int pFlags, int pStartId) {
        sCount = 0;
        mPositionList = new ArrayList<Position>();
        mPositionSet = new ArrayList<Position>();

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("POSITION_BROADCAST");
        mPositionReceiver = new PositionReceiver();
        registerReceiver(mPositionReceiver,intentFilter);

        return START_STICKY;
    }

    public class PositionReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context pContext, Intent pIntent) {
            Position position = pIntent.getExtras().getParcelable("POSITION");
            if(sCount==3){
                mPositionSet.clear();
                for(int i=0; i<mPositionList.size(); i++){
                    mPositionSet.add(mPositionList.get(i));
                }
                localize();

                mPositionList.clear();
                mPositionList.add(position);
                sCount = 1;
            }else{
                mPositionList.add(position);
                sCount++;
            }
        }
    }

    public void localize(){
        double centroidLat = 0.0, centroidLng = 0.0;
        for(int i=0; i<3; i++){
            Position position = mPositionSet.get(i);
            double lat = position.getLatitude();
            double lng = position.getLongitude();
            double latlng[] = LatLngToXY(lat,lng);
            centroidLat = centroidLat+latlng[0];
            centroidLng = centroidLng+latlng[1];
        }
        centroidLat = centroidLat/3;
        centroidLng = centroidLng/3;
        double centroid[] = XYToLatLng(centroidLat,centroidLng);
        mLocation = new Location(mPositionSet,centroid[0],centroid[1]);

        Intent intent = new Intent();
        intent.putExtra("LOCATION",mLocation);
        intent.setAction("LOCATION_BROADCAST");
        sendBroadcast(intent);
    }

    public double[] LatLngToXY(Double pLat, Double pLng){
        Double angle = DEG_TO_RADIANS(0.0);
        Double xx = (pLng)*METERS_DEGLON(0.0);
        Double yy = (pLat)*METERS_DEGLAT(0.0);

        Double r = Math.sqrt(xx*xx + yy*yy);
        if(r!=0.0)
        {
            Double ct = xx/r;
            Double st = yy/r;
            xx = r * ( (ct * Math.cos(angle)) + (st * Math.sin(angle)) );
            yy = r * ( (st * Math.cos(angle)) - (ct * Math.sin(angle)) );
        }
        Double pxpos_mtrs = xx;
        Double pypos_mtrs = yy;

        double[] FinalLatLng = new double[]{Double.parseDouble(pxpos_mtrs.toString().substring(0,9)),Double.parseDouble(pypos_mtrs.toString().substring(0,9))};
        return  FinalLatLng;

    }

    public double[] XYToLatLng(Double pX, Double pY){
     /* X,Y to Lat/Lon Coordinate Translation  */
        Double angle = DEG_TO_RADIANS(0.0);
        Double pxpos_mtrs = pX;
        Double pypos_mtrs = pY;
        Double xx = pxpos_mtrs;
        Double yy = pypos_mtrs;
        Double r = Math.sqrt(xx*xx + yy*yy);

        if(r!=0.0)
        {
            Double ct = xx/r;
            Double st = yy/r;
            xx = r * ( (ct * Math.cos(angle))+ (st * Math.sin(angle)) );
            yy = r * ( (st * Math.cos(angle))- (ct * Math.sin(angle)) );
        }

        Double plon = xx/METERS_DEGLON(0.0);
        Double plat = yy/METERS_DEGLAT(0.0);

        double[] FinalXY = new double[]{Double.parseDouble(plat.toString().substring(0,9)) , Double.parseDouble(plon.toString().substring(0,9))};
        return FinalXY;
    }

    public Double METERS_DEGLON(Double pX)
    {

        Double d2r=DEG_TO_RADIANS(pX);
        return((111415.13 * Math.cos(d2r))- (94.55 * Math.cos(3.0*d2r)) + (0.12 * Math.cos(5.0*d2r)));
    }

    public Double  METERS_DEGLAT(Double pX)
    {
        Double  d2r=DEG_TO_RADIANS(pX);
        return(111132.09 - (566.05 * Math.cos(2.0*d2r))+ (1.20 * Math.cos(4.0*d2r)) - (0.002 * Math.cos(6.0*d2r)));

    }
    public Double DEG_TO_RADIANS(Double pX)
    {
        return (pX/RADIANS);
    }

    public Double calculateDistance(Double pRssi, int pTxPower){
        if(pRssi == 0)
            return -1.0;
        Double ratio = pRssi*1.0/pTxPower;
        if(ratio<1.0){
            return Math.pow(ratio, 10);
        }else {
            return ((0.89976)*Math.pow(ratio,7.7095) + 0.111);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mPositionReceiver);
        stopSelf();
    }
}
