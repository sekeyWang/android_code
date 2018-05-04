package csu.example.gaodetest1;

import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class history extends AppCompatActivity {

    static String namespace=resource.getNamespace();
    public List<Map<String,Object>> datalist;
    String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history);//注意为“R.layout.activity_second”
        username = getIntent().getStringExtra("username");
        StrictMode.setThreadPolicy(new
                StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());
        StrictMode.setVmPolicy(
                new StrictMode.VmPolicy.Builder().detectLeakedSqlLiteObjects().detectLeakedClosableObjects().penaltyLog().penaltyDeath().build());
        datalist=new ArrayList();
        init();
    }
    public void init(){
        try {
            URL url = new URL("http://115.29.55.131:9191/users/log?account="+username+ "&namespace=" + namespace);
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            if(conn.getResponseCode()==200){
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String result = in.readLine();
                JSONObject jsonObj = new JSONObject(result);
                if (jsonObj.optInt("code") == 200) {
                    Toast.makeText(getApplicationContext(),"查询成功", Toast.LENGTH_LONG).show();
                    JSONArray JA = jsonObj.optJSONArray("data");
                    for (int i = 0; i < JA.length(); i++) {
                        JSONObject obj = JA.getJSONObject(i);
                        String remark = obj.getString("remark");
                        String logType = obj.getString("logType");
                        long ti = obj.getLong("operTime");
                        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        String dateString = formatter.format(ti);
                        Map map=new HashMap<String, Object>();
                        if (!remark.equals("null")) map.put("remark", "详细："+remark);
                        else map.put("remark", "详细：无");
                        map.put("logType", "行为:"+logType);
                        map.put("ti", "时间:"+dateString);
                        datalist.add(map);
                    }
                }
                else Toast.makeText(getApplicationContext(), jsonObj.optString("data"), Toast.LENGTH_LONG).show();
            }
            else{
                Toast.makeText(getApplicationContext(), "查询失败", Toast.LENGTH_LONG).show();
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
        SimpleAdapter simpleAdapter = new SimpleAdapter(history.this, datalist, R.layout.list, new String[]{"logType", "ti", "remark"}, new int[]{R.id.textView6, R.id.textView7, R.id.textView8});
        ListView listView = (ListView) findViewById(R.id.list_view);
        listView.setAdapter(simpleAdapter);
    }

}