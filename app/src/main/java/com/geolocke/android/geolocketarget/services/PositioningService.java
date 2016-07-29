package com.geolocke.android.geolocketarget.services;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.geolocke.android.geolocketarget.trilateration.NonLinearLeastSquaresSolver;
import com.geolocke.android.geolocketarget.trilateration.TrilaterationFunction;

import org.apache.commons.math3.fitting.leastsquares.LeastSquaresOptimizer;
import org.apache.commons.math3.fitting.leastsquares.LevenbergMarquardtOptimizer;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Manasa on 12-07-2016.
 */
public class PositioningService extends Service {
    public static final Double RADIANS = 57.2957795;
    private static final int MAX_BEACONS = 10;
    ParsedListReceiver mParsedListReceiver;
    double[][]positions = new double[MAX_BEACONS][2];
    double[] distances = new double[MAX_BEACONS];
    int index;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("PARSED_BROADCAST");
        mParsedListReceiver = new ParsedListReceiver();
        registerReceiver(mParsedListReceiver, intentFilter);

        return START_STICKY;
    }

    public class ParsedListReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i("got", "intent");

            try{
                JSONObject mParseObject = new JSONObject(intent.getStringExtra("PARSED_STRUCTURE"));
                JSONArray mParseArray = mParseObject.getJSONArray("PARSED_LIST");
                index = 0;
                for(int i=0; i<mParseArray.length(); i++){
                    JSONObject deviceObject = mParseArray.getJSONObject(i);
                    // TODO: 29-07-2016 put actual latlng 
                    /*double lat = deviceObject.getDouble("LATITUDE");
                    double lng = deviceObject.getDouble("LONGITUDE");*/
                    double lat = 28.5253323;
                    double lng = 77.5782068;
                    positions[index] = LatLngToXY(lat,lng);
                    distances[index] = deviceObject.getDouble("DISTANCE_IN_METRES");
                    index++;
                }

                NonLinearLeastSquaresSolver solver = new NonLinearLeastSquaresSolver(new TrilaterationFunction(positions, distances), new LevenbergMarquardtOptimizer());
                LeastSquaresOptimizer.Optimum optimum = solver.solve();

                double[] centroid = optimum.getPoint().toArray();
                double[] latlng = XYToLatLng(centroid[0], centroid[1]);

                // error and geometry information; may throw SingularMatrixException depending the threshold argument provided
                RealVector standardDeviation = optimum.getSigma(0);
                RealMatrix covarianceMatrix = optimum.getCovariances(0);

                Log.i("Position:", String.valueOf(latlng[0])+","+String.valueOf(latlng[1]));
                Log.i("Standard Deviation:",standardDeviation.toArray().toString());
                Log.i("Covariance Matrix:",covarianceMatrix.getData().toString());

                Intent i = new Intent();
                i.putExtra("LATITUDE",latlng[0]);
                i.putExtra("LONGITUDE",latlng[1]);
                i.setAction("POSITION");
                sendBroadcast(i);

            }catch (JSONException e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mParsedListReceiver);
        stopSelf();
    }

    public double[] LatLngToXY(Double Lat, Double Lng){
        Double angle = DEG_TO_RADIANS(0.0);
        Double xx = (Lng)*METERS_DEGLON(0.0);
        Double yy = (Lat)*METERS_DEGLAT(0.0);

        Double r = Math.sqrt(xx*xx + yy*yy);

      /* alert('LL_TO_XY: xx=' + xx + ' yy=' + yy + ' r=' + r);
      return false;*/

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

    public double[] XYToLatLng(Double x, Double y){
     /* X,Y to Lat/Lon Coordinate Translation  */
        Double angle = DEG_TO_RADIANS(0.0);
        Double pxpos_mtrs = x;
        Double pypos_mtrs = y;
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

    public Double METERS_DEGLON(Double x)
    {

        Double d2r=DEG_TO_RADIANS(x);
        return((111415.13 * Math.cos(d2r))- (94.55 * Math.cos(3.0*d2r)) + (0.12 * Math.cos(5.0*d2r)));
    }

    public Double  METERS_DEGLAT(Double x)
    {
        Double  d2r=DEG_TO_RADIANS(x);
        return(111132.09 - (566.05 * Math.cos(2.0*d2r))+ (1.20 * Math.cos(4.0*d2r)) - (0.002 * Math.cos(6.0*d2r)));

    }
    public Double DEG_TO_RADIANS(Double x)
    {
        return (x/RADIANS);
    }

}
