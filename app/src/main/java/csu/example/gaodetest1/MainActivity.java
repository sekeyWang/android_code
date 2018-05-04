package csu.example.gaodetest1;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.google.zxing.activity.CaptureActivity;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class MainActivity extends AppCompatActivity{
    MapView mMapView = null;
    Button bt;
    private Context mContext;
    private final static int REQ_CODE = 1028;
    AMap aMap;
    String token, username;
    static String namespace = resource.getNamespace();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        token = getIntent().getStringExtra("token");
        username = getIntent().getStringExtra("username");
        bt = (Button) findViewById(R.id.button);
        bt.setOnClickListener(listener);

        StrictMode.setThreadPolicy(new
                StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());
        StrictMode.setVmPolicy(
                new StrictMode.VmPolicy.Builder().detectLeakedSqlLiteObjects().detectLeakedClosableObjects().penaltyLog().penaltyDeath().build());

        mContext = this;
        //获取地图控件引用
        mMapView = (MapView) findViewById(R.id.map);
        //在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，创建地图
        mMapView.onCreate(savedInstanceState);
        aMap = mMapView.getMap();
        initmylocation();
        initmarker();
        initslidingmenu();
    }
    SlidingMenu menu;

    public void clickBack(View view) {
        menu.toggle();
    }

    public void clickTab(View view) {
        String text = ((TextView) view).getText().toString();
        if (text.equals("历史记录")) {
//            Toast.makeText(this, "点击了 " + text, Toast.LENGTH_SHORT).show();
            menu.toggle();
            Intent myIntent = new Intent(MainActivity.this, history.class);
            myIntent.putExtra("username", username);
            this.startActivity(myIntent);
        }
    }
    void initslidingmenu(){
        menu = new SlidingMenu(this);
        menu.setMode(SlidingMenu.LEFT);
        menu.setShadowWidthRes(R.dimen.shadow_width);
        menu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
        // 设置渐入渐出效果的值
        menu.setFadeDegree(0.35f);
        menu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
        //为侧滑菜单设置布局
        menu.setMenu(R.layout.left);
    }
    void initmylocation(){
        MyLocationStyle myLocationStyle;
        myLocationStyle = new MyLocationStyle();//初始化定位蓝点样式类myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);//连续定位、且将视角移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。（1秒1次定位）如果不设置myLocationType，默认也会执行此种模式。
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATE);
        myLocationStyle.interval(2000); //设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。
        aMap.setMyLocationStyle(myLocationStyle);//设置定位蓝点的Style
        aMap.getUiSettings().setMyLocationButtonEnabled(true);//设置默认定位按钮是否显示，非必需设置。
        aMap.setMyLocationEnabled(true);// 设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false。
        aMap.moveCamera(CameraUpdateFactory.zoomTo(17));
    }
    void initmarker(){
        try {
            URL url = new URL("http://115.29.55.131:9191/devices/position?namespace=" + namespace);
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            if(conn.getResponseCode()==200){
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String line = in.readLine();
                JSONObject jsonObj = new JSONObject(line);
                if (jsonObj.optInt("code") == 200) {
                    JSONArray JA = jsonObj.optJSONArray("data");
                    for (int i = 0; i < JA.length(); i++){
                        JSONObject obj = (JSONObject) JA.get(i);
                        int id = obj.getInt("id");
                        boolean busy = obj.getBoolean("busy");
                        JSONObject pos = obj.getJSONObject("position");
                        double lat = pos.getDouble("lat");
                        double lon = pos.getDouble("lon");
                        if (!busy) addmarker(lat, lon);
                    }
                }
                else Toast.makeText(getApplicationContext(), jsonObj.optString("data"), Toast.LENGTH_LONG).show();
            }
            else{
                Toast.makeText(getApplicationContext(), "无法获取周围设备位置", Toast.LENGTH_LONG).show();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(mContext, CaptureActivity.class);
            startActivityForResult(intent, REQ_CODE);
        }
    };
    public void addmarker(double x, double y){
        LatLng latLng = new LatLng(x,y);
        MarkerOptions markerOption = new MarkerOptions();
        markerOption.position(latLng);
//        markerOption.title(title);

        markerOption.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                .decodeResource(getResources(),R.drawable.bike3)));
        final Marker marker = aMap.addMarker(markerOption);

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
        mMapView.onDestroy();
    }
    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView.onResume ()，重新绘制加载地图
        mMapView.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView.onPause ()，暂停地图的绘制
        mMapView.onPause();
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，保存地图当前的状态
        mMapView.onSaveInstanceState(outState);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_CODE) {
            try {
                String result = data.getStringExtra(CaptureActivity.SCAN_QRCODE_RESULT);
                JSONObject jobj = new JSONObject(result);
                String id = jobj.optString("id");

                StringBuilder buf = new StringBuilder();
                URL url = new URL("http://115.29.55.131:9191/device/"+ id +"/operation?action=lent&namespace=" + namespace + "&token=" + token);
                HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                OutputStream out = conn.getOutputStream();
                if(conn.getResponseCode()==200){
                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    String line = in.readLine();
                    JSONObject jsonObj = new JSONObject(line);
                    if (jsonObj.optInt("code") == 200) {
                        Toast.makeText(getApplicationContext(),"租借成功", Toast.LENGTH_LONG).show();
                        Intent myIntent = new Intent(MainActivity.this, lent.class);
                        myIntent.putExtra("id", id);
                        myIntent.putExtra("namespace", namespace);
                        MainActivity.this.startActivity(myIntent);
                    }
                    else Toast.makeText(getApplicationContext(), jsonObj.optString("data"), Toast.LENGTH_LONG).show();
                }
                else{
                    Toast.makeText(getApplicationContext(), "租借失败", Toast.LENGTH_LONG).show();
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void showToast(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }
}
