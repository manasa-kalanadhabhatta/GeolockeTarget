package com.geolocke.android.targetsdk.services;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.geolocke.android.targetsdk.beans.GeolockeIBeacon;
import com.geolocke.android.targetsdk.beans.GeolockeIBeaconScan;
import com.geolocke.android.targetsdk.beans.Position;
import com.geolocke.android.targetsdk.trilateration.NonLinearLeastSquaresSolver;
import com.geolocke.android.targetsdk.trilateration.TrilaterationFunction;

import org.apache.commons.math3.fitting.leastsquares.LeastSquaresOptimizer;
import org.apache.commons.math3.fitting.leastsquares.LevenbergMarquardtOptimizer;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

import java.util.ArrayList;

/**
 * Created by Manasa on 12-07-2016.
 */
public class PositioningService extends Service {
    public static final Double RADIANS = 57.2957795;
    private static final int MAX_BEACONS = 10;
    ParsedListReceiver mParsedListReceiver;
    double[][] mLatLngArray = new double[MAX_BEACONS][2];
    double[] mDistanceArray = new double[MAX_BEACONS];
    int mIndex;
    Position mPosition;


    @Nullable
    @Override
    public IBinder onBind(Intent pIntent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent pIntent, int pFlags, int pStartId) {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("GEOLOCKE_IBEACON_SCAN_BROADCAST");
        mParsedListReceiver = new ParsedListReceiver();
        registerReceiver(mParsedListReceiver, intentFilter);

        return START_STICKY;
    }

    public class ParsedListReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context pContext, Intent pIntent) {

            GeolockeIBeaconScan geolockeIBeaconScan = pIntent.getExtras().getParcelable("GEOLOCKE_IBEACON_SCAN");
            ArrayList<GeolockeIBeacon> geolockeIBeaconList = new ArrayList<GeolockeIBeacon>();
            geolockeIBeaconList = geolockeIBeaconScan.getGeolockeIBeaconList();
            ArrayList<Integer> rssiList = new ArrayList<Integer>();
            rssiList = geolockeIBeaconScan.getRssiList();

            mIndex = 0;
            for(int i=0; i<geolockeIBeaconList.size(); i++){
                GeolockeIBeacon geolockeIBeacon = geolockeIBeaconList.get(i);
                Double rssi = Double.parseDouble(rssiList.get(i).toString());
                int txPower = geolockeIBeacon.getTxPower();
                Double distance = calculateDistance(rssi, txPower);

                // TODO: 29-07-2016 put actual latlng instead of the hardcoded ones - uncomment below code
                    /*double lat = geolockeIBeacon.getLatitude();
                    double lng = geolockeIBeacon.getLongitude();*/

                double lat = 28.5253323;
                double lng = 77.5782068;
                mLatLngArray[mIndex] = LatLngToXY(lat,lng);
                mDistanceArray[mIndex] = distance;
                mIndex++;
            }

            NonLinearLeastSquaresSolver solver = new NonLinearLeastSquaresSolver(new TrilaterationFunction(mLatLngArray, mDistanceArray), new LevenbergMarquardtOptimizer());
            LeastSquaresOptimizer.Optimum optimum = solver.solve();

            double[] centroid = optimum.getPoint().toArray();
            double[] latlng = XYToLatLng(centroid[0], centroid[1]);

            // TODO: 31-07-2016 use these when we know how to (or what they are)
            // error and geometry information; may throw SingularMatrixException depending the threshold argument provided
            /*RealVector standardDeviation = optimum.getSigma(0);
            RealMatrix covarianceMatrix = optimum.getCovariances(0);*/

            Log.i("Position:", String.valueOf(latlng[0])+","+String.valueOf(latlng[1]));

            mPosition = new Position(latlng[0],latlng[1],geolockeIBeaconScan);

            Intent intent = new Intent();
            intent.putExtra("POSITION",mPosition);
            intent.setAction("POSITION_BROADCAST");
            sendBroadcast(intent);

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mParsedListReceiver);
        stopSelf();
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

}
