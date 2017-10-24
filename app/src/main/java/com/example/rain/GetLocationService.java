package com.example.rain;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

public class GetLocationService extends Service {
    /**
     * 定位
     */
    public LocationClient mlocationClient;
    public String GpsId;
    public GetLocationService() {
    }
    private LocationBinder locationBinder = new LocationBinder();

    private LocationListener mLocationListener;

    public void registerListener(LocationListener listener){
        mLocationListener=  listener;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        //throw new UnsupportedOperationException("Not yet implemented");
        return  locationBinder;
    }
    class LocationBinder extends Binder{
        public String getLocation(){
            return GpsId;
        }
        public void test(){
            Toast.makeText(getApplicationContext(),"dsadad",Toast.LENGTH_LONG).show();
        }
        public GetLocationService getService(){return GetLocationService.this;}
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mlocationClient = new LocationClient(getApplicationContext());
        mlocationClient.registerLocationListener(new MyLocationListener());
        initLocation();
        mlocationClient.start();
       /* if (mLocationListener!=null &&GpsId!=null){
            mLocationListener.callback();
        }*/
    }
    /**
     * 获取定位信息
     */
    private void initLocation(){
        LocationClientOption option = new LocationClientOption();
        option.setScanSpan(60000);//60秒更新一次位置
        option.setIsNeedAddress(true);
        mlocationClient.setLocOption(option);
    }
    /**
     * 监听器
     */
    public class  MyLocationListener implements BDLocationListener{
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            GpsId = bdLocation.getCity();
            if (mLocationListener!=null)
                mLocationListener.callback();
            //Toast.makeText(getApplicationContext(),GpsId,Toast.LENGTH_LONG).show();
        }
    }
    /**
     * 停止定位
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        mlocationClient.stop();
    }
}
