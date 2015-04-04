package com.xunce.gps;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;


public class MainActivity extends ActionBarActivity {
    private Double homeLat=26.0673834d; //宿舍纬度
    private Double homeLon=119.3119936d; //宿舍经度
    private EditText editText = null;
    private MyReceiver receiver=null;
    private final static String TAG = ActionBarActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText=(EditText)findViewById(R.id.editText);

        //判断GPS是否可用
        Log.i(TAG, UtilTool.isGpsEnabled((LocationManager) getSystemService(Context.LOCATION_SERVICE)) + "");
        if(!UtilTool.isGpsEnabled((LocationManager)getSystemService(Context.LOCATION_SERVICE))){
            Toast.makeText(this, "GSP当前已禁用，请在您的系统设置屏幕启动。", Toast.LENGTH_LONG).show();
            Intent callGPSSettingIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(callGPSSettingIntent);
            return;
        }

        //启动服务
        startService(new Intent(this, GpsService.class));

        //注册广播
        receiver = new MyReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.ljq.activity.GpsService");
        registerReceiver(receiver, filter);
    }

    //获取广播数据
    private class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle=intent.getExtras();
            String lon=bundle.getString("lon");
            String lat=bundle.getString("lat");
            if(lon!=null&&!"".equals(lon)&&lat!=null&&!"".equals(lat)){
                double distance=getDistance(Double.parseDouble(lat),
                        Double.parseDouble(lon), homeLat, homeLon);
                editText.setText("目前经纬度\n经度："+lon+"\n纬度："+lat+"\n离宿舍距离："+java.lang.Math.abs(distance));
            }else{
                editText.setText("目前经纬度\n经度："+lon+"\n纬度："+lat);
            }
        }
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(receiver);
        stopService(new Intent(this, GpsService.class));

        super.onDestroy();
    }

    /**
     * 把经纬度换算成距离
     *
     * @param lat1 开始纬度
     * @param lon1 开始经度
     * @param lat2 结束纬度
     * @param lon2 结束经度
     * @return
     */
    private double getDistance(double lat1, double lon1, double lat2, double lon2) {
        float[] results = new float[1];
        Location.distanceBetween(lat1, lon1, lat2, lon2, results);
        return results[0];
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
